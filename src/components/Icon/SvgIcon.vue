<!--
  SVG 图标组件
  用于显示 iconfont Symbol 方式的图标
-->
<template>
  <svg :class="svgClass" :style="svgStyle" aria-hidden="true">
    <use :xlink:href="iconName" />
  </svg>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  /** 图标名称（不需要加 icon- 前缀） */
  name: string
  /** 图标颜色 */
  color?: string
  /** 图标大小 */
  size?: string | number
  /** 自定义类名 */
  className?: string
}

const props = withDefaults(defineProps<Props>(), {
  color: 'currentColor',
  size: '1em',
  className: ''
})

/**
 * 图标名称（添加 #icon- 前缀）
 */
const iconName = computed(() => `#icon-${props.name}`)

/**
 * SVG 类名
 */
const svgClass = computed(() => {
  return ['svg-icon', props.className].filter(Boolean).join(' ')
})

/**
 * SVG 样式
 */
const svgStyle = computed(() => {
  const size = typeof props.size === 'number' ? `${props.size}px` : props.size
  return {
    width: size,
    height: size,
    color: props.color,
    fill: 'currentColor'
  }
})
</script>

<style scoped>
.svg-icon {
  display: inline-block;
  vertical-align: middle;
  overflow: hidden;
}
</style>

