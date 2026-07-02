<template>
  <div
    ref="cardRef"
    class="group relative overflow-hidden rounded-2xl cursor-pointer article-card-separated glass-effect shadow-sm transition-all duration-500 hover:shadow-xl hover:-translate-y-1.5 hover:border-primary-400/40 dark:hover:border-primary-500/30 will-change-transform transform-gpu flex"
    :class="[
      layout === 'list' 
        ? 'flex-col sm:flex-row h-auto sm:h-52 md:h-56' 
        : 'flex-col h-[26rem]',
      isVisible ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-8'
    ]"
    @click="handleClick"
  >
    <!-- 图片区域 -->
    <div 
      class="relative overflow-hidden shrink-0 transform-gpu animate-image-container"
      :class="[
        layout === 'list' 
          ? 'w-full sm:w-[35%] md:w-[32%] h-44 sm:h-full' 
          : 'w-full h-48'
      ]"
    >
      <img
        v-if="article.coverImage"
        :src="article.coverImage"
        :alt="article.title"
        loading="lazy"
        decoding="async"
        class="object-cover w-full h-full transition-transform duration-700 group-hover:scale-105 transform-gpu"
      />
      <!-- 无封面图时：高雅渐变色块 + 微标 -->
      <div
        v-else
        class="w-full h-full bg-gradient-to-tr from-primary-400/80 via-primary-500/90 to-primary-600 dark:from-primary-950/60 dark:to-primary-900/80 flex items-center justify-center text-white/40"
      >
        <svg class="w-10 h-10 animate-pulse" fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" d="M2.25 15.75l5.159-5.159a2.25 2.25 0 013.182 0l5.159 5.159m-1.5-1.5l1.409-1.409a2.25 2.25 0 013.182 0l2.909 2.909m-18 3.75h16.5a1.5 1.5 0 001.5-1.5V6a1.5 1.5 0 00-1.5-1.5H3.75A1.5 1.5 0 002.25 6v12a1.5 1.5 0 001.5 1.5zm10.5-11.25h.008v.008h-.008V8.25zm.375 0a.375 0 11-.75 0 .375 0 01.75 0z" />
        </svg>
      </div>

      <!-- 分类徽章在图片上浮空 -->
      <span
        class="absolute top-3 left-3 px-2.5 py-0.5 text-xs font-semibold rounded-md bg-primary-600/95 text-white shadow-sm backdrop-blur-sm z-10"
      >
        {{ article.categoryName }}
      </span>
    </div>

    <!-- 文字内容区域 -->
    <div class="flex-1 p-5 flex flex-col justify-between min-w-0">
      <div class="min-w-0 flex-1">
        <!-- 标签展示 -->
        <div class="flex flex-wrap gap-1.5 mb-2.5">
          <span
            v-for="tag in article.tags?.slice(0, 3)"
            :key="tag.id"
            class="px-2 py-0.5 text-[10px] font-medium rounded bg-slate-100 text-slate-600 dark:bg-slate-800/40 dark:text-slate-400"
          >
            # {{ tag.name }}
          </span>
        </div>

        <!-- 标题 -->
        <h3
          class="mb-2 text-lg font-bold text-slate-800 dark:text-slate-100 group-hover:text-primary-600 dark:group-hover:text-primary-400 transition-colors line-clamp-2 leading-snug"
        >
          {{ article.title }}
        </h3>

        <!-- 摘要 -->
        <p class="mb-4 text-xs sm:text-sm text-slate-500 dark:text-slate-400 line-clamp-2 sm:line-clamp-3 leading-relaxed">
          {{ article.summary }}
        </p>
      </div>

      <!-- 元数据底部 -->
      <div
        class="flex justify-between items-center text-[11px] text-slate-400 dark:text-slate-500 pt-3 border-t border-slate-100 dark:border-slate-800/60 gap-2 mt-auto"
      >
        <div class="flex items-center gap-x-3 truncate">
          <span class="flex items-center shrink-0">
            <el-icon class="mr-1"><Calendar /></el-icon>
            {{ formatDate(article.publishedTime) }}
          </span>
          <span class="flex items-center shrink-0">
            <el-icon class="mr-1"><View /></el-icon>
            {{ article.views }}
          </span>
          <span class="flex items-center shrink-0">
            <el-icon class="mr-1"><Pointer /></el-icon>
            {{ article.likes || 0 }}
          </span>
        </div>
        <span class="text-primary-600 dark:text-primary-450 font-bold shrink-0 hover:underline">阅读全文 →</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { Calendar, View, Pointer } from "@element-plus/icons-vue";
import { useIntersectionObserver } from "@vueuse/core";
import type { Article } from "@/types";
import { formatDate } from "@/utils/format";

interface Props {
  article: Article;
  layout?: 'grid' | 'list';
}

const props = withDefaults(defineProps<Props>(), {
  layout: 'grid'
});
const router = useRouter();

const cardRef = ref<HTMLElement | null>(null);
const isVisible = ref(false);

// 元素进入视口时触发动画
useIntersectionObserver(
  cardRef,
  ([{ isIntersecting }]) => {
    if (isIntersecting && !isVisible.value) {
      isVisible.value = true;
    }
  },
  {
    threshold: 0.1, // 元素出现 10% 时触发
  }
);

const handleClick = () => {
  router.push(`/article/${props.article.id}`);
};
</script>
