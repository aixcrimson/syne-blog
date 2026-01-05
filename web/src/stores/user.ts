import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'
import type { UserInfo } from '@/types'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string | null>(localStorage.getItem('token'))
  const currentUser = ref<UserInfo | null>(null)

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  
  // 动作
  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUser = (user: UserInfo) => {
    currentUser.value = user
  }

  const fetchCurrentUser = async () => {
    if (!token.value) return
    try {
      const user = await authApi.getCurrentUser()
      if (user) {
        setUser(user)
      }
    } catch (error) {
      console.error('获取当前用户信息失败:', error)
      // 如果获取失败（token失效），可能需要清理登录状态
      // logout() 
      // 这里暂不自动登出，以免网络波动导致掉登，视需求而定
    }
  }

  const logout = () => {
    token.value = null
    currentUser.value = null
    localStorage.removeItem('token')
  }

  return {
    token,
    currentUser,
    isLoggedIn,
    setToken,
    setUser,
    fetchCurrentUser,
    logout
  }
})
