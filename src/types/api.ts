/**
 * API 相关类型定义
 */

/**
 * 通用的 API 响应格式
 */
export interface ApiResponse<T = any> {
  code: number
  data: T
  message: string
}

/**
 * 分页请求参数
 */
export interface PaginationParams {
  page: number
  pageSize: number
  keyword?: string
  [key: string]: any
}

/**
 * 分页响应数据
 */
export interface PaginationResponse<T = any> {
  list: T[]
  total: number
  page: number
  pageSize: number
  totalPages: number
}

/**
 * 列表响应数据（不带分页）
 */
export interface ListResponse<T = any> {
  list: T[]
  total: number
}


