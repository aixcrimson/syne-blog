<template>
  <div class="skill-tab">
    <!-- 操作栏 -->
    <div
      class="mb-4 flex justify-between items-center bg-gray-50 p-3 rounded-lg border border-gray-100"
    >
      <span class="text-gray-500 text-sm">共 {{ list.length }} 个技能</span>
      <div class="flex gap-2">
        <el-tooltip content="刷新列表" placement="top">
          <el-button circle :icon="Refresh" @click="loadData" />
        </el-tooltip>
        <el-button type="primary" :icon="Plus" @click="handleCreate">
          新建技能
        </el-button>
      </div>
    </div>

    <!-- 表格 -->
    <div class="glass-card rounded-lg overflow-hidden">
      <el-table v-loading="loading" :data="list" stripe class="w-full">
        <el-table-column label="名称" prop="name" width="150" font-bold />

        <el-table-column label="熟练度" min-width="200">
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <el-progress
                :percentage="row.percentage"
                :color="row.color"
                :stroke-width="12"
                class="flex-1"
              />
            </div>
          </template>
        </el-table-column>


        <el-table-column label="颜色" width="100" align="center">
          <template #default="{ row }">
            <div class="flex items-center justify-center gap-2">
              <div
                class="w-4 h-4 rounded border border-gray-200"
                :style="{ background: row.color }"
              ></div>
              <span class="text-xs text-gray-500">{{ row.color }}</span>
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
      :title="isEdit ? '编辑技能' : '新建技能'"
      :width="isMobile ? '90%' : '500px'"
      :close-on-click-modal="false"
      destroy-on-close
      append-to-body
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" :label-width="isMobile ? 'auto' : '80px'">
        <el-form-item label="名称" prop="name">
          <el-input
            v-model="form.name"
            placeholder="例如：Vue.js"
            maxlength="50"
          />
        </el-form-item>
        <el-form-item label="熟练度" prop="percentage">
          <el-slider v-model="form.percentage" show-input :min="0" :max="100" />
        </el-form-item>
        <el-form-item label="颜色" prop="color">
          <div class="flex items-center gap-2 w-full">
            <el-color-picker
              v-model="form.color"
              show-alpha
              :predefine="predefineColors"
            />
            <el-input
              v-model="form.color"
              class="flex-1"
              placeholder="rgb(59, 130, 246)"
            />
          </div>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number
            v-model="form.sortOrder"
            :min="0"
            :max="999"
            controls-position="right"
          />
          <span class="text-gray-400 text-xs ml-2">数字越小越靠前</span>
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
import { skillApi } from "@/api/siteContent";
import type { Skill, SkillForm } from "@/types";
import { useResponsive } from '@/utils/useResponsive';

// ==================== 状态 ====================
const { isMobile } = useResponsive();
const loading = ref(false);
const list = ref<Skill[]>([]);
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitting = ref(false);
const editId = ref<number | null>(null);

const formRef = ref<FormInstance>();
const form = reactive<SkillForm>({
  name: "",
  percentage: 50,
  color: "rgb(59, 130, 246)",
  sortOrder: 0,
});

const rules: FormRules<SkillForm> = {
  name: [
    { required: true, message: "请输入技能名称", trigger: "blur" },
    { max: 50, message: "名称不能超过50个字符", trigger: "blur" },
  ],
  percentage: [{ required: true, message: "请设置熟练度", trigger: "change" }],
  color: [{ required: true, message: "请选择颜色", trigger: "change" }],
};

const predefineColors = [
  "rgb(59, 130, 246)",
  "rgb(139, 92, 246)",
  "rgb(16, 185, 129)",
  "rgb(245, 158, 11)",
  "rgb(236, 72, 153)",
  "rgb(239, 68, 68)",
  "rgb(6, 182, 212)",
  "rgb(132, 204, 22)",
  "rgb(99, 102, 241)",
  "rgb(249, 115, 22)",
];

// ==================== 方法 ====================

const formatDate = (date: string) => {
  return date ? dayjs(date).format("YYYY-MM-DD HH:mm") : "-";
};


const loadData = async () => {
  loading.value = true;
  try {
    list.value = await skillApi.getList();
  } catch (error) {
    console.error("加载技能失败:", error);
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

const handleEdit = (row: Skill) => {
  isEdit.value = true;
  editId.value = row.id;
  form.name = row.name;
  form.percentage = row.percentage;
  form.color = row.color;
  form.sortOrder = row.sortOrder;
  dialogVisible.value = true;
};

const handleDelete = async (row: Skill) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除技能「${row.name}」吗？`,
      "删除确认",
      {
        type: "warning",
        confirmButtonText: "确定",
        cancelButtonText: "取消",
      }
    );

    await skillApi.delete(String(row.id));
    ElMessage.success("删除成功");
    loadData();
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除失败:", error);
    }
  }
};

const resetForm = () => {
  form.name = "";
  form.percentage = 50;
  form.color = "rgb(59, 130, 246)";
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
          await skillApi.update(editId.value, form);
          ElMessage.success("更新成功");
        } else {
          await skillApi.create(form);
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
:deep(.el-table) {
  --el-table-row-hover-bg-color: var(--color-primary-100);
}
:deep(.el-color-picker__trigger) {
  width: 36px;
  height: 36px;
  border-radius: 6px;
}
</style>
