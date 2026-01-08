<template>
  <div class="notice-tab">
    <!-- 操作栏 -->
    <div
      class="mb-4 flex justify-between items-center bg-gray-50 p-3 rounded-lg border border-gray-100"
    >
      <span class="text-gray-500 text-sm">共 {{ list.length }} 条公告</span>
      <div class="flex gap-2">
        <el-tooltip content="刷新列表" placement="top">
          <el-button circle :icon="Refresh" @click="loadData" />
        </el-tooltip>
        <el-button type="primary" :icon="Plus" @click="handleCreate">
          新建公告
        </el-button>
      </div>
    </div>

    <!-- 表格 -->
    <div class="glass-card rounded-lg overflow-hidden">
      <el-table v-loading="loading" :data="list" stripe class="w-full">
        <el-table-column label="公告内容" min-width="300">
          <template #default="{ row }">
            <div class="whitespace-pre-wrap line-clamp-3" :title="row.content">
              {{ row.content }}
            </div>
          </template>
        </el-table-column>

        <el-table-column label="显示状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.isShow"
              :active-value="1"
              :inactive-value="0"
              :loading="row.updating"
              @change="handleToggleShow(row)"
            />
          </template>
        </el-table-column>

        <el-table-column
          label="排序"
          width="80"
          align="center"
          prop="sortOrder"
        />

        <el-table-column label="创建时间" width="160" align="center">
          <template #default="{ row }">
            <span class="text-sm text-gray-500">{{
              formatDate(row.createTime)
            }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)"
              >编辑</el-button
            >
            <el-button
              type="danger"
              link
              :icon="Delete"
              @click="handleDelete(row)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑公告' : '新建公告'"
      width="500px"
      :close-on-click-modal="false"
      destroy-on-close
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            rows="4"
            placeholder="请输入公告内容"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="显示" prop="isShow">
          <el-switch
            v-model="form.isShow"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number
            v-model="form.sortOrder"
            :min="0"
            :max="999"
            controls-position="right"
          />
          <span class="text-gray-400 text-xs ml-2">数字越大越靠前</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit"
          >确定</el-button
        >
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { Plus, Refresh, Edit, Delete } from "@element-plus/icons-vue";
import {
  ElMessage,
  ElMessageBox,
  type FormInstance,
  type FormRules,
} from "element-plus";
import dayjs from "dayjs";
import { noticeApi } from "@/api/siteContent";
import type { Notice, NoticeForm } from "@/types";

// ==================== 状态 ====================
const loading = ref(false);
const list = ref<(Notice & { updating?: boolean })[]>([]);
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitting = ref(false);
const editId = ref<number | null>(null);

const formRef = ref<FormInstance>();
const form = reactive<NoticeForm>({
  content: "",
  isShow: 1,
  sortOrder: 0,
});

const rules: FormRules<NoticeForm> = {
  content: [
    { required: true, message: "请输入公告内容", trigger: "blur" },
    { max: 500, message: "内容不能超过500个字符", trigger: "blur" },
  ],
};

// ==================== 方法 ====================

const formatDate = (date: string) => {
  return date ? dayjs(date).format("YYYY-MM-DD HH:mm") : "-";
};

const loadData = async () => {
  loading.value = true;
  try {
    const res = await noticeApi.getList();
    list.value = res.map((item) => ({ ...item, updating: false }));
  } catch (error) {
    console.error("加载公告失败:", error);
  } finally {
    loading.value = false;
  }
};

const handleToggleShow = async (row: Notice & { updating?: boolean }) => {
  row.updating = true;
  try {
    await noticeApi.toggleShow(row.id);
    ElMessage.success("状态已更新");
  } catch (error) {
    // 失败回滚
    row.isShow = row.isShow === 1 ? 0 : 1;
    console.error("切换状态失败:", error);
  } finally {
    row.updating = false;
  }
};

const handleCreate = () => {
  isEdit.value = false;
  editId.value = null;
  resetForm();
  dialogVisible.value = true;
};

const handleEdit = (row: Notice) => {
  isEdit.value = true;
  editId.value = row.id;
  form.content = row.content;
  form.isShow = row.isShow;
  form.sortOrder = row.sortOrder;
  dialogVisible.value = true;
};

const handleDelete = async (row: Notice) => {
  try {
    await ElMessageBox.confirm("确定要删除该公告吗？", "删除确认", {
      type: "warning",
      confirmButtonText: "确定",
      cancelButtonText: "取消",
    });

    await noticeApi.delete(String(row.id));
    ElMessage.success("删除成功");
    loadData();
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除失败:", error);
    }
  }
};

const resetForm = () => {
  form.content = "";
  form.isShow = 1;
  form.sortOrder = 0;
  formRef.value?.clearValidate();
};

const handleSubmit = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      try {
        if (isEdit.value && editId.value) {
          await noticeApi.update(editId.value, form);
          ElMessage.success("更新成功");
        } else {
          await noticeApi.create(form);
          ElMessage.success("创建成功");
        }
        dialogVisible.value = false;
        loadData();
      } catch (error) {
        console.error("提交失败:", error);
      } finally {
        submitting.value = false;
      }
    }
  });
};

// ==================== 初始化 ====================
onMounted(() => {
  loadData();
});
</script>

<style scoped>
.line-clamp-3 {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
  overflow: hidden;
}

:deep(.el-table__row:hover) {
  background-color: rgba(var(--color-primary-50), 0.5);
}
</style>
