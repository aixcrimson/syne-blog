<template>
  <el-popover
    v-model:visible="popoverVisible"
    :disabled="disabled"
    placement="bottom-start"
    :width="720"
    trigger="click"
    :hide-after="0"
    :offset="4"
  >
    <template #reference>
      <el-input
        :model-value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :clearable="clearable"
        :size="size"
        readonly
        @clear="handleClear"
      >
        <template #prefix>
          <span v-if="modelValue" class="icon-preview">
            <el-icon><component :is="modelValue" /></el-icon>
          </span>
        </template>
        <template #suffix>
          <el-icon v-if="!popoverVisible"><ArrowDown /></el-icon>
          <el-icon v-else><ArrowUp /></el-icon>
        </template>
      </el-input>
    </template>

    <div class="icon-selector">
      <!-- 顶部搜索区 -->
      <div class="selector-header">
        <el-input
          v-model="searchKeyword"
          placeholder="输入关键词搜索图标..."
          clearable
          class="search-input"
          size="large"
        >
          <template #prefix>
            <el-icon class="search-icon"><Search /></el-icon>
          </template>
        </el-input>
        <div class="header-meta">
          <span class="meta-count">{{ filteredIcons.length }} 个结果</span>
          <div v-if="modelValue" class="selected-preview">
            <el-icon><component :is="modelValue" /></el-icon>
            <span>{{ modelValue }}</span>
          </div>
        </div>
      </div>

      <!-- 主体区域 -->
      <div class="selector-body">
        <!-- 左侧分类 -->
        <nav class="category-nav">
          <div class="nav-title">分类</div>
          <div
            v-for="cat in iconCategories"
            :key="cat.key"
            class="nav-item"
            :class="{ active: currentCategory === cat.key }"
            @click="currentCategory = cat.key"
          >
            <el-icon class="nav-icon"><component :is="cat.icon" /></el-icon>
            <span class="nav-label">{{ cat.name }}</span>
            <span class="nav-badge">{{ getCategoryCount(cat.key) }}</span>
          </div>
        </nav>

        <!-- 右侧图标网格 -->
        <div class="icon-area">
          <el-scrollbar max-height="400px">
            <div v-if="filteredIcons.length > 0" class="icon-grid">
              <div
                v-for="icon in filteredIcons"
                :key="icon"
                class="icon-card"
                :class="{ active: modelValue === icon }"
                @click="selectIcon(icon)"
              >
                <div class="card-icon">
                  <el-icon><component :is="icon" /></el-icon>
                </div>
                <div class="card-name">{{ formatIconName(icon) }}</div>
              </div>
            </div>
            <div v-else class="empty-state">
              <el-icon class="empty-icon"><Search /></el-icon>
              <p>未找到匹配的图标</p>
              <span>尝试其他关键词</span>
            </div>
          </el-scrollbar>
        </div>
      </div>
    </div>
  </el-popover>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { ArrowDown, ArrowUp, Search } from "@element-plus/icons-vue";
import * as ElementPlusIconsVue from "@element-plus/icons-vue";
import type { PropType } from "vue";

