<template>
  <header class="fixed top-0 z-50 w-full shadow-sm header-glass">
    <nav class="px-4 mx-auto max-w-7xl sm:px-6 lg:px-8">
      <div class="flex justify-between items-center h-16">
        <!-- Logo - 左侧 -->
        <div class="flex items-center min-w-[200px] space-x-2">
          <div
            class="flex justify-center items-center w-8 h-8 bg-gradient-to-br rounded-lg transition-all duration-300 cursor-pointer from-primary-500 to-primary-700 hover:scale-105 hover:shadow-lg"
            @click="logoModalOpen = true"
          >
            <img
              :src="logoImage"
              alt="syne-blog logo"
              class="w-full h-full rounded-md"
            />
          </div>
          <router-link
            to="/"
            class="text-xl font-bold text-gray-900 transition-colors dark:text-gray-100 hover:text-primary-600"
          >
            syne-blog
          </router-link>
        </div>

        <!-- 导航菜单 - 中间 -->
        <div class="hidden items-center space-x-8 md:flex">
          <router-link
            v-for="item in menuItems"
            :key="item.path"
            :to="item.path"
            class="flex gap-2 items-center px-3 py-2 text-sm font-medium text-gray-700 rounded-md transition-colors dark:text-gray-300 hover:text-primary-600"
            exact-active-class="text-primary-600 bg-primary-50 dark:bg-primary-900/20"
          >
            <el-icon v-if="item.icon" :size="18">
              <component :is="item.icon" />
            </el-icon>
            {{ item.name }}
          </router-link>
        </div>

        <!-- 右侧工具栏 -->
        <div class="flex items-center space-x-3">
          <!-- GitHub 链接 -->
          <el-tooltip content="访问我的 GitHub" placement="bottom">
            <a
              :href="siteStore.authorInfo?.github || '#'"
              target="_blank"
              rel="noopener noreferrer"
              class="hidden justify-center items-center w-10 h-10 rounded-full transition-colors github-link md:flex hover:bg-gray-100"
            >
              <svg
                class="w-6 h-6 text-gray-700 hover:text-gray-900"
                fill="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  fill-rule="evenodd"
                  d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.531 1.032 1.531 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z"
                  clip-rule="evenodd"
                />
              </svg>
            </a>
          </el-tooltip>

          <!-- 搜索 -->
          <el-tooltip content="搜索" placement="bottom">
            <button
              class="hidden justify-center items-center w-10 h-10 rounded-full transition-colors github-link md:flex hover:bg-gray-100"
              @click="handleSearch"
            >
              <el-icon class="text-xl text-gray-700 hover:text-gray-900">
                <Search />
              </el-icon>
            </button>
          </el-tooltip>

          <!-- 明暗模式切换 -->
          <el-tooltip content="切换明暗模式" placement="bottom">
            <button
              class="hidden justify-center items-center w-10 h-10 rounded-full transition-colors theme-mode-btn md:flex hover:bg-gray-100 dark:hover:bg-gray-800"
              @click="appStore.toggleThemeMode"
            >
              <el-icon
                class="text-xl"
                :class="
                  appStore.isDarkMode ? 'text-yellow-400' : 'text-gray-700'
                "
              >
                <Sunny v-if="!appStore.isDarkMode" />
                <Moon v-else />
              </el-icon>
            </button>
          </el-tooltip>

          <!-- 主题颜色切换 -->
          <el-dropdown @command="handleThemeColorChange" trigger="click">
            <el-button circle class="theme-color-toggle" title="切换主题色">
              <el-icon>
                <Brush />
              </el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="blue">
                  <div class="flex items-center space-x-2">
                    <div class="w-4 h-4 bg-blue-500 rounded-full"></div>
                    <span>蓝色</span>
                    <el-icon v-if="appStore.themeColor === 'blue'" class="ml-2"
                      ><Check
                    /></el-icon>
                  </div>
                </el-dropdown-item>
                <el-dropdown-item command="purple">
                  <div class="flex items-center space-x-2">
                    <div class="w-4 h-4 bg-purple-500 rounded-full"></div>
                    <span>紫色</span>
                    <el-icon
                      v-if="appStore.themeColor === 'purple'"
                      class="ml-2"
                      ><Check
                    /></el-icon>
                  </div>
                </el-dropdown-item>
                <el-dropdown-item command="green">
                  <div class="flex items-center space-x-2">
                    <div class="w-4 h-4 bg-green-500 rounded-full"></div>
                    <span>绿色</span>
                    <el-icon v-if="appStore.themeColor === 'green'" class="ml-2"
                      ><Check
                    /></el-icon>
                  </div>
                </el-dropdown-item>
                <el-dropdown-item command="orange">
                  <div class="flex items-center space-x-2">
                    <div class="w-4 h-4 bg-orange-500 rounded-full"></div>
                    <span>橙色</span>
                    <el-icon
                      v-if="appStore.themeColor === 'orange'"
                      class="ml-2"
                      ><Check
                    /></el-icon>
                  </div>
                </el-dropdown-item>
                <el-dropdown-item command="pink">
                  <div class="flex items-center space-x-2">
                    <div class="w-4 h-4 bg-pink-500 rounded-full"></div>
                    <span>粉色</span>
                    <el-icon v-if="appStore.themeColor === 'pink'" class="ml-2"
                      ><Check
                    /></el-icon>
                  </div>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>

          <!-- 登录/用户信息 -->
          <div v-if="!userStore.isLoggedIn" class="hidden md:block">
            <el-button type="primary" round @click="router.push('/login')">
              登录
            </el-button>
          </div>
          <el-dropdown
            v-else
            trigger="click"
            @command="handleUserCommand"
          >
            <div
              class="flex gap-2 items-center pr-2 rounded-full transition-colors cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-800"
            >
              <el-avatar
                :size="32"
                :src="
                  userStore.currentUser?.avatar ||
                  'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
                "
              />
              <span
                class="hidden text-sm font-medium text-gray-700 dark:text-gray-300 lg:block"
                >{{ userStore.currentUser?.username || "用户" }}</span
              >
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item divided command="logout" class="text-red-500"
                  >退出登录</el-dropdown-item
                >
              </el-dropdown-menu>
            </template>
          </el-dropdown>

          <!-- 移动端菜单按钮 -->
          <el-button
            class="md:hidden"
            circle
            @click="mobileMenuOpen = !mobileMenuOpen"
          >
            <el-icon>
              <Menu />
            </el-icon>
          </el-button>
        </div>
      </div>

      <!-- 移动端菜单 -->
      <div v-if="mobileMenuOpen" class="py-4 border-t md:hidden">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          class="flex gap-2 items-center px-3 py-2 text-base font-medium text-gray-700 rounded-md hover:text-primary-600 hover:bg-primary-50"
          exact-active-class="text-primary-600 bg-primary-50"
          @click="mobileMenuOpen = false"
        >
          <el-icon v-if="item.icon" :size="20">
            <component :is="item.icon" />
          </el-icon>
          {{ item.name }}
        </router-link>

        <!-- 移动端 GitHub 链接 -->
        <a
          :href="siteStore.authorInfo?.github || '#'"
          target="_blank"
          rel="noopener noreferrer"
          class="flex gap-2 items-center px-3 py-2 text-base font-medium text-gray-700 rounded-md transition-colors dark:text-gray-300 hover:text-primary-600 hover:bg-primary-50 dark:hover:bg-primary-900/20"
          @click="mobileMenuOpen = false"
        >
          <el-icon :size="20">
            <svg fill="currentColor" viewBox="0 0 24 24">
              <path
                fill-rule="evenodd"
                d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.531 1.032 1.531 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z"
                clip-rule="evenodd"
              />
            </svg>
          </el-icon>
          GitHub
        </a>

        <!-- 移动端工具栏 -->
        <div
          class="grid grid-cols-2 gap-4 px-3 pt-4 mt-4 border-t border-gray-100 dark:border-gray-800"
        >
          <button
            @click="appStore.toggleThemeMode"
            class="flex gap-2 justify-center items-center p-3 bg-gray-50 rounded-xl transition-all dark:bg-gray-800/50 hover:bg-gray-100 dark:hover:bg-gray-800 active:scale-95"
          >
            <el-icon
              :size="18"
              :class="
                appStore.isDarkMode
                  ? 'text-yellow-400'
                  : 'text-gray-700 dark:text-gray-300'
              "
            >
              <Sunny v-if="!appStore.isDarkMode" />
              <Moon v-else />
            </el-icon>
            <span
              class="text-sm font-medium text-gray-700 dark:text-gray-200"
              >{{ appStore.isDarkMode ? "浅色" : "深色" }}</span
            >
          </button>

          <button
            @click="
              handleSearch();
              mobileMenuOpen = false;
            "
            class="flex gap-2 justify-center items-center p-3 bg-gray-50 rounded-xl transition-all dark:bg-gray-800/50 hover:bg-gray-100 dark:hover:bg-gray-800 active:scale-95"
          >
            <el-icon :size="18" class="text-gray-700 dark:text-gray-300"
              ><Search
            /></el-icon>
            <span class="text-sm font-medium text-gray-700 dark:text-gray-200"
              >搜索</span
            >
          </button>
        </div>

        <!-- 移动端登录按钮 -->
        <div v-if="!userStore.isLoggedIn" class="px-3 mt-4">
          <el-button
            type="primary"
            class="w-full !h-11 !rounded-xl !text-base"
            @click="
              router.push('/login');
              mobileMenuOpen = false;
            "
          >
            <el-icon class="mr-2"><User /></el-icon>
            登录 / 注册
          </el-button>
        </div>
      </div>
    </nav>
  </header>

  <!-- Logo 放大展示弹窗 -->
  <Teleport to="body">
    <Transition
      enter-active-class="transition-all duration-300 ease-out"
      enter-from-class="opacity-0"
      enter-to-class="opacity-100"
      leave-active-class="transition-all duration-200 ease-in"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div
        v-if="logoModalOpen"
        class="flex fixed inset-0 z-50 justify-center items-center bg-black bg-opacity-80 cursor-pointer"
        @click="logoModalOpen = false"
      >
        <Transition
          enter-active-class="transition-all duration-300 ease-out"
          enter-from-class="opacity-0 scale-75"
          enter-to-class="opacity-100 scale-100"
          leave-active-class="transition-all duration-200 ease-in"
          leave-from-class="opacity-100 scale-100"
          leave-to-class="opacity-0 scale-75"
        >
          <div v-if="logoModalOpen" class="relative mx-4 max-w-sm" @click.stop>
            <!-- 关闭按钮 -->
            <button
              class="absolute right-0 -top-10 p-2 text-white transition-colors hover:text-gray-300"
              @click="logoModalOpen = false"
            >
              <svg
                class="w-8 h-8"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M6 18L18 6M6 6l12 12"
                ></path>
              </svg>
            </button>

            <!-- Logo 图片 -->
            <div class="p-1 bg-white rounded-2xl shadow-2xl">
              <img
                :src="logoImage"
                alt="syne-blog logo"
                class="w-full h-full rounded-xl"
              />
            </div>

            <!-- 标题 -->
            <div class="mt-4 text-center">
              <h3 class="text-xl font-semibold text-white">syne-blog</h3>
              <p class="mt-1 text-sm text-gray-300">现代化博客平台</p>
            </div>
          </div>
        </Transition>
      </div>
    </Transition>
  </Teleport>
  <!-- 搜索弹窗 -->
  <el-dialog
    v-model="searchVisible"
    title="搜索文章"
    class="search-dialog"
    center
    destroy-on-close
  >
    <el-input
      v-model="searchKeyword"
      placeholder="请输入关键词..."
      size="large"
      clearable
      @keyup.enter="submitSearch"
    >
      <template #prefix>
        <el-icon><Search /></el-icon>
      </template>
    </el-input>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="searchVisible = false">取消</el-button>
        <el-button type="primary" @click="submitSearch"> 搜索 </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAppStore } from "@/stores/app";
