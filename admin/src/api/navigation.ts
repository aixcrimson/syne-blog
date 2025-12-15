/**
 * 导航站点管理 API
 * 提供导航分类和站点的增删改查功能
 * @requirements 10.1, 10.2, 10.3, 10.5
 */
import { get, post, put, del } from './request'
import type { NavigationCategory, NavigationSite, SortOrderItem } from '@/types'

/**
 * 导航分类表单接口
 */
export interface NavigationCategoryForm {
  /** 分类名称 */
  name: string
  /** 分类图标 */
  icon?: string
  /** 排序顺序 */
  sortOrder: number
}

/**
 * 导航站点表单接口
 */
export interface NavigationSiteForm {
  /** 分类ID */
  categoryId: number
  /** 站点名称 */
  name: string
  /** 站点描述 */
  description?: string
  /** 站点URL */
  url: string
  /** 站点图标 */
  icon?: string
  /** 排序顺序 */
  sortOrder: number
}

/**
 * 导航 API 接口
 */
export const navigationApi = {
  // ==================== 分类相关 ====================

  /**
   * 获取导航分类列表（包含站点）
   * @returns 分类列表
   * @requirements 10.1
   */
  getCategories(): Promise<NavigationCategory[]> {
    return get<{ list: NavigationCategory[] }>('/admin/navigation/categories').then(res => res.list)
  },

  /**
   * 创建导航分类
   * @param data 分类表单数据
   * @returns 创建的分类
   * @requirements 10.2
   */
  createCategory(data: NavigationCategoryForm): Promise<NavigationCategory> {
    return post<NavigationCategory>('/admin/navigation/categories', data)
  },

  /**
   * 更新导航分类
   * @param id 分类ID
   * @param data 分类表单数据
   * @returns 更新后的分类
   * @requirements 10.2
   */
  updateCategory(id: number, data: NavigationCategoryForm): Promise<NavigationCategory> {
    return put<NavigationCategory>(`/admin/navigation/categories/${id}`, data)
  },

  /**
   * 删除导航分类
   * @param id 分类ID
   * @requirements 10.2
   */
  deleteCategory(id: number): Promise<void> {
    return del(`/admin/navigation/categories/${id}`)
  },

  /**
   * 更新分类排序
   * @param orders 排序项列表
   * @requirements 10.5
   */
  updateCategorySortOrder(orders: SortOrderItem[]): Promise<void> {
    return put('/admin/navigation/categories/sort', { orders })
  },

  // ==================== 站点相关 ====================

  /**
   * 创建导航站点
   * @param data 站点表单数据
   * @returns 创建的站点
   * @requirements 10.3
   */
  createSite(data: NavigationSiteForm): Promise<NavigationSite> {
    return post<NavigationSite>('/admin/navigation/sites', data)
  },

  /**
   * 更新导航站点
   * @param id 站点ID
   * @param data 站点表单数据
   * @returns 更新后的站点
   * @requirements 10.3
   */
  updateSite(id: number, data: NavigationSiteForm): Promise<NavigationSite> {
    return put<NavigationSite>(`/admin/navigation/sites/${id}`, data)
  },

  /**
   * 删除导航站点
   * @param id 站点ID
   * @requirements 10.3
   */
  deleteSite(id: number): Promise<void> {
    return del(`/admin/navigation/sites/${id}`)
  },

  /**
   * 更新站点排序
   * @param orders 排序项列表
   * @requirements 10.5
   */
  updateSiteSortOrder(orders: SortOrderItem[]): Promise<void> {
    return put('/admin/navigation/sites/sort', { orders })
  }
}

export default navigationApi
