<template>
  <div class="user-management p-6">
    <!-- 页面标题 -->
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-800">用户管理</h1>
      <p class="text-gray-500 mt-1">管理系统用户，控制用户状态</p>
    </div>

    <!-- 搜索区域 -->
    <div class="glass-card p-4 mb-6 rounded-lg">
      <div class="flex flex-wrap items-center gap-4">
        <!-- 用户名搜索 -->
        <div class="flex items-center gap-2">
          <span class="text-gray-600">用户名：</span>
          <el-input
            v-model="searchKeyword"
            placeholder="请输入用户名搜索"
            clearable
            style="width: 220px"
            :prefix-icon="Search"
            @keyup.enter="handleSearch"
            @clear="handleSearch"
          />
        </div>
        <el-button type="primary" :icon="Search" @click="handleSearch">
          搜索
        </el-button>
      </div>
    </div>

    <!-- 用户列表表格 -->
    <div class="glass-card rounded-lg overflow-hidden">
      <el-table 
        v-loading="loading" 
        :data="userList" 
        stripe 
        style="width: 100%"
      >
        <!-- 用户名 -->
        <el-table-column label="用户名" min-width="120">
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <el-avatar :size="32" :src="row.avatar">
                {{ row.username?.charAt(0)?.toUpperCase() }}
              </el-avatar>
              <span class="text-gray-800 font-medium">{{ row.username }}</span>
            </div>
          </template>
        </el-table-column>
        
        <!-- 邮箱 -->
        <el-table-column label="邮箱" min-width="180">
          <template #default="{ row }">
            <span class="text-gray-600">{{ row.email || '-' }}</span>
          </template>
        </el-table-column>
        
        <!-- 角色 -->
        <el-table-column label="角色" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.role === UserRole.ADMIN ? 'danger' : 'info'" size="small">
              {{ getRoleLabel(row.role) }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 状态 -->
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === UserStatus.NORMAL ? 'success' : 'danger'" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <!-- 注册时间 -->
        <el-table-column label="注册时间" width="170">
          <template #default="{ row }">
            <span class="text-gray-500 text-sm">{{ formatDate(row.createTime) }}</span>
          </template>
        </el-table-column>
        
        <!-- 操作 -->
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <!-- 启用/禁用按钮 -->
              <el-tooltip :content="row.status === UserStatus.NORMAL ? '禁用用户' : '启用用户'">
                <el-button 
                  :type="row.status === UserStatus.NORMAL ? 'danger' : 'success'" 
                  :icon="row.status === UserStatus.NORMAL ? Lock : Unlock"
                  size="small" 
                  circle 
                  :disabled="isCurrentUser(row.id)"
                  @click="handleToggleStatus(row)" 
                />
              </el-tooltip>
              <!-- 当前用户提示 -->
              <el-tooltip v-if="isCurrentUser(row.id)" content="不能禁用自己">
                <el-icon class="text-gray-400"><Warning /></el-icon>
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
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 用户管理页面
 * 实现用户列表展示、搜索和状态切换
 * @requirements 11.1, 11.2, 11.3, 11.4, 11.5
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Lock, Unlock, Warning } from '@element-plus/icons-vue'
import { userApi } from '@/api/user'
import { useUserStore } from '@/stores/user'
import type { User } from '@/types'
import { UserRole, UserStatus } from '@/types'
import dayjs from 'dayjs'

/** 用户 Store */
const userStore = useUserStore()

/** 加载状态 */
const loading = ref(false)

/** 用户列表 */
const userList = ref<User[]>([])

/** 搜索关键词 */
const searchKeyword = ref('')

/** 分页信息 */
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

/**
 * 获取角色标签文本
 */
const getRoleLabel = (role: UserRole): string => {
  const labelMap: Record<UserRole, string> = {
    [UserRole.ADMIN]: '管理员',
    [UserRole.USER]: '普通用户'
  }
  return labelMap[role] || '未知'
}

/**
 * 获取状态标签文本
 */
const getStatusLabel = (status: UserStatus): string => {
  const labelMap: Record<UserStatus, string> = {
    [UserStatus.NORMAL]: '正常',
    [UserStatus.DISABLED]: '禁用'
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
 * 判断是否为当前登录用户
 * @requirements 11.5
 */
const isCurrentUser = (userId: number): boolean => {
  return userStore.userId === userId
}


/**
 * 加载用户列表
 * @requirements 11.1
 */
const loadUserList = async () => {
  loading.value = true
  try {
    const result = await userApi.getList({
      page: pagination.page,
      pageSize: pagination.pageSize,
      keyword: searchKeyword.value || undefined
    })
    userList.value = result.list
    pagination.total = result.total
  } catch (error) {
    console.error('加载用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 处理搜索
 * @requirements 11.2
 */
const handleSearch = () => {
  pagination.page = 1
  loadUserList()
}

/**
 * 处理分页大小变化
 */
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.page = 1
  loadUserList()
}

/**
 * 处理页码变化
 */
const handlePageChange = (page: number) => {
  pagination.page = page
  loadUserList()
}

/**
 * 切换用户状态（启用/禁用）
 * @requirements 11.3, 11.4, 11.5
 */
const handleToggleStatus = async (user: User) => {
  // 自我禁用保护
  if (isCurrentUser(user.id)) {
    ElMessage.warning('不能禁用自己的账号')
    return
  }
  
  const isDisabling = user.status === UserStatus.NORMAL
  const actionText = isDisabling ? '禁用' : '启用'
  
  try {
    await ElMessageBox.confirm(
      `确定要${actionText}用户「${user.username}」吗？`,
      `${actionText}确认`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: isDisabling ? 'warning' : 'info'
      }
    )
    await userApi.toggleStatus(user.id)
    ElMessage.success(`${actionText}成功`)
    loadUserList()
  } catch (error: unknown) {
    if (error !== 'cancel') {
      console.error(`${actionText}用户失败:`, error)
    }
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadUserList()
})
</script>

<style scoped>
:deep(.el-table__row:hover) {
  background-color: rgba(var(--color-primary-50), 0.5);
}

:deep(.el-tag) {
  border-radius: 4px;
}

:deep(.el-avatar) {
  background-color: var(--el-color-primary-light-7);
  color: var(--el-color-primary);
}
</style>
