<template>
  <div class="project-tab">
    <!-- 操作栏 -->
    <div
      class="mb-4 flex justify-between items-center bg-gray-50 p-3 rounded-lg border border-gray-100"
    >
      <span class="text-gray-500 text-sm">共 {{ list.length }} 个项目</span>
      <div class="flex gap-2">
        <el-tooltip content="刷新列表" placement="top">
          <el-button circle :icon="Refresh" @click="loadData" />
        </el-tooltip>
        <el-button type="primary" :icon="Plus" @click="handleCreate">
          新建项目
        </el-button>
      </div>
    </div>

    <!-- 表格 -->
    <div class="glass-card rounded-lg overflow-hidden">
      <el-table v-loading="loading" :data="list" stripe class="w-full">
        <el-table-column label="封面" width="100" align="center">
          <template #default="{ row }">
            <el-image
              v-if="row.coverImage"
              :src="row.coverImage"
              :preview-src-list="[row.coverImage]"
              fit="cover"
              class="w-16 h-10 rounded border border-gray-200 cursor-pointer"
              preview-teleported
            />
            <div
              v-else
              class="w-16 h-10 bg-gray-100 rounded flex items-center justify-center text-gray-400 mx-auto"
            >
              <el-icon><Picture /></el-icon>
            </div>
          </template>
        </el-table-column>

        <el-table-column
          label="标题"
          prop="title"
          min-width="150"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            <div class="font-medium">{{ row.title }}</div>
            <div class="text-xs text-gray-500 truncate">
              {{ row.description }}
            </div>
          </template>
        </el-table-column>

        <el-table-column
          label="技术栈"
          prop="techStack"
          min-width="150"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            <div class="flex flex-wrap gap-1" v-if="row.techStack">
              <el-tag
                v-for="tech in row.techStack.split(',')"
                :key="tech"
                size="small"
                type="info"
                effect="plain"
              >
                {{ tech.trim() }}
              </el-tag>
            </div>
            <span v-else class="text-gray-400">-</span>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag
              v-if="row.isFeatured"
              type="success"
              size="small"
              effect="dark"
              >精选</el-tag
            >
            <el-tag v-else type="info" size="small">普通</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="链接" width="150">
          <template #default="{ row }">
            <div class="flex flex-col gap-1 text-xs">
              <a
                v-if="row.githubUrl"
                :href="row.githubUrl"
                target="_blank"
                class="text-blue-500 hover:underline flex items-center"
              >
                <el-icon class="mr-1"><Link /></el-icon> Github
              </a>
              <a
                v-if="row.previewUrl"
                :href="row.previewUrl"
                target="_blank"
                class="text-green-500 hover:underline flex items-center"
              >
                <el-icon class="mr-1"><View /></el-icon> 预览Demo
              </a>
            </div>
          </template>
        </el-table-column>

        <el-table-column
          label="排序"
          width="80"
          align="center"
          prop="sortOrder"
        />

        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              size="small"
              :type="row.isFeatured ? 'warning' : 'success'"
              link
              :loading="row.updating"
              @click="handleToggleFeatured(row)"
            >
              {{ row.isFeatured ? "取消精选" : "设为精选" }}
            </el-button>
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
      :title="isEdit ? '编辑项目' : '新建项目'"
      width="600px"
      :close-on-click-modal="false"
      destroy-on-close
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="项目标题" prop="title">
          <el-input
            v-model="form.title"
            placeholder="请输入项目标题"
            maxlength="100"
          />
        </el-form-item>
        <el-form-item label="项目描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            rows="3"
            placeholder="简短的项目描述"
            maxlength="255"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="封面图片" prop="coverImage">
          <el-input
            v-model="form.coverImage"
            placeholder="请输入图片URL或上传"
          />
          <div class="text-xs text-gray-400 mt-1">
            建议尺寸 16:9，例如 640x360
          </div>
        </el-form-item>
        <el-form-item label="技术栈" prop="techStack">
          <el-input
            v-model="form.techStack"
            placeholder="例如：Vue3, Spring Boot, PostgreSQL"
          />
          <div class="text-xs text-gray-400 mt-1">
            多个技术栈请用英文逗号分隔
          </div>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="Github" prop="githubUrl">
              <el-input
                v-model="form.githubUrl"
                placeholder="https://github.com/..."
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预览链接" prop="previewUrl">
              <el-input v-model="form.previewUrl" placeholder="https://..." />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="是否精选" prop="isFeatured">
              <el-switch
                v-model="form.isFeatured"
                :active-value="1"
                :inactive-value="0"
                active-text="是"
                inactive-text="否"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序权重" prop="sortOrder">
              <el-input-number
                v-model="form.sortOrder"
                :min="0"
                :max="999"
                controls-position="right"
                class="w-full"
              />
            </el-form-item>
          </el-col>
        </el-row>
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
import {
  Plus,
  Refresh,
  Edit,
  Delete,
  Picture,
  Link,
  View,
} from "@element-plus/icons-vue";
import {
  ElMessage,
  ElMessageBox,
  type FormInstance,
  type FormRules,
} from "element-plus";
import { projectApi } from "@/api/siteContent";
import type { Project, ProjectForm } from "@/types";

