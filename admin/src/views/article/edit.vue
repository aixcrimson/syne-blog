<template>
  <div class="article-edit p-6" v-loading="loading">
    <!-- 页面头部 -->
    <div class="flex items-center justify-between mb-6">
      <div class="flex items-center gap-4">
        <el-button :icon="ArrowLeft" @click="handleBack">返回</el-button>
        <h1 class="text-2xl font-bold text-gray-800">{{ pageTitle }}</h1>
      </div>
      <div class="flex items-center gap-3">
        <el-button @click="handleSaveDraft" :loading="saving">
          保存草稿
        </el-button>
        <el-button type="primary" @click="handlePublish" :loading="saving">
          发布文章
        </el-button>
      </div>
    </div>

    <!-- 编辑表单 -->
    <div class="flex gap-6">
      <!-- 左侧：编辑器区域 -->
      <div class="flex-1 min-w-0">
        <el-form
          ref="formRef"
          :model="formData"
          :rules="formRules"
          label-position="top"
        >
          <!-- 文章标题 -->
          <el-form-item label="文章标题" prop="title">
            <el-input
              v-model="formData.title"
              placeholder="请输入文章标题"
              maxlength="200"
              show-word-limit
              size="large"
              class="title-input"
            />
          </el-form-item>

          <!-- 文章摘要 -->
          <el-form-item label="文章摘要" prop="summary">
            <el-input
              v-model="formData.summary"
              type="textarea"
              placeholder="请输入文章摘要（可选）"
              :rows="3"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>

          <!-- Markdown 编辑器 -->
          <el-form-item label="文章内容" prop="content">
            <MdEditor
              v-model="formData.content"
              :preview="true"
              :toolbars-exclude="['github']"
              style="height: 600px"
              class="md-editor-custom"
            />
          </el-form-item>
        </el-form>
      </div>

      <!-- 右侧：设置面板 -->
      <div class="w-80 flex-shrink-0">
        <div class="glass-card p-4 rounded-lg sticky top-6">
          <h3 class="text-lg font-semibold text-gray-800 mb-4">文章设置</h3>
          
          <el-form
            ref="settingsFormRef"
            :model="formData"
            :rules="formRules"
            label-position="top"
          >
            <!-- 封面图片 -->
            <el-form-item label="封面图片">
              <div class="w-full">
                <el-input
                  v-model="formData.coverImage"
                  placeholder="请输入封面图片 URL"
                  clearable
                />
                <!-- 封面预览 -->
                <div 
                  v-if="formData.coverImage" 
                  class="mt-2 rounded-lg overflow-hidden border border-gray-200"
                >
                  <img 
                    :src="formData.coverImage" 
                    alt="封面预览"
                    class="w-full h-40 object-cover"
                    @error="formData.coverImage = ''"
                  />
                </div>
              </div>
            </el-form-item>

            <!-- 分类选择 -->
            <el-form-item label="文章分类" prop="categoryId">
              <el-select
                v-model="formData.categoryId"
                placeholder="请选择分类"
                class="w-full"
              >
                <el-option
                  v-for="category in categoryList"
                  :key="category.id"
                  :label="category.name"
                  :value="category.id"
                />
              </el-select>
            </el-form-item>

            <!-- 标签选择 -->
            <el-form-item label="文章标签">
              <el-select
                v-model="formData.tagIds"
                multiple
                placeholder="请选择标签"
                class="w-full"
              >
                <el-option
                  v-for="tag in tagList"
                  :key="tag.id"
                  :label="tag.name"
                  :value="tag.id"
                >
                  <div class="flex items-center gap-2">
                    <span 
                      class="w-3 h-3 rounded-full" 
                      :style="{ backgroundColor: tag.color }"
                    />
                    <span>{{ tag.name }}</span>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>

            <!-- 置顶设置 -->
            <el-form-item label="置顶文章">
              <el-switch
                v-model="formData.isTop"
                :active-value="1"
                :inactive-value="0"
                active-text="是"
                inactive-text="否"
              />
            </el-form-item>

            <!-- 推荐设置 -->
            <el-form-item label="推荐文章">
              <el-switch
                v-model="formData.isRecommend"
                :active-value="1"
                :inactive-value="0"
                active-text="是"
                inactive-text="否"
              />
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 文章编辑页面
 * 实现文章的创建和编辑功能，包含 Markdown 编辑器
 * @requirements 6.1, 6.2, 6.3, 6.4, 6.5, 6.6, 6.7, 6.8
 */
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { articleApi } from '@/api/article'
import { categoryApi } from '@/api/category'
import { tagApi } from '@/api/tag'
import type { ArticleForm, Category, Tag } from '@/types'
import { ArticleStatus } from '@/types'

