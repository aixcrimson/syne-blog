<template>
  <div
    ref="cardRef"
    class="group relative overflow-hidden rounded-2xl cursor-pointer article-card border border-slate-200/70 shadow-sm transition-all duration-700 hover:shadow-xl hover:-translate-y-1 dark:border-slate-700/70 will-change-transform transform-gpu"
    :class="[
      layout === 'list' ? 'h-72 sm:h-80 md:h-[22rem]' : 'h-64',
      isVisible ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-8'
    ]"
    @click="handleClick"
  >
    <!-- 封面图作为背景 -->
    <div class="absolute inset-0 transform-gpu">
      <img
        v-if="article.coverImage"
        :src="article.coverImage"
        :alt="article.title"
        loading="lazy"
        decoding="async"
        class="object-cover w-full h-full transition-transform duration-500 group-hover:scale-110 motion-reduce:transform-none transform-gpu"
      />
      <!-- 无封面时的占位背景 -->
      <div
        v-else
        class="w-full h-full bg-gradient-to-br from-primary-400 to-primary-600"
      />
    </div>

    <!-- 渐变遮罩层 -->
    <div
      class="absolute inset-0 bg-gradient-to-t from-black/80 via-black/40 to-transparent"
    />

    <!-- 卡片内容 - 定位在底部 -->
    <div class="absolute inset-x-0 bottom-0 p-4 sm:p-6 flex flex-col">
      <!-- 标签 -->
      <div class="flex flex-wrap gap-2 mb-3">
        <span
          class="px-2.5 py-0.5 text-xs font-medium rounded-full bg-primary-500/90 text-white backdrop-blur-sm"
        >
          {{ article.categoryName }}
        </span>
        <span
          v-for="tag in article.tags?.slice(0, 2)"
          :key="tag.id"
          class="px-2.5 py-0.5 text-xs font-medium rounded-full bg-white/20 text-white backdrop-blur-sm"
        >
          {{ tag.name }}
        </span>
      </div>

      <!-- 标题 -->
      <h3
        class="mb-2 text-xl font-semibold text-white line-clamp-2 drop-shadow-sm"
      >
        {{ article.title }}
      </h3>

      <!-- 摘要 -->
      <p class="mb-4 text-sm text-white/80 line-clamp-2">
        {{ article.summary }}
      </p>

      <!-- 元信息 -->
      <div
        class="flex flex-wrap justify-between items-center text-xs text-white/70 pt-2 border-t border-white/20 gap-2"
      >
        <div class="flex items-center gap-x-3">
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
        <span class="text-white/90 font-medium shrink-0">阅读更多 →</span>
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
