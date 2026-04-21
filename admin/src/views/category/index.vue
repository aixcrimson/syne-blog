<template>
  <div class="category-management p-6">
    <!-- 页面标题 -->
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-800">分类管理</h1>
      <p class="text-gray-500 mt-1">管理博客文章分类</p>
    </div>

    <!-- 操作区域 -->
    <div class="glass-card p-4 mb-6 rounded-lg">
      <div class="flex items-center justify-between">
        <span class="text-gray-600">共 {{ categoryList.length }} 个分类</span>
        <el-button type="primary" :icon="Plus" @click="handleCreate">
          新建分类
        </el-button>
      </div>
    </div>

    <!-- 分类列表表格 -->
    <div class="glass-card rounded-lg overflow-hidden">
      <el-table v-loading="loading" :data="categoryList" stripe style="width: 100%">
        <el-table-column label="名称" min-width="150">
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <span class="text-gray-800 font-medium">{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column v-if="!isMobile" label="别名" width="150">
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ row.slug }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="!isMobile" label="描述" min-width="200">
          <template #default="{ row }">
            <span class="text-gray-500 text-sm truncate" :title="row.description">
              {{ row.description || '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column v-if="!isMobile" label="排序" width="80" align="center">
          <template #default="{ row }">
            <span class="text-gray-600">{{ row.sortOrder }}</span>
          </template>
        </el-table-column>
        <el-table-column label="文章数" width="100" align="center">
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
                <el-button type="primary" :icon="Edit" size="small" circle @click="handleEdit(row)" />
              </el-tooltip>
              <el-tooltip :content="row.articleCount > 0 ? '分类下有文章，无法删除' : '删除'">
                <el-button type="danger" :icon="Delete" size="small" circle :disabled="row.articleCount > 0" @click="handleDelete(row)" />
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新建/编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      :width="isMobile ? '90%' : '500px'" 
      :close-on-click-modal="false" 
      append-to-body
      @closed="handleDialogClose"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" :label-width="isMobile ? 'auto' : '80px'" :label-position="isMobile ? 'top' : 'right'">
        <el-form-item label="名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入分类名称" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="别名" prop="slug">
          <el-input v-model="formData.slug" placeholder="请输入分类别名（URL友好）" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入分类描述" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" :max="9999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="flex justify-end gap-2">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">{{ isEdit ? '更新' : '创建' }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>


<script setup lang="ts">
/**
 * 分类管理页面
 * @requirements 7.1, 7.2, 7.3, 7.4, 7.5, 7.6
 */
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { categoryApi } from '@/api/category'
import type { Category, CategoryForm } from '@/types'
import { isCategoryNameUnique, isCategorySlugUnique, canDeleteCategory } from '@/utils/validate'
import dayjs from 'dayjs'
import { useResponsive } from '@/utils/useResponsive'

// 响应式状态
const { isMobile } = useResponsive()

const loading = ref(false)

const categoryList = ref<Category[]>([])
const dialogVisible = ref(false)
const dialogTitle = computed(() => isEdit.value ? '编辑分类' : '新建分类')
const isEdit = ref(false)
const editingId = ref<number | null>(null)
const formRef = ref<FormInstance>()
const submitting = ref(false)
const formData = reactive<CategoryForm>({ name: '', slug: '', description: '', sortOrder: 0 })

/** 验证分类名称唯一性 @requirements 7.3 */
const validateNameUnique = (_rule: unknown, value: string, callback: (error?: Error) => void) => {
  if (!isCategoryNameUnique(value, categoryList.value, editingId.value)) {
    callback(new Error('分类名称已存在'))
  } else {
    callback()
  }
}

/** 验证分类别名唯一性 @requirements 7.3 */
const validateSlugUnique = (_rule: unknown, value: string, callback: (error?: Error) => void) => {
  if (!isCategorySlugUnique(value, categoryList.value, editingId.value)) {
    callback(new Error('分类别名已存在'))
  } else {
    callback()
  }
}

const formRules: FormRules<CategoryForm> = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { max: 50, message: '名称不能超过50个字符', trigger: 'blur' },
    { validator: validateNameUnique, trigger: 'blur' }
  ],
  slug: [
    { required: true, message: '请输入分类别名', trigger: 'blur' },
    { max: 50, message: '别名不能超过50个字符', trigger: 'blur' },
    { pattern: /^[a-z0-9-]+$/, message: '别名只能包含小写字母、数字和连字符', trigger: 'blur' },
    { validator: validateSlugUnique, trigger: 'blur' }
  ],
  description: [{ max: 200, message: '描述不能超过200个字符', trigger: 'blur' }],
  sortOrder: [
    { required: true, message: '请输入排序值', trigger: 'blur' },
    { type: 'number', message: '排序值必须是数字', trigger: 'blur' }
  ]
}


const formatDate = (date: string) => date ? dayjs(date).format('YYYY-MM-DD HH:mm') : '-'

const loadCategoryList = async () => {
  loading.value = true
  try {
    categoryList.value = await categoryApi.getList()
  } catch (error) {
    console.error('加载分类列表失败:', error)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  formData.name = ''
  formData.slug = ''
  formData.description = ''
  formData.sortOrder = 0
  formRef.value?.clearValidate()
}

const handleCreate = () => {
  isEdit.value = false
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (category: Category) => {
  isEdit.value = true
  editingId.value = category.id
  formData.name = category.name
  formData.slug = category.slug
  formData.description = category.description || ''
  formData.sortOrder = category.sortOrder
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  submitting.value = true
  try {
    if (isEdit.value && editingId.value) {
      await categoryApi.update(editingId.value, formData)
      ElMessage.success('更新成功')
    } else {
      await categoryApi.create(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadCategoryList()
  } catch (error) {
    console.error('保存分类失败:', error)
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (category: Category) => {
  if (!canDeleteCategory(category)) {
    ElMessage.warning(`分类「${category.name}」下有 ${category.articleCount} 篇文章，无法删除`)
    return
  }
  try {
    await ElMessageBox.confirm(`确定要删除分类「${category.name}」吗？`, '删除确认', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    await categoryApi.delete(category.id)
    ElMessage.success('删除成功')
    loadCategoryList()
  } catch (error: unknown) {
    if (error !== 'cancel') console.error('删除分类失败:', error)
  }
}

const handleDialogClose = () => resetForm()

onMounted(() => loadCategoryList())
</script>

<style scoped>
:deep(.el-table__row:hover) { background-color: rgba(var(--color-primary-50), 0.5); }
:deep(.el-tag) { border-radius: 4px; }
</style>
