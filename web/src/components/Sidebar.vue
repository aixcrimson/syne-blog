<template>
  <aside class="w-full sidebar">
    <div class="flex flex-col gap-3">
      <!-- 作者信息卡片 -->
      <div class="overflow-hidden paper-card paper-card-hover">
        <div class="p-4">

          <div class="flex flex-col items-center text-center">
            <!-- 头像 -->
            <div class="relative mb-3">
              <img
                :src="siteStore.authorInfo.avatar || defaultAvatar"
                :alt="siteStore.authorInfo.username"
                class="w-[72px] h-[72px] rounded-full border-4 border-white shadow-lg"
              />

              <div
                class="absolute -right-1 -bottom-1 w-6 h-6 bg-green-500 rounded-full border-2 border-white"
              ></div>
            </div>

            <!-- 名称 -->
            <h3
              class="mb-1 text-lg font-bold text-slate-900 dark:text-slate-100"
            >
              {{ siteStore.authorInfo.username }}
            </h3>
            <p class="mb-2.5 text-sm leading-6 text-slate-600 dark:text-slate-400">
              {{ siteStore.authorInfo.bio || "热爱技术,热爱分享" }}
            </p>

            <!-- 统计信息 -->
            <div class="grid grid-cols-3 gap-1.5 mb-2.5 w-full">
              <div class="stat-item">
                <div class="text-xl font-bold text-primary-600">
                  {{ stats.totalArticles }}
                </div>
                <div class="mt-1 text-xs text-slate-600 dark:text-slate-400">
                  文章
                </div>
              </div>
              <div class="stat-item">
                <div class="text-xl font-bold text-primary-600">
                  {{ stats.totalCategories }}
                </div>
                <div class="mt-1 text-xs text-slate-600 dark:text-slate-400">
                  分类
                </div>
              </div>
              <div class="stat-item">
                <div class="text-xl font-bold text-primary-600">
                  {{ stats.totalViews }}
                </div>
                <div class="mt-1 text-xs text-slate-600 dark:text-slate-400">
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
              <el-button type="primary" class="w-full" size="small">

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

      <div class="xl:sticky xl:top-24 flex flex-col gap-3">
        <!-- 分类列表卡片 -->
        <div class="overflow-hidden paper-card paper-card-hover">
        <div class="px-4 py-2.5 border-b border-slate-200/70 dark:border-slate-700/70">
          <h3 class="text-base font-semibold text-slate-900 dark:text-slate-100">
            分类列表
          </h3>
        </div>
        <div class="p-3">
          <div class="space-y-1">
            <!-- 加载中 -->
            <div v-if="loadingCategories" class="flex justify-center items-center py-8">
              <el-icon class="is-loading text-2xl text-primary-500">
                <Loading />
              </el-icon>
            </div>

            <!-- 分类列表 -->
            <template v-else>
              <div
                v-for="category in categories"
                :key="category.id"
                class="flex justify-between items-center px-2.5 py-2 rounded-lg transition-all duration-200 cursor-pointer category-item"
                :class="[
                  props.selectedCategoryId === category.id
                    ? 'bg-primary-50 dark:bg-primary-900/30'
                    : 'hover:bg-slate-50/70 dark:hover:bg-slate-800/50'
                ]"
                @click="handleCategoryClick(category.id)"
              >
                <div class="flex gap-2.5 items-center">
                  <div
                    class="w-2 h-2 rounded-full transition-transform duration-200"
                    :class="[
                      props.selectedCategoryId === category.id
                        ? 'bg-primary-600 scale-125'
                        : 'bg-primary-500'
                    ]"
                  ></div>
                  <span
                    class="text-sm font-medium transition-colors duration-200"
                    :class="[
                      props.selectedCategoryId === category.id
                        ? 'text-primary-600 dark:text-primary-400'
                        : 'text-slate-700 dark:text-slate-300'
                    ]"
                    >{{ category.name }}</span
                  >
                </div>
                <span
                  class="px-1.5 py-0.5 text-xs rounded-full transition-colors duration-200"
                  :class="[
                    props.selectedCategoryId === category.id
                      ? 'text-primary-600 bg-primary-100 dark:text-primary-400 dark:bg-primary-900/50'
                      : 'text-slate-500 bg-slate-100/80 dark:text-slate-400 dark:bg-slate-800/60'
                  ]"
                >
                  {{ category.articleCount }}
                </span>
              </div>

              <el-empty
                v-if="categories.length === 0"
                description="暂无分类"
                :image-size="60"
              />
            </template>
          </div>
        </div>
      </div>

      <!-- 标签云卡片 -->
      <div class="overflow-hidden paper-card paper-card-hover">
        <div class="px-4 py-2.5 border-b border-slate-200/70 dark:border-slate-700/70">
          <h3 class="text-base font-semibold text-slate-900 dark:text-slate-100">
            标签云
          </h3>
        </div>
        <div class="p-4">
          <div v-if="loadingTags" class="flex justify-center items-center py-6">
            <el-icon class="is-loading text-2xl text-primary-500">
              <Loading />
            </el-icon>
          </div>
          <div v-else class="flex flex-wrap gap-2">
            <div
              v-for="tag in tags"
              :key="tag.id"
              class="px-3 py-1 text-xs font-medium transition-all duration-300 rounded-full cursor-pointer select-none"
              :class="[
                selectedTagIds?.includes(tag.id)
                  ? 'bg-primary-500 text-white shadow-sm scale-105'
                  : 'bg-slate-100 text-slate-600 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 dark:hover:bg-slate-700'
              ]"
              @click="handleTagClick(tag.id)"
            >
              {{ tag.name }}
            </div>
            <el-empty
              v-if="tags.length === 0"
              description="暂无标签"
              :image-size="40"
            />
          </div>
        </div>
      </div>
      </div>
    </div>
  </aside>
