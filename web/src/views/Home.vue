<template>
  <div class="home">
    <!-- Hero Section -->
    <section
      class="hero relative min-h-[calc(100vh-64px)] flex items-center justify-center"
    >
      <div class="text-center px-4">
        <h1 class="text-5xl font-semibold tracking-tight text-slate-900 dark:text-slate-50 md:text-6xl lg:text-7xl" style="font-family: 'Georgia', 'Times New Roman', serif;">
          Syne's Blog
        </h1>
        <p class="mt-8 text-xl text-slate-600 dark:text-slate-300 md:text-2xl h-8">
          <Typewriter
            v-if="notices.length > 0"
            :texts="noticeTexts"
            :type-speed="200"
            :delete-speed="100"
            :pause-time="1500"
          />
        </p>
      </div>

      <!-- 滚动提示箭头 -->
      <div
        class="absolute right-0 left-0 bottom-4 flex justify-center md:bottom-8"
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
    <section
      id="main-content"
      class="pt-16 pb-8 bg-transparent"
      :style="{ minHeight: mainContentMinHeight }"
    >
      <div class="px-4 mx-auto max-w-[1480px] sm:px-6 lg:px-8">
        <div class="flex flex-col gap-6 xl:grid xl:grid-cols-[minmax(0,900px)_260px] xl:items-start xl:justify-center">

          <!-- 最新文章 -->
          <div class="min-w-0 xl:w-full xl:max-w-[900px]">

            <div class="flex items-center gap-4 mb-6">
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
              <!-- 骨架屏 -->
              <template v-if="loading">
                <ArticleCardSkeleton v-for="i in 3" :key="i" />
              </template>

              <!-- 文章列表 -->
              <ArticleCard
                v-for="article in latestArticles"
                v-else
                :key="article.id"
                :article="article"
              />
            </div>
          </div>

          <!-- 侧边栏 -->
          <Sidebar class="hidden xl:block xl:w-[260px] xl:flex-none" />
        </div>
      </div>

    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import ArticleCard from "@/components/ArticleCard.vue";
import ArticleCardSkeleton from "@/components/ArticleCardSkeleton.vue";
import Sidebar from "@/components/Sidebar.vue";
import Typewriter from "@/components/Typewriter.vue";
import type { Article, Notice } from "@/types";
import { articleApi } from "@/api/article";
import { siteApi } from "@/api/site";
import { ArrowDown } from "@element-plus/icons-vue";

const router = useRouter();

const headerOffset = 72;
const mainContentMinHeight = `calc(100vh - ${headerOffset}px)`;

const scrollToContent = () => {
  const mainContent = document.getElementById("main-content");
  if (!mainContent) return;

  const top =
    mainContent.getBoundingClientRect().top + window.scrollY - headerOffset;
  window.scrollTo({ top, behavior: "smooth" });
};

// 最新文章
const latestArticles = ref<Article[]>([]);
const loading = ref(false);

// 公告
const notices = ref<Notice[]>([]);
const noticeTexts = computed(() =>
  notices.value.map((n) => n.content)
);

const getLatestArticles = async () => {
  loading.value = true;
  try {
    const res = await articleApi.getList({
      page: 1,
      pageSize: 6,
    });
    latestArticles.value = res.list;
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const getNotices = async () => {
  try {
    notices.value = await siteApi.getNotices();
  } catch (error) {
    console.error(error);
  }
};

onMounted(() => {
  getLatestArticles();
  getNotices();
});
</script>
