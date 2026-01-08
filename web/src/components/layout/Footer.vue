<template>
  <footer class="mt-auto text-gray-300 footer-glass">
    <div class="px-4 py-8 mx-auto max-w-7xl sm:px-6 lg:px-8 md:py-12">
      <div class="hidden gap-6 md:grid md:grid-cols-3 md:gap-8">
        <!-- 关于部分 -->
        <div>
          <h3 class="mb-4 text-lg font-semibold text-white">关于博客</h3>
          <p class="text-sm leading-6">
            {{ siteStore.authorInfo.bio }}
          </p>
        </div>

        <!-- 快速链接 -->
        <div>
          <h3 class="mb-4 text-lg font-semibold text-white">快速链接</h3>
          <ul class="space-y-2">
            <li v-for="item in menuItems" :key="item.path">
              <router-link
                :to="item.path"
                class="text-sm transition-colors hover:text-primary-400"
              >
                {{ item.name }}
              </router-link>
            </li>
          </ul>
        </div>

        <!-- 联系方式 -->
        <div>
          <h3 class="mb-4 text-lg font-semibold text-white">联系我</h3>
          <ul class="space-y-2">
            <li class="flex items-center space-x-2">
              <el-icon><Message /></el-icon>
              <a
                :href="`mailto:${siteStore.authorInfo.email}`"
                class="text-sm hover:text-primary-400"
              >
                {{ siteStore.authorInfo.email }}
              </a>
            </li>
            <li
              v-if="siteStore.authorInfo.github"
              class="flex items-center space-x-2"
            >
              <el-icon><Link /></el-icon>
              <a
                :href="siteStore.authorInfo.github"
                target="_blank"
                class="text-sm hover:text-primary-400"
              >
                GitHub
              </a>
            </li>
            <li
              v-if="siteStore.authorInfo.bilibili"
              class="flex items-center space-x-2"
            >
              <el-icon><Link /></el-icon>
              <a
                :href="siteStore.authorInfo.bilibili"
                target="_blank"
                class="text-sm hover:text-primary-400"
              >
                Bilibili
              </a>
            </li>
          </ul>
        </div>
      </div>

      <!-- 版权信息 -->
      <div
        class="pt-0 mt-0 text-center border-t-0 md:mt-8 md:pt-8 md:border-t md:border-gray-800"
      >
        <p class="text-xs text-gray-500 md:text-sm md:text-gray-300">
          © {{ currentYear }} {{ siteStore.authorInfo.username }}. All rights
          reserved.
        </p>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useSiteStore } from "@/stores/site";
import { Message, Link } from "@element-plus/icons-vue";

const siteStore = useSiteStore();

const currentYear = computed(() => new Date().getFullYear());

const menuItems = [
  { name: "首页", path: "/" },
  { name: "文章", path: "/articles" },
  { name: "网站导航", path: "/navigation" },
  { name: "关于", path: "/about" },
];
</script>

<style scoped>
/* 毛玻璃效果 Footer */
.footer-glass {
  background: rgba(17, 24, 39, 0.85);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}
</style>
