<template>
  <div class="main-layout min-h-screen flex flex-col">
    <!-- 背景图片 -->
    <div class="background-image" :style="{ backgroundImage: `url(${bgImage})` }"></div>
    
    <!-- 导航栏 -->
    <Header />
    
    <!-- 主要内容区域 -->
    <main class="flex-1">
      <router-view />
    </main>
    
    <!-- 页脚 -->
    <Footer />
  </div>
</template>

<script setup lang="ts">
import Header from '@/components/layout/Header.vue'
import Footer from '@/components/layout/Footer.vue'
import bgImage from '@/assets/images/common/lightTheme.png'
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
}

/* 确保内容在背景之上 */
.main-layout > *:not(.background-image) {
  position: relative;
  z-index: 10;
}
</style>

