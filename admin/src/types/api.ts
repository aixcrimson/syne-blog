/**
 * API 响应类型定义
 * 包含通用响应结构和分页相关类型
 */

/**
 * 通用 API 响应结构
 * @template T 响应数据类型
 */
export interface ApiResponse<T = unknown> {
  /** 响应状态码 */
  code: number
  /** 响应消息 */
  message: string
  /** 响应数据 */
  data: T
}

/**
 * 分页结果
 * @template T 列表项类型
 */
export interface PageResult<T> {
  /** 数据列表 */
  list: T[]
  /** 总记录数 */
  total: number
  /** 当前页码 */
  page: number
  /** 每页数量 */
  pageSize: number
  /** 总页数 */
  totalPages: number
}

/**
 * 分页查询参数
 */
export interface PaginationParams {
  /** 页码（从1开始） */
  page: number
  /** 每页数量 */
  pageSize: number
}

/**
 * 带排序的分页查询参数
 */
export interface SortablePaginationParams extends PaginationParams {
  /** 排序字段 */
  sortField?: string
  /** 排序方向 */
  sortOrder?: 'asc' | 'desc'
}

/**
 * 批量操作参数
 */
export interface BatchOperationParams {
  /** ID列表 */
  ids: number[]
}

/**
 * 批量操作结果
 */
export interface BatchOperationResult {
  /** 成功数量 */
  successCount: number
  /** 失败数量 */
  failCount: number
  /** 失败的ID列表 */
  failedIds?: number[]
}

/**
 * 业务错误码枚举
 */
export enum BusinessErrorCode {
  /** 用户名或密码错误 */
  INVALID_CREDENTIALS = 1001,
  /** 用户已被禁用 */
  USER_DISABLED = 1002,
  /** 文章不存在 */
  ARTICLE_NOT_FOUND = 2001,
  /** 分类不存在 */
  CATEGORY_NOT_FOUND = 2002,
  /** 分类下存在文章，无法删除 */
  CATEGORY_HAS_ARTICLES = 2003,
  /** 名称已存在 */
  NAME_ALREADY_EXISTS = 2004,
  /** 别名已存在 */
  SLUG_ALREADY_EXISTS = 2005
}

/**
 * HTTP 状态码枚举
 */
export enum HttpStatusCode {
  /** 成功 */
  OK = 200,
  /** 已创建 */
  CREATED = 201,
  /** 请求参数错误 */
  BAD_REQUEST = 400,
  /** 未授权 */
  UNAUTHORIZED = 401,
  /** 禁止访问 */
  FORBIDDEN = 403,
  /** 资源不存在 */
  NOT_FOUND = 404,
  /** 服务器内部错误 */
  INTERNAL_SERVER_ERROR = 500
}
