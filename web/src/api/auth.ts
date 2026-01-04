import { post } from './request'
import type { LoginData, RegisterData, LoginResponse } from '@/types'

export const authApi = {
  // 登录
  login: (data: LoginData) => {
    return post<LoginResponse>('/auth/login', data)
  },

  // 注册
  register: (data: RegisterData) => {
    return post<void>('/auth/register', data)
  },

  // 发送验证码 (Mock for now, or real if endpoint exists)
  sendCode: async (email: string) => {
    // 暂时保持 Mock，或者如果后端有 /auth/send-code 接口则替换为:
    // return post<void>('/auth/send-code', { email })
    // 这里暂时先保留 Mock 以防后端未准备好
    console.log(`[Mock] 验证码已发送至 ${email}`)
    return Promise.resolve({ message: '验证码已发送' })
  }
}

