-- ============================================
-- 博客系统数据库设计 (PostgreSQL 优化版本)
-- 版本: 1.1
-- 创建日期: 2025-11-10
-- 数据库引擎: PostgreSQL 14+
-- 字符集: UTF8
-- 字段命名: create_time, update_time, deleted
-- ============================================

-- ============================================
-- 1. 用户表 (users)
-- 存储博客作者和访客的基本信息
-- ============================================
DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  email VARCHAR(100) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  avatar VARCHAR(500) DEFAULT NULL,
  bio TEXT DEFAULT NULL,
  github VARCHAR(255) DEFAULT NULL,
  bilibili VARCHAR(255) DEFAULT NULL,
  role SMALLINT NOT NULL DEFAULT 2 CHECK (role IN (1, 2)),
  status SMALLINT NOT NULL DEFAULT 1 CHECK (status IN (0, 1)),
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0 CHECK (deleted IN (0, 1))
);

-- 添加注释
COMMENT ON TABLE users IS '用户表';
COMMENT ON COLUMN users.id IS '用户ID，主键';
COMMENT ON COLUMN users.username IS '用户名，唯一';
COMMENT ON COLUMN users.email IS '邮箱地址，唯一';
COMMENT ON COLUMN users.password_hash IS '密码哈希值';
COMMENT ON COLUMN users.role IS '用户角色: 1-管理员, 2-普通用户';
COMMENT ON COLUMN users.status IS '账号状态: 1-正常, 0-禁用';
COMMENT ON COLUMN users.create_time IS '创建时间';
COMMENT ON COLUMN users.update_time IS '更新时间';
COMMENT ON COLUMN users.deleted IS '逻辑删除: 0-未删除, 1-已删除';

-- 创建索引
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_create_time ON users(create_time);
CREATE INDEX idx_users_deleted ON users(deleted);

-- ============================================
-- 2. 分类表 (categories)
-- 文章分类管理
-- ============================================
DROP TABLE IF EXISTS categories CASCADE;
CREATE TABLE categories (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(50) NOT NULL UNIQUE,
  slug VARCHAR(50) NOT NULL UNIQUE,
  description TEXT DEFAULT NULL,
  icon VARCHAR(255) DEFAULT NULL,
  sort_order INTEGER NOT NULL DEFAULT 0,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0 CHECK (deleted IN (0, 1))
);

COMMENT ON TABLE categories IS '文章分类表';
COMMENT ON COLUMN categories.sort_order IS '排序权重，数字越大越靠前';
COMMENT ON COLUMN categories.create_time IS '创建时间';
COMMENT ON COLUMN categories.update_time IS '更新时间';
COMMENT ON COLUMN categories.deleted IS '逻辑删除: 0-未删除, 1-已删除';

CREATE INDEX idx_categories_sort_order ON categories(sort_order);
CREATE INDEX idx_categories_deleted ON categories(deleted);

-- ============================================
-- 3. 标签表 (tags)
-- 文章标签管理
-- ============================================
DROP TABLE IF EXISTS tags CASCADE;
CREATE TABLE tags (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(50) NOT NULL UNIQUE,
  slug VARCHAR(50) NOT NULL UNIQUE,
  color VARCHAR(20) DEFAULT '#409EFF',
  usage_count INTEGER NOT NULL DEFAULT 0 CHECK (usage_count >= 0),
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0 CHECK (deleted IN (0, 1))
);

COMMENT ON TABLE tags IS '标签表';
COMMENT ON COLUMN tags.usage_count IS '标签使用次数统计';
COMMENT ON COLUMN tags.create_time IS '创建时间';
COMMENT ON COLUMN tags.update_time IS '更新时间';
COMMENT ON COLUMN tags.deleted IS '逻辑删除: 0-未删除, 1-已删除';

CREATE INDEX idx_tags_usage_count ON tags(usage_count);
CREATE INDEX idx_tags_deleted ON tags(deleted);

