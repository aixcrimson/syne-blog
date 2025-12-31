import request from './request'

// 模拟延迟
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

export const authApi = {
  // 登录 (Mock)
  login: async (data: any) => {
    await delay(1000)
    // 模拟登录成功
    if (data.username === 'admin' || data.username === 'test@example.com') {
      return {
        token: 'mock-token-123456',
        userInfo: {
          id: 1,
          username: 'Admin User',
          email: 'test@example.com',
          avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Felix',
          bio: 'Administrator'
        }
      }
    }
    // 模拟失败
    throw new Error('用户名或密码错误')
  },

  // 注册 (Mock)
  register: async (data: any) => {
    await delay(1500)
    return {
      message: '注册成功'
    }
  },

  // 发送验证码 (Mock)
  sendCode: async (email: string) => {
    await delay(1000)
    console.log(`验证码已发送至 ${email}: 123456`)
    return {
      message: '验证码已发送'
    }
  }
}
