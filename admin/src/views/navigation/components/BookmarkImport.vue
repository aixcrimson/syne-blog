<template>
  <el-dialog
    v-model="visible"
    title="导入Chrome书签"
    :width="isMobile ? '90%' : '800px'"
    :close-on-click-modal="false"
    :lock-scroll="false"
    append-to-body
    @closed="handleClose"
  >
    <!-- 步骤指示器 -->
    <el-steps :active="currentStep" finish-status="success" class="mb-6">
      <el-step title="选择文件" />
      <el-step title="预览配置" />
      <el-step title="导入完成" />
    </el-steps>

    <!-- 步骤1: 文件上传 -->
    <div v-if="currentStep === 0" class="step-content">
      <el-upload
        ref="uploadRef"
        class="upload-area"
        drag
        :accept="'.html'"
        :limit="1"
        :auto-upload="false"
        :on-change="handleFileChange"
        :on-remove="handleFileRemove"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          将Chrome导出的书签HTML文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            只支持HTML格式的Chrome书签文件，文件大小不超过5MB
          </div>
        </template>
      </el-upload>

      <div class="mt-4">
        <el-alert type="info" show-icon :closable="false">
          <template #title> 如何导出Chrome书签？ </template>
          <ol class="ml-4 mt-2 text-sm">
            <li>打开Chrome浏览器</li>
            <li>点击右上角三点菜单 → 书签 → 书签管理器</li>
            <li>点击右上角三点菜单 → 导出书签</li>
            <li>保存为HTML文件</li>
          </ol>
        </el-alert>
      </div>
    </div>

    <!-- 步骤2: 预览和配置 -->
    <div v-if="currentStep === 1" class="step-content">
      <div v-loading="loading" class="min-h-[400px]">
        <!-- 统计信息 -->
        <div class="mb-4 p-4 bg-gray-50 rounded">
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="text-center">
                <div class="text-2xl font-bold text-primary">
                  {{ previewData?.totalBookmarks || 0 }}
                </div>
                <div class="text-gray-500">书签总数</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="text-center">
                <div class="text-2xl font-bold text-success">
                  {{ previewData?.totalFolders || 0 }}
                </div>
                <div class="text-gray-500">文件夹数量</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="text-center">
                <div class="text-2xl font-bold text-warning">
                  {{ selectedCount }}
                </div>
                <div class="text-gray-500">已选择导入</div>
              </div>
            </el-col>
          </el-row>
        </div>

        <!-- 文件夹映射配置 -->
        <div class="mb-4">
          <h4 class="text-base font-medium mb-2">文件夹映射配置</h4>
          <p class="text-sm text-gray-500 mb-3">
            请将Chrome书签文件夹映射到导航分类
          </p>

          <el-table :data="folderMappings" border style="width: 100%">
            <el-table-column label="文件夹" width="250">
              <template #default="{ row }">
                <div class="flex items-center">
                  <el-checkbox
                    v-model="row.selected"
                    @change="handleFolderSelect(row)"
                  />
                  <span class="ml-2">{{ row.folder }}</span>
                  <el-tag size="small" class="ml-2"
                    >{{ row.count }} 个书签</el-tag
                  >
                </div>
              </template>
            </el-table-column>
            <el-table-column label="映射到分类">
              <template #default="{ row }">
                <div v-if="!row.selected" class="text-gray-400">未选择</div>
                <template v-else>
                  <el-select
                    v-model="row.mappingType"
                    placeholder="请选择"
                    @change="handleMappingTypeChange(row)"
                  >
                    <el-option label="映射到现有分类" value="existing" />
                    <el-option label="创建新分类" value="new" />
                  </el-select>
                </template>
              </template>
            </el-table-column>
            <el-table-column label="目标分类">
              <template #default="{ row }">
                <div v-if="!row.selected">-</div>
                <template v-else-if="row.mappingType === 'existing'">
                  <el-select v-model="row.categoryId" placeholder="选择分类">
                    <el-option
                      v-for="cat in categories"
                      :key="cat.id"
                      :label="cat.name"
                      :value="cat.id"
                    />
                  </el-select>
                </template>
                <template v-else-if="row.mappingType === 'new'">
                  <el-input
                    v-model="row.newCategoryName"
                    placeholder="新分类名称"
                    class="mb-2"
                  />

                </template>
                <div v-else>-</div>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 书签预览 -->
        <div>
          <h4 class="text-base font-medium mb-2">书签预览</h4>
          <el-table :data="previewBookmarks" border max-height="300">
            <el-table-column label="名称" prop="name" />
            <el-table-column label="URL" prop="url" show-overflow-tooltip />
            <el-table-column label="文件夹" prop="folder" width="150" />
          </el-table>
        </div>
      </div>
    </div>

    <!-- 步骤3: 导入结果 -->
    <div v-if="currentStep === 2" class="step-content">
      <div class="text-center py-8">
        <el-icon v-if="importSuccess" class="text-6xl text-success mb-4">
          <circle-check-filled />
        </el-icon>
        <el-icon v-else class="text-6xl text-danger mb-4">
          <circle-close-filled />
        </el-icon>

        <h3 class="text-xl font-medium mb-2">
          {{ importSuccess ? "导入成功！" : "导入失败" }}
        </h3>

        <div v-if="importResult" class="text-left max-w-md mx-auto">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="成功导入">
              {{ importResult.successCount }} 个
            </el-descriptions-item>
            <el-descriptions-item label="跳过重复">
              {{ importResult.skipCount }} 个
            </el-descriptions-item>
            <el-descriptions-item label="导入失败">
              {{ importResult.errorCount }} 个
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </div>

    <!-- 对话框底部 -->
    <template #footer>
      <div class="flex justify-between">
        <el-button @click="visible = false">取消</el-button>
        <div>
          <el-button
            v-if="currentStep > 0 && currentStep < 2"
            @click="prevStep"
          >
            上一步
          </el-button>
          <el-button
            v-if="currentStep === 0"
            type="primary"
            :disabled="!selectedFile"
            @click="nextStep"
          >
            下一步
          </el-button>
          <el-button
            v-if="currentStep === 1"
            type="primary"
            :loading="importing"
            :disabled="!canImport"
            @click="handleImport"
          >
            {{ importing ? "导入中..." : "开始导入" }}
          </el-button>
          <el-button
            v-if="currentStep === 2"
            type="primary"
            @click="handleComplete"
          >
            完成
          </el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { ElMessage } from "element-plus";
