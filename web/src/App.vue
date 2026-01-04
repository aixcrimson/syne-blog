<template>
  <div id="app" class="flex flex-col min-h-screen bg-transparent">
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from "vue";
import { useAppStore } from "@/stores/app";
import { useSiteStore } from "@/stores/site";

const appStore = useAppStore();
const siteStore = useSiteStore();

onMounted(() => {
  // 初始化应用
  appStore.init();
  // 加载博主信息
  siteStore.fetchAuthorInfo();
});
</script>

<style scoped>
/* App级别样式 */
</style>