// ==================== 路由 ====================
const route = useRoute()
const router = useRouter()

/** 是否为编辑模式 */
const isEdit = computed(() => !!route.params.id)

/** 文章 ID（编辑模式） */
const articleId = computed(() => Number(route.params.id) || 0)

/** 页面标题 */
const pageTitle = computed(() => isEdit.value ? '编辑文章' : '新建文章')

// ==================== 状态 ====================

/** 表单引用 */
const formRef = ref<FormInstance>()

/** 设置表单引用（右侧面板） */
const settingsFormRef = ref<FormInstance>()

/** 加载状态 */
const loading = ref(false)

/** 保存中状态 */
const saving = ref(false)

/** 分类列表 */
const categoryList = ref<Category[]>([])

/** 标签列表 */
const tagList = ref<Tag[]>([])

/** 文章表单数据 */
const formData = reactive<ArticleForm>({
  title: '',
  summary: '',
  content: '',
  categoryId: undefined,
  tagIds: [],
  coverImage: '',
  status: ArticleStatus.DRAFT,
  isTop: 0,
  isRecommend: 0
})

/** 初始表单数据（用于检查修改） */
const initialFormData = ref<string>('')
// 初始化时保存一份
initialFormData.value = JSON.stringify(formData)

// ==================== 表单验证规则 ====================

/**
 * 表单验证规则
 * @requirements 6.7 - 必填字段验证
 */