defineProps({
  modelValue: {
    type: String,
    default: "",
  },
  placeholder: {
    type: String,
    default: "请选择图标",
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  clearable: {
    type: Boolean,
    default: true,
  },
  size: {
    type: String as PropType<"default" | "large" | "small">,
    default: "default",
  },
});

const emit = defineEmits<{
  (e: "update:modelValue", value: string): void;
  (e: "change", value: string): void;
}>();

// 状态管理
const popoverVisible = ref(false);
const currentCategory = ref("all");
const searchKeyword = ref("");

// 图标分类定义
const iconCategories = [
  { key: "all", name: "全部图标", icon: "Grid" },
  { key: "arrow", name: "方向箭头", icon: "Right" },
  { key: "edit", name: "编辑操作", icon: "Edit" },
  { key: "data", name: "数据图表", icon: "DataLine" },
  { key: "notice", name: "消息提示", icon: "Bell" },
  { key: "media", name: "多媒体", icon: "VideoPlay" },
  { key: "location", name: "位置场景", icon: "Location" },
  { key: "user", name: "用户相关", icon: "User" },
  { key: "system", name: "系统设置", icon: "Setting" },
  { key: "office", name: "办公文档", icon: "Document" },
  { key: "other", name: "其他图标", icon: "MoreFilled" },
];

// 图标分类映射
const iconCategoryMap: Record<string, string[]> = {
  arrow: [
    "ArrowDown", "ArrowUp", "ArrowLeft", "ArrowRight",
    "ArrowDownBold", "ArrowUpBold", "ArrowLeftBold", "ArrowRightBold",
    "DArrowLeft", "DArrowRight", "Back", "Right",
    "Top", "Bottom", "TopLeft", "TopRight", "BottomLeft", "BottomRight",
    "CaretBottom", "CaretLeft", "CaretRight", "CaretTop",
    "DCaret", "Sort", "SortDown", "SortUp",
    "Rank", "Download", "Upload", "Promotion",
    "RefreshLeft", "RefreshRight", "Refresh", "Loading",
  ],
  edit: [
    "Edit", "EditPen", "Delete", "DeleteFilled",
    "Plus", "Minus", "Check", "Close",
    "Select", "CloseBold", "CircleCheck", "CircleCheckFilled",
    "CircleClose", "CircleCloseFilled", "CirclePlus", "CirclePlusFilled",
    "Remove", "RemoveFilled", "Checked", "SemiSelect",
    "Copy", "CopyDocument", "Scissors", "ScaleToOriginal",
    "Crop", "Stamp", "Brush", "MagicStick",
    "ZoomIn", "ZoomOut", "FullScreen", "Aim",
  ],
  data: [
    "DataLine", "DataBoard", "DataAnalysis", "Histogram",
    "PieChart", "TrendCharts", "Odometer", "Stopwatch",
    "Timer", "Coin", "Money", "Wallet",
    "GoldMedal", "Trophy", "Medal", "Present",
    "Discount", "ShoppingCart", "ShoppingCartFull", "ShoppingBag",
    "ShoppingTrolley", "Goods", "GoodsFilled", "SoldOut",
    "Sell", "SetUp", "Operation", "Share",
    "Connection", "Link", "Paperclip", "Ticket",
  ],
  notice: [
    "Bell", "BellFilled", "Notification", "Message",
    "MessageBox", "ChatDotRound", "ChatDotSquare", "ChatLineRound",
    "ChatLineSquare", "ChatRound", "ChatSquare", "Comment",
    "Position", "Flag", "Finished", "Failed",
    "InfoFilled", "Warning", "WarningFilled", "QuestionFilled",
    "SuccessFilled", "CircleCheck", "CircleCheckFilled", "CircleClose",
    "Help", "Service", "Opportunity", "Postcard",
  ],
  media: [
    "VideoPlay", "VideoPause", "VideoCamera", "VideoCameraFilled",
    "Film", "Picture", "PictureFilled", "PictureRounded",
    "Camera", "CameraFilled", "Mic", "Mute",
    "Headset", "Monitor", "Iphone", "Cellphone",
    "Printer", "Files", "Folder", "FolderAdd",
    "FolderChecked", "FolderDelete", "FolderOpened", "FolderRemove",
    "Document", "DocumentAdd", "DocumentChecked", "DocumentCopy",
    "DocumentDelete", "DocumentRemove", "Tickets", "Collection",
  ],
  location: [
    "Location", "LocationFilled", "LocationInformation", "Place",
    "MapLocation", "AddLocation", "DeleteLocation", "Coordinate",
    "Guide", "Position", "Van", "Ship",
    "Bicycle", "OfficeBuilding", "School", "Suitcase",
    "SuitcaseLine", "TakeawayBox", "Box", "Refrigerator",
    "House", "HomeFilled", "Smoking",
    "NoSmoking", "GobletFull", "GobletSquareFull", "CoffeeCup",
    "Coffee", "Dessert", "IceCream", "Food",
  ],
  user: [
    "User", "UserFilled", "Avatar", "Postcard",
    "Female", "Male", "Checked", "Connection",
    "Service", "Key", "Unlock", "Lock",
    "Star", "StarFilled", "CollectionTag", "Collection",
    "View", "Hide", "Reading", "ReadingLamp",
    "Notebook", "Management", "Pointer", "Cpu",
    "Coordinate", "Guide", "Platform", "Share",
    "ChromeFilled", "ElemeFilled", "Eleme", "Opportunity",
  ],
  system: [
    "Setting", "Tools", "Operation", "SetUp",
    "TurnOff", "Open", "Switch", "SwitchButton",
    "SwitchFilled", "Menu", "Grid", "List",
    "Search", "Filter", "More", "MoreFilled",
    "Expand", "Fold", "HelpFilled", "Help",
    "InfoFilled", "QuestionFilled", "SuccessFilled", "WarningFilled",
    "MoonNight", "Moon", "Sunny", "Cloudy",
    "PartlyCloudy", "Lightning", "Heavy", "Drizzling",
    "Pouring", "WindPower", "Hot", "Cold",
  ],
  office: [
    "Document", "DocumentAdd", "DocumentChecked", "DocumentCopy",
    "DocumentDelete", "DocumentRemove", "Files", "Folder",
    "FolderAdd", "FolderChecked", "FolderDelete", "FolderOpened",
    "FolderRemove", "Memo", "Notebook", "Reading",
    "Calendar", "Date", "Timer", "Clock",
    "AlarmClock", "Watch", "Stamp", "Briefcase",
    "Management", "Coordinate", "Histogram", "DataLine",
    "DataBoard", "DataAnalysis", "PieChart", "TrendCharts",
    "Printer", "Phone", "PhoneFilled", "Cellphone",
  ],
  other: [
    "Apple", "Cherry", "Grape", "Watermelon",
    "Orange", "Pear", "Lollipop", "IceCreamRound",
    "IceCreamSquare", "IceDrink", "Milk", "KnifeFork",
    "Chicken", "Burger", "Rice", "Bowl",
    "Football", "Basketball", "Baseball", "Soccer",
    "Umbrella", "Sugar", "Smoking", "NoSmoking",
    "FirstAidKit", "Magnet", "Compass", "Discover",
    "Magic", "Chrome", "MostlyCloudy", "Sunset",
    "Sunrise", "Rainbow", "Ship", "Bicycle",
    "Van", "Truck", "TakeawayBox", "Suitcase",
  ],
};

// 获取所有图标名称
const allIconNames = computed(() => Object.keys(ElementPlusIconsVue).sort());

// 获取分类图标数量
const getCategoryCount = (category: string): number => {
  if (category === "all") {
    return allIconNames.value.length;
  }
  const categoryIcons = iconCategoryMap[category] || [];
  return categoryIcons.filter((icon) => allIconNames.value.includes(icon)).length;
};

// 根据分类和搜索过滤图标
const filteredIcons = computed(() => {
  let icons = allIconNames.value;

  // 按分类筛选
  if (currentCategory.value !== "all") {
    const categoryIcons = iconCategoryMap[currentCategory.value] || [];
    icons = icons.filter((icon) => categoryIcons.includes(icon));
  }

  // 按搜索关键词筛选
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    icons = icons.filter(
      (icon) =>
        icon.toLowerCase().includes(keyword) ||
        formatIconName(icon).toLowerCase().includes(keyword)
    );
  }

  return icons;
});

