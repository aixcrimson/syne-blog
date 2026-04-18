# Syne Blog Server - 后端服务

基于 Spring Boot 3.2 + MyBatis-Plus + PostgreSQL 的博客系统后端。

## 📋 项目简介

这是 Syne Blog 的后端服务，提供博客前台和管理后台的所有 API 接口。项目采用 RESTful 风格设计，支持 JWT 认证、权限控制、分页查询等功能。

## 🛠️ 技术栈

- **Spring Boot 3.2.0** - 核心框架
- **MyBatis-Plus 3.5.5** - 持久层框架
- **PostgreSQL** - 数据库
- **Spring Security** - 安全框架
- **JWT** - 认证方案
- **Swagger/OpenAPI 3** - API 文档
- **Lombok** - 代码简化
- **Hutool** - 工具类库

## ✨ 功能特性

### 🔐 认证授权

- JWT Token 认证
- 基于角色的权限控制
- 接口级别权限验证

### 📝 文章模块

- 文章 CRUD 操作
- 分页查询（支持多条件筛选）
- 文章状态管理（发布/草稿/下架）
- 文章置顶/推荐功能
- 文章浏览量/点赞统计

### 📁 分类与标签

- 分类 CRUD
- 标签 CRUD
- 文章关联查询

### 💬 评论模块

- 评论列表（分页、多条件筛选）
- 评论审核（通过/驳回）
- 批量操作支持
- 嵌套回复结构

### 🔗 导航模块

- 导航分类管理
- 导航站点管理
- 排序功能（分类排序、站点排序）
- **Chrome 书签解析与导入**
  - 支持解析 Chrome 导出的 HTML 书签文件
  - 自动提取书签文件夹和站点信息
  - 灵活的分类映射和批量导入

### 👤 作者信息模块

- 公告管理（CRUD、显示状态控制）
- 技能管理（CRUD）
- 项目管理（CRUD、精选标记）
- 时间线管理（CRUD）

### 👥 用户模块

- 用户 CRUD
- 角色权限管理
- 账号状态控制
- 密码修改

### 📊 仪表盘

- 数据统计（文章数、分类数、标签数、评论数等）
- 最近文章列表
- 最近评论列表

## 🚀 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- PostgreSQL 14+

### 1. 数据库准备

```bash
# 创建数据库
psql -U postgres
CREATE DATABASE syne_blog;
\q

# 导入表结构
psql -U postgres -d syne_blog -f src/main/resources/sql/schema-postgres.sql
```

### 2. 配置文件（重要 ⚠️）

**推荐方式：使用本地配置文件（密码不会提交到 Git）**


```bash
# Windows PowerShell
$env:DB_PASSWORD="your_password"

# IDEA: Run → Edit Configurations → Environment variables
# 添加: DB_PASSWORD=your_password
```

### 3. 安装依赖

```bash
mvn clean install
```

### 4. 启动项目

```bash
mvn spring-boot:run
```

或使用 IDE 运行 `ServerApplication.java`

### 5. 验证启动

访问: http://localhost:8080/api

API 文档: http://localhost:8080/api/doc.html

## 📁 项目结构

