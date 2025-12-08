<template>
  <div class="dashboard p-6">
    <!-- 页面标题 -->
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-800">仪表盘</h1>
      <p class="text-gray-500 mt-1">欢迎使用博客管理系统</p>
    </div>

    <!-- 统计卡片区域 -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6 gap-4 mb-6">
      <template v-if="loading">
        <!-- 骨架屏 -->
        <div 
          v-for="i in 6" 
          :key="i" 
          class="glass-card p-4 rounded-lg animate-pulse"
        >
          <div class="flex items-center justify-between">
            <div>
              <div class="h-4 bg-gray-200 rounded w-16 mb-2"></div>
              <div class="h-8 bg-gray-200 rounded w-12"></div>
            </div>
            <div class="h-10 w-10 bg-gray-200 rounded-full"></div>
          </div>
        </div>
      </template>
      <template v-else>
        <!-- 统计卡片 -->
        <div 
          v-for="card in statCards" 
          :key="card.key" 
          class="glass-card p-4 rounded-lg hover:shadow-md transition-shadow"
        >
          <div class="flex items-center justify-between">
            <div>
              <div class="text-sm text-gray-500">{{ card.label }}</div>
              <div class="text-2xl font-bold mt-1" :class="card.color">
                {{ formatNumber(stats[card.key as keyof DashboardStats]) }}
              </div>
            </div>
            <div 
              class="w-10 h-10 rounded-full flex items-center justify-center bg-gray-100"
              :class="card.color"
            >
              <component :is="card.icon" class="w-5 h-5" />
            </div>
          </div>
        </div>
      </template>
    </div>

    <!-- 最近文章和评论区域 -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- 最近文章 -->
      <div class="glass-card rounded-lg overflow-hidden">
        <div class="px-4 py-3 border-b border-gray-200/50 flex items-center justify-between">
          <h2 class="text-lg font-semibold text-gray-800">最近文章</h2>
          <span class="text-sm text-gray-500">最近 5 篇</span>
        </div>
        <div class="p-4">
          <template v-if="loading">
            <!-- 骨架屏 -->
            <div 
              v-for="i in 5" 
              :key="i" 
              class="py-3 border-b border-gray-100 last:border-0 animate-pulse"
            >
              <div class="h-4 bg-gray-200 rounded w-3/4 mb-2"></div>
              <div class="flex justify-between">
                <div class="h-3 bg-gray-200 rounded w-20"></div>
                <div class="h-3 bg-gray-200 rounded w-24"></div>
              </div>
            </div>
          </template>
          <template v-else-if="recentArticles.length === 0">
            <div class="text-center py-8 text-gray-400">
              <el-icon :size="48" class="mb-2"><Document /></el-icon>
              <p>暂无文章</p>
            </div>
          </template>
          <template v-else>
            <div 
              v-for="article in recentArticles" 
              :key="article.id"
              class="py-3 border-b border-gray-100 last:border-0 hover:bg-gray-50/50 -mx-4 px-4 transition-colors cursor-pointer"
            >
              <div class="font-medium text-gray-800 truncate mb-1">
                {{ article.title }}
              </div>
              <div class="flex justify-between text-sm text-gray-500">
                <span class="flex items-center gap-1">
                  <el-icon><View /></el-icon>
                  {{ article.views }}
                </span>
                <span>{{ formatDate(article.publishedTime) }}</span>
              </div>
            </div>
          </template>
        </div>
      </div>

      <!-- 最近评论 -->
      <div class="glass-card rounded-lg overflow-hidden">
        <div class="px-4 py-3 border-b border-gray-200/50 flex items-center justify-between">
          <h2 class="text-lg font-semibold text-gray-800">最近评论</h2>
          <span class="text-sm text-gray-500">最近 5 条</span>
        </div>
        <div class="p-4">
          <template v-if="loading">
            <!-- 骨架屏 -->
            <div 
              v-for="i in 5" 
              :key="i" 
              class="py-3 border-b border-gray-100 last:border-0 animate-pulse"
            >
              <div class="flex items-start gap-3">
                <div class="w-8 h-8 bg-gray-200 rounded-full flex-shrink-0"></div>
                <div class="flex-1">
                  <div class="h-3 bg-gray-200 rounded w-20 mb-2"></div>
                  <div class="h-4 bg-gray-200 rounded w-full mb-2"></div>
                  <div class="h-3 bg-gray-200 rounded w-32"></div>
                </div>
              </div>
            </div>
          </template>
          <template v-else-if="recentComments.length === 0">
            <div class="text-center py-8 text-gray-400">
              <el-icon :size="48" class="mb-2"><ChatDotRound /></el-icon>
              <p>暂无评论</p>
            </div>
          </template>
          <template v-else>
            <div 
              v-for="comment in recentComments" 
              :key="comment.id"
              class="py-3 border-b border-gray-100 last:border-0 hover:bg-gray-50/50 -mx-4 px-4 transition-colors"
            >
              <div class="flex items-start gap-3">
                <div class="w-8 h-8 bg-primary-100 rounded-full flex items-center justify-center flex-shrink-0">
                  <span class="text-sm font-medium text-primary-600">
                    {{ comment.username.charAt(0).toUpperCase() }}
                  </span>
                </div>
                <div class="flex-1 min-w-0">
                  <div class="text-sm text-gray-500 mb-1">
                    <span class="font-medium text-gray-700">{{ comment.username }}</span>
                    <span class="mx-1">评论了</span>
                    <span class="text-primary-600">{{ comment.articleTitle }}</span>
                  </div>
                  <div class="text-gray-800 text-sm line-clamp-2 mb-1">
                    {{ comment.content }}
                  </div>
                  <div class="text-xs text-gray-400">
                    {{ formatDate(comment.createTime) }}
                  </div>
                </div>
              </div>
            </div>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 仪表盘首页
 * 展示博客统计数据、最近文章和最近评论
 */
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Document, 
  Folder, 
  PriceTag, 
  ChatDotRound, 
  View, 
  Star 
} from '@element-plus/icons-vue'
import { dashboardApi } from '@/api/dashboard'
import type { DashboardStats, RecentArticle, RecentComment } from '@/types'
import dayjs from 'dayjs'