import { useUserStore } from "@/stores/user";
import { useSiteStore } from "@/stores/site";
import { ElMessage } from "element-plus";
import {
  Menu,
  Brush,
  Check,
  Search,
  Sunny,
  Moon,
  House,
  Document,
  Compass,
  User,
} from "@element-plus/icons-vue";
import type { MenuItem, ThemeColor } from "@/types";
import logoImage from "@/assets/images/common/logo.png";

const appStore = useAppStore();
const userStore = useUserStore();
const siteStore = useSiteStore();

const mobileMenuOpen = ref(false);
const logoModalOpen = ref(false);

const menuItems: MenuItem[] = [
  { name: "首页", path: "/", icon: House },
  { name: "文章", path: "/articles", icon: Document },
  { name: "网站导航", path: "/navigation", icon: Compass },
  { name: "关于", path: "/about", icon: User },
];

/**
 * 处理搜索按钮点击
 */
const router = useRouter();
const searchVisible = ref(false);
const searchKeyword = ref("");

/**
 * 处理搜索按钮点击
 */
const handleSearch = () => {
  searchVisible.value = true;
};

const submitSearch = () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning("请输入搜索内容");
    return;
  }
  router.push({
    path: "/articles",
    query: { keyword: searchKeyword.value },
  });
  searchVisible.value = false;
  searchKeyword.value = "";
};
const handleUserCommand = (command: string) => {
  if (command === "profile") {
    router.push("/profile");
  } else if (command === "logout") {
    handleLogout();
  }
};


