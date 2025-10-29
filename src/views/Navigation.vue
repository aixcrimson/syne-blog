<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- 页面标题 -->
      <div class="text-center mb-12">
        <h1 class="text-4xl font-bold text-gray-900 mb-4">网站导航</h1>
        <p class="text-lg text-gray-600">收集常用的优质网站，助力高效工作与学习</p>
      </div>

      <!-- 导航分类 -->
      <div class="space-y-12">
        <div v-for="category in categories" :key="category" class="space-y-6">
          <h2 class="text-2xl font-bold text-gray-800 border-l-4 border-primary-500 pl-4">
            {{ category }}
          </h2>
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <a
              v-for="site in getSitesByCategory(category)"
              :key="site.id"
              :href="site.url"
              target="_blank"
              rel="noopener noreferrer"
              class="group bg-white rounded-lg shadow-sm hover:shadow-lg transition-all duration-300 p-6 border border-gray-200 hover:border-primary-500"
            >
              <div class="flex items-start space-x-4">
                <div class="flex-shrink-0">
                  <div class="w-12 h-12 bg-gradient-to-br from-primary-400 to-primary-600 rounded-lg flex items-center justify-center text-white font-bold text-xl group-hover:scale-110 transition-transform">
                    {{ site.icon || site.name.charAt(0) }}
                  </div>
                </div>
                <div class="flex-1 min-w-0">
                  <h3 class="text-lg font-semibold text-gray-900 group-hover:text-primary-600 transition-colors truncate">
                    {{ site.name }}
                  </h3>
                  <p class="text-sm text-gray-600 mt-1 line-clamp-2">
                    {{ site.description }}
                  </p>
                  <div class="mt-2 flex items-center text-xs text-gray-400">
                    <el-icon class="mr-1"><Link /></el-icon>
                    <span class="truncate">{{ site.url }}</span>
                  </div>
                </div>
              </div>
            </a>
          </div>
        </div>
      </div>

      <!-- 添加网站提示 -->
      <div class="mt-12 text-center">
        <div class="inline-block bg-blue-50 rounded-lg px-6 py-4">
          <p class="text-sm text-gray-600">
            <el-icon class="mr-2"><InfoFilled /></el-icon>
            如有更多优质网站推荐，欢迎联系我～
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Link, InfoFilled } from '@element-plus/icons-vue'
import type { NavigationSite } from '@/types'

// 导航网站数据
const navigationSites: NavigationSite[] = [
  // 开发工具
  {
    id: 1,
    name: 'GitHub',
    description: '全球最大的代码托管平台，开源项目的家园',
    url: 'https://github.com',
    category: '开发工具',
    icon: 'GH'
  },
  {
    id: 2,
    name: 'Stack Overflow',
    description: '程序员问答社区，解决编程难题的好帮手',
    url: 'https://stackoverflow.com',
    category: '开发工具',
    icon: 'SO'
  },
  {
    id: 3,
    name: 'MDN Web Docs',
    description: 'Web开发权威文档，前端开发者必备',
    url: 'https://developer.mozilla.org',
    category: '开发工具',
    icon: 'MD'
  },
  {
    id: 4,
    name: 'Vue.js',
    description: '渐进式JavaScript框架官方文档',
    url: 'https://vuejs.org',
    category: '开发工具',
    icon: 'V'
  },
  {
    id: 5,
    name: 'Can I Use',
    description: '检查浏览器对Web技术的支持情况',
    url: 'https://caniuse.com',
    category: '开发工具',
    icon: 'CI'
  },
  {
    id: 6,
    name: 'CodePen',
    description: '在线代码编辑器和前端开发社区',
    url: 'https://codepen.io',
    category: '开发工具',
    icon: 'CP'
  },

  // 设计资源
  {
    id: 7,
    name: 'Dribbble',
    description: '设计师作品展示和灵感获取平台',
    url: 'https://dribbble.com',
    category: '设计资源',
    icon: 'Dr'
  },
  {
    id: 8,
    name: 'Behance',
    description: 'Adobe旗下创意作品展示平台',
    url: 'https://www.behance.net',
    category: '设计资源',
    icon: 'Be'
  },
  {
    id: 9,
    name: 'Unsplash',
    description: '免费高质量图片素材库',
    url: 'https://unsplash.com',
    category: '设计资源',
    icon: 'Un'
  },
  {
    id: 10,
    name: 'iconfont',
    description: '阿里巴巴矢量图标库',
    url: 'https://www.iconfont.cn',
    category: '设计资源',
    icon: 'IF'
  },
  {
    id: 11,
    name: 'Figma',
    description: '协作式界面设计工具',
    url: 'https://www.figma.com',
    category: '设计资源',
    icon: 'Fi'
  },

  // 学习教程
  {
    id: 12,
    name: '菜鸟教程',
    description: '提供各种编程语言的基础教程',
    url: 'https://www.runoob.com',
    category: '学习教程',
    icon: '菜'
  },
  {
    id: 13,
    name: 'freeCodeCamp',
    description: '免费学习编程的优质平台',
    url: 'https://www.freecodecamp.org',
    category: '学习教程',
    icon: 'FC'
  },
  {
    id: 14,
    name: 'Coursera',
    description: '在线课程学习平台',
    url: 'https://www.coursera.org',
    category: '学习教程',
    icon: 'Co'
  },
  {
    id: 15,
    name: 'bilibili',
    description: '国内优质的视频学习平台',
    url: 'https://www.bilibili.com',
    category: '学习教程',
    icon: 'B'
  },

  // 实用工具
  {
    id: 16,
    name: 'ProcessOn',
    description: '在线作图工具，支持流程图、思维导图等',
    url: 'https://www.processon.com',
    category: '实用工具',
    icon: 'PO'
  },
  {
    id: 17,
    name: '正则表达式测试',
    description: '在线测试和学习正则表达式',
    url: 'https://regex101.com',
    category: '实用工具',
    icon: 'RE'
  },
  {
    id: 18,
    name: 'JSON 格式化',
    description: 'JSON数据格式化和验证工具',
    url: 'https://www.json.cn',
    category: '实用工具',
    icon: 'JS'
  },
  {
    id: 19,
    name: 'TinyPNG',
    description: '在线图片压缩工具',
    url: 'https://tinypng.com',
    category: '实用工具',
    icon: 'TP'
  },
  {
    id: 20,
    name: 'Carbon',
    description: '创建漂亮的代码截图',
    url: 'https://carbon.now.sh',
    category: '实用工具',
    icon: 'Ca'
  }
]

// 获取所有分类
const categories = computed(() => {
  const categorySet = new Set(navigationSites.map(site => site.category))
  return Array.from(categorySet)
})

// 根据分类获取网站
const getSitesByCategory = (category: string) => {
  return navigationSites.filter(site => site.category === category)
}
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>

