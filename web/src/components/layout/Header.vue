<template>
  <header
    class="fixed top-0 z-50 w-full border-b border-slate-200/70 bg-white/75 backdrop-blur-md shadow-[0_10px_30px_-25px_rgba(15,23,42,0.45)] dark:border-slate-800/70 dark:bg-slate-900/70"
  >
    <nav class="px-4 mx-auto max-w-7xl sm:px-6 lg:px-8">
      <div class="flex justify-between items-center h-16">
        <!-- Logo - 左侧 -->
        <div class="flex items-center shrink-0 space-x-2">
          <router-link
            to="/"
            class="flex justify-center items-center w-8 h-8 rounded-lg transition-opacity duration-300 hover:opacity-70"
          >
            <img
              :src="logoImage"
              alt="syne-blog logo"
              class="w-full h-full rounded-md"
            />
          </router-link>
          <router-link
            to="/"
            class="text-xl font-semibold text-slate-900 transition-colors hover:text-primary-600 dark:text-slate-50"
          >
            syne-blog
          </router-link>
        </div>

        <!-- 导航菜单 - 中间 -->
        <div class="hidden items-center space-x-8 md:flex">
          <router-link
            v-for="item in menuItems"
            :key="item.path"
            :to="item.path"
            class="flex gap-2 items-center px-3 py-2 text-sm font-medium text-slate-600 rounded-md transition-colors hover:text-primary-600 dark:text-slate-300"
            exact-active-class="text-primary-600 bg-primary-50 dark:bg-primary-900/20"
          >
            <el-icon v-if="item.icon" :size="18">
              <component :is="item.icon" />
            </el-icon>
            {{ item.name }}
          </router-link>
        </div>

        <!-- 右侧工具栏 -->
        <div class="flex items-center space-x-3">
          <!-- GitHub 链接 -->
          <el-tooltip content="访问我的 GitHub" placement="bottom">
            <a
              :href="siteStore.authorInfo?.github || '#'"
              target="_blank"
              rel="noopener noreferrer"
              class="hidden justify-center items-center w-10 h-10 rounded-full transition-colors github-link hover:bg-slate-100/70 dark:hover:bg-slate-800/60 md:flex"
            >
              <svg
                class="w-6 h-6 text-slate-600 hover:text-slate-900 dark:hover:text-white"
                fill="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  fill-rule="evenodd"
                  d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.531 1.032 1.531 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z"
                  clip-rule="evenodd"
                />
              </svg>
            </a>
          </el-tooltip>

          <!-- 搜索 -->
          <el-tooltip content="搜索" placement="bottom">
            <button
              class="hidden justify-center items-center w-10 h-10 rounded-full transition-colors github-link hover:bg-slate-100/70 dark:hover:bg-slate-800/60 md:flex"
              @click="handleSearch"
            >
              <el-icon class="text-xl text-slate-600 hover:text-slate-900 dark:hover:text-white">
                <Search />
              </el-icon>
            </button>
          </el-tooltip>

          <!-- 登录/用户信息 -->
          <div v-if="!userStore.isLoggedIn" class="hidden md:block">
            <el-button type="primary" round @click="router.push('/login')">
              登录
            </el-button>
          </div>
          <el-dropdown
            v-else
            trigger="click"
            @command="handleUserCommand"
          >
            <div
              class="flex gap-2 items-center pr-2 rounded-full transition-colors cursor-pointer hover:bg-slate-100/70 dark:hover:bg-slate-800/60"
            >
              <el-avatar
                :size="32"
                :src="
                  userStore.currentUser?.avatar ||
                  'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
                "
              />
              <span
                class="hidden text-sm font-medium text-slate-600 dark:text-slate-300 lg:block"
              >
                {{ userStore.currentUser?.username || "用户" }}
              </span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item divided command="logout" class="text-red-500">
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>

          <!-- 移动端菜单按钮 -->
          <el-button
            class="md:hidden"
            circle
            @click="mobileMenuOpen = !mobileMenuOpen"
          >
            <el-icon>
              <Menu />
            </el-icon>
          </el-button>
        </div>
      </div>

      <!-- 移动端菜单 -->
      <div v-if="mobileMenuOpen" class="py-4 border-t md:hidden">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          class="flex gap-2 items-center px-3 py-2 text-base font-medium text-slate-700 rounded-md hover:text-primary-600 hover:bg-primary-50 dark:text-slate-300 dark:hover:bg-primary-900/20"
          exact-active-class="text-primary-600 bg-primary-50"
          @click="mobileMenuOpen = false"
        >
          <el-icon v-if="item.icon" :size="20">
            <component :is="item.icon" />
          </el-icon>
          {{ item.name }}
        </router-link>

        <!-- 移动端 GitHub 链接 -->
        <a
          :href="siteStore.authorInfo?.github || '#'"
          target="_blank"
          rel="noopener noreferrer"
          class="flex gap-2 items-center px-3 py-2 text-base font-medium text-slate-700 rounded-md transition-colors hover:text-primary-600 hover:bg-primary-50 dark:text-slate-300 dark:hover:bg-primary-900/20"
          @click="mobileMenuOpen = false"
        >
          <el-icon :size="20">
            <svg fill="currentColor" viewBox="0 0 24 24">
              <path
                fill-rule="evenodd"
                d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.531 1.032 1.531 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z"
                clip-rule="evenodd"
              />
            </svg>
          </el-icon>
          GitHub
        </a>

        <!-- 移动端工具栏 (仅保留搜索) -->
        <div class="px-3 pt-4 mt-4 border-t border-slate-200/70 dark:border-slate-800/70">
          <button
            class="flex gap-2 w-full justify-center items-center p-3 bg-white/70 rounded-xl transition-all hover:bg-white active:scale-95 dark:bg-slate-900/60 dark:hover:bg-slate-800"
            @click="
              handleSearch();
              mobileMenuOpen = false;
            "
          >
            <el-icon :size="18" class="text-slate-700 dark:text-slate-300">
              <Search />
            </el-icon>
            <span class="text-sm font-medium text-slate-700 dark:text-slate-200">
              搜索
            </span>
          </button>
        </div>

        <!-- 移动端登录按钮 -->
        <div v-if="!userStore.isLoggedIn" class="px-3 mt-4">
          <el-button
            type="primary"
            class="w-full !h-11 !rounded-xl !text-base"
            @click="
              router.push('/login');
              mobileMenuOpen = false;
            "
          >
            <el-icon class="mr-2"><User /></el-icon>
            登录 / 注册
          </el-button>
        </div>
      </div>
    </nav>
  </header>

  <!-- 搜索弹窗 -->
  <el-dialog
    v-model="searchVisible"
    title="搜索"
    class="search-dialog"
    top="10vh"
    destroy-on-close
    @opened="focusSearchInput"
  >
    <div class="search-panel">
      <el-input
        ref="searchInputRef"
        v-model="searchKeyword"
        placeholder="搜索文章"
        size="large"
        clearable
        class="search-panel__input"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <div class="search-panel__divider"></div>

      <div
        class="search-panel__body"
        :class="{ 'search-panel__body--compact': !trimmedSearchKeyword }"
      >
        <div
          v-if="searchLoading && trimmedSearchKeyword"
          class="search-panel__state search-panel__state--loading"
        >
          <el-icon class="is-loading search-panel__loading-icon"><Loading /></el-icon>
          正在准备本地搜索数据...
        </div>

        <div
          v-else-if="!trimmedSearchKeyword"
          class="search-panel__state search-panel__state--compact"
        >
          输入关键词开始搜索
        </div>

        <div 
          v-else-if="filteredSearchResults.length" 
          class="search-result-list"
          v-bind="containerProps"
        >
          <div v-bind="wrapperProps">
            <button
              v-for="{ data: item, index } in virtualList"
              :key="item.article.id"
              type="button"
              class="search-result-item"
              @click="openArticle(item.article.id)"
            >
              <span class="search-result-item__index">{{ index + 1 }}.</span>
              <div class="search-result-item__content">
                <h3 class="search-result-item__title" v-html="item.titleHtml"></h3>
                <p class="search-result-item__summary" v-html="item.previewHtml"></p>
                <div class="search-result-item__meta">
                  <span>{{ item.article.categoryName }}</span>
                  <span>{{ formatDate(item.article.publishedTime) }}</span>
                  <span>{{ item.article.views }} 阅读</span>
                </div>
              </div>
            </button>
          </div>
        </div>

        <el-empty
          v-else
          description="没有找到相关文章"
          :image-size="56"
        />
      </div>

      <div v-if="trimmedSearchKeyword && !searchLoading" class="search-panel__footer">
        <span class="search-panel__count">共搜索到 {{ searchTotal }} 篇文章</span>
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, nextTick, ref, watch } from "vue";
import { useRouter } from "vue-router";
import { useVirtualList } from "@vueuse/core";
import { useAppStore } from "@/stores/app";
import { useUserStore } from "@/stores/user";
import { useSiteStore } from "@/stores/site";
import { articleApi } from "@/api/article";
import { formatDate } from "@/utils/format";
import { ElMessage } from "element-plus";
import {
  Menu,
  Search,
  House,
  Document,
  Compass,
  User,
  Sunny,
  Moon,
  Brush,
  Loading,
} from "@element-plus/icons-vue";
import type { ArticleSearchItem, MenuItem } from "@/types";

