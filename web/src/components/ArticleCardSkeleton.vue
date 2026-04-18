<template>
  <el-skeleton animated>
    <template #template>
      <div
        class="group relative overflow-hidden rounded-2xl h-80 border border-slate-200/70 dark:border-slate-700/70"
      >
        <!-- 模拟封面图作为背景 -->
        <div
          class="absolute inset-0 bg-gradient-to-br from-primary-200 via-primary-300 to-primary-400 dark:from-slate-700 dark:to-slate-600"
        />

        <!-- 渐变遮罩层 -->
        <div
          class="absolute inset-0 bg-gradient-to-t from-black/60 via-black/30 to-transparent dark:from-black/80 dark:via-black/50"
        />

        <!-- 卡片内容 - 定位在底部 -->
        <div class="absolute inset-x-0 bottom-0 p-6 flex flex-col">
          <!-- 标签 -->
          <div class="flex flex-wrap gap-2 mb-3">
            <el-skeleton-item
              variant="rect"
              class="!rounded-full"
              style="width: 60px; height: 22px"
            />
            <el-skeleton-item
              variant="rect"
              class="!rounded-full"
              style="width: 48px; height: 22px"
            />
            <el-skeleton-item
              variant="rect"
              class="!rounded-full"
              style="width: 40px; height: 22px"
            />
          </div>

          <!-- 标题 (line-clamp-2) -->
          <div class="mb-2 space-y-1.5">
            <el-skeleton-item
              variant="rect"
              class="!rounded-md"
              style="width: 88%; height: 24px"
            />
            <el-skeleton-item
              variant="rect"
              class="!rounded-md"
              style="width: 62%; height: 24px"
            />
          </div>

          <!-- 摘要 (line-clamp-2) -->
          <div class="mb-4 space-y-1.5">
            <el-skeleton-item
              variant="rect"
              class="!rounded"
              style="width: 100%; height: 16px"
            />
            <el-skeleton-item
              variant="rect"
              class="!rounded"
              style="width: 72%; height: 16px"
            />
          </div>

          <!-- 元信息 -->
          <div
            class="flex flex-wrap justify-between items-center pt-3 border-t border-white/40 dark:border-white/20 gap-2"
          >
            <div class="flex items-center gap-x-3">
              <el-skeleton-item
                variant="rect"
                class="!rounded"
                style="width: 70px; height: 14px"
              />
              <el-skeleton-item
                variant="rect"
                class="!rounded"
                style="width: 42px; height: 14px"
              />
              <el-skeleton-item
                variant="rect"
                class="!rounded"
                style="width: 38px; height: 14px"
              />
            </div>
            <el-skeleton-item
              variant="rect"
              class="!rounded"
              style="width: 78px; height: 14px"
            />
          </div>
        </div>
      </div>
    </template>
  </el-skeleton>
</template>

<script setup lang="ts">
</script>
