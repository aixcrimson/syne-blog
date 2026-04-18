<template>
  <footer
    class="mt-auto border-t border-slate-200/70 bg-white/75 text-slate-600 backdrop-blur-md dark:bg-slate-900/70 dark:border-slate-800/70 dark:text-slate-300"
  >
    <div class="px-4 py-6 mx-auto max-w-7xl sm:px-6 lg:px-8">
      <p class="text-center text-xs text-slate-500 md:text-sm dark:text-slate-400">
        © {{ currentYear }} {{ siteStore.authorInfo.username }}. All rights
        reserved.
      </p>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useSiteStore } from "@/stores/site";

const siteStore = useSiteStore();

const currentYear = computed(() => new Date().getFullYear());
</script>
