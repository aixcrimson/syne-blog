<template>
  <el-popover
    v-model:visible="popoverVisible"
    :disabled="disabled"
    placement="bottom-start"
    :width="420"
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
            <el-icon v-if="isElementIcon(modelValue)"><component :is="modelValue" /></el-icon>
            <span v-else class="emoji-icon">{{ modelValue }}</span>
          </span>
        </template>
        <template #suffix>
          <el-icon v-if="!popoverVisible"><ArrowDown /></el-icon>
          <el-icon v-else><ArrowUp /></el-icon>
        </template>
      </el-input>
    </template>

    <div class="icon-selector">
      <!-- 标签页切换 -->
      <el-tabs v-model="activeTab" class="icon-tabs">
        <el-tab-pane label="Emoji 表情" name="emoji">
          <el-scrollbar height="350px">
            <!-- Emoji 分类标签 -->
            <div class="emoji-category-tabs">
              <el-tag
                v-for="(cat, key) in emojiCategories"
                :key="key"
                :type="currentEmojiCategory === key ? 'primary' : 'info'"
                size="small"
                class="emoji-category-tag"
                @click="currentEmojiCategory = key"
              >
                {{ cat.name }}
              </el-tag>
            </div>

            <!-- Emoji 网格（不支持搜索，仅按分类展示） -->
            <div class="icon-grid">
              <div
                v-for="icon in getFilteredEmojiIcons()"
                :key="icon.key"
                class="icon-item"
                :class="{ active: modelValue === icon.key }"
                @click="selectIcon(icon.key)"
              >
                <div class="icon-display emoji-display">{{ icon.key }}</div>
                <div class="icon-name">{{ icon.name }}</div>
              </div>
            </div>
          </el-scrollbar>
        </el-tab-pane>

        <el-tab-pane label="Element Icons" name="element">
          <el-scrollbar height="350px">
            <!-- 搜索框 -->
            <el-input
              v-model="searchKeyword"
              placeholder="搜索图标..."
              size="small"
              clearable
              class="mb-3"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>

            <!-- 图标网格 -->
            <div class="icon-grid">
              <div
                v-for="icon in getFilteredElementIcons()"
                :key="icon.key"
                class="icon-item"
                :class="{ active: modelValue === icon.key }"
                @click="selectIcon(icon.key)"
              >
                <div class="icon-display">
                  <el-icon><component :is="icon.key" /></el-icon>
                </div>
                <div class="icon-name">{{ icon.name }}</div>
              </div>
            </div>
          </el-scrollbar>
        </el-tab-pane>
      </el-tabs>
    </div>
  </el-popover>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ArrowDown, ArrowUp, Search } from '@element-plus/icons-vue'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import type { PropType } from 'vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '请选择图标'
  },
  disabled: {
    type: Boolean,
    default: false
  },
  clearable: {
    type: Boolean,
    default: true
  },
  size: {
    type: String as PropType<'default' | 'large' | 'small'>,
    default: 'default'
  }
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
  (e: 'change', value: string): void
}>()

// 状态管理
const popoverVisible = ref(false)
const activeTab = ref<'emoji' | 'element'>('emoji')
const currentEmojiCategory = ref('frequently')
const searchKeyword = ref('')

// 检测是否为 Element Plus 图标
const isElementIcon = (icon: string): boolean => {
  return !!icon &&
         !/\p{Extended_Pictographic}/u.test(icon) &&
         /^[A-Z]/.test(icon) &&
         icon in ElementPlusIconsVue
}

// 处理选择
const selectIcon = (icon: string) => {
  emit('update:modelValue', icon)
  emit('change', icon)
  popoverVisible.value = false
}

// 处理清空
const handleClear = () => {
  emit('update:modelValue', '')
  emit('change', '')
}

