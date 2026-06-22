<template>
  <Teleport to="body">
    <!-- 点击外部关闭 -->
    <div
      v-if="isExpanded || showColorPicker"
      class="fixed inset-0 z-40"
      @click="closeAll"
    />

    <div class="fixed right-4 top-1/2 z-50 -translate-y-1/2">
      <div class="relative flex flex-col items-center gap-3">
        <div class="relative flex items-center">
          <!-- 展开的菜单项 - 在按钮左侧 -->
          <Transition
          enter-active-class="transition-all duration-300 ease-out"
          enter-from-class="opacity-0 translate-x-4 scale-95"
          enter-to-class="opacity-100 translate-x-0 scale-100"
          leave-active-class="transition-all duration-200 ease-in"
          leave-from-class="opacity-100 translate-x-0 scale-100"
          leave-to-class="opacity-0 translate-x-4 scale-95"
        >
          <div
            v-show="isExpanded"
            class="absolute right-full mr-3 flex flex-col gap-1 p-2 rounded-2xl border border-slate-200/70 bg-white/80 shadow-lg backdrop-blur-md dark:border-slate-700/70 dark:bg-slate-800/80"
          >
            <!-- 滚动到顶部 -->
            <el-tooltip content="滚动到顶部" placement="left" :show-after="300">
              <button
                class="flex items-center justify-center w-10 h-10 rounded-xl text-slate-600 transition-all duration-200 cursor-pointer hover:bg-slate-100 dark:text-slate-300 dark:hover:bg-slate-700/50"
                @click="scrollToTop"
              >
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 15l7-7 7 7" />
                </svg>
              </button>
            </el-tooltip>

            <!-- 滚动到底部 -->
            <el-tooltip content="滚动到底部" placement="left" :show-after="300">
              <button
                class="flex items-center justify-center w-10 h-10 rounded-xl text-slate-600 transition-all duration-200 cursor-pointer hover:bg-slate-100 dark:text-slate-300 dark:hover:bg-slate-700/50"
                @click="scrollToBottom"
              >
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                </svg>
              </button>
            </el-tooltip>

            <div class="w-full h-px bg-slate-200 dark:bg-slate-700" />

            <!-- 明暗模式切换 -->
            <el-tooltip :content="appStore.isDarkMode ? '切换到浅色模式' : '切换到深色模式'" placement="left" :show-after="300">
              <button
                class="flex items-center justify-center w-10 h-10 rounded-xl text-slate-600 transition-all duration-200 cursor-pointer hover:bg-slate-100 dark:text-slate-300 dark:hover:bg-slate-700/50"
                @click="toggleTheme"
              >
                <el-icon class="text-lg" :class="appStore.isDarkMode ? 'text-yellow-400' : ''">
                  <Sunny v-if="!appStore.isDarkMode" />
                  <Moon v-else />
                </el-icon>
              </button>
            </el-tooltip>

            <!-- 背景模式切换 -->
            <el-tooltip :content="appStore.backgroundMode === 'paper' ? '切换到图片背景' : '切换到纸卡背景'" placement="left" :show-after="300">
              <button
                class="flex items-center justify-center w-10 h-10 rounded-xl text-slate-600 transition-all duration-200 cursor-pointer hover:bg-slate-100 dark:text-slate-300 dark:hover:bg-slate-700/50"
                @click="appStore.toggleBackgroundMode"
              >
                <!-- 图片背景图标 -->
                <svg v-if="appStore.backgroundMode === 'image'" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
                <!-- 纸卡背景图标 -->
                <svg v-else class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
              </button>
            </el-tooltip>

            <div class="w-full h-px bg-slate-200 dark:bg-slate-700" />

            <!-- 布局模式切换 (仅桌面端显示) -->
            <div class="hidden md:block w-full">
              <el-tooltip :content="appStore.articleListLayout === 'grid' ? '切换到列表视图' : '切换到网格视图'" placement="left" :show-after="300">
                <button
                  class="flex items-center justify-center w-10 h-10 rounded-xl text-slate-600 transition-all duration-200 cursor-pointer hover:bg-slate-100 dark:text-slate-300 dark:hover:bg-slate-700/50 w-full"
                  @click="appStore.toggleArticleListLayout"
                >
                  <!-- 列表视图图标 -->
                  <el-icon v-if="appStore.articleListLayout === 'grid'" class="text-lg">
                    <Menu />
                  </el-icon>
                  <!-- 网格视图图标 -->
                  <el-icon v-else class="text-lg">
                    <Grid />
                  </el-icon>
                </button>
              </el-tooltip>
            </div>

            <div class="hidden md:block w-full h-px bg-slate-200 dark:bg-slate-700" />

            <!-- 主题颜色选择 -->
            <div class="relative">
              <el-tooltip content="切换主题颜色" placement="left" :show-after="300" :disabled="showColorPicker">
                <button
                  class="flex items-center justify-center w-10 h-10 rounded-xl text-slate-600 transition-all duration-200 cursor-pointer hover:bg-slate-100 dark:text-slate-300 dark:hover:bg-slate-700/50"
                  @click.stop="showColorPicker = !showColorPicker"
                >
                  <div
                    class="w-5 h-5 rounded-full ring-2 ring-white dark:ring-slate-700"
                    :class="currentColorClass"
                  />
                </button>
              </el-tooltip>

              <!-- 颜色选择器弹出层 -->
              <Transition
                enter-active-class="transition-all duration-200 ease-out"
                enter-from-class="opacity-0 translate-x-2 scale-95"
                enter-to-class="opacity-100 translate-x-0 scale-100"
                leave-active-class="transition-all duration-150 ease-in"
                leave-from-class="opacity-100 translate-x-0 scale-100"
                leave-to-class="opacity-0 translate-x-2 scale-95"
              >
                <div
                  v-show="showColorPicker"
                  class="absolute right-full top-0 mr-3 flex flex-col gap-2 p-2 rounded-xl border border-slate-200/70 bg-white/90 shadow-lg backdrop-blur-md dark:border-slate-700/70 dark:bg-slate-800/90"
                >
                  <button
                    v-for="color in themeColors"
                    :key="color.value"
                    class="group relative flex items-center justify-center w-8 h-8 rounded-lg transition-all duration-200 cursor-pointer hover:scale-110"
                    :class="appStore.themeColor === color.value ? 'ring-2 ring-offset-2 ring-slate-400 dark:ring-slate-500' : ''"
                    :title="color.label"
                    @click="handleThemeColorChange(color.value)"
                  >
                    <div
                      class="w-6 h-6 rounded-full shadow-sm"
                      :class="color.class"
                    />
                    <el-icon
                      v-if="appStore.themeColor === color.value"
                      class="absolute text-white text-xs"
                    >
                      <Check />
                    </el-icon>
                  </button>
                </div>
              </Transition>
            </div>
          </div>
        </Transition>

        <!-- 主触发按钮 - 固定位置 -->
        <button
          class="flex items-center justify-center w-12 h-12 rounded-xl border border-slate-200/70 bg-white/80 shadow-lg backdrop-blur-md transition-all duration-300 cursor-pointer hover:scale-105 hover:shadow-xl dark:border-slate-700/70 dark:bg-slate-800/80"
          @click="toggleExpand"
        >
          <svg
            class="w-5 h-5 text-slate-600 transition-transform duration-300 dark:text-slate-300"
            :class="isExpanded ? 'rotate-45' : ''"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M12 6v6m0 0v6m0-6h6m-6 0H6"
            />
          </svg>
        </button>
        </div>

        <!-- 目录按钮 - 在主按钮下方，大屏幕隐藏 -->
        <div v-if="tocStore.tocItems.length" class="relative xl:hidden mt-3">
          <!-- 目录弹出面板 - 在按钮左侧 -->
          <Transition
            enter-active-class="transition-all duration-200 ease-out"
            enter-from-class="opacity-0 translate-x-2 scale-95"
            enter-to-class="opacity-100 translate-x-0 scale-100"
            leave-active-class="transition-all duration-150 ease-in"
            leave-from-class="opacity-100 translate-x-0 scale-100"
            leave-to-class="opacity-0 translate-x-2 scale-95"
          >
            <div
              v-show="tocStore.showToc"
              class="absolute right-full bottom-0 mr-3 w-56 max-h-80 overflow-hidden rounded-2xl border border-slate-200/70 bg-white/95 shadow-xl backdrop-blur-md dark:border-slate-700/70 dark:bg-slate-800/95"
            >
              <div class="p-3 border-b border-slate-200/70 dark:border-slate-700/70">
                <span class="text-sm font-semibold text-slate-700 dark:text-slate-200">目录</span>
              </div>
              <nav class="p-2 max-h-64 overflow-y-auto">
                <div
                  v-for="item in tocStore.visibleTocItems"
                  :key="item.id"
                  class="w-full flex items-center text-left text-sm py-1.5 px-2 rounded-lg transition-colors cursor-pointer hover:bg-slate-100 dark:hover:bg-slate-700/50"
                  :class="[
                    tocIndentClass(item.level),
                    tocStore.activeHeadingId === item.id
                      ? 'text-primary-600 bg-primary-50 dark:bg-primary-900/30'
                      : 'text-slate-600 dark:text-slate-400'
                  ]"
                  @click="scrollToHeading(item)"
                >
                  <!-- 折叠/展开图标 -->
                  <el-icon
                    v-if="tocStore.hasChildrenMap.get(item.id)"
                    class="mr-1 flex-shrink-0 hover:text-primary-600"
                    @click.stop="tocStore.toggleHeadingCollapse(item.id)"
                  >
                    <ArrowRight v-if="tocStore.collapsedHeadingIds.has(item.id)" />
                    <ArrowDown v-else />
                  </el-icon>
                  <span v-else class="w-[14px] mr-1 inline-block flex-shrink-0"></span>

                  <span class="flex-1 truncate" :title="item.title">
                    {{ item.title }}
                  </span>
                </div>
              </nav>
            </div>
          </Transition>

          <button
            class="flex items-center justify-center w-12 h-12 rounded-xl border border-slate-200/70 bg-white/80 shadow-lg backdrop-blur-md transition-all duration-300 cursor-pointer hover:scale-105 hover:shadow-xl dark:border-slate-700/70 dark:bg-slate-800/80"
            :class="tocStore.showToc ? 'ring-2 ring-primary-500' : ''"
            @click.stop="tocStore.toggleToc"
          >
            <svg class="w-5 h-5 text-slate-600 dark:text-slate-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 10h16M4 14h16M4 18h16" />
            </svg>
          </button>
        </div>

        <!-- 博客信息侧边栏按钮 - 大屏幕隐藏 (仅首页和文章列表展示) -->
        <div v-if="showSidebarButton" class="relative xl:hidden mt-3">
          <button
            class="flex items-center justify-center w-12 h-12 rounded-xl border border-slate-200/70 bg-white/80 shadow-lg backdrop-blur-md transition-all duration-300 cursor-pointer hover:scale-105 hover:shadow-xl dark:border-slate-700/70 dark:bg-slate-800/80"
            @click="appStore.toggleSidebar"
            title="博客信息"
          >
            <svg class="w-5 h-5 text-slate-600 dark:text-slate-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
            </svg>
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { useTocStore } from '@/stores/toc'
import { Sunny, Moon, Check, Menu, Grid, User, ArrowDown, ArrowRight } from '@element-plus/icons-vue'
import type { ThemeColor } from '@/types'

