/**
 * 评论管理 API
 */
import { get, put, del } from './request'
import type { Comment, CommentListParams } from '@/types'
import type { PageResult } from '@/types/api'

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
  updateStatus(ids: string, status: 1 | 3): Promise<void> {
    return put(`/admin/comments/status`, null, { params: { ids, status } })
  },

  /**
   * 删除评论
   * @param ids 评论ID，多个用逗号分隔
   */
  delete(ids: string): Promise<void> {
    return del(`/admin/comments`, { ids })
  }
}

export default commentApi

