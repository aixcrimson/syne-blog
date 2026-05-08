<template>
  <div class="cover-manage-tab">
    <div class="mb-6">
      <h2 class="text-lg font-semibold text-gray-800 mb-2 flex items-center gap-2">
        <el-icon><Picture /></el-icon>
        随机图库管理
      </h2>
      <p class="text-sm text-gray-500">上传的图片将被自动居中裁剪，并转换为高压缩率的 WebP 格式保存至独立图库，用作文章或页面的随机封面。</p>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <!-- PC端图库上传 -->
      <div class="border border-gray-100 rounded-lg p-6 flex flex-col gap-4 shadow-sm hover:shadow-md transition-shadow">
        <div>
          <h3 class="font-medium text-gray-800 text-base mb-1">PC端图库</h3>
          <p class="text-sm text-gray-500">将裁剪为 1200×630 比例</p>
        </div>
        <div class="mt-auto pt-4">
          <el-button 
            type="primary" 
            plain 
            class="w-full sm:w-auto"
            @click="triggerCoverUpload('pc')" 
            :loading="coverUploadLoading.pc"
          >
            <el-icon class="mr-1"><Upload /></el-icon>
            上传 PC 端图库
          </el-button>
        </div>
      </div>

      <!-- 移动端图库上传 -->
      <div class="border border-gray-100 rounded-lg p-6 flex flex-col gap-4 shadow-sm hover:shadow-md transition-shadow">
        <div>
          <h3 class="font-medium text-gray-800 text-base mb-1">移动端图库</h3>
          <p class="text-sm text-gray-500">将裁剪为 600×800 比例</p>
        </div>
        <div class="mt-auto pt-4">
          <el-button 
            type="success" 
            plain 
            class="w-full sm:w-auto"
            @click="triggerCoverUpload('mobile')" 
            :loading="coverUploadLoading.mobile"
          >
            <el-icon class="mr-1"><Upload /></el-icon>
            上传移动端图库
          </el-button>
        </div>
      </div>
    </div>

    <!-- 隐藏的 input -->
    <input
      ref="coverInputRef"
      type="file"
      accept="image/jpeg,image/png,image/gif,image/webp"
      class="hidden"
      @change="handleCoverChange"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Picture, Upload } from '@element-plus/icons-vue'
import { fileApi } from '@/api/file'
import { processCoverImage, type CoverType } from '@/utils/image'

defineOptions({
  name: "CoverManageTab"
})

// ==================== 引用与状态 ====================
const coverInputRef = ref<HTMLInputElement>()
const coverUploadLoading = reactive({ pc: false, mobile: false })
const currentCoverUploadType = ref<CoverType>('pc')

// ==================== 方法 ====================

/**
 * 触发图库上传
 */
const triggerCoverUpload = (type: CoverType) => {
  currentCoverUploadType.value = type
  coverInputRef.value?.click()
}

/**
 * 处理图库文件选择并上传
 */
const handleCoverChange = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]

  if (!file) return

  const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
  if (!validTypes.includes(file.type)) {
    ElMessage.error('请上传 JPG、PNG、GIF 或 WebP 格式的图片')
    return
  }

  const type = currentCoverUploadType.value
  coverUploadLoading[type] = true

  try {
    // 1. 前端处理图片：居中裁剪并转 WebP
    const webpBlob = await processCoverImage(file, type)
    
    // 2. 调用上传 API
    await fileApi.uploadCover(webpBlob, type)
    ElMessage.success(`图库 (${type === 'pc' ? 'PC端' : '移动端'}) 上传成功`)
  } catch (error: any) {
    console.error('图库上传失败:', error)
    ElMessage.error(error.message || '上传失败，请重试')
  } finally {
    coverUploadLoading[type] = false
    target.value = ''
  }
}
</script>

<style scoped>
.cover-manage-tab {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(5px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