const appStore = useAppStore()
const tocStore = useTocStore()
const route = useRoute()

const isExpanded = ref(false)
const showColorPicker = ref(false)

const showSidebarButton = computed(() => {
  return route.path === '/' || route.path === '/articles'
})

const themeColors = [
  { value: 'blue' as ThemeColor, label: '蓝色', class: 'bg-blue-500' },
  { value: 'purple' as ThemeColor, label: '紫色', class: 'bg-purple-500' },
  { value: 'green' as ThemeColor, label: '绿色', class: 'bg-green-500' },
  { value: 'orange' as ThemeColor, label: '橙色', class: 'bg-orange-500' },
  { value: 'pink' as ThemeColor, label: '粉色', class: 'bg-pink-500' },
]

const currentColorClass = computed(() => {
  const colorMap: Record<string, string> = {
    blue: 'bg-blue-500',
    purple: 'bg-purple-500',
    green: 'bg-green-500',
    orange: 'bg-orange-500',
    pink: 'bg-pink-500',
  }
  return colorMap[appStore.themeColor] || 'bg-blue-500'
})

const toggleExpand = () => {
  isExpanded.value = !isExpanded.value
  if (!isExpanded.value) {
    showColorPicker.value = false
  }
}

const closeAll = () => {
  isExpanded.value = false
  showColorPicker.value = false
  tocStore.closeToc()
  appStore.closeSidebar()
}

