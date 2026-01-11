import { ref, onMounted, onUnmounted } from 'vue'

/**
 * 响应式断点配置
 * 与 Tailwind CSS 断点保持一致
 */
const BREAKPOINTS = {
  /** 移动端断点 */
  mobile: 768
} as const

/**
 * 响应式状态管理 Composable
 * 监听窗口尺寸变化，提供设备类型判断
 * 
 * @example
 * ```ts
 * const { isMobile, isDesktop } = useResponsive()
 * // 在模板中使用：v-if="isMobile"
 * ```
 */
export function useResponsive() {
  /** 是否为移动端 (< 768px) */
  const isMobile = ref(false)
  
  /** 是否为桌面端 (>= 768px) */
  const isDesktop = ref(true)

  /**
   * 更新响应式状态
   */
  const updateResponsiveState = () => {
    const width = window.innerWidth
    isMobile.value = width < BREAKPOINTS.mobile
    isDesktop.value = width >= BREAKPOINTS.mobile
  }

  /**
   * 防抖处理的 resize 事件处理器
   */
  let resizeTimer: ReturnType<typeof setTimeout> | null = null
  const handleResize = () => {
    if (resizeTimer) {
      clearTimeout(resizeTimer)
    }
    resizeTimer = setTimeout(() => {
      updateResponsiveState()
    }, 100)
  }

  onMounted(() => {
    // 初始化状态
    updateResponsiveState()
    // 监听窗口尺寸变化
    window.addEventListener('resize', handleResize)
  })

  onUnmounted(() => {
    // 清理监听器
    window.removeEventListener('resize', handleResize)
    if (resizeTimer) {
      clearTimeout(resizeTimer)
    }
  })

  return {
    isMobile,
    isDesktop
  }
}