const formRules: FormRules = {
  title: [
    { required: true, message: '请输入文章标题', trigger: 'blur' },
    { max: 200, message: '标题不能超过200个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入文章内容', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择文章分类', trigger: 'change' },
    { 
      validator: (_rule: unknown, value: number | undefined, callback: (error?: Error) => void) => {
        if (!value || value === 0) {
          callback(new Error('请选择文章分类'))
        } else {
          callback()
        }
      }, 
      trigger: 'change' 
    }
  ]
}

// ==================== 方法 ====================

/**
 * 加载分类列表
 */
const loadCategories = async () => {
  try {
    const result = await categoryApi.getList()
    categoryList.value = result
  } catch (error) {
    console.error('加载分类列表失败:', error)
  }
}

/**
 * 加载标签列表
 */
const loadTags = async () => {
  try {
    const result = await tagApi.getList()
    tagList.value = result
  } catch (error) {
    console.error('加载标签列表失败:', error)
  }
}

/**
 * 加载文章详情（编辑模式）
 */
const loadArticle = async () => {
  if (!isEdit.value) return
  
  loading.value = true
  try {
    const article = await articleApi.getById(articleId.value)
    // 填充表单数据
    formData.title = article.title
    formData.summary = article.summary
    formData.content = article.content
    formData.categoryId = article.categoryId || undefined
    formData.tagIds = article.tags?.map(tag => tag.id) || []
    formData.coverImage = article.coverImage || ''
    formData.status = article.status
    formData.isTop = article.isTop
    formData.isRecommend = article.isRecommend
    // 保存初始状态
    initialFormData.value = JSON.stringify(formData)
  } catch (error) {
    console.error('加载文章详情失败:', error)
    ElMessage.error('加载文章失败')
    router.push('/article/list')
  } finally {
    loading.value = false
  }
}

/**
 * 返回文章列表
 */
const handleBack = async () => {
  // 检查是否有未保存的修改
  const isModified = JSON.stringify(formData) !== initialFormData.value
  
  if (isModified) {
    try {
      await ElMessageBox.confirm(
        '当前有未保存的内容，确定要离开吗？',
        '提示',
        {
          confirmButtonText: '确定离开',
          cancelButtonText: '继续编辑',
          type: 'warning'
        }
      )
      router.push('/article/list')
    } catch {
      // 用户取消，继续编辑
    }
  } else {
    router.push('/article/list')
  }
}

/**
 * 验证表单
 * @returns 验证是否通过
 */
const validateForm = async (): Promise<boolean> => {
  if (!formRef.value || !settingsFormRef.value) return false
  
  try {
    // 同时校验左侧表单和右侧设置表单
    await Promise.all([
      formRef.value.validate(),
      settingsFormRef.value.validate()
    ])
    return true
  } catch (err: any) {
    // 获取第一个校验错误的字段和提示信息
    const errorFields = err as Record<string, { message: string }[]>
    const firstField = Object.keys(errorFields)[0]
    if (firstField && errorFields[firstField]?.[0]?.message) {
      ElMessage.warning(errorFields[firstField][0].message)
    }
    return false
  }
}

/**
 * 保存草稿
 * @requirements 6.5 - 保存草稿功能
 */
const handleSaveDraft = async () => {
  // 草稿模式下只验证标题
  if (!formData.title.trim()) {
    ElMessage.warning('请输入文章标题')
    return
  }
  
  saving.value = true
  try {
    const data: ArticleForm = {
      ...formData,
      status: ArticleStatus.DRAFT,
      ...(isEdit.value && { id: articleId.value })
    }
    
    if (isEdit.value) {
      await articleApi.update(articleId.value, data)
      ElMessage.success('草稿保存成功')
    } else {
      const result = await articleApi.create(data)
      ElMessage.success('草稿保存成功')
      // 跳转到编辑页面
      router.replace(`/article/edit/${result.id}`)
    }
  } catch (error) {
    console.error('保存草稿失败:', error)
    ElMessage.error('保存草稿失败')
  } finally {
    saving.value = false
  }
}

/**
 * 发布文章
 * @requirements 6.6 - 发布功能
 */
const handlePublish = async () => {
  // 验证表单
  const valid = await validateForm()
  if (!valid) {
    return
  }
  
  saving.value = true
  try {
    const data: ArticleForm = {
      ...formData,
      status: ArticleStatus.PUBLISHED,
      ...(isEdit.value && { id: articleId.value })
    }
    
    if (isEdit.value) {
      await articleApi.update(articleId.value, data)
      ElMessage.success('文章发布成功')
    } else {
      await articleApi.create(data)
      ElMessage.success('文章发布成功')
    }
    
    // 返回列表页
    router.push('/article/list')
  } catch (error) {
    console.error('发布文章失败:', error)
    ElMessage.error('发布文章失败')
  } finally {
    saving.value = false
  }
}

// ==================== 生命周期 ====================

onMounted(async () => {
  // 并行加载分类和标签
  await Promise.all([loadCategories(), loadTags()])
  // 编辑模式下加载文章详情
  if (isEdit.value) {
    await loadArticle()
  }
})
</script>


<style scoped>
/* 标题输入框样式 */
.title-input :deep(.el-input__inner) {
  font-size: 1.25rem;
  font-weight: 600;
}

/* Markdown 编辑器自定义样式 */
.md-editor-custom {
  border-radius: 8px;
  overflow: hidden;
}

/* 毛玻璃卡片效果 */
.glass-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

/* 标签颜色圆点 */
.tag-color-dot {
  display: inline-block;
  width: 12px;
  height: 12px;
  border-radius: 50%;
}
</style>
