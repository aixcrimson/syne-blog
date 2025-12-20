import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserInfo } from '@/types'

/** Token 存储的 key */
const TOKEN_KEY = 'token'

/**
 * 用户状态 Store
 * 管理用户信息和 Token
 * @requirements 3.4, 3.5
 */
export const useUserStore = defineStore('user', () => {
  // ==================== 状态 ====================
  
  /** 访问令牌 */
  const token = ref<string | null>(null)
  
  /** 用户信息 */
  const userInfo = ref<UserInfo | null>(null)

  // ==================== 计算属性 ====================

  /**
   * 是否已登录
   */
  const isLoggedIn = computed(() => !!token.value)

  /**
   * 是否为管理员
   */
  const isAdmin = computed(() => userInfo.value?.role === 1)

  /**
   * 当前用户 ID
   */
  const userId = computed(() => userInfo.value?.id ?? null)

  /**
   * 当前用户名
   */
  const username = computed(() => userInfo.value?.username ?? '')

  // ==================== 动作 ====================

  /**
   * 初始化用户状态
   * 从 localStorage 读取 Token
   */
  const init = () => {

    const savedToken = localStorage.getItem(TOKEN_KEY)
    if (savedToken) {
      token.value = savedToken
    }
  }

  /**
   * 设置 Token
   * @param newToken 新的 Token
   */
  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem(TOKEN_KEY, newToken)
  }

  /**
   * 获取 Token
   * @returns 当前 Token
   */
  const getToken = (): string | null => {
    // 优先返回内存中的 token，否则从 localStorage 读取
    if (token.value) {
      return token.value
    }
    const savedToken = localStorage.getItem(TOKEN_KEY)
    if (savedToken) {
      token.value = savedToken
    }
    return token.value
  }

  /**
   * 清除 Token
   */
  const clearToken = () => {
    token.value = null
    localStorage.removeItem(TOKEN_KEY)
  }

  /**
   * 设置用户信息
   * @param info 用户信息
   */
  const setUserInfo = (info: UserInfo) => {
    userInfo.value = info
  }

  /**
   * 清除用户信息
   */
  const clearUserInfo = () => {
    userInfo.value = null
  }

  /**
   * 登录成功后设置状态
   * @param newToken Token
   * @param info 用户信息
   */
  const loginSuccess = (newToken: string, info: UserInfo) => {
    setToken(newToken)
    setUserInfo(info)
  }

  /**
   * 退出登录
   * 清除 Token 和用户信息
   */
  const logout = () => {
    clearToken()
    clearUserInfo()
  }

  /**
   * 更新用户信息（部分更新）
   * @param info 要更新的用户信息字段
   */
  const updateUserInfo = (info: Partial<UserInfo>) => {
    if (userInfo.value) {
      userInfo.value = { ...userInfo.value, ...info }
    }
  }

  return {
    // 状态
    token,
    userInfo,
    // 计算属性
    isLoggedIn,
    isAdmin,
    userId,
    username,
    // 动作
    init,
    setToken,
    getToken,
    clearToken,
    setUserInfo,
    clearUserInfo,
    loginSuccess,
    logout,
    updateUserInfo
  }
})
