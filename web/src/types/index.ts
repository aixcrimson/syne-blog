// 文章接口定义
export interface Article {
  id: number
  title: string
  summary: string
  content: string
  categoryId?: number
  categoryName: string
  tags: { id: number; name: string }[]
  coverImage?: string
  publishedTime: string
  createTime?: string
  updateTime?: string
  views: number
  likes?: number
  favorites?: number
  commentsCount?: number
  status?: number
  isTop?: number
  isRecommend?: number
}

// 分类信息接口
export interface CategoryInfo {
  id: number
  name: string
  articleCount: number
}

// 标签信息接口
export interface TagInfo {
  id: number
  name: string
}

// 统计信息接口
export interface StatsInfo {
  totalArticles: number
  totalCategories: number
  totalViews: number
}

// 分页参数
export interface PaginationParams {
  page: number
  pageSize: number
}

// 分页结果
export interface PaginationResult<T> {
  data: T[]
  total: number
  page: number
  pageSize: number
  totalPages: number
}

/**
 * 用户信息接口
 * 统一用于站点信息展示和 API 返回
 */
export interface UserInfo {
  id?: number
  username: string
  email: string
  avatar: string | null
  bio: string | null
  github?: string | null
  bilibili?: string | null
  role?: number
}

// 主题模式
export type ThemeMode = 'light' | 'dark'

// 主题色调
export type ThemeColor = 'blue' | 'purple' | 'green' | 'orange' | 'pink'

// 导航菜单项
export interface MenuItem {
  name: string
  path: string
  icon?: any
}

// 导航站点
export interface NavigationSite {
  id: number
  categoryId: number
  categoryName: string
  name: string
  description: string
  url: string
  icon?: string
  sortOrder: number
  createTime: string
  updateTime: string
}

// 导航分类组
export interface NavigationCategory {
  categoryId: number
  categoryName: string
  sites: NavigationSite[]
}

// 公告接口
// 公告接口
export interface Notice {
  id: number
  content: string
  isShow: number
  sortOrder: number
  createTime: string
}

// 技能接口
export interface Skill {
  id: number
  name: string
  icon: string
  percentage: number
  color: string
  sortOrder: number
}

// 精选项目接口
export interface Project {
  id: number
  title: string
  description: string
  coverImage?: string
  githubUrl?: string
  previewUrl?: string
  techStack: string
  isFeatured: number
  sortOrder: number
}

// 时间线接口
export interface Timeline {
  id: number
  year: string
  title: string
  description: string
  icon: string
  color: string
  sortOrder: number
}

// 登录参数
export interface LoginData {
  username?: string
  password?: string
  email?: string
}

// 注册参数
export interface RegisterData {
  username: string
  password: string
  email: string
  code?: string
}

// 用户更新数据
export interface UserUpdateData {
  id: number
  username: string
  email: string
  avatar?: string
  bio?: string
  github?: string
  bilibili?: string
  password?: string // 某些接口可能需要
}

// 修改密码数据
export interface ChangePasswordData {
  oldPassword: string
  newPassword: string
}

// 登录响应
export interface LoginResponse {
  token: string
  userInfo: UserInfo
}

/**
 * 评论展示视图对象
 */
export interface CommentShowVO {
  id: number
  articleId: number
  userId: number | null
  username: string
  userAvatar: string | null
  parentId: number | null
  replyToUsername: string | null
  content: string
  createTime: string
  children?: CommentShowVO[]
}

/**
 * 创建评论请求参数
 */
export interface CommentCreateDTO {
  articleId: number
  parentId?: number | null
  content: string
}



