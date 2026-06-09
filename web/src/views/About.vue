<template>
  <div class="py-12 about">
    <div class="px-4 mx-auto max-w-4xl sm:px-6 lg:px-8">
      <!-- 个人信息卡片 - 直接显示 -->
      <div class="paper-card overflow-hidden mb-8">
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
              class="mb-2 text-3xl font-semibold text-slate-900 dark:text-slate-100"
            >
              {{ userInfo.username }}
            </h1>
            <p class="max-w-md text-center text-slate-600 dark:text-slate-300">
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

      <!-- 技能栈 -->
      <div class="paper-card p-8 mb-8">
        <h2 class="mb-6 text-2xl font-semibold text-slate-900 dark:text-slate-100">
          技术栈
        </h2>
        <!-- 骨架屏 -->
        <div v-if="loadingSkills" class="grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-4">
          <div
            v-for="i in 8"
            :key="i"
            class="flex flex-col p-4 bg-white/70 rounded-xl border border-slate-200/70 dark:bg-slate-900/60 dark:border-slate-800/60"
          >
            <el-skeleton animated>
              <template #template>
                <div class="flex justify-between items-center mb-3">
                  <el-skeleton-item variant="text" style="width: 50%; height: 20px" />
                  <el-skeleton-item variant="text" style="width: 20%; height: 16px" />
                </div>
                <el-skeleton-item variant="rect" style="width: 100%; height: 6px; border-radius: 9999px" />
              </template>
            </el-skeleton>
          </div>
        </div>
        <!-- 实际内容 -->
        <div v-else class="grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-4">
          <div
            v-for="skill in skills"
            :key="skill.id"
            class="flex flex-col p-4 bg-white/70 rounded-xl border border-slate-200/70 transition-all duration-300 skill-card dark:bg-slate-900/60 dark:border-slate-800/60 hover:-translate-y-1 hover:shadow-lg"
            :style="{ '--skill-color': skill.color || 'rgb(59, 130, 246)' }"
          >
            <div class="flex justify-between items-center mb-3">
              <div class="flex items-center min-w-0">
                <i
                  v-if="skill.icon && skill.icon.startsWith('fa')"
                  :class="skill.icon + ' mr-2 text-xl shrink-0'"
                  :style="{ color: skill.color }"
                ></i>
                <span v-else class="mr-2 text-xl shrink-0">{{ skill.icon }}</span>
                <span class="font-semibold text-slate-700 dark:text-slate-200 truncate">{{
                  skill.name
                }}</span>
              </div>
              <span class="ml-2 text-xs font-bold shrink-0" :style="{ color: skill.color }"
                >{{ skill.percentage }}%</span
              >
            </div>
            <!-- 进度条 -->
            <div class="w-full bg-slate-100 dark:bg-slate-800 rounded-full h-1.5 overflow-hidden">
              <div
                class="h-full rounded-full transition-all duration-500 ease-out"
                :style="{ width: skill.percentage + '%', backgroundColor: skill.color }"
              ></div>
            </div>
          </div>
        </div>
      </div>

      <!-- 项目展示 -->
      <div class="paper-card p-8 mb-8">
        <h2 class="mb-6 text-2xl font-semibold text-slate-900 dark:text-slate-100">
          精选项目
        </h2>
        <!-- 骨架屏 -->
        <div v-if="loadingProjects" class="grid grid-cols-1 gap-6 md:grid-cols-2">
          <div
            v-for="i in 4"
            :key="i"
            class="rounded-xl border border-slate-200/70 dark:border-slate-700/70 overflow-hidden"
          >
            <el-skeleton animated>
              <template #template>
                <el-skeleton-item
                  variant="rect"
                  style="width: 100%; height: 192px"
                />
                <div class="p-5">
                  <div class="flex justify-between items-start mb-2">
                    <el-skeleton-item
                      variant="h3"
                      style="width: 120px; height: 24px"
                    />
                    <div class="flex gap-2">
                      <el-skeleton-item
                        variant="circle"
                        style="width: 20px; height: 20px"
                      />
                      <el-skeleton-item
                        variant="circle"
                        style="width: 20px; height: 20px"
                      />
                    </div>
                  </div>
                  <el-skeleton-item
                    variant="text"
                    style="width: 100%"
                    class="mb-2"
                  />
                  <el-skeleton-item
                    variant="text"
                    style="width: 70%"
                    class="mb-4"
                  />
                  <div class="flex gap-2">
                    <el-skeleton-item
                      v-for="j in 3"
                      :key="j"
                      variant="rect"
                      style="width: 50px; height: 22px; border-radius: 4px"
                    />
                  </div>
                </div>
              </template>
            </el-skeleton>
          </div>
        </div>
        <!-- 实际内容 -->
        <div v-else class="grid grid-cols-1 gap-6 md:grid-cols-2">
          <div
            v-for="project in projects"
            :key="project.id"
            class="paper-card paper-card-hover overflow-hidden group"
          >
            <div class="overflow-hidden relative h-48">
              <div
                class="absolute inset-0 z-10 transition-colors bg-primary-900/10 group-hover:bg-transparent"
              ></div>
              <img
                :src="project.coverImage || 'https://picsum.photos/600/300'"
                :alt="project.title"
                class="object-cover w-full h-full transition-transform duration-500 transform group-hover:scale-105 motion-reduce:transform-none"
              />
            </div>
            <div class="p-5 bg-white/70 dark:bg-slate-900/70">
              <div class="flex justify-between items-start mb-2">
                <h3
                  class="text-lg font-semibold text-slate-900 transition-colors dark:text-slate-100 group-hover:text-primary-600"
                >
                  {{ project.title }}
                </h3>
                <div class="flex gap-2">
                  <a
                    v-if="project.githubUrl"
                    :href="project.githubUrl"
                    target="_blank"
                    class="text-slate-400 hover:text-slate-900 dark:hover:text-white"
                  >
                    <el-icon><Link /></el-icon>
                  </a>
                  <a
                    v-if="project.previewUrl"
                    :href="project.previewUrl"
                    target="_blank"
                    class="text-slate-400 hover:text-slate-900 dark:hover:text-white"
                  >
                    <el-icon><View /></el-icon>
                  </a>
                </div>
              </div>
              <p
                class="mb-4 text-sm text-slate-600 dark:text-slate-400 line-clamp-2"
              >
                {{ project.description }}
              </p>
              <div class="flex flex-wrap gap-2">
                <span
                  v-for="tag in project.techStack
                    ? project.techStack.split(',')
                    : []"
                  :key="tag"
                  class="px-2 py-0.5 text-xs text-slate-600 bg-slate-100/80 rounded dark:bg-slate-800/60 dark:text-slate-300"
                >
                  {{ tag.trim() }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 时间线 -->
      <div class="paper-card p-8">
        <h2 class="mb-6 text-2xl font-semibold text-slate-900 dark:text-slate-100">
          成长历程
        </h2>
        <!-- 骨架屏 -->
        <div v-if="loadingTimelines" class="space-y-6">
          <el-skeleton animated>
            <template #template>
              <div
                v-for="i in 4"
                :key="i"
                class="flex gap-4"
              >
                <div class="flex flex-col items-center">
                  <el-skeleton-item
                    variant="circle"
                    style="width: 16px; height: 16px"
                  />
                  <div class="w-0.5 h-12 bg-slate-200 dark:bg-slate-700 mt-2" />
                </div>
                <div class="flex-1 pb-4">
                  <div class="flex items-center gap-4 mb-2">
                    <el-skeleton-item variant="text" style="width: 60px" />
                    <el-skeleton-item variant="h4" style="width: 150px; height: 20px" />
                  </div>
                  <el-skeleton-item variant="text" style="width: 90%" />
                </div>
              </div>
            </template>
          </el-skeleton>
        </div>
        <!-- 实际内容 -->
        <el-timeline v-else>
          <el-timeline-item
            v-for="item in timelines"
            :key="item.id"
            :timestamp="item.year"
            placement="top"
            :type="['primary', 'success', 'warning', 'danger', 'info'].includes(item.color || '') ? item.color : ''"
            :color="!['primary', 'success', 'warning', 'danger', 'info'].includes(item.color || '') ? (item.color || '#e5e7eb') : ''"
            :icon="item.icon ? (item.icon.includes('fa') ? '' : item.icon) : ''"
          >
            <div class="group">
              <h4
                class="mb-1 font-semibold text-slate-900 transition-colors dark:text-slate-100 group-hover:text-primary-600"
              >
                {{ item.title }}
              </h4>
              <p class="text-sm text-slate-600 dark:text-slate-400">
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

// 加载状态
const loadingSkills = ref(true);
const loadingProjects = ref(true);
const loadingTimelines = ref(true);

// 技能数据
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
  } finally {
    loadingSkills.value = false;
  }
};

const fetchProjects = async () => {
  try {
    projects.value = await siteApi.getFeaturedProjects();
  } catch (e) {
    console.error("获取项目失败", e);
  } finally {
    loadingProjects.value = false;
  }
};

const fetchTimelines = async () => {
  try {
    timelines.value = await siteApi.getTimelines();
  } catch (e) {
    console.error("获取时间线失败", e);
  } finally {
    loadingTimelines.value = false;
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
.skill-card {
  user-select: none;
}
.skill-card:hover {
  border-color: var(--skill-color) !important;
  box-shadow: 0 4px 12px -2px var(--skill-color) !important;
}
</style>
