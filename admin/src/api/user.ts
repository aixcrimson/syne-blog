/**
 * 用户 API
 * 处理用户管理、个人资料更新和密码修改
 * @requirements 11.1, 11.3, 11.4, 12.1, 12.3
 */
import { get, put } from './request'
import type { User, UserListParams, ChangePasswordParams } from '@/types'
import type { PageResult } from '@/types/api'

/**
 * 用户 API 接口
 */
export const userApi = {
  /**
   * 获取用户列表（分页）
   * @param params 查询参数（页码、每页数量、搜索关键词）
   * @returns 分页用户列表
   * @requirements 11.1, 11.2
   */
  getList: (params: UserListParams): Promise<PageResult<User>> => {
    return get<PageResult<User>>('/admin/users', params)
  },

  /**
   * 切换用户状态（启用/禁用）
   * @param id 用户ID
   * @returns void
   * @requirements 11.3, 11.4
   */
  toggleStatus: (id: number): Promise<void> => {
    return put(`/admin/users/${id}/toggle-status`)
  },

  /**
   * 更新个人资料
   * @param data 个人资料数据
   * @returns void
   * @requirements 12.1
   */
  updateProfile: (data: any): Promise<void> => {
    return put(`/admin/users/${data.id}`, data)
  },

  /**
   * 修改密码
   * @param data 密码数据（原密码、新密码、确认密码）
   * @returns void
   * @requirements 12.3
   */
  changePassword: (data: ChangePasswordParams): Promise<void> => {
    return put('/admin/users/password', data)
  }
}

export default userApi
