/**
 * 管理端基础类型定义
 * 包含所有实体接口和状态枚举
 */

// ==================== 状态枚举 ====================

/**
 * 文章状态枚举
 */
export enum ArticleStatus {
  /** 已发布 */
  PUBLISHED = 1,
  /** 草稿 */
  DRAFT = 2,
  /** 已下架 */
  OFFLINE = 3
}

/**
 * 评论状态枚举
 */
export enum CommentStatus {
  /** 正常 */
  NORMAL = 1,
  /** 待审核 */
  PENDING = 2,
  /** 已删除 */
  DELETED = 3
}

/**
 * 用户角色枚举
 */
export enum UserRole {
  /** 管理员 */
  ADMIN = 1,
  /** 普通用户 */
  USER = 2
}

/**
 * 用户状态枚举
 */
export enum UserStatus {
  /** 禁用 */
  DISABLED = 0,
  /** 正常 */
  NORMAL = 1
}

// ==================== 实体接口 ====================

/**
 * 标签接口
 */
export interface Tag {
  /** 标签ID */
  id: number
  /** 标签名称 */
  name: string
  /** 标签别名（URL友好） */
  slug: string
  /** 标签颜色 */
  color: string
  /** 有效文章数 */
  articleCount: number
  /** 创建时间 */
  createTime: string
}


/**
 * 标签表单接口
 */
export interface TagForm {
  /** 标签名称 */
  name: string
  /** 标签别名 */
  slug: string
  /** 标签颜色 */
  color: string
}

/**
 * 分类接口
 */
export interface Category {
  /** 分类ID */
  id: number
  /** 分类名称 */
  name: string
  /** 分类别名（URL友好） */
  slug: string
  /** 分类描述 */
  description: string
  /** 排序顺序 */
  sortOrder: number
  /** 文章数量 */
  articleCount: number
  /** 创建时间 */
  createTime: string
}

/**
 * 分类表单接口
 */
export interface CategoryForm {
  /** 分类名称 */
  name: string
  /** 分类别名 */
  slug: string
  /** 分类描述 */
  description?: string
  /** 排序顺序 */
  sortOrder: number
}

/**
 * 文章接口
 */
export interface Article {
  /** 文章ID */
  id: number
  /** 文章标题 */
  title: string
  /** 文章摘要 */
  summary: string
  /** 文章内容（Markdown） */
  content: string
  /** 分类ID */
  categoryId: number
  /** 分类名称 */
  categoryName: string
  /** 标签列表 */
  tags: Tag[]
  /** 封面图片URL */
  coverImage: string
  /** 浏览量 */
  views: number
  /** 点赞数 */
  likes: number
  /** 收藏数 */
  favorites: number
  /** 文章状态 */
  status: ArticleStatus
  /** 是否置顶 (0-否, 1-是) */
  isTop: number
  /** 是否推荐 (0-否, 1-是) */
  isRecommend: number
  /** 发布时间 */
  publishedTime: string
  /** 创建时间 */
  createTime: string
  /** 更新时间 */
  updateTime: string
}

/**
 * 文章表单接口
 */
export interface ArticleForm {
  /** 文章ID（编辑时需要） */
  id?: number
  /** 文章标题 */
  title: string
  /** 文章摘要 */
  summary: string
  /** 文章内容 */
  content: string
  /** 分类ID */
  categoryId?: number
  /** 标签ID列表 */
  tagIds: number[]
  /** 封面图片URL */
  coverImage?: string
  /** 文章状态 */
  status: ArticleStatus
  /** 是否置顶 */
  isTop: number
  /** 是否推荐 */
  isRecommend: number
}


/**
 * 文章列表查询参数
 */
export interface ArticleListParams {
  /** 页码 */
  page: number
  /** 每页数量 */
  pageSize: number
  /** 搜索关键词 */
  keyword?: string
  /** 分类ID */
  categoryId?: number
  /** 文章状态 */
  status?: ArticleStatus
}

/**
 * 评论接口
 */
export interface Comment {
  /** 评论ID */
  id: number
  /** 文章ID */
  articleId: number
  /** 文章标题 */
  articleTitle: string
  /** 用户ID */
  userId: number
  /** 用户名 */
  username: string
  /** 评论内容 */
  content: string
  /** 评论状态 */
  status: CommentStatus
  /** 创建时间 */
  createTime: string
}

/**
 * 评论列表查询参数
 */
