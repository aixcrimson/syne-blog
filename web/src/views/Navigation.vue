<template>
  <div class="py-12 min-h-screen">
    <div class="px-4 mx-auto max-w-7xl sm:px-6 lg:px-8">
      <!-- 页面标题 -->
      <div class="mb-12 text-center">
        <p
          class="page-mono text-xs uppercase tracking-[0.35em] text-slate-500 dark:text-slate-400"
        >
          Personal Nav Index
        </p>
        <h1
          class="mt-3 text-4xl font-semibold tracking-tight text-slate-900 dark:text-slate-50 sm:text-5xl"
        >
          网站导航
        </h1>
        <p
          class="mx-auto mt-4 max-w-2xl text-base text-slate-600 dark:text-slate-300 sm:text-lg"
        >
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
                  class="paper-card h-32"
                />
              </div>
            </template>
          </el-skeleton>
        </div>
      </div>

      <!-- 导航分类 -->
      <div v-else-if="navigationCategories.length > 0" class="space-y-12">
        <div
          v-for="(category, index) in navigationCategories"
          :key="category.categoryId"
          class="space-y-6"
        >
          <div class="flex items-center gap-4">
            <h2
              class="text-2xl font-semibold tracking-tight text-slate-900 dark:text-slate-100"
            >
              {{ category.categoryName }}
            </h2>

            <span class="flex-1 h-px bg-slate-200/70 dark:bg-slate-700/60" />
          </div>
          <div class="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
            <a
              v-for="site in category.sites"
              :key="site.id"
              :href="site.url"
              target="_blank"
              rel="noopener noreferrer"
              class="paper-card paper-card-hover group relative cursor-pointer p-6 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-blue-500/60 focus-visible:ring-offset-2 focus-visible:ring-offset-[#F8F5F2] dark:focus-visible:ring-offset-[#0B0C10]"
            >
              <div class="flex items-start space-x-4">
                <div class="flex-shrink-0">
                  <div
                    class="site-icon-wrapper justify-center items-center w-12 h-12 text-lg font-semibold text-white bg-slate-900 rounded-xl shadow-[0_8px_18px_-10px_rgba(15,23,42,0.6)] transition-transform duration-300 group-hover:-rotate-2 group-hover:scale-105 dark:bg-slate-100 dark:text-slate-900 motion-reduce:transform-none"
                  >
                    <img
                      v-if="site.icon && site.icon.startsWith('http')"
                      :src="site.icon"
                      :alt="site.name"
                      class="object-cover w-full h-full rounded-xl"
                    />
                    <span v-else class="page-mono">{{
                      site.icon || site.name?.charAt(0)
                    }}</span>
                  </div>
                </div>
                <div class="flex-1 min-w-0">
                  <h3
                    class="text-lg font-semibold text-slate-900 truncate transition-colors dark:text-slate-100 group-hover:text-blue-600"
                  >
                    {{ site.name }}
                  </h3>
                  <p
                    class="mt-1 text-sm text-slate-600 dark:text-slate-400 line-clamp-2"
                  >
                    {{ site.description }}
                  </p>
                  <div
                    class="page-mono flex items-center mt-3 text-xs text-slate-500 dark:text-slate-400"
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
        <div
          class="paper-card inline-flex items-center gap-2 px-6 py-3 text-sm text-slate-600 dark:text-slate-300"
        >
          <el-icon class="text-slate-400"><InfoFilled /></el-icon>
          <span>如有更多优质网站推荐，欢迎联系我～</span>
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

.site-icon-wrapper {
  display: none;
}

@media (min-width: 640px) {
  .site-icon-wrapper {
    display: flex;
  }
}
</style>