-- ============================================
-- 4. 文章表 (articles)
-- 核心内容表，存储博客文章
-- ============================================
DROP TABLE IF EXISTS articles CASCADE;
CREATE TABLE articles (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  category_id BIGINT NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
  title VARCHAR(200) NOT NULL,
  summary TEXT NOT NULL,
  content TEXT NOT NULL,
  cover_image VARCHAR(500) DEFAULT NULL,
  views INTEGER NOT NULL DEFAULT 0 CHECK (views >= 0),
  likes INTEGER NOT NULL DEFAULT 0 CHECK (likes >= 0),
  favorites INTEGER NOT NULL DEFAULT 0 CHECK (favorites >= 0),
  comments_count INTEGER NOT NULL DEFAULT 0 CHECK (comments_count >= 0),
  status SMALLINT NOT NULL DEFAULT 2 CHECK (status IN (1, 2, 3)),
  is_top SMALLINT NOT NULL DEFAULT 0 CHECK (is_top IN (0, 1)),
  is_recommend SMALLINT NOT NULL DEFAULT 0 CHECK (is_recommend IN (0, 1)),
  published_at TIMESTAMP DEFAULT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0 CHECK (deleted IN (0, 1))
);

COMMENT ON TABLE articles IS '文章表';
COMMENT ON COLUMN articles.status IS '文章状态: 1-已发布, 2-草稿, 3-已下架';
COMMENT ON COLUMN articles.is_top IS '是否置顶: 0-否, 1-是';
COMMENT ON COLUMN articles.is_recommend IS '是否推荐: 0-否, 1-是';
COMMENT ON COLUMN articles.create_time IS '创建时间';
COMMENT ON COLUMN articles.update_time IS '更新时间';
COMMENT ON COLUMN articles.deleted IS '逻辑删除: 0-未删除, 1-已删除';

-- 创建索引
CREATE INDEX idx_articles_user_id ON articles(user_id);
CREATE INDEX idx_articles_category_id ON articles(category_id);
CREATE INDEX idx_articles_status ON articles(status);
CREATE INDEX idx_articles_is_top ON articles(is_top);
CREATE INDEX idx_articles_is_recommend ON articles(is_recommend);
CREATE INDEX idx_articles_published_at ON articles(published_at);
CREATE INDEX idx_articles_views ON articles(views);
CREATE INDEX idx_articles_likes ON articles(likes);
CREATE INDEX idx_articles_create_time ON articles(create_time);
CREATE INDEX idx_articles_deleted ON articles(deleted);

-- 创建全文搜索索引（PostgreSQL 方式）
-- 注意：使用 'simple' 配置，如需中文分词请安装 zhparser 扩展后改为 'chinese'
CREATE INDEX idx_articles_fulltext ON articles 
  USING gin(to_tsvector('simple', title || ' ' || summary || ' ' || content));

-- ============================================
-- 5. 文章标签关联表 (article_tags)
-- 多对多关系：一篇文章可以有多个标签
-- ============================================
DROP TABLE IF EXISTS article_tags CASCADE;
CREATE TABLE article_tags (
  id BIGSERIAL PRIMARY KEY,
  article_id BIGINT NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
  tag_id BIGINT NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(article_id, tag_id)
);

COMMENT ON TABLE article_tags IS '文章标签关联表';
COMMENT ON COLUMN article_tags.create_time IS '创建时间';

CREATE INDEX idx_article_tags_tag_id ON article_tags(tag_id);

-- ============================================
-- 6. 文章点赞表 (article_likes)
-- 记录用户对文章的点赞行为
-- ============================================
DROP TABLE IF EXISTS article_likes CASCADE;
CREATE TABLE article_likes (
  id BIGSERIAL PRIMARY KEY,
  article_id BIGINT NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
  user_id BIGINT DEFAULT NULL REFERENCES users(id) ON DELETE CASCADE,
  ip_address VARCHAR(45) DEFAULT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(article_id, user_id)
);

COMMENT ON TABLE article_likes IS '文章点赞表';
COMMENT ON COLUMN article_likes.user_id IS '用户ID，游客为NULL';
COMMENT ON COLUMN article_likes.create_time IS '创建时间';

CREATE INDEX idx_article_likes_user_id ON article_likes(user_id);
CREATE INDEX idx_article_likes_create_time ON article_likes(create_time);

-- ============================================
-- 7. 文章收藏表 (article_favorites)
-- 记录用户对文章的收藏行为
-- ============================================
DROP TABLE IF EXISTS article_favorites CASCADE;
CREATE TABLE article_favorites (
  id BIGSERIAL PRIMARY KEY,
  article_id BIGINT NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(article_id, user_id)
);

COMMENT ON TABLE article_favorites IS '文章收藏表';
COMMENT ON COLUMN article_favorites.create_time IS '创建时间';

CREATE INDEX idx_article_favorites_user_id ON article_favorites(user_id);
CREATE INDEX idx_article_favorites_create_time ON article_favorites(create_time);

