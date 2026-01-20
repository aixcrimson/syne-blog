# Syne Blog Admin - 博客管理后台

基于 Vue 3 + TypeScript + Element Plus 的博客后台管理系统。

## 📋 项目简介

这是 Syne Blog 的管理后台，提供全面的博客内容管理功能，包括文章管理、分类标签管理、评论审核、导航管理、用户管理等。项目采用现代化前端架构，界面美观，操作便捷。

## 🛠️ 技术栈

- **前端框架**: Vue 3 (Composition API + `<script setup>`)
- **开发语言**: TypeScript
- **状态管理**: Pinia
- **路由管理**: Vue Router 4
- **UI 组件库**: Element Plus
- **样式方案**: TailwindCSS 3
- **构建工具**: Vite 5
- **Markdown 编辑器**: md-editor-v3
- **拖拽排序**: vuedraggable
- **HTTP 客户端**: Axios
- **日期处理**: Day.js

## ✨ 功能特性

### 🔐 认证与权限

- JWT Token 认证
- 自动 Token 过期检测与刷新
- 路由级别权限控制
- 登录状态持久化

### 📊 仪表盘

- 数据统计概览（文章数、分类数、标签数、评论数、浏览量、点赞数）
- 最近发布文章列表
- 最近评论列表
- 数据可视化展示

### 📝 文章管理

- 文章列表（分页、搜索、筛选）
- 文章创建/编辑（Markdown 编辑器）
- 文章状态管理（发布/草稿/下架）
- 文章置顶/推荐功能
- 批量删除文章

### 📁 分类管理

- 分类列表展示
- 分类增删改查
- 分类关联文章统计

### 🏷️ 标签管理

- 标签列表展示
- 标签增删改查
- 标签使用统计

### 💬 评论管理

- 评论列表（分页、搜索、时间筛选）
- 评论审核（通过/驳回）
- 批量操作支持
- 评论详情查看

### 🔗 导航管理

- 导航分类管理
- 导航站点管理
- **拖拽排序**（分类排序、站点排序、跨分类拖拽）
- **Chrome 书签导入**
  - 支持解析 Chrome 导出的 HTML 书签文件
  - 文件夹到分类的灵活映射
  - 可选择导入到现有分类或创建新分类

### 👤 作者信息管理

- **公告管理**: 发布/编辑/删除公告，控制显示状态
- **技能管理**: 管理技能标签和熟练度
- **项目管理**: 展示项目经历，支持精选标记
- **时间线管理**: 成长历程记录

### 👥 用户管理

- 用户列表（分页、搜索、角色/状态筛选）
- 用户创建/编辑/删除
- 账号状态管理
- 角色权限分配

### ⚙️ 个人设置

- 个人信息修改
- 密码修改

## 📁 项目结构

```
admin/
├── public/                 # 静态资源
├── src/
│   ├── api/               # API 接口封装
│   │   ├── article.ts        # 文章相关接口
│   │   ├── auth.ts           # 认证相关接口
│   │   ├── category.ts       # 分类相关接口
│   │   ├── comment.ts        # 评论相关接口
│   │   ├── dashboard.ts      # 仪表盘相关接口
│   │   ├── navigation.ts     # 导航相关接口
│   │   ├── siteContent.ts    # 作者信息相关接口
│   │   ├── tag.ts            # 标签相关接口
│   │   ├── user.ts           # 用户相关接口
│   │   └── request.ts        # Axios 封装
│   ├── assets/            # 静态资源（图片、图标等）
│   ├── components/        # 公共组件
│   │   └── layout/           # 布局组件
│   │       ├── AdminLayout.vue   # 管理后台布局
│   │       ├── Sidebar.vue       # 侧边栏导航
│   │       └── Navbar.vue        # 顶部导航栏
│   ├── router/            # 路由配置
│   │   └── index.ts          # 路由定义与守卫
│   ├── stores/            # Pinia 状态管理
│   │   ├── app.ts            # 应用全局状态
│   │   └── user.ts           # 用户状态
│   ├── types/             # TypeScript 类型定义
│   ├── utils/             # 工具函数
│   ├── views/             # 页面组件
│   │   ├── article/          # 文章管理
│   │   │   ├── list.vue         # 文章列表
│   │   │   └── edit.vue         # 文章编辑
│   │   ├── author/           # 作者信息管理
│   │   │   ├── index.vue        # 作者信息主页
│   │   │   └── components/      # 子组件（公告、技能、项目、时间线）
│   │   ├── category/         # 分类管理
│   │   ├── comment/          # 评论管理
│   │   ├── dashboard/        # 仪表盘
│   │   ├── login/            # 登录页
│   │   ├── navigation/       # 导航管理
│   │   │   ├── index.vue        # 导航管理主页
│   │   │   └── components/      # 子组件（书签导入等）
│   │   ├── settings/         # 个人设置
│   │   ├── tag/              # 标签管理
│   │   └── user/             # 用户管理
│   ├── App.vue            # 根组件
│   ├── main.ts            # 入口文件
│   └── style.css          # 全局样式
├── index.html
├── package.json
├── tsconfig.json
├── vite.config.ts
├── tailwind.config.js
└── README.md
```

## 🚀 快速开始

### 环境要求

- Node.js >= 18.0.0
- npm >= 8.0.0

### 安装依赖

```bash
cd admin
npm install
```

### 配置环境变量

根据需要修改环境配置文件：

- `.env.development` - 开发环境配置
- `.env.production` - 生产环境配置
- `.env.staging` - 预发布环境配置

```bash
# .env.development 示例
VITE_API_BASE_URL=http://localhost:8080/api
```

### 启动开发服务器

```bash
# 连接本地后端
npm run dev

# 连接生产后端（用于调试）
npm run dev:prod
```

访问 http://localhost:5173 查看项目。

### 构建生产版本

```bash
npm run build
```

构建产物将输出到 `dist` 目录。

### 预览生产构建

```bash
npm run preview
```

## 📱 响应式设计

管理后台采用响应式设计，支持多种设备访问：

- **桌面端**: 完整侧边栏导航体验
- **平板端**: 可折叠侧边栏
- **移动端**: 抽屉式导航菜单

响应断点：

- `sm`: ≥640px
- `md`: ≥768px
- `lg`: ≥1024px
- `xl`: ≥1280px

## 🔧 开发规范

### 代码风格

- 使用 Vue 3 Composition API + `<script setup>` 语法
- TypeScript 严格模式
- 组件按功能模块化组织
- API 接口统一封装

### 命名规范

- **组件文件**: PascalCase (如 `ArticleList.vue`)
- **工具函数**: camelCase (如 `formatDate.ts`)
- **常量**: UPPER_SNAKE_CASE
- **接口类型**: PascalCase + 后缀 (如 `ArticleDTO`, `UserVO`)

### 注释规范

使用 JSDoc 风格注释，详细说明函数用途和参数。

## 📄 许可证

MIT License

## 👨‍💻 作者

DutyZero / Syne
