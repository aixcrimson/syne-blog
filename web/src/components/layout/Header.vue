<template>
  <header class="header-glass shadow-sm sticky top-0 z-50">
    <nav class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between items-center h-16">
        <!-- Logo - 左侧 -->
        <div class="flex items-center min-w-[200px]">
          <router-link to="/" class="flex items-center space-x-2">
            <div class="w-8 h-8 bg-gradient-to-br from-primary-500 to-primary-700 rounded-lg flex items-center justify-center">
              <span class="text-white font-bold text-lg">X</span>
            </div>
            <span class="text-xl font-bold text-gray-900">syne-blog</span>
          </router-link>
        </div>
        
        <!-- 导航菜单 - 中间 -->
        <div class="hidden md:flex items-center space-x-8">
          <router-link
            v-for="item in menuItems"
            :key="item.path"
            :to="item.path"
            class="nav-link text-gray-700 hover:text-primary-600 px-3 py-2 rounded-md text-sm font-medium transition-colors flex items-center gap-2"
            exact-active-class="text-primary-600 bg-primary-50"
          >
            <SvgIcon v-if="item.icon" :name="item.icon" size="18" color="#000" />
            {{ item.name }}
          </router-link>
        </div>
        
        <!-- 右侧工具栏 -->
        <div class="flex items-center space-x-3">
          <!-- GitHub 链接 -->
          <el-tooltip content="访问我的 GitHub" placement="bottom">
            <a
              :href="appStore.userInfo.github"
              target="_blank"
              rel="noopener noreferrer"
              class="github-link hidden md:flex items-center justify-center w-10 h-10 rounded-full hover:bg-gray-100 transition-colors"
            >
              <svg class="w-6 h-6 text-gray-700 hover:text-gray-900" fill="currentColor" viewBox="0 0 24 24">
                <path fill-rule="evenodd" d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.531 1.032 1.531 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z" clip-rule="evenodd" />
              </svg>
            </a>
          </el-tooltip>

          <!-- 搜索 -->
          <el-tooltip content="搜索" placement="bottom">
            <button
              class="github-link hidden md:flex items-center justify-center w-10 h-10 rounded-full hover:bg-gray-100 transition-colors"
              @click="handleSearch"
            >
              <el-icon class="text-xl text-gray-700 hover:text-gray-900">
                <Search />
              </el-icon>
            </button>
          </el-tooltip>

          <!-- 主题颜色切换 -->
          <el-dropdown @command="handleThemeColorChange" trigger="click">
            <el-button circle class="theme-color-toggle" title="切换主题色">
              <el-icon>
                <Brush />
              </el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="blue">
                  <div class="flex items-center space-x-2">
                    <div class="w-4 h-4 rounded-full bg-blue-500"></div>
                    <span>蓝色</span>
                    <el-icon v-if="appStore.themeColor === 'blue'" class="ml-2"><Check /></el-icon>
                  </div>
                </el-dropdown-item>
                <el-dropdown-item command="purple">
                  <div class="flex items-center space-x-2">
                    <div class="w-4 h-4 rounded-full bg-purple-500"></div>
                    <span>紫色</span>
                    <el-icon v-if="appStore.themeColor === 'purple'" class="ml-2"><Check /></el-icon>
                  </div>
                </el-dropdown-item>
                <el-dropdown-item command="green">
                  <div class="flex items-center space-x-2">
                    <div class="w-4 h-4 rounded-full bg-green-500"></div>
                    <span>绿色</span>
                    <el-icon v-if="appStore.themeColor === 'green'" class="ml-2"><Check /></el-icon>
                  </div>
                </el-dropdown-item>
                <el-dropdown-item command="orange">
                  <div class="flex items-center space-x-2">
                    <div class="w-4 h-4 rounded-full bg-orange-500"></div>
                    <span>橙色</span>
                    <el-icon v-if="appStore.themeColor === 'orange'" class="ml-2"><Check /></el-icon>
                  </div>
                </el-dropdown-item>
                <el-dropdown-item command="pink">
                  <div class="flex items-center space-x-2">
                    <div class="w-4 h-4 rounded-full bg-pink-500"></div>
                    <span>粉色</span>
                    <el-icon v-if="appStore.themeColor === 'pink'" class="ml-2"><Check /></el-icon>
                  </div>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          
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
          class="flex items-center gap-2 px-3 py-2 rounded-md text-base font-medium text-gray-700 hover:text-primary-600 hover:bg-primary-50"
          exact-active-class="text-primary-600 bg-primary-50"
          @click="mobileMenuOpen = false"
        >
          <SvgIcon v-if="item.icon" :name="item.icon" size="20" />
          {{ item.name }}
        </router-link>
        
        <!-- 移动端 GitHub 链接 -->
        <a
          :href="appStore.userInfo.github"
          target="_blank"
          rel="noopener noreferrer"
          class="block px-3 py-2 rounded-md text-base font-medium text-gray-700 hover:text-primary-600 hover:bg-primary-50"
        >
          GitHub
        </a>
      </div>
    </nav>
  </header>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useAppStore } from '@/stores/app'
import { Menu, Brush, Check, Search } from '@element-plus/icons-vue'
import type { MenuItem, ThemeColor } from '@/types'

const appStore = useAppStore()
const mobileMenuOpen = ref(false)

const menuItems: MenuItem[] = [
  { name: '首页', path: '/', icon: 'blogshouye' },
  { name: '文章', path: '/articles', icon: 'blogwenzhang' },
  { name: '网站导航', path: '/navigation', icon: 'blogwangyedaohang' },
  { name: '关于', path: '/about', icon: 'blogguanyu' }
]

/**
 * 处理搜索按钮点击
 */
const handleSearch = () => {
  // TODO: 实现搜索功能
  console.log('点击搜索按钮')
}

/**
 * 处理主题颜色切换
 */
const handleThemeColorChange = (color: ThemeColor) => {
  console.log('Header: 切换主题色到', color)
  console.log('当前 appStore:', appStore)
  appStore.setThemeColor(color)
  console.log('切换完成，新颜色:', appStore.themeColor)
}
</script>

<style scoped>
/* 毛玻璃效果 Header */
.header-glass {
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.3);
}

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
  background: var(--color-primary-600);
  transition: all 0.3s ease;
  transform: translateX(-50%);
}

.nav-link:hover::after,
.nav-link.router-link-exact-active::after {
  width: 80%;
}

.github-link {
  transition: all 0.3s ease;
}

.github-link:hover {
  transform: scale(1.1);
}

.theme-color-toggle {
  transition: all 0.3s ease;
}

.theme-color-toggle:hover {
  transform: rotate(15deg);
}
</style>

