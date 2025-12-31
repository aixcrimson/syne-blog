<template>
  <div class="main-layout min-h-screen flex flex-col">
    <!-- 背景图片 -->
    <div
      class="background-image"
      :style="{ backgroundImage: `url(${bgImage})` }"
    ></div>

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
import { computed } from "vue";
import { useAppStore } from "@/stores/app";
import Header from "@/components/layout/Header.vue";
import Footer from "@/components/layout/Footer.vue";
import darkThemeImg from "@/assets/images/common/darkTheme.png";
import lightThemeImg from "@/assets/images/common/lightTheme.png";

const appStore = useAppStore();

const bgImage = computed(() => {
  return appStore.isDarkMode ? darkThemeImg : lightThemeImg;
});
</script>

<style scoped>
.main-layout {
  position: relative;
  background: transparent;
}

/* 背景图片层 */
.background-image {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  z-index: 0;
  pointer-events: none;
  transition: background-image 0.5s ease-in-out;
}
</style>