const handleLogout = () => {
  userStore.logout();
  ElMessage.success("已退出登录");
  router.push("/");
};

/**
 * 处理主题颜色切换
 */
const handleThemeColorChange = (color: ThemeColor) => {
  console.log("Header: 切换主题色到", color);
  console.log("当前 appStore:", appStore);
  appStore.setThemeColor(color);
  console.log("切换完成，新颜色:", appStore.themeColor);
};
</script>

<style scoped>
/* 毛玻璃效果 Header */
.header-glass {
  background: var(--glass-bg);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--glass-border);
}

.nav-link {
  position: relative;
}

.nav-link::after {
  content: "";
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 2px;
  background: var(--color-primary-600);
  transition: all 0.3s ease;
  transform: translateX(-50%);
}

.nav-link:hover::after,
.nav-link.router-link-exact-active::after {
  width: 80%;
}

.github-link {
  transition: all 0.3s ease;
}

.github-link:hover {
  transform: scale(1.1);
}

.theme-color-toggle {
  transition: all 0.3s ease;
}

.theme-color-toggle:hover {
  transform: rotate(15deg);
}
</style>

<style>
/* 搜索弹窗响应式 */
.search-dialog {
  max-width: 500px;
  border-radius: 16px !important;
  overflow: hidden;
}

@media (max-width: 768px) {
  .search-dialog {
    width: 90% !important;
    margin-top: 10vh !important;
  }

  .search-dialog .el-dialog__body {
    padding: 20px 16px !important;
  }
}
</style>