export interface CommentListParams {
  /** 页码 */
  page: number
  /** 每页数量 */
  pageSize: number
  /** 评论状态 */
  status?: CommentStatus
}

/**
 * 用户接口
 */
export interface User {
  /** 用户ID */
  id: number
  /** 用户名 */
  username: string
  /** 邮箱 */
  email: string
  /** 头像URL */
  avatar: string
  /** 个人简介 */
  bio: string
  /** GitHub链接 */
  github: string
  /** Bilibili链接 */
  bilibili: string
  /** 用户角色 */
  role: UserRole
  /** 用户状态 */
  status: UserStatus
  /** 注册时间 */
  createTime: string
}

/**
 * 用户列表查询参数
 */
export interface UserListParams {
  /** 页码 */
  page: number
  /** 每页数量 */
  pageSize: number
  /** 搜索关键词 */
  keyword?: string
}

/**
 * 用户信息（登录后返回）
 */
export interface UserInfo {
  /** 用户ID */
  id: number
  /** 用户名 */
  username: string
  /** 邮箱 */
  email: string
  /** 头像URL */
  avatar: string
  /** 个人简介 */
  bio?: string
  /** GitHub链接 */
  github?: string
  /** Bilibili链接 */
  bilibili?: string
  /** 用户角色 */
  role: UserRole
}

/**
 * 更新个人资料参数
 */
export interface UpdateProfileParams {
  /** 头像URL */
  avatar?: string
  /** 个人简介 */
  bio?: string
  /** GitHub链接 */
  github?: string
  /** Bilibili链接 */
  bilibili?: string
}

/**
 * 修改密码参数
 */
export interface ChangePasswordParams {
  /** 原密码 */
  oldPassword: string
  /** 新密码 */
  newPassword: string
  /** 确认密码 */
  confirmPassword: string
}


/**
 * 导航站点接口
 */
export interface NavigationSite {
  /** 站点ID */
  id: number
  /** 分类ID */
  categoryId: number
  /** 站点名称 */
  name: string
  /** 站点描述 */
  description: string
  /** 站点URL */
  url: string
  /** 排序顺序 */
  sortOrder: number
}

/**
 * 导航分类接口
 */
export interface NavigationCategory {
  /** 分类ID */
  id: number
  /** 分类名称 */
  name: string
  /** 排序顺序 */
  sortOrder: number
  /** 站点列表 */
  sites: NavigationSite[]
}

/**
 * 排序更新项
 */
export interface SortOrderItem {
  /** 项目ID */
  id: number
  /** 排序顺序 */
  sortOrder: number
}

// ==================== 仪表盘相关 ====================

/**
 * 仪表盘统计数据
 */
export interface DashboardStats {
  /** 文章总数 */
  articleCount: number
  /** 分类总数 */
  categoryCount: number
  /** 标签总数 */
  tagCount: number
  /** 评论总数 */
  commentCount: number
  /** 总浏览量 */
  totalViews: number
  /** 总点赞数 */
  totalLikes: number
}

/**
 * 最近文章（仪表盘用）
 */
export interface RecentArticle {
  /** 文章ID */
  id: number
  /** 文章标题 */
  title: string
  /** 浏览量 */
  views: number
  /** 发布时间 */
  publishedTime: string
}

/**
 * 最近评论（仪表盘用）
 */
export interface RecentComment {
  /** 评论ID */
  id: number
  /** 评论内容 */
  content: string
  /** 用户名 */
  username: string
  /** 文章标题 */
  articleTitle: string
  /** 创建时间 */
  createTime: string
}

// ==================== 菜单相关 ====================

/**
 * 菜单项接口
 */
export interface MenuItem {
  /** 路由路径 */
  path: string
  /** 菜单名称 */
  name: string
  /** 菜单图标 */
  icon: string
  /** 子菜单 */
  children?: MenuItem[]
}

// ==================== 登录相关 ====================

/**
 * 登录参数
 */
export interface LoginParams {
  /** 用户名 */
  username: string
  /** 密码 */
  password: string
}

/**
 * 登录响应
 */
export interface LoginResponse {
  /** 访问令牌 */
  token: string
  /** Token类型 */
  tokenType?: string
  /** 用户ID */
  id: number
  /** 用户名 */
  username: string
  /** 邮箱 */
  email: string
  /** 用户角色 */
  role: UserRole
  /** 头像 */
  avatar?: string
  /** 个人简介 */
  bio: string
  /** GitHub链接 */
  github: string
  /** B站链接 */
  bilibili: string
  /** 过期时间（秒） */
  expiresIn?: number
}

