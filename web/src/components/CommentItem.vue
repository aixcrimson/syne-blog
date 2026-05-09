<template>
  <div class="comment-item paper-card p-4">
    <!-- 评论主体 -->
    <div class="flex gap-3">
      <!-- 头像 -->
      <el-avatar :size="40" :src="comment.userAvatar || undefined">
        {{ comment.username?.charAt(0) || "访" }}
      </el-avatar>

      <!-- 评论内容 -->
      <div class="flex-1 min-w-0">
        <!-- 用户信息和时间 -->
        <div class="flex items-center gap-2 mb-1">
          <span class="font-medium text-slate-900 dark:text-slate-100">
            {{ comment.username }}
          </span>
          <span v-if="comment.replyToUsername" class="text-slate-500 text-sm">
            回复
            <span class="text-primary-600">@{{ comment.replyToUsername }}</span>
          </span>
          <span class="text-slate-400 text-xs">
            {{ formatTime(comment.createTime) }}
          </span>
        </div>

        <!-- 评论内容 -->
        <p
          class="text-slate-700 dark:text-slate-300 whitespace-pre-wrap break-words"
        >
          {{ comment.content }}
        </p>

        <!-- 操作按钮 -->
        <div class="flex items-center gap-4 mt-2">
          <button
            class="text-slate-500 hover:text-primary-600 text-sm flex items-center gap-1 transition-colors"
            @click="toggleReplyForm"
          >
            <el-icon><ChatLineSquare /></el-icon>
            回复
          </button>
        </div>

        <!-- 回复输入框 -->
        <div
          v-if="showReplyForm"
          class="mt-3 pl-2 border-l-2 border-primary-200"
        >
          <el-input
            v-model="replyContent"
            type="textarea"
            :rows="2"
            :placeholder="`回复 @${comment.username}...`"
            maxlength="1000"
            show-word-limit
            resize="none"
          />
          <div class="flex justify-end gap-2 mt-2">
            <el-button size="small" @click="cancelReply">取消</el-button>
            <el-button
              type="primary"
              size="small"
              @click="submitReply"
              :loading="submitting"
            >
              回复
            </el-button>
          </div>
        </div>

        <!-- 子评论（回复） -->
        <div
          v-if="comment.children && comment.children.length > 0"
          class="mt-3 space-y-3"
        >
          <CommentItem
            v-for="child in comment.children"
            :key="child.id"
            :comment="child"
            :article-id="articleId"
            :is-reply="true"
            @reply-success="$emit('reply-success')"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { ChatLineSquare } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { commentApi } from "@/api/comment";
import type { CommentShowVO } from "@/types";
import dayjs from "@/utils/dayjs";

const props = defineProps<{
  comment: CommentShowVO;
  articleId: number;
  isReply?: boolean;
}>();

const emit = defineEmits<{
  "reply-success": [];
}>();

const showReplyForm = ref(false);
const replyContent = ref("");
const submitting = ref(false);

/**
 * 格式化时间（显示相对时间）
 */
const formatTime = (time: string) => {
  const date = dayjs(time);
  const now = dayjs();

  // 如果是7天内，显示相对时间
  if (now.diff(date, "day") < 7) {
    return date.fromNow();
  }
  // 否则显示完整日期
  return date.format("YYYY-MM-DD HH:mm");
};

/**
 * 切换回复表单显示
 */
const toggleReplyForm = () => {
  showReplyForm.value = !showReplyForm.value;
  if (!showReplyForm.value) {
    replyContent.value = "";
  }
};

/**
 * 取消回复
 */
const cancelReply = () => {
  showReplyForm.value = false;
  replyContent.value = "";
};

/**
 * 提交回复
 */
const submitReply = async () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning("请输入回复内容");
    return;
  }

  submitting.value = true;
  try {
    await commentApi.createComment({
      articleId: props.articleId,
      parentId: props.comment.id,
      content: replyContent.value,
    });
    ElMessage.success("回复成功");
    cancelReply();
    emit("reply-success");
  } catch (e) {
    // 错误提示已由 axios 拦截器统一处理
    console.error("回复失败:", e);
  } finally {
    submitting.value = false;
  }
};
</script>

<style scoped>
/* 子评论样式调整 */
.comment-item :deep(.comment-item) {
  background: transparent;
  border: none;
  padding: 0.5rem 0;
}
</style>