-- ============================================
-- 8. 评论表 (comments)
-- 文章评论，支持多级回复
-- ============================================
DROP TABLE IF EXISTS comments CASCADE;
CREATE TABLE comments (
  id BIGSERIAL PRIMARY KEY,
  article_id BIGINT NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
  user_id BIGINT DEFAULT NULL REFERENCES users(id) ON DELETE SET NULL,
  parent_id BIGINT DEFAULT NULL REFERENCES comments(id) ON DELETE CASCADE,
  content TEXT NOT NULL,
  ip_address VARCHAR(45) DEFAULT NULL,
  status SMALLINT NOT NULL DEFAULT 1 CHECK (status IN (1, 2, 3)),
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0 CHECK (deleted IN (0, 1))
);

COMMENT ON TABLE comments IS '评论表';
COMMENT ON COLUMN comments.status IS '评论状态: 1-正常, 2-待审核, 3-已删除';
COMMENT ON COLUMN comments.create_time IS '创建时间';
COMMENT ON COLUMN comments.update_time IS '更新时间';
COMMENT ON COLUMN comments.deleted IS '逻辑删除: 0-未删除, 1-已删除';

CREATE INDEX idx_comments_article_id ON comments(article_id);
CREATE INDEX idx_comments_user_id ON comments(user_id);
CREATE INDEX idx_comments_parent_id ON comments(parent_id);
CREATE INDEX idx_comments_status ON comments(status);
CREATE INDEX idx_comments_create_time ON comments(create_time);
CREATE INDEX idx_comments_deleted ON comments(deleted);

-- ============================================
-- 9. 导航站点分类表 (navigation_categories)
-- 导航站点的分类管理
-- ============================================
DROP TABLE IF EXISTS navigation_categories CASCADE;
CREATE TABLE navigation_categories (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(50) NOT NULL UNIQUE,
  icon VARCHAR(255) DEFAULT NULL,
  sort_order INTEGER NOT NULL DEFAULT 0,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0 CHECK (deleted IN (0, 1))
);

COMMENT ON TABLE navigation_categories IS '导航站点分类表';
COMMENT ON COLUMN navigation_categories.create_time IS '创建时间';
COMMENT ON COLUMN navigation_categories.update_time IS '更新时间';
COMMENT ON COLUMN navigation_categories.deleted IS '逻辑删除: 0-未删除, 1-已删除';

CREATE INDEX idx_navigation_categories_sort_order ON navigation_categories(sort_order);
CREATE INDEX idx_navigation_categories_deleted ON navigation_categories(deleted);

-- ============================================
-- 10. 导航站点表 (navigation_sites)
-- 存储常用网站导航信息
-- ============================================
DROP TABLE IF EXISTS navigation_sites CASCADE;
CREATE TABLE navigation_sites (
  id BIGSERIAL PRIMARY KEY,
  category_id BIGINT NOT NULL REFERENCES navigation_categories(id) ON DELETE CASCADE,
  name VARCHAR(100) NOT NULL,
  description TEXT DEFAULT NULL,
  url VARCHAR(500) NOT NULL,
  icon VARCHAR(500) DEFAULT NULL,
  sort_order INTEGER NOT NULL DEFAULT 0,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0 CHECK (deleted IN (0, 1))
);

COMMENT ON TABLE navigation_sites IS '导航站点表';
COMMENT ON COLUMN navigation_sites.create_time IS '创建时间';
COMMENT ON COLUMN navigation_sites.update_time IS '更新时间';
COMMENT ON COLUMN navigation_sites.deleted IS '逻辑删除: 0-未删除, 1-已删除';

CREATE INDEX idx_navigation_sites_category_id ON navigation_sites(category_id);
CREATE INDEX idx_navigation_sites_sort_order ON navigation_sites(sort_order);
CREATE INDEX idx_navigation_sites_deleted ON navigation_sites(deleted);

-- ============================================
-- 触发器函数：自动更新 update_time 字段
-- ============================================
CREATE OR REPLACE FUNCTION update_update_time_column()
RETURNS TRIGGER AS $$
BEGIN
  NEW.update_time = CURRENT_TIMESTAMP;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 为所有需要的表添加触发器
CREATE TRIGGER trigger_users_update_time BEFORE UPDATE ON users
  FOR EACH ROW EXECUTE FUNCTION update_update_time_column();

