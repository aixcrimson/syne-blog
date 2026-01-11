<template>
  <div class="comment-management p-6">
    <!-- 页面标题 -->
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-800">评论管理</h1>
      <p class="text-gray-500 mt-1">审核和管理用户评论</p>
    </div>

    <!-- 筛选和操作区域 -->
    <div class="glass-card p-4 mb-6 rounded-lg">
      <div class="flex flex-wrap items-center justify-between gap-4" :class="isMobile ? 'flex-col items-stretch' : ''">
        <!-- 状态筛选 -->
        <div class="flex items-center gap-4" :class="isMobile ? 'justify-between' : ''">
          <span class="text-gray-600">状态筛选：</span>
          <el-select 
            v-model="filterStatus" 
            placeholder="全部状态" 
            clearable 
            style="width: 150px"
            :class="isMobile ? 'flex-1' : ''"
            @change="handleFilterChange"
          >
            <el-option 
              v-for="item in statusOptions" 
              :key="item.value" 
              :label="item.label" 
              :value="item.value" 
            />
          </el-select>
        </div>
        
        <!-- 批量操作按钮 -->
        <div class="flex items-center gap-2" :class="isMobile ? 'justify-end w-full' : ''">
          <el-button 
            type="success" 
            :icon="Check" 
            :disabled="selectedIds.length === 0"
            :size="isMobile ? 'small' : 'default'"
            @click="handleBatchApprove"
          >
            <span v-if="!isMobile">批量审核</span>
            <span v-else>审核</span>
             ({{ selectedIds.length }})
          </el-button>
          <el-button 
            type="danger" 
            :icon="Delete" 
            :disabled="selectedIds.length === 0"
            :size="isMobile ? 'small' : 'default'"
            @click="handleBatchDelete"
          >
            <span v-if="!isMobile">批量删除</span>
             <span v-else>删除</span>
             ({{ selectedIds.length }})
          </el-button>
        </div>
      </div>
    </div>

    <!-- 评论列表表格 -->
    <div class="glass-card rounded-lg overflow-hidden">
      <el-table 
        v-loading="loading" 
        :data="commentList" 
        stripe 
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <!-- 批量选择列 -->
        <el-table-column type="selection" width="55" />
        
        <!-- 评论内容 -->
        <el-table-column label="评论内容" min-width="250">
          <template #default="{ row }">
            <div class="text-gray-800 line-clamp-2">{{ row.content }}</div>
            <!-- 移动端显示一些额外信息 -->
            <div v-if="isMobile" class="text-xs text-gray-500 mt-1 flex items-center gap-2">
               <span>{{ row.username }}</span>
               <span>•</span>
               <span>{{ formatDate(row.createTime) }}</span>
            </div>
          </template>
        </el-table-column>
        
        <!-- 文章标题 (移动端隐藏) -->
        <el-table-column v-if="!isMobile" label="所属文章" min-width="180">
          <template #default="{ row }">
            <el-tooltip :content="row.articleTitle" placement="top">
              <span class="text-primary-600 cursor-pointer truncate block">
                {{ row.articleTitle }}
              </span>
            </el-tooltip>
          </template>
        </el-table-column>
        
        <!-- 评论者 (移动端隐藏) -->
        <el-table-column v-if="!isMobile" label="评论者" width="120">
          <template #default="{ row }">
            <span class="text-gray-600">{{ row.username }}</span>
          </template>
        </el-table-column>
        
        <!-- 状态 -->
        <el-table-column label="状态" :width="isMobile ? 80 : 100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <!-- 评论时间 (移动端隐藏) -->
        <el-table-column v-if="!isMobile" label="评论时间" width="170">
          <template #default="{ row }">
            <span class="text-gray-500 text-sm">{{ formatDate(row.createTime) }}</span>
          </template>
        </el-table-column>
        
        <!-- 操作 -->
        <el-table-column label="操作" :width="isMobile ? 120 : 150" fixed="right">
          <template #default="{ row }">
            <div class="flex items-center gap-2 justify-end">
              <!-- 审核通过按钮（仅待审核状态显示） -->
              <el-tooltip v-if="row.status === CommentStatus.PENDING" content="审核通过">
                <el-button 
                  type="success" 
                  :icon="Check" 
                  size="small" 
                  circle 
                  @click="handleApprove(row)" 
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
 * 评论管理页面
 * @requirements 9.1, 9.2, 9.3, 9.4, 9.5
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Check, Delete } from '@element-plus/icons-vue'
import { commentApi } from '@/api/comment'
import type { Comment } from '@/types'
import { CommentStatus } from '@/types'
import dayjs from 'dayjs'
import { useResponsive } from '@/utils/useResponsive'

