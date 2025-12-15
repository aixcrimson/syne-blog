<template>
  <aside 
    class="sidebar fixed left-0 top-0 h-screen z-50 transition-all duration-300 glass-sidebar"
    :class="collapsed ? 'w-16' : 'w-60'"
  >
    <!-- Logo 区域 -->
    <div 
      class="logo-area h-16 flex items-center border-b border-gray-200/50 px-4"
      :class="collapsed ? 'justify-center' : 'justify-start'"
    >
      <div class="flex items-center gap-3 overflow-hidden">
        <!-- Logo 图标 -->
        <div class="w-8 h-8 rounded-lg bg-gradient-to-br from-primary-500 to-primary-600 flex items-center justify-center flex-shrink-0">
          <el-icon class="text-white text-lg">
            <Promotion />
          </el-icon>
        </div>
        <!-- 系统名称 -->
        <transition name="fade">
          <span 
            v-show="!collapsed" 
            class="text-lg font-bold text-gray-800 whitespace-nowrap"
          >
            博客管理
          </span>
        </transition>
      </div>
    </div>

    <!-- 菜单列表 -->
    <nav class="menu-list py-4 px-2 overflow-y-auto" style="height: calc(100vh - 8rem);">
      <ul class="space-y-1">
        <li v-for="item in menuItems" :key="item.path">
          <div
            class="menu-item flex items-center gap-3 px-3 py-2.5 rounded-lg cursor-pointer transition-all duration-200"
            :class="[
              activeMenu === item.path 
                ? 'bg-primary-500 text-white shadow-md' 
                : 'text-gray-600 hover:bg-gray-100 hover:text-gray-900',
              collapsed ? 'justify-center' : ''
            ]"
            :title="collapsed ? item.name : ''"
            @click="handleMenuClick(item)"
          >
            <el-icon class="text-lg flex-shrink-0">
              <component :is="item.icon" />
            </el-icon>
            <transition name="fade">
              <span 
                v-show="!collapsed" 
                class="text-sm font-medium whitespace-nowrap"
              >
                {{ item.name }}
              </span>
            </transition>
          </div>
        </li>
      </ul>
    </nav>

    <!-- 收起/展开按钮 -->
    <div class="collapse-btn absolute bottom-4 left-0 right-0 px-2">
      <div
        class="flex items-center justify-center py-2 rounded-lg cursor-pointer text-gray-500 hover:bg-gray-100 hover:text-gray-700 transition-all"
        :class="collapsed ? 'px-0' : 'px-3'"
        @click="emit('toggle')"
      >
        <el-icon class="text-lg transition-transform duration-300" :class="collapsed ? 'rotate-180' : ''">
          <DArrowLeft />
        </el-icon>
        <transition name="fade">
          <span v-show="!collapsed" class="ml-2 text-sm">收起菜单</span>
        </transition>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
/**
 * 侧边栏导航组件
 * 实现 Logo 显示、菜单项渲染、高亮和展开/收起功能
 */
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { MenuItem } from '@/types'

/**
 * 组件属性
 */
interface Props {
  /** 是否收起状态 */
  collapsed: boolean
}

defineProps<Props>()

/**
 * 组件事件
 */
const emit = defineEmits<{
  (e: 'toggle'): void
}>()

const route = useRoute()
const router = useRouter()

/**
 * 菜单配置列表
 */
const menuItems: MenuItem[] = [
  { path: '/dashboard', name: '仪表盘', icon: 'Odometer' },
  { path: '/article', name: '文章管理', icon: 'Document' },
  { path: '/category', name: '分类管理', icon: 'Folder' },
  { path: '/tag', name: '标签管理', icon: 'PriceTag' },
  { path: '/comment', name: '评论管理', icon: 'ChatDotRound' },
  { path: '/navigation', name: '导航管理', icon: 'Link' },
  { path: '/user', name: '用户管理', icon: 'User' },
  { path: '/settings', name: '个人设置', icon: 'Setting' }
]

/**
 * 当前激活的菜单路径
 */
const activeMenu = computed(() => {
  // 匹配一级路径
  const path = '/' + route.path.split('/')[1]
  return path
})

/**
 * 处理菜单点击
 */
const handleMenuClick = (item: MenuItem) => {
  router.push(item.path)
}
</script>


<style scoped>
/* 毛玻璃侧边栏 */
.glass-sidebar {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-right: 1px solid rgba(229, 231, 235, 0.5);
  box-shadow: 4px 0 24px rgba(0, 0, 0, 0.05);
}

/* 菜单项激活状态阴影 */
.menu-item.bg-primary-500 {
  box-shadow: 0 4px 12px rgba(var(--color-primary-500), 0.3);
}

/* 淡入淡出动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 自定义滚动条 */
.menu-list::-webkit-scrollbar {
  width: 4px;
}

.menu-list::-webkit-scrollbar-thumb {
  background-color: #d1d5db;
  border-radius: 9999px;
}

.menu-list::-webkit-scrollbar-thumb:hover {
  background-color: #9ca3af;
}

/* 暗色模式下的滚动条 */
.dark .menu-list::-webkit-scrollbar-thumb {
  background-color: #4a5568;
}

.dark .menu-list::-webkit-scrollbar-thumb:hover {
  background-color: #718096;
}
</style>
