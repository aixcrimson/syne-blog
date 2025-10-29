import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { ThemeColor, UserInfo } from '@/types'

export const useAppStore = defineStore('app', () => {
  // 状态
  const themeColor = ref<ThemeColor>('blue')
  const userInfo = ref<UserInfo>({
    name: 'DutyZero',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=DutyZero',
    bio: '热爱技术，专注于前端开发和用户体验',
    email: 'contact@example.com',
    github: 'https://github.com/aixcrimson',
    twitter: 'https://twitter.com'
  })
  const loading = ref(false)

  // 动作
  const init = () => {
    // 从本地存储读取主题色设置
    const savedColor = localStorage.getItem('themeColor') as ThemeColor
    
    // 应用主题色（如果有保存的就用保存的，否则用默认的）
    if (savedColor) {
      themeColor.value = savedColor
      applyThemeColor(savedColor)
    } else {
      // 应用默认主题色
      applyThemeColor(themeColor.value)
    }
  }

  const setThemeColor = (color: ThemeColor) => {
    console.log('设置主题色:', color)
    themeColor.value = color
    localStorage.setItem('themeColor', color)
    applyThemeColor(color)
  }

  const applyThemeColor = (color: ThemeColor) => {
    console.log('应用主题色:', color)
    const colorMap: Record<ThemeColor, Record<string, string>> = {
      blue: {
        '50': '#eff6ff',
        '100': '#dbeafe',
        '200': '#bfdbfe',
        '300': '#93c5fd',
        '400': '#60a5fa',
        '500': '#3b82f6',
        '600': '#2563eb',
        '700': '#1d4ed8',
        '800': '#1e40af',
        '900': '#1e3a8a',
      },
      purple: {
        '50': '#faf5ff',
        '100': '#f3e8ff',
        '200': '#e9d5ff',
        '300': '#d8b4fe',
        '400': '#c084fc',
        '500': '#a855f7',
        '600': '#9333ea',
        '700': '#7e22ce',
        '800': '#6b21a8',
        '900': '#581c87',
      },
      green: {
        '50': '#f0fdf4',
        '100': '#dcfce7',
        '200': '#bbf7d0',
        '300': '#86efac',
        '400': '#4ade80',
        '500': '#22c55e',
        '600': '#16a34a',
        '700': '#15803d',
        '800': '#166534',
        '900': '#14532d',
      },
      orange: {
        '50': '#fff7ed',
        '100': '#ffedd5',
        '200': '#fed7aa',
        '300': '#fdba74',
        '400': '#fb923c',
        '500': '#f97316',
        '600': '#ea580c',
        '700': '#c2410c',
        '800': '#9a3412',
        '900': '#7c2d12',
      },
      pink: {
        '50': '#fdf2f8',
        '100': '#fce7f3',
        '200': '#fbcfe8',
        '300': '#f9a8d4',
        '400': '#f472b6',
        '500': '#ec4899',
        '600': '#db2777',
        '700': '#be185d',
        '800': '#9f1239',
        '900': '#831843',
      }
    }

    const colors = colorMap[color]
    const root = document.documentElement
    
    console.log('设置CSS变量:', colors)
    
    // 设置CSS变量
    Object.entries(colors).forEach(([key, value]) => {
      root.style.setProperty(`--color-primary-${key}`, value)
      console.log(`设置 --color-primary-${key} = ${value}`)
    })
    
    // 同时更新Element Plus的主题色
    root.style.setProperty('--el-color-primary', colors['500'])
    root.style.setProperty('--el-color-primary-light-3', colors['400'])
    root.style.setProperty('--el-color-primary-light-5', colors['300'])
    root.style.setProperty('--el-color-primary-light-7', colors['200'])
    root.style.setProperty('--el-color-primary-light-8', colors['100'])
    root.style.setProperty('--el-color-primary-light-9', colors['50'])
    root.style.setProperty('--el-color-primary-dark-2', colors['600'])
    
    console.log('主题色应用完成')
  }

  const setLoading = (value: boolean) => {
    loading.value = value
  }

  const updateUserInfo = (info: Partial<UserInfo>) => {
    userInfo.value = { ...userInfo.value, ...info }
  }

  return {
    themeColor,
    userInfo,
    loading,
    init,
    setThemeColor,
    setLoading,
    updateUserInfo
  }
})

