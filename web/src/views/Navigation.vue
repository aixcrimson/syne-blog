<template>
  <div class="py-12 min-h-screen bg-transparent">
    <div class="px-4 mx-auto max-w-7xl sm:px-6 lg:px-8">
      <!-- 页面标题 -->
      <div class="mb-12 text-center">
        <h1 class="mb-4 text-4xl font-bold text-gray-900 dark:text-gray-100">
          网站导航
        </h1>
        <p class="text-lg text-gray-600 dark:text-gray-300">
          收集常用的优质网站，助力高效工作与学习
        </p>
      </div>

      <!-- 骨架屏加载状态 -->
      <div v-if="loading" class="space-y-12">
        <div v-for="i in 2" :key="i" class="space-y-6">
          <el-skeleton animated>
            <template #template>
              <div class="flex gap-4 items-center mb-6">
                <el-skeleton-item
                  variant="rect"
                  style="width: 4px; height: 32px"
                />
                <el-skeleton-item variant="text" style="width: 150px" />
              </div>
              <div class="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
                <div
                  v-for="j in 6"
                  :key="j"
                  class="h-32 bg-gray-100 rounded-lg dark:bg-gray-800"
                />
              </div>
            </template>
          </el-skeleton>
        </div>
      </div>

      <!-- 导航分类 -->
      <div v-else-if="navigationCategories.length > 0" class="space-y-12">
        <div
          v-for="category in navigationCategories"
          :key="category.categoryId"
          class="space-y-6"
        >
          <h2
            class="pl-4 text-2xl font-bold text-gray-800 border-l-4 dark:text-gray-200 border-primary-500"
          >
            {{ category.categoryName }}
          </h2>
          <div class="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
            <a
              v-for="site in category.sites"
              :key="site.id"
              :href="site.url"
              target="_blank"
              rel="noopener noreferrer"
              class="p-6 rounded-lg shadow-sm transition-all duration-300 group glass-card hover:shadow-lg hover:border-primary-500"
            >
              <div class="flex items-start space-x-4">
                <div class="flex-shrink-0">
                  <div
                    class="justify-center items-center w-12 h-12 text-xl font-bold text-white bg-gradient-to-br rounded-lg transition-transform site-icon-wrapper from-primary-400 to-primary-600 group-hover:scale-110"
                  >
                    <img
                      v-if="site.icon && site.icon.startsWith('http')"
                      :src="site.icon"
                      :alt="site.name"
                      class="object-cover w-full h-full rounded-lg"
                    />
                    <span v-else>{{ site.icon || site.name.charAt(0) }}</span>
                  </div>
                </div>
                <div class="flex-1 min-w-0">
                  <h3
                    class="text-lg font-semibold text-gray-900 truncate transition-colors dark:text-gray-100 group-hover:text-primary-600"
                  >
                    {{ site.name }}
                  </h3>
                  <p
                    class="mt-1 text-sm text-gray-600 dark:text-gray-400 line-clamp-2"
                  >
                    {{ site.description }}
                  </p>
                  <div
                    class="flex items-center mt-2 text-xs text-gray-400 dark:text-gray-500"
                  >
                    <el-icon class="mr-1"><Link /></el-icon>
                    <span class="truncate">{{ site.url }}</span>
                  </div>
                </div>
              </div>
            </a>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-else description="暂无导航数据" />

      <!-- 添加网站提示 -->
      <div class="mt-12 text-center">
        <div class="inline-block px-6 py-4 bg-blue-50 rounded-lg">
          <p class="text-sm text-gray-600 dark:text-gray-600">
            <el-icon class="mr-2"><InfoFilled /></el-icon>
            如有更多优质网站推荐，欢迎联系我～
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { Link, InfoFilled } from "@element-plus/icons-vue";
import type { NavigationCategory } from "@/types";
import { navigationApi } from "@/api/navigation";

// 导航数据
const navigationCategories = ref<NavigationCategory[]>([]);
const loading = ref(false);

const getNavigationSites = async () => {
  loading.value = true;
  try {
    navigationCategories.value = await navigationApi.getList();
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  getNavigationSites();
});
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.glass-card {
  background: var(--glass-bg);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid var(--glass-border);
}

.site-icon-wrapper {
  display: none;
}

@media (min-width: 640px) {
  .site-icon-wrapper {
    display: flex;
  }
}
</style>
