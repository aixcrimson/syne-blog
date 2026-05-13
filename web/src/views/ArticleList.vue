<template>
  <div class="article-list py-12">
    <div class="max-w-[1480px] mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex flex-col gap-6 xl:grid xl:grid-cols-[minmax(0,900px)_260px] xl:justify-center">



        <!-- 主内容区 -->
        <div class="min-w-0 xl:w-full xl:max-w-[900px]">

          <!-- 顶部公告栏 -->
          <div class="mb-8 overflow-hidden rounded-2xl bg-white/60 dark:bg-slate-800/60 border border-slate-200/50 dark:border-slate-700/50 shadow-sm backdrop-blur-md transition-all hover:shadow-md">
            <div class="flex items-center p-4 sm:px-6">
              <div class="flex-shrink-0 flex items-center justify-center w-10 h-10 rounded-full bg-primary-50 dark:bg-primary-900/30 text-primary-500 dark:text-primary-400 mr-4">
                <el-icon :size="20"><Bell /></el-icon>
              </div>
              <div class="flex-1 min-w-0 text-base sm:text-lg text-slate-700 dark:text-slate-300 font-medium tracking-wide" style="font-family: 'Georgia', 'Times New Roman', serif;">
                <Typewriter
                  v-if="notices.length > 0"
                  :texts="noticeTexts"
                  :type-speed="150"
                  :delete-speed="80"
                  :pause-time="2000"
                />
                <span v-else class="animate-pulse">正在获取宇宙信号...</span>
              </div>
            </div>
          </div>

          <!-- 文章列表 (动态布局) -->
          <div 
            class="grid gap-6 mb-8 transition-all duration-300"
            :class="appStore.articleListLayout === 'grid' ? 'grid-cols-1 md:grid-cols-2' : 'grid-cols-1'"
          >
            <!-- 骨架屏 -->
            <template v-if="loading">
              <ArticleCardSkeleton 
                v-for="i in pageSize" 
                :key="i"
                :layout="appStore.articleListLayout"
              />
            </template>

            <!-- 文章卡片 -->
            <ArticleCard
              v-for="article in articles"
              v-else
              :key="article.id"
              :article="article"
              :layout="appStore.articleListLayout"
            />
          </div>

          <!-- 空状态 -->
          <el-empty
            v-if="articles.length === 0 && !loading"
            description="暂无文章"
          />

          <!-- 分页 -->
          <div class="flex justify-center w-full overflow-hidden" v-if="totalPages > 1">
            <!-- 桌面端分页 -->
            <el-pagination
              class="hidden sm:flex"
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="totalArticles"
              :page-sizes="[6, 12, 24, 48]"
              layout="total, sizes, prev, pager, next, jumper"
              @current-change="handlePageChange"
              @size-change="handleSizeChange"
            />
            <!-- 移动端分页 -->
            <el-pagination
              class="flex sm:hidden"
              v-model:current-page="currentPage"
              :page-size="pageSize"
              :total="totalArticles"
              small
              layout="prev, pager, next"
              @current-change="handlePageChange"
            />
          </div>
        </div>

        <!-- 侧边栏 (桌面端) -->
        <Sidebar
          class="hidden xl:block xl:w-[260px]"
          :selected-tag-ids="selectedTagIds"
          @category-click="handleCategorySelect"
          @tag-click="handleTagSelect"
        />
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, shallowRef, watch, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { Bell } from "@element-plus/icons-vue";
import { useAppStore } from "@/stores/app";
import ArticleCard from "@/components/ArticleCard.vue";
import ArticleCardSkeleton from "@/components/ArticleCardSkeleton.vue";
import Sidebar from "@/components/Sidebar.vue";
import Typewriter from "@/components/Typewriter.vue";
import { articleApi } from "@/api/article";
import { siteApi } from "@/api/site";
import type { Article, Notice } from "@/types";

const route = useRoute();
const router = useRouter();
const appStore = useAppStore();

const currentPage = ref(1);
const pageSize = ref(6);
const searchKeyword = ref("");
const selectedCategory = ref<number | string>("");
const selectedTagIds = ref<number[]>([]);
const loading = ref(false);

const articles = shallowRef<Article[]>([]);
const totalArticles = ref(0);

// 公告 (打字机数据)
const notices = ref<Notice[]>([]);
const noticeTexts = computed(() => notices.value.map((n) => n.content));

