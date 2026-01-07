<template>
  <div class="timeline-tab">
    <!-- 操作栏 -->
    <div
      class="mb-4 flex justify-between items-center bg-gray-50 p-3 rounded-lg border border-gray-100"
    >
      <span class="text-gray-500 text-sm">共 {{ list.length }} 个节点</span>
      <div class="flex gap-2">
        <el-tooltip content="刷新列表" placement="top">
          <el-button circle :icon="Refresh" @click="loadData" />
        </el-tooltip>
        <el-button type="primary" :icon="Plus" @click="handleCreate">
          新建节点
        </el-button>
      </div>
    </div>

    <!-- 表格 -->
    <div class="glass-card rounded-lg overflow-hidden">
      <el-table v-loading="loading" :data="list" stripe class="w-full">
        <el-table-column
          label="年份/时间"
          prop="year"
          width="120"
          align="center"
        >
          <template #default="{ row }">
            <el-tag effect="plain" class="font-bold">{{ row.year }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="内容" min-width="250">
          <template #default="{ row }">
            <div class="font-bold mb-1">{{ row.title }}</div>
            <div class="text-sm text-gray-500 whitespace-pre-wrap">
              {{ row.description }}
            </div>
          </template>
        </el-table-column>

        <el-table-column label="样式" width="150">
          <template #default="{ row }">
            <div class="flex flex-col gap-1 items-start">
              <div class="text-xs text-gray-400">
                颜色:
                <el-tag :type="row.color" size="small">{{ row.color }}</el-tag>
              </div>
              <div class="text-xs text-gray-400">
                图标: {{ row.icon || "-" }}
              </div>
            </div>
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
      :title="isEdit ? '编辑时间节点' : '新建时间节点'"
      width="500px"
      :close-on-click-modal="false"
      destroy-on-close
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="年份" prop="year">
          <el-input
            v-model="form.year"
            placeholder="例如：2023"
            maxlength="20"
          />
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input
            v-model="form.title"
            placeholder="例如：加入公司"
            maxlength="100"
          />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            rows="3"
            placeholder="详细描述..."
            maxlength="255"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="颜色" prop="color">
          <el-select
            v-model="form.color"
            placeholder="选择颜色类型"
            class="w-full"
          >
            <el-option
              v-for="item in colorOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
              <span class="flex items-center gap-2">
                <span
                  :class="`bg-${
                    item.value === 'primary'
                      ? 'blue'
                      : item.value === 'success'
                      ? 'green'
                      : item.value === 'warning'
                      ? 'yellow'
                      : item.value === 'danger'
                      ? 'red'
                      : 'gray'
                  }-500 w-3 h-3 rounded-full display-block`"
                ></span>
                {{ item.label }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-input
            v-model="form.icon"
            placeholder="Element Plus Icon Name (e.g. Star)"
          />
          <div class="text-xs text-gray-400 mt-1">
            请输入 Element Plus 图标名称
          </div>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number
            v-model="form.sortOrder"
            :min="0"
            :max="999"
            controls-position="right"
          />
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
import { timelineApi } from "@/api/siteContent";
import type { Timeline, TimelineForm } from "@/types";

// ==================== 状态 ====================
const loading = ref(false);
const list = ref<Timeline[]>([]);
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitting = ref(false);
const editId = ref<number | null>(null);

const formRef = ref<FormInstance>();
const form = reactive<TimelineForm>({
  year: "",
  title: "",
  description: "",
  icon: "",
  color: "primary",
  sortOrder: 0,
});

const rules: FormRules<TimelineForm> = {
  year: [
    { required: true, message: "请输入年份/时间点", trigger: "blur" },
    { max: 20, message: "不能超过20个字符", trigger: "blur" },
  ],
  title: [
    { required: true, message: "请输入标题", trigger: "blur" },
    { max: 100, message: "不能超过100个字符", trigger: "blur" },
  ],
  color: [{ required: true, message: "请选择颜色类型", trigger: "change" }],
};

const colorOptions = [
  { label: "主要 (Primary)", value: "primary" },
  { label: "成功 (Success)", value: "success" },
  { label: "警告 (Warning)", value: "warning" },
  { label: "危险 (Danger)", value: "danger" },
  { label: "信息 (Info)", value: "info" },
];

// ==================== 方法 ====================

const formatDate = (date: string) => {
  return date ? dayjs(date).format("YYYY-MM-DD HH:mm") : "-";
};

const loadData = async () => {
  loading.value = true;
  try {
    list.value = await timelineApi.getList();
  } catch (error) {
    console.error("加载时间线失败:", error);
  } finally {
    loading.value = false;
  }
};

const handleCreate = () => {
  isEdit.value = false;
  editId.value = null;
  resetForm();
  dialogVisible.value = true;
};

const handleEdit = (row: Timeline) => {
  isEdit.value = true;
  editId.value = row.id;
  form.year = row.year;
  form.title = row.title;
  form.description = row.description;
  form.icon = row.icon;
  form.color = row.color;
  form.sortOrder = row.sortOrder;
  dialogVisible.value = true;
};

const handleDelete = async (row: Timeline) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除时间节点「${row.title}」吗？`,
      "删除确认",
      {
        type: "warning",
        confirmButtonText: "确定",
        cancelButtonText: "取消",
      }
    );

    await timelineApi.delete(String(row.id));
    ElMessage.success("删除成功");
    loadData();
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除失败:", error);
    }
  }
};

const resetForm = () => {
  form.year = "";
  form.title = "";
  form.description = "";
  form.icon = "";
  form.color = "primary";
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
          await timelineApi.update(editId.value, form);
          ElMessage.success("更新成功");
        } else {
          await timelineApi.create(form);
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
:deep(.el-table__row:hover) {
  background-color: rgba(var(--color-primary-50), 0.5);
}
</style>
