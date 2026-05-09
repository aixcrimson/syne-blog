/**
 * 图片处理工具类
 */

export type CoverType = 'pc' | 'mobile'

/**
 * 目标输出尺寸：按 2× DPR 设计，覆盖主流高分屏
 * - PC: 1920×1080（与详情页 aspect-video / 16:9 对齐）
 * - Mobile: 1080×1440（3:4，覆盖手机 3× DPR 主流宽度）
 */
const DIMENSIONS: Record<CoverType, { width: number; height: number }> = {
  pc: { width: 1920, height: 1080 },
  mobile: { width: 1080, height: 1440 }
}

/**
 * 创建一张指定尺寸的 Canvas，并启用高质量插值
 */
const createCanvas = (width: number, height: number): { canvas: HTMLCanvasElement; ctx: CanvasRenderingContext2D } => {
  const canvas = document.createElement('canvas')
  canvas.width = width
  canvas.height = height
  const ctx = canvas.getContext('2d')
  if (!ctx) throw new Error('无法创建 Canvas context')
  ctx.imageSmoothingEnabled = true
  ctx.imageSmoothingQuality = 'high'
  return { canvas, ctx }
}

/**
 * 多步降采样（step-down）：每次缩小不超过一半，避免单步大幅缩放产生的走样/糊
 * 直到下一次减半会小于 targetW/targetH，再交由调用方做最后一次精确绘制
 */
const stepDown = (
  source: HTMLImageElement | HTMLCanvasElement,
  sourceW: number,
  sourceH: number,
  targetW: number,
  targetH: number
): { node: HTMLImageElement | HTMLCanvasElement; width: number; height: number } => {
  let cur: HTMLImageElement | HTMLCanvasElement = source
  let curW = sourceW
  let curH = sourceH

  while (curW * 0.5 >= targetW && curH * 0.5 >= targetH) {
    const nextW = Math.max(1, Math.floor(curW * 0.5))
    const nextH = Math.max(1, Math.floor(curH * 0.5))
    const { canvas, ctx } = createCanvas(nextW, nextH)
    ctx.drawImage(cur, 0, 0, nextW, nextH)
    cur = canvas
    curW = nextW
    curH = nextH
  }

  return { node: cur, width: curW, height: curH }
}

/**
 * 将图片处理为 WebP 格式，并进行居中裁剪 (cover)
 *
 * 关键点：
 * 1) 不上采样：若原图小于目标，按原图能覆盖的最大尺寸输出，避免人为放大造成的模糊
 * 2) 多步降采样 + 高质量插值：消除一次性大幅缩放造成的"软糊"
 * 3) WebP 质量默认 0.85：兼顾清晰度与体积
 *
 * @param file 原始图片文件
 * @param type 平台类型 (pc/mobile)
 * @param quality WebP 压缩质量 (0-1)
 * @returns 处理后的 Blob
 */
export const processCoverImage = (file: File, type: CoverType, quality = 0.85): Promise<Blob> => {
  return new Promise((resolve, reject) => {
    const targetDim = DIMENSIONS[type]
    const reader = new FileReader()

    reader.onload = (e) => {
      const img = new Image()
      img.onload = () => {
        try {
          // 1. 不上采样：若原图任一边不足以覆盖目标，则将输出尺寸按原图等比缩小
          let outW = targetDim.width
          let outH = targetDim.height
          if (img.width < outW || img.height < outH) {
            const fitScale = Math.min(img.width / outW, img.height / outH)
            outW = Math.max(1, Math.round(outW * fitScale))
            outH = Math.max(1, Math.round(outH * fitScale))
          }

          // 2. 计算 cover 模式下的绘制尺寸与居中偏移
          const targetRatio = outW / outH
          const sourceRatio = img.width / img.height

          let drawWidth = outW
          let drawHeight = outH
          let offsetX = 0
          let offsetY = 0

          if (sourceRatio > targetRatio) {
            // 原图更宽，以高度为基准缩放，水平居中裁剪
            const scale = outH / img.height
            drawWidth = img.width * scale
            offsetX = (outW - drawWidth) / 2
          } else {
            // 原图更高，以宽度为基准缩放，垂直居中裁剪
            const scale = outW / img.width
            drawHeight = img.height * scale
            offsetY = (outH - drawHeight) / 2
          }

          // 3. 多步降采样到接近 drawWidth × drawHeight
          const stepped = stepDown(img, img.width, img.height, drawWidth, drawHeight)

          // 4. 最终绘制到目标 Canvas（含居中裁剪）
          const { canvas, ctx } = createCanvas(outW, outH)
          ctx.drawImage(stepped.node, offsetX, offsetY, drawWidth, drawHeight)

          // 5. 输出为 WebP Blob
          canvas.toBlob(
            (blob) => {
              if (blob) {
                resolve(blob)
              } else {
                reject(new Error('Canvas toBlob 失败'))
              }
            },
            'image/webp',
            quality
          )
        } catch (err) {
          reject(err instanceof Error ? err : new Error('图片处理失败'))
        }
      }
      img.onerror = () => reject(new Error('图片加载失败'))
      img.src = e.target?.result as string
    }
    reader.onerror = () => reject(new Error('文件读取失败'))
    reader.readAsDataURL(file)
  })
}
