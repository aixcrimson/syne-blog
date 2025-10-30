/**
 * 文章相关 API
 */
import { get, post, put, del } from './request'
import type { Article } from '@/types'
import type { PaginationParams, PaginationResponse } from '@/types/api'

/**
 * 文章 API 服务
 */
export const articleApi = {
  /**
   * 获取文章列表（分页）
   * @param params 分页参数
   * @returns 文章列表数据
   */
  getList(params: PaginationParams) {
    return get<PaginationResponse<Article>>('/articles', params)
  },

  /**
   * 获取文章详情
   * @param id 文章 ID
   * @returns 文章详情
   */
  getById(id: number | string) {
    return get<Article>(`/articles/${id}`)
  },

  /**
   * 获取推荐文章
   * @param limit 数量限制
   * @returns 推荐文章列表
   */
  getRecommended(limit: number = 5) {
    return get<Article[]>('/articles/recommended', { limit })
  },

  /**
   * 获取热门文章
   * @param limit 数量限制
   * @returns 热门文章列表
   */
  getHot(limit: number = 5) {
    return get<Article[]>('/articles/hot', { limit })
  },

  /**
   * 按分类获取文章
   * @param category 分类
   * @param params 分页参数
   * @returns 文章列表
   */
  getByCategory(category: string, params: PaginationParams) {
    return get<PaginationResponse<Article>>(`/articles/category/${category}`, params)
  },

  /**
   * 按标签获取文章
   * @param tag 标签
   * @param params 分页参数
   * @returns 文章列表
   */
  getByTag(tag: string, params: PaginationParams) {
    return get<PaginationResponse<Article>>(`/articles/tag/${tag}`, params)
  },

  /**
   * 搜索文章
   * @param keyword 关键词
   * @param params 分页参数
   * @returns 文章列表
   */
  search(keyword: string, params: Omit<PaginationParams, 'keyword'>) {
    return get<PaginationResponse<Article>>('/articles/search', {
      ...params,
      keyword
    })
  },

  /**
   * 创建文章（需要管理员权限）
   * @param data 文章数据
   * @returns 创建的文章
   */
  create(data: Partial<Article>) {
    return post<Article>('/articles', data)
  },

  /**
   * 更新文章（需要管理员权限）
   * @param id 文章 ID
   * @param data 文章数据
   * @returns 更新后的文章
   */
  update(id: number | string, data: Partial<Article>) {
    return put<Article>(`/articles/${id}`, data)
  },

  /**
   * 删除文章（需要管理员权限）
   * @param id 文章 ID
   * @returns 删除结果
   */
  delete(id: number | string) {
    return del(`/articles/${id}`)
  },

  /**
   * 点赞文章
   * @param id 文章 ID
   * @returns 点赞后的数据
   */
  like(id: number | string) {
    return post<{ likes: number }>(`/articles/${id}/like`)
  },

  /**
   * 收藏文章
   * @param id 文章 ID
   * @returns 收藏后的数据
   */
  favorite(id: number | string) {
    return post<{ favorited: boolean }>(`/articles/${id}/favorite`)
  },

  /**
   * 增加浏览量
   * @param id 文章 ID
   * @returns 浏览量数据
   */
  increaseViews(id: number | string) {
    return post<{ views: number }>(`/articles/${id}/views`)
  }
}


