# Syne Blog

一个现代化的全栈博客系统，前后端分离架构，包含博客前台、管理后台和后端服务。

## 📦 项目结构

```
syne-blog/
├── server/          # 后端服务 (Spring Boot)
│   ├── src/
│   ├── pom.xml
│   └── README.md
├── web/             # 博客前台 (Vue 3 + TypeScript)
│   ├── src/
│   ├── package.json
│   └── README.md
├── admin/           # 管理后台 (Vue 3 + TypeScript)
│   ├── src/
│   ├── package.json
│   └── README.md
└── README.md        # 项目总体说明
```

## 🛠️ 技术栈

### 后端 (Server)

- **Spring Boot 3.2.0** - Java 后端框架
- **MyBatis-Plus 3.5.5** - 持久层框架
- **PostgreSQL** - 关系型数据库
- **Spring Security + JWT** - 认证授权
- **Swagger/OpenAPI 3** - API 文档
- **Lombok** - 代码简化
- **Hutool** - 工具类库

### 博客前台 (Web)

- **Vue 3** - 渐进式 JavaScript 框架
- **TypeScript** - 类型安全
- **Vite** - 构建工具
- **Element Plus** - UI 组件库
- **TailwindCSS** - CSS 框架
- **Pinia** - 状态管理
- **Vue Router** - 路由管理
- **Axios** - HTTP 客户端
- **markdown-it** - Markdown 渲染
- **highlight.js** - 代码高亮

### 管理后台 (Admin)

- **Vue 3** - 渐进式 JavaScript 框架
- **TypeScript** - 类型安全
- **Vite 5** - 构建工具
- **Element Plus** - UI 组件库
- **TailwindCSS 3** - CSS 框架
- **Pinia** - 状态管理
- **Vue Router 4** - 路由管理
- **md-editor-v3** - Markdown 编辑器
- **vuedraggable** - 拖拽排序
- **Day.js** - 日期处理

## ✨ 功能特性

### 博客前台 (Web)

- ✅ **首页**: 展示博客简介、最新文章和统计信息
- ✅ **文章列表页**: 支持分页、搜索、分类筛选、标签筛选
- ✅ **文章详情页**: Markdown 格式文章渲染、代码高亮、相关文章推荐
- ✅ **关于我页面**: 个人信息、技能栈、成长历程展示
- ✅ **导航页**: 个人收藏网站导航
- ✅ **主题切换**: 支持亮色/暗色主题切换
- ✅ **响应式设计**: 完美适配桌面端和移动端
- ✅ **评论系统**: 支持文章评论和回复

### 管理后台 (Admin)

- ✅ **仪表盘**: 数据统计概览、最近文章、最近评论
- ✅ **文章管理**: 文章增删改查、Markdown 编辑、状态管理、置顶/推荐
- ✅ **分类管理**: 分类增删改查、关联文章统计
- ✅ **标签管理**: 标签增删改查、使用统计
- ✅ **评论管理**: 评论列表、审核通过/驳回、批量操作
- ✅ **导航管理**: 分类和站点管理、拖拽排序、**Chrome 书签导入**
- ✅ **作者信息**: 公告、技能、项目、时间线管理
- ✅ **用户管理**: 用户增删改查、角色权限、状态管理
- ✅ **个人设置**: 信息修改、密码修改
- ✅ **JWT 认证**: Token 认证、自动过期检测

### 后端服务 (Server)

- ✅ **RESTful API**: 规范的 API 设计
- ✅ **JWT 认证**: 安全的用户认证机制
- ✅ **权限控制**: 基于角色的访问控制
- ✅ **分页查询**: 支持多条件筛选和分页
- ✅ **统一响应**: 规范的 API 响应格式
- ✅ **异常处理**: 全局异常处理机制
- ✅ **日志记录**: 完善的日志体系

## 🚀 快速开始

### 环境要求

- **Node.js**: 18+ (前端)
- **JDK**: 17+ (后端)
- **Maven**: 3.6+ (后端)
- **PostgreSQL**: 14+ (数据库)

### 1. 克隆项目

```bash
git clone https://github.com/aixcrimson/syne-blog.git
cd syne-blog
```

### 2. 数据库准备

```bash
# 创建数据库
psql -U postgres
CREATE DATABASE syne_blog;
\q

# 导入数据库结构
psql -U postgres -d syne_blog -f server/src/main/resources/sql/schema-postgres.sql
```

