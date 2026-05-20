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
    <div class="px-4 mx-auto max-w-[1480px] sm:px-6 lg:px-8">
      <div class="flex flex-col gap-6 xl:grid xl:grid-cols-[minmax(0,900px)_260px] xl:items-start xl:justify-center">

        <!-- 左侧：文章主体 -->
        <div class="min-w-0 xl:w-full xl:max-w-[900px]">
          <div
            v-if="article"
            class="paper-card overflow-hidden"
          >
            <!-- 文章头部 -->
            <div class="p-5 sm:p-8 border-b article-header">
              <div class="mb-6">
                <button
                  @click="goBackToList"
                  class="group inline-flex items-center gap-1.5 px-3 py-1.5 text-sm font-medium text-slate-600 bg-slate-100/80 rounded-full transition-all hover:bg-slate-200 hover:text-slate-900 dark:text-slate-300 dark:bg-slate-800/80 dark:hover:bg-slate-700 dark:hover:text-slate-50 cursor-pointer"
                >
                  <svg class="w-4 h-4 transition-transform group-hover:-translate-x-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                  </svg>
                  返回列表
                </button>
              </div>

              <h1 class="mb-4 text-2xl sm:text-3xl md:text-4xl font-semibold text-slate-900 dark:text-slate-50">
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
                loading="lazy"
                decoding="async"
                class="object-cover w-full h-full"
              />
            </div>

            <!-- 文章内容 -->
            <div class="p-5 sm:p-8 article-content" @click="handleCopyClick">
              <div ref="contentRef" class="markdown-content" v-html="renderedContent"></div>
            </div>

            <!-- 文章底部 -->
            <div
              class="p-5 sm:p-8 border-t border-slate-200/70 bg-white/70 dark:border-slate-700/70 dark:bg-slate-900/60 article-footer"
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

          <!-- 上一篇 / 下一篇 -->
          <div v-if="adjacentArticles.length > 0" class="mt-12">
            <div
              class="grid grid-cols-1 gap-6"
              :class="adjacentArticles.length > 1 ? 'md:grid-cols-2' : 'md:grid-cols-1'"
            >

              <router-link
                v-for="item in adjacentArticles"
                :key="`${item.type}-${item.article.id}`"
                :to="{ name: 'ArticleDetail', params: { id: item.article.id } }"
                class="article-nav-card paper-card paper-card-hover"
              >
                <div class="article-nav-card__media">
                  <img
                    v-if="item.article.coverImage"
                    :src="item.article.coverImage"
                    :alt="item.article.title"
                    loading="lazy"
                    decoding="async"
                    class="article-nav-card__image"
                  />
                  <div v-else class="article-nav-card__fallback"></div>
                  <div class="article-nav-card__overlay"></div>
                </div>
                <div class="article-nav-card__content">
                  <div class="article-nav-card__default">
                    <span class="article-nav-card__eyebrow">{{ item.label }}</span>
                    <h2 class="article-nav-card__title">{{ item.article.title }}</h2>
                  </div>
                  <p class="article-nav-card__summary">
                    {{ item.article.summary || item.article.title }}
                  </p>
                </div>
              </router-link>
            </div>
          </div>
        </div>

        <!-- 右侧：目录（大屏幕显示） -->
        <aside
          v-if="article && tocItems.length"
          ref="tocAsideRef"
          class="hidden xl:block xl:w-[260px]"
        >
          <div class="toc-sidebar-shell" :style="tocSidebarStyle">
            <div class="toc-card paper-card">
              <div class="toc-title cursor-pointer flex items-center justify-between" @click="isTocExpanded = !isTocExpanded">
                <span>目录</span>
                <el-icon>
                  <ArrowDown v-if="isTocExpanded" />
                  <ArrowRight v-else />
                </el-icon>
              </div>
              <el-collapse-transition>
                <nav v-show="isTocExpanded" class="toc-list">
                  <div
                    v-for="item in tocStore.visibleTocItems"
                    :key="item.id"
                    class="toc-item flex items-center cursor-pointer"
                    :class="[
                      tocIndentClass(item.level),
                      activeHeadingId === item.id ? 'is-active' : ''
                    ]"
                    @click="handleTocItemClick(item)"
                  >
                    <!-- 折叠/展开图标 -->
                    <el-icon
                      v-if="tocStore.hasChildrenMap.get(item.id)"
                      class="mr-1 flex-shrink-0 hover:text-primary"
                      @click.stop="tocStore.toggleHeadingCollapse(item.id)"
                    >
                      <ArrowRight v-if="tocStore.collapsedHeadingIds.has(item.id)" />
                      <ArrowDown v-else />
                    </el-icon>
                    <span v-else class="w-[14px] mr-1 inline-block flex-shrink-0"></span>
                    
                    <span class="flex-1 truncate" :title="item.title">
                      {{ item.title }}
                    </span>
                  </div>
                </nav>
              </el-collapse-transition>
            </div>
          </div>
        </aside>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, shallowRef, computed, onMounted, onUnmounted, watch, nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";
