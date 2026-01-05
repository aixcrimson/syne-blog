/**
 * 用户相关 API
 */
import { put } from './request'
import type { UserUpdateData, ChangePasswordData, UserInfo } from '@/types'

/**
 * 用户 API 服务
 */
export const userApi = {

  /**
   * 更新用户信息
   * @param id 用户ID
   * @param data 更新数据
   * @returns 更新后的用户信息
   */
  updateProfile(id: number | string, data: Partial<UserUpdateData>) {
    return put<UserInfo>(`/users/${id}`, data)
  },

  /**
   * 修改密码
   * @param id 用户ID
   * @param data 密码数据
   */
  updatePassword(id: number | string, data: ChangePasswordData) {
    return put<string>(`/users/${id}/password`, data)
  }
}



