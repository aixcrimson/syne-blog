<template>
  <div class="article-list py-12 bg-transparent">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- 页面标题 -->
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900 mb-4">文章列表</h1>
        <p class="text-gray-600">共 {{ totalArticles }} 篇文章</p>
      </div>
      
      <!-- 搜索和筛选 -->
      <div class="glass-card rounded-lg shadow-sm p-6 mb-8">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索文章..."
            clearable
            @input="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          
          <el-select
            v-model="selectedCategory"
            placeholder="选择分类"
            clearable
            @change="handleFilter"
          >
            <el-option
              v-for="category in categories"
              :key="category"
              :label="category"
              :value="category"
            />
          </el-select>
          
          <el-select
            v-model="selectedTag"
            placeholder="选择标签"
            clearable
            @change="handleFilter"
          >
            <el-option
              v-for="tag in tags"
              :key="tag"
              :label="tag"
              :value="tag"
            />
          </el-select>
        </div>
      </div>
      
      <!-- 文章列表 -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
        <ArticleCard
          v-for="article in paginatedArticles.data"
          :key="article.id"
          :article="article"
        />
      </div>
      
      <!-- 空状态 -->
      <el-empty
        v-if="paginatedArticles.data.length === 0"
        description="暂无文章"
      />
      
      <!-- 分页 -->
      <div class="flex justify-center" v-if="paginatedArticles.totalPages > 1">
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
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useArticleStore } from '@/stores/article'
import { Search } from '@element-plus/icons-vue'
import ArticleCard from '@/components/ArticleCard.vue'

const articleStore = useArticleStore()

const currentPage = ref(1)
const pageSize = ref(6)
const searchKeyword = ref('')
const selectedCategory = ref('')
const selectedTag = ref('')

// 获取分类和标签
const categories = computed(() => articleStore.getAllCategories)
const tags = computed(() => articleStore.getAllTags)

// 过滤后的文章
const filteredArticles = computed(() => {
  let result = articleStore.articles

  // 搜索过滤
  if (searchKeyword.value) {
    result = articleStore.searchArticles(searchKeyword.value)
  }

  // 分类过滤
  if (selectedCategory.value) {
    result = result.filter(article => article.category === selectedCategory.value)
  }

  // 标签过滤
  if (selectedTag.value) {
    result = result.filter(article => article.tags.includes(selectedTag.value))
  }

  return result
})

const totalArticles = computed(() => filteredArticles.value.length)

// 分页后的文章
const paginatedArticles = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  const data = filteredArticles.value
    .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
    .slice(start, end)

  return {
    data,
    total: totalArticles.value,
    page: currentPage.value,
    pageSize: pageSize.value,
    totalPages: Math.ceil(totalArticles.value / pageSize.value)
  }
})

const handleSearch = () => {
  currentPage.value = 1
}

const handleFilter = () => {
  currentPage.value = 1
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
}

// 监听过滤条件变化，重置到第一页
watch([searchKeyword, selectedCategory, selectedTag], () => {
  currentPage.value = 1
})
</script>

<style scoped>
.glass-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}
</style>

