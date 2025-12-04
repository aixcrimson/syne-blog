<script setup lang="ts">
/**
 * 管理端主布局组件
 * 实现左侧导航栏 + 右侧内容区布局
 * 应用毛玻璃效果样式
 */
import { ref, provide } from 'vue'
import Sidebar from './Sidebar.vue'
import Navbar from './Navbar.vue'

/**
 * 侧边栏是否收起状态
 */
const collapsed = ref(false)

/**
 * 切换侧边栏展开/收起
 */
const toggleSidebar = () => {
  collapsed.value = !collapsed.value
}

// 提供给子组件使用
provide('collapsed', collapsed)
provide('toggleSidebar', toggleSidebar)
</script>

<template>
  <div class="admin-layout min-h-screen bg-gray-100">
    <!-- 左侧导航栏 -->
    <Sidebar :collapsed="collapsed" @toggle="toggleSidebar" />
    
    <!-- 右侧主内容区 -->
    <div 
      class="main-container transition-all duration-300"
      :class="collapsed ? 'ml-16' : 'ml-60'"
    >
      <!-- 顶部导航栏 -->
      <Navbar :collapsed="collapsed" @toggle="toggleSidebar" />
      
      <!-- 页面内容区 -->
      <main class="p-6 mt-16">
        <div class="glass-card p-6 min-h-[calc(100vh-8rem)]">
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
</style>
