<template>
  <div class="home">
    <!-- Hero Section -->
    <section class="hero bg-gradient-to-r from-primary-600 to-primary-800 text-white">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20">
        <div class="text-center">
          <h1 class="text-4xl md:text-5xl font-bold mb-4">
            欢迎来到我的博客
          </h1>
          <p class="text-xl md:text-2xl mb-8 text-primary-100">
            分享技术，记录成长
          </p>
          <div class="flex justify-center space-x-4">
            <el-button type="primary" size="large" @click="router.push('/articles')">
              浏览文章
            </el-button>
            <el-button size="large" @click="router.push('/about')">
              关于我
            </el-button>
          </div>
        </div>
      </div>
    </section>
    
    <!-- 最新文章 -->
    <section class="py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center mb-8">
          <h2 class="text-3xl font-bold text-gray-900">最新文章</h2>
          <el-button text type="primary" @click="router.push('/articles')">
            查看全部 →
          </el-button>
        </div>
        
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <ArticleCard
            v-for="article in latestArticles"
            :key="article.id"
            :article="article"
          />
        </div>
      </div>
    </section>
    
    <!-- 统计信息 -->
    <section class="bg-white py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-8 text-center">
          <div class="stat-card">
            <div class="text-4xl font-bold text-primary-600 mb-2">
              {{ articleStore.totalArticles }}
            </div>
            <div class="text-gray-600">文章总数</div>
          </div>
          <div class="stat-card">
            <div class="text-4xl font-bold text-primary-600 mb-2">
              {{ totalViews }}
            </div>
            <div class="text-gray-600">总浏览量</div>
          </div>
          <div class="stat-card">
            <div class="text-4xl font-bold text-primary-600 mb-2">
              {{ articleStore.getAllCategories.length }}
            </div>
            <div class="text-gray-600">分类数量</div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useArticleStore } from '@/stores/article'
import ArticleCard from '@/components/ArticleCard.vue'

const router = useRouter()
const articleStore = useArticleStore()

const latestArticles = computed(() => articleStore.latestArticles.slice(0, 6))

const totalViews = computed(() => {
  return articleStore.articles.reduce((sum, article) => sum + article.views, 0)
})
</script>

<style scoped>
.stat-card {
  @apply p-6 rounded-lg transition-transform hover:scale-105;
}
</style>

