import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

interface CurrentUser {
  id?: number
  username: string
  avatar?: string
  role?: number
}

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string | null>(localStorage.getItem('token'))
  const currentUser = ref<CurrentUser | null>(null)

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  
  // 动作
  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUser = (user: CurrentUser) => {
    currentUser.value = user
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
    logout
  }
})