// ==================== 状态 ====================
const loading = ref(false);
const list = ref<(Project & { updating?: boolean })[]>([]);
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitting = ref(false);
const editId = ref<number | null>(null);

const formRef = ref<FormInstance>();
const form = reactive<ProjectForm>({
  title: "",
  description: "",
  coverImage: "",
  githubUrl: "",
  previewUrl: "",
  techStack: "",
  isFeatured: 0,
  sortOrder: 0,
});

const rules: FormRules<ProjectForm> = {
  title: [
    { required: true, message: "请输入项目标题", trigger: "blur" },
    { max: 100, message: "不能超过100个字符", trigger: "blur" },
  ],
  description: [
    { required: true, message: "请输入项目描述", trigger: "blur" },
    { max: 255, message: "不能超过255个字符", trigger: "blur" },
  ],
};

// ==================== 方法 ====================

const loadData = async () => {
  loading.value = true;
  try {
    const res = await projectApi.getList();
    list.value = res.map((item) => ({ ...item, updating: false }));
  } catch (error) {
    console.error("加载项目失败:", error);
  } finally {
    loading.value = false;
  }
};

const handleToggleFeatured = async (row: Project & { updating?: boolean }) => {
  row.updating = true;
  try {
    const updated = await projectApi.toggleFeatured(row.id);
    row.isFeatured = updated.isFeatured;
    ElMessage.success(updated.isFeatured ? "已设为精选" : "已取消精选");
  } catch (error) {
    console.error("更新状态失败:", error);
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

const handleEdit = (row: Project) => {
  isEdit.value = true;
  editId.value = row.id;
  Object.assign(form, {
    title: row.title,
    description: row.description,
    coverImage: row.coverImage,
    githubUrl: row.githubUrl,
    previewUrl: row.previewUrl,
    techStack: row.techStack,
    isFeatured: row.isFeatured,
    sortOrder: row.sortOrder,
  });
  dialogVisible.value = true;
};

const handleDelete = async (row: Project) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除项目「${row.title}」吗？`,
      "删除确认",
      {
        type: "warning",
        confirmButtonText: "确定",
        cancelButtonText: "取消",
      }
    );

    await projectApi.delete(String(row.id));
    ElMessage.success("删除成功");
    loadData();
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除失败:", error);
    }
  }
};

const resetForm = () => {
  form.title = "";
  form.description = "";
  form.coverImage = "";
  form.githubUrl = "";
  form.previewUrl = "";
  form.techStack = "";
  form.isFeatured = 0;
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
          await projectApi.update(editId.value, form);
          ElMessage.success("更新成功");
        } else {
          await projectApi.create(form);
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