import {
  UploadFilled,
  CircleCheckFilled,
  CircleCloseFilled,
} from "@element-plus/icons-vue";
import { navigationApi } from '@/api/navigation'
import type {
  BookmarkPreviewDTO,
  FolderMapping,
  NavigationCategory,
} from '@/types'
import { useResponsive } from '@/utils/useResponsive'

const { isMobile } = useResponsive()

interface FolderMappingItem {
  folder: string;
  count: number;
  selected: boolean;
  mappingType: "existing" | "new" | "";
  categoryId: number | null;
  newCategoryName: string;
}

// 组件属性
const props = defineProps<{
  modelValue: boolean;
  categories: NavigationCategory[];
}>();

// 事件
const emit = defineEmits<{
  "update:modelValue": [value: boolean];
  success: [];
}>();

// 响应式数据
const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit("update:modelValue", val),
});

const currentStep = ref(0);
const loading = ref(false);
const importing = ref(false);
const uploadRef = ref();
const selectedFile = ref<File | null>(null);
const previewData = ref<BookmarkPreviewDTO | null>(null);
const folderMappings = ref<FolderMappingItem[]>([]);
const importSuccess = ref(false);
const importResult = ref<{
  successCount: number;
  skipCount: number;
  errorCount: number;
} | null>(null);

// 计算属性
const selectedCount = computed(() => {
  return folderMappings.value
    .filter((item) => item.selected)
    .reduce((sum, item) => sum + item.count, 0);
});

const previewBookmarks = computed(() => {
  if (!previewData.value) return [];
  return previewData.value.bookmarks.slice(0, 10); // 只显示前10个
});

const canImport = computed(() => {
  return folderMappings.value.some((item) => {
    if (!item.selected) return false;
    if (item.mappingType === "existing") {
      return item.categoryId !== null;
    } else if (item.mappingType === "new") {
      return item.newCategoryName.trim() !== "";
    }
    return false;
  });
});

// 方法
const handleFileChange = (file: any) => {
  selectedFile.value = file.raw;
};

const handleFileRemove = () => {
  selectedFile.value = null;
};