### 3. 启动后端

```bash
cd server

# 修改配置文件
# 编辑 src/main/resources/application-dev.yml
# 设置数据库密码

# 安装依赖并启动
mvn clean install
mvn spring-boot:run
```

后端服务将运行在: http://localhost:8080/api

### 4. 启动博客前台

```bash
cd web

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

博客前台将运行在: http://localhost:3000

### 5. 启动管理后台

```bash
cd admin

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

管理后台将运行在: http://localhost:5173

## 📖 详细文档

- [后端开发文档](./server/README.md)
- [博客前台文档](./web/README.md)
- [管理后台文档](./admin/README.md)

## 🏗️ 开发规范

### Git 提交规范

使用语义化提交信息：

```
feat: 新功能
fix: 修复 bug
docs: 文档更新
style: 代码格式调整
refactor: 重构
test: 测试相关
chore: 构建/工具链相关
```

示例：

```bash
git commit -m "feat(server): 添加用户登录接口"
git commit -m "fix(web): 修复文章列表分页问题"
git commit -m "feat(admin): 添加 Chrome 书签导入功能"
```

### 分支管理

```
main          # 主分支（生产环境）
develop       # 开发分支
feature/*     # 功能分支
bugfix/*      # 修复分支
release/*     # 发布分支
```

### 项目规范

- **后端**: 遵循 RESTful API 设计规范
- **前端**: 遵循 Vue 3 组合式 API 风格
- **代码风格**: 使用 ESLint + Prettier (前端)
- **注释**: 重要逻辑必须添加注释 (JSDoc 风格)

## 📂 目录说明

### 后端目录结构

```
server/src/main/java/com/syne/server/
├── config/          # 配置类（安全、跨域、Swagger等）
├── controller/      # 控制器
│   ├── admin/          # 管理端接口
│   └── web/            # 前台接口
├── service/         # 业务层
│   └── impl/           # 业务实现
├── mapper/          # 数据访问层
├── model/           # 数据模型
│   ├── entity/         # 实体类
│   ├── dto/            # 数据传输对象
│   └── vo/             # 视图对象
├── common/          # 通用类（Result、PageResult等）
├── utils/           # 工具类
└── exception/       # 自定义异常
```

### 博客前台目录结构

```
web/src/
├── api/             # API 接口
├── assets/          # 静态资源
├── components/      # 通用组件
│   └── layout/         # 布局组件
├── views/           # 页面组件
├── router/          # 路由配置
├── stores/          # 状态管理
├── utils/           # 工具函数
├── types/           # TypeScript 类型
└── App.vue          # 根组件
```

### 管理后台目录结构

```
admin/src/
├── api/             # API 接口封装
├── assets/          # 静态资源
├── components/      # 公共组件
│   └── layout/         # 布局组件
├── views/           # 页面组件
│   ├── article/        # 文章管理
│   ├── author/         # 作者信息
│   ├── category/       # 分类管理
│   ├── comment/        # 评论管理
│   ├── dashboard/      # 仪表盘
│   ├── navigation/     # 导航管理
│   ├── settings/       # 个人设置
│   ├── tag/            # 标签管理
│   └── user/           # 用户管理
├── router/          # 路由配置
├── stores/          # 状态管理
├── utils/           # 工具函数
├── types/           # TypeScript 类型
└── App.vue          # 根组件
```

## 🌐 部署

### 后端部署

支持 Docker 容器化部署，详见 [后端文档](./server/README.md)。

### 前端部署

前端项目可部署至：

- **Vercel** (推荐)
- **Netlify**
- **Nginx 静态托管**

### 在线演示

- 博客前台: https://syne-blog-web.vercel.app (示例)
- 管理后台: https://syne-blog-admin.vercel.app (示例)

## 📝 API 文档

启动后端后访问: http://localhost:8080/api/doc.html

## 📄 开源协议

MIT License

## 👥 贡献者

- [@Syne](https://github.com/aixcrimson) - 项目作者

## 🙏 致谢

感谢以下开源项目：

- [Vue.js](https://vuejs.org/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Element Plus](https://element-plus.org/)
- [TailwindCSS](https://tailwindcss.com/)
- [MyBatis-Plus](https://baomidou.com/)
