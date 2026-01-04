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

// 用户信息
export interface UserInfo {
  name: string
  avatar: string
  bio: string
  email: string
  github?: string
  bilibili?: string
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
export interface Notice {
  id: number
  content: string
  isEnabled?: number
  sortOrder?: number
  createTime?: string
}

// 项目展示接口
export interface Project {
  id: number
  title: string
  description: string
  cover?: string
  tags: string[]
  links: {
    github?: string
    demo?: string
  }
}

// 技能分组接口
export interface SkillGroup {
  category: string
  items: {
    name: string
    icon: string
    level?: number
  }[]
}

// 里程碑/成长历程接口
export interface Milestone {
  date: string
  title: string
  content: string
  color?: string
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

// 登录响应
export interface LoginResponse {
  token: string
  userInfo: UserInfo
}


