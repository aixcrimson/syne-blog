-- ============================================
-- 博客系统数据库设计
-- 版本: 1.0
-- 创建日期: 2025-11-03
-- 数据库引擎: MySQL 8.0+
-- 字符集: utf8mb4
-- ============================================

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 1. 用户表 (users)
-- 存储博客作者和访客的基本信息
-- ============================================
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
                         `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键',
                         `username` VARCHAR(50) NOT NULL COMMENT '用户名，唯一',
                         `email` VARCHAR(100) NOT NULL COMMENT '邮箱地址，唯一',
                         `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希值',
                         `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
                         `bio` TEXT DEFAULT NULL COMMENT '个人简介',
                         `github` VARCHAR(255) DEFAULT NULL COMMENT 'GitHub主页链接',
                         `bilibili` VARCHAR(255) DEFAULT NULL COMMENT 'Bilibili主页链接',
                         `role` TINYINT UNSIGNED NOT NULL DEFAULT 2 COMMENT '用户角色: 1-管理员, 2-普通用户',
                         `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '账号状态: 1-正常, 0-禁用',
                         `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `uk_username` (`username`),
                         UNIQUE KEY `uk_email` (`email`),
                         KEY `idx_role` (`role`),
                         KEY `idx_status` (`status`),
                         KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 2. 分类表 (categories)
-- 文章分类管理
-- ============================================
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories` (
                              `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID，主键',
                              `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
                              `slug` VARCHAR(50) NOT NULL COMMENT 'URL友好的分类标识',
                              `description` TEXT DEFAULT NULL COMMENT '分类描述',
                              `icon` VARCHAR(255) DEFAULT NULL COMMENT '分类图标',
                              `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序权重，数字越大越靠前',
                              `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_name` (`name`),
                              UNIQUE KEY `uk_slug` (`slug`),
                              KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章分类表';

-- ============================================
-- 3. 标签表 (tags)
-- 文章标签管理
-- ============================================
DROP TABLE IF EXISTS `tags`;
CREATE TABLE `tags` (
                        `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '标签ID，主键',
                        `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
                        `slug` VARCHAR(50) NOT NULL COMMENT 'URL友好的标签标识',
                        `color` VARCHAR(20) DEFAULT '#409EFF' COMMENT '标签颜色，用于前端显示',
                        `usage_count` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '标签使用次数统计',
                        `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_name` (`name`),
                        UNIQUE KEY `uk_slug` (`slug`),
                        KEY `idx_usage_count` (`usage_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- ============================================
-- 4. 文章表 (articles)
-- 核心内容表，存储博客文章
-- ============================================
DROP TABLE IF EXISTS `articles`;
CREATE TABLE `articles` (
                            `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '文章ID，主键',
                            `user_id` BIGINT UNSIGNED NOT NULL COMMENT '作者ID，外键关联users表',
                            `category_id` BIGINT UNSIGNED NOT NULL COMMENT '分类ID，外键关联categories表',
                            `title` VARCHAR(200) NOT NULL COMMENT '文章标题',
                            `summary` TEXT NOT NULL COMMENT '文章摘要',
                            `content` LONGTEXT NOT NULL COMMENT '文章正文，Markdown格式',
                            `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
                            `views` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '浏览次数',
                            `likes` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '点赞次数',
                            `favorites` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '收藏次数',
                            `comments_count` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '评论数量',
                            `status` TINYINT UNSIGNED NOT NULL DEFAULT 2 COMMENT '文章状态: 1-已发布, 2-草稿, 3-已下架',
                            `published_time` DATETIME DEFAULT NULL COMMENT '发布时间，NULL 表示未发布',
                            `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`id`),
                            KEY `idx_user_id` (`user_id`),
                            KEY `idx_category_id` (`category_id`),
                            KEY `idx_status` (`status`),
                            KEY `idx_published_time` (`published_time`),
                            KEY `idx_views` (`views`),
                            KEY `idx_likes` (`likes`),
                            KEY `idx_created_at` (`created_at`),
                            FULLTEXT KEY `ft_title_content` (`title`, `summary`, `content`) WITH PARSER ngram,
                            CONSTRAINT `fk_articles_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
                            CONSTRAINT `fk_articles_category_id` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- ============================================
-- 5. 文章标签关联表 (article_tags)
-- 多对多关系：一篇文章可以有多个标签，一个标签可以属于多篇文章
-- ============================================
DROP TABLE IF EXISTS `article_tags`;
CREATE TABLE `article_tags` (
                                `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
                                `article_id` BIGINT UNSIGNED NOT NULL COMMENT '文章ID，外键关联articles表',
                                `tag_id` BIGINT UNSIGNED NOT NULL COMMENT '标签ID，外键关联tags表',
                                `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                PRIMARY KEY (`id`),
                                UNIQUE KEY `uk_article_tag` (`article_id`, `tag_id`),
                                KEY `idx_tag_id` (`tag_id`),
                                CONSTRAINT `fk_article_tags_article_id` FOREIGN KEY (`article_id`) REFERENCES `articles` (`id`) ON DELETE CASCADE,
                                CONSTRAINT `fk_article_tags_tag_id` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签关联表';

-- ============================================
-- 6. 文章点赞表 (article_likes)
-- 记录用户对文章的点赞行为
-- ============================================
DROP TABLE IF EXISTS `article_likes`;
CREATE TABLE `article_likes` (
                                 `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
                                 `article_id` BIGINT UNSIGNED NOT NULL COMMENT '文章ID，外键关联articles表',
                                 `user_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '用户ID，外键关联users表，游客为NULL',
                                 `ip_address` VARCHAR(45) DEFAULT NULL COMMENT 'IP地址，用于防刷',
                                 `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `uk_article_user` (`article_id`, `user_id`),
                                 KEY `idx_user_id` (`user_id`),
                                 KEY `idx_created_at` (`created_at`),
                                 CONSTRAINT `fk_article_likes_article_id` FOREIGN KEY (`article_id`) REFERENCES `articles` (`id`) ON DELETE CASCADE,
                                 CONSTRAINT `fk_article_likes_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章点赞表';

-- ============================================
-- 7. 文章收藏表 (article_favorites)
-- 记录用户对文章的收藏行为
-- ============================================
DROP TABLE IF EXISTS `article_favorites`;
CREATE TABLE `article_favorites` (
                                     `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `article_id` BIGINT UNSIGNED NOT NULL COMMENT '文章ID，外键关联articles表',
                                     `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID，外键关联users表',
                                     `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `uk_article_user` (`article_id`, `user_id`),
                                     KEY `idx_user_id` (`user_id`),
                                     KEY `idx_created_at` (`created_at`),
                                     CONSTRAINT `fk_article_favorites_article_id` FOREIGN KEY (`article_id`) REFERENCES `articles` (`id`) ON DELETE CASCADE,
                                     CONSTRAINT `fk_article_favorites_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章收藏表';

-- ============================================
-- 8. 评论表 (comments)
-- 文章评论，支持多级回复
-- ============================================
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments` (
                            `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '评论ID，主键',
                            `article_id` BIGINT UNSIGNED NOT NULL COMMENT '文章ID，外键关联articles表',
                            `user_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '用户ID，外键关联users表，游客为NULL',
                            `parent_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '父评论ID，用于多级回复',
                            `content` TEXT NOT NULL COMMENT '评论内容',
                            `ip_address` VARCHAR(45) DEFAULT NULL COMMENT 'IP地址',
                            `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '评论状态: 1-正常, 2-待审核, 3-已删除',
                            `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`id`),
                            KEY `idx_article_id` (`article_id`),
                            KEY `idx_user_id` (`user_id`),
                            KEY `idx_parent_id` (`parent_id`),
                            KEY `idx_status` (`status`),
                            KEY `idx_created_at` (`created_at`),
                            CONSTRAINT `fk_comments_article_id` FOREIGN KEY (`article_id`) REFERENCES `articles` (`id`) ON DELETE CASCADE,
                            CONSTRAINT `fk_comments_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL,
                            CONSTRAINT `fk_comments_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `comments` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- ============================================
-- 9. 导航站点分类表 (navigation_categories)
-- 导航站点的分类管理
-- ============================================
DROP TABLE IF EXISTS `navigation_categories`;
CREATE TABLE `navigation_categories` (
                                         `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID，主键',
                                         `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
                                         `icon` VARCHAR(255) DEFAULT NULL COMMENT '分类图标',
                                         `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序权重',
                                         `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                         PRIMARY KEY (`id`),
                                         UNIQUE KEY `uk_name` (`name`),
                                         KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='导航站点分类表';

-- ============================================
-- 10. 导航站点表 (navigation_sites)
-- 存储常用网站导航信息
-- ============================================
DROP TABLE IF EXISTS `navigation_sites`;
CREATE TABLE `navigation_sites` (
                                    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '站点ID，主键',
                                    `category_id` BIGINT UNSIGNED NOT NULL COMMENT '分类ID，外键关联navigation_categories表',
                                    `name` VARCHAR(100) NOT NULL COMMENT '站点名称',
                                    `description` TEXT DEFAULT NULL COMMENT '站点描述',
                                    `url` VARCHAR(500) NOT NULL COMMENT '站点URL',
                                    `icon` VARCHAR(500) DEFAULT NULL COMMENT '站点图标URL',
                                    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序权重',
                                    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    PRIMARY KEY (`id`),
                                    KEY `idx_category_id` (`category_id`),
                                    KEY `idx_sort_order` (`sort_order`),
                                    CONSTRAINT `fk_navigation_sites_category_id` FOREIGN KEY (`category_id`) REFERENCES `navigation_categories` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='导航站点表';

-- ============================================
-- 触发器：更新标签使用次数
-- ============================================
DELIMITER $$

-- 添加文章标签时，增加标签使用次数
DROP TRIGGER IF EXISTS `tr_article_tags_after_insert`$$
CREATE TRIGGER `tr_article_tags_after_insert`
    AFTER INSERT ON `article_tags`
    FOR EACH ROW
BEGIN
    UPDATE `tags` SET `usage_count` = `usage_count` + 1 WHERE `id` = NEW.`tag_id`;
END$$

-- 删除文章标签时，减少标签使用次数
DROP TRIGGER IF EXISTS `tr_article_tags_after_delete`$$
CREATE TRIGGER `tr_article_tags_after_delete`
    AFTER DELETE ON `article_tags`
    FOR EACH ROW
BEGIN
    UPDATE `tags` SET `usage_count` = `usage_count` - 1 WHERE `id` = OLD.`tag_id`;
END$$

DELIMITER ;

-- ============================================
-- 初始化数据
-- ============================================

-- 插入默认管理员用户
INSERT INTO `users` (`username`, `email`, `password_hash`, `avatar`, `bio`, `github`, `role`, `status`)
VALUES ('Syne', 'hitori150221@outlook.com', '$2a$10$placeholder', '/avatars/admin.jpg', '热爱技术，专注于软件开发', 'https://github.com/aixcrimson', 1, 1);

-- 插入默认分类
INSERT INTO `categories` (`name`, `slug`, `description`, `sort_order`) VALUES
                                                                           ('前端开发', 'frontend', 'Web前端技术分享', 100),
                                                                           ('后端开发', 'backend', '服务端技术探讨', 90),
                                                                           ('数据库', 'database', '数据库设计与优化', 80),
                                                                           ('DevOps', 'devops', '运维与部署相关', 70),
                                                                           ('算法', 'algorithm', '数据结构与算法', 60),
                                                                           ('随笔', 'essay', '生活随笔', 50);

-- 插入默认标签
INSERT INTO `tags` (`name`, `slug`, `color`) VALUES
                                                 ('Vue', 'vue', '#42b883'),
                                                 ('React', 'react', '#61dafb'),
                                                 ('TypeScript', 'typescript', '#3178c6'),
                                                 ('Node.js', 'nodejs', '#339933'),
                                                 ('MySQL', 'mysql', '#4479A1'),
                                                 ('Redis', 'redis', '#DC382D'),
                                                 ('Docker', 'docker', '#2496ED'),
                                                 ('算法', 'algorithm', '#FF6B6B');

-- 插入导航分类
INSERT INTO `navigation_categories` (`name`, `icon`, `sort_order`) VALUES
                                                                       ('开发工具', 'tools', 100),
                                                                       ('学习资源', 'book', 90),
                                                                       ('技术社区', 'community', 80),
                                                                       ('设计资源', 'design', 70);

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 常用查询示例
-- ============================================

-- 1. 查询文章列表（包含作者、分类、标签）
/*
SELECT 
  a.id, a.title, a.summary, a.cover_image, a.views, a.likes, 
  a.published_time, a.created_at,
  u.username AS author_name, u.avatar AS author_avatar,
  c.name AS category_name,
  GROUP_CONCAT(t.name) AS tags
FROM articles a
LEFT JOIN users u ON a.user_id = u.id
LEFT JOIN categories c ON a.category_id = c.id
LEFT JOIN article_tags at ON a.id = at.article_id
LEFT JOIN tags t ON at.tag_id = t.id
WHERE a.status = 1
GROUP BY a.id
ORDER BY a.published_time DESC
LIMIT 10;
*/

-- 2. 查询文章详情（包含所有关联信息）
/*
SELECT
  a.*,
  u.username AS author_name, u.avatar AS author_avatar, u.bio AS author_bio,
  c.name AS category_name, c.slug AS category_slug
FROM articles a
LEFT JOIN users u ON a.user_id = u.id
LEFT JOIN categories c ON a.category_id = c.id
WHERE a.id = 1;
*/

-- 3. 查询热门文章（按浏览量排序）
/*
SELECT id, title, summary, views, likes, published_time
FROM articles
WHERE status = 1
ORDER BY views DESC
LIMIT 10;
*/

-- 4. 查询用户收藏的文章
/*
SELECT a.id, a.title, a.summary, af.created_at AS favorited_at
FROM article_favorites af
LEFT JOIN articles a ON af.article_id = a.id
WHERE af.user_id = 1
ORDER BY af.created_at DESC;
*/