// Emoji 分类数据
const emojiCategories: Record<string, { name: string; icons: string[] }> = {
  frequently: {
    name: '常用',
    icons: [
      '😀', '😃', '😄', '😁', '😆', '😅', '😂', '🤣', '😊', '😇',
      '🙂', '🙃', '😉', '😌', '😍', '🥰', '😘', '😗', '😙', '😚',
      '😋', '😛', '😝', '😜', '🤪', '🤨', '🧐', '🤓', '😎', '🥸',
      '🤩', '🥳', '😏', '😒', '😞', '😔', '😟', '😕', '🙁', '☹️',
      '😣', '😖', '😫', '😩', '🥺', '😢', '😭', '😤', '😠', '😡',
      '🤬', '🤯', '😳', '🥵', '🥶', '😱', '😨', '😰', '😥', '😓',
      '🤗', '🤔', '🤭', '🤫', '🤥', '😶', '😐', '😑', '😬', '🙄',
      '😯', '😦', '😧', '😮', '😲', '🥱', '😴', '🤤', '😪', '😵',
      '🤐', '🥴', '🤢', '🤮', '🤧', '😷', '🤒', '🤕', '🤑', '🤠',
      '😈', '👿', '👹', '👺', '🤡', '💩', '👻', '💀', '☠️', '👽',
      '👾', '🤖', '🎃'
    ]
  },
  emotions: {
    name: '表情',
    icons: [
      '😀', '😃', '😄', '😁', '😆', '😅', '😂', '🤣', '🥲', '😊',
      '😇', '🙂', '🙃', '😉', '😌', '😍', '🥰', '😘', '😗', '😙',
      '😚', '😋', '😛', '😝', '😜', '🤪', '🤨', '🧐', '🤓', '😎',
      '🥸', '🤩', '🥳', '😏', '😒', '😞', '😔', '😟', '😕', '🙁',
      '☹️', '😣', '😖', '😫', '😩', '🥺', '😢', '😭', '😤', '😠',
      '😡', '🤬', '🤯', '😳', '🥵', '🥶', '😱', '😨', '😰', '😥',
      '😓', '🤗', '🤔', '🤭', '🤫', '🤥', '😶', '😐', '😑', '😬',
      '🙄', '😯', '😦', '😧', '😮', '😲', '🥱', '😴', '🤤', '😪',
      '😵', '🤐', '🥴', '🤢', '🤮', '🤧', '😷', '🤒', '🤕', '🤑',
      '🤠', '😈', '👿', '👹', '👺', '🤡', '💩', '👻', '💀', '☠️',
      '👽', '👾', '🤖', '🎃', '😺', '😸', '😹', '😻', '😼', '😽',
      '🙀', '😿', '😾'
    ]
  },
  people: {
    name: '人物',
    icons: [
      '👋', '🤚', '🖐️', '✋', '🖖', '👌', '🤌', '🤏', '✌️', '🤞',
      '🤟', '🤘', '🤙', '👈', '👉', '👆', '🖕', '👇', '☝️', '👍',
      '👎', '👊', '✊', '🤛', '🤜', '👏', '🙌', '👐', '🤲', '🤝',
      '🙏', '✍️', '💅', '🤳', '💪', '🦾', '🦿', '🦵', '🦶', '👂',
      '🦻', '👃', '🧠', '🦷', '🦴', '👀', '👁️', '👅', '👄', '💋',
      '🩸'
    ]
  },
  animals: {
    name: '动物',
    icons: [
      '🐶', '🐱', '🐭', '🐹', '🐰', '🦊', '🐻', '🐼', '🐻‍❄️', '🐨',
      '🐯', '🦁', '🐮', '🐷', '🐽', '🐸', '🐵', '🙈', '🙉', '🙊',
      '🐒', '🐔', '🐧', '🐦', '🐤', '🐣', '🐥', '🦆', '🦅', '🦉',
      '🦇', '🐺', '🐗', '🐴', '🦄', '🐝', '🪱', '🐛', '🦋', '🐌',
      '🐞', '🐜', '🦟', '🦗', '🕷️', '🕸️', '🦂', '🐢', '🐍', '🦎',
      '🦖', '🦕', '🐙', '🦑', '🦐', '🦞', '🦀', '🐡', '🐠', '🐟',
      '🐬', '🐳', '🐋', '🦈', '🐊', '🐅', '🐆', '🦓', '🦍', '🦧',
      '🐘', '🦛', '🦏', '🐪', '🐫', '🦒', '🦘', '🐃', '🐂', '🐄',
      '🐎', '🐖', '🐏', '🐑', '🦙', '🐐', '🦌', '🐕', '🐩', '🦮',
      '🐈', '🐓', '🦃', '🦚', '🦜', '🦢', '🦩', '🕊️', '🐇', '🦝',
      '🦨', '🦡', '🦫', '🦦', '🦥', '🐁', '🐀', '🐿️', '🦔'
    ]
  },
  food: {
    name: '食物',
    icons: [
      '🍇', '🍈', '🍉', '🍊', '🍋', '🍌', '🍍', '🥭', '🍎', '🍏',
      '🍐', '🍑', '🍒', '🍓', '🫐', '🥝', '🍅', '🫒', '🥥', '🥑',
      '🍆', '🥔', '🥕', '🌽', '🌶️', '🫑', '🥒', '🥬', '🥦', '🧄',
      '🧅', '🍄', '🥜', '🌰', '🍞', '🥐', '🥖', '🫓', '🥨', '🥯',
      '🧀', '🥚', '🍳', '🧈', '🥞', '🧇', '🥓', '🥩', '🍗', '🍖',
      '🦴', '🌭', '🍔', '🍟', '🍕', '🫔', '🥪', '🥙', '🧆', '🌮',
      '🌯', '🫔', '🥗', '🥘', '🫕', '🥫', '🍝', '🍜', '🍲', '🍛',
      '🍣', '🍱', '🥟', '🦪', '🍤', '🍙', '🍚', '🍘', '🍥', '🥠',
      '🥮', '🍢', '🍡', '🍧', '🍨', '🍦', '🥧', '🧁', '🍰', '🎂',
      '🍮', '🍭', '🍬', '🍫', '🍿', '🍩', '🍪', '🌰', '🥜', '🍯'
    ]
  },
  activities: {
    name: '活动',
    icons: [
      '⚽', '🏀', '🏈', '⚾', '🥎', '🎾', '🏐', '🏉', '🥏', '🎱',
      '🪀', '🏓', '🏸', '🏒', '🏑', '🥍', '🏏', '🪃', '🥅', '⛳',
      '🪁', '🏹', '🎣', '🤿', '🥊', '🥋', '🎽', '🛹', '🛷', '⛸️',
      '🥌', '🎿', '⛷️', '🏂', '🪂', '🏋️', '🤼', '🤸', '⛹️', '🤺',
      '🏇', '🧘', '🏄', '🏊', '🤽', '🚣', '🧗', '🚵', '🚴', '🏆',
      '🥇', '🥈', '🥉', '🏅', '🎖️', '🏵️', '🎗️', '🎫', '🎟️', '🎪',
      '🤹', '🎭', '🩰', '🎨', '🎬', '🎤', '🎧', '🎼', '🎵', '🎶',
      '🥁', '🪘', '🎹', '🎻', '🎺', '🎸', '🪕', '🎯', '🎳', '🎰',
      '🧩', '🎲'
    ]
  },
  travel: {
    name: '地点',
    icons: [
      '🌍', '🌎', '🌏', '🌐', '🗺️', '🗾', '🧭', '🏔️', '⛰️', '🌋',
      '🗻', '🏕️', '🏖️', '🏜️', '🏝️', '🏞️', '🏟️', '🏛️', '🏗️', '🧱',
      '🪨', '🪵', '🛖', '🏘️', '🏚️', '🏠', '🏡', '🏢', '🏣', '🏤',
      '🏥', '🏦', '🏨', '🏩', '🏪', '🏫', '🏬', '🏭', '🏯', '🏰',
      '💒', '🗼', '🗽', '⛪', '🕌', '🛕', '🕍', '⛩️', '🕋', '⛲',
      '⛺', '🌁', '🌃', '🏙️', '🌄', '🌅', '🌆', '🌇', '🌉', '♨️',
      '🎠', '🎡', '🎢', '💈', '🎪', '🚂', '🚃', '🚄', '🚅', '🚆',
      '🚇', '🚈', '🚉', '🚊', '🚝', '🚞', '🚋', '🚌', '🚍', '🚎',
      '🚐', '🚑', '🚒', '🚓', '🚔', '🚕', '🚖', '🚗', '🚘', '🚙',
      '🚚', '🚛', '🚜', '🏎️', '🏍️', '🛵', '🦽', '🦼', '🛺', '🚲',
      '🛴', '🛹', '🛼', '🚁', '🚟', '🚠', '🚡', '🛰️', '🚀', '🛸',
      '🛶', '⛵', '🚤', '🛥️', '🛳️', '⛴️', '🚢', '⚓', '⛽', '🚧',
      '🚨', '🚥', '🚦', '🛑', '🚏', '🗿'
    ]
  },
  objects: {
    name: '物品',
    icons: [
      '⌚', '📱', '📲', '💻', '⌨️', '🖥️', '🖨️', '🖱️', '🖲️', '🕹️',
      '🗜️', '💽', '💾', '💿', '📀', '📼', '📷', '📸', '📹', '🎥',
      '📽️', '🎞️', '📞', '☎️', '📟', '📠', '📺', '📻', '🎙️', '🎚️',
      '🎛️', '🧭', '⏱️', '⏲️', '⏰', '🕰️', '⌛', '⏳', '📡', '🔋',
      '🔌', '💡', '🔦', '🕯️', '🪔', '🧯', '🛢️', '💸', '💵', '💴',
      '💶', '💷', '🪙', '💰', '💳', '💎', '⚖️', '🧰', '🔧', '🔨',
      '⚒️', '🛠️', '⛏️', '🔩', '⚙️', '🧱', '⛓️', '🧲', '🔫', '💣',
      '🧨', '🪓', '🔪', '🗡️', '⚔️', '🛡️', '🚬', '⚰️', '⚱️', '🏺',
      '🔮', '📿', '🧿', '💈', '⚗️', '🔭', '🔬', '🕳️', '🩹', '🩺',
      '💊', '💉', '🧬', '🦠', '🧫', '🧪', '🌡️', '🧹', '🧺', '🧻',
      '🚽', '🚰', '🚿', '🛁', '🛀', '🛌', '🧴', '🧷', '🧹', '🧺',
      '🧼', '🪠', '🪤', '🪣', '🧽', '🛎️', '🔑', '🗝️', '🚪', '🪑',
      '🛋️', '🛏️', '🛌', '🧸', '🖼️', '🛍️', '🛒', '🎁', '🎈', '🎏',
      '🎀', '🎊', '🎉', '🎎', '🏮', '🎐', '🧧', '✉️', '📩', '📨',
      '📧', '💌', '📥', '📤', '📦', '🏷️', '📪', '📫', '📬', '📭',
      '📮', '📯', '📜', '📃', '📄', '📑', '📊', '📈', '📉', '🗒️',
      '🗓️', '📆', '📅', '📇', '🗃️', '🗳️', '🗄️', '📋', '📁', '📂',
      '🗂️', '🗞️', '📰', '📓', '📔', '📒', '📕', '📗', '📘', '📙',
      '📚', '📖', '🔖', '🧷', '🔗', '📎', '🖇️', '📐', '📏', '🧮',
      '📌', '📍', '✂️', '🖊️', '🖋️', '✒️', '🖌️', '🖍️', '📝', '✏️',
      '🔍', '🔎', '🔏', '🔐', '🔒', '🔓', '🗝️', '🔑', '🔨', '🔩',
      '⚙️', '🔧', '🔪', '🔯', '🧿', '🧭', '🧰', '🧲'
    ]
  },
  symbols: {
    name: '符号',
    icons: [
      '❤️', '🧡', '💛', '💚', '💙', '💜', '🖤', '🤍', '🤎', '💔',
      '❣️', '💕', '💞', '💓', '💗', '💖', '💘', '💝', '💟', '☮️',
      '✝️', '☪️', '🕉️', '☸️', '✡️', '🔯', '🕎', '☯️', '☦️', '🛐',
      '⛎', '♈', '♉', '♊', '♋', '♌', '♍', '♎', '♏', '♐',
      '♑', '♒', '♓', '🆔', '⚛️', '🉑', '☢️', '☣️', '📴', '📳',
      '🈶', '🈚', '🈸', '🈺', '🈷️', '✴️', '🆚', '💮', '🉐', '㊙️',
      '㊗️', '🈴', '🈵', '🈹', '🈲', '🅰️', '🅱️', '🆎', '🆑', '🅾️',
      '🆘', '❌', '⭕', '🛑', '⛔', '📛', '🚫', '💯', '💢', '♨️',
      '🚷', '🚯', '🚳', '🚱', '🔞', '📵', '🚭', '❗', '❕', '❓',
      '❔', '‼️', '⁉️', '🔅', '🔆', '〽️', '⚠️', '🚸', '🔱', '⚜️',
      '🔰', '♻️', '✅', '🈯', '💹', '❇️', '✳️', '❎', '🌐', '💠',
      'Ⓜ️', '🌀', '💤', '🏧', '🚾', '♿', '🅿️', '🛗', '🈳', '🈂️',
      '🛂', '🛃', '🛄', '🛅', '🚹', '🚺', '🚼', '🚻', '🚮', '🎦',
      '📶', '🈁', '🔣', 'ℹ️', '🔤', '🔡', '🔠', '🆖', '🆗', '🆙',
      '🆒', '🆕', '🆓', '0️⃣', '1️⃣', '2️⃣', '3️⃣', '4️⃣', '5️⃣',
      '6️⃣', '7️⃣', '8️⃣', '9️⃣', '🔟', '🔢', '#️⃣', '*️⃣', '⏏️',
      '▶️', '⏸️', '⏯️', '⏹️', '⏺️', '⏭️', '⏮️', '◀️', '🔼', '🔽',
      '➡️', '⬅️', '⬆️', '⬇️', '↗️', '↘️', '↙️', '↖️', '↕️', '↔️',
      '↪️', '↩️', '⤴️', '⤵️', '🔀', '🔁', '🔂', '🔄', '🔃', '🎵',
      '🎶', '➕', '➖', '➗', '✖️', '♾️', '💲', '💱', '™️', '©️',
      '®️', '👁️‍🗨️', '🔚', '🔙', '🔛', '🔝', '🔜', '〰️', '➰', '➿',
      '✔️', '☑️', '🔘', '🔴', '🟠', '🟡', '🟢', '🔵', '🟣', '⚫',
      '⚪', '🟤', '🔺', '🔻', '🔸', '🔹', '🔶', '🔷', '🔳', '🔲',
      '▪️', '▫️', '◾', '◽', '◼️', '◻️'
    ]
  }
}

