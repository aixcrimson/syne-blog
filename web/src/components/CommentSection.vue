<template>
  <div class="comment-section">
    <!-- 评论输入区域 -->
    <div class="comment-input-section glass-card p-6 mb-6 rounded-lg">
      <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-4">
        <el-icon class="mr-2"><ChatDotRound /></el-icon>
        发表评论
      </h3>
      <el-form ref="formRef" :model="commentForm" :rules="rules">
        <el-form-item prop="content">
          <el-input
            v-model="commentForm.content"
            type="textarea"
            :rows="4"
            placeholder="说点什么吧..."
            maxlength="1000"
            show-word-limit
            resize="none"
          />
        </el-form-item>
        <div class="flex justify-end">
          <el-button
            type="primary"
            @click="submitComment"
            :loading="submitting"
          >
            发表评论
          </el-button>
        </div>
      </el-form>
    </div>

    <!-- 评论列表 -->
    <div class="comment-list-section">
      <div class="flex justify-between items-center mb-4">
        <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100">
          全部评论 ({{ total }})
        </h3>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="py-8">
        <el-skeleton :rows="3" animated />
        <el-skeleton :rows="3" animated class="mt-4" />
      </div>

      <!-- 评论为空 -->
      <el-empty
        v-else-if="comments.length === 0"
        description="暂无评论，快来抢沙发吧~"
      />

      <!-- 评论列表 -->
      <div v-else class="space-y-4">
        <CommentItem
          v-for="comment in comments"
          :key="comment.id"
          :comment="comment"
          :article-id="articleId"
          @reply-success="handleReplySuccess"
        />
      </div>

      <!-- 分页 -->
      <div v-if="total > pageSize" class="flex justify-center mt-6">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="fetchComments"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from "vue";
import { ChatDotRound } from "@element-plus/icons-vue";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";
import { commentApi, type CommentShowVO } from "@/api/comment";
import CommentItem from "./CommentItem.vue";

const props = defineProps<{
  articleId: number;
}>();

const formRef = ref<FormInstance>();
const commentForm = reactive({
  content: "",
});
const rules: FormRules = {
  content: [
    { required: true, message: "请输入评论内容", trigger: "blur" },
    {
      min: 1,
      max: 1000,
      message: "评论内容长度应在1-1000字符之间",
      trigger: "blur",
    },
  ],
};

const comments = ref<CommentShowVO[]>([]);
const loading = ref(false);
const submitting = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

/**
 * 获取评论列表
 */
const fetchComments = async () => {
  loading.value = true;
  try {
    const res = await commentApi.getArticleComments(props.articleId, {
      page: currentPage.value,
      pageSize: pageSize.value,
    });
    comments.value = res.list || [];
    total.value = res.total || 0;
  } catch (e) {
    console.error("获取评论列表失败:", e);
    ElMessage.error("获取评论失败");
  } finally {
    loading.value = false;
  }
};

/**
 * 提交评论
 */
const submitComment = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (!valid) return;

    submitting.value = true;
    try {
      await commentApi.createComment({
        articleId: props.articleId,
        content: commentForm.content,
      });
      ElMessage.success("评论发表成功");
      commentForm.content = "";
      formRef.value?.resetFields();
      // 刷新评论列表
      currentPage.value = 1;
      await fetchComments();
    } catch (e) {
      console.error("发表评论失败:", e);
      ElMessage.error("发表评论失败");
    } finally {
      submitting.value = false;
    }
  });
};

/**
 * 处理回复成功
 */
const handleReplySuccess = () => {
  fetchComments();
};

// 监听文章ID变化
watch(
  () => props.articleId,
  () => {
    currentPage.value = 1;
    fetchComments();
  }
);

onMounted(() => {
  fetchComments();
});
</script>

<style scoped>
.glass-card {
  background: var(--glass-bg);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid var(--glass-border);
}
</style>
