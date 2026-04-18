<template>
  <div class="py-12 article-detail">
    <!-- 阅读进度条 -->
    <div
      class="reading-progress"
      role="progressbar"
      aria-label="阅读进度"
      :aria-valuenow="Math.round(readingProgress)"
      aria-valuemin="0"
      aria-valuemax="100"
    >
      <div
        class="reading-progress__bar"
        :style="{ width: `${readingProgress}%` }"
      ></div>
    </div>

    <!-- 主布局容器 -->
    <div class="px-4 mx-auto max-w-[1400px] sm:px-6 lg:px-8">
      <div class="grid grid-cols-1 xl:grid-cols-[1fr_280px] gap-8">
        <!-- 左侧：文章主体 -->
        <div class="min-w-0">
          <div
            v-if="article"
            class="paper-card overflow-hidden"
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

              <h1 class="mb-4 text-4xl font-semibold text-slate-900 dark:text-slate-50">
                {{ article.title }}
              </h1>

              <div
                class="flex flex-wrap items-center mb-4 space-x-4 text-sm text-slate-600 dark:text-slate-400"
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

              <p class="text-lg text-slate-600 dark:text-slate-300">
                {{ article.summary }}
              </p>
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
              <div ref="contentRef" class="markdown-content" v-html="renderedContent"></div>
            </div>

            <!-- 文章底部 -->
            <div
              class="p-8 border-t border-slate-200/70 bg-white/70 dark:border-slate-700/70 dark:bg-slate-900/60 article-footer"
            >
              <div class="flex justify-between items-center">
                <div class="text-sm text-slate-600 dark:text-slate-400">
                  最后更新：{{ formatDate(article.updateTime || "") }}
                </div>
                <div class="flex space-x-2">
                  <el-button
                    :type="article?.isLiked ? 'primary' : 'info'"
                    :plain="!article?.isLiked"
                    round
                    @click="handleLike"
                    title="点赞"
                  >
                    <el-icon class="mr-1"><Pointer /></el-icon>
                    {{ article?.likes || 0 }}
                  </el-button>
                  <el-button
                    :type="article?.isFavorited ? 'warning' : 'info'"
                    :plain="!article?.isFavorited"
                    round
                    @click="handleFavorite"
                    title="收藏"
                  >
                    <el-icon class="mr-1"><Star /></el-icon>
                    {{ article?.favorites || 0 }}
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
            <h2 class="mb-6 text-2xl font-semibold text-slate-900 dark:text-slate-100">
              相关文章
            </h2>
            <div class="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
              <ArticleCard
                v-for="relatedArticle in relatedArticles"
                :key="relatedArticle.id"
                :article="relatedArticle"
              />
            </div>
          </div>
        </div>

        <!-- 右侧：目录（大屏幕显示） -->
        <aside v-if="article && tocItems.length" class="hidden xl:block">
          <div class="sticky top-24">
            <div class="toc-card paper-card">
              <div class="toc-title">目录</div>
              <nav class="toc-list">
                <button
                  v-for="item in tocItems"
                  :key="item.id"
                  type="button"
                  class="toc-item"
                  :class="[
                    tocIndentClass(item.level),
                    activeHeadingId === item.id ? 'is-active' : ''
                  ]"
                  @click="scrollToHeading(item.id)"
                >
                  {{ item.title }}
                </button>
              </nav>
            </div>
          </div>
        </aside>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from "vue";
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
import { renderMarkdownWithToc } from "@/utils/markdown";
import { formatDate } from "@/utils/format";
import { articleApi } from "@/api/article";
import type { Article } from "@/types";
import { useUserStore } from "@/stores/user";
import { useTocStore } from "@/stores/toc";

const userStore = useUserStore();
const tocStore = useTocStore();
const route = useRoute();

const articleId = computed(() => Number(route.params.id));
const article = ref<Article | null>(null);
const relatedArticles = ref<Article[]>([]);
const loading = ref(false);
const contentRef = ref<HTMLElement | null>(null);
const readingProgress = ref(0);
const activeHeadingId = ref("");
const headingPositions = ref<{ id: string; top: number }[]>([]);

// 默认作者
const defaultAuthor = "站长";

const markdownResult = computed(() => {
  if (!article.value) return { html: "", toc: [] };
  return renderMarkdownWithToc(article.value.content);
});

const renderedContent = computed(() => markdownResult.value.html);

