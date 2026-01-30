<template>
  <div class="image-upload-container">
    <div
      class="upload-box"
      :class="{ 'has-image': modelValue }"
      :style="{ width, height }"
      v-loading="loading"
      element-loading-text="上传中..."
      @click="triggerSelect"
    >
      <!-- 有图片显示预览 -->
      <template v-if="modelValue">
        <img :src="modelValue" class="uploaded-image" alt="已上传图片" />
        <div class="image-actions">
          <span class="action-btn" @click.stop="triggerSelect">
            <el-icon><Edit /></el-icon>
          </span>
          <span class="action-btn" @click.stop="handleRemove">
            <el-icon><Delete /></el-icon>
          </span>
        </div>
      </template>

      <!-- 无图片显示上传占位符 -->
      <template v-else>
        <div class="placeholder">
          <el-icon class="upload-icon"><Plus /></el-icon>
          <span class="upload-text">点击上传</span>
        </div>
      </template>

      <input
        ref="fileInput"
        type="file"
        class="hidden-input"
        accept="image/jpeg,image/png,image/gif,image/webp"
        @change="handleFileChange"
      />
    </div>
    <div v-if="tip" class="upload-tip">{{ tip }}</div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Delete, Edit } from '@element-plus/icons-vue'
import { fileApi } from '@/api/file'

defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  width: {
    type: String,
    default: '100%'
  },
  height: {
    type: String,
    default: '200px'
  },
  tip: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue', 'change'])

const loading = ref(false)
const fileInput = ref<HTMLInputElement>()

const triggerSelect = () => {
  fileInput.value?.click()
}

const handleFileChange = async (e: Event) => {
  const target = e.target as HTMLInputElement
  const file = target.files?.[0]

  if (!file) return

  // 验证
  const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
  if (!validTypes.includes(file.type)) {
    ElMessage.error('请上传 JPG、PNG、GIF 或 WebP 格式的图片')
    target.value = ''
    return
  }

  const maxSize = 10 * 1024 * 1024 // 10MB
  if (file.size > maxSize) {
    ElMessage.error('图片大小不能超过 10MB')
    target.value = ''
    return
  }

  try {
    loading.value = true
    const res = await fileApi.uploadImage(file)
    emit('update:modelValue', res.url)
    emit('change', res.url)
    ElMessage.success('上传成功')
  } catch (error) {
    console.error('上传失败', error)
  } finally {
    loading.value = false
    target.value = ''
  }
}

const handleRemove = () => {
  emit('update:modelValue', '')
  emit('change', '')
}
</script>

<style scoped>
.image-upload-container {
  width: 100%;
}

.upload-box {
  position: relative;
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: var(--el-fill-color-lighter);
}

.upload-box:hover {
  border-color: var(--el-color-primary);
}

.uploaded-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: var(--el-text-color-secondary);
}

.upload-icon {
  font-size: 28px;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 14px;
}

.image-actions {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  opacity: 0;
  transition: opacity 0.3s;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
}

.upload-box:hover .image-actions {
  opacity: 1;
}

.action-btn {
  color: #fff;
  font-size: 20px;
  cursor: pointer;
}

.action-btn:hover {
  color: var(--el-color-primary);
}

.hidden-input {
  display: none;
}

.upload-tip {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-top: 8px;
  line-height: 1.4;
}
</style>
