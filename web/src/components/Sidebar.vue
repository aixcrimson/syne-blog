<template>
  <aside class="sidebar">
    <div class="flex flex-col gap-6 sticky top-20">
      <!-- 公告栏 -->
      <div
        class="notice-card rounded-xl p-5 shoadow-sm border border-primary-100"
      >
        <div class="flex items-center gap-2 mb-3">
          <div class="w-2 h-2 bg-primary-500 rounded-full animate-pulse"></div>
          <p class="font-bold text-primary-600 text-lg">公告栏</p>
        </div>
        <p class="text-sm text-gray-700 leading-relaxed">
          📢 天行健，君子以自强不息
        </p>
      </div>

      <!-- 标签页切换卡片 -->
      <div class="tab-card glass-section rounded-xl shadow-sm overflow-hidden">
        <!-- 标签页头部 -->
        <div class="flex border-b border-gray-200">
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
                v-for="category in categoriesWithCount"
                :key="category.name"
                class="category-item flex items-center justify-between p-3 rounded-lg hover:bg-gray-50 transition-colors cursor-pointer"
                @click="handleCategoryClick(category.name)"
              >
                <div class="flex items-center gap-3">
                  <div class="w-2 h-2 rounded-full bg-primary-500"></div>
                  <span class="text-sm font-medium text-gray-700">{{
                    category.name
                  }}</span>
                </div>
                <span
                  class="text-xs text-gray-500 bg-gray-100 px-2 py-1 rounded-full"
                >
                  {{ category.count }}
                </span>
              </div>

              <el-empty
                v-if="categoriesWithCount.length === 0"
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
                  :src="appStore.userInfo.avatar"
                  :alt="appStore.userInfo.name"
                  class="w-24 h-24 rounded-full border-4 border-white shadow-lg"
                />
                <div
                  class="absolute -bottom-1 -right-1 w-6 h-6 bg-green-500 rounded-full border-2 border-white"
                ></div>
              </div>

              <!-- 名称 -->
              <h3 class="text-xl font-bold text-gray-900 mb-2">
                {{ appStore.userInfo.name }}
              </h3>
              <p class="text-sm text-gray-600 mb-4">
                {{ appStore.userInfo.bio || "热爱技术,热爱分享" }}
              </p>

              <!-- 统计信息 -->
              <div class="w-full grid grid-cols-3 gap-3 mb-4">
                <div class="stat-item">
                  <div class="text-2xl font-bold text-primary-600">
                    {{ articleStore.totalArticles }}
                  </div>
                  <div class="text-xs text-gray-600 mt-1">文章</div>
                </div>
                <div class="stat-item">
                  <div class="text-2xl font-bold text-primary-600">
                    {{ articleStore.getAllCategories.length }}
                  </div>
                  <div class="text-xs text-gray-600 mt-1">分类</div>
                </div>
                <div class="stat-item">
                  <div class="text-2xl font-bold text-primary-600">
                    {{ totalViews }}
                  </div>
                  <div class="text-xs text-gray-600 mt-1">浏览</div>
                </div>
              </div>

              <!-- GitHub 按钮 -->
              <a
                v-if="appStore.userInfo.github"
                :href="appStore.userInfo.github"
                target="_blank"
                rel="noopener noreferrer"
                class="w-full"
              >
                <el-button type="primary" class="w-full" size="default">
                  <span class="flex items-center justify-center gap-2">
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
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { useArticleStore } from "@/stores/article";
import { useAppStore } from "@/stores/app";

const router = useRouter();
const articleStore = useArticleStore();
const appStore = useAppStore();

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
 * 总浏览量
 */
const totalViews = computed(() => {
    return articleStore.articles.reduce((sum, article) => sum + article.views, 0)
})

/**
 * 分类机器文章数量
 */
const categoriesWithCount = computed(() => {
  const categories = articleStore.getAllCategories;
  return categories.map((category) => ({
    name: category,
    count: articleStore.articles.filter(
      (article) => article.category === category
    ).length,
  }));
});

/**
 * 点击分类,跳转到文章列表页并筛选该分类
 */
const handleCategoryClick = (category: string): void => {
  router.push({
    path: "/articles",
    query: { category },
  });
};
</script>
<style scoped>
/* 毛玻璃效果 */
.glass-section {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

/* 公告栏 */
.notice-card {
  background: linear-gradient(
    135deg,
    var(--color-primary-50) 0%,
    rgba(255, 255, 255, 0.9) 100%
  );
  transform: all 0.3s ease;
}

.notice-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

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
