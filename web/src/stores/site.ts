import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { UserInfo } from '@/types'
import { siteApi } from '@/api/site'
// 默认头像 (用于加载前的占位)
import defaultAvatar from '@/assets/images/avatar/defalutAvatar.jpg'

export const useSiteStore = defineStore('site', () => {
  // 博主信息
  const authorInfo = ref<UserInfo>({
    name: '',
    avatar: defaultAvatar,
    bio: '',
    email: '',
    github: '',
    bilibili: ''
  })

  // 动作: 获取博主信息
  const fetchAuthorInfo = async () => {
    try {
      const data = await siteApi.getAuthorInfo()
      if (data) {
        authorInfo.value = data
      }
    } catch (error) {
      console.error('获取博主信息失败:', error)
      // 失败时可以保持默认值或设置特定状态
    }
  }

  // 动作: 更新博主信息 (本地更新 + TODO: 需调用API)
  const updateAuthorInfo = (info: Partial<UserInfo>) => {
    authorInfo.value = { ...authorInfo.value, ...info }
  }

  return {
    authorInfo,
    fetchAuthorInfo,
    updateAuthorInfo
  }
})
