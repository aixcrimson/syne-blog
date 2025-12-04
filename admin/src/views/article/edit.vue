<script setup lang="ts">
/**
 * 文章编辑页面
 * 后续任务会实现完整的文章编辑功能
 */
import { useRoute } from 'vue-router'

const route = useRoute()
const isEdit = !!route.params.id
</script>

<template>
  <div class="article-edit">
    <h1 class="text-2xl font-bold text-gray-800 mb-4">
      {{ isEdit ? '编辑文章' : '新建文章' }}
    </h1>
    <p class="text-gray-600">文章编辑功能（后续任务实现）</p>
  </div>
</template>

<style scoped>
</style>
