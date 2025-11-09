<template>
  <footer class="footer-glass text-gray-300 mt-auto">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
        <!-- 关于部分 -->
        <div>
          <h3 class="text-white text-lg font-semibold mb-4">关于博客</h3>
          <p class="text-sm leading-6">
            {{ appStore.userInfo.bio }}
          </p>
        </div>
        
        <!-- 快速链接 -->
        <div>
          <h3 class="text-white text-lg font-semibold mb-4">快速链接</h3>
          <ul class="space-y-2">
            <li v-for="item in menuItems" :key="item.path">
              <router-link
                :to="item.path"
                class="text-sm hover:text-primary-400 transition-colors"
              >
                {{ item.name }}
              </router-link>
            </li>
          </ul>
        </div>
        
        <!-- 联系方式 -->
        <div>
          <h3 class="text-white text-lg font-semibold mb-4">联系我</h3>
          <ul class="space-y-2">
            <li class="flex items-center space-x-2">
              <el-icon><Message /></el-icon>
              <a :href="`mailto:${appStore.userInfo.email}`" class="text-sm hover:text-primary-400">
                {{ appStore.userInfo.email }}
              </a>
            </li>
            <li v-if="appStore.userInfo.github" class="flex items-center space-x-2">
              <el-icon><Link /></el-icon>
              <a :href="appStore.userInfo.github" target="_blank" class="text-sm hover:text-primary-400">
                GitHub
              </a>
            </li>
            <li v-if="appStore.userInfo.bilibili" class="flex items-center space-x-2">
              <el-icon><Link /></el-icon>
              <a :href="appStore.userInfo.bilibili" target="_blank" class="text-sm hover:text-primary-400">
                Bilibili
              </a>
            </li>
          </ul>
        </div>
      </div>
      
      <!-- 版权信息 -->
      <div class="border-t border-gray-800 mt-8 pt-8 text-center">
        <p class="text-sm">
          © {{ currentYear }} {{ appStore.userInfo.name }}. All rights reserved.
        </p>
        <p class="text-xs mt-2 text-gray-500">
          Built with Vue 3 + TypeScript + Tailwind CSS
        </p>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAppStore } from '@/stores/app'
import { Message, Link } from '@element-plus/icons-vue'

const appStore = useAppStore()

const currentYear = computed(() => new Date().getFullYear())

const menuItems = [
  { name: '首页', path: '/' },
  { name: '文章', path: '/articles' },
  { name: '网站导航', path: '/navigation' },
  { name: '关于', path: '/about' }
]
</script>

<style scoped>
/* 毛玻璃效果 Footer */
.footer-glass {
  background: rgba(17, 24, 39, 0.85);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}
</style>

