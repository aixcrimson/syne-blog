<template>
  <div class="article-list py-12">
    <div class="max-w-[1480px] mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex flex-col gap-6 xl:grid xl:grid-cols-[minmax(0,900px)_260px] xl:justify-center">

        <!-- 移动端筛选按钮 -->
        <div class="mb-4 xl:hidden">
          <el-button
            type="primary"
            class="w-full"
            @click="drawerVisible = true"
          >
            <el-icon class="mr-2"><Filter /></el-icon>
            筛选与分类
          </el-button>
        </div>

        <!-- 侧边栏抽屉 (移动端) -->

        <el-drawer
          v-model="drawerVisible"
          title="筛选与分类"
          direction="ltr"
          size="80%"
        >
          <Sidebar
            :selected-tag-ids="selectedTagIds"
            @category-click="handleCategorySelect"
            @tag-click="handleTagSelect"
          />
        </el-drawer>

        <!-- 主内容区 -->
        <div class="min-w-0 xl:w-full xl:max-w-[900px]">

          <!-- 页面标题 -->
          <div class="mb-6">
            <div class="flex items-center gap-4">
              <h1
                class="text-3xl font-semibold tracking-tight text-slate-900 dark:text-slate-100"
              >
                文章列表
              </h1>

              <span class="flex-1 h-px bg-slate-200/70 dark:bg-slate-700/60" />
            </div>
            <p class="mt-2 text-slate-600 dark:text-slate-300">
              共 {{ totalArticles }} 篇文章
            </p>
          </div>

          <!-- 搜索区 -->
          <div class="paper-card p-6 mb-6">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索文章标题或摘要..."
              clearable
              size="large"
              @input="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>

          <!-- 文章列表 -->
          <div class="space-y-6 mb-8">
            <!-- 骨架屏 -->
            <template v-if="loading">
              <ArticleCardSkeleton v-for="i in pageSize" :key="i" />
            </template>

            <!-- 文章卡片 -->
            <ArticleCard
              v-for="article in articles"
              v-else
              :key="article.id"
              :article="article"
            />
          </div>

          <!-- 空状态 -->
          <el-empty
            v-if="articles.length === 0 && !loading"
            description="暂无文章"
          />

          <!-- 分页 -->
          <div class="flex justify-center" v-if="totalPages > 1">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="totalArticles"
              :page-sizes="[6, 12, 24, 48]"
              layout="total, sizes, prev, pager, next, jumper"
              @current-change="handlePageChange"
              @size-change="handleSizeChange"
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
import { ref, watch, onMounted } from "vue";
import { useRoute } from "vue-router";
import { Search, Filter } from "@element-plus/icons-vue";
import ArticleCard from "@/components/ArticleCard.vue";
import ArticleCardSkeleton from "@/components/ArticleCardSkeleton.vue";
import Sidebar from "@/components/Sidebar.vue";
import { articleApi } from "@/api/article";
import type { Article } from "@/types";

const route = useRoute();

const drawerVisible = ref(false);

const currentPage = ref(1);
const pageSize = ref(6);
const searchKeyword = ref("");
const selectedCategory = ref<number | string>("");
const selectedTagIds = ref<number[]>([]);
const loading = ref(false);

const articles = ref<Article[]>([]);
const totalArticles = ref(0);

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

const handleSearch = () => {
  currentPage.value = 1;
  loadArticles();
};

const handleCategorySelect = (id: number) => {
  selectedCategory.value = id;
  selectedTagIds.value = []; // 切换分类时重置标签
  drawerVisible.value = false;
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
  // drawerVisible.value = false; // 多选模式下不自动关闭抽屉，方便用户连续选择
  window.scrollTo({ top: 0, behavior: "smooth" });
};

const handlePageChange = (page: number) => {
  currentPage.value = page;
  loadArticles();
  window.scrollTo({ top: 0, behavior: "smooth" });
};

const handleSizeChange = (size: number) => {
  pageSize.value = size;
  currentPage.value = 1;
  loadArticles();
};

// 监听过滤条件变化
watch([searchKeyword, selectedCategory, selectedTagIds], () => {
  currentPage.value = 1;
  loadArticles();
}, { deep: true });

onMounted(() => {
  if (route.query.category) {
    selectedCategory.value = route.query.category as string;
  }
  if (route.query.keyword) {
    searchKeyword.value = route.query.keyword as string;
  }
  if (route.query.tag) {
    const tags = String(route.query.tag).split(',').map(Number).filter(n => !isNaN(n));
    selectedTagIds.value = tags;
  }
  loadArticles();
});
</script>