import {
  User,
  Calendar,
  View,
  Star,
  Share,
  Pointer,
  ArrowDown,
  ArrowRight,
} from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
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
const router = useRouter();

// 返回列表：优先使用浏览器后退（保留分页/筛选状态和滚动位置）
const goBackToList = () => {
  // 检查是否有历史记录可回退（window.history.state.back 是 vue-router 注入的上一页路径）
  const back = window.history.state?.back as string | undefined
  if (back && (back.startsWith('/articles') || back === '/')) {
    router.back()
  } else {
    router.push('/articles')
  }
}

const articleId = computed(() => Number(route.params.id));
const article = shallowRef<Article | null>(null);
const prevArticle = shallowRef<Article | null>(null);
const nextArticle = shallowRef<Article | null>(null);
const loading = ref(false);
const contentRef = ref<HTMLElement | null>(null);
const tocAsideRef = ref<HTMLElement | null>(null);
const readingProgress = ref(0);
const activeHeadingId = ref("");
const headingPositions = ref<{ id: string; top: number }[]>([]);
const tocSidebarMetrics = ref<{ left: number; width: number } | null>(null);
const isTocExpanded = ref(true);



type ArticleNavItem = {
  type: "prev" | "next";
  label: string;
  article: Article;
};

// 默认作者
const defaultAuthor = "站长";

const markdownResult = computed(() => {
  if (!article.value) return { html: "", toc: [] };
  return renderMarkdownWithToc(article.value.content);
});

const renderedContent = computed(() => markdownResult.value.html);

const tocItems = computed(() =>
  markdownResult.value.toc.filter((item) => item.level >= 1 && item.level <= 4)
);



// 处理目录项点击事件
const handleTocItemClick = (item: any) => {
  scrollToHeading(item.id);
  if (tocStore.hasChildrenMap.get(item.id)) {
    tocStore.toggleHeadingCollapse(item.id);
  }
};

const adjacentArticles = computed<ArticleNavItem[]>(() => {
  const items: ArticleNavItem[] = [];

  if (prevArticle.value) {
    items.push({
      type: "prev",
      label: "上一篇",
      article: prevArticle.value,
    });
  }

  if (nextArticle.value) {
    items.push({
      type: "next",
      label: "下一篇",
      article: nextArticle.value,
    });
  }

  return items;
});

const tocSidebarStyle = computed(() => {
  if (!tocSidebarMetrics.value) {
    return {
      opacity: "0",
      pointerEvents: "none" as const,
    };
  }

  return {
    left: `${tocSidebarMetrics.value.left}px`,
    width: `${tocSidebarMetrics.value.width}px`,
  };
});

// 获取文章详情
const fetchArticle = async () => {
  loading.value = true;
  try {
    article.value = await articleApi.getById(articleId.value);
  } catch (e) {
    article.value = null;
    // 错误提示已由 axios 拦截器统一处理
    console.error("获取文章详情失败:", e);
  } finally {
    loading.value = false;
  }
};

