import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { ThemeMode, UserInfo } from '@/types'

export const useAppStore = defineStore('app', () => {
  // 状态
  const theme = ref<ThemeMode>('light')
  const userInfo = ref<UserInfo>({
    name: 'DutyZero',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=DutyZero',
    bio: '热爱技术，专注于前端开发和用户体验',
    email: 'contact@example.com',
    github: 'https://github.com',
    twitter: 'https://twitter.com'
  })
  const loading = ref(false)

  // 动作
  const init = () => {
    // 从本地存储读取主题设置
    const savedTheme = localStorage.getItem('theme') as ThemeMode
    if (savedTheme) {
      theme.value = savedTheme
      applyTheme(savedTheme)
    }
  }

  const setTheme = (newTheme: ThemeMode) => {
    theme.value = newTheme
    localStorage.setItem('theme', newTheme)
    applyTheme(newTheme)
  }

  const toggleTheme = () => {
    const newTheme = theme.value === 'light' ? 'dark' : 'light'
    setTheme(newTheme)
  }

  const applyTheme = (themeMode: ThemeMode) => {
    if (themeMode === 'dark') {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
  }

  const setLoading = (value: boolean) => {
    loading.value = value
  }

  const updateUserInfo = (info: Partial<UserInfo>) => {
    userInfo.value = { ...userInfo.value, ...info }
  }

  return {
    theme,
    userInfo,
    loading,
    init,
    setTheme,
    toggleTheme,
    setLoading,
    updateUserInfo
  }
})

