<template>
  <div class="py-12 bg-transparent article-detail">
    <div class="px-4 mx-auto max-w-4xl sm:px-6 lg:px-8">
      <div
        v-if="article"
        class="overflow-hidden rounded-lg shadow-sm glass-card"
      >
        <!-- 文章头部 -->
        <div class="p-8 border-b article-header">
          <div class="mb-4">
            <router-link
              to="/articles"
              class="text-sm text-primary-600 hover:text-primary-700"
            >
              ← 返回列表
            </router-link>
          </div>

          <h1 class="mb-4 text-4xl font-bold text-gray-900">
            {{ article.title }}
          </h1>

          <div
            class="flex flex-wrap items-center mb-4 space-x-4 text-sm text-gray-600"
          >
            <span class="flex items-center">
              <el-icon class="mr-1"><User /></el-icon>
              {{ defaultAuthor }}
            </span>
            <span class="flex items-center">
              <el-icon class="mr-1"><Calendar /></el-icon>
              {{ formatDate(article.publishedTime) }}
            </span>
            <span class="flex items-center">
              <el-icon class="mr-1"><View /></el-icon>
              {{ article.views }} 次阅读
            </span>
          </div>

          <div class="flex flex-wrap gap-2 mb-4">
            <el-tag type="primary">{{ article.categoryName }}</el-tag>
            <el-tag v-for="tag in article.tags" :key="tag.id" type="info">
              {{ tag.name }}
            </el-tag>
          </div>

          <p class="text-lg text-gray-600">{{ article.summary }}</p>
        </div>

        <!-- 封面图 -->
        <div v-if="article.coverImage" class="overflow-hidden aspect-video">
          <img
            :src="article.coverImage"
            :alt="article.title"
            class="object-cover w-full h-full"
          />
        </div>

        <!-- 文章内容 -->
        <div class="p-8 article-content">
          <div class="markdown-content" v-html="renderedContent"></div>
        </div>

        <!-- 文章底部 -->
        <div class="p-8 bg-gray-50 border-t article-footer">
          <div class="flex justify-between items-center">
            <div class="text-sm text-gray-600">
              最后更新：{{ formatDate(article.updateTime || "") }}
            </div>
            <div class="flex space-x-2">
              <el-button type="primary" circle @click="handleLike" title="点赞">
                <el-icon><Pointer /></el-icon>
              </el-button>
              <el-button
                type="warning"
                circle
                @click="handleFavorite"
                title="收藏"
              >
                <el-icon><Star /></el-icon>
              </el-button>
              <el-button circle @click="handleShare" title="分享">
                <el-icon><Share /></el-icon>
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 文章未找到 -->
      <el-empty v-else description="文章未找到" />

      <!-- 评论区 -->
      <div v-if="article" class="mt-12">
        <CommentSection :article-id="articleId" />
      </div>

      <!-- 相关文章 -->
      <div v-if="relatedArticles.length > 0" class="mt-12">
        <h2 class="mb-6 text-2xl font-bold text-gray-900">相关文章</h2>
        <div class="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
          <ArticleCard
            v-for="relatedArticle in relatedArticles"
            :key="relatedArticle.id"
            :article="relatedArticle"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRoute } from "vue-router";
import {
  User,
  Calendar,
  View,
  Star,
  Share,
  Pointer,
} from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import ArticleCard from "@/components/ArticleCard.vue";
import CommentSection from "@/components/CommentSection.vue";
import { renderMarkdown } from "@/utils/markdown";
import { formatDate } from "@/utils/format";
import { articleApi } from "@/api/article";
import type { Article } from "@/types";

const route = useRoute();

const articleId = computed(() => Number(route.params.id));
const article = ref<Article | null>(null);
const relatedArticles = ref<Article[]>([]);
const loading = ref(false);

// 默认作者
const defaultAuthor = "站长";

const renderedContent = computed(() => {
  if (!article.value) return "";
  return renderMarkdown(article.value.content);
});

// 获取文章详情
const fetchArticle = async () => {
  loading.value = true;
  try {
    article.value = await articleApi.getById(articleId.value);
  } catch (e) {
    console.error("获取文章详情失败:", e);
    ElMessage.error("获取文章失败");
  } finally {
    loading.value = false;
  }
};

// 获取推荐文章
const fetchRecommendedArticles = async () => {
  try {
    relatedArticles.value = await articleApi.getRecommended(3);
  } catch (e) {
    console.error("获取推荐文章失败:", e);
  }
};

const handleLike = async () => {
  try {
    const res = await articleApi.like(articleId.value);
    if (article.value && res) {
      if (typeof res.likes === "number") {
        article.value.likes = res.likes;
      }
      ElMessage.success(res.liked ? "点赞成功" : "已取消点赞");
    }
  } catch (e) {
    console.error("点赞失败:", e);
    ElMessage.error("点赞失败");
  }
};

// 收藏
const handleFavorite = async () => {
  try {
    const res = await articleApi.favorite(articleId.value);

    // 更新 UI 计数和状态
    if (article.value && res) {
      if (typeof res.favorites === "number") {
        article.value.favorites = res.favorites;
      }

      ElMessage.success(res.favorited ? "收藏成功" : "已取消收藏");
    }
  } catch (e) {
    console.error("收藏失败:", e);
    ElMessage.error("收藏失败");
  }
};

const handleShare = () => {
  if (navigator.share) {
    navigator.share({
      title: article.value?.title,
      text: article.value?.summary,
      url: window.location.href,
    });
  } else {
    navigator.clipboard.writeText(window.location.href);
    ElMessage.success("链接已复制到剪贴板");
  }
};

// 增加浏览量
const handleIncreaseViews = async () => {
  try {
    const res = await articleApi.increaseViews(articleId.value);
    if (article.value && res && typeof res.views === "number") {
      article.value.views = res.views;
    }
  } catch (e) {
    console.error("增加浏览量失败:", e);
  }
};

onMounted(() => {
  fetchArticle().then(() => {
    // 文章加载成功后再增加浏览量，确保 article.value 存在，且体验更好
    handleIncreaseViews();
  });
  fetchRecommendedArticles();
  window.scrollTo({ top: 0, behavior: "smooth" });
});
</script>

<style scoped>
.glass-card {
  background: var(--glass-bg);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid var(--glass-border);
}

.article-header {
  background: transparent;
}
</style>
