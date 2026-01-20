<template>
  <div class="page-shell min-h-screen flex items-center justify-center px-4 relative overflow-hidden">
    <div class="pointer-events-none absolute inset-0 paper-bg">
      <div
        class="absolute -top-24 -right-16 w-64 h-64 rounded-full blur-3xl bg-amber-200/40 dark:bg-amber-400/10"
      />
      <div
        class="absolute -bottom-20 -left-14 w-72 h-72 rounded-full blur-3xl bg-sky-200/40 dark:bg-sky-500/10"
      />
      <div class="absolute inset-0 opacity-60 page-grid dark:opacity-30" />
    </div>
    <div class="relative z-10 text-center">
      <p class="page-mono text-xs uppercase tracking-[0.35em] text-slate-500 dark:text-slate-400">
        Lost Page
      </p>
      <h1
        class="text-9xl font-semibold bg-clip-text text-transparent bg-gradient-to-r from-slate-900 to-blue-600 dark:from-slate-100 dark:to-sky-400"
      >
        404
      </h1>
      <p class="mt-4 text-2xl font-semibold text-slate-900 dark:text-slate-100">
        页面不见了
      </p>
      <p class="mt-2 text-slate-600 dark:text-slate-400">
        抱歉，你访问的页面似乎去火星探险了...
      </p>
      <div class="mt-8">
        <el-button type="primary" size="large" @click="router.push('/')" round>
          回到首页
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";

const router = useRouter();
</script>