// ==================== 书签导入相关 ====================

/**
 * 书签项
 */
export interface BookmarkItem {
  /** 书签名称 */
  name: string
  /** 书签URL */
  url: string
  /** 所属文件夹 */
  folder: string
  /** 书签描述 */
  description?: string
}

/**
 * 文件夹统计
 */
export interface FolderStats {
  /** 文件夹名称 */
  name: string
  /** 文件夹路径 */
  path: string
  /** 书签数量 */
  count: number
}

/**
 * 书签预览响应
 */
export interface BookmarkPreviewDTO {
  /** 文件夹统计列表 */
  categories: FolderStats[]
  /** 书签列表 */
  bookmarks: BookmarkItem[]
  /** 书签总数 */
  totalBookmarks: number
  /** 文件夹总数 */
  totalFolders: number
}

/**
 * 文件夹映射
 */
export interface FolderMapping {
  /** 文件夹名称 */
  folder: string
  /** 映射的分类ID */
  categoryId: number
  /** 是否创建新分类 */
  createNew?: boolean
  /** 新分类名称 */
  newCategoryName?: string
}

/**
 * 书签映射请求
 */
export interface BookmarkMappingDTO {
  /** 文件夹与分类的映射关系 */
  mappings: FolderMapping[]
  /** 要导入的书签列表 */
  bookmarks: BookmarkItem[]
}

// ==================== 作者信息相关 ====================

/**
 * 公告接口
 */
export interface Notice {
  /** 公告ID */
  id: number
  /** 公告内容 */
  content: string
  /** 是否显示: 0-隐藏, 1-显示 */
  isShow: number
  /** 排序权重 */
  sortOrder: number
  /** 创建时间 */
  createTime: string
}

/**
 * 公告表单接口
 */
export interface NoticeForm {
  /** 公告内容 */
  content: string
  /** 是否显示 */
  isShow: number
  /** 排序权重 */
  sortOrder: number
}

/**
 * 技能接口
 */
export interface Skill {
  /** 技能ID */
  id: number
  /** 技能名称 */
  name: string
  /** 熟练度百分比 (0-100) */
  percentage: number
  /** 进度条颜色 */
  color: string
  /** 排序权重 */
  sortOrder: number
  /** 创建时间 */
  createTime: string
}

/**
 * 技能表单接口
 */
export interface SkillForm {
  /** 技能名称 */
  name: string
  /** 熟练度百分比 */
  percentage: number
  /** 进度条颜色 */
  color: string
  /** 排序权重 */
  sortOrder: number
}

/**
 * 项目接口
 */
export interface Project {
  /** 项目ID */
  id: number
  /** 项目标题 */
  title: string
  /** 项目描述 */
  description: string
  /** 封面图片 */
  coverImage: string
  /** GitHub链接 */
  githubUrl: string
  /** 预览链接 */
  previewUrl: string
  /** 技术栈（逗号分隔） */
  techStack: string
  /** 是否精选: 0-普通, 1-精选 */
  isFeatured: number
  /** 排序权重 */
  sortOrder: number
  /** 创建时间 */
  createTime: string
}

/**
 * 项目表单接口
 */
export interface ProjectForm {
  /** 项目标题 */
  title: string
  /** 项目描述 */
  description: string
  /** 封面图片 */
  coverImage?: string
  /** GitHub链接 */
  githubUrl?: string
  /** 预览链接 */
  previewUrl?: string
  /** 技术栈 */
  techStack?: string
  /** 是否精选 */
  isFeatured: number
  /** 排序权重 */
  sortOrder: number
}

/**
 * 时间线接口
 */
export interface Timeline {
  /** 时间线ID */
  id: number
  /** 年份或时间点 */
  year: string
  /** 标题 */
  title: string
  /** 描述 */
  description: string
  /** 节点颜色类型 */
  color: string
  /** 排序权重 */
  sortOrder: number
  /** 创建时间 */
  createTime: string
}

/**
 * 时间线表单接口
 */
export interface TimelineForm {
  /** 年份或时间点 */
  year: string
  /** 标题 */
  title: string
  /** 描述 */
  description?: string
  /** 节点颜色类型 */
  color: string
  /** 排序权重 */
  sortOrder: number
}