const getNotices = async () => {
  try {
    notices.value = await siteApi.getNotices();
  } catch (error) {
    console.error("获取公告失败:", error);
  }
};

/**
 * 将当前筛选/分页状态同步到 URL query 参数（使用 replace 不产生新历史记录）
 */
const syncQueryToUrl = () => {
  const query: Record<string, string> = {};
  if (currentPage.value > 1) query.page = String(currentPage.value);
  if (pageSize.value !== 6) query.pageSize = String(pageSize.value);
  if (selectedCategory.value) query.category = String(selectedCategory.value);
  if (selectedTagIds.value.length > 0) query.tag = selectedTagIds.value.join(',');
  if (searchKeyword.value) query.keyword = searchKeyword.value;

  router.replace({ path: '/articles', query });
};

// 获取文章列表
const loadArticles = async () => {
  loading.value = true;
  try {
    // 构建请求参数
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value || undefined,
      categoryId: selectedCategory.value || undefined,
      tagIds: selectedTagIds.value.length > 0 ? selectedTagIds.value.join(',') : undefined,
    };

    const res = await articleApi.getList(params);
    articles.value = res.list;
    totalArticles.value = res.total;
  } catch (error) {
    console.error("获取文章列表失败:", error);
  } finally {
    loading.value = false;
  }
};

// 计算总页数
const totalPages = ref(0);
watch(totalArticles, () => {
  totalPages.value = Math.ceil(totalArticles.value / pageSize.value);
});

const handleCategorySelect = (id: number) => {
  selectedCategory.value = id;
  selectedTagIds.value = []; // 切换分类时重置标签
  currentPage.value = 1;
  window.scrollTo({ top: 0, behavior: "smooth" });
};

const handleTagSelect = (id: number) => {
  const index = selectedTagIds.value.indexOf(id);
  if (index > -1) {
    selectedTagIds.value.splice(index, 1);
  } else {
    selectedTagIds.value.push(id);
  }
  selectedCategory.value = ""; // 切换标签时重置分类
  currentPage.value = 1;
  window.scrollTo({ top: 0, behavior: "smooth" });
};

const handlePageChange = (page: number) => {
  currentPage.value = page;
  window.scrollTo({ top: 0, behavior: "smooth" });
};

const handleSizeChange = (size: number) => {
  pageSize.value = size;
  currentPage.value = 1;
};

// 监听过滤条件变化 → 加载数据 + 同步 URL
watch([currentPage, pageSize, searchKeyword, selectedCategory, selectedTagIds], () => {
  loadArticles();
  syncQueryToUrl();
}, { deep: true });

// 监听路由参数变化，实现通过全局抽屉点击分类/标签时更新列表
watch(() => route.query, (newQuery) => {
  if (route.path !== '/articles') return;

  // 防止自身 syncQueryToUrl 触发的变化导致重复加载
  const qPage = Number(newQuery.page) || 1;
  const qPageSize = Number(newQuery.pageSize) || 6;
  const qCategory = newQuery.category ? Number(newQuery.category) : "";
  const qKeyword = (newQuery.keyword as string) || "";
  const qTags = newQuery.tag
    ? String(newQuery.tag).split(',').map(Number).filter(n => !isNaN(n))
    : [];

  // 仅在外部导航（如抽屉点击分类）时更新状态
  if (
    qPage !== currentPage.value ||
    qPageSize !== pageSize.value ||
    qCategory !== selectedCategory.value ||
    qKeyword !== searchKeyword.value ||
    JSON.stringify(qTags) !== JSON.stringify(selectedTagIds.value)
  ) {
    currentPage.value = qPage;
    pageSize.value = qPageSize;
    selectedCategory.value = qCategory;
    searchKeyword.value = qKeyword;
    selectedTagIds.value = qTags;
  }
}, { deep: true });

/**
 * 初始化：从 URL query 恢复状态
 */
onMounted(() => {
  if (route.query.page) currentPage.value = Number(route.query.page) || 1;
  if (route.query.pageSize) pageSize.value = Number(route.query.pageSize) || 6;
  if (route.query.category) selectedCategory.value = Number(route.query.category);
  if (route.query.keyword) searchKeyword.value = route.query.keyword as string;
  if (route.query.tag) {
    selectedTagIds.value = String(route.query.tag).split(',').map(Number).filter(n => !isNaN(n));
  }
  loadArticles();
  getNotices();
});
</script>

