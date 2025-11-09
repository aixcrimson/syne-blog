<template>
  <div class="home bg-transparent">
    <!-- Hero Section -->
    <section class="hero bg-transparent text-white h-screen">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20">
        <div class="text-center">
          <h1 class="text-4xl md:text-5xl font-bold mb-4 text-shadow">
            Syne's Blog
          </h1>
          <p class="text-xl md:text-2xl mb-8 text-shadow">分享技术，记录成长</p>
          <div class="flex justify-center space-x-4">
            <el-button
              type="primary"
              size="large"
              @click="router.push('/articles')"
            >
              浏览文章
            </el-button>
            <el-button size="large" @click="router.push('/about')">
              关于我
            </el-button>
          </div>
        </div>
      </div>
    </section>

    <!-- 内容 -->
    <section class="py-16 bg-transparent">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <!-- 侧边栏 -->
          <Sidebar class="lg:col-spam-1" />

          <!-- 最新文章 -->
          <div class="lg:col-span-3">
            <div class="flex justify-between items-center mb-6">
              <h2 class="text-3xl font-bold text-gray-900 flex items-center gap-3">
                <span class="w-1 h-8 bg-primary-600 rounded-full"></span>
                最新文章
              </h2>
              <el-button text type="primary" @click="router.push('/articles')" class="text-base">
                查看全部 →
              </el-button>
            </div>
            
            <div class="space-y-6">
              <ArticleCard
                v-for="article in latestArticles"
                :key="article.id"
                :article="article"
              />
            </div>
          </div>
        </div>
      </div>
    </section>

  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useRouter } from "vue-router";
import { useArticleStore } from "@/stores/article";
import ArticleCard from "@/components/ArticleCard.vue";
import Sidebar from "@/components/Sidebar.vue";

const router = useRouter();
const articleStore = useArticleStore();

// 最新文章
const latestArticles = computed(() => articleStore.latestArticles.slice(0, 6));

</script>

<style scoped>
/* 文字阴影让文字在背景上更清晰 */
.text-shadow {
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
}

</style>