/** 加载状态 */
const loading = ref(true)

/** 统计数据 */
const stats = ref<DashboardStats>({
  articleCount: 0,
  categoryCount: 0,
  tagCount: 0,
  commentCount: 0,
  totalViews: 0,
  totalLikes: 0
})

/** 最近文章列表 */
const recentArticles = ref<RecentArticle[]>([])

/** 最近评论列表 */
const recentComments = ref<RecentComment[]>([])

/**
 * 统计卡片配置
 */
const statCards = [
  { key: 'articleCount', label: '文章总数', icon: Document, color: 'text-blue-500' },
  { key: 'categoryCount', label: '分类总数', icon: Folder, color: 'text-purple-500' },
  { key: 'tagCount', label: '标签总数', icon: PriceTag, color: 'text-green-500' },
  { key: 'commentCount', label: '评论总数', icon: ChatDotRound, color: 'text-orange-500' },
  { key: 'totalViews', label: '总浏览量', icon: View, color: 'text-cyan-500' },
  { key: 'totalLikes', label: '总点赞数', icon: Star, color: 'text-pink-500' }
]

/**
 * 格式化日期
 */
const formatDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

/**
 * 格式化数字（大数字显示为 K/M）
 */
const formatNumber = (num: number): string => {
  if (num >= 1000000) {
    return (num / 1000000).toFixed(1) + 'M'
  }
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K'
  }
  return num.toString()
}

/**
 * 加载仪表盘数据
 */
const loadDashboardData = async () => {
  loading.value = true
  try {
    const data = await dashboardApi.getDashboardData()
    stats.value = data.stats
    recentArticles.value = data.recentArticles
    recentComments.value = data.recentComments
  } catch (error) {
    console.error('加载仪表盘数据失败:', error)
    ElMessage.error('加载数据失败，请刷新重试')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDashboardData()
})
</script>



<style scoped>
/* 主题色背景 */
.bg-primary-100 {
  background-color: var(--color-primary-100);
}

.text-primary-600 {
  color: var(--color-primary-600);
}

/* 多行文本截断 */
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
