<template>
  <div class="home">
    <!-- Hero Section -->
    <section
      class="hero relative min-h-[calc(100vh-64px)]"
    >
      <div
        class="flex items-center px-4 py-20 mx-auto max-w-7xl h-full sm:px-6 lg:px-8"
      >
        <div class="max-w-3xl text-left">
          <p class="page-mono text-xs uppercase tracking-[0.35em] text-slate-500 dark:text-slate-400">
            Index / 00
          </p>
          <h1 class="mt-3 text-4xl font-semibold tracking-tight text-slate-900 dark:text-slate-50 md:text-5xl">
            Syne's Blog
          </h1>
          <p class="mt-4 text-lg text-slate-600 dark:text-slate-300 md:text-2xl">
            分享技术，记录成长
          </p>
          <div class="mt-8 flex flex-wrap gap-3">
            <el-button
              type="primary"
              size="large"
              @click="router.push('/articles')"
            >
              浏览文章
            </el-button>
            <el-button size="large" plain @click="router.push('/about')">
              关于我
            </el-button>
          </div>
        </div>
      </div>

      <!-- 滚动提示箭头 -->
      <div
        class="flex absolute right-0 left-0 bottom-4 justify-center md:bottom-8"
      >
        <div
          class="flex justify-center items-center w-12 h-12 rounded-full border border-slate-200/70 bg-white/70 shadow-[0_10px_24px_-18px_rgba(15,23,42,0.4)] transition-all duration-300 animate-bounce cursor-pointer hover:border-blue-300/80 hover:shadow-[0_16px_45px_-22px_rgba(37,99,235,0.45)] dark:border-slate-800/70 dark:bg-slate-900/60 motion-reduce:animate-none"
          @click="scrollToContent"
        >
          <el-icon class="text-2xl text-slate-700 dark:text-slate-200">
            <ArrowDown />
          </el-icon>
        </div>
      </div>
    </section>

    <!-- 内容 -->
    <section id="main-content" class="pt-16 pb-8 bg-transparent">
      <div class="px-4 mx-auto max-w-7xl sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-4">
          <!-- 侧边栏 -->
          <Sidebar class="hidden lg:block lg:col-span-1" />

          <!-- 最新文章 -->
          <div class="lg:col-span-3">
            <div class="flex items-center gap-4 mb-6">
              <span class="page-mono text-xs text-slate-500 dark:text-slate-400">
                #01
              </span>
              <h2
                class="text-3xl font-semibold tracking-tight text-slate-900 dark:text-slate-100"
              >
                最新文章
              </h2>
              <span class="flex-1 h-px bg-slate-200/70 dark:bg-slate-700/60" />
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
  if (!mainContent) return;

  const headerOffset = 72;
  const top =
    mainContent.getBoundingClientRect().top + window.scrollY - headerOffset;
  window.scrollTo({ top, behavior: "smooth" });
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
