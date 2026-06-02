/**
 * 文件上传 API
 */
import { post } from './request'

export interface FileUploadResult {
  url: string
  fileName: string
  originalName: string
  size: number
  contentType: string
}

export const fileApi = {
  /**
   * 上传图片
   * @param file 文件对象
   */
  uploadImage: (file: File): Promise<FileUploadResult> => {
    const formData = new FormData()
    formData.append('file', file)

    return post<FileUploadResult>('/admin/file/upload/image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  /**
   * 上传图库图片
   * @param file 文件对象（前端处理好的 WebP）
   * @param type 类型 (pc / mobile)
   */
  uploadCover: (file: File | Blob, type: 'pc' | 'mobile'): Promise<FileUploadResult> => {
    const formData = new FormData()
    // 由于可能是 Blob，我们需要给它一个名字
    formData.append('file', file, `upload_${type}.webp`)
    formData.append('type', type)

    return post<FileUploadResult>('/admin/file/cover/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  /**
   * 批量上传图库图片
   * @param files 文件对象数组（前端处理好的 WebP）
   * @param type 类型 (pc / mobile)
   */
  uploadCoverBatch: (files: (File | Blob)[], type: 'pc' | 'mobile'): Promise<FileUploadResult[]> => {
    const formData = new FormData()
    files.forEach((file, index) => {
      formData.append('files', file, `upload_${type}_${index}.webp`)
    })
    formData.append('type', type)

    return post<FileUploadResult[]>('/admin/file/cover/upload/batch', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}

export default fileApi
