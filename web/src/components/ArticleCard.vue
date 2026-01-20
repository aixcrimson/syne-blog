<template>
  <div
    class="paper-card paper-card-hover overflow-hidden cursor-pointer article-card"
    @click="handleClick"
  >
    <!-- 封面图 -->
    <div v-if="article.coverImage" class="overflow-hidden h-72">
      <img
        :src="article.coverImage"
        :alt="article.title"
        class="object-cover w-full h-full transition-transform duration-300 hover:scale-110 motion-reduce:transform-none"
      />
    </div>

    <!-- 卡片内容 -->
    <div class="p-6">
      <!-- 标题 -->
      <h3
        class="mb-2 text-xl font-semibold text-slate-900 transition-colors dark:text-slate-100 line-clamp-2 hover:text-primary-600"
      >
        {{ article.title }}
      </h3>

      <!-- 摘要 -->
      <p class="mb-4 text-sm text-slate-600 dark:text-slate-400 line-clamp-3">
        {{ article.summary }}
      </p>

      <!-- 标签 -->
      <div class="flex flex-wrap gap-2 mb-4">
        <el-tag type="primary" size="small">{{ article.categoryName }}</el-tag>
        <el-tag
          v-for="tag in article.tags"
          :key="tag.id"
          type="info"
          size="small"
        >
          {{ tag.name }}
        </el-tag>
      </div>

      <!-- 元信息 -->
      <div
        class="flex justify-between items-center text-xs text-slate-500 dark:text-slate-500"
      >
        <div class="flex items-center space-x-4">
          <span class="flex items-center">
            <el-icon class="mr-1"><Calendar /></el-icon>
            {{ formatDate(article.publishedTime) }}
          </span>
          <span class="flex items-center">
            <el-icon class="mr-1"><View /></el-icon>
            {{ article.views }}
          </span>
          <span class="flex items-center">
            <el-icon class="mr-1"><Pointer /></el-icon>
            {{ article.likes || 0 }}
          </span>
          <span class="flex items-center">
            <el-icon class="mr-1"><Star /></el-icon>
            {{ article.favorites || 0 }}
          </span>
        </div>
        <el-button text type="primary" size="small"> 阅读更多 → </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";
import { Calendar, View, Pointer, Star } from "@element-plus/icons-vue";
import type { Article } from "@/types";
import { formatDate } from "@/utils/format";

interface Props {
  article: Article;
}

const props = defineProps<Props>();
const router = useRouter();

const handleClick = () => {
  router.push(`/article/${props.article.id}`);
};
</script>
