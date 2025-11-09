<template>
  <div
    class="article-card glass-card rounded-lg shadow-sm hover:shadow-lg transition-all duration-300 overflow-hidden cursor-pointer"
    @click="handleClick"
  >
    <!-- 封面图 -->
    <div v-if="article.coverImage" class="h-72 overflow-hidden">
      <img
        :src="article.coverImage"
        :alt="article.title"
        class="w-full h-full object-cover transition-transform duration-300 hover:scale-110"
      />
    </div>
    
    <!-- 卡片内容 -->
    <div class="p-6">
      <!-- 标题 -->
      <h3 class="text-xl font-bold text-gray-900 mb-2 line-clamp-2 hover:text-primary-600 transition-colors">
        {{ article.title }}
      </h3>
      
      <!-- 摘要 -->
      <p class="text-gray-600 text-sm mb-4 line-clamp-3">
        {{ article.summary }}
      </p>
      
      <!-- 标签 -->
      <div class="flex flex-wrap gap-2 mb-4">
        <el-tag type="primary" size="small">{{ article.category }}</el-tag>
        <el-tag
          v-for="tag in article.tags.slice(0, 2)"
          :key="tag"
          type="info"
          size="small"
        >
          {{ tag }}
        </el-tag>
      </div>
      
      <!-- 元信息 -->
      <div class="flex items-center justify-between text-xs text-gray-500">
        <div class="flex items-center space-x-4">
          <span class="flex items-center">
            <el-icon class="mr-1"><Calendar /></el-icon>
            {{ formatDate(article.createdAt) }}
          </span>
          <span class="flex items-center">
            <el-icon class="mr-1"><View /></el-icon>
            {{ article.views }}
          </span>
        </div>
        <el-button text type="primary" size="small">
          阅读更多 →
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { Calendar, View } from '@element-plus/icons-vue'
import type { Article } from '@/types'
import { formatDate } from '@/utils/format'

interface Props {
  article: Article
}

const props = defineProps<Props>()
const router = useRouter()

const handleClick = () => {
  router.push(`/article/${props.article.id}`)
}
</script>

<style scoped>
.glass-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.article-card {
  @apply transform hover:-translate-y-1;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>