```
server/
├── src/
│   ├── main/
│   │   ├── java/com/syne/server/
│   │   │   ├── config/          # 配置类
│   │   │   │   ├── SecurityConfig.java      # 安全配置
│   │   │   │   ├── CorsConfig.java          # 跨域配置
│   │   │   │   └── SwaggerConfig.java       # API 文档配置
│   │   │   ├── controller/      # 控制器
│   │   │   │   ├── admin/          # 管理端接口
│   │   │   │   │   ├── ArticleController.java
│   │   │   │   │   ├── AuthController.java
│   │   │   │   │   ├── CategoryController.java
│   │   │   │   │   ├── CommentController.java
│   │   │   │   │   ├── DashboardController.java
│   │   │   │   │   ├── NavigationController.java
│   │   │   │   │   ├── SiteContentController.java
│   │   │   │   │   ├── TagController.java
│   │   │   │   │   └── UserController.java
│   │   │   │   └── web/            # 前台接口
│   │   │   │       ├── ArticleController.java
│   │   │   │       ├── AuthController.java
│   │   │   │       ├── CategoryController.java
│   │   │   │       ├── CommentController.java
│   │   │   │       ├── NavigationController.java
│   │   │   │       ├── SiteContentController.java
│   │   │   │       ├── StatsController.java
│   │   │   │       ├── TagController.java
│   │   │   │       └── UserController.java
│   │   │   ├── service/         # 业务层
│   │   │   │   └── impl/           # 业务实现
│   │   │   ├── mapper/          # 数据访问层
│   │   │   ├── model/           # 数据模型
│   │   │   │   ├── entity/         # 实体类
│   │   │   │   ├── dto/            # 数据传输对象
│   │   │   │   └── vo/             # 视图对象
│   │   │   ├── common/          # 通用类
│   │   │   │   ├── Result.java        # 统一响应
│   │   │   │   ├── PageQuery.java     # 分页查询
│   │   │   │   └── PageResult.java    # 分页结果
│   │   │   ├── utils/           # 工具类
│   │   │   │   ├── JwtUtils.java      # JWT 工具
│   │   │   │   └── SecurityUtils.java # 安全工具
│   │   │   ├── exception/       # 异常处理
│   │   │   └── ServerApplication.java
│   │   └── resources/
│   │       ├── mapper/          # MyBatis XML
│   │       ├── sql/             # SQL 脚本
│   │       │   └── schema-postgres.sql
│   │       ├── application.yml        # 主配置
│   │       ├── application-dev.yml    # 开发配置
│   │       └── application-prod.yml   # 生产配置
│   └── test/
├── pom.xml
├── Dockerfile
└── README.md
```

## 📡 API 接口概览

### 管理端接口 (`/admin/*`)

| 模块     | 路径                    | 说明                     |
| -------- | ----------------------- | ------------------------ |
| 认证     | `/admin/auth/*`         | 登录、获取用户信息       |
| 仪表盘   | `/admin/dashboard/*`    | 统计数据、最近内容       |
| 文章     | `/admin/articles/*`     | 文章 CRUD、状态管理      |
| 分类     | `/admin/categories/*`   | 分类 CRUD                |
| 标签     | `/admin/tags/*`         | 标签 CRUD                |
| 评论     | `/admin/comments/*`     | 评论列表、审核           |
| 导航     | `/admin/navigation/*`   | 分类和站点管理、书签导入 |
| 作者信息 | `/admin/site-content/*` | 公告、技能、项目、时间线 |
| 用户     | `/admin/users/*`        | 用户 CRUD、密码修改      |

### 前台接口 (`/web/*`)

| 模块     | 路径                  | 说明               |
| -------- | --------------------- | ------------------ |
| 认证     | `/web/auth/*`         | 登录、注册         |
| 文章     | `/web/articles/*`     | 文章列表、详情     |
| 分类     | `/web/categories/*`   | 分类列表           |
| 标签     | `/web/tags/*`         | 标签列表           |
| 评论     | `/web/comments/*`     | 评论列表、发表评论 |
| 导航     | `/web/navigation/*`   | 导航数据           |
| 作者信息 | `/web/site-content/*` | 公告、技能等信息   |
| 统计     | `/web/stats/*`        | 网站统计数据       |

## 🔧 开发规范

### 命名规范

- **Entity**: 与数据库表对应，如 `User.java`
- **Mapper**: 数据访问接口，如 `UserMapper.java`
- **Service**: 业务接口，如 `UserService.java`
- **ServiceImpl**: 业务实现，如 `UserServiceImpl.java`
- **Controller**: 控制器，如 `UserController.java`
- **DTO**: 数据传输对象，如 `UserLoginDTO.java`
- **VO**: 视图对象，如 `UserVO.java`

### 代码风格

- 使用 Lombok 简化代码
- Controller 只处理请求响应
- 业务逻辑放在 Service 层
- 统一返回格式 (Result 类)
- 统一异常处理 (GlobalExceptionHandler)

### 注释规范

- 使用 Javadoc 注释类和方法
- 复杂逻辑添加行内注释
- Controller 方法使用 Swagger 注解

## 🐳 Docker 部署

### 构建镜像

```bash
docker build -t syne-blog-server .
```

### 运行容器

```bash
docker run -d \
  --name syne-blog-server \
  -p 8080:8080 \
  -e DB_HOST=your_db_host \
  -e DB_PORT=5432 \
  -e DB_NAME=syne_blog \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=your_password \
  -e JWT_SECRET=your_jwt_secret \
  syne-blog-server
```

## 📄 许可证

MIT License

## 👨‍💻 作者

DutyZero / Syne