// 获取上一篇和下一篇文章
const fetchAdjacentArticles = async () => {
  try {
    const res = await articleApi.getList({ page: 1, pageSize: 1000 });
    const articles = res.list || [];
    const currentIndex = articles.findIndex((item) => item.id === articleId.value);

    if (currentIndex === -1) {
      prevArticle.value = null;
      nextArticle.value = null;
      return;
    }

    prevArticle.value = currentIndex < articles.length - 1 ? articles[currentIndex + 1] : null;
    nextArticle.value = currentIndex > 0 ? articles[currentIndex - 1] : null;
  } catch (e) {
    prevArticle.value = null;
    nextArticle.value = null;
    console.error("获取上一篇和下一篇文章失败:", e);
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
    // 错误提示已由 axios 拦截器统一处理
    console.error("点赞失败:", e);
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
    // 错误提示已由 axios 拦截器统一处理
    console.error("收藏失败:", e);
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

const updateTocSidebarMetrics = async () => {
  await nextTick();

  if (
    !tocAsideRef.value ||
    window.innerWidth < 1280 ||
    !article.value ||
    !tocItems.value.length
  ) {
    tocSidebarMetrics.value = null;
    return;
  }

  const { left, width } = tocAsideRef.value.getBoundingClientRect();

  if (width <= 0) {
    tocSidebarMetrics.value = null;
    return;
  }

  tocSidebarMetrics.value = { left, width };
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

// rAF 节流：确保 scroll 回调最多每帧执行一次，避免掉帧
let scrollRafId = 0
const handleScroll = () => {
  if (scrollRafId) return
  scrollRafId = requestAnimationFrame(() => {
    scrollRafId = 0
    updateReadingProgress()
    updateActiveHeading()
  })
}

// resize 防抖：窗口尺寸变化后 150ms 再执行计算
let resizeTimer = 0
const handleResize = () => {
  clearTimeout(resizeTimer)
  resizeTimer = window.setTimeout(() => {
    void updateHeadingPositions()
    void updateTocSidebarMetrics()
    updateReadingProgress()
    updateActiveHeading()
  }, 150)
}

const scrollToHeading = (id: string) => {
  const target = document.getElementById(id);
  if (!target) return;

  const top = target.getBoundingClientRect().top + window.scrollY - 96;
  window.scrollTo({ top: Math.max(0, top), behavior: "smooth" });
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

const loadArticlePage = async () => {
  await Promise.all([fetchArticle(), fetchAdjacentArticles()]);

  if (article.value) {
    // 文章加载成功后再增加浏览量，确保 article.value 存在，且体验更好
    handleIncreaseViews();
  }
};

// 处理代码块复制
const handleCopyClick = async (e: MouseEvent) => {
  const target = e.target as HTMLElement;
  const btn = target.closest('.code-block-copy');
  if (!btn) return;
  
  const wrapper = btn.closest('.code-block-wrapper');
  if (!wrapper) return;
  
  const codeEl = wrapper.querySelector('code');
  if (!codeEl) return;
  
  try {
    await navigator.clipboard.writeText(codeEl.textContent || '');
    const copyIcon = btn.querySelector('.copy-icon') as HTMLElement;
    const successIcon = btn.querySelector('.success-icon') as HTMLElement;
    if (copyIcon && successIcon) {
      copyIcon.style.display = 'none';
      successIcon.style.display = 'block';
      setTimeout(() => {
        copyIcon.style.display = 'block';
        successIcon.style.display = 'none';
      }, 2000);
    }
    ElMessage.success("代码已复制到剪贴板");
  } catch (err) {
    console.error("复制失败:", err);
    ElMessage.error("复制失败，请手动选择复制");
  }
};

onMounted(() => {
  loadArticlePage();
  window.scrollTo({ top: 0, behavior: "smooth" });
  window.addEventListener("scroll", handleScroll, { passive: true });
  window.addEventListener("resize", handleResize);
});

// 监听路由参数变化，重新加载数据
watch(articleId, () => {
  loadArticlePage();
  window.scrollTo({ top: 0, behavior: "smooth" });
});

watch(renderedContent, async () => {
  await Promise.all([updateHeadingPositions(), updateTocSidebarMetrics()]);
  handleScroll();
});

// 同步目录数据到 toc store
watch(tocItems, (items) => {
  tocStore.setTocItems(items);
  void updateTocSidebarMetrics();
}, { immediate: true });

// 同步当前激活标题到 toc store
watch(activeHeadingId, (id) => {
  tocStore.setActiveHeadingId(id);
  
  if (id) {
    nextTick(() => {
      // 找到所有的 active 元素（可能有多个 toc 列表，如移动端和 PC 端）
      const activeEls = document.querySelectorAll('.toc-list .is-active');
      activeEls.forEach((el) => {
        el.scrollIntoView({ block: 'nearest', behavior: 'smooth' });
      });
    });
  }
});

onUnmounted(() => {
  window.removeEventListener("scroll", handleScroll);
  window.removeEventListener("resize", handleResize);
  // 清理未执行的 rAF / timer
  if (scrollRafId) cancelAnimationFrame(scrollRafId)
  if (resizeTimer) clearTimeout(resizeTimer)
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

.toc-sidebar-shell {
  position: fixed;
  top: 96px;
  z-index: 30;
  transition: opacity 0.2s ease;
}


.toc-title {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 8px;
  user-select: none;
}
.toc-title:hover {
  color: var(--color-primary);
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

.article-nav-card {
  position: relative;
  display: block;
  min-height: 200px;
  overflow: hidden;
  isolation: isolate;
}

.article-nav-card__media {
  position: absolute;
  inset: 0;
}

.article-nav-card__image,
.article-nav-card__fallback {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 400ms ease;
}

.article-nav-card__fallback {
  background:
    linear-gradient(135deg, rgba(59, 130, 246, 0.85), rgba(168, 85, 247, 0.8)),
    linear-gradient(45deg, rgba(15, 23, 42, 0.12), rgba(255, 255, 255, 0.08));
}

.article-nav-card__overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg, rgba(15, 23, 42, 0.68), rgba(15, 23, 42, 0.3));
}

.article-nav-card__content {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: flex-end;
  min-height: 200px;
  padding: 24px;
  color: #fff;
}

.article-nav-card__default,
.article-nav-card__summary {
  max-width: min(100%, 420px);
  transition: opacity 260ms ease, transform 260ms ease;
}

.article-nav-card__default {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.article-nav-card__eyebrow {
  font-size: 0.875rem;
  font-weight: 500;
  opacity: 0.9;
}

.article-nav-card__title {
  margin: 0;
  font-size: 1.625rem;
  font-weight: 600;
  line-height: 1.35;
  color: inherit;
  text-wrap: balance;
}

.article-nav-card__summary {
  position: absolute;
  right: 24px;
  bottom: 24px;
  left: 24px;
  margin: 0;
  font-size: 0.95rem;
  line-height: 1.75;
  opacity: 0;
  transform: translateY(12px);
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-nav-card:hover .article-nav-card__image,
.article-nav-card:hover .article-nav-card__fallback {
  transform: scale(1.08);
}

.article-nav-card:hover .article-nav-card__default {
  opacity: 0;
  transform: translateY(-10px);
}

.article-nav-card:hover .article-nav-card__summary {
  opacity: 1;
  transform: translateY(0);
}

@media (max-width: 767px) {
  .article-nav-card,
  .article-nav-card__content {
    min-height: 180px;
  }

  .article-nav-card__content {
    padding: 20px;
  }

  .article-nav-card__title {
    font-size: 1.375rem;
  }

  .article-nav-card__summary {
    right: 20px;
    bottom: 20px;
    left: 20px;
    -webkit-line-clamp: 4;
  }
}
</style>
