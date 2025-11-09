# Syne Blog - 后端服务

基于 Spring Boot 3.2 + MyBatis-Plus + PostgreSQL 的博客系统后端。

## 技术栈

- **Spring Boot 3.2.0** - 核心框架
- **MyBatis-Plus 3.5.5** - 持久层框架
- **PostgreSQL** - 数据库
- **Spring Security** - 安全框架
- **JWT** - 认证方案
- **Lombok** - 代码简化
- **Hutool** - 工具类库

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- PostgreSQL 14+

### 2. 数据库准备

```bash
# 创建数据库
psql -U postgres
CREATE DATABASE syne_blog;

# 导入表结构
psql -U postgres -d syne_blog -f ../web/database/schema-postgres.sql
```

### 3. 配置文件（重要 ⚠️）

**推荐方式：使用本地配置文件（密码不会提交到 Git）**

```bash
# 1. 复制配置模板
cd src/main/resources
cp application-dev-local.yml.example application-dev-local.yml

# 2. 编辑 application-dev-local.yml
# 修改 password 字段为你的实际数据库密码
# 此文件已在 .gitignore 中，不会被提交
```

**或者使用环境变量：**

```bash
# Windows PowerShell
$env:DB_PASSWORD="your_password"

# IDEA: Run → Edit Configurations → Environment variables
# 添加: DB_PASSWORD=your_password
```

### 4. 安装依赖

```bash
mvn clean install
```

### 5. 启动项目

```bash
mvn spring-boot:run
```

或使用 IDE 运行 `ServerApplication.java`

### 6. 验证启动

访问: http://localhost:8080/api

## 项目结构

```
server/
├── src/
│   ├── main/
│   │   ├── java/com/syne/server/
│   │   │   ├── config/          # 配置类
│   │   │   ├── controller/      # 控制器
│   │   │   ├── service/         # 业务层
│   │   │   ├── mapper/          # 数据访问层
│   │   │   ├── entity/          # 实体类
│   │   │   ├── dto/             # 数据传输对象
│   │   │   ├── vo/              # 视图对象
│   │   │   ├── utils/           # 工具类
│   │   │   ├── common/          # 通用类
│   │   │   └── ServerApplication.java
│   │   └── resources/
│   │       ├── mapper/          # MyBatis XML
│   │       ├── application.yml  # 主配置
│   │       └── application-dev.yml  # 开发配置
│   └── test/
├── pom.xml
└── README.md
```

## 开发规范

### 命名规范

- **Entity**: 与数据库表对应，如 `User.java`
- **Mapper**: 数据访问接口，如 `UserMapper.java`
- **Service**: 业务接口，如 `IUserService.java`
- **ServiceImpl**: 业务实现，如 `UserServiceImpl.java`
- **Controller**: 控制器，如 `UserController.java`
- **DTO**: 数据传输对象，如 `UserLoginDTO.java`
- **VO**: 视图对象，如 `UserVO.java`

### 代码风格

- 使用 Lombok 简化代码
- Controller 只处理请求响应
- 业务逻辑放在 Service 层
- 统一返回格式（待实现 Result 类）
- 统一异常处理（待实现 GlobalExceptionHandler）

## API 接口

### 用户相关
- POST `/api/auth/register` - 用户注册
- POST `/api/auth/login` - 用户登录
- GET `/api/auth/info` - 获取用户信息

### 文章相关
- GET `/api/articles` - 获取文章列表
- GET `/api/articles/{id}` - 获取文章详情
- POST `/api/articles` - 创建文章
- PUT `/api/articles/{id}` - 更新文章
- DELETE `/api/articles/{id}` - 删除文章

*详细 API 文档待添加 Swagger*

## 依赖说明

详见 [DEPENDENCIES.md](./DEPENDENCIES.md)

## 常见问题

### 1. 数据库连接失败
- 检查 PostgreSQL 是否启动
- 确认数据库名、用户名、密码是否正确
- 检查端口是否为 5432

### 2. Maven 依赖下载慢
配置国内镜像源（阿里云）：
```xml
<mirror>
  <id>aliyun</id>
  <mirrorOf>central</mirrorOf>
  <url>https://maven.aliyun.com/repository/public</url>
</mirror>
```

### 3. Lombok 不生效
- IDEA: 安装 Lombok 插件并启用注解处理
- Eclipse: 安装 Lombok 并重启

## 下一步计划

- [ ] 完善实体类和 Mapper
- [ ] 实现用户认证和授权
- [ ] 实现文章 CRUD
- [ ] 添加统一响应格式
- [ ] 添加全局异常处理
- [ ] 集成 Swagger API 文档
- [ ] 添加 Redis 缓存
- [ ] 添加单元测试

## License

MIT
