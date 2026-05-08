import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { ThemeColor } from '@/types'
import lightThemeBg from '@/assets/images/common/lightTheme.png'
import darkThemeBg from '@/assets/images/common/darkTheme.png'

export const useAppStore = defineStore('app', () => {
  // 状态
  const themeColor = ref<ThemeColor>('blue')
  const themeMode = ref<'light' | 'dark'>('light')
  const backgroundMode = ref<'paper' | 'image'>('paper')
  const articleListLayout = ref<'grid' | 'list'>('grid')
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

    // 背景模式
    const savedBackgroundMode = localStorage.getItem('backgroundMode') as 'paper' | 'image' | null
    if (savedBackgroundMode) {
      backgroundMode.value = savedBackgroundMode
    }

    // 文章列表布局
    const savedLayout = localStorage.getItem('articleListLayout') as 'grid' | 'list' | null
    if (savedLayout) {
      articleListLayout.value = savedLayout
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

  const toggleBackgroundMode = () => {
    backgroundMode.value = backgroundMode.value === 'paper' ? 'image' : 'paper'
    localStorage.setItem('backgroundMode', backgroundMode.value)
    applyTheme()
  }

  const toggleArticleListLayout = () => {
    articleListLayout.value = articleListLayout.value === 'grid' ? 'list' : 'grid'
    localStorage.setItem('articleListLayout', articleListLayout.value)
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

    // 设置背景模式
    if (backgroundMode.value === 'image') {
      root.setAttribute('data-bg-mode', 'image')
    } else {
      root.setAttribute('data-bg-mode', 'paper')
    }

    root.style.setProperty('--page-bg-image-light', `url("${lightThemeBg}")`)
    root.style.setProperty('--page-bg-image-dark', `url("${darkThemeBg}")`)

    console.log('主题应用完成:', {
      color: themeColor.value,
      mode: themeMode.value,
      background: backgroundMode.value
    })
  }

  const setLoading = (value: boolean) => {
    loading.value = value
  }

  return {
    themeColor,
    themeMode,
    backgroundMode,
    articleListLayout,
    isDarkMode,
    loading,
    init,
    setThemeColor,
    toggleThemeMode,
    setThemeMode,
    toggleBackgroundMode,
    toggleArticleListLayout,
    setLoading
  }
})
