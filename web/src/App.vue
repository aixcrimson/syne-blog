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
import { useUserStore } from "@/stores/user";

const appStore = useAppStore();
const siteStore = useSiteStore();
const userStore = useUserStore();

onMounted(() => {
  // 初始化应用
  appStore.init();
  // 加载博主信息
  siteStore.fetchAuthorInfo();
  // 如果有 token，获取用户信息
  if (userStore.token) {
    userStore.fetchCurrentUser();
  }
});
</script>

<style scoped>
/* App级别样式 */
</style>
