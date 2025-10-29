<template>
  <div class="main-layout min-h-screen flex flex-col">
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
</script>

<style scoped>
.main-layout {
  background: linear-gradient(to bottom, #f9fafb 0%, #ffffff 100%);
}
</style>

