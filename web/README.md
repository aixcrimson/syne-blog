# syne-blog

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

- Node.js >= 18.0.0
- npm >= 8.0.0

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

## 📱 响应式设计

项目采用移动优先的响应式设计策略，使用 Tailwind CSS 的响应式工具类：

- `sm`: ≥640px
- `md`: ≥768px
- `lg`: ≥1024px
- `xl`: ≥1280px

## 📄 许可证

MIT License

## 👨‍💻 作者

DutyZero
