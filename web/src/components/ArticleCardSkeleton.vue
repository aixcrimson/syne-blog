<template>
  <el-skeleton animated>
    <template #template>
      <div
        class="group relative overflow-hidden rounded-2xl border border-slate-200/50 bg-white/70 dark:border-slate-800/40 dark:bg-slate-900/60 transform-gpu flex"
        :class="[
          layout === 'list' 
            ? 'flex-col sm:flex-row h-auto sm:h-52 md:h-56' 
            : 'flex-col h-[26rem]'
        ]"
      >
        <!-- 模拟封面图 -->
        <div
          class="relative overflow-hidden shrink-0 transform-gpu bg-slate-200/60 dark:bg-slate-800/50"
          :class="[
            layout === 'list' 
              ? 'w-full sm:w-[35%] md:w-[32%] h-44 sm:h-full' 
              : 'w-full h-48'
          ]"
        >
          <!-- 模拟分类标签 -->
          <el-skeleton-item
            variant="rect"
            class="absolute top-3 left-3 !rounded-md"
            style="width: 54px; height: 20px"
          />
        </div>

        <!-- 模拟文字内容区域 -->
        <div class="flex-1 p-5 flex flex-col justify-between min-w-0">
          <div class="min-w-0 flex-1">
            <!-- 模拟标签 -->
            <div class="flex flex-wrap gap-1.5 mb-3">
              <el-skeleton-item
                variant="rect"
                class="!rounded"
                style="width: 46px; height: 16px"
              />
              <el-skeleton-item
                variant="rect"
                class="!rounded"
                style="width: 52px; height: 16px"
              />
            </div>

            <!-- 模拟标题 -->
            <div class="mb-3 space-y-1.5">
              <el-skeleton-item
                variant="rect"
                class="!rounded-md"
                style="width: 90%; height: 22px"
              />
              <el-skeleton-item
                variant="rect"
                class="!rounded-md"
                style="width: 60%; height: 22px"
              />
            </div>

            <!-- 模拟摘要 -->
            <div class="mb-4 space-y-1.5">
              <el-skeleton-item
                variant="rect"
                class="!rounded"
                style="width: 100%; height: 14px"
              />
              <el-skeleton-item
                variant="rect"
                class="!rounded"
                style="width: 75%; height: 14px"
              />
            </div>
          </div>

          <!-- 模拟元数据底部 -->
          <div
            class="flex justify-between items-center pt-3 border-t border-slate-100 dark:border-slate-800/60 gap-2 mt-auto"
          >
            <div class="flex items-center gap-x-3">
              <el-skeleton-item
                variant="rect"
                class="!rounded"
                style="width: 65px; height: 12px"
              />
              <el-skeleton-item
                variant="rect"
                class="!rounded"
                style="width: 42px; height: 12px"
              />
              <el-skeleton-item
                variant="rect"
                class="!rounded"
                style="width: 38px; height: 12px"
              />
            </div>
            <el-skeleton-item
              variant="rect"
              class="!rounded"
              style="width: 60px; height: 12px"
            />
          </div>
        </div>
      </div>
    </template>
  </el-skeleton>
</template>

<script setup lang="ts">
interface Props {
  layout?: 'grid' | 'list';
}

withDefaults(defineProps<Props>(), {
  layout: 'grid'
});
</script>
