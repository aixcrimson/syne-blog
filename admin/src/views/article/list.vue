<template>
  <div class="article-list p-6">
    <!-- 页面标题 -->
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-800">文章管理</h1>
      <p class="text-gray-500 mt-1">管理博客文章内容</p>
    </div>

    <!-- 搜索和筛选区域 -->
    <div class="glass-card p-4 mb-6 rounded-lg">
      <div class="flex flex-wrap items-center gap-4" :class="isMobile ? 'flex-col items-stretch' : ''">
        <!-- 关键词搜索 -->
        <el-input
          v-model="searchParams.keyword"
          placeholder="搜索文章标题"
          clearable
          :class="isMobile ? 'w-full' : 'w-64'"
          :prefix-icon="Search"
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        />
        
        <div class="flex gap-4" :class="isMobile ? 'w-full' : ''">
          <!-- 分类筛选 -->
          <el-select
            v-model="searchParams.categoryId"
            placeholder="选择分类"
            clearable
            :class="isMobile ? 'flex-1' : 'w-40'"
            @change="handleSearch"
          >
            <el-option
              v-for="category in categoryList"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
          
          <!-- 状态筛选 -->
          <el-select
            v-model="searchParams.status"
            placeholder="选择状态"
            clearable
            :class="isMobile ? 'flex-1' : 'w-32'"
            @change="handleSearch"
          >
            <el-option
              v-for="option in statusOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </div>
        
        <div class="flex gap-2" :class="isMobile ? 'w-full' : ''">
          <!-- 搜索按钮 -->
          <el-button type="primary" :icon="Search" @click="handleSearch" :class="isMobile ? 'flex-1' : ''">
            搜索
          </el-button>
          
          <!-- 重置按钮 -->
          <el-button @click="handleReset" :class="isMobile ? 'flex-1' : ''">重置</el-button>
        </div>
        
        <!-- 新建按钮 -->
        <el-button 
          type="primary" 
          :icon="Plus" 
          class="ml-auto"
          :class="isMobile ? 'w-full ml-0 mt-2' : ''"
          @click="handleCreate"
        >
          新建文章
        </el-button>
      </div>
    </div>

    <!-- 文章列表表格 -->
    <div class="glass-card rounded-lg overflow-hidden">
      <el-table
        v-loading="loading"
        :data="articleList"
        stripe
        style="width: 100%"
      >
        <!-- 标题列 -->
        <el-table-column label="标题" min-width="200">
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <!-- 置顶/推荐只在桌面端显示完整标签，移动端简化或隐藏 -->
              <el-tag v-if="row.isTop === 1 && !isMobile" type="danger" size="small">置顶</el-tag>
              <el-tag v-if="row.isRecommend === 1 && !isMobile" type="warning" size="small">推荐</el-tag>
              
              <!-- 移动端用小圆点表示状态 -->
              <div v-if="isMobile && (row.isTop === 1 || row.isRecommend === 1)" class="flex gap-1">
                <div v-if="row.isTop === 1" class="w-1.5 h-1.5 rounded-full bg-red-500"></div>
                <div v-if="row.isRecommend === 1" class="w-1.5 h-1.5 rounded-full bg-yellow-500"></div>
              </div>

              <!-- 标题文本 -->
              <span 
                class="text-gray-800 hover:text-primary-500 cursor-pointer truncate"
                :title="row.title"
                @click="handleEdit(row)"
              >
                {{ row.title }}
              </span>
            </div>
          </template>
        </el-table-column>
        
        <!-- 分类列 (移动端隐藏) -->
        <el-table-column v-if="!isMobile" label="分类" width="120">
          <template #default="{ row }">
            <el-tag type="info" size="small">
              {{ row.categoryName || '未分类' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <!-- 状态列 -->
        <el-table-column label="状态" :width="isMobile ? 80 : 100" align="center">
          <template #default="{ row }">
            <el-dropdown trigger="click" @command="(cmd: ArticleStatus) => handleStatusChange(row, cmd)">
              <el-tag 
                :type="getStatusType(row.status)" 
                class="cursor-pointer"
                :size="isMobile ? 'small' : 'default'"
              >
                {{ getStatusLabel(row.status) }}
              </el-tag>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item 
                    v-for="option in statusOptions"
                    :key="option.value"
                    :command="option.value"
                  >
                    {{ option.label }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
        
        <!-- 浏览量列 (移动端隐藏) -->
        <el-table-column v-if="!isMobile" label="浏览量" width="100" align="center">
          <template #default="{ row }">
            <span class="text-gray-600">{{ row.views || 0 }}</span>
          </template>
        </el-table-column>

        <!-- 点赞列 (移动端隐藏) -->
        <el-table-column v-if="!isMobile" label="点赞" width="80" align="center">
          <template #default="{ row }">
            <span class="text-gray-600">{{ row.likes || 0 }}</span>
          </template>
        </el-table-column>

        <!-- 收藏列 (移动端隐藏) -->
        <el-table-column v-if="!isMobile" label="收藏" width="80" align="center">
          <template #default="{ row }">
            <span class="text-gray-600">{{ row.favorites || 0 }}</span>
          </template>
        </el-table-column>
        
        <!-- 发布时间列 (移动端隐藏) -->
        <el-table-column v-if="!isMobile" label="发布时间" width="170">
          <template #default="{ row }">
            <span class="text-gray-500 text-sm">
              {{ formatDate(row.publishedTime) }}
            </span>
          </template>
        </el-table-column>
        
        <!-- 操作列 -->
        <el-table-column label="操作" :width="isMobile ? 120 : 220" fixed="right">
          <template #default="{ row }">
            <div class="flex items-center gap-1 justify-end">
              <!-- 移动端只显示编辑和更多 -->
              <template v-if="isMobile">
                <el-button type="primary" :icon="Edit" size="small" circle @click="handleEdit(row)" />
                <el-dropdown trigger="click">
                  <el-button type="info" :icon="More" size="small" circle />
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item @click="handleToggleTop(row)">
                        {{ row.isTop === 1 ? '取消置顶' : '置顶' }}
                      </el-dropdown-item>
                      <el-dropdown-item @click="handleToggleRecommend(row)">
                        {{ row.isRecommend === 1 ? '取消推荐' : '推荐' }}
                      </el-dropdown-item>
                      <el-dropdown-item divided class="text-red-500" @click="handleDelete(row)">
                        删除
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </template>
              
              <!-- 桌面端显示全部按钮 -->
              <template v-else>
                <!-- 置顶按钮 -->
                <el-tooltip :content="row.isTop === 1 ? '取消置顶' : '置顶'">
                  <el-button
                    :type="row.isTop === 1 ? 'warning' : 'default'"
                    :icon="Top"
                    size="small"
                    circle
                    @click="handleToggleTop(row)"
                  />
                </el-tooltip>
                
                <!-- 推荐按钮 -->
                <el-tooltip :content="row.isRecommend === 1 ? '取消推荐' : '推荐'">
                  <el-button
                    :type="row.isRecommend === 1 ? 'warning' : 'default'"
                    :icon="row.isRecommend === 1 ? StarFilled : Star"
                    size="small"
                    circle
                    @click="handleToggleRecommend(row)"
                  />
                </el-tooltip>
                
                <!-- 编辑按钮 -->
                <el-tooltip content="编辑">
                  <el-button
                    type="primary"
                    :icon="Edit"
                    size="small"
                    circle
                    @click="handleEdit(row)"
                  />
                </el-tooltip>
                
                <!-- 删除按钮 -->
                <el-tooltip content="删除">
                  <el-button
                    type="danger"
                    :icon="Delete"
                    size="small"
                    circle
                    @click="handleDelete(row)"
                  />
                </el-tooltip>
              </template>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="flex justify-end p-4 border-t border-gray-100">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          :layout="isMobile ? 'prev, pager, next' : 'total, sizes, prev, pager, next, jumper'"
          :small="isMobile"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 文章列表页面
 * 实现文章的分页展示、搜索筛选、状态切换等功能
 * @requirements 5.1, 5.2, 5.3, 5.4, 5.5, 5.6, 5.7, 5.8, 5.9
 */
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Search, 
  Plus, 
  Edit, 
  Delete,
  Top,
  Star,
  StarFilled,
  More
} from '@element-plus/icons-vue'
import { articleApi } from '@/api/article'
import { categoryApi } from '@/api/category'
import type { Article, ArticleListParams, Category } from '@/types'
import { ArticleStatus } from '@/types'
import dayjs from 'dayjs'
import { useResponsive } from '@/utils/useResponsive'

// 响应式状态
const { isMobile } = useResponsive()

// ==================== 路由 ====================
const router = useRouter()

// ==================== 状态 ====================

/** 加载状态 */
const loading = ref(false)

/** 文章列表 */
const articleList = ref<Article[]>([])

/** 分页信息 */
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

/** 搜索和筛选参数 */
const searchParams = reactive({
  keyword: '',
  categoryId: undefined as number | undefined,
  status: undefined as ArticleStatus | undefined
})

/** 分类列表（用于筛选下拉框） */
const categoryList = ref<Category[]>([])

// ==================== 常量配置 ====================

/** 文章状态选项 */
const statusOptions = [
  { value: ArticleStatus.PUBLISHED, label: '已发布', type: 'success' },
  { value: ArticleStatus.DRAFT, label: '草稿', type: 'info' },
  { value: ArticleStatus.OFFLINE, label: '已下架', type: 'danger' }
]

// ==================== 计算属性 ====================

/**
 * 获取状态标签类型
 */
const getStatusType = (status: ArticleStatus) => {
  const option = statusOptions.find(opt => opt.value === status)
  return option?.type || 'info'
}

/**
 * 获取状态标签文本
 */
const getStatusLabel = (status: ArticleStatus) => {
  const option = statusOptions.find(opt => opt.value === status)
  return option?.label || '未知'
}

// ==================== 方法 ====================

/**
 * 格式化日期
 */
const formatDate = (date: string) => {
  if (!date) return '-'
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

/**
 * 加载分类筛选列表
 */
const loadCategoryList = async () => {
  try {
    const result = await categoryApi.getList()
    categoryList.value = result
  } catch (error) {
    console.error('加载分类列表失败:', error)
  } 
}

/**
 * 加载文章列表
 */
const loadArticleList = async () => {
  loading.value = true
  try {
    const params: ArticleListParams = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      keyword: searchParams.keyword || undefined,
      categoryId: searchParams.categoryId,
      status: searchParams.status
    }
    
    const result = await articleApi.getList(params)
    articleList.value = result.list
    pagination.total = result.total
  } catch (error) {
    console.error('加载文章列表失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 处理搜索
 */
const handleSearch = () => {
  pagination.page = 1
  loadArticleList()
}

/**
 * 重置搜索条件
 */
const handleReset = () => {
  searchParams.keyword = ''
  searchParams.categoryId = undefined
  searchParams.status = undefined
  pagination.page = 1
  loadArticleList()
}

/**
 * 处理分页变化
 */
const handlePageChange = (page: number) => {
  pagination.page = page
  loadArticleList()
}

/**
 * 处理每页数量变化
 */
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.page = 1
  loadArticleList()
}

/**
 * 跳转到新建文章页面
 * @requirements 5.4
 */
const handleCreate = () => {
  router.push('/article/create')
}

/**
 * 跳转到编辑文章页面
 * @requirements 5.5
 */
const handleEdit = (article: Article) => {
  router.push(`/article/edit/${article.id}`)
}

/**
 * 删除文章
 * @requirements 5.6, 5.7
 */
const handleDelete = async (article: Article) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除文章「${article.title}」吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await articleApi.delete(article.id)
    ElMessage.success('删除成功')
    loadArticleList()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除文章失败:', error)
    }
  }
}

/**
 * 切换置顶状态
 * @requirements 5.8
 */
const handleToggleTop = async (article: Article) => {
  try {
    await articleApi.toggleTop(article.id)
    const newStatus = article.isTop === 1 ? '取消置顶' : '置顶'
    ElMessage.success(`${newStatus}成功`)
    loadArticleList()
  } catch (error) {
    console.error('切换置顶状态失败:', error)
  }
}

/**
 * 切换推荐状态
 * @requirements 5.8
 */
const handleToggleRecommend = async (article: Article) => {
  try {
    await articleApi.toggleRecommend(article.id)
    const newStatus = article.isRecommend === 1 ? '取消推荐' : '推荐'
    ElMessage.success(`${newStatus}成功`)
    loadArticleList()
  } catch (error) {
    console.error('切换推荐状态失败:', error)
  }
}

/**
 * 更新文章状态
 * @requirements 5.9
 */
const handleStatusChange = async (article: Article, newStatus: ArticleStatus) => {
  try {
    await articleApi.updateStatus(article.id, newStatus)
    ElMessage.success('状态更新成功')
    loadArticleList()
  } catch (error) {
    console.error('更新状态失败:', error)
  }
}

// ==================== 生命周期 ====================

onMounted(() => {
  loadCategoryList()
  loadArticleList()
})
</script>


<style scoped>
/* 主题色悬停效果 */
.hover\:text-primary-500:hover {
  color: var(--color-primary-500);
}

/* 表格行悬停效果 */
:deep(.el-table__row:hover) {
  background-color: rgba(var(--color-primary-50), 0.5);
}

/* 标签样式优化 */
:deep(.el-tag) {
  border-radius: 4px;
}

/* 下拉菜单中的标签可点击样式 */
.cursor-pointer {
  cursor: pointer;
}
</style>
