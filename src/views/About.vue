<template>
  <div class="about py-12">
    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- 个人信息卡片 -->
      <div class="bg-white rounded-lg shadow-sm overflow-hidden mb-8">
        <div class="bg-gradient-to-r from-primary-600 to-primary-800 h-32"></div>
        <div class="px-8 pb-8">
          <div class="flex flex-col items-center -mt-16 mb-6">
            <img
              :src="userInfo.avatar"
              :alt="userInfo.name"
              class="w-32 h-32 rounded-full border-4 border-white shadow-lg mb-4"
            />
            <h1 class="text-3xl font-bold text-gray-900 mb-2">
              {{ userInfo.name }}
            </h1>
            <p class="text-gray-600 text-center max-w-md">
              {{ userInfo.bio }}
            </p>
          </div>
          
          <!-- 联系方式 -->
          <div class="flex justify-center space-x-4 mb-6">
            <el-button type="primary" @click="handleEmail">
              <el-icon class="mr-2"><Message /></el-icon>
              发送邮件
            </el-button>
            <el-button v-if="userInfo.github" @click="openLink(userInfo.github!)">
              <el-icon class="mr-2"><Link /></el-icon>
              GitHub
            </el-button>
            <el-button v-if="userInfo.twitter" @click="openLink(userInfo.twitter!)">
              <el-icon class="mr-2"><Link /></el-icon>
              Twitter
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- 技能栈 -->
      <div class="bg-white rounded-lg shadow-sm p-8 mb-8">
        <h2 class="text-2xl font-bold text-gray-900 mb-6">技能栈</h2>
        <div class="grid grid-cols-2 md:grid-cols-3 gap-4">
          <div
            v-for="skill in skills"
            :key="skill.name"
            class="skill-card p-4 border border-gray-200 rounded-lg hover:border-primary-500 transition-colors"
          >
            <div class="text-center">
              <div class="text-2xl mb-2">{{ skill.icon }}</div>
              <div class="font-medium text-gray-900">{{ skill.name }}</div>
              <el-progress
                :percentage="skill.level"
                :color="skill.level >= 80 ? '#67C23A' : '#409EFF'"
                :show-text="false"
                class="mt-2"
              />
            </div>
          </div>
        </div>
      </div>
      
      <!-- 关于博客 -->
      <div class="bg-white rounded-lg shadow-sm p-8 mb-8">
        <h2 class="text-2xl font-bold text-gray-900 mb-6">关于博客</h2>
        <div class="text-gray-600 max-w-none">
          <p class="text-gray-600 leading-7 mb-4">
            这是我的个人技术博客，用于记录和分享我在前端开发过程中的学习心得、项目经验和技术思考。
          </p>
          <p class="text-gray-600 leading-7 mb-4">
            博客主要关注以下技术领域：
          </p>
          <ul class="list-disc list-inside text-gray-600 space-y-2 mb-4">
            <li>Vue.js / React 等前端框架</li>
            <li>TypeScript / JavaScript</li>
            <li>CSS / Tailwind CSS 等样式方案</li>
            <li>前端工程化与性能优化</li>
            <li>Web 新技术探索</li>
          </ul>
          <p class="text-gray-600 leading-7">
            希望通过博客的方式，与大家一起交流学习，共同进步。如果你对文章有任何疑问或建议，欢迎通过邮件与我联系！
          </p>
        </div>
      </div>
      
      <!-- 时间线 -->
      <div class="bg-white rounded-lg shadow-sm p-8">
        <h2 class="text-2xl font-bold text-gray-900 mb-6">成长历程</h2>
        <el-timeline>
          <el-timeline-item
            v-for="item in timeline"
            :key="item.date"
            :timestamp="item.date"
            placement="top"
          >
            <el-card>
              <h4 class="font-semibold text-gray-900 mb-2">{{ item.title }}</h4>
              <p class="text-gray-600 text-sm">{{ item.content }}</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAppStore } from '@/stores/app'
import { Message, Link } from '@element-plus/icons-vue'

const appStore = useAppStore()
const userInfo = computed(() => appStore.userInfo)

const skills = [
  { name: 'Vue.js', icon: '💚', level: 90 },
  { name: 'TypeScript', icon: '💙', level: 85 },
  { name: 'JavaScript', icon: '💛', level: 95 },
  { name: 'React', icon: '⚛️', level: 75 },
  { name: 'CSS/SCSS', icon: '🎨', level: 88 },
  { name: 'Tailwind CSS', icon: '🌊', level: 92 },
  { name: 'Node.js', icon: '🟢', level: 70 },
  { name: 'Git', icon: '📦', level: 85 },
  { name: 'Webpack/Vite', icon: '⚡', level: 80 }
]

const timeline = [
  {
    date: '2024-02',
    title: '博客上线',
    content: '个人博客正式上线，开始记录技术学习历程'
  },
  {
    date: '2023-10',
    title: '深入学习 Vue 3',
    content: '系统学习 Vue 3 Composition API 和 TypeScript'
  },
  {
    date: '2023-06',
    title: '开始前端之旅',
    content: '正式踏入前端开发领域，学习 HTML/CSS/JavaScript 基础'
  }
]

const handleEmail = () => {
  window.location.href = `mailto:${userInfo.value.email}`
}

const openLink = (url: string) => {
  window.open(url, '_blank')
}
</script>

<style scoped>
.skill-card {
  transition: all 0.3s ease;
}

.skill-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
</style>

