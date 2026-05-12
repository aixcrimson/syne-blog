<template>
  <div class="author-info-manage p-0 md:p-6">
    <!-- 页面标题 -->
    <div class="mb-6 hidden md:block">
      <h1 class="text-2xl font-bold text-gray-800">作者信息管理</h1>
      <p class="text-gray-500 mt-1">管理关于页面的作者相关信息</p>
    </div>

    <!-- 主要内容区域 -->
    <div class="glass-card p-4 md:p-6 rounded-lg min-h-[600px]">
      <el-tabs v-model="activeTab" class="author-tabs">
        <el-tab-pane label="公告管理" name="notice">
          <NoticeTab v-if="activeTab === 'notice'" />
        </el-tab-pane>
        <el-tab-pane label="技能栈" name="skill">
          <SkillTab v-if="activeTab === 'skill'" />
        </el-tab-pane>
        <el-tab-pane label="精选项目" name="project">
          <ProjectTab v-if="activeTab === 'project'" />
        </el-tab-pane>
        <el-tab-pane label="时间线" name="timeline">
          <TimelineTab v-if="activeTab === 'timeline'" />
        </el-tab-pane>
        <el-tab-pane label="图库管理" name="cover">
          <CoverTab v-if="activeTab === 'cover'" />
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import NoticeTab from "./components/NoticeTab.vue";
import SkillTab from "./components/SkillTab.vue";
import ProjectTab from "./components/ProjectTab.vue";
import TimelineTab from "./components/TimelineTab.vue";
import CoverTab from "./components/CoverTab.vue";

const activeTab = ref("notice");

defineOptions({
  name: "AuthorInfo"
});
</script>

<style scoped>
:deep(.el-tabs__header) {
  margin-bottom: 24px;
}
:deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background-color: #e5e7eb;
}
:deep(.el-tabs__item) {
  font-size: 16px;
}
:deep(.el-tabs__item.is-active) {
  font-weight: 600;
}
</style>
