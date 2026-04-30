/**
 * 文章管理 API
 * 提供文章的增删改查、状态切换等功能
 */
import { get, post, put, del } from './request'
import type { Article, ArticleForm, ArticleListParams, ArticleStatus } from '@/types'
import type { PageResult } from '@/types/api'

/**
 * 文章 API 接口
 */
export const articleApi = {
  /**
   * 获取文章分页列表
   * @param params 查询参数（页码、每页数量、关键词、分类ID、状态）
   * @returns 分页结果
   */
  getList(params: ArticleListParams): Promise<PageResult<Article>> {
    return get<PageResult<Article>>('/admin/articles', params)
  },

  /**
   * 根据ID获取文章详情
   * @param id 文章ID
   * @returns 文章详情
   */
  getById(id: number): Promise<Article> {
    return get<Article>(`/admin/articles/${id}`)
  },

  /**
   * 创建新文章
   * @param data 文章表单数据
   * @returns 创建的文章
   */
  create(data: ArticleForm): Promise<Article> {
    return post<Article>('/admin/articles', data)
  },

  /**
   * 更新文章
   * @param id 文章ID
   * @param data 文章表单数据
   * @returns 更新后的文章
   */
  update(id: number, data: ArticleForm): Promise<Article> {
    return put<Article>(`/admin/articles/${id}`, data)
  },

  /**
   * 删除文章（逻辑删除）
   * @param id 文章ID
   */
  delete(id: number): Promise<void> {
    return del('/admin/articles', { ids: id })
  },

  /**
   * 切换文章置顶状态
   * @param id 文章ID
   */
  toggleTop(id: number): Promise<void> {
    return put(`/admin/articles/${id}/toggle-top`)
  },



  /**
   * 更新文章发布状态
   * @param id 文章ID
   * @param status 新状态（已发布、草稿、已下架）
   */
  updateStatus(id: number, status: ArticleStatus): Promise<void> {
    return put(`/admin/articles/${id}/status?status=${status}`)
  }
}

export default articleApi
