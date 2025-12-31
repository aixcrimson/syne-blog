/**
 * 导航 API
 */
import { get } from './request'
import { NavigationCategory } from '@/types'

/**
 * 导航 API 服务
 */
export const navigationApi = {
  /**
   * 获取所有导航数据
   * @returns 导航数据
   */
  getList() {
    return get<NavigationCategory[]>('/navigations')
  },
}


