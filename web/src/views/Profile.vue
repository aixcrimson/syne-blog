<template>
  <div class="px-4 pt-20 pb-12 min-h-screen sm:px-6 lg:px-8">
    <div class="mx-auto max-w-4xl">
      <div class="overflow-hidden rounded-2xl shadow-xl glass-card">
        <!-- 头部背景 (可选) -->
        <div class="h-32 bg-gradient-to-r to-purple-600 from-primary-500"></div>

        <div class="px-8 pb-8">
          <!-- 头像区域 -->
          <div class="flex relative justify-between items-end -mt-16 mb-6">
            <div class="relative group">
              <div
                class="overflow-hidden w-32 h-32 bg-white rounded-full border-4 border-white shadow-xl transition-transform duration-300 dark:border-gray-800 hover:scale-105"
              >
                <img
                  :src="
                    userStore.currentUser?.avatar ||
                    'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
                  "
                  alt="Avatar"
                  class="object-cover w-full h-full"
                />
              </div>
              <!-- 相机图标 -->
              <div
                class="flex absolute right-2 bottom-2 justify-center items-center w-10 h-10 text-white rounded-full shadow-lg transition-transform duration-300 cursor-pointer bg-primary-500 hover:bg-primary-600 hover:scale-110 active:scale-95"
                title="更换头像"
              >
                <el-icon :size="20"><Camera /></el-icon>
              </div>
            </div>
            <div class="mb-4">
              <el-button type="primary" @click="handleSave" :loading="saving"
                >保存修改</el-button
              >
            </div>
          </div>

          <!-- 内容区域 -->
          <el-tabs v-model="activeTab" class="demo-tabs">
            <el-tab-pane label="个人资料" name="profile">
              <el-form label-position="top" class="mt-4">
                <div class="grid grid-cols-1 gap-6 md:grid-cols-2">
                  <el-form-item label="用户名">
                    <el-input
                      v-model="formData.username"
                      disabled
                      placeholder="登录账号"
                    />
                  </el-form-item>
                  <el-form-item label="邮箱">
                    <el-input
                      v-model="formData.email"
                      disabled
                      placeholder="绑定邮箱"
                    />
                  </el-form-item>
                  <el-form-item label="GitHub">
                    <el-input
                      v-model="formData.github"
                      placeholder="GitHub 主页链接"
                    />
                  </el-form-item>
                  <el-form-item label="Bilibili">
                    <el-input
                      v-model="formData.bilibili"
                      placeholder="Bilibili 主页链接"
                    />
                  </el-form-item>
                </div>
                <el-form-item label="个人简介" class="mt-4">
                  <el-input
                    v-model="formData.bio"
                    type="textarea"
                    rows="4"
                    placeholder="介绍一下你自己..."
                  />
                </el-form-item>
              </el-form>
            </el-tab-pane>
            <el-tab-pane label="账号安全" name="security">
              <div class="py-4 space-y-6">
                <div
                  class="flex justify-between items-center p-4 bg-gray-50 rounded-lg dark:bg-gray-800/50"
                >
                  <div>
                    <h4 class="font-medium text-gray-900 dark:text-gray-100">
                      修改密码
                    </h4>
                    <p class="mt-1 text-sm text-gray-500">
                      定期修改密码可以保护你的账号安全
                    </p>
                  </div>
                  <el-button>修改</el-button>
                </div>
              </div>
            </el-tab-pane>

            <!-- 我的点赞 -->
            <el-tab-pane label="我的点赞" name="liked">
              <div v-loading="loading" class="py-4">
                <div v-if="articles.length > 0" class="space-y-4">
                  <ArticleCard
                    v-for="article in articles"
                    :key="article.id"
                    :article="article"
                    class="transition-all duration-300 transform hover:-translate-y-1 hover:shadow-lg"
                  />
                  <!-- 分页 -->
                  <div class="flex justify-center pt-8">
                    <el-pagination
                      v-model:current-page="currentPage"
                      v-model:page-size="pageSize"
                      :total="total"
                      layout="prev, pager, next"
                      background
                      @current-change="handlePageChange"
                    />
                  </div>
                </div>
                <el-empty v-else description="暂无点赞文章" />
              </div>
            </el-tab-pane>

            <!-- 我的收藏 -->
            <el-tab-pane label="我的收藏" name="favorite">
              <div v-loading="loading" class="py-4">
                <div v-if="articles.length > 0" class="space-y-4">
                  <ArticleCard
                    v-for="article in articles"
                    :key="article.id"
                    :article="article"
                    class="transition-all duration-300 transform hover:-translate-y-1 hover:shadow-lg"
                  />
                  <!-- 分页 -->
                  <div class="flex justify-center pt-8">
                    <el-pagination
                      v-model:current-page="currentPage"
                      v-model:page-size="pageSize"
                      :total="total"
                      layout="prev, pager, next"
                      background
                      @current-change="handlePageChange"
                    />
                  </div>
                </div>
                <el-empty v-else description="暂无收藏文章" />
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from "vue";
import { useUserStore } from "@/stores/user";
import { ElMessage } from "element-plus";
import { Camera } from "@element-plus/icons-vue";
import { userApi } from "@/api/user";
import { articleApi } from "@/api/article";
import ArticleCard from "@/components/ArticleCard.vue";

const userStore = useUserStore();
const activeTab = ref("profile");
const saving = ref(false);

const formData = reactive({
  username: "",
  email: "",
  bio: "",
  github: "",
  bilibili: "",
  avatar: "",
});

onMounted(() => {
  // 初始化数据
  if (userStore.currentUser) {
    const user = userStore.currentUser;
    formData.username = user.username || "";
    // 兼容处理可能缺失的字段
    formData.email = (user as any).email || "";
    formData.bio = (user as any).bio || "";
    formData.github = (user as any).github || "";
    formData.bilibili = (user as any).bilibili || "";
    formData.avatar = user.avatar || "";
  }
});

// 文章列表相关
const loading = ref(false);
const articles = ref<any[]>([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

const loadArticles = async (type: "liked" | "favorite") => {
  loading.value = true;
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
    };
    const res =
      type === "liked"
        ? await articleApi.getLikedList(params)
        : await articleApi.getFavoriteList(params);

    articles.value = res.list;
    total.value = res.total;
  } catch (error) {
    ElMessage.error("获取文章列表失败");
  } finally {
    loading.value = false;
  }
};

// 监听 Tab 切换
watch(activeTab, (newTab) => {
  if (newTab === "liked" || newTab === "favorite") {
    currentPage.value = 1;
    loadArticles(newTab);
  }
});

const handlePageChange = (page: number) => {
  currentPage.value = page;
  if (activeTab.value === "liked" || activeTab.value === "favorite") {
    loadArticles(activeTab.value as "liked" | "favorite");
  }
};

const handleSave = async () => {
  if (!userStore.currentUser?.id) {
    ElMessage.error("未找到用户信息");
    return;
  }

  saving.value = true;
  try {
    await userApi.updateProfile(userStore.currentUser.id, formData);
    ElMessage.success("保存成功");
    // 更新本地 store
    // userStore.updateUser(result) // 如果有更新 userStore 的方法
  } catch (error) {
    ElMessage.error("保存失败");
  } finally {
    saving.value = false;
  }
};
</script>

<style scoped>
.glass-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.dark .glass-card {
  background: rgba(30, 41, 59, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.1);
}
</style>
