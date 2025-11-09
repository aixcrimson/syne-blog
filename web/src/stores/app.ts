import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { ThemeColor, UserInfo } from '@/types'
import authorAvatar from '@/assets/images/avatar/author.jpg'

export const useAppStore = defineStore('app', () => {
  // 状态
  const themeColor = ref<ThemeColor>('blue')
  const userInfo = ref<UserInfo>({
    name: 'Syne',
    avatar: authorAvatar,
    bio: '热爱技术,专注于软件开发',
    email: 'hitori150221@outlook.com',
    github: 'https://github.com/aixcrimson',
    bilibili: 'https://space.bilibili.com/366835700?spm_id_from=333.1007.0.0'
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
    const root = document.documentElement
    
    // 移除旧的主题属性
    root.removeAttribute('data-theme')
    
    // 如果不是默认蓝色主题，设置对应的 data-theme 属性
    // blue 是默认主题，定义在 :root 中，不需要设置 data-theme
    if (color !== 'blue') {
      root.setAttribute('data-theme', color)
    }
    
    console.log('主题色应用完成:', color)
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