CREATE TRIGGER trigger_categories_update_time BEFORE UPDATE ON categories
  FOR EACH ROW EXECUTE FUNCTION update_update_time_column();

CREATE TRIGGER trigger_tags_update_time BEFORE UPDATE ON tags
  FOR EACH ROW EXECUTE FUNCTION update_update_time_column();

CREATE TRIGGER trigger_articles_update_time BEFORE UPDATE ON articles
  FOR EACH ROW EXECUTE FUNCTION update_update_time_column();

CREATE TRIGGER trigger_comments_update_time BEFORE UPDATE ON comments
  FOR EACH ROW EXECUTE FUNCTION update_update_time_column();

CREATE TRIGGER trigger_navigation_categories_update_time BEFORE UPDATE ON navigation_categories
  FOR EACH ROW EXECUTE FUNCTION update_update_time_column();

CREATE TRIGGER trigger_navigation_sites_update_time BEFORE UPDATE ON navigation_sites
  FOR EACH ROW EXECUTE FUNCTION update_update_time_column();

-- ============================================
-- 触发器函数：自动更新标签使用次数
-- ============================================
CREATE OR REPLACE FUNCTION update_tag_usage_count_on_insert()
RETURNS TRIGGER AS $$
BEGIN
  UPDATE tags SET usage_count = usage_count + 1 WHERE id = NEW.tag_id;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION update_tag_usage_count_on_delete()
RETURNS TRIGGER AS $$
BEGIN
  UPDATE tags SET usage_count = usage_count - 1 WHERE id = OLD.tag_id;
  RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- 添加触发器
CREATE TRIGGER trigger_article_tags_insert AFTER INSERT ON article_tags
  FOR EACH ROW EXECUTE FUNCTION update_tag_usage_count_on_insert();

CREATE TRIGGER trigger_article_tags_delete AFTER DELETE ON article_tags
  FOR EACH ROW EXECUTE FUNCTION update_tag_usage_count_on_delete();

-- ============================================
-- 初始化数据
-- ============================================

-- 插入默认管理员用户（密码: admin123）
INSERT INTO users (username, email, password_hash, avatar, bio, github, role, status) 
VALUES ('Syne', 'hitori150221@outlook.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z3rkKKvCbe9c4fO9rQg7UbiC', '/avatars/admin.jpg', '热爱技术，专注于软件开发', 'https://github.com/aixcrimson', 1, 1);

-- 插入默认分类
INSERT INTO categories (name, slug, description, sort_order) VALUES
('前端开发', 'frontend', 'Web前端技术分享', 100),
('后端开发', 'backend', '服务端技术探讨', 90),
('数据库', 'database', '数据库设计与优化', 80),
('DevOps', 'devops', '运维与部署相关', 70),
('算法', 'algorithm', '数据结构与算法', 60),
('随笔', 'essay', '生活随笔', 50);

-- 插入默认标签
INSERT INTO tags (name, slug, color) VALUES
('Vue', 'vue', '#42b883'),
('React', 'react', '#61dafb'),
('TypeScript', 'typescript', '#3178c6'),
('Node.js', 'nodejs', '#339933'),
('PostgreSQL', 'postgresql', '#336791'),
('Redis', 'redis', '#DC382D'),
('Docker', 'docker', '#2496ED'),
('算法', 'algorithm', '#FF6B6B'),
('Spring Boot', 'spring-boot', '#6DB33F'),
('MyBatis', 'mybatis', '#E34F26');

-- 插入测试文章
INSERT INTO articles (user_id, category_id, title, summary, content, cover_image, status, is_top, is_recommend, published_at) VALUES
(1, 1, 'Vue 3 Composition API 深度解析', '本文详细介绍了 Vue 3 Composition API 的核心概念和使用方法', '# Vue 3 Composition API 深度解析\n\nVue 3 引入了 Composition API，这是一种全新的组件逻辑组织方式...', '/covers/vue3.jpg', 1, 1, 1, CURRENT_TIMESTAMP),
(1, 2, 'Spring Boot 微服务架构实践', '从零开始构建 Spring Boot 微服务应用', '# Spring Boot 微服务架构实践\n\n本文将介绍如何使用 Spring Boot 构建微服务架构...', '/covers/springboot.jpg', 1, 0, 1, CURRENT_TIMESTAMP),
(1, 3, 'PostgreSQL 性能优化技巧', '深入探讨 PostgreSQL 数据库的性能优化方法', '# PostgreSQL 性能优化技巧\n\n本文总结了 PostgreSQL 性能优化的核心技巧...', '/covers/postgresql.jpg', 1, 0, 1, CURRENT_TIMESTAMP),
(1, 4, 'Docker 容器化部署实战', '使用 Docker 容器化部署应用的完整指南', '# Docker 容器化部署实战\n\n容器化已成为现代应用部署的标准方式...', '/covers/docker.jpg', 1, 0, 0, CURRENT_TIMESTAMP),
(1, 5, '常用算法与数据结构总结', '整理常用的算法和数据结构知识点', '# 常用算法与数据结构总结\n\n本文总结了常见的算法和数据结构...', '/covers/algorithm.jpg', 2, 0, 0, NULL);

