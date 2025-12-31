<template>
  <div class="home bg-transparent">
    <!-- Hero Section -->
    <section
      class="hero bg-transparent text-white h-[calc(100vh-64px)] relative"
    >
      <div
        class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20 h-full flex items-center justify-center"
      >
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

      <!-- 滚动提示箭头 -->
      <!-- 滚动提示箭头 -->
      <div
        class="absolute bottom-8 left-1/2 transform -translate-x-1/2 cursor-pointer animate-bounce flex items-center justify-center w-12 h-12 rounded-full bg-black/20 dark:bg-white/10 backdrop-blur-sm hover:bg-primary-500/80 transition-all duration-300"
        @click="scrollToContent"
      >
        <el-icon class="text-white text-2xl">
          <ArrowDown />
        </el-icon>
      </div>
    </section>

    <!-- 内容 -->
    <section id="main-content" class="py-16 bg-transparent">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <!-- 侧边栏 -->
          <Sidebar class="lg:col-spam-1" />

          <!-- 最新文章 -->
          <div class="lg:col-span-3">
            <div class="flex justify-between items-center mb-6">
              <h2
                class="text-3xl font-bold text-gray-900 dark:text-gray-100 flex items-center gap-3"
              >
                <span class="w-1 h-8 bg-primary-600 rounded-full"></span>
                最新文章
              </h2>
              <el-button
                type="primary"
                plain
                round
                @click="router.push('/articles')"
              >
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
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import ArticleCard from "@/components/ArticleCard.vue";
import Sidebar from "@/components/Sidebar.vue";
import type { Article } from "@/types";
import { articleApi } from "@/api/article";
import { ArrowDown } from "@element-plus/icons-vue";

const router = useRouter();

const scrollToContent = () => {
  const mainContent = document.getElementById("main-content");
  if (mainContent) {
    mainContent.scrollIntoView({ behavior: "smooth" });
  }
};

// 最新文章
const latestArticles = ref<Article[]>([]);
// const latestArticles = computed(() => articleStore.latestArticles.slice(0, 6));

const getLatestArticles = async () => {
  try {
    const res = await articleApi.getList({
      page: 1,
      pageSize: 6,
    });
    latestArticles.value = res.list;
  } catch (error) {
    console.error(error);
  }
};

onMounted(() => {
  getLatestArticles();
});
</script>

<style scoped>
/* 文字阴影让文字在背景上更清晰 */
.text-shadow {
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
}
</style>