// 格式化图标名称（驼峰转空格分隔）
const formatIconName = (icon: string): string => {
  return icon.replace(/([A-Z])/g, " $1").trim();
};

// 处理选择
const selectIcon = (icon: string) => {
  emit("update:modelValue", icon);
  emit("change", icon);
  popoverVisible.value = false;
};

// 处理清空
const handleClear = () => {
  emit("update:modelValue", "");
  emit("change", "");
};
</script>

<style scoped>
.icon-selector {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* ===== 顶部搜索区 ===== */
.selector-header {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.search-input {
  width: 100%;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 0 0 1px var(--el-border-color) inset;
  transition: all 0.2s;
}

.search-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--el-color-primary-light-5) inset;
}

.search-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px var(--el-color-primary) inset;
}

.search-icon {
  font-size: 16px;
  color: var(--el-text-color-placeholder);
}

.header-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.meta-count {
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.selected-preview {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 12px;
  background: var(--el-color-primary-light-9);
  border: 1px solid var(--el-color-primary-light-7);
  border-radius: 20px;
  color: var(--el-color-primary);
  font-size: 13px;
  font-weight: 500;
}

.selected-preview .el-icon {
  font-size: 16px;
}

/* ===== 主体区域 ===== */
.selector-body {
  display: flex;
  gap: 16px;
}

/* ===== 左侧分类导航 ===== */
.category-nav {
  width: 130px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-title {
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: var(--el-text-color-placeholder);
  padding: 0 12px 8px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  margin-bottom: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  font-size: 13px;
  color: var(--el-text-color-regular);
  background: transparent;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.15s ease;
  user-select: none;
}

.nav-item:hover {
  background: var(--el-fill-color-light);
  color: var(--el-text-color-primary);
}

.nav-item.active {
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}

.nav-icon {
  font-size: 16px;
  flex-shrink: 0;
  opacity: 0.7;
}

.nav-item:hover .nav-icon,
.nav-item.active .nav-icon {
  opacity: 1;
}

.nav-item.active .nav-icon {
  color: var(--el-color-primary);
}

.nav-label {
  flex: 1;
  white-space: nowrap;
}

.nav-badge {
  font-size: 11px;
  min-width: 24px;
  padding: 2px 6px;
  text-align: center;
  background: var(--el-fill-color);
  border-radius: 10px;
  color: var(--el-text-color-secondary);
  flex-shrink: 0;
}

.nav-item.active .nav-badge {
  background: var(--el-color-primary);
  color: #fff;
}

/* ===== 右侧图标区域 ===== */
.icon-area {
  flex: 1;
  min-width: 0;
  border-left: 1px solid var(--el-border-color-lighter);
  padding-left: 16px;
}

.icon-grid {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 10px;
  padding: 4px;
}

.icon-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 14px 8px 10px;
  background: var(--el-fill-color-lighter);
  border: 1px solid transparent;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.18s ease;
}

.icon-card:hover {
  background: var(--el-color-primary-light-9);
  border-color: var(--el-color-primary-light-5);
  transform: translateY(-2px);
  box-shadow: 0 6px 16px -4px rgba(0, 0, 0, 0.1);
}

.icon-card.active {
  background: var(--el-color-primary-light-8);
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 2px var(--el-color-primary-light-7);
}

.card-icon {
  font-size: 24px;
  color: var(--el-text-color-primary);
  transition: all 0.18s;
  line-height: 1;
}

.icon-card:hover .card-icon {
  color: var(--el-color-primary);
  transform: scale(1.1);
}

.icon-card.active .card-icon {
  color: var(--el-color-primary);
}

.card-name {
  font-size: 10px;
  color: var(--el-text-color-secondary);
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
  line-height: 1.3;
}

.icon-card.active .card-name {
  color: var(--el-color-primary);
  font-weight: 500;
}

/* ===== 空状态 ===== */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: var(--el-text-color-placeholder);
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.4;
}

.empty-state p {
  font-size: 14px;
  margin: 0 0 4px;
  color: var(--el-text-color-secondary);
}

.empty-state span {
  font-size: 12px;
}

/* ===== 输入框图标预览 ===== */
.icon-preview {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: var(--el-color-primary);
}

/* ===== 滚动条 ===== */
:deep(.el-scrollbar__view) {
  padding: 0;
}

:deep(.el-scrollbar__bar) {
  opacity: 0;
  transition: opacity 0.3s;
}

:deep(.el-scrollbar:hover .el-scrollbar__bar) {
  opacity: 1;
}
</style>
