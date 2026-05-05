/**
 * 标签管理 API
 * 提供标签的增删改查功能
 */
import { get, post, put, del } from './request'
import type { Tag, TagForm } from '@/types'
import type { PageResult, PaginationParams } from '@/types/api'

/**
 * 标签 API 接口
 */
export const tagApi = {
  /**
   * 获取标签列表
   * @param params 分页参数
   * @returns 标签列表分页结果
   */
  getList(params?: PaginationParams): Promise<PageResult<Tag>> {
    return get<PageResult<Tag>>('/admin/tags', params)
  },

  /**
   * 创建新标签
   * @param data 标签表单数据
   * @returns 创建的标签
   */
  create(data: TagForm): Promise<Tag> {
    return post<Tag>('/admin/tags', data)
  },

  /**
   * 更新标签
   * @param id 标签ID
   * @param data 标签表单数据
   * @returns 更新后的标签
   */
  update(id: number, data: TagForm): Promise<Tag> {
    return put<Tag>(`/admin/tags/${id}`, data)
  },

  /**
   * 删除标签
   * @param id 标签ID
   */
  delete(id: number): Promise<void> {
    return del('/admin/tags', { ids: id })
  }
}

export default tagApi