import logoImage from "@/assets/images/common/logo.png";

const appStore = useAppStore();
const userStore = useUserStore();
const siteStore = useSiteStore();
const router = useRouter();

const mobileMenuOpen = ref(false);
const searchVisible = ref(false);
const searchKeyword = ref("");
const searchLoading = ref(false);
const searchInputRef = ref<{ focus?: () => void } | null>(null);
const cachedArticles = ref<(ArticleSearchItem & { contentText: string })[]>([]);
const hasLoadedSearchArticles = ref(false);
const trimmedSearchKeyword = computed(() => searchKeyword.value.trim());


const menuItems: MenuItem[] = [
  { name: "首页", path: "/", icon: House },
  { name: "文章", path: "/articles", icon: Document },
  { name: "网站导航", path: "/navigation", icon: Compass },
  { name: "关于", path: "/about", icon: User },
];

type SearchableArticle = ArticleSearchItem & {
  contentText: string;
};

type SearchResultItem = {
  article: SearchableArticle;
  titleHtml: string;
  previewHtml: string;
  score: number;
};

const escapeHtml = (value: string) =>

  value
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/\"/g, "&quot;")
    .replace(/'/g, "&#39;");

const escapeRegExp = (value: string) => value.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");

const normalizeSearchText = (value: string) =>
  value
    .replace(/```/g, " ")
    .replace(/`/g, "")
    .replace(/!\[[^\]]*\]\([^)]*\)/g, " ")
    .replace(/\[([^\]]+)\]\([^)]*\)/g, "$1")
    .replace(/<[^>]+>/g, " ")
    .replace(/[#>*_~\-]/g, " ")
    .replace(/\r?\n+/g, " ")
    .replace(/\s+/g, " ")
    .trim();

const findMatchIndex = (text: string, keyword: string) =>
  text.toLocaleLowerCase().indexOf(keyword.toLocaleLowerCase());


const highlightText = (text: string, keyword: string) => {
  if (!text) return "";
  if (!keyword) return escapeHtml(text);

  const regex = new RegExp(escapeRegExp(keyword), "ig");
  let lastIndex = 0;
  let result = "";

  for (const match of text.matchAll(regex)) {
    const matchText = match[0];
    const matchIndex = match.index ?? 0;
    result += escapeHtml(text.slice(lastIndex, matchIndex));
    result += `<mark class="search-highlight">${escapeHtml(matchText)}</mark>`;
    lastIndex = matchIndex + matchText.length;
  }

  result += escapeHtml(text.slice(lastIndex));
  return result;
};

const buildExcerptHtml = (text: string, keyword: string, radius: number = 26) => {
  if (!text) return "";

  const matchIndex = findMatchIndex(text, keyword);
  if (matchIndex === -1) {
    const shortText = text.length > 88 ? `${text.slice(0, 88)}...` : text;
    return escapeHtml(shortText);
  }

  const start = Math.max(0, matchIndex - radius);
  const end = Math.min(text.length, matchIndex + keyword.length + radius * 2);
  const prefix = start > 0 ? "..." : "";
  const suffix = end < text.length ? "..." : "";
  return `${prefix}${highlightText(text.slice(start, end), keyword)}${suffix}`;
};

const buildPreviewHtml = (article: SearchableArticle, keyword: string) => {
  const summary = article.summary || "";
  const category = article.categoryName || "";
  const tagsText = article.tags?.map((tag) => tag.name).join(" / ") || "";
  const contentText = article.contentText || "";

  if (summary && findMatchIndex(summary, keyword) !== -1) {
    return buildExcerptHtml(summary, keyword, 24);
  }

  if (category && findMatchIndex(category, keyword) !== -1) {
    return `分类：${highlightText(category, keyword)}`;
  }

  if (tagsText && findMatchIndex(tagsText, keyword) !== -1) {
    return `标签：${highlightText(tagsText, keyword)}`;
  }

  if (contentText && findMatchIndex(contentText, keyword) !== -1) {
    return buildExcerptHtml(contentText, keyword, 34);
  }

  return escapeHtml(summary || contentText.slice(0, 88) || article.title);
};

const getSearchScore = (article: SearchableArticle, keyword: string) => {
  const titleIndex = findMatchIndex(article.title || "", keyword);
  if (titleIndex !== -1) return titleIndex;

  const summaryIndex = findMatchIndex(article.summary || "", keyword);
  if (summaryIndex !== -1) return 1000 + summaryIndex;

  const categoryIndex = findMatchIndex(article.categoryName || "", keyword);
  if (categoryIndex !== -1) return 2000 + categoryIndex;

  const tagsText = article.tags?.map((tag) => tag.name).join(" ") || "";
  const tagIndex = findMatchIndex(tagsText, keyword);
  if (tagIndex !== -1) return 3000 + tagIndex;

  const contentIndex = findMatchIndex(article.contentText || "", keyword);
  if (contentIndex !== -1) return 4000 + contentIndex;

  return Number.POSITIVE_INFINITY;
};


const filteredSearchResults = computed<SearchResultItem[]>(() => {
  const keyword = trimmedSearchKeyword.value;
  if (!keyword) {
    return [];
  }

  return cachedArticles.value
    .map((article) => ({
      article,
      score: getSearchScore(article, keyword),
      titleHtml: highlightText(article.title || "", keyword),
      previewHtml: buildPreviewHtml(article, keyword),
    }))
    .filter((item) => Number.isFinite(item.score))
    .sort((a, b) => {
      if (a.score !== b.score) {
        return a.score - b.score;
      }
      return `${b.article.publishedTime || ""}`.localeCompare(`${a.article.publishedTime || ""}`);
    });
});

const { list: virtualList, containerProps, wrapperProps } = useVirtualList(
  filteredSearchResults,
  {
    itemHeight: 90, // 预估每个搜索结果卡片高度 + gap
    overscan: 5,
  }
);

const searchTotal = computed(() => filteredSearchResults.value.length);

const ensureSearchArticlesLoaded = async () => {
  if (hasLoadedSearchArticles.value || searchLoading.value) {
    return;
  }

  searchLoading.value = true;
  try {
    const res = await articleApi.getSearchIndex();
    cachedArticles.value = (res || []).map((article) => ({
      ...article,
      contentText: normalizeSearchText(article.content || ""),
    }));
    hasLoadedSearchArticles.value = true;
  } catch (error) {
    // 错误提示已由 axios 拦截器统一处理
    console.error("加载搜索文章失败:", error);
  } finally {
    searchLoading.value = false;
  }
};


const handleSearch = () => {
  searchVisible.value = true;
};

const openArticle = (id: number) => {
  searchVisible.value = false;
  router.push(`/article/${id}`);
};

const focusSearchInput = () => {
  searchInputRef.value?.focus?.();
};

watch(searchVisible, async (visible) => {
  if (visible) {
    await nextTick();
    void ensureSearchArticlesLoaded();
    return;
  }

  searchKeyword.value = "";
});

const handleUserCommand = (command: string) => {
  if (command === "profile") {
    router.push("/profile");
  } else if (command === "logout") {
    handleLogout();
  }
};

const handleLogout = () => {
  userStore.logout();
  ElMessage.success("已退出登录");
  router.push("/");
};
</script>

<style scoped>
.nav-link {
  position: relative;
}

.nav-link::after {
  content: "";
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 2px;
  background: var(--color-primary-600);
  transition: all 0.3s ease;
  transform: translateX(-50%);
}

.nav-link:hover::after,
.nav-link.router-link-exact-active::after {
  width: 80%;
}

.github-link {
  transition: all 0.3s ease;
}

.github-link:hover {
  transform: scale(1.1);
}

.theme-color-toggle {
  transition: all 0.3s ease;
}

.theme-color-toggle:hover {
  transform: rotate(15deg);
}

.search-panel {
  display: flex;
  flex-direction: column;
}

.search-panel__input :deep(.el-input__wrapper) {
  min-height: 48px;
  border-radius: 9999px;
  box-shadow: 0 0 0 1px rgba(59, 130, 246, 0.28) inset;
}

.search-panel__input :deep(.el-input__wrapper.is-focus) {
  box-shadow:
    0 0 0 1px rgba(59, 130, 246, 0.48) inset,
    0 0 0 4px rgba(96, 165, 250, 0.14);
}

.search-panel__divider {
  margin: 12px 0 14px;
  border-top: 1px dashed rgba(96, 165, 250, 0.5);
}

.search-panel__body {
  padding: 2px 6px 0 0;
}

.search-panel__body--compact {
  overflow: hidden;
}

.search-panel__state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 180px;
  color: rgb(100 116 139);
  font-size: 0.95rem;
}

.search-panel__state--compact {
  justify-content: flex-start;
  min-height: 32px;
  padding: 2px 2px 4px;
  color: rgb(148 163 184);
  font-size: 0.875rem;
}

.search-panel__state--loading {
  gap: 10px;
  min-height: 120px;
}

.search-panel__loading-icon {
  font-size: 1rem;
}

.search-result-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 2px 4px;
  max-height: min(60vh, 600px);
  overflow-y: auto;
}

