<template>
  <div class="home bg-transparent">
    <!-- Hero Section -->
    <section class="hero bg-transparent text-white h-screen">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20">
        <div class="text-center">
          <h1 class="text-4xl md:text-5xl font-bold mb-4 text-shadow">
            Syne's Blog
          </h1>
          <p class="text-xl md:text-2xl mb-8 text-shadow">分享技术，记录成长</p>
          <div class="flex justify-center space-x-4">
            <el-button
              type="primary"
              size="large"
              @click="router.push('/articles')"
            >
              浏览文章
            </el-button>
            <el-button size="large" @click="router.push('/about')">
              关于我
            </el-button>
          </div>
        </div>
      </div>
    </section>

    <!-- 内容 -->
    <section class="py-16 bg-transparent">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <!-- 侧边栏 -->
          <aside class="lg:col-span-1">
            <div class="flex flex-col gap-6 sticky top-20">
              <!-- 公告栏 -->
              <div class="notice-card rounded-xl p-5 shadow-sm border border-primary-100">
                <div class="flex items-center gap-2 mb-3">
                  <div class="w-2 h-2 bg-primary-600 rounded-full animate-pulse"></div>
                  <p class="font-bold text-primary-600 text-lg">公告栏</p>
                </div>
                <p class="text-sm text-gray-700 leading-relaxed">📢 天行健，君子以自强不息</p>
              </div>

              <!-- 作者资料 -->
              <div class="author-card glass-section rounded-xl p-6 shadow-sm">
                <div class="flex flex-col items-center text-center">
                  <!-- 头像 -->
                  <div class="relative mb-4">
                    <img
                      :src="appStore.userInfo.avatar"
                      :alt="appStore.userInfo.name"
                      class="w-24 h-24 rounded-full border-4 border-white shadow-lg"
                    />
                    <div class="absolute -bottom-1 -right-1 w-6 h-6 bg-green-500 rounded-full border-2 border-white"></div>
                  </div>
                  
                  <!-- 名称 -->
                  <h3 class="text-xl font-bold text-gray-900 mb-2">{{ appStore.userInfo.name }}</h3>
                  <p class="text-sm text-gray-600 mb-4">{{ appStore.userInfo.bio || '热爱技术，热爱分享' }}</p>
                  
                  <!-- 统计信息 -->
                  <div class="w-full grid grid-cols-3 gap-3 mb-4">
                    <div class="stat-item">
                      <div class="text-2xl font-bold text-primary-600">{{ articleStore.totalArticles }}</div>
                      <div class="text-xs text-gray-600 mt-1">文章</div>
                    </div>
                    <div class="stat-item">
                      <div class="text-2xl font-bold text-primary-600">{{ articleStore.getAllCategories.length }}</div>
                      <div class="text-xs text-gray-600 mt-1">分类</div>
                    </div>
                    <div class="stat-item">
                      <div class="text-2xl font-bold text-primary-600">{{ totalViews }}</div>
                      <div class="text-xs text-gray-600 mt-1">浏览</div>
                    </div>
                  </div>

                  <!-- GitHub 按钮 -->
                  <a
                    v-if="appStore.userInfo.github"
                    :href="appStore.userInfo.github"
                    target="_blank"
                    rel="noopener noreferrer"
                    class="w-full"
                  >
                    <el-button type="primary" class="w-full" size="default">
                      <span class="flex items-center justify-center gap-2">
                        <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                          <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/>
                        </svg>
                        GitHub
                      </span>
                    </el-button>
                  </a>
                </div>
              </div>

            </div>
          </aside>

          <!-- 最新文章 -->
          <div class="lg:col-span-3">
            <div class="flex justify-between items-center mb-6">
              <h2 class="text-3xl font-bold text-gray-900 flex items-center gap-3">
                <span class="w-1 h-8 bg-primary-600 rounded-full"></span>
                最新文章
              </h2>
              <el-button text type="primary" @click="router.push('/articles')" class="text-base">
                查看全部 →
              </el-button>
            </div>
            
            <div class="space-y-6">
              <ArticleCard
                v-for="article in latestArticles"
                :key="article.id"
                :article="article"
              />
            </div>
          </div>
        </div>
      </div>
    </section>

  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useRouter } from "vue-router";
import { useArticleStore } from "@/stores/article";
import { useAppStore } from "@/stores/app";
import ArticleCard from "@/components/ArticleCard.vue";

const router = useRouter();
const articleStore = useArticleStore();
const appStore = useAppStore()

// 最新文章
const latestArticles = computed(() => articleStore.latestArticles.slice(0, 6));

// 总浏览量
const totalViews = computed(() => {
  return articleStore.articles.reduce((sum, article) => sum + article.views, 0);
});
</script>

<style scoped>
/* 文字阴影让文字在背景上更清晰 */
.text-shadow {
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
}

/* 毛玻璃卡片 */
.glass-section {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

/* 公告栏 */
.notice-card {
  background: linear-gradient(135deg, var(--color-primary-50) 0%, rgba(255, 255, 255, 0.9) 100%);
  transition: all 0.3s ease;
}

.notice-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

/* 作者卡片 */
.author-card {
  transition: all 0.3s ease;
}

.author-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

/* 统计项 */
.stat-item {
  transition: transform 0.2s ease;
}

.stat-item:hover {
  transform: scale(1.1);
}
</style>
