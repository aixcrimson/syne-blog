/**
 * 认证 API
 * 处理用户登录、登出和获取当前用户信息
 * @requirements 3.2, 3.5
 */
import { post, get } from './request'
import type { LoginParams, LoginResponse, UserInfo } from '@/types'

/**
 * 认证 API 接口
 */
export const authApi = {
  /**
   * 用户登录
   * @param params 登录参数（用户名、密码）
   * @returns 登录响应（Token 和用户信息）
   * @requirements 3.2
   */
  login: (params: LoginParams): Promise<LoginResponse> => {
    return post<LoginResponse>('/auth/login', params)
  },

  /**
   * 用户登出
   * @returns void
   * @requirements 3.5
   */
  logout: (): Promise<void> => {
    return post('/auth/logout')
  },

  /**
   * 获取当前登录用户信息
   * @returns 用户信息
   */
  getCurrentUser: (): Promise<UserInfo> => {
    return get<UserInfo>('/auth/current')
  }
}

export default authApi
