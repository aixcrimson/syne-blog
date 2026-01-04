import { get } from './request'
import type { UserInfo, Notice, Project, SkillGroup, Milestone } from '@/types'

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
    return get<Notice[]>('/notices')
  },

  /**
   * 获取精选项目 (About页)
   */
  getProjects() {
    return get<Project[]>('/projects')
  },

  /**
   * 获取技能栈 (About页)
   */
  getSkills() {
    return get<SkillGroup[]>('/skills')
  },

  /**
   * 获取成长历程 (About页)
   */
  getMilestones() {
    return get<Milestone[]>('/milestones')
  }
}