// 响应式状态
const { isMobile } = useResponsive()

/** 加载状态 */
const loading = ref(false)


/** 评论列表 */
const commentList = ref<Comment[]>([])

/** 筛选状态 */
const filterStatus = ref<CommentStatus | undefined>(undefined)

/** 选中的评论ID列表 */
const selectedIds = ref<number[]>([])

/** 分页信息 */
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

/** 状态选项 @requirements 9.2 */
const statusOptions = [
  { label: '正常', value: CommentStatus.NORMAL },
  { label: '待审核', value: CommentStatus.PENDING },
  { label: '已删除', value: CommentStatus.DELETED }
]

/**
 * 获取状态标签类型
 */
const getStatusTagType = (status: CommentStatus): 'success' | 'warning' | 'danger' | 'info' => {
  const typeMap: Record<CommentStatus, 'success' | 'warning' | 'danger' | 'info'> = {
    [CommentStatus.NORMAL]: 'success',
    [CommentStatus.PENDING]: 'warning',
    [CommentStatus.DELETED]: 'danger'
  }
  return typeMap[status] || 'info'
}

/**
 * 获取状态标签文本
 */
const getStatusLabel = (status: CommentStatus): string => {
  const labelMap: Record<CommentStatus, string> = {
    [CommentStatus.NORMAL]: '正常',
    [CommentStatus.PENDING]: '待审核',
    [CommentStatus.DELETED]: '已删除'
  }
  return labelMap[status] || '未知'
}

/**
 * 格式化日期
 */
const formatDate = (date: string) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm') : '-'
}

/**
 * 加载评论列表
 * @requirements 9.1
 */
const loadCommentList = async () => {
  loading.value = true
  try {
    const result = await commentApi.getList({
      page: pagination.page,
      pageSize: pagination.pageSize,
      status: filterStatus.value
    })
    commentList.value = result.list
    pagination.total = result.total
  } catch (error) {
    console.error('加载评论列表失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 处理筛选变化
 * @requirements 9.2
 */
const handleFilterChange = () => {
  pagination.page = 1
  loadCommentList()
}

/**
 * 处理选择变化
 * @requirements 9.5
 */
const handleSelectionChange = (selection: Comment[]) => {
  selectedIds.value = selection.map(item => item.id)
}

/**
 * 处理分页大小变化
 */
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.page = 1
  loadCommentList()
}

/**
 * 处理页码变化
 */
const handlePageChange = (page: number) => {
  pagination.page = page
  loadCommentList()
}

/**
 * 审核通过评论
 * @requirements 9.3
 */
const handleApprove = async (comment: Comment) => {
  try {
    await ElMessageBox.confirm(
      `确定要审核通过该评论吗？`,
      '审核确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    await commentApi.approve(comment.id)
    ElMessage.success('审核通过')
    loadCommentList()
  } catch (error: unknown) {
    if (error !== 'cancel') {
      console.error('审核评论失败:', error)
    }
  }
}

/**
 * 删除评论
 * @requirements 9.4
 */
const handleDelete = async (comment: Comment) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除该评论吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await commentApi.delete(comment.id)
    ElMessage.success('删除成功')
    loadCommentList()
  } catch (error: unknown) {
    if (error !== 'cancel') {
      console.error('删除评论失败:', error)
    }
  }
}

/**
 * 批量审核通过
 * @requirements 9.5
 */
const handleBatchApprove = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请先选择要审核的评论')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要批量审核通过 ${selectedIds.value.length} 条评论吗？`,
      '批量审核确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    const result = await commentApi.batchApprove(selectedIds.value)
    ElMessage.success(`成功审核 ${result.successCount} 条评论`)
    selectedIds.value = []
    loadCommentList()
  } catch (error: unknown) {
    if (error !== 'cancel') {
      console.error('批量审核失败:', error)
    }
  }
}

/**
 * 批量删除
 * @requirements 9.5
 */
const handleBatchDelete = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请先选择要删除的评论')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要批量删除 ${selectedIds.value.length} 条评论吗？此操作不可恢复！`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    const result = await commentApi.batchDelete(selectedIds.value)
    ElMessage.success(`成功删除 ${result.successCount} 条评论`)
    selectedIds.value = []
    loadCommentList()
  } catch (error: unknown) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
    }
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadCommentList()
})
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

:deep(.el-table__row:hover) {
  background-color: rgba(var(--color-primary-50), 0.5);
}

:deep(.el-tag) {
  border-radius: 4px;
}
</style>
