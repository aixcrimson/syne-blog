/**
 * 评论相关 API
 */
import { get, post } from './request'
import type { PaginationParams, PaginationResponse } from '@/types/api'
import type { CommentShowVO, CommentCreateDTO } from '@/types'


/**
 * 评论 API 服务
 */
export const commentApi = {
  /**
   * 获取文章评论列表
   * @param articleId 文章ID
   * @param params 分页参数
   * @returns 评论列表（树形结构）
   */
  getArticleComments(articleId: number, params?: Partial<PaginationParams>) {
    return get<PaginationResponse<CommentShowVO>>(`/comments/article/${articleId}`, params)
  },

  /**
   * 创建评论
   * @param data 评论数据
   * @returns 创建的评论
   */
  createComment(data: CommentCreateDTO) {
    return post<CommentShowVO>('/comments', data)
  }
}
