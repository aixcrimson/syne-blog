/**
 * 仪表盘 API
 * 提供统计数据、最近文章和最近评论的获取方法
 */
import { get } from './request'
import type { DashboardStats, RecentArticle, RecentComment } from '@/types'

/**
 * 仪表盘数据接口
 * 包含统计数据、最近文章和最近评论
 */
export interface DashboardData {
  /** 统计数据 */
  stats: DashboardStats
  /** 最近文章列表（最多5篇） */
  recentArticles: RecentArticle[]
  /** 最近评论列表（最多5条） */
  recentComments: RecentComment[]
}

/**
 * 仪表盘 API 方法集合
 */
export const dashboardApi = {
  /**
   * 获取仪表盘统计数据
   * 包含文章数、分类数、标签数、评论数、浏览量、点赞数
   * @returns 统计数据
   */
  getStats: () => get<DashboardStats>('/admin/dashboard/stats'),

  /**
   * 获取最近发布的文章列表
   * @param limit 返回数量限制，默认5
   * @returns 最近文章列表
   */
  getRecentArticles: (limit: number = 5) => 
    get<RecentArticle[]>('/admin/dashboard/recent-articles', { limit }),

  /**
   * 获取最近的评论列表
   * @param limit 返回数量限制，默认5
   * @returns 最近评论列表
   */
  getRecentComments: (limit: number = 5) => 
    get<RecentComment[]>('/admin/dashboard/recent-comments', { limit }),

  /**
   * 获取仪表盘全部数据（统计+最近文章+最近评论）
   * 一次请求获取所有数据，减少网络请求
   * @returns 仪表盘完整数据
   */
  getDashboardData: () => get<DashboardData>('/admin/dashboard'),
}

export default dashboardApi
