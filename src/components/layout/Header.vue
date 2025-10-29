<template>
  <header class="bg-white shadow-sm sticky top-0 z-50">
    <nav class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between items-center h-16">
        <!-- Logo -->
        <div class="flex items-center">
          <router-link to="/" class="flex items-center space-x-2">
            <div class="w-8 h-8 bg-gradient-to-br from-primary-500 to-primary-700 rounded-lg flex items-center justify-center">
              <span class="text-white font-bold text-lg">D</span>
            </div>
            <span class="text-xl font-bold text-gray-900">我的博客</span>
          </router-link>
        </div>
        
        <!-- 导航菜单 -->
        <div class="hidden md:flex items-center space-x-8">
          <router-link
            v-for="item in menuItems"
            :key="item.path"
            :to="item.path"
            class="nav-link text-gray-700 hover:text-primary-600 px-3 py-2 rounded-md text-sm font-medium transition-colors"
            active-class="text-primary-600 bg-primary-50"
          >
            {{ item.name }}
          </router-link>
        </div>
        
        <!-- 主题切换按钮 -->
        <div class="flex items-center space-x-4">
          <el-tooltip content="切换主题" placement="bottom">
            <el-button
              circle
              @click="toggleTheme"
              class="theme-toggle"
            >
              <el-icon v-if="appStore.theme === 'light'">
                <Sunny />
              </el-icon>
              <el-icon v-else>
                <Moon />
              </el-icon>
            </el-button>
          </el-tooltip>
          
          <!-- 移动端菜单按钮 -->
          <el-button
            class="md:hidden"
            circle
            @click="mobileMenuOpen = !mobileMenuOpen"
          >
            <el-icon>
              <Menu />
            </el-icon>
          </el-button>
        </div>
      </div>
      
      <!-- 移动端菜单 -->
      <div v-if="mobileMenuOpen" class="md:hidden py-4 border-t">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          class="block px-3 py-2 rounded-md text-base font-medium text-gray-700 hover:text-primary-600 hover:bg-primary-50"
          active-class="text-primary-600 bg-primary-50"
          @click="mobileMenuOpen = false"
        >
          {{ item.name }}
        </router-link>
      </div>
    </nav>
  </header>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useAppStore } from '@/stores/app'
import { Sunny, Moon, Menu } from '@element-plus/icons-vue'
import type { MenuItem } from '@/types'

const appStore = useAppStore()
const mobileMenuOpen = ref(false)

const menuItems: MenuItem[] = [
  { name: '首页', path: '/' },
  { name: '文章', path: '/articles' },
  { name: '关于', path: '/about' }
]

const toggleTheme = () => {
  appStore.toggleTheme()
}
</script>

<style scoped>
.nav-link {
  position: relative;
}

.nav-link::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 2px;
  background: var(--el-color-primary);
  transition: all 0.3s ease;
  transform: translateX(-50%);
}

.nav-link:hover::after,
.nav-link.router-link-active::after {
  width: 80%;
}
</style>

