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
              :src="userInfo.avatar || defaultAvatar"
              :alt="userInfo.username"
              class="mb-4 w-32 h-32 rounded-full border-4 border-white shadow-lg"
            />
            <h1
              class="mb-2 text-3xl font-bold text-gray-900 dark:text-gray-100"
            >
              {{ userInfo.username }}
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
        <div class="flex flex-wrap gap-3">
          <div
            v-for="skill in skills"
            :key="skill.id"
            class="flex items-center px-4 py-2 bg-gray-50 rounded-lg border border-gray-100 transition-all duration-300 skill-tag dark:bg-gray-800 dark:border-gray-700 hover:border-primary-500 hover:bg-primary-50 dark:hover:bg-gray-700"
          >
            <!-- 这里简单处理图标，如果是 font awesome 类名则用 i 标签，否则作为 emoji 显示 (如果后端存的是 emoji) -->
            <i
              v-if="skill.icon && skill.icon.startsWith('fa')"
              :class="skill.icon + ' mr-2 text-xl'"
            ></i>
            <span v-else class="mr-2 text-xl">{{ skill.icon }}</span>

            <span class="font-medium text-gray-700 dark:text-gray-200">{{
              skill.name
            }}</span>
            <span class="ml-2 text-xs text-gray-500"
              >{{ skill.percentage }}%</span
            >
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
            :key="project.id"
            class="overflow-hidden rounded-xl border border-gray-200 transition-all duration-300 project-card group dark:border-gray-700 hover:shadow-lg"
          >
            <div class="overflow-hidden relative h-48">
              <div
                class="absolute inset-0 z-10 transition-colors bg-primary-900/10 group-hover:bg-transparent"
              ></div>
              <img
                :src="project.coverImage || 'https://picsum.photos/600/300'"
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
                    v-if="project.githubUrl"
                    :href="project.githubUrl"
                    target="_blank"
                    class="text-gray-400 hover:text-gray-900 dark:hover:text-white"
                  >
                    <el-icon><Link /></el-icon>
                  </a>
                  <a
                    v-if="project.previewUrl"
                    :href="project.previewUrl"
                    target="_blank"
                    class="text-gray-400 hover:text-gray-900 dark:hover:text-white"
                  >
                    <el-icon><View /></el-icon>
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
                  v-for="tag in project.techStack
                    ? project.techStack.split(',')
                    : []"
                  :key="tag"
                  class="px-2 py-0.5 text-xs text-gray-600 bg-gray-100 rounded dark:bg-gray-700 dark:text-gray-300"
                >
                  {{ tag.trim() }}
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
            v-for="item in timelines"
            :key="item.id"
            :timestamp="item.year"
            placement="top"
            :color="item.color || '#e5e7eb'"
            :icon="item.icon ? (item.icon.includes('fa') ? '' : item.icon) : ''"
          >
            <!-- 注意：el-timeline-item 的 icon 属性预期是 Element Plus Icon 组件或名称，
                 如果后端返回的是 'Star' 这种字符串，Element Plus 可能需要 component is="" 转换，或者我们保留默认圆点。
                 这里暂时只用颜色，如果需要图标组件，需要动态映射。
             -->
            <div class="group">
              <h4
                class="mb-1 font-semibold text-gray-900 transition-colors dark:text-gray-100 group-hover:text-primary-600"
              >
                {{ item.title }}
              </h4>
              <p class="text-sm text-gray-600 dark:text-gray-400">
                {{ item.description }}
              </p>
            </div>
          </el-timeline-item>
        </el-timeline>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from "vue";
import { useSiteStore } from "@/stores/site";
import { Message, Link, View } from "@element-plus/icons-vue";
import { siteApi } from "@/api/site";
import type { Skill, Project, Timeline } from "@/types";
import defaultAvatar from "@/assets/images/avatar/defalutAvatar.jpg";

const siteStore = useSiteStore();
const userInfo = computed(() => siteStore.authorInfo);

// 技能数据 (扁平化，或者前端自行分组)
const skills = ref<Skill[]>([]);

// 项目数据
const projects = ref<Project[]>([]);

// 时间线数据
const timelines = ref<Timeline[]>([]);

const fetchSkills = async () => {
  try {
    skills.value = await siteApi.getSkills();
  } catch (e) {
    console.error("获取技能失败", e);
  }
};

const fetchProjects = async () => {
  try {
    projects.value = await siteApi.getFeaturedProjects();
  } catch (e) {
    console.error("获取项目失败", e);
  }
};

const fetchTimelines = async () => {
  try {
    timelines.value = await siteApi.getTimelines();
  } catch (e) {
    console.error("获取时间线失败", e);
  }
};

const handleEmail = () => {
  window.location.href = `mailto:${userInfo.value.email}`;
};

const openLink = (url: string) => {
  window.open(url, "_blank");
};

onMounted(() => {
  fetchSkills();
  fetchProjects();
  fetchTimelines();
});
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
