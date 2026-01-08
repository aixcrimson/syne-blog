/**
 * 作者信息管理 API
 * 包含公告、技能、项目、时间线的 CRUD 操作
 */
import { get, post, put, del } from './request'
import type { 
  Notice, NoticeForm, 
  Skill, SkillForm, 
  Project, ProjectForm, 
  Timeline, TimelineForm 
} from '@/types'

// ==================== 公告 API ====================

/**
 * 公告 API 接口
 */
export const noticeApi = {
  /**
   * 获取公告列表
   * @returns 公告列表
   */
  getList: (): Promise<Notice[]> => {
    return get<Notice[]>('/admin/notices')
  },

  /**
   * 获取公告详情
   * @param id 公告ID
   * @returns 公告详情
   */
  getById: (id: number): Promise<Notice> => {
    return get<Notice>(`/admin/notices/${id}`)
  },

  /**
   * 创建公告
   * @param data 公告数据
   * @returns 创建的公告
   */
  create: (data: NoticeForm): Promise<Notice> => {
    return post<Notice>('/admin/notices', data)
  },

  /**
   * 更新公告
   * @param id 公告ID
   * @param data 公告数据
   * @returns 更新后的公告
   */
  update: (id: number, data: NoticeForm): Promise<Notice> => {
    return put<Notice>(`/admin/notices/${id}`, data)
  },

  /**
   * 删除公告
   * @param ids 公告ID（多个用逗号分隔）
   * @returns 删除结果
   */
  delete: (ids: string): Promise<string> => {
    return del<string>('/admin/notices', { ids })
  },

  /**
   * 切换公告显示状态
   * @param id 公告ID
   * @returns 更新后的公告
   */
  toggleShow: (id: number): Promise<Notice> => {
    return put<Notice>(`/admin/notices/${id}/toggle-show`)
  }
}

// ==================== 技能 API ====================

/**
 * 技能 API 接口
 */
export const skillApi = {
  /**
   * 获取技能列表
   * @returns 技能列表
   */
  getList: (): Promise<Skill[]> => {
    return get<Skill[]>('/admin/skills')
  },

  /**
   * 获取技能详情
   * @param id 技能ID
   * @returns 技能详情
   */
  getById: (id: number): Promise<Skill> => {
    return get<Skill>(`/admin/skills/${id}`)
  },

  /**
   * 创建技能
   * @param data 技能数据
   * @returns 创建的技能
   */
  create: (data: SkillForm): Promise<Skill> => {
    return post<Skill>('/admin/skills', data)
  },

  /**
   * 更新技能
   * @param id 技能ID
   * @param data 技能数据
   * @returns 更新后的技能
   */
  update: (id: number, data: SkillForm): Promise<Skill> => {
    return put<Skill>(`/admin/skills/${id}`, data)
  },

  /**
   * 删除技能
   * @param ids 技能ID（多个用逗号分隔）
   * @returns 删除结果
   */
  delete: (ids: string): Promise<string> => {
    return del<string>('/admin/skills', { ids })
  }
}

// ==================== 项目 API ====================

/**
 * 项目 API 接口
 */
export const projectApi = {
  /**
   * 获取项目列表
   * @returns 项目列表
   */
  getList: (): Promise<Project[]> => {
    return get<Project[]>('/admin/projects')
  },

  /**
   * 获取项目详情
   * @param id 项目ID
   * @returns 项目详情
   */
  getById: (id: number): Promise<Project> => {
    return get<Project>(`/admin/projects/${id}`)
  },

  /**
   * 创建项目
   * @param data 项目数据
   * @returns 创建的项目
   */
  create: (data: ProjectForm): Promise<Project> => {
    return post<Project>('/admin/projects', data)
  },

  /**
   * 更新项目
   * @param id 项目ID
   * @param data 项目数据
   * @returns 更新后的项目
   */
  update: (id: number, data: ProjectForm): Promise<Project> => {
    return put<Project>(`/admin/projects/${id}`, data)
  },

  /**
   * 删除项目
   * @param ids 项目ID（多个用逗号分隔）
   * @returns 删除结果
   */
  delete: (ids: string): Promise<string> => {
    return del<string>('/admin/projects', { ids })
  },

  /**
   * 切换项目精选状态
   * @param id 项目ID
   * @returns 更新后的项目
   */
  toggleFeatured: (id: number): Promise<Project> => {
    return put<Project>(`/admin/projects/${id}/toggle-featured`)
  }
}

// ==================== 时间线 API ====================

/**
 * 时间线 API 接口
 */
export const timelineApi = {
  /**
   * 获取时间线列表
   * @returns 时间线列表
   */
  getList: (): Promise<Timeline[]> => {
    return get<Timeline[]>('/admin/timelines')
  },

  /**
   * 获取时间线详情
   * @param id 时间线ID
   * @returns 时间线详情
   */
  getById: (id: number): Promise<Timeline> => {
    return get<Timeline>(`/admin/timelines/${id}`)
  },

  /**
   * 创建时间线
   * @param data 时间线数据
   * @returns 创建的时间线
   */
  create: (data: TimelineForm): Promise<Timeline> => {
    return post<Timeline>('/admin/timelines', data)
  },

  /**
   * 更新时间线
   * @param id 时间线ID
   * @param data 时间线数据
   * @returns 更新后的时间线
   */
  update: (id: number, data: TimelineForm): Promise<Timeline> => {
    return put<Timeline>(`/admin/timelines/${id}`, data)
  },

  /**
   * 删除时间线
   * @param ids 时间线ID（多个用逗号分隔）
   * @returns 删除结果
   */
  delete: (ids: string): Promise<string> => {
    return del<string>('/admin/timelines', { ids })
  }
}

export default {
  noticeApi,
  skillApi,
  projectApi,
  timelineApi
}
