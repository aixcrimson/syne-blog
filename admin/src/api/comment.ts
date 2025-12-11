/**
 * 评论管理 API
 * 提供评论的查询、审核、删除和批量操作功能
 * @requirements 9.1, 9.3, 9.4, 9.5
 */
import { get, put, del } from './request'
import type { Comment, CommentListParams } from '@/types'
import type { PageResult, BatchOperationResult } from '@/types/api'

/**
 * 评论 API 接口
 */
export const commentApi = {
  /**
   * 获取评论分页列表
   * @param params 查询参数（页码、每页数量、状态）
   * @returns 分页结果
   * @requirements 9.1
   */
  getList(params: CommentListParams): Promise<PageResult<Comment>> {
    return get<PageResult<Comment>>('/admin/comments', params)
  },

  /**
   * 审核通过评论
   * @param id 评论ID
   * @requirements 9.3
   */
  approve(id: number): Promise<void> {
    return put(`/admin/comments/${id}/approve`)
  },

  /**
   * 删除评论（逻辑删除）
   * @param id 评论ID
   * @requirements 9.4
   */
  delete(id: number): Promise<void> {
    return del(`/admin/comments/${id}`)
  },

  /**
   * 批量审核通过评论
   * @param ids 评论ID列表
   * @returns 批量操作结果
   * @requirements 9.5
   */
  batchApprove(ids: number[]): Promise<BatchOperationResult> {
    return put<BatchOperationResult>('/admin/comments/batch-approve', { ids })
  },

  /**
   * 批量删除评论
   * @param ids 评论ID列表
   * @returns 批量操作结果
   * @requirements 9.5
   */
  batchDelete(ids: number[]): Promise<BatchOperationResult> {
    return del<BatchOperationResult>('/admin/comments/batch', { ids })
  }
}

export default commentApi