const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

const scrollToBottom = () => {
  window.scrollTo({
    top: document.documentElement.scrollHeight,
    behavior: 'smooth'
  })
}

const handleThemeColorChange = (color: ThemeColor) => {
  appStore.setThemeColor(color)
  showColorPicker.value = false
}

const tocIndentClass = (level: number) => {
  if (level >= 4) return 'pl-6'
  if (level === 3) return 'pl-4'
  return ''
}

const scrollToHeading = (item: any) => {
  const target = document.getElementById(item.id)
  if (!target) return
  target.scrollIntoView({ behavior: 'smooth', block: 'start' })
  if (tocStore.hasChildrenMap.get(item.id)) {
    tocStore.toggleHeadingCollapse(item.id)
  }
}

const toggleTheme = (event: MouseEvent) => {
  const x = event.clientX
  const y = event.clientY
  
  const isAppearanceTransition =
    document.startViewTransition !== undefined &&
    !window.matchMedia('(prefers-reduced-motion: reduce)').matches

  if (!isAppearanceTransition) {
    appStore.toggleThemeMode()
    return
  }

  const endRadius = Math.hypot(
    Math.max(x, window.innerWidth - x),
    Math.max(y, window.innerHeight - y)
  )

  const transition = document.startViewTransition(() => {
    appStore.toggleThemeMode()
  })

  transition.ready.then(() => {
    const isDark = appStore.isDarkMode
    const clipPath = [
      `circle(0px at ${x}px ${y}px)`,
      `circle(${endRadius}px at ${x}px ${y}px)`
    ]
    
    document.documentElement.animate(
      {
        clipPath: isDark ? clipPath : [...clipPath].reverse()
      },
      {
        duration: 450,
        easing: 'ease-out',
        pseudoElement: isDark
          ? '::view-transition-new(root)'
          : '::view-transition-old(root)'
      }
    )
  })
}
</script>
