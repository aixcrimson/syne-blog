<template>
  <div class="login-page min-h-screen flex items-center justify-center">
    <!-- 背景装饰 -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none">
      <div class="absolute -top-40 -right-40 w-80 h-80 bg-primary-200/30 rounded-full blur-3xl"></div>
      <div class="absolute -bottom-40 -left-40 w-80 h-80 bg-primary-300/20 rounded-full blur-3xl"></div>
    </div>

    <!-- 登录卡片 -->
    <div class="glass-card p-8 w-full max-w-md relative z-10">
      <!-- Logo 和标题 -->
      <div class="text-center mb-8">
        <div class="w-16 h-16 mx-auto mb-4 rounded-2xl bg-white flex items-center justify-center p-1 shadow-lg border border-gray-100">
          <img
            :src="logo"
            alt="Logo"
            class="w-full h-full object-contain rounded-xl"
          />
        </div>
        <h1 class="text-2xl font-bold text-gray-800">博客管理系统</h1>
        <p class="text-gray-500 mt-2">请登录您的账号</p>
      </div>

      <!-- 登录表单 -->
      <el-form
        ref="formRef"
        :model="loginForm"
        :rules="rules"
        size="large"
        @keyup.enter="handleKeyEnter"
      >
        <!-- 用户名 -->
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>

        <!-- 密码 -->
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            :type="showPassword ? 'text' : 'password'"
            placeholder="请输入密码"
            :prefix-icon="Lock"
          >
            <template #suffix>
              <el-icon 
                class="cursor-pointer hover:text-primary-500 transition-colors"
                @click="togglePasswordVisibility"
              >
                <View v-if="showPassword" />
                <Hide v-else />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <!-- 登录按钮 -->
        <el-form-item class="mt-6">
          <el-button
            type="primary"
            class="w-full"
            :loading="loading"
            :disabled="isSubmitDisabled"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 底部提示 -->
      <div class="text-center text-sm text-gray-400 mt-6">
        <p>© Syne's Blog Admin System</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 登录页面
 * 实现用户登录表单、验证和登录逻辑
 * @requirements 3.2, 3.3
 */
import { ref, reactive, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, Lock, View, Hide } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { authApi } from '@/api/auth'
import type { LoginParams, UserInfo } from '@/types'
import logo from '@/assets/common/logo.png'

// ==================== 路由 ====================
const router = useRouter()
const route = useRoute()

// ==================== Store ====================
const userStore = useUserStore()

// ==================== 状态 ====================

/** 表单引用 */
const formRef = ref<FormInstance>()

/** 登录表单数据 */
const loginForm = reactive<LoginParams>({
  username: '',
  password: ''
})

/** 是否显示密码 */
const showPassword = ref(false)

/** 是否正在提交 */
const loading = ref(false)

// ==================== 表单验证规则 ====================

/**
 * 登录表单验证规则
 * @requirements 3.3 - 表单验证
 */
const rules: FormRules<LoginParams> = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度为 2-20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度为 6-32 个字符', trigger: 'blur' }
  ]
}

// ==================== 计算属性 ====================

/** 登录按钮是否禁用 */
const isSubmitDisabled = computed(() => {
  return !loginForm.username || !loginForm.password || loading.value
})

// ==================== 方法 ====================

/**
 * 切换密码显示/隐藏
 */
const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value
}

/**
 * 处理登录提交
 * @requirements 3.2 - 验证凭据并跳转到首页
 * @requirements 3.3 - 显示错误提示信息
 */
const handleLogin = async () => {
  // 表单验证
  if (!formRef.value) return
  
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true

  try {
    // 调用登录 API
    const response = await authApi.login(loginForm)

    // 构建用户信息对象（使用登录接口返回的信息）
    const userInfo: UserInfo = {
      id: response.userId,
      username: response.username,
      email: response.email,
      role: response.role,
      avatar: response.avatar || '',
      bio: response.bio,
      github: response.github,
      bilibili: response.bilibili
    }

    // 一次性保存所有登录状态
    userStore.loginSuccess(response.token, userInfo)

    ElMessage.success('登录成功')

    // 跳转到原始目标页面或首页
    const redirect = (route.query.redirect as string) || '/dashboard'
    router.push(redirect)
  } catch (error: any) {
    // 错误已在 request.ts 中统一处理，这里不需要额外处理
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 处理回车键提交
 */
const handleKeyEnter = () => {
  if (!isSubmitDisabled.value) {
    handleLogin()
  }
}
</script>


<style scoped>
/* 主题色变量引用 */
.bg-primary-200\/30 {
  background-color: var(--color-primary-100);
  opacity: 0.45;
}

.bg-primary-300\/20 {
  background-color: var(--color-primary-200);
  opacity: 0.25;
}

.hover\:text-primary-500:hover {
  color: var(--color-primary-500);
}

/* 登录页面特殊样式 */
.login-page {
  background: linear-gradient(135deg, #fafaf9 0%, #f5f5f4 100%);
}

/* 输入框聚焦效果 */
:deep(.el-input__wrapper) {
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--el-color-primary-light-5) inset;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px var(--el-color-primary) inset;
}

/* 按钮样式 */
:deep(.el-button--primary) {
  background: var(--el-color-primary);
  border: 1px solid var(--el-color-primary);
  transition: all 0.3s ease;
}

:deep(.el-button--primary:hover:not(:disabled)) {
  box-shadow: var(--shadow-md);
}

:deep(.el-button--primary:active:not(:disabled)) {
  box-shadow: var(--shadow-sm);
}
</style>
