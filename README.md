# 我的博客 - 前端项目

一个基于 Vue 3 + TypeScript + Tailwind CSS 的现代化个人博客网站。

## 📋 项目简介

这是一个功能完善的个人博客前端项目，提供了文章展示、分类筛选、搜索等功能。项目采用了现代化的前端技术栈，代码结构清晰，易于维护和扩展。

## 🛠️ 技术栈

- **前端框架**: Vue 3 (Composition API)
- **开发语言**: TypeScript
- **状态管理**: Pinia
- **路由管理**: Vue Router
- **UI 组件库**: Element Plus
- **样式方案**: Tailwind CSS
- **构建工具**: Vite
- **Markdown 渲染**: markdown-it
- **代码高亮**: highlight.js

## ✨ 功能特性

### 第一阶段功能

- ✅ **首页**: 展示博客简介、最新文章和统计信息
- ✅ **文章列表页**: 支持分页、搜索、分类筛选、标签筛选
- ✅ **文章详情页**: Markdown 格式文章渲染、代码高亮、相关文章推荐
- ✅ **关于我页面**: 个人信息、技能栈、成长历程展示
- ✅ **统一布局**: 顶部导航栏 + 页脚
- ✅ **主题切换**: 支持亮色/暗色主题切换
- ✅ **响应式设计**: 完美适配桌面端和移动端

## 📁 项目结构

```
web/
├── public/                 # 静态资源
├── src/
│   ├── assets/            # 资源文件
│   ├── components/        # 公共组件
│   │   ├── layout/       # 布局组件
│   │   │   ├── Header.vue    # 顶部导航栏
│   │   │   └── Footer.vue    # 页脚
│   │   └── ArticleCard.vue   # 文章卡片组件
│   ├── layouts/           # 布局模板
│   │   └── MainLayout.vue    # 主布局
│   ├── views/             # 页面组件
│   │   ├── Home.vue          # 首页
│   │   ├── ArticleList.vue   # 文章列表
│   │   ├── ArticleDetail.vue # 文章详情
│   │   └── About.vue         # 关于我
│   ├── stores/            # Pinia 状态管理
│   │   ├── app.ts           # 应用全局状态
│   │   └── article.ts       # 文章状态
│   ├── router/            # 路由配置
│   │   └── index.ts
│   ├── utils/             # 工具函数
│   │   ├── markdown.ts      # Markdown 渲染
│   │   └── format.ts        # 格式化工具
│   ├── types/             # TypeScript 类型定义
│   │   └── index.ts
│   ├── mock/              # 模拟数据
│   │   └── articles.ts      # 文章数据
│   ├── App.vue            # 根组件
│   ├── main.ts            # 入口文件
│   └── style.css          # 全局样式
├── index.html
├── package.json
├── tsconfig.json
├── vite.config.ts
├── tailwind.config.js
├── postcss.config.js
└── README.md
```

## 🚀 快速开始

### 环境要求

- Node.js >= 16.0.0
- npm >= 7.0.0

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

访问 http://localhost:3000 查看项目。

### 构建生产版本

```bash
npm run build
```

### 预览生产构建

```bash
npm run preview
```

## 📝 使用说明

### 添加文章

在 `src/mock/articles.ts` 文件中添加新的文章对象：

```typescript
{
  id: 7,
  title: '文章标题',
  summary: '文章摘要',
  content: `# Markdown 内容`,
  author: '作者',
  category: '分类',
  tags: ['标签1', '标签2'],
  coverImage: '封面图片URL',
  createdAt: '2024-01-01T00:00:00Z',
  updatedAt: '2024-01-01T00:00:00Z',
  views: 0
}
```

### 修改个人信息

在 `src/stores/app.ts` 中修改 `userInfo` 对象：

```typescript
const userInfo = ref<UserInfo>({
  name: '你的名字',
  avatar: '头像URL',
  bio: '个人简介',
  email: '邮箱',
  github: 'GitHub链接',
  twitter: 'Twitter链接'
})
```

### 自定义主题颜色

在 `tailwind.config.js` 中修改主题颜色：

```javascript
theme: {
  extend: {
    colors: {
      primary: {
        // 自定义颜色值
      }
    }
  }
}
```

## 🎨 主要组件说明

### ArticleCard 组件

文章卡片组件，用于展示文章摘要信息。

**Props:**
- `article`: Article - 文章对象

### Header 组件

顶部导航栏，包含 Logo、导航菜单、主题切换按钮。

### Footer 组件

页脚组件，显示博客信息、快速链接、联系方式。

## 🔧 核心功能实现

### Markdown 渲染

使用 `markdown-it` 解析 Markdown 文本，并使用 `highlight.js` 进行代码高亮。

```typescript
import { renderMarkdown } from '@/utils/markdown'

const html = renderMarkdown('# Hello World')
```

### 状态管理

使用 Pinia 管理全局状态：

- **appStore**: 应用级别状态（主题、用户信息等）
- **articleStore**: 文章相关状态（文章列表、当前文章等）

### 路由配置

使用 Vue Router 进行页面路由管理，支持路由懒加载和页面标题自动更新。

## 📱 响应式设计

项目采用移动优先的响应式设计策略，使用 Tailwind CSS 的响应式工具类：

- `sm`: ≥640px
- `md`: ≥768px
- `lg`: ≥1024px
- `xl`: ≥1280px

## 🎯 后续计划

- [ ] 接入后端 API
- [ ] 添加评论功能
- [ ] 添加文章搜索高亮
- [ ] 添加文章目录导航
- [ ] 添加阅读进度条
- [ ] 添加文章点赞功能
- [ ] SEO 优化
- [ ] 性能优化

## 📄 许可证

MIT License

## 👨‍💻 作者

DutyZero

## 🙏 致谢

感谢所有开源项目的贡献者们！

