import type { Article } from '@/types'

export const mockArticles: Article[] = [
  {
    id: 1,
    title: 'Vue 3 Composition API 完全指南',
    summary: '深入了解 Vue 3 的 Composition API，掌握现代 Vue 开发的核心技术。本文将带你从基础到高级，全面掌握组合式 API 的使用方法。',
    content: `# Vue 3 Composition API 完全指南

## 什么是 Composition API？

Composition API 是 Vue 3 中引入的一套新的 API，它提供了一种更灵活的方式来组织组件逻辑。

## 为什么需要 Composition API？

在 Vue 2 中，我们使用 Options API 来组织代码，但随着组件变得越来越复杂，相关的逻辑会被拆分到不同的选项中：

- 数据在 \`data\` 中
- 方法在 \`methods\` 中  
- 计算属性在 \`computed\` 中
- 生命周期钩子分散各处

这使得阅读和维护大型组件变得困难。

## 核心概念

### 1. setup 函数

\`\`\`typescript
import { ref, computed } from 'vue'

export default {
  setup() {
    const count = ref(0)
    const doubleCount = computed(() => count.value * 2)
    
    function increment() {
      count.value++
    }
    
    return {
      count,
      doubleCount,
      increment
    }
  }
}
\`\`\`

### 2. 响应式引用

使用 \`ref\` 和 \`reactive\` 创建响应式数据：

\`\`\`typescript
import { ref, reactive } from 'vue'

const count = ref(0)
const state = reactive({
  name: 'Vue',
  version: 3
})
\`\`\`

### 3. 生命周期钩子

\`\`\`typescript
import { onMounted, onUnmounted } from 'vue'

onMounted(() => {
  console.log('组件已挂载')
})

onUnmounted(() => {
  console.log('组件已卸载')
})
\`\`\`

## 最佳实践

1. **逻辑复用**：使用组合式函数（Composables）
2. **类型安全**：结合 TypeScript 使用
3. **代码组织**：按功能而非选项组织代码

## 总结

Composition API 为 Vue 3 带来了更好的逻辑复用、类型推导和代码组织能力。`,
    author: 'DutyZero',
    category: 'Vue',
    tags: ['Vue3', 'JavaScript', 'Frontend'],
    coverImage: 'https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=800',
    createdAt: '2024-01-15T10:30:00Z',
    updatedAt: '2024-01-15T10:30:00Z',
    views: 1234
  },
  {
    id: 2,
    title: 'TypeScript 高级类型系统详解',
    summary: 'TypeScript 的类型系统非常强大，本文将深入探讨高级类型特性，包括泛型、条件类型、映射类型等。',
    content: `# TypeScript 高级类型系统详解

## 引言

TypeScript 的类型系统是其最强大的特性之一。掌握高级类型能帮助我们写出更安全、更灵活的代码。

## 泛型（Generics）

泛型允许我们在定义函数、接口或类时不预先指定具体的类型，而在使用时再指定：

\`\`\`typescript
function identity<T>(arg: T): T {
  return arg
}

const result = identity<string>('hello')
\`\`\`

## 条件类型（Conditional Types）

\`\`\`typescript
type IsString<T> = T extends string ? true : false

type A = IsString<string>  // true
type B = IsString<number>  // false
\`\`\`

## 映射类型（Mapped Types）

\`\`\`typescript
type Readonly<T> = {
  readonly [P in keyof T]: T[P]
}

interface User {
  name: string
  age: number
}

type ReadonlyUser = Readonly<User>
\`\`\`

## 实用工具类型

TypeScript 提供了许多内置的工具类型：

- \`Partial<T>\`：所有属性可选
- \`Required<T>\`：所有属性必需
- \`Pick<T, K>\`：选择部分属性
- \`Omit<T, K>\`：忽略部分属性

## 总结

掌握 TypeScript 的高级类型系统可以大大提升代码质量和开发效率。`,
    author: 'DutyZero',
    category: 'TypeScript',
    tags: ['TypeScript', 'JavaScript', '类型系统'],
    coverImage: 'https://images.unsplash.com/photo-1516116216624-53e697fedbea?w=800',
    createdAt: '2024-01-20T14:20:00Z',
    updatedAt: '2024-01-20T14:20:00Z',
    views: 856
  },
  {
    id: 3,
    title: 'Tailwind CSS 实战技巧',
    summary: '通过实际项目案例，学习如何高效使用 Tailwind CSS 构建现代化的用户界面。',
    content: `# Tailwind CSS 实战技巧

## 为什么选择 Tailwind CSS？

Tailwind CSS 是一个功能类优先的 CSS 框架，它提供了大量的工具类，让你可以快速构建自定义设计。

## 核心优势

1. **快速开发**：直接在 HTML 中使用工具类
2. **高度可定制**：通过配置文件自定义设计系统
3. **响应式设计**：内置响应式前缀
4. **性能优化**：自动移除未使用的 CSS

## 实用技巧

### 1. 响应式设计

\`\`\`html
<div class="w-full md:w-1/2 lg:w-1/3">
  响应式容器
</div>
\`\`\`

### 2. 状态变体

\`\`\`html
<button class="bg-blue-500 hover:bg-blue-700 active:bg-blue-800">
  点击我
</button>
\`\`\`

### 3. 自定义配置

\`\`\`javascript
// tailwind.config.js
module.exports = {
  theme: {
    extend: {
      colors: {
        primary: '#3490dc'
      }
    }
  }
}
\`\`\`

## 最佳实践

- 使用 @apply 指令提取重复的工具类
- 利用配置文件建立设计系统
- 配合组件化开发使用

## 总结

Tailwind CSS 是现代前端开发的利器，掌握它能大大提升开发效率。`,
    author: 'DutyZero',
    category: 'CSS',
    tags: ['CSS', 'Tailwind', 'UI'],
    coverImage: 'https://images.unsplash.com/photo-1507721999472-8ed4421c4af2?w=800',
    createdAt: '2024-02-01T09:15:00Z',
    updatedAt: '2024-02-01T09:15:00Z',
    views: 692
  },
  {
    id: 4,
    title: 'Pinia 状态管理最佳实践',
    summary: 'Pinia 是 Vue 3 官方推荐的状态管理库，本文介绍如何在实际项目中使用 Pinia。',
    content: `# Pinia 状态管理最佳实践

## Pinia 简介

Pinia 是 Vue 的专属状态管理库，它是 Vuex 的继任者，提供了更简洁的 API 和更好的 TypeScript 支持。

## 创建 Store

\`\`\`typescript
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const name = ref('张三')
  const age = ref(25)
  
  const info = computed(() => \`\${name.value}, \${age.value}岁\`)
  
  function updateName(newName: string) {
    name.value = newName
  }
  
  return { name, age, info, updateName }
})
\`\`\`

## 使用 Store

\`\`\`vue
<script setup>
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
</script>

<template>
  <div>{{ userStore.info }}</div>
  <button @click="userStore.updateName('李四')">
    更新名字
  </button>
</template>
\`\`\`

## 核心概念

### State

状态是存储在 store 中的数据。

### Getters

计算属性，基于 state 派生数据。

### Actions

修改 state 的方法，可以是异步的。

## 最佳实践

1. **模块化**：按功能划分不同的 store
2. **类型安全**：充分利用 TypeScript
3. **组合式风格**：使用 Composition API 风格定义 store

## 总结

Pinia 简化了状态管理，是 Vue 3 项目的首选方案。`,
    author: 'DutyZero',
    category: 'Vue',
    tags: ['Vue3', 'Pinia', '状态管理'],
    coverImage: 'https://images.unsplash.com/photo-1555949963-aa79dcee981c?w=800',
    createdAt: '2024-02-10T16:45:00Z',
    updatedAt: '2024-02-10T16:45:00Z',
    views: 543
  },
  {
    id: 5,
    title: 'Element Plus 组件库使用指南',
    summary: 'Element Plus 是基于 Vue 3 的组件库，本文介绍如何在项目中使用它来快速构建界面。',
    content: `# Element Plus 组件库使用指南

## 什么是 Element Plus？

Element Plus 是一套为开发者、设计师和产品经理准备的基于 Vue 3.0 的桌面端组件库。

## 安装与配置

\`\`\`bash
npm install element-plus
\`\`\`

### 完整引入

\`\`\`typescript
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'

const app = createApp(App)
app.use(ElementPlus)
app.mount('#app')
\`\`\`

### 按需引入

使用 unplugin-vue-components 实现自动按需引入。

## 常用组件

### Button 按钮

\`\`\`vue
<el-button type="primary">主要按钮</el-button>
<el-button type="success">成功按钮</el-button>
<el-button type="warning">警告按钮</el-button>
\`\`\`

### Form 表单

\`\`\`vue
<el-form :model="form" label-width="120px">
  <el-form-item label="用户名">
    <el-input v-model="form.name" />
  </el-form-item>
  <el-form-item>
    <el-button type="primary" @click="onSubmit">
      提交
    </el-button>
  </el-form-item>
</el-form>
\`\`\`

### Table 表格

\`\`\`vue
<el-table :data="tableData">
  <el-table-column prop="name" label="姓名" />
  <el-table-column prop="age" label="年龄" />
</el-table>
\`\`\`

## 主题定制

可以通过 CSS 变量自定义主题颜色。

## 总结

Element Plus 提供了丰富的组件，能够快速构建企业级应用。`,
    author: 'DutyZero',
    category: 'Vue',
    tags: ['Vue3', 'Element Plus', 'UI组件'],
    coverImage: 'https://images.unsplash.com/photo-1558655146-9f40138edfeb?w=800',
    createdAt: '2024-02-18T11:30:00Z',
    updatedAt: '2024-02-18T11:30:00Z',
    views: 421
  },
  {
    id: 6,
    title: '前端性能优化实战',
    summary: '从多个维度讲解前端性能优化的方法和技巧，包括加载优化、渲染优化、代码优化等。',
    content: `# 前端性能优化实战

## 为什么需要性能优化？

性能直接影响用户体验，研究表明：
- 页面加载时间每增加 1 秒，转化率下降 7%
- 53% 的移动用户会放弃加载超过 3 秒的页面

## 优化策略

### 1. 加载优化

**代码分割**
\`\`\`javascript
// 路由懒加载
const Home = () => import('./views/Home.vue')
\`\`\`

**资源压缩**
- 使用 Gzip/Brotli 压缩
- 压缩图片资源
- Tree Shaking 移除未使用代码

### 2. 渲染优化

**虚拟列表**

对于长列表，只渲染可视区域的内容。

**防抖和节流**
\`\`\`javascript
import { debounce } from 'lodash'

const handleSearch = debounce((keyword) => {
  // 搜索逻辑
}, 300)
\`\`\`

### 3. 缓存策略

- 浏览器缓存
- Service Worker
- CDN 缓存

### 4. 图片优化

- 使用 WebP 格式
- 懒加载
- 响应式图片

## 性能监控

使用 Lighthouse、WebPageTest 等工具监控性能指标：
- FCP (First Contentful Paint)
- LCP (Largest Contentful Paint)
- CLS (Cumulative Layout Shift)

## 总结

性能优化是一个持续的过程，需要在开发中时刻关注。`,
    author: 'DutyZero',
    category: '性能优化',
    tags: ['性能', 'Web', '优化'],
    coverImage: 'https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=800',
    createdAt: '2024-02-25T13:00:00Z',
    updatedAt: '2024-02-25T13:00:00Z',
    views: 789
  }
]