</template>
<script setup lang="ts">
import { ref, onMounted } from "vue";
import { Loading } from "@element-plus/icons-vue";
import { useSiteStore } from "@/stores/site";
import { articleApi } from "@/api/article";
import type { CategoryInfo, TagInfo, StatsInfo } from "@/types";
import defaultAvatar from "@/assets/images/avatar/defalutAvatar.jpg";

const siteStore = useSiteStore();

const props = defineProps<{
  selectedTagIds?: number[];
  selectedCategoryId?: number | string;
}>();

const emit = defineEmits<{
  (e: "category-click", id: number): void;
  (e: "tag-click", id: number): void;
}>();

// 加载状态
const loadingCategories = ref(true);
const loadingTags = ref(true);

// 数据
const categories = ref<CategoryInfo[]>([]);
const tags = ref<TagInfo[]>([]);
// 统计数据
const stats = ref<StatsInfo>({
  totalArticles: 0,
  totalCategories: 0,
  totalViews: 0,
});

/**
 * 获取分类列表
 */
const fetchCategories = async () => {
  loadingCategories.value = true;
  try {
    categories.value = await articleApi.getCategories();
  } catch (e) {
    console.error("获取分类失败:", e);
  } finally {
    loadingCategories.value = false;
  }
};

/**
 * 获取标签列表
 */
const fetchTags = async () => {
  loadingTags.value = true;
  try {
    tags.value = await articleApi.getTags();
  } catch (e) {
    console.error("获取标签失败:", e);
  } finally {
    loadingTags.value = false;
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
 * 点击分类,跳转到文章列表页并筛选该分类
 */
const handleCategoryClick = (id: number): void => {
  emit("category-click", id);
};

/**
 * 点击标签
 */
const handleTagClick = (id: number): void => {
  emit("tag-click", id);
};

onMounted(() => {
  fetchCategories();
  fetchTags();
  fetchStats();
});
</script>
<style scoped>
/* 统计项 */
.stat-item {
  transition: transform 0.2s ease;
}

.stat-item:hover {
  transform: scale(1.1);
}

.tag-pill {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.tag-pill:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(var(--color-primary-rgb), 0.3);
}
</style>
