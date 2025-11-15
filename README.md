# Syne Blog

一个现代化的全栈博客系统，前后端分离架构。

## 📦 项目结构

```
syne-blog/
├── server/          # 后端服务 (Spring Boot)
│   ├── src/
│   ├── pom.xml
│   └── README.md
├── web/             # 前端应用 (Vue 3 + TypeScript)
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
- **Lombok** - 代码简化
- **Hutool** - 工具类库

### 前端 (Web)
- **Vue 3** - 渐进式 JavaScript 框架
- **TypeScript** - 类型安全
- **Vite** - 构建工具
- **Element Plus** - UI 组件库
- **TailwindCSS** - CSS 框架
- **Pinia** - 状态管理
- **Vue Router** - 路由管理
- **Axios** - HTTP 客户端

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

### 4. 启动前端

```bash
cd web

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端应用将运行在: http://localhost:3000

## 📖 详细文档

- [后端开发文档](./server/README.md)
- [前端开发文档](./web/README.md)
- 后端依赖说明（待补充）
- [数据库结构与初始化脚本](./server/src/main/resources/sql/schema-postgres.sql)

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
- **注释**: 重要逻辑必须添加注释

## 📂 目录说明

### 后端目录结构

```
server/src/main/java/com/syne/server/
├── config/          # 配置类
├── controller/      # 控制器
├── service/         # 业务层
│   └── impl/        # 业务实现
├── mapper/          # 数据访问层
├── entity/          # 实体类
├── dto/             # 数据传输对象
├── vo/              # 视图对象
├── common/          # 通用类
├── utils/           # 工具类
└── exception/       # 自定义异常
```

### 前端目录结构

```
web/src/
├── api/             # API 接口
├── assets/          # 静态资源
├── components/      # 通用组件
├── views/           # 页面组件
├── router/          # 路由配置
├── store/           # 状态管理
├── utils/           # 工具函数
├── types/           # TypeScript 类型
└── App.vue          # 根组件
```

## 🔧 开发工具推荐

### 后端开发
- **IDE**: IntelliJ IDEA
- **插件**: Lombok, MyBatisX

### 前端开发
- **IDE**: VS Code / Cursor
- **插件**: Volar, ESLint, Prettier, Tailwind CSS IntelliSense

## 📝 API 文档

API 文档地址：http://localhost:8080/api/doc.html (待配置 Swagger)

## 🐛 问题反馈

如有问题，请在 [Issues](https://github.com/aixcrimson/syne-blog/issues) 中提出。

## 📄 开源协议

MIT License

## 👥 贡献者

- [@Syne](https://github.com/aixcrimson)

## 🎯 开发路线图

- [x] 项目初始化
- [x] 数据库设计
- [x] 后端基础架构
- [x] 前端基础架构
- [ ] 用户认证系统
- [ ] 文章 CRUD
- [ ] 评论系统
- [ ] 文件上传
- [ ] 搜索功能
- [ ] 后台管理
- [ ] 部署上线

## 📞 联系方式

- Email: hitori150221@outlook.com
- GitHub: [@aixcrimson](https://github.com/aixcrimson)