// 获取过滤后的 emoji 图标（不支持关键词搜索）
const getFilteredEmojiIcons = () => {
  const category = emojiCategories[currentEmojiCategory.value]
  if (!category) return []

  return category.icons.map(icon => ({
    key: icon,
    name: '', // emoji 可以不显示名称
    category: currentEmojiCategory.value
  }))
}

// 获取过滤后的 Element Plus 图标
const getFilteredElementIcons = () => {
  const allIconNames = Object.keys(ElementPlusIconsVue)

  let icons = allIconNames

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    icons = icons.filter(icon =>
      icon.toLowerCase().includes(keyword) ||
      icon.replace(/([A-Z])/g, ' $1').toLowerCase().includes(keyword)
    )
  }

  // 按名称排序
  icons.sort()

  return icons.map(icon => ({
    key: icon,
    name: icon.replace(/([A-Z])/g, ' $1').trim(),
    category: 'all',
    component: ElementPlusIconsVue[icon as keyof typeof ElementPlusIconsVue]
  }))
}
</script>

<style scoped>
.icon-selector {
  padding: 12px;
}

.icon-tabs {
  margin-bottom: 12px;
}

.emoji-category-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 12px;
}

.emoji-category-tag {
  cursor: pointer;
  user-select: none;
  transition: all 0.2s;
}

.emoji-category-tag:hover {
  opacity: 0.8;
}

.icon-grid {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 8px;
}

.icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 8px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  min-height: 64px;
}

.icon-item:hover {
  border-color: var(--el-color-primary);
  background-color: var(--el-color-primary-light-9);
  transform: translateY(-1px);
}

.icon-item.active {
  border-color: var(--el-color-primary);
  background-color: var(--el-color-primary-light-8);
}

.icon-display {
  font-size: 24px;
  line-height: 1;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 24px;
}

.emoji-display {
  font-size: 28px;
  line-height: 1;
}

.icon-name {
  font-size: 11px;
  color: #909399;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
}

.icon-item.active .icon-name {
  color: var(--el-color-primary);
  font-weight: 500;
}

.icon-preview {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}

.emoji-icon {
  font-size: 20px;
}

:deep(.el-tabs__header) {
  margin: 0 0 8px 0;
}

:deep(.el-tabs__item) {
  padding: 0 12px;
  height: 32px;
  line-height: 32px;
}

:deep(.el-scrollbar__view) {
  padding: 0;
}

.mb-3 {
  margin-bottom: 12px;
}
</style>