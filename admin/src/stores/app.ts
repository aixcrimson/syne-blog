import { defineStore } from 'pinia'
import { ref } from 'vue'

/**
 * 主题色类型
 * 支持 5 种主题：蓝、紫、绿、橙、粉
 */
export type ThemeColor = 'blue' | 'purple' | 'green' | 'orange' | 'pink'

/**
 * 主题色选项配置
 */
export const themeColorOptions: { value: ThemeColor; label: string; color: string }[] = [
  { value: 'blue', label: '蓝色', color: '#3b82f6' },
  { value: 'purple', label: '紫色', color: '#a855f7' },
  { value: 'green', label: '绿色', color: '#22c55e' },
  { value: 'orange', label: '橙色', color: '#f97316' },
  { value: 'pink', label: '粉色', color: '#ec4899' }
]

/**
 * 应用状态 Store
 * 管理主题色和侧边栏收起状态
 * @requirements 2.3, 1.4
 */
export const useAppStore = defineStore('app', () => {
  // ==================== 状态 ====================
  
  /** 当前主题色 */
  const themeColor = ref<ThemeColor>('blue')
  
  /** 侧边栏是否收起 */
  const sidebarCollapsed = ref(false)
  
  /** 全局加载状态 */
  const loading = ref(false)

  // ==================== 动作 ====================

  /**
   * 初始化应用状态
   * 从 localStorage 读取保存的设置
   */
  const init = () => {
    // 读取保存的主题色
    const savedTheme = localStorage.getItem('admin_theme_color') as ThemeColor
    if (savedTheme && isValidThemeColor(savedTheme)) {
      themeColor.value = savedTheme
      applyThemeColor(savedTheme)
    } else {
      applyThemeColor(themeColor.value)
    }

    // 读取保存的侧边栏状态
    const savedCollapsed = localStorage.getItem('admin_sidebar_collapsed')
    if (savedCollapsed !== null) {
      sidebarCollapsed.value = savedCollapsed === 'true'
    }
  }

  /**
   * 验证主题色是否有效
   * @param color 待验证的主题色
   */
  const isValidThemeColor = (color: string): color is ThemeColor => {
    return ['blue', 'purple', 'green', 'orange', 'pink'].includes(color)
  }

  /**
   * 设置主题色
   * @param color 主题色
   */
  const setThemeColor = (color: ThemeColor) => {
    themeColor.value = color
    localStorage.setItem('admin_theme_color', color)
    applyThemeColor(color)
  }

  /**
   * 应用主题色到 DOM
   * @param color 主题色
   */
  const applyThemeColor = (color: ThemeColor) => {
    const root = document.documentElement
    
    // 移除旧的主题属性
    root.removeAttribute('data-theme')
    
    // blue 是默认主题，定义在 :root 中，不需要设置 data-theme
    if (color !== 'blue') {
      root.setAttribute('data-theme', color)
    }
  }

  /**
   * 切换侧边栏收起状态
   */
  const toggleSidebar = () => {
    sidebarCollapsed.value = !sidebarCollapsed.value
    localStorage.setItem('admin_sidebar_collapsed', String(sidebarCollapsed.value))
  }

  /**
   * 设置侧边栏收起状态
   * @param collapsed 是否收起
   */
  const setSidebarCollapsed = (collapsed: boolean) => {
    sidebarCollapsed.value = collapsed
    localStorage.setItem('admin_sidebar_collapsed', String(collapsed))
  }

  /**
   * 设置全局加载状态
   * @param value 是否加载中
   */
  const setLoading = (value: boolean) => {
    loading.value = value
  }

  return {
    // 状态
    themeColor,
    sidebarCollapsed,
    loading,
    // 动作
    init,
    setThemeColor,
    toggleSidebar,
    setSidebarCollapsed,
    setLoading,
    // 工具
    isValidThemeColor
  }
})
