/**
 * 分类管理 API
 * 提供分类的增删改查功能
 */
import { get, post, put, del } from './request'
import type { Category, CategoryForm } from '@/types'

/**
 * 分类 API 接口
 */
export const categoryApi = {
  /**
   * 获取分类列表
   * @returns 分类列表
   */
  getList(): Promise<Category[]> {
    return get<Category[]>('/admin/categories')
  },

  /**
   * 创建新分类
   * @param data 分类表单数据
   * @returns 创建的分类
   */
  create(data: CategoryForm): Promise<Category> {
    return post<Category>('/admin/categories', data)
  },

  /**
   * 更新分类
   * @param id 分类ID
   * @param data 分类表单数据
   * @returns 更新后的分类
   */
  update(id: number, data: CategoryForm): Promise<Category> {
    return put<Category>(`/admin/categories/${id}`, data)
  },

  /**
   * 删除分类
   * @param id 分类ID
   */
  delete(id: number): Promise<void> {
    return del(`/admin/categories/${id}`)
  }
}

export default categoryApi