-- 插入文章标签关联
INSERT INTO article_tags (article_id, tag_id) VALUES
(1, 1), (1, 3),  -- Vue 3 文章关联 Vue 和 TypeScript
(2, 9), (2, 4),  -- Spring Boot 文章关联 Spring Boot 和 Node.js
(3, 5),          -- PostgreSQL 文章关联 PostgreSQL
(4, 7),          -- Docker 文章关联 Docker
(5, 8);          -- 算法文章关联算法

-- 插入导航分类
INSERT INTO navigation_categories (name, icon, sort_order) VALUES
('开发工具', 'tools', 100),
('学习资源', 'book', 90),
('技术社区', 'community', 80),
('设计资源', 'design', 70);

-- 插入导航站点
INSERT INTO navigation_sites (category_id, name, description, url, icon, sort_order) VALUES
(1, 'VS Code', '微软开发的代码编辑器', 'https://code.visualstudio.com/', '/icons/vscode.png', 100),
(1, 'JetBrains', '专业的开发工具套件', 'https://www.jetbrains.com/', '/icons/jetbrains.png', 90),
(2, 'MDN', 'Web 开发权威文档', 'https://developer.mozilla.org/', '/icons/mdn.png', 100),
(2, '菜鸟教程', '编程学习入门网站', 'https://www.runoob.com/', '/icons/runoob.png', 90),
(3, 'GitHub', '全球最大的代码托管平台', 'https://github.com/', '/icons/github.png', 100),
(3, 'Stack Overflow', '程序员问答社区', 'https://stackoverflow.com/', '/icons/stackoverflow.png', 90);

-- ============================================
-- 常用查询示例
-- ============================================

-- 1. 查询文章列表（包含作者、分类、标签）
/*
SELECT 
  a.id, a.title, a.summary, a.cover_image, a.views, a.likes, 
  a.published_at, a.create_time, a.is_top, a.is_recommend,
  u.username AS author_name, u.avatar AS author_avatar,
  c.name AS category_name,
  string_agg(t.name, ',') AS tags
FROM articles a
LEFT JOIN users u ON a.user_id = u.id
LEFT JOIN categories c ON a.category_id = c.id
LEFT JOIN article_tags at ON a.id = at.article_id
LEFT JOIN tags t ON at.tag_id = t.id
WHERE a.status = 1 AND a.deleted = 0
GROUP BY a.id, u.username, u.avatar, c.name
ORDER BY a.is_top DESC, a.published_at DESC
LIMIT 10;
*/

-- 2. 全文搜索示例
/*
SELECT id, title, summary
FROM articles
WHERE to_tsvector('simple', title || ' ' || summary || ' ' || content) 
      @@ plainto_tsquery('simple', '前端 开发')
  AND deleted = 0
ORDER BY ts_rank(
  to_tsvector('simple', title || ' ' || summary || ' ' || content),
  plainto_tsquery('simple', '前端 开发')
) DESC
LIMIT 10;
*/

-- 3. 查询热门文章
/*
SELECT id, title, summary, views, likes, published_at
FROM articles
WHERE status = 1 AND deleted = 0
ORDER BY views DESC
LIMIT 10;
*/

-- 4. 查询用户的文章列表（包含草稿）
/*
SELECT id, title, summary, status, views, likes, create_time
FROM articles
WHERE user_id = 1 AND deleted = 0
ORDER BY create_time DESC;
*/

-- 注意：如需中文分词支持，请执行以下步骤：
-- 1. 安装 zhparser 扩展
-- 2. CREATE EXTENSION zhparser;
-- 3. CREATE TEXT SEARCH CONFIGURATION chinese (PARSER = zhparser);
-- 4. 将上述 SQL 中的 'simple' 改为 'chinese'
