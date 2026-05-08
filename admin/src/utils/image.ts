/**
 * 图片处理工具类
 */

export type CoverType = 'pc' | 'mobile'

const DIMENSIONS: Record<CoverType, { width: number; height: number }> = {
  pc: { width: 1200, height: 630 },
  mobile: { width: 600, height: 800 }
}

/**
 * 将图片处理为 WebP 格式，并进行居中裁剪 (cover)
 *
 * @param file 原始图片文件
 * @param type 平台类型 (pc/mobile)
 * @param quality WebP 压缩质量 (0-1)
 * @returns 处理后的 Blob
 */
export const processCoverImage = (file: File, type: CoverType, quality = 0.82): Promise<Blob> => {
  return new Promise((resolve, reject) => {
    const targetDim = DIMENSIONS[type]
    const reader = new FileReader()

    reader.onload = (e) => {
      const img = new Image()
      img.onload = () => {
        // 1. 计算缩放和裁剪参数 (Cover 模式)
        const targetRatio = targetDim.width / targetDim.height
        const sourceRatio = img.width / img.height

        let drawWidth = targetDim.width
        let drawHeight = targetDim.height
        let offsetX = 0
        let offsetY = 0

        if (sourceRatio > targetRatio) {
          // 原图更宽，以高度为基准缩放，水平居中裁剪
          const scale = targetDim.height / img.height
          drawWidth = img.width * scale
          offsetX = (targetDim.width - drawWidth) / 2
        } else {
          // 原图更高，以宽度为基准缩放，垂直居中裁剪
          const scale = targetDim.width / img.width
          drawHeight = img.height * scale
          offsetY = (targetDim.height - drawHeight) / 2
        }

        // 2. 绘制到 Canvas
        const canvas = document.createElement('canvas')
        canvas.width = targetDim.width
        canvas.height = targetDim.height
        const ctx = canvas.getContext('2d')

        if (!ctx) {
          reject(new Error('无法创建 Canvas context'))
          return
        }

        ctx.drawImage(img, offsetX, offsetY, drawWidth, drawHeight)

        // 3. 输出为 WebP Blob
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
      }
      img.onerror = () => reject(new Error('图片加载失败'))
      img.src = e.target?.result as string
    }
    reader.onerror = () => reject(new Error('文件读取失败'))
    reader.readAsDataURL(file)
  })
}
