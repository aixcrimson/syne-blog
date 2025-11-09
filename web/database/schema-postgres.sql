-- ============================================
-- 博客系统数据库设计 (PostgreSQL 版本)
-- 版本: 1.0
-- 创建日期: 2025-11-06
-- 数据库引擎: PostgreSQL 14+
-- 字符集: UTF8
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
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 添加注释
COMMENT ON TABLE users IS '用户表';
COMMENT ON COLUMN users.id IS '用户ID，主键';
COMMENT ON COLUMN users.username IS '用户名，唯一';
COMMENT ON COLUMN users.email IS '邮箱地址，唯一';
COMMENT ON COLUMN users.password_hash IS '密码哈希值';
COMMENT ON COLUMN users.role IS '用户角色: 1-管理员, 2-普通用户';
COMMENT ON COLUMN users.status IS '账号状态: 1-正常, 0-禁用';

-- 创建索引
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_created_at ON users(created_at);

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
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE categories IS '文章分类表';
COMMENT ON COLUMN categories.sort_order IS '排序权重，数字越大越靠前';

CREATE INDEX idx_categories_sort_order ON categories(sort_order);

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
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE tags IS '标签表';
COMMENT ON COLUMN tags.usage_count IS '标签使用次数统计';

CREATE INDEX idx_tags_usage_count ON tags(usage_count);

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
  published_at TIMESTAMP DEFAULT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE articles IS '文章表';
COMMENT ON COLUMN articles.status IS '文章状态: 1-已发布, 2-草稿, 3-已下架';

-- 创建索引
CREATE INDEX idx_articles_user_id ON articles(user_id);
CREATE INDEX idx_articles_category_id ON articles(category_id);
CREATE INDEX idx_articles_status ON articles(status);
CREATE INDEX idx_articles_published_at ON articles(published_at);
CREATE INDEX idx_articles_views ON articles(views);
CREATE INDEX idx_articles_likes ON articles(likes);
CREATE INDEX idx_articles_created_at ON articles(created_at);

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
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(article_id, tag_id)
);

COMMENT ON TABLE article_tags IS '文章标签关联表';

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
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(article_id, user_id)
);

COMMENT ON TABLE article_likes IS '文章点赞表';
COMMENT ON COLUMN article_likes.user_id IS '用户ID，游客为NULL';

CREATE INDEX idx_article_likes_user_id ON article_likes(user_id);
CREATE INDEX idx_article_likes_created_at ON article_likes(created_at);

-- ============================================
-- 7. 文章收藏表 (article_favorites)
-- 记录用户对文章的收藏行为
-- ============================================
DROP TABLE IF EXISTS article_favorites CASCADE;
CREATE TABLE article_favorites (
  id BIGSERIAL PRIMARY KEY,
  article_id BIGINT NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(article_id, user_id)
);

COMMENT ON TABLE article_favorites IS '文章收藏表';

CREATE INDEX idx_article_favorites_user_id ON article_favorites(user_id);
CREATE INDEX idx_article_favorites_created_at ON article_favorites(created_at);

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
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE comments IS '评论表';
COMMENT ON COLUMN comments.status IS '评论状态: 1-正常, 2-待审核, 3-已删除';

CREATE INDEX idx_comments_article_id ON comments(article_id);
CREATE INDEX idx_comments_user_id ON comments(user_id);
CREATE INDEX idx_comments_parent_id ON comments(parent_id);
CREATE INDEX idx_comments_status ON comments(status);
CREATE INDEX idx_comments_created_at ON comments(created_at);

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
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE navigation_categories IS '导航站点分类表';

CREATE INDEX idx_navigation_categories_sort_order ON navigation_categories(sort_order);

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
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE navigation_sites IS '导航站点表';

CREATE INDEX idx_navigation_sites_category_id ON navigation_sites(category_id);
CREATE INDEX idx_navigation_sites_sort_order ON navigation_sites(sort_order);

-- ============================================
-- 触发器函数：自动更新 updated_at 字段
-- ============================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = CURRENT_TIMESTAMP;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 为所有需要的表添加触发器
CREATE TRIGGER trigger_users_updated_at BEFORE UPDATE ON users
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_categories_updated_at BEFORE UPDATE ON categories
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_tags_updated_at BEFORE UPDATE ON tags
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_articles_updated_at BEFORE UPDATE ON articles
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_comments_updated_at BEFORE UPDATE ON comments
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_navigation_categories_updated_at BEFORE UPDATE ON navigation_categories
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_navigation_sites_updated_at BEFORE UPDATE ON navigation_sites
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

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

-- 插入默认管理员用户
INSERT INTO users (username, email, password_hash, avatar, bio, github, role, status) 
VALUES ('Syne', 'hitori150221@outlook.com', '$2a$10$placeholder', '/avatars/admin.jpg', '热爱技术，专注于软件开发', 'https://github.com/aixcrimson', 1, 1);

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
('MySQL', 'mysql', '#4479A1'),
('Redis', 'redis', '#DC382D'),
('Docker', 'docker', '#2496ED'),
('算法', 'algorithm', '#FF6B6B');

-- 插入导航分类
INSERT INTO navigation_categories (name, icon, sort_order) VALUES
('开发工具', 'tools', 100),
('学习资源', 'book', 90),
('技术社区', 'community', 80),
('设计资源', 'design', 70);

-- ============================================
-- 常用查询示例
-- ============================================

-- 1. 查询文章列表（包含作者、分类、标签）
/*
SELECT 
  a.id, a.title, a.summary, a.cover_image, a.views, a.likes, 
  a.published_at, a.created_at,
  u.username AS author_name, u.avatar AS author_avatar,
  c.name AS category_name,
  string_agg(t.name, ',') AS tags
FROM articles a
LEFT JOIN users u ON a.user_id = u.id
LEFT JOIN categories c ON a.category_id = c.id
LEFT JOIN article_tags at ON a.id = at.article_id
LEFT JOIN tags t ON at.tag_id = t.id
WHERE a.status = 1
GROUP BY a.id, u.username, u.avatar, c.name
ORDER BY a.published_at DESC
LIMIT 10;
*/

-- 2. 全文搜索示例
/*
SELECT id, title, summary
FROM articles
WHERE to_tsvector('simple', title || ' ' || summary || ' ' || content) 
      @@ plainto_tsquery('simple', '前端 开发')
ORDER BY ts_rank(
  to_tsvector('simple', title || ' ' || summary || ' ' || content),
  plainto_tsquery('simple', '前端 开发')
) DESC
LIMIT 10;
*/

-- 注意：如需中文分词支持，请执行以下步骤：
-- 1. 安装 zhparser 扩展
-- 2. CREATE EXTENSION zhparser;
-- 3. CREATE TEXT SEARCH CONFIGURATION chinese (PARSER = zhparser);
-- 4. 将上述 SQL 中的 'simple' 改回 'chinese'

-- 3. 查询热门文章
/*
SELECT id, title, summary, views, likes, published_at
FROM articles
WHERE status = 1
ORDER BY views DESC
LIMIT 10;
*/

