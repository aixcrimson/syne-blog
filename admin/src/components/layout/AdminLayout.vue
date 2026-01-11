<template>
  <div class="admin-layout min-h-screen bg-gray-100">
    <!-- 移动端遮罩层 -->
    <Transition name="fade">
      <div
        v-if="isMobile && sidebarOpen"
        class="sidebar-overlay"
        @click="closeSidebar"
      ></div>
    </Transition>

    <!-- 左侧导航栏 -->
    <Sidebar
      :collapsed="collapsed"
      :is-mobile="isMobile"
      :is-open="sidebarOpen"
      @toggle="toggleSidebar"
      @close="closeSidebar"
    />
    
    <!-- 右侧主内容区 -->
    <div 
      class="main-container transition-all duration-300"
      :class="mainContainerClass"
    >
      <!-- 顶部导航栏 -->
      <Navbar
        :collapsed="collapsed"
        :is-mobile="isMobile"
        @toggle="handleNavbarToggle"
      />
      
      <!-- 页面内容区 -->
      <main class="p-4 md:p-6 mt-16">
        <div class="glass-card p-4 md:p-6 min-h-[calc(100vh-8rem)]">
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 管理端主布局组件
 * 实现响应式布局：移动端抽屉式侧边栏 + 桌面端可折叠侧边栏
 */
import { ref, computed, provide, watch } from 'vue'
import Sidebar from './Sidebar.vue'
import Navbar from './Navbar.vue'
import { useResponsive } from '@/utils/useResponsive'

// 响应式状态
const { isMobile, isDesktop } = useResponsive()

/**
 * 侧边栏是否收起状态（桌面端使用）
 */
const collapsed = ref(false)

/**
 * 移动端侧边栏是否打开
 */
const sidebarOpen = ref(false)

/**
 * 主内容区的 class
 * 移动端：无左侧 margin
 * 桌面端：根据侧边栏状态设置 margin
 */
const mainContainerClass = computed(() => {
  if (isMobile.value) {
    return 'ml-0'
  }
  return collapsed.value ? 'ml-16' : 'ml-60'
})

/**
 * 切换侧边栏展开/收起（桌面端）
 */
const toggleSidebar = () => {
  collapsed.value = !collapsed.value
}

/**
 * 打开侧边栏（移动端）
 */
const openSidebar = () => {
  sidebarOpen.value = true
}

/**
 * 关闭侧边栏（移动端）
 */
const closeSidebar = () => {
  sidebarOpen.value = false
}

/**
 * 顶部导航栏的 toggle 按钮点击处理
 * 移动端：打开/关闭侧边栏
 * 桌面端：折叠/展开侧边栏
 */
const handleNavbarToggle = () => {
  if (isMobile.value) {
    sidebarOpen.value ? closeSidebar() : openSidebar()
  } else {
    toggleSidebar()
  }
}

/**
 * 监听设备类型变化
 * 从移动端切换到桌面端时，关闭移动端侧边栏
 */
watch(isDesktop, (newVal) => {
  if (newVal) {
    sidebarOpen.value = false
  }
})

// 提供给子组件使用
provide('collapsed', collapsed)
provide('toggleSidebar', toggleSidebar)
provide('isMobile', isMobile)
</script>


<style scoped>
/* 主布局容器 */
.admin-layout {
  display: flex;
  min-height: 100vh;
}

/* 主内容区容器 */
.main-container {
  flex: 1;
  min-height: 100vh;
}

/* 移动端遮罩层 */
.sidebar-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 40;
  backdrop-filter: blur(2px);
}

/* 遮罩层淡入淡出动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>

