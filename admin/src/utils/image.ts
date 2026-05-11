/**
 * 图片处理工具类
 */

export type CoverType = 'pc' | 'mobile'

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
 * 将图片处理为 WebP 格式，完全保留原始物理分辨率
 *
 * 关键点：
 * 1) 不对原图做任何尺寸缩放或裁剪，100% 保留超高分辨率，避免在大屏幕设备上拉伸模糊。
 * 2) 依靠 WebP 算法压缩体积（质量默认 0.85），大幅缩小图片文件大小的同时维持高清画质。
 *
 * @param file 原始图片文件
 * @param type 平台类型 (pc/mobile) 仅作保留，方便未来需要分类处理时使用
 * @param quality WebP 压缩质量 (0-1)
 * @returns 处理后的 Blob
 */
export const processCoverImage = (file: File, type: CoverType, quality = 0.85): Promise<Blob> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()

    reader.onload = (e) => {
      const img = new Image()
      img.onload = () => {
        try {
          // 直接使用原图的分辨率创建 Canvas 绘制
          const outW = img.width
          const outH = img.height

          const { canvas, ctx } = createCanvas(outW, outH)
          ctx.drawImage(img, 0, 0, outW, outH)

          // 输出为 WebP Blob
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
