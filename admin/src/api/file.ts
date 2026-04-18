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
  }
}

export default fileApi
