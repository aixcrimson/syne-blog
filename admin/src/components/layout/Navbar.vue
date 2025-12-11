<template>
  <header 
    class="navbar fixed top-0 right-0 h-16 z-40 glass-navbar transition-all duration-300"
    :class="collapsed ? 'left-16' : 'left-60'"
  >
    <div class="h-full flex items-center justify-between px-6">
      <!-- 左侧：折叠按钮 + 面包屑 -->
      <div class="flex items-center gap-4">
        <!-- 折叠按钮 -->
        <el-icon 
          class="text-xl text-gray-600 cursor-pointer hover:text-primary-500 transition-colors"
          @click="emit('toggle')"
        >
          <Fold v-if="!collapsed" />
          <Expand v-else />
        </el-icon>
        
        <!-- 面包屑（可选，后续扩展） -->
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <!-- 右侧：主题切换 + 用户信息 -->
      <div class="flex items-center gap-4">
        <!-- 主题色切换 -->
        <el-dropdown trigger="click" @command="handleThemeChange">
          <div class="flex items-center gap-2 px-3 py-1.5 rounded-lg cursor-pointer hover:bg-black/5 dark:hover:bg-white/10 transition-colors">
            <div
              class="w-4 h-4 rounded-full"
              :style="{ backgroundColor: currentThemeColor }"
            ></div>
            <span class="text-sm text-gray-600 dark:text-gray-300">主题</span>
            <el-icon class="text-gray-400 dark:text-gray-500">
              <ArrowDown />
            </el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item 
                v-for="theme in themes" 
                :key="theme.value"
                :command="theme.value"
              >
                <div class="flex items-center gap-2">
                  <div 
                    class="w-4 h-4 rounded-full"
                    :style="{ backgroundColor: theme.color }"
                  ></div>
                  <span>{{ theme.name }}</span>
                  <el-icon v-if="currentTheme === theme.value" class="ml-2 text-primary-500">
                    <Check />
                  </el-icon>
                </div>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>

        <!-- 明暗模式切换开关 -->
        <el-tooltip content="切换明暗模式" placement="bottom">
          <div
            class="cursor-pointer p-2 rounded-lg transition-all duration-300 hover:bg-bg-tertiary"
            @click="toggleThemeMode"
          >
            <div
              class="w-12 h-6 bg-bg-tertiary rounded-full relative transition-all duration-300 border border-border-primary"
              :style="{
                '--toggle-position': isDarkMode ? '24px' : '2px',
                '--toggle-bg': 'var(--color-primary-500)'
              }"
            >
              <div
                class="absolute top-0.5 w-[18px] h-[18px] rounded-full flex items-center justify-center transition-all duration-300 text-white text-xs"
                :style="{
                  'left': 'var(--toggle-position)',
                  'background': 'var(--toggle-bg)'
                }"
              >
                <el-icon v-if="!isDarkMode" size="14"><Sunny /></el-icon>
                <el-icon v-else size="14"><Moon /></el-icon>
              </div>
            </div>
          </div>
        </el-tooltip>

        <!-- 分隔线 -->
        <div class="h-6 w-px bg-gray-200 dark:bg-gray-700"></div>

        <!-- 用户信息下拉菜单 -->
        <el-dropdown trigger="click">
          <div class="flex items-center gap-2 px-3 py-1.5 rounded-lg cursor-pointer hover:bg-black/5 dark:hover:bg-white/10 transition-colors">
            <!-- 头像 -->
            <el-avatar
              :size="32"
              :src="userInfo.avatar"
              class="bg-primary-500"
            >
              <el-icon><UserFilled /></el-icon>
            </el-avatar>
            <!-- 用户名 -->
            <span class="text-sm font-medium text-gray-700 dark:text-gray-200">{{ userInfo.username }}</span>
            <el-icon class="text-gray-400 dark:text-gray-500">
              <ArrowDown />
            </el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="router.push('/settings')">
                <el-icon><Setting /></el-icon>
                <span class="ml-2">个人设置</span>
              </el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">
                <el-icon class="text-red-500"><SwitchButton /></el-icon>
                <span class="ml-2 text-red-500">退出登录</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
/**
 * 顶部导航栏组件
 * 实现用户信息显示、主题色切换和退出登录功能
 */
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

/**
 * 组件属性
 */
interface Props {
  /** 侧边栏是否收起 */
  collapsed: boolean
}

defineProps<Props>()

/**
 * 组件事件
 */
const emit = defineEmits<{
  (e: 'toggle'): void
  (e: 'logout'): void
}>()

const router = useRouter()

/**
 * 主题色配置
 */
const themes = [
  { name: '蓝色', value: 'blue', color: '#3b82f6' },
  { name: '紫色', value: 'purple', color: '#9333ea' },
  { name: '绿色', value: 'green', color: '#16a34a' },
  { name: '橙色', value: 'orange', color: '#ea580c' },
  { name: '粉色', value: 'pink', color: '#db2777' }
]

/**
 * 当前主题
 */
const currentTheme = ref(localStorage.getItem('theme') || 'blue')

/**
 * 主题模式
 */
const themeMode = ref<'light' | 'dark'>((localStorage.getItem('themeMode') as 'light' | 'dark') || 'light')

/**
 * 当前主题颜色
 */
const currentThemeColor = computed(() => {
  const theme = themes.find(t => t.value === currentTheme.value)
  return theme?.color || '#3b82f6'
})

/**
 * 是否为暗色模式
 */
const isDarkMode = computed(() => themeMode.value === 'dark')

/**
 * 模拟用户信息（后续会从 store 获取）
 */
const userInfo = computed(() => ({
  username: '管理员',
  avatar: ''
}))

/**
 * 切换主题色
 */
const handleThemeChange = (theme: string) => {
  currentTheme.value = theme
  localStorage.setItem('theme', theme)

  // 设置 data-theme 属性
  if (theme === 'blue') {
    document.documentElement.removeAttribute('data-theme')
  } else {
    document.documentElement.setAttribute('data-theme', theme)
  }

  ElMessage.success(`已切换为${themes.find(t => t.value === theme)?.name}主题`)
}

/**
 * 切换主题模式
 */
const toggleThemeMode = () => {
  themeMode.value = themeMode.value === 'light' ? 'dark' : 'light'
  localStorage.setItem('themeMode', themeMode.value)
  applyThemeMode()
}

/**
 * 应用主题模式
 */
const applyThemeMode = () => {
  const root = document.documentElement
  if (themeMode.value === 'dark') {
    root.setAttribute('data-theme-mode', 'dark')
    document.body.classList.add('dark')
  } else {
    root.removeAttribute('data-theme-mode')
    document.body.classList.remove('dark')
  }
}

/**
 * 退出登录
 */
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 清除 token
    localStorage.removeItem('token')
    
    ElMessage.success('已退出登录')
    router.push('/login')
  } catch {
    // 用户取消
  }
}

/**
 * 初始化主题
 */
const initTheme = () => {
  const savedTheme = localStorage.getItem('theme')
  if (savedTheme && savedTheme !== 'blue') {
    document.documentElement.setAttribute('data-theme', savedTheme)
  }

  // 初始化主题模式
  applyThemeMode()
}

// 初始化
onMounted(() => {
  initTheme()
})
</script>


<style scoped>
/* 毛玻璃顶部导航栏 */
.glass-navbar {
  background: var(--glass-bg);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--glass-border);
  box-shadow: var(--shadow-md);
}
</style>
