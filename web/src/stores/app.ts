import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { ThemeColor, UserInfo } from '@/types'
import authorAvatar from '@/assets/images/avatar/author.jpg'

export const useAppStore = defineStore('app', () => {
  // 状态
  const themeColor = ref<ThemeColor>('blue')
  const themeMode = ref<'light' | 'dark'>('light')
  const userInfo = ref<UserInfo>({
    name: 'Syne',
    avatar: authorAvatar,
    bio: '热爱技术,专注于软件开发',
    email: 'hitori150221@outlook.com',
    github: 'https://github.com/aixcrimson',
    bilibili: 'https://space.bilibili.com/366835700?spm_id_from=333.1007.0.0'
  })
  const loading = ref(false)

  // 计算属性
  const isDarkMode = computed(() => themeMode.value === 'dark')

  // 动作
  const init = () => {
    // 读取主题模式
    const savedMode = localStorage.getItem('themeMode') as 'light' | 'dark' | null
    if (savedMode) {
      themeMode.value = savedMode
    } else {
      // 可以根据系统偏好自动设置
      themeMode.value = window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
    }

    // 从本地存储读取主题色设置
    const savedColor = localStorage.getItem('themeColor') as ThemeColor

    // 应用主题色（如果有保存的就用保存的，否则用默认的）
    if (savedColor) {
      themeColor.value = savedColor
    }

    // 应用主题
    applyTheme()
  }

  const setThemeColor = (color: ThemeColor) => {
    console.log('设置主题色:', color)
    themeColor.value = color
    localStorage.setItem('themeColor', color)
    applyTheme()
  }

  // 新增：切换主题模式
  const toggleThemeMode = () => {
    themeMode.value = themeMode.value === 'light' ? 'dark' : 'light'
    localStorage.setItem('themeMode', themeMode.value)
    applyTheme()
  }

  // 设置主题模式
  const setThemeMode = (mode: 'light' | 'dark') => {
    themeMode.value = mode
    localStorage.setItem('themeMode', mode)
    applyTheme()
  }

  // 统一的主题应用函数
  const applyTheme = () => {
    const root = document.documentElement

    // 设置主题色
    if (themeColor.value !== 'blue') {
      root.setAttribute('data-theme', themeColor.value)
    } else {
      root.removeAttribute('data-theme')
    }

    // 设置明暗模式
    if (themeMode.value === 'dark') {
      root.setAttribute('data-theme-mode', 'dark')
      document.body.classList.add('dark')
    } else {
      root.removeAttribute('data-theme-mode')
      document.body.classList.remove('dark')
    }

    console.log('主题应用完成:', { color: themeColor.value, mode: themeMode.value })
  }

  const setLoading = (value: boolean) => {
    loading.value = value
  }

  const updateUserInfo = (info: Partial<UserInfo>) => {
    userInfo.value = { ...userInfo.value, ...info }
  }

  return {
    themeColor,
    themeMode,
    isDarkMode,
    userInfo,
    loading,
    init,
    setThemeColor,
    toggleThemeMode,
    setThemeMode,
    setLoading,
    updateUserInfo
  }
})

