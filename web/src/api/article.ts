/**
 * 文章相关 API
 */
import { get, post } from './request'
import type { Article, ArticleSearchItem, CategoryInfo, TagInfo, StatsInfo } from '@/types'

import type { PaginationParams, PaginationResponse } from '@/types/api'

/**
 * 文章 API 服务
 */
export const articleApi = {
  /**
   * 获取文章列表（分页）
   * @param params 参数
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
   * 获取顶部搜索使用的文章索引
   */
  getSearchIndex() {
    return get<ArticleSearchItem[]>('/articles/search-index')
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
   * 获取我点赞的文章列表
   * @param params 分页参数
   */
  getLikedList(params: PaginationParams) {
    return get<PaginationResponse<Article>>('/articles/liked', params)
  },

  /**
   * 获取我收藏的文章列表
   * @param params 分页参数
   */
  getFavoriteList(params: PaginationParams) {
    return get<PaginationResponse<Article>>('/articles/favorite', params)
  },

  /**
   * 点赞文章
   * @param id 文章 ID
   * @returns 点赞后的数据
   */
  like(id: number | string) {
    return post<{ likes: number; liked: boolean }>(`/articles/${id}/like`)
  },

  /**
   * 收藏文章
   * @param id 文章 ID
   * @returns 收藏后的数据
   */
  favorite(id: number | string) {
    return post<{ favorited: boolean; favorites: number }>(`/articles/${id}/favorite`)
  },

  /**
   * 增加浏览量
   * @param id 文章 ID
   * @returns 浏览量数据
   */
  increaseViews(id: number | string) {
    return post<{ views: number }>(`/articles/${id}/views`)
  },

  /**
   * 获取所有分类（带文章数量）
   * @returns 分类列表
   */
  getCategories() {
    return get<CategoryInfo[]>('/categories')
  },

  /**
   * 获取所有标签
   * @returns 标签列表
   */
  getTags() {
    return get<TagInfo[]>('/tags')
  },

  /**
   * 获取统计信息
   * @returns 统计数据
   */
  getStats() {
    return get<StatsInfo>('/stats')
  }
}


