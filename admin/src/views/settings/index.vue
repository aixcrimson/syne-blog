<template>
  <div class="settings-page p-6">
    <!-- 页面标题 -->
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-800">个人设置</h1>
      <p class="text-gray-500 mt-1">管理您的个人信息和账号安全</p>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- 个人信息卡片 -->
      <div class="glass-card p-6 rounded-lg">
        <h2 class="text-lg font-semibold text-gray-800 mb-6 flex items-center gap-2">
          <el-icon><User /></el-icon>
          个人信息
        </h2>

        <el-form
          ref="profileFormRef"
          :model="profileForm"
          :rules="profileRules"
          :label-width="isMobile ? 'auto' : '80px'"
          :label-position="isMobile ? 'top' : 'left'"
        >
          <!-- 头像上传 -->
          <el-form-item label="头像" prop="avatar">
            <div class="flex items-center gap-4" :class="isMobile ? 'flex-col items-start' : ''">
              <div class="relative w-20 h-20 overflow-hidden rounded-full group flex-shrink-0">
                <el-avatar
                  :size="80"
                  :src="avatarPreview || profileForm.avatar"
                  class="w-full h-full object-cover"
                >
                  {{ userStore.username?.charAt(0)?.toUpperCase() }}
                </el-avatar>
                <div
                  class="absolute inset-0 bg-gray-800/75 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-200 cursor-pointer"
                  @click="triggerAvatarUpload"
                >
                  <el-icon class="text-white text-2xl"><Camera /></el-icon>
                </div>
              </div>
              <div class="flex flex-col gap-2">
                <el-button size="small" @click="triggerAvatarUpload">
                  <el-icon class="mr-1"><Upload /></el-icon>
                  上传头像
                </el-button>
                <span class="text-xs text-gray-400">支持 JPG、PNG 格式，建议尺寸 200x200</span>
              </div>
              <input
                ref="avatarInputRef"
                type="file"
                accept="image/jpeg,image/png,image/gif"
                class="hidden"
                @change="handleAvatarChange"
              />
            </div>
          </el-form-item>

          <!-- 用户名（只读） -->
          <el-form-item label="用户名">
            <el-input 
              :model-value="userStore.username" 
              disabled 
              placeholder="用户名"
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
            <span class="text-xs text-gray-400 mt-1">用户名不可修改</span>
          </el-form-item>

          <!-- 邮箱（只读） -->
          <el-form-item label="邮箱">
            <el-input 
              :model-value="userStore.userInfo?.email" 
              disabled 
              placeholder="邮箱"
            >
              <template #prefix>
                <el-icon><Message /></el-icon>
              </template>
            </el-input>
            <span class="text-xs text-gray-400 mt-1">邮箱不可修改</span>
          </el-form-item>

          <!-- 个人简介 -->
          <el-form-item label="简介" prop="bio">
            <el-input
              v-model="profileForm.bio"
              type="textarea"
              :rows="3"
              placeholder="介绍一下自己吧..."
              maxlength="200"
              show-word-limit
            />
          </el-form-item>

          <!-- GitHub 链接 -->
          <el-form-item label="GitHub" prop="github">
            <el-input
              v-model="profileForm.github"
              placeholder="https://github.com/username"
              clearable
            >
              <template #prefix>
                <el-icon><Link /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <!-- Bilibili 链接 -->
          <el-form-item label="Bilibili" prop="bilibili">
            <el-input
              v-model="profileForm.bilibili"
              placeholder="https://space.bilibili.com/uid"
              clearable
            >
              <template #prefix>
                <el-icon><VideoCamera /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <!-- 保存按钮 -->
          <el-form-item>
            <div class="flex gap-2" :class="isMobile ? 'w-full' : ''">
              <el-button 
                type="primary" 
                :loading="profileLoading"
                :class="isMobile ? 'flex-1' : ''"
                @click="handleSaveProfile"
              >
                <el-icon class="mr-1"><Check /></el-icon>
                保存修改
              </el-button>
              <el-button @click="resetProfileForm" :class="isMobile ? 'flex-1' : ''">
                <el-icon class="mr-1"><RefreshLeft /></el-icon>
                重置
              </el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>

      <!-- 修改密码卡片 -->
      <div class="glass-card p-6 rounded-lg">
        <h2 class="text-lg font-semibold text-gray-800 mb-6 flex items-center gap-2">
          <el-icon><Lock /></el-icon>
          修改密码
        </h2>

        <el-form
          ref="passwordFormRef"
          :model="passwordForm"
          :rules="passwordRules"
          :label-width="isMobile ? 'auto' : '100px'"
          :label-position="isMobile ? 'top' : 'left'"
        >
          <!-- 原密码 -->
          <el-form-item label="原密码" prop="oldPassword">
            <el-input
              v-model="passwordForm.oldPassword"
              type="password"
              placeholder="请输入原密码"
              show-password
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <!-- 新密码 -->
          <el-form-item label="新密码" prop="newPassword">
            <el-input
              v-model="passwordForm.newPassword"
              type="password"
              placeholder="请输入新密码（6-32位）"
              show-password
            >
              <template #prefix>
                <el-icon><Key /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <!-- 确认密码 -->
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="passwordForm.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              show-password
            >
              <template #prefix>
                <el-icon><Key /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <!-- 修改按钮 -->
          <el-form-item>
            <div class="flex gap-2" :class="isMobile ? 'w-full' : ''">
              <el-button 
                type="primary" 
                :loading="passwordLoading"
                :class="isMobile ? 'flex-1' : ''"
                @click="handleChangePassword"
              >
                <el-icon class="mr-1"><Check /></el-icon>
                修改密码
              </el-button>
              <el-button @click="resetPasswordForm" :class="isMobile ? 'flex-1' : ''">
                <el-icon class="mr-1"><RefreshLeft /></el-icon>
                重置
              </el-button>
            </div>
          </el-form-item>
        </el-form>

        <!-- 密码安全提示 -->
        <div class="mt-6 p-4 bg-blue-50 rounded-lg">
          <h3 class="text-sm font-medium text-blue-800 mb-2">密码安全提示</h3>
          <ul class="text-xs text-blue-600 space-y-1">
            <li>• 密码长度应为 6-32 个字符</li>
            <li>• 建议使用字母、数字和特殊字符的组合</li>
            <li>• 请勿使用与其他网站相同的密码</li>
            <li>• 定期更换密码可提高账号安全性</li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 个人设置页面
 * 实现个人信息编辑和密码修改功能
 * @requirements 12.1, 12.2, 12.3, 12.4, 12.5
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { 
  User, Lock, Key, Check, RefreshLeft, 
  Camera, Upload, Message, Link, VideoCamera 
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { userApi } from '@/api/user'
import type { UpdateProfileParams, ChangePasswordParams } from '@/types'
import { useResponsive } from '@/utils/useResponsive'

// 响应式状态
const { isMobile } = useResponsive()

// ==================== Store ====================
const userStore = useUserStore()


// ==================== 表单引用 ====================
const profileFormRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()
const avatarInputRef = ref<HTMLInputElement>()

// ==================== 加载状态 ====================
const profileLoading = ref(false)
const passwordLoading = ref(false)

// ==================== 头像预览 ====================
const avatarPreview = ref<string>('')

// ==================== 个人信息表单 ====================
const profileForm = reactive<UpdateProfileParams>({
  avatar: '',
  bio: '',
  github: '',
  bilibili: ''
})

// ==================== 密码表单 ====================
const passwordForm = reactive<ChangePasswordParams>({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// ==================== 表单验证规则 ====================

/**
 * URL 验证函数
 */
const validateUrl = (_rule: unknown, value: string, callback: (error?: Error) => void) => {
  if (!value) {
    callback()
    return
  }
  try {
    new URL(value)
    callback()
  } catch {
    callback(new Error('请输入有效的 URL 地址'))
  }
}

/**
 * 个人信息表单验证规则
 */
const profileRules: FormRules<UpdateProfileParams> = {
  bio: [
    { max: 200, message: '简介不能超过 200 个字符', trigger: 'blur' }
  ],
  github: [
    { validator: validateUrl, trigger: 'blur' }
  ],
  bilibili: [
    { validator: validateUrl, trigger: 'blur' }
  ]
}

/**
 * 确认密码验证函数
 * @requirements 12.5 - 新密码与确认密码一致性验证
 */
const validateConfirmPassword = (_rule: unknown, value: string, callback: (error?: Error) => void) => {
  if (!value) {
    callback(new Error('请再次输入新密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

/**
 * 密码表单验证规则
 * @requirements 12.4, 12.5
 */
const passwordRules: FormRules<ChangePasswordParams> = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度为 6-32 个字符', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度为 6-32 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// ==================== 方法 ====================

/**
 * 初始化个人信息表单
 * 从用户store中获取信息并填充表单
 */
const initProfileForm = () => {
  const userInfo = userStore.userInfo
  if (userInfo) {
    // 从用户信息中填充表单字段
    profileForm.avatar = userInfo.avatar || ''
    profileForm.bio = userInfo.bio || ''
    profileForm.github = userInfo.github || ''
    profileForm.bilibili = userInfo.bilibili || ''
  }
}

/**
 * 触发头像上传
 */
const triggerAvatarUpload = () => {
  avatarInputRef.value?.click()
}

/**
 * 处理头像文件选择
 * @requirements 12.2 - 头像上传预览
 */
const handleAvatarChange = (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  
  if (!file) return
  
  // 验证文件类型
  const validTypes = ['image/jpeg', 'image/png', 'image/gif']
  if (!validTypes.includes(file.type)) {
    ElMessage.error('请上传 JPG、PNG 或 GIF 格式的图片')
    return
  }
  
  // 验证文件大小（最大 2MB）
  const maxSize = 2 * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.error('图片大小不能超过 2MB')
    return
  }
  
  // 创建预览
  const reader = new FileReader()
  reader.onload = (e) => {
    avatarPreview.value = e.target?.result as string
    profileForm.avatar = avatarPreview.value
  }
  reader.readAsDataURL(file)
  
  // 清空 input，允许重复选择同一文件
  target.value = ''
}

/**
 * 保存个人信息
 * @requirements 12.1
 */
const handleSaveProfile = async () => {
  if (!profileFormRef.value) return
  
  const valid = await profileFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  profileLoading.value = true
  
  try {
    await userApi.updateProfile(profileForm)
    
    // 更新本地用户信息
    userStore.updateUserInfo({
      avatar: profileForm.avatar
    })
    
    ElMessage.success('个人信息保存成功')
    avatarPreview.value = ''
  } catch (error) {
    console.error('保存个人信息失败:', error)
  } finally {
    profileLoading.value = false
  }
}

/**
 * 重置个人信息表单
 */
const resetProfileForm = () => {
  avatarPreview.value = ''
  initProfileForm()
  profileFormRef.value?.clearValidate()
}

/**
 * 修改密码
 * @requirements 12.3, 12.4, 12.5
 */
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  
  const valid = await passwordFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  passwordLoading.value = true
  
  try {
    await userApi.changePassword(passwordForm)
    ElMessage.success('密码修改成功，请重新登录')
    resetPasswordForm()
    
    // 可选：修改密码后自动退出登录
    // userStore.logout()
    // router.push('/login')
  } catch (error) {
    console.error('修改密码失败:', error)
  } finally {
    passwordLoading.value = false
  }
}

/**
 * 重置密码表单
 */
const resetPasswordForm = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordFormRef.value?.clearValidate()
}

// ==================== 生命周期 ====================
onMounted(() => {
  // 直接从 store 中初始化表单，登录时已经获取了完整用户信息
  initProfileForm()
})
</script>

<style scoped>
/* 头像悬停效果 */
.group:hover .el-avatar {
  filter: brightness(0.8);
}

/* 表单项样式 */
:deep(.el-form-item__label) {
  color: #374151;
  font-weight: 500;
}

:deep(.el-input__wrapper) {
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--el-color-primary-light-5) inset;
}

:deep(.el-textarea__inner) {
  transition: all 0.3s ease;
}

:deep(.el-textarea__inner:hover) {
  box-shadow: 0 0 0 1px var(--el-color-primary-light-5) inset;
}

/* 按钮样式 */
:deep(.el-button--primary) {
  transition: all 0.3s ease;
}

:deep(.el-button--primary:hover:not(:disabled)) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(var(--color-primary-500), 0.3);
}

/* 头像样式 */
:deep(.el-avatar) {
  background-color: var(--el-color-primary-light-7);
  color: var(--el-color-primary);
  font-weight: 600;
}
</style>
