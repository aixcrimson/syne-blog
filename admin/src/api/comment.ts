/**
 * 评论管理 API
 */
import { get, put, del } from './request'
import type { Comment, CommentListParams } from '@/types'
import type { PageResult } from '@/types/api'

export interface BatchResult {
  successCount: number
  [key: string]: any
}

export const commentApi = {
  /**
   * 获取评论分页列表
   * @param params 查询参数
   */
  getList(params: CommentListParams): Promise<PageResult<Comment>> {
    return get<PageResult<Comment>>('/admin/comments', params)
  },

  /**
   * 更新评论状态
   * @param ids 评论ID，多个用逗号分隔
   * @param status 目标状态：1-通过，3-驳回
   */
  updateStatus(ids: string | number | number[], status: 1 | 3): Promise<BatchResult> {
    const idsStr = Array.isArray(ids) ? ids.join(',') : String(ids)
    return put<BatchResult>(`/admin/comments/status`, null, { params: { ids: idsStr, status } })
  },

  /**
   * 删除评论
   * @param ids 评论ID，多个用逗号分隔
   */
  delete(ids: string | number | number[]): Promise<BatchResult> {
    const idsStr = Array.isArray(ids) ? ids.join(',') : String(ids)
    return del<BatchResult>(`/admin/comments`, { ids: idsStr })
  },

  /**
   * 审核通过单条评论
   * @param id 评论ID
   */
  approve(id: number): Promise<BatchResult> {
    return this.updateStatus(id, 1) // 1 for NORMAL/APPROVED
  },

  /**
   * 批量审核通过
   * @param ids 评论ID列表
   */
  batchApprove(ids: number[]): Promise<BatchResult> {
    return this.updateStatus(ids, 1)
  },

  /**
   * 批量删除
   * @param ids 评论ID列表
   */
  batchDelete(ids: number[]): Promise<BatchResult> {
    return this.delete(ids)
  }
}

export default commentApi

