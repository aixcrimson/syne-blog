import { get } from './request'
import type { UserInfo, Notice, Project, Skill, Timeline } from '@/types'

export const siteApi = {
  /**
   * 获取博主/站长信息
   */
  getAuthorInfo() {
    return get<UserInfo>('/site/author')
  },

  /**
   * 获取侧边栏公告
   */
  getNotices() {
    return get<Notice[]>('/site/notices')
  },

  /**
   * 获取精选项目 (About页)
   */
  getFeaturedProjects() {
    return get<Project[]>('/site/projects')
  },
  
  /**
   * 获取所有项目
   */
  getAllProjects() {
    return get<Project[]>('/site/projects/all')
  },

  /**
   * 获取技能栈 (About页)
   */
  getSkills() {
    return get<Skill[]>('/site/skills')
  },

  /**
   * 获取成长历程 (About页)
   */
  getTimelines() {
    return get<Timeline[]>('/site/timelines')
  }
}
