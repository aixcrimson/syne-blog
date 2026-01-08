<template>
  <aside class="w-full sidebar">
    <div class="flex sticky top-20 flex-col gap-6">
      <!-- 公告栏 -->
      <div
        class="p-5 bg-white border-b transition-all duration-300 md:bg-gradient-to-br md:to-white md:rounded-xl md:border md:shadow-sm md:from-primary-50 md:border-primary-100 dark:bg-gray-800 dark:border-gray-700 md:dark:from-gray-800 md:dark:to-gray-800 md:hover:-translate-y-0.5 md:hover:shadow-md"
      >
        <div class="flex gap-2 items-center mb-3">
          <div class="w-2 h-2 rounded-full animate-pulse bg-primary-500"></div>
          <p class="text-lg font-bold text-primary-600">公告栏</p>
        </div>
        <div v-if="notices.length > 0" class="space-y-2">
          <p
            v-for="notice in notices"
            :key="notice.id"
            class="text-sm leading-relaxed text-gray-700 dark:text-gray-300"
          >
            📢 {{ notice.content }}
          </p>
        </div>
        <p v-else class="text-sm leading-relaxed text-gray-500">暂无公告</p>
      </div>

      <!-- 标签页切换卡片 -->
      <div
        class="overflow-hidden tab-card md:rounded-xl md:shadow-sm glass-section"
      >
        <!-- 标签页头部 -->
        <div class="flex border-b border-gray-200 dark:border-gray-700">
          <button
            v-for="tab in tabs"
            :key="tab.key"
            :class="[
              'flex-1 py-3 px-4 text-sm font-medium transition-all',
              activeTab === tab.key
                ? 'text-primary-600 border-b-2 border-primary-600 bg-primary-50'
                : 'text-gray-600 hover:text-gray-900 hover:bg-gray-50',
            ]"
            @click="activeTab = tab.key as 'categories' | 'profile'"
          >
            {{ tab.label }}
          </button>
        </div>

        <!-- 标签页内容 -->
        <div class="p-6">
          <!-- 文章分类标签页 -->
          <div v-show="activeTab === 'categories'" class="categories-content">
            <div class="space-y-2">
              <div
                v-for="category in categories"
                :key="category.id"
                class="flex justify-between items-center p-3 rounded-lg transition-colors cursor-pointer category-item hover:bg-gray-50 dark:hover:bg-gray-800"
                @click="handleCategoryClick(category.id)"
              >
                <div class="flex gap-3 items-center">
                  <div class="w-2 h-2 rounded-full bg-primary-500"></div>
                  <span
                    class="text-sm font-medium text-gray-700 dark:text-gray-300"
                    >{{ category.name }}</span
                  >
                </div>
                <span
                  class="px-2 py-1 text-xs text-gray-500 bg-gray-100 rounded-full dark:text-gray-400 dark:bg-gray-700"
                >
                  {{ category.articleCount }}
                </span>
              </div>

              <el-empty
                v-if="categories.length === 0"
                description="暂无分类"
                :image-size="60"
              />
            </div>
          </div>
          <!-- 个人信息标签页 -->
          <div v-show="activeTab === 'profile'" class="profile-content">
            <div class="flex flex-col items-center text-center">
              <!-- 头像 -->
              <div class="relative mb-4">
                <img
                  :src="siteStore.authorInfo.avatar || defaultAvatar"
                  :alt="siteStore.authorInfo.username"
                  class="w-24 h-24 rounded-full border-4 border-white shadow-lg"
                />
                <div
                  class="absolute -right-1 -bottom-1 w-6 h-6 bg-green-500 rounded-full border-2 border-white"
                ></div>
              </div>

              <!-- 名称 -->
              <h3
                class="mb-2 text-xl font-bold text-gray-900 dark:text-gray-100"
              >
                {{ siteStore.authorInfo.username }}
              </h3>
              <p class="mb-4 text-sm text-gray-600 dark:text-gray-400">
                {{ siteStore.authorInfo.bio || "热爱技术,热爱分享" }}
              </p>

              <!-- 统计信息 -->
              <div class="grid grid-cols-3 gap-3 mb-4 w-full">
                <div class="stat-item">
                  <div class="text-2xl font-bold text-primary-600">
                    {{ stats.totalArticles }}
                  </div>
                  <div class="mt-1 text-xs text-gray-600 dark:text-gray-400">
                    文章
                  </div>
                </div>
                <div class="stat-item">
                  <div class="text-2xl font-bold text-primary-600">
                    {{ stats.totalCategories }}
                  </div>
                  <div class="mt-1 text-xs text-gray-600 dark:text-gray-400">
                    分类
                  </div>
                </div>
                <div class="stat-item">
                  <div class="text-2xl font-bold text-primary-600">
                    {{ stats.totalViews }}
                  </div>
                  <div class="mt-1 text-xs text-gray-600 dark:text-gray-400">
                    浏览
                  </div>
                </div>
              </div>

              <!-- GitHub 按钮 -->
              <a
                v-if="siteStore.authorInfo.github"
                :href="siteStore.authorInfo.github"
                target="_blank"
                rel="noopener noreferrer"
                class="w-full"
              >
                <el-button type="primary" class="w-full" size="default">
                  <span class="flex gap-2 justify-center items-center">
                    <svg
                      class="w-5 h-5"
                      fill="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"
                      />
                    </svg>
                    GitHub
                  </span>
                </el-button>
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </aside>
</template>
<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useSiteStore } from "@/stores/site";
import { articleApi } from "@/api/article";
import { siteApi } from "@/api/site";
import type { CategoryInfo, StatsInfo, Notice } from "@/types";
import defaultAvatar from "@/assets/images/avatar/defalutAvatar.jpg";

