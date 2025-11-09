// 文章接口定义
export interface Article {
  id: number
  title: string
  summary: string
  content: string
  author: string
  category: string
  tags: string[]
  coverImage?: string
  createdAt: string
  updatedAt: string
  views: number
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
  icon?: string
}

// 导航网站
export interface NavigationSite {
  id: number
  name: string
  description: string
  url: string
  category: string
  icon?: string
}

