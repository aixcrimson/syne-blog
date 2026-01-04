<template>
  <div class="py-12 bg-transparent about">
    <div class="px-4 mx-auto max-w-4xl sm:px-6 lg:px-8">
      <!-- 个人信息卡片 -->
      <div class="overflow-hidden mb-8 rounded-lg shadow-sm glass-card">
        <div
          class="h-32 bg-gradient-to-r from-primary-600 to-primary-800"
        ></div>
        <div class="px-8 pb-8">
          <div class="flex flex-col items-center -mt-16 mb-6">
            <img
              :src="userInfo.avatar"
              :alt="userInfo.name"
              class="mb-4 w-32 h-32 rounded-full border-4 border-white shadow-lg"
            />
            <h1
              class="mb-2 text-3xl font-bold text-gray-900 dark:text-gray-100"
            >
              {{ userInfo.name }}
            </h1>
            <p class="max-w-md text-center text-gray-600 dark:text-gray-300">
              {{ userInfo.bio }}
            </p>
          </div>

          <!-- 联系方式 -->
          <div class="flex justify-center mb-6 space-x-4">
            <el-button type="primary" @click="handleEmail">
              <el-icon class="mr-2"><Message /></el-icon>
              发送邮件
            </el-button>
            <el-button
              v-if="userInfo.github"
              @click="openLink(userInfo.github!)"
            >
              <el-icon class="mr-2"><Link /></el-icon>
              GitHub
            </el-button>
            <el-button
              v-if="userInfo.bilibili"
              @click="openLink(userInfo.bilibili!)"
            >
              <el-icon class="mr-2"><Link /></el-icon>
              Bilibili
            </el-button>
          </div>
        </div>
      </div>

      <!-- 技能栈 (优化版) -->
      <div class="p-8 mb-8 rounded-lg shadow-sm glass-card">
        <h2 class="mb-6 text-2xl font-bold text-gray-900 dark:text-gray-100">
          技术栈
        </h2>
        <div class="space-y-6">
          <div v-for="group in skillGroups" :key="group.category">
            <h3
              class="mb-3 text-sm font-semibold tracking-wider text-gray-500 uppercase dark:text-gray-400"
            >
              {{ group.category }}
            </h3>
            <div class="flex flex-wrap gap-3">
              <div
                v-for="skill in group.items"
                :key="skill.name"
                class="flex items-center px-4 py-2 bg-gray-50 rounded-lg border border-gray-100 transition-all duration-300 skill-tag dark:bg-gray-800 dark:border-gray-700 hover:border-primary-500 hover:bg-primary-50 dark:hover:bg-gray-700"
              >
                <span class="mr-2 text-xl">{{ skill.icon }}</span>
                <span class="font-medium text-gray-700 dark:text-gray-200">{{
                  skill.name
                }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 项目展示 (新增) -->
      <div class="p-8 mb-8 rounded-lg shadow-sm glass-card">
        <h2 class="mb-6 text-2xl font-bold text-gray-900 dark:text-gray-100">
          精选项目
        </h2>
        <div class="grid grid-cols-1 gap-6 md:grid-cols-2">
          <div
            v-for="project in projects"
            :key="project.title"
            class="overflow-hidden rounded-xl border border-gray-200 transition-all duration-300 project-card group dark:border-gray-700 hover:shadow-lg"
          >
            <div class="overflow-hidden relative h-48">
              <div
                class="absolute inset-0 z-10 transition-colors bg-primary-900/10 group-hover:bg-transparent"
              ></div>
              <img
                :src="project.cover"
                :alt="project.title"
                class="object-cover w-full h-full transition-transform duration-500 transform group-hover:scale-105"
              />
            </div>
            <div class="p-5 bg-white dark:bg-gray-800">
              <div class="flex justify-between items-start mb-2">
                <h3
                  class="text-lg font-bold text-gray-900 transition-colors dark:text-gray-100 group-hover:text-primary-600"
                >
                  {{ project.title }}
                </h3>
                <div class="flex gap-2">
                  <a
                    v-if="project.links.github"
                    :href="project.links.github"
                    target="_blank"
                    class="text-gray-400 hover:text-gray-900 dark:hover:text-white"
                  >
                    <el-icon><Link /></el-icon>
                  </a>
                </div>
              </div>
              <p
                class="mb-4 text-sm text-gray-600 dark:text-gray-400 line-clamp-2"
              >
                {{ project.description }}
              </p>
              <div class="flex flex-wrap gap-2">
                <span
                  v-for="tag in project.tags"
                  :key="tag"
                  class="px-2 py-0.5 text-xs text-gray-600 bg-gray-100 rounded dark:bg-gray-700 dark:text-gray-300"
                >
                  {{ tag }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 时间线 -->
      <div class="p-8 rounded-lg shadow-sm glass-card">
        <h2 class="mb-6 text-2xl font-bold text-gray-900 dark:text-gray-100">
          成长历程
        </h2>
        <el-timeline>
          <el-timeline-item
            v-for="item in timeline"
            :key="item.date"
            :timestamp="item.date"
            placement="top"
            :color="item.color || '#e5e7eb'"
          >
            <div class="group">
              <h4
                class="mb-1 font-semibold text-gray-900 transition-colors dark:text-gray-100 group-hover:text-primary-600"
              >
                {{ item.title }}
              </h4>
              <p class="text-sm text-gray-600 dark:text-gray-400">
                {{ item.content }}
              </p>
            </div>
          </el-timeline-item>
        </el-timeline>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useSiteStore } from "@/stores/site";
import { Message, Link } from "@element-plus/icons-vue";

const siteStore = useSiteStore();
const userInfo = computed(() => siteStore.authorInfo);

// 技能分组数据
const skillGroups = [
  {
    category: "前端架构",
    items: [
      { name: "Vue 3", icon: "💚" },
      { name: "TypeScript", icon: "💙" },
      { name: "Tailwind CSS", icon: "🌊" },
      { name: "Vite", icon: "⚡" },
      { name: "Element Plus", icon: "🎨" },
    ],
  },
  {
    category: "后端服务",
    items: [
      { name: "Java", icon: "☕" },
      { name: "Spring Boot", icon: "🍃" },
      { name: "PostgreSQL", icon: "🐘" },
      { name: "Redis", icon: "🔴" },
    ],
  },
  {
    category: "开发工具",
    items: [
      { name: "Git", icon: "📦" },
      { name: "Docker", icon: "🐳" },
      { name: "VS Code", icon: "💻" },
    ],
  },
];

// 项目数据
const projects = [
  {
    title: "Syne Blog System",
    description:
      "基于 Vue 3 和 Spring Boot 开发的现代化个人博客系统。支持动态文章管理、Markdown 渲染、评论系统及深色模式。",
    cover: "https://picsum.photos/id/1/600/300", // 占位图
    tags: ["Vue 3", "Spring Boot", "TypeScript"],
    links: {
      github: "https://github.com/your-repo/syne-blog",
      demo: "#",
    },
  },
  {
    title: "Smart Cloud Admin",
    description:
      "企业级后台管理系统解决方案，集成了权限管理、数据可视化大屏和工作流引擎。",
    cover: "https://picsum.photos/id/20/600/300",
    tags: ["Vue 3", "Element Plus", "ECharts"],
    links: {
      github: "#",
    },
  },
];

const timeline = [
  {
    date: "2024-12",
    title: "System 2.0 重构",
    content: "全面升级前后端技术栈，引入 PostgreSQL 和 Tailwind CSS",
    color: "#409EFF",
  },
  {
    date: "2024-02",
    title: "博客上线",
    content: "个人博客正式上线，开始记录技术学习历程",
    color: "#67C23A",
  },
  {
    date: "2023-06",
    title: "全栈进阶",
    content: "深入研究 Spring Boot 微服务架构与容器化部署",
  },
];

const handleEmail = () => {
  window.location.href = `mailto:${userInfo.value.email}`;
};

const openLink = (url: string) => {
  window.open(url, "_blank");
};
</script>

<style scoped>
.glass-card {
  background: var(--glass-bg);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid var(--glass-border);
}

.skill-tag {
  user-select: none;
}

.project-card {
  background: var(--glass-bg);
}
</style>