const tocItems = computed(() =>
  markdownResult.value.toc.filter((item) => item.level >= 2 && item.level <= 4)
);

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
      if (typeof res.liked === "boolean") {
        article.value.isLiked = res.liked;
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
  if (!userStore.token) {
    ElMessage.warning("请先登录后收藏");
    return;
  }

  try {
    const res = await articleApi.favorite(articleId.value);

    // 更新 UI 计数和状态
    if (article.value && res) {
      if (typeof res.favorites === "number") {
        article.value.favorites = res.favorites;
      }
      if (typeof res.favorited === "boolean") {
        article.value.isFavorited = res.favorited;
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

const updateHeadingPositions = async () => {
  await nextTick();
  if (!contentRef.value) {
    headingPositions.value = [];
    return;
  }
  const headings = Array.from(
    contentRef.value.querySelectorAll("h2, h3, h4")
  ) as HTMLElement[];
  headingPositions.value = headings.map((heading) => ({
    id: heading.id,
    top: heading.getBoundingClientRect().top + window.scrollY,
  }));
};

const updateActiveHeading = () => {
  if (!headingPositions.value.length) {
    activeHeadingId.value = "";
    return;
  }
  const offset = 96;
  const currentTop = window.scrollY + offset;
  let currentId = headingPositions.value[0].id;
  for (const heading of headingPositions.value) {
    if (heading.top <= currentTop) {
      currentId = heading.id;
    } else {
      break;
    }
  }
  activeHeadingId.value = currentId;
};

const updateReadingProgress = () => {
  if (!contentRef.value) return;
  const top = contentRef.value.getBoundingClientRect().top + window.scrollY;
  const height = contentRef.value.scrollHeight;
  const viewport = window.innerHeight;
  const maxScroll = height - viewport;
  if (maxScroll <= 0) {
    readingProgress.value = window.scrollY >= top ? 100 : 0;
    return;
  }
  const progress = ((window.scrollY - top) / maxScroll) * 100;
  readingProgress.value = Math.min(100, Math.max(0, progress));
};

const handleScroll = () => {
  updateReadingProgress();
  updateActiveHeading();
};

const handleResize = () => {
  updateHeadingPositions();
  handleScroll();
};

const scrollToHeading = (id: string) => {
  const target = document.getElementById(id);
  if (!target) return;
  target.scrollIntoView({ behavior: "smooth", block: "start" });
};

const tocIndentClass = (level: number) => {
  if (level >= 4) return "toc-indent-2";
  if (level === 3) return "toc-indent-1";
  return "";
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
  window.addEventListener("scroll", handleScroll, { passive: true });
  window.addEventListener("resize", handleResize);
});

// 监听路由参数变化，重新加载数据
watch(articleId, () => {
  fetchArticle().then(() => {
    handleIncreaseViews();
  });
  // 推荐文章也刷新一下
  fetchRecommendedArticles();
  window.scrollTo({ top: 0, behavior: "smooth" });
});

watch(renderedContent, async () => {
  await updateHeadingPositions();
  handleScroll();
});

// 同步目录数据到 toc store
watch(tocItems, (items) => {
  tocStore.setTocItems(items);
}, { immediate: true });

// 同步当前激活标题到 toc store
watch(activeHeadingId, (id) => {
  tocStore.setActiveHeadingId(id);
});

onUnmounted(() => {
  window.removeEventListener("scroll", handleScroll);
  window.removeEventListener("resize", handleResize);
  tocStore.clear();
});
</script>

<style scoped>
.article-header {
  background: transparent;
}

.reading-progress {
  position: fixed;
  top: 64px;
  left: 0;
  right: 0;
  height: 3px;
  z-index: 40;
  pointer-events: none;
  background: transparent;
}

.reading-progress__bar {
  height: 100%;
  background: linear-gradient(90deg, var(--color-primary-500), var(--color-primary-700));
  transition: width 120ms linear;
}

.toc-card {
  padding: 16px 14px;
  overflow: hidden;
}

.toc-title {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 12px;
}

.toc-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
  max-height: calc(100vh - 200px);
  overflow: auto;
  padding-right: 4px;
}

.toc-item {
  text-align: left;
  font-size: 0.875rem;
  line-height: 1.4;
  color: var(--color-text-secondary);
  padding: 6px 8px;
  border-radius: 8px;
  transition: color 0.2s ease, background 0.2s ease;
}

.toc-item:hover {
  color: var(--color-text-primary);
  background: var(--color-bg-tertiary);
}

.toc-item.is-active {
  color: var(--color-primary-600);
  background: var(--color-primary-50);
}

.toc-indent-1 {
  padding-left: 20px;
}

.toc-indent-2 {
  padding-left: 32px;
}
</style>