.search-result-list::-webkit-scrollbar {
  width: 6px;
}

.search-result-list::-webkit-scrollbar-thumb {
  background: rgba(148, 163, 184, 0.3);
  border-radius: 4px;
}

.search-result-list::-webkit-scrollbar-track {
  background: transparent;
}

.search-result-item {
  position: relative;
  width: 100%;
  display: grid;
  grid-template-columns: 24px minmax(0, 1fr);
  gap: 10px;
  align-items: start;
  padding: 10px 12px;
  text-align: left;
  border-radius: 10px;
  border: 1px solid transparent;
  border-bottom: 1px solid rgba(226, 232, 240, 0.5);
  background: transparent;
  transition: all 0.2s ease;
  cursor: pointer;
}

.search-result-item:last-child {
  border-bottom-color: transparent;
}

.search-result-item:hover {
  border-color: rgba(219, 234, 254, 0.8);
  background: rgba(239, 246, 255, 0.6);
  box-shadow: 0 4px 12px -6px rgba(59, 130, 246, 0.15);
  transform: translateX(4px);
  z-index: 1;
}

.search-result-item__index {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  margin-top: 2px;
  font-size: 0.8rem;
  font-weight: 600;
  color: rgb(148, 163, 184);
  background: transparent;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.search-result-item:hover .search-result-item__index {
  color: rgb(59, 130, 246);
  background: rgba(59, 130, 246, 0.1);
}

.search-result-item__content {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.search-result-item__title {
  margin: 0;
  font-size: 0.95rem;
  font-weight: 600;
  line-height: 1.35;
  color: rgb(30, 41, 59);
  transition: color 0.2s ease;
}

.search-result-item:hover .search-result-item__title {
  color: rgb(37, 99, 235);
}

.search-result-item__summary {
  margin: 0;
  color: rgb(100, 116, 139);
  font-size: 0.8rem;
  line-height: 1.5;
  display: -webkit-box;
  overflow: hidden;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.search-result-item__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 2px;
}

.search-result-item__meta span {
  display: inline-flex;
  align-items: center;
  padding: 1px 6px;
  font-size: 0.7rem;
  font-weight: 500;
  color: rgb(100, 116, 139);
  background: rgba(241, 245, 249, 0.6);
  border-radius: 4px;
  border: 1px solid rgba(226, 232, 240, 0.6);
}

.search-panel__footer {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px dashed rgba(96, 165, 250, 0.5);
}

.search-panel__count {
  display: block;
  font-size: 0.86rem;
  color: rgb(71 85 105);
}


:deep(.search-highlight) {
  padding: 0 0.18em;
  border-radius: 0.3em;
  background: rgba(250, 204, 21, 0.28);
  color: inherit;
}


.dark .search-panel__state {
  color: rgb(148 163 184);
}

.dark .search-result-item {
  border-bottom-color: rgba(51, 65, 85, 0.4);
}

.dark .search-result-item:hover {
  border-color: rgba(59, 130, 246, 0.3);
  background: rgba(30, 41, 59, 0.5);
  box-shadow: 0 4px 12px -6px rgba(0, 0, 0, 0.4);
}

.dark .search-result-item:hover .search-result-item__index {
  color: rgb(96, 165, 250);
  background: rgba(96, 165, 250, 0.15);
}

.dark .search-result-item__title {
  color: rgb(226, 232, 240);
}

.dark .search-result-item:hover .search-result-item__title {
  color: rgb(96, 165, 250);
}

.dark .search-result-item__summary {
  color: rgb(148, 163, 184);
}

.dark .search-result-item__meta span {
  background: rgba(15, 23, 42, 0.4);
  border-color: rgba(51, 65, 85, 0.6);
  color: rgb(148, 163, 184);
}

.dark .search-panel__count,
.dark .search-panel__state--compact {
  color: rgb(148 163 184);
}

.dark :deep(.search-highlight) {
  background: rgba(250, 204, 21, 0.22);
}

@media (max-width: 768px) {
  .search-panel__body {
    padding-right: 2px;
  }

  .search-result-list {
    max-height: 50vh;
    gap: 4px;
  }

  .search-result-item {
    grid-template-columns: 20px minmax(0, 1fr);
    gap: 8px;
    padding: 8px 10px;
  }

  .search-result-item__index {
    width: 20px;
    height: 20px;
    font-size: 0.75rem;
  }

  .search-result-item__title {
    font-size: 0.9rem;
  }

  .search-result-item__summary {
    font-size: 0.75rem;
    line-height: 1.45;
  }
  
  .search-result-item__meta span {
    font-size: 0.65rem;
    padding: 1px 4px;
  }
}

</style>

<style>
.search-dialog {
  width: min(94vw, 820px) !important;
  border-radius: 20px !important;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.96) !important;
  backdrop-filter: blur(18px);
}

.search-dialog .el-dialog__header {
  margin-right: 0;
  padding: 18px 24px 8px;
}

.search-dialog .el-dialog__title {
  font-size: 1.35rem;
  font-weight: 600;
  color: rgb(37 99 235);
}

.search-dialog .el-dialog__headerbtn {
  top: 20px;
  right: 20px;
}

.search-dialog .el-dialog__body {
  padding: 8px 24px 20px !important;
}


.dark .search-dialog {
  background: rgba(15, 23, 42, 0.94) !important;
}

.dark .search-dialog .el-dialog__title,
.dark .search-dialog .el-dialog__headerbtn .el-dialog__close {
  color: rgb(226 232 240);
}

@media (max-width: 768px) {
  .search-dialog {
    width: 94% !important;
    margin-top: 8vh !important;
    border-radius: 18px !important;
  }

  .search-dialog .el-dialog__header {
    padding: 16px 16px 6px;
  }

  .search-dialog .el-dialog__body {
    padding: 8px 16px 16px !important;
  }
}
</style>
