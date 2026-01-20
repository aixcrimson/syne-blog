<template>
  <div class="main-layout min-h-screen flex flex-col relative overflow-hidden page-shell">
    <div class="pointer-events-none absolute inset-0 paper-bg">
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
    <div class="relative z-10">
      <Footer />
    </div>
  </div>
</template>

<script setup lang="ts">
import Header from "@/components/layout/Header.vue";
import Footer from "@/components/layout/Footer.vue";
</script>

<style scoped>
.main-layout {
  position: relative;
}
</style>
