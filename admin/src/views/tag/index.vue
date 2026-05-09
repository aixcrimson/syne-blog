<template>
  <div class="tag-management p-0 md:p-6">
    <!-- 页面标题 -->
    <div class="mb-6 hidden md:block">
      <h1 class="text-2xl font-bold text-gray-800">标签管理</h1>
      <p class="text-gray-500 mt-1">管理博客的文章标签</p>
    </div>

    <!-- 搜索和操作区域 -->
    <div class="glass-card p-4 mb-4 md:mb-6 rounded-lg">
      <div class="flex flex-wrap items-center justify-between gap-4">
        <div class="flex items-center gap-4 flex-1">
          <el-input
            v-model="queryParams.keyword"
            placeholder="搜索标签名称"
            clearable
            class="w-64"
            :prefix-icon="Search"
            @keyup.enter="handleSearch"
            @clear="handleSearch"
          />
          <el-button type="primary" :icon="Search" @click="handleSearch">
            搜索
          </el-button>
          <span class="text-gray-500 text-sm ml-2">共 {{ total }} 个标签</span>
        </div>
        <el-button type="primary" :icon="Plus" @click="handleCreate">
          新建标签
        </el-button>
      </div>
    </div>

    <div class="glass-card rounded-lg overflow-hidden">
      <el-table v-loading="loading" :data="tagList" stripe style="width: 100%">
        <el-table-column label="名称" min-width="150">
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <span 
                class="w-3 h-3 rounded-full" 
                :style="{ backgroundColor: row.color }"
              ></span>
              <span class="text-gray-800 font-medium">{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column v-if="!isMobile" label="别名" width="150">
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ row.slug }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="颜色" width="120" align="center">
          <template #default="{ row }">
            <div class="flex items-center justify-center gap-2">
              <span 
                class="w-6 h-6 rounded border border-gray-200" 
                :style="{ backgroundColor: row.color }"
              ></span>
              <span class="text-gray-500 text-xs">{{ row.color }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="有效文章数" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.articleCount > 0 ? 'success' : 'info'" size="small">
              {{ row.articleCount }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="!isMobile" label="创建时间" width="170">
          <template #default="{ row }">
            <span class="text-gray-500 text-sm">{{ formatDate(row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" :width="isMobile ? 120 : 150" fixed="right">
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <el-tooltip content="编辑">
                <el-button 
                  type="primary" 
                  :icon="Edit" 
                  size="small" 
                  circle 
                  @click="handleEdit(row)" 
                />
              </el-tooltip>
              <el-tooltip :content="row.articleCount > 0 ? '该标签下有文章，无法删除' : '删除'">
                <span>
                  <el-button 
                    type="danger" 
                    :icon="Delete" 
                    size="small" 
                    circle
                    :disabled="row.articleCount > 0"
                    @click="handleDelete(row)" 
                  />
                </span>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页区域 -->
      <div class="p-4 flex justify-end">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 新建/编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      :width="isMobile ? '90%' : '500px'" 
      :close-on-click-modal="false" 
      @closed="handleDialogClose"
    >
      <el-form 
        ref="formRef" 
        :model="formData" 
        :rules="formRules" 
        :label-width="isMobile ? 'auto' : '80px'" 
        :label-position="isMobile ? 'top' : 'right'"
      >
        <el-form-item label="名称" prop="name">
          <el-input 
            v-model="formData.name" 
            placeholder="请输入标签名称" 
            maxlength="50" 
            show-word-limit 
          />
        </el-form-item>
        <el-form-item label="别名" prop="slug">
          <el-input 
            v-model="formData.slug" 
            placeholder="请输入标签别名（URL友好）" 
            maxlength="50" 
            show-word-limit 
          />
        </el-form-item>
        <el-form-item label="颜色" prop="color">
          <div class="flex items-center gap-4 w-full">
            <el-color-picker 
              v-model="formData.color" 
              :predefine="predefineColors"
              show-alpha
            />
            <el-input 
              v-model="formData.color" 
              placeholder="rgb(59, 130, 246)" 
              class="flex-1"
            />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="flex justify-end gap-2">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            :loading="submitting" 
            @click="handleSubmit"
          >
            {{ isEdit ? '更新' : '创建' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>


<script setup lang="ts">
/**
 * 标签管理页面
 * @requirements 8.1, 8.2, 8.3, 8.4, 8.5
 */
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Edit, Delete, Search } from '@element-plus/icons-vue'
import { tagApi } from '@/api/tag'
import type { Tag, TagForm } from '@/types'
import { isTagNameUnique, isTagSlugUnique } from '@/utils/validate'
import dayjs from 'dayjs'
import { useResponsive } from '@/utils/useResponsive'

// 响应式状态
const { isMobile } = useResponsive()

/** 加载状态 */
const loading = ref(false)


/** 标签列表 */
const tagList = ref<Tag[]>([])

/** 总数 */
const total = ref(0)

/** 查询参数 */
const queryParams = reactive({
  page: 1,
  pageSize: 10,
  keyword: ''
})

/** 对话框可见性 */
const dialogVisible = ref(false)

/** 对话框标题 */
const dialogTitle = computed(() => isEdit.value ? '编辑标签' : '新建标签')

/** 是否编辑模式 */
const isEdit = ref(false)

/** 编辑中的标签ID */
const editingId = ref<number | null>(null)

/** 表单引用 */
const formRef = ref<FormInstance>()

/** 提交状态 */
const submitting = ref(false)

/** 表单数据 */
const formData = reactive<TagForm>({
  name: '',
  slug: '',
  color: 'rgb(59, 130, 246)'
})

/** 预定义颜色 @requirements 8.3 */
const predefineColors = [
  'rgb(59, 130, 246)', // 蓝色
  'rgb(139, 92, 246)', // 紫色
  'rgb(16, 185, 129)', // 绿色
  'rgb(245, 158, 11)', // 橙色
  'rgb(236, 72, 153)', // 粉色
  'rgb(239, 68, 68)', // 红色
  'rgb(6, 182, 212)', // 青色
  'rgb(132, 204, 22)', // 黄绿色
  'rgb(99, 102, 241)', // 靛蓝色
  'rgb(249, 115, 22)', // 深橙色
]

/**
 * 验证标签名称唯一性
 * @requirements 8.4
 */
const validateNameUnique = (
  _rule: unknown, 
  value: string, 
  callback: (error?: Error) => void
) => {
  if (!isTagNameUnique(value, tagList.value, editingId.value)) {
    callback(new Error('标签名称已存在'))
  } else {
    callback()
  }
}

/**
 * 验证标签别名唯一性
 * @requirements 8.4
 */
const validateSlugUnique = (
  _rule: unknown, 
  value: string, 
  callback: (error?: Error) => void
) => {
  if (!isTagSlugUnique(value, tagList.value, editingId.value)) {
    callback(new Error('标签别名已存在'))
  } else {
    callback()
  }
}

/** 表单验证规则 */
const formRules: FormRules<TagForm> = {
  name: [
    { required: true, message: '请输入标签名称', trigger: 'blur' },
    { max: 50, message: '名称不能超过50个字符', trigger: 'blur' },
    { validator: validateNameUnique, trigger: 'blur' }
  ],
  slug: [
    { required: true, message: '请输入标签别名', trigger: 'blur' },
    { max: 50, message: '别名不能超过50个字符', trigger: 'blur' },
    { pattern: /^[a-z0-9-]+$/, message: '别名只能包含小写字母、数字和连字符', trigger: 'blur' },
    { validator: validateSlugUnique, trigger: 'blur' }
  ],
  color: [
    { required: true, message: '请选择标签颜色', trigger: 'change' }
  ]
}

/**
 * 格式化日期
 */
const formatDate = (date: string) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm') : '-'
}

/**
 * 加载标签列表
 * @requirements 8.1
 */
const loadTagList = async () => {
  loading.value = true
  try {
    const params = {
      ...queryParams,
      keyword: queryParams.keyword || undefined
    }
    const res = await tagApi.getList(params)
    tagList.value = res.list
    total.value = res.total
  } catch (error) {
    console.error('加载标签列表失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 处理搜索
 */
const handleSearch = () => {
  queryParams.page = 1
  loadTagList()
}

/**
 * 分页大小变化
 */
const handleSizeChange = (val: number) => {
  queryParams.pageSize = val
  queryParams.page = 1
  loadTagList()
}
/**
 * 当前页变化
 */
const handleCurrentChange = (val: number) => {
  queryParams.page = val
  loadTagList()
}

/**
 * 重置表单
 */
const resetForm = () => {
  formData.name = ''
  formData.slug = ''
  formData.color = 'rgb(59, 130, 246)'
  formRef.value?.clearValidate()
}

/**
 * 打开新建对话框
 * @requirements 8.2
 */
const handleCreate = () => {
  isEdit.value = false
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

/**
 * 打开编辑对话框
 * @requirements 8.5
 */
const handleEdit = (tag: Tag) => {
  isEdit.value = true
  editingId.value = tag.id
  formData.name = tag.name
  formData.slug = tag.slug
  formData.color = tag.color
  dialogVisible.value = true
}

/**
 * 提交表单
 * @requirements 8.2, 8.5
 */
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  
  submitting.value = true
  try {
    if (isEdit.value && editingId.value) {
      await tagApi.update(editingId.value, formData)
      ElMessage.success('更新成功')
    } else {
      await tagApi.create(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadTagList()
  } catch (error) {
    console.error('保存标签失败:', error)
  } finally {
    submitting.value = false
  }
}

/**
 * 删除标签
 * @requirements 8.5
 */
const handleDelete = async (tag: Tag) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除标签「${tag.name}」吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await tagApi.delete(tag.id)
    ElMessage.success('删除成功')
    
    // 如果当前页只有一条数据，且不是第一页，删除后跳转到前一页
    if (tagList.value.length === 1 && queryParams.page > 1) {
      queryParams.page--
    }
    
    loadTagList()
  } catch (error: unknown) {
    if (error !== 'cancel') {
      console.error('删除标签失败:', error)
    }
  }
}

/**
 * 对话框关闭回调
 */
const handleDialogClose = () => {
  resetForm()
}

// 组件挂载时加载数据
onMounted(() => {
  loadTagList()
})
</script>

<style scoped>
:deep(.el-table) {
  --el-table-row-hover-bg-color: var(--color-primary-100);
}

:deep(.el-tag) {
  border-radius: 4px;
}

:deep(.el-color-picker__trigger) {
  width: 40px;
  height: 40px;
  border-radius: 6px;
}
</style>