const resolveCategoryName = (folderPath: string): string => {
  if (!folderPath) return "其他";
  const parts = folderPath.split("/").map((p) => p.trim()).filter(Boolean);
  const excludeRoots = new Set([
    "书签栏",
    "书签分类",
    "其他书签",
    "移动设备书签",
    "根目录",
    "bookmarks bar",
    "bookmarks",
    "other bookmarks",
    "mobile bookmarks",
  ]);

  for (const part of parts) {
    if (!excludeRoots.has(part.toLowerCase())) {
      return part;
    }
  }
  return parts[parts.length - 1] || "其他";
};

const nextStep = async () => {
  if (currentStep.value === 0) {
    if (!selectedFile.value) {
      ElMessage.error("请选择要导入的文件");
      return;
    }

    loading.value = true;
    try {
      previewData.value = await navigationApi.parseBookmarkFile(
        selectedFile.value
      );

      // 初始化文件夹映射，智能匹配现有分类或预设新分类名
      folderMappings.value = previewData.value.categories.map((cat) => {
        const resolvedName = resolveCategoryName(cat.name);
        const existingCat = props.categories.find(
          (c) => c.name.toLowerCase() === resolvedName.toLowerCase()
        );

        return {
          folder: cat.name,
          count: cat.count,
          selected: true,
          mappingType: existingCat ? ("existing" as const) : ("new" as const),
          categoryId: existingCat ? existingCat.id : null,
          newCategoryName: existingCat ? "" : resolvedName,
        };
      });

      currentStep.value = 1;
    } catch (error: any) {
      ElMessage.error(error.message || "解析文件失败");
    } finally {
      loading.value = false;
    }
  } else {
    currentStep.value++;
  }
};

const prevStep = () => {
  currentStep.value--;
};

const handleFolderSelect = (row: FolderMappingItem) => {
  if (!row.selected) {
    row.mappingType = "";
    row.categoryId = null;
    row.newCategoryName = "";
  }
};

const handleMappingTypeChange = (row: FolderMappingItem) => {
  row.categoryId = null;
  if (row.mappingType === "new") {
    row.newCategoryName = row.folder;
  }
};

const handleImport = async () => {
  // 构建映射数据
  const mappings: FolderMapping[] = [];
  const selectedBookmarks: any[] = [];

  folderMappings.value.forEach((mapping) => {
    if (!mapping.selected) return;

    if (mapping.mappingType === "existing" && mapping.categoryId) {
      mappings.push({
        folder: mapping.folder,
        categoryId: mapping.categoryId,
      });
    } else if (mapping.mappingType === "new" && mapping.newCategoryName) {
      mappings.push({
        folder: mapping.folder,
        categoryId: 0, // 临时值，后端会创建新分类
        createNew: true,
        newCategoryName: mapping.newCategoryName,
      });
    }

    // 添加该书签文件夹下的所有书签
    if (previewData.value) {
      const folderBookmarks = previewData.value.bookmarks.filter(
        (b) => b.folder === mapping.folder
      );
      selectedBookmarks.push(...folderBookmarks);
    }
  });

  if (mappings.length === 0) {
    ElMessage.error("请至少选择一个文件夹并配置映射");
    return;
  }

  importing.value = true;
  try {
    const result = await navigationApi.importBookmarks({
      mappings,
      bookmarks: selectedBookmarks,
    });

    // 解析结果
    const match = result.match(/成功 (\d+) 个，跳过 (\d+) 个，失败 (\d+) 个/);
    if (match) {
      importResult.value = {
        successCount: parseInt(match[1]),
        skipCount: parseInt(match[2]),
        errorCount: parseInt(match[3]),
      };
    }

    importSuccess.value = true;
    currentStep.value = 2;
    ElMessage.success("导入完成");
  } catch (error: any) {
    importSuccess.value = false;
    importResult.value = {
      successCount: 0,
      skipCount: 0,
      errorCount: 0,
    };
    currentStep.value = 2;
    ElMessage.error(error.message || "导入失败");
  } finally {
    importing.value = false;
  }
};

const handleComplete = () => {
  if (importSuccess.value) {
    emit("success");
  }
  visible.value = false;
};

const handleClose = () => {
  currentStep.value = 0;
  selectedFile.value = null;
  previewData.value = null;
  folderMappings.value = [];
  importSuccess.value = false;
  importResult.value = null;
  uploadRef.value?.clearFiles();
};
</script>

<style scoped>
.upload-area {
  width: 100%;
}

.step-content {
  min-height: 450px;
}

:deep(.el-upload-dragger) {
  width: 100%;
}
</style>
