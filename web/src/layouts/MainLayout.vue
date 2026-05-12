<template>
  <div class="main-layout min-h-screen flex flex-col relative page-shell">
    <div class="pointer-events-none absolute inset-0 paper-bg overflow-hidden">
      <div
        class="absolute -top-24 -right-16 w-64 h-64 rounded-full blur-3xl bg-amber-200/40 dark:bg-amber-400/10"
      />
      <div
        class="absolute -bottom-20 -left-14 w-72 h-72 rounded-full blur-3xl bg-sky-200/40 dark:bg-sky-500/10"
      />
      <div class="absolute inset-0 opacity-60 page-grid dark:opacity-30" />
    </div>

    <!-- 导航栏 -->
    <Header />

    <!-- 主要内容区域 -->
    <main class="flex-1 pt-16 relative z-10">
      <router-view />
    </main>

    <!-- 页脚 -->
    <div class="relative">
      <Footer />
    </div>

    <!-- 移动端全局侧边栏 (由 FloatingToolbar 触发) -->
    <el-drawer
      :model-value="appStore.showSidebar"
      @update:model-value="appStore.showSidebar = $event"
      title="关于作者 & 分类"
      direction="ltr"
      size="80%"
      :lock-scroll="false"
    >
      <Sidebar @category-click="handleCategoryClick" @tag-click="handleTagClick" />
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";
import { useAppStore } from "@/stores/app";
import Header from "@/components/layout/Header.vue";
import Footer from "@/components/layout/Footer.vue";
import Sidebar from "@/components/Sidebar.vue";

const router = useRouter();
const appStore = useAppStore();

const handleCategoryClick = (id: number) => {
  appStore.closeSidebar();
  router.push(`/articles?category=${id}`);
};

const handleTagClick = (id: number) => {
  appStore.closeSidebar();
  router.push(`/articles?tag=${id}`);
};
</script>

<style scoped>
.main-layout {
  position: relative;
}
</style>