const siteStore = useSiteStore();

const emit = defineEmits<{
  (e: "category-click", id: number): void;
}>();

// 分类数据
const categories = ref<CategoryInfo[]>([]);
// 统计数据
const stats = ref<StatsInfo>({
  totalArticles: 0,
  totalCategories: 0,
  totalViews: 0,
});
// 公告数据
const notices = ref<Notice[]>([]);

/**
 * 标签页配置
 */
const tabs = [
  {
    key: "categories",
    label: "文章分类",
  },
  {
    key: "profile",
    label: "个人信息",
  },
];

/**
 * 当前激活的标签页
 */
const activeTab = ref<"categories" | "profile">("categories");

/**
 * 获取分类列表
 */
const fetchCategories = async () => {
  try {
    categories.value = await articleApi.getCategories();
  } catch (e) {
    console.error("获取分类失败:", e);
  }
};

/**
 * 获取统计信息
 */
const fetchStats = async () => {
  try {
    stats.value = await articleApi.getStats();
  } catch (e) {
    console.error("获取统计信息失败:", e);
  }
};

/**
 * 获取公告列表
 */
const fetchNotices = async () => {
  try {
    notices.value = await siteApi.getNotices();
  } catch (e) {
    console.error("获取公告失败:", e);
  }
};

/**
 * 点击分类,跳转到文章列表页并筛选该分类
 */
const handleCategoryClick = (id: number): void => {
  emit("category-click", id);
};

onMounted(() => {
  fetchCategories();
  fetchStats();
  fetchNotices();
});
</script>
<style scoped>
/* 毛玻璃效果 */
@media (min-width: 768px) {
  .glass-section {
    background: var(--glass-bg);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    border: 1px solid var(--glass-border);
  }
}

/* 公告栏 - 已改为 Tailwind 类控制 */

/* 标签页卡片 */
.tab-card {
  transition: all 0.3s ease;
}

.tab-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

/* 统计项 */
.stat-item {
  transition: transform 0.2s ease;
}

.stat-item:hover {
  transform: scale(1.1);
}
</style>
