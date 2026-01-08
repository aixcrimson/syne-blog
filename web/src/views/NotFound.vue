<template>
  <div class="min-h-screen flex items-center justify-center px-4">
    <div class="text-center">
      <h1
        class="text-9xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-primary-500 to-purple-600"
      >
        404
      </h1>
      <p class="mt-4 text-2xl font-semibold text-gray-900 dark:text-white">
        页面不见了
      </p>
      <p class="mt-2 text-gray-600 dark:text-gray-400">
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
