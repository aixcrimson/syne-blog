<template>
  <div class="navigation-management p-0 md:p-6">
    <!-- 页面标题 -->
    <div class="mb-4 md:mb-6 hidden md:block">
      <h1 class="text-2xl font-bold text-gray-800">导航管理</h1>
      <p class="text-gray-500 mt-1">管理导航站点和分类</p>
    </div>

    <!-- 操作区域 -->
    <div class="glass-card p-4 mb-4 md:mb-6 rounded-lg">
      <div class="flex items-center justify-between">
        <span class="text-gray-600">共 {{ categoryList.length }} 个分类，{{ totalSites }} 个站点</span>
        <div class="flex gap-2">
          <el-button type="success" :icon="Upload" @click="showBookmarkImport">
            导入书签
          </el-button>
          <el-button type="primary" :icon="Plus" @click="handleCreateCategory">
            新建分类
          </el-button>
        </div>
      </div>
    </div>

    <!-- 分类分组展示 -->
    <div v-loading="loading" class="space-y-6">
      <div v-if="categoryList.length === 0 && !loading" class="glass-card p-8 rounded-lg text-center">
        <el-empty description="暂无导航分类">
          <el-button type="primary" @click="handleCreateCategory">创建第一个分类</el-button>
        </el-empty>
      </div>

      <!-- 可拖拽分类列表 -->
      <draggable
        v-model="categoryList"
        item-key="id"
        handle=".category-drag-handle"
        @end="handleCategoryDragEnd"
      >
        <template #item="{ element: category }">
          <div class="glass-card rounded-lg overflow-hidden mb-4">
            <!-- 分类头部 -->
            <div class="flex items-center justify-between p-4 bg-gray-50/50 border-b border-gray-100">
              <div class="flex items-center gap-3">
                <el-icon class="category-drag-handle cursor-move text-gray-400 hover:text-gray-600">
                  <Rank />
                </el-icon>

                <span class="font-medium text-gray-800">{{ category.name }}</span>
                <el-tag type="info" size="small">{{ category.sites?.length || 0 }} 个站点</el-tag>
              </div>
              <div class="flex items-center gap-2">
                <el-tooltip content="添加站点">
                  <el-button type="success" :icon="Plus" size="small" circle @click="handleCreateSite(category)" />
                </el-tooltip>
                <el-tooltip content="编辑分类">
                  <el-button type="primary" :icon="Edit" size="small" circle @click="handleEditCategory(category)" />
                </el-tooltip>
                <el-tooltip :content="category.sites?.length ? '分类下有站点，无法删除' : '删除分类'">
                  <el-button 
                    type="danger" 
                    :icon="Delete" 
                    size="small" 
                    circle 
                    :disabled="category.sites?.length > 0"
                    @click="handleDeleteCategory(category)" 
                  />
                </el-tooltip>
              </div>
            </div>

            <!-- 站点列表 -->
            <div class="p-4">
              <draggable
                v-model="category.sites"
                item-key="id"
                handle=".site-drag-handle"
                group="sites"
                :empty-insert-threshold="50"
                @change="(evt: DragChangeEvent) => handleSiteDragChange(evt, category)"
              >
                <template #item="{ element: site }">
                  <div class="flex items-center justify-between p-3 mb-2 bg-white rounded-lg border border-gray-100 hover:shadow-sm transition-shadow">
                    <div class="flex items-center gap-3 flex-1 min-w-0">
                      <el-icon class="site-drag-handle cursor-move text-gray-400 hover:text-gray-600">
                        <Rank />
                      </el-icon>

                      <div class="flex-1 min-w-0">
                        <div class="font-medium text-gray-800 truncate">{{ site.name }}</div>
                        <div class="text-xs text-gray-400 truncate">{{ site.url }}</div>
                      </div>
                    </div>
                    <div class="flex items-center gap-2 ml-4">
                      <el-tooltip content="访问站点">
                        <el-button :icon="Link" size="small" circle @click="openSite(site.url)" />
                      </el-tooltip>
                      <el-tooltip content="编辑站点">
                        <el-button type="primary" :icon="Edit" size="small" circle @click="handleEditSite(site, category)" />
                      </el-tooltip>
                      <el-tooltip content="删除站点">
                        <el-button type="danger" :icon="Delete" size="small" circle @click="handleDeleteSite(site)" />
                      </el-tooltip>
                    </div>
                  </div>
                </template>
                <template #footer>
                  <div v-if="!category.sites?.length" class="text-center py-4 text-gray-400">
                    暂无站点，点击上方"+"按钮添加或拖拽站点到此处
                  </div>
                </template>
              </draggable>
            </div>
          </div>
        </template>
      </draggable>
    </div>

    <!-- 分类对话框 -->
    <el-dialog 
      v-model="categoryDialogVisible" 
      :title="isCategoryEdit ? '编辑分类' : '新建分类'" 
      :width="isMobile ? '90%' : '500px'" 
      :close-on-click-modal="false"
      append-to-body
      @closed="handleCategoryDialogClose"
    >
      <el-form ref="categoryFormRef" :model="categoryForm" :rules="categoryFormRules" :label-width="isMobile ? 'auto' : '80px'">
        <el-form-item label="名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="categoryForm.sortOrder" :min="0" :max="9999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="flex justify-end gap-2">
          <el-button @click="categoryDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="categorySubmitting" @click="handleCategorySubmit">
            {{ isCategoryEdit ? '更新' : '创建' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 站点对话框 -->
    <el-dialog 
      v-model="siteDialogVisible" 
      :title="isSiteEdit ? '编辑站点' : '新建站点'" 
      :width="isMobile ? '90%' : '550px'" 
      :close-on-click-modal="false"
      append-to-body
      @closed="handleSiteDialogClose"
    >
      <el-form ref="siteFormRef" :model="siteForm" :rules="siteFormRules" :label-width="isMobile ? 'auto' : '80px'">
        <el-form-item label="名称" prop="name">
          <el-input v-model="siteForm.name" placeholder="请输入站点名称" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="URL" prop="url">
          <el-input v-model="siteForm.url" placeholder="请输入站点URL（如 https://example.com）" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="siteForm.description" type="textarea" :rows="2" placeholder="请输入站点描述" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="siteForm.sortOrder" :min="0" :max="9999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="flex justify-end gap-2">
          <el-button @click="siteDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="siteSubmitting" @click="handleSiteSubmit">
            {{ isSiteEdit ? '更新' : '创建' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 书签导入对话框 -->
    <BookmarkImport
      v-model="bookmarkImportVisible"
      :categories="categoryList"
      @success="handleImportSuccess"
    />
  </div>
</template>


<script setup lang="ts">
/**
 * 导航管理页面
 * @requirements 10.1, 10.2, 10.3, 10.5
 */
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Edit, Delete, Rank, Link, Upload } from '@element-plus/icons-vue'
import draggable from 'vuedraggable'
import { navigationApi, type NavigationCategoryForm, type NavigationSiteForm } from '@/api/navigation'
import type { NavigationCategory, NavigationSite, SortOrderItem } from '@/types'
import { isValidUrl } from '@/utils/validate'
import BookmarkImport from './components/BookmarkImport.vue'
import { useResponsive } from '@/utils/useResponsive'

const { isMobile } = useResponsive()

/** vuedraggable change 事件类型 */
interface DragChangeEvent {
  added?: { element: NavigationSite; newIndex: number }
  removed?: { element: NavigationSite; oldIndex: number }
  moved?: { element: NavigationSite; newIndex: number; oldIndex: number }
}

/** 加载状态 */
const loading = ref(false)

/** 分类列表 */
const categoryList = ref<NavigationCategory[]>([])

/** 站点总数 */
const totalSites = computed(() => 
  categoryList.value.reduce((sum, cat) => sum + (cat.sites?.length || 0), 0)
)

// ==================== 分类相关 ====================

/** 分类对话框可见性 */
const categoryDialogVisible = ref(false)

/** 是否编辑分类 */
const isCategoryEdit = ref(false)

/** 编辑中的分类ID */
const editingCategoryId = ref<number | null>(null)

/** 分类表单引用 */
const categoryFormRef = ref<FormInstance>()

/** 分类提交状态 */
const categorySubmitting = ref(false)

/** 分类表单数据 */
const categoryForm = reactive<NavigationCategoryForm>({
  name: '',
  sortOrder: 0
})

/** 分类表单验证规则 */
const categoryFormRules: FormRules<NavigationCategoryForm> = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { max: 50, message: '名称不能超过50个字符', trigger: 'blur' }
  ],
  sortOrder: [
    { required: true, message: '请输入排序值', trigger: 'blur' }
  ]
}

// ==================== 站点相关 ====================

/** 站点对话框可见性 */
const siteDialogVisible = ref(false)

/** 是否编辑站点 */
const isSiteEdit = ref(false)

/** 编辑中的站点ID */
const editingSiteId = ref<number | null>(null)

/** 站点表单引用 */
const siteFormRef = ref<FormInstance>()

/** 站点提交状态 */
const siteSubmitting = ref(false)

/** 站点表单数据 */
const siteForm = reactive<NavigationSiteForm>({
  categoryId: 0,
  name: '',
  description: '',
  url: '',
  sortOrder: 0
})

/**
 * URL 验证器
 * @requirements 10.4
 */
const validateUrl = (_rule: unknown, value: string, callback: (error?: Error) => void) => {
  if (!value) {
    callback(new Error('请输入站点URL'))
  } else if (!isValidUrl(value)) {
    callback(new Error('请输入有效的URL（以 http:// 或 https:// 开头）'))
  } else {
    callback()
  }
}

/** 站点表单验证规则 */
const siteFormRules: FormRules<NavigationSiteForm> = {
  name: [
    { required: true, message: '请输入站点名称', trigger: 'blur' },
    { max: 100, message: '名称不能超过100个字符', trigger: 'blur' }
  ],
  url: [
    { required: true, validator: validateUrl, trigger: 'blur' }
  ],
  description: [
    { max: 200, message: '描述不能超过200个字符', trigger: 'blur' }
  ],
  sortOrder: [
    { required: true, message: '请输入排序值', trigger: 'blur' }
  ]
}

// ==================== 书签导入相关 ====================

/** 书签导入对话框可见性 */
const bookmarkImportVisible = ref(false)

/**
 * 显示书签导入对话框
 */
const showBookmarkImport = () => {
  bookmarkImportVisible.value = true
}

/**
 * 处理导入成功
 */
const handleImportSuccess = () => {
  ElMessage.success('书签导入成功')
  loadCategoryList() // 刷新数据
}

// ==================== 数据加载 ====================

/**
 * 加载分类列表
 * @requirements 10.1
 */
const loadCategoryList = async () => {
  loading.value = true
  try {
    categoryList.value = await navigationApi.getAllNavigationSites()
  } catch (error) {
    console.error('加载导航分类失败:', error)
  } finally {
    loading.value = false
  }
}

// ==================== 分类操作 ====================

/** 重置分类表单 */
const resetCategoryForm = () => {
  categoryForm.name = ''
  categoryForm.sortOrder = categoryList.value.length
  categoryFormRef.value?.clearValidate()
}

/**
 * 打开新建分类对话框
 * @requirements 10.2
 */
const handleCreateCategory = () => {
  isCategoryEdit.value = false
  editingCategoryId.value = null
  resetCategoryForm()
  categoryDialogVisible.value = true
}

/**
 * 打开编辑分类对话框
 * @requirements 10.2
 */
const handleEditCategory = (category: NavigationCategory) => {
  isCategoryEdit.value = true
  editingCategoryId.value = category.id
  categoryForm.name = category.name
  categoryForm.sortOrder = category.sortOrder
  categoryDialogVisible.value = true
}

/**
 * 提交分类表单
 * @requirements 10.2
 */
const handleCategorySubmit = async () => {
  if (!categoryFormRef.value) return
  try { await categoryFormRef.value.validate() } catch { return }

  categorySubmitting.value = true
  try {
    if (isCategoryEdit.value && editingCategoryId.value) {
      await navigationApi.updateCategory(editingCategoryId.value, categoryForm)
      ElMessage.success('更新成功')
    } else {
      await navigationApi.createCategory(categoryForm)
      ElMessage.success('创建成功')
    }
    categoryDialogVisible.value = false
    loadCategoryList()
  } catch (error) {
    console.error('保存分类失败:', error)
  } finally {
    categorySubmitting.value = false
  }
}

/**
 * 删除分类
 * @requirements 10.2
 */
const handleDeleteCategory = async (category: NavigationCategory) => {
  if (category.sites?.length) {
    ElMessage.warning(`分类「${category.name}」下有站点，无法删除`)
    return
  }
  try {
    await ElMessageBox.confirm(`确定要删除分类「${category.name}」吗？`, '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await navigationApi.deleteCategory(category.id)
    ElMessage.success('删除成功')
    loadCategoryList()
  } catch (error: unknown) {
    if (error !== 'cancel') console.error('删除分类失败:', error)
  }
}

/** 分类对话框关闭回调 */
const handleCategoryDialogClose = () => resetCategoryForm()

/**
 * 分类拖拽结束处理
 * @requirements 10.5
 */
const handleCategoryDragEnd = async () => {
  const orders: SortOrderItem[] = categoryList.value.map((cat, index) => ({
    id: cat.id,
    sortOrder: index
  }))
  try {
    await navigationApi.updateCategorySortOrder(orders)
    ElMessage.success('排序已更新')
  } catch (error) {
    console.error('更新分类排序失败:', error)
    loadCategoryList() // 恢复原顺序
  }
}

// ==================== 站点操作 ====================

/** 重置站点表单 */
const resetSiteForm = () => {
  siteForm.categoryId = 0
  siteForm.name = ''
  siteForm.description = ''
  siteForm.url = ''
  siteForm.sortOrder = 0
  siteFormRef.value?.clearValidate()
}

/**
 * 打开新建站点对话框
 * @requirements 10.3
 */
const handleCreateSite = (category: NavigationCategory) => {
  isSiteEdit.value = false
  editingSiteId.value = null
  resetSiteForm()
  siteForm.categoryId = category.id
  siteForm.sortOrder = category.sites?.length || 0
  siteDialogVisible.value = true
}

/**
 * 打开编辑站点对话框
 * @requirements 10.3
 */
const handleEditSite = (site: NavigationSite, category: NavigationCategory) => {
  isSiteEdit.value = true
  editingSiteId.value = site.id
  siteForm.categoryId = category.id
  siteForm.name = site.name
  siteForm.description = site.description || ''
  siteForm.url = site.url
  siteForm.sortOrder = site.sortOrder
  siteDialogVisible.value = true
}

/**
 * 提交站点表单
 * @requirements 10.3
 */
const handleSiteSubmit = async () => {
  if (!siteFormRef.value) return
  try { await siteFormRef.value.validate() } catch { return }

  siteSubmitting.value = true
  try {
    if (isSiteEdit.value && editingSiteId.value) {
      await navigationApi.updateSite(editingSiteId.value, siteForm)
      ElMessage.success('更新成功')
    } else {
      await navigationApi.createSite(siteForm)
      ElMessage.success('创建成功')
    }
    siteDialogVisible.value = false
    loadCategoryList()
  } catch (error) {
    console.error('保存站点失败:', error)
  } finally {
    siteSubmitting.value = false
  }
}

/**
 * 删除站点
 * @requirements 10.3
 */
const handleDeleteSite = async (site: NavigationSite) => {
  try {
    await ElMessageBox.confirm(`确定要删除站点「${site.name}」吗？`, '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await navigationApi.deleteSite(site.id)
    ElMessage.success('删除成功')
    loadCategoryList()
  } catch (error: unknown) {
    if (error !== 'cancel') console.error('删除站点失败:', error)
  }
}

/** 站点对话框关闭回调 */
const handleSiteDialogClose = () => resetSiteForm()

/**
 * 站点拖拽变化处理（支持跨分类拖拽）
 * @requirements 10.5
 */
const handleSiteDragChange = async (evt: DragChangeEvent, category: NavigationCategory) => {
  // 只在 added 或 moved 事件时处理（避免重复处理）
  if (!evt.added && !evt.moved) return
  
  // 确保 sites 数组已初始化
  if (!category.sites) {
    category.sites = []
  }

  // 构建排序数据（包含 categoryId 用于跨分类拖拽）
  const orders: SortOrderItem[] = category.sites.map((site, index) => ({
    id: site.id,
    sortOrder: index,
    categoryId: category.id // 始终传入当前分类ID
  }))

  try {
    await navigationApi.updateSiteSortOrder(orders)
    ElMessage.success(evt.added ? '站点已移动到此分类' : '排序已更新')
  } catch (error) {
    console.error('更新站点排序失败:', error)
    loadCategoryList() // 恢复原顺序
  }
}

/**
 * 打开站点链接
 */
const openSite = (url: string) => {
  window.open(url, '_blank')
}

// 组件挂载时加载数据
onMounted(() => loadCategoryList())
</script>

<style scoped>
:deep(.el-table__row:hover) {
  background-color: rgba(var(--color-primary-50), 0.5);
}

.category-drag-handle,
.site-drag-handle {
  cursor: move;
}

.category-drag-handle:active,
.site-drag-handle:active {
  cursor: grabbing;
}
</style>
