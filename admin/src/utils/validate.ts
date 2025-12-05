/**
 * 验证工具函数
 * 提供各种数据验证功能
 */

import type { Category, Tag } from '@/types'

// ==================== 分类验证 ====================

/**
 * 验证分类名称唯一性
 * @param name 要验证的名称
 * @param categories 现有分类列表
 * @param excludeId 排除的分类ID（编辑时使用）
 * @returns 是否唯一
 * @requirements 7.3
 */
export function isCategoryNameUnique(
  name: string,
  categories: Category[],
  excludeId?: number | null
): boolean {
  if (!name) return true
  return !categories.some(cat => cat.name === name && cat.id !== excludeId)
}

/**
 * 验证分类别名唯一性
 * @param slug 要验证的别名
 * @param categories 现有分类列表
 * @param excludeId 排除的分类ID（编辑时使用）
 * @returns 是否唯一
 * @requirements 7.3
 */
export function isCategorySlugUnique(
  slug: string,
  categories: Category[],
  excludeId?: number | null
): boolean {
  if (!slug) return true
  return !categories.some(cat => cat.slug === slug && cat.id !== excludeId)
}

/**
 * 检查分类是否可以删除
 * @param category 要删除的分类
 * @returns 是否可以删除
 * @requirements 7.6
 */
export function canDeleteCategory(category: Category): boolean {
  return category.articleCount === 0
}

// ==================== 标签验证 ====================

/**
 * 验证标签名称唯一性
 * @param name 要验证的名称
 * @param tags 现有标签列表
 * @param excludeId 排除的标签ID（编辑时使用）
 * @returns 是否唯一
 * @requirements 8.4
 */
export function isTagNameUnique(
  name: string,
  tags: Tag[],
  excludeId?: number | null
): boolean {
  if (!name) return true
  return !tags.some(tag => tag.name === name && tag.id !== excludeId)
}

/**
 * 验证标签别名唯一性
 * @param slug 要验证的别名
 * @param tags 现有标签列表
 * @param excludeId 排除的标签ID（编辑时使用）
 * @returns 是否唯一
 * @requirements 8.4
 */
export function isTagSlugUnique(
  slug: string,
  tags: Tag[],
  excludeId?: number | null
): boolean {
  if (!slug) return true
  return !tags.some(tag => tag.slug === slug && tag.id !== excludeId)
}

// ==================== URL 验证 ====================

/**
 * 验证 URL 格式是否有效
 * @param url 要验证的 URL
 * @returns 是否为有效 URL
 * @requirements 10.4
 */
export function isValidUrl(url: string): boolean {
  if (!url) return false
  try {
    const urlObj = new URL(url)
    return urlObj.protocol === 'http:' || urlObj.protocol === 'https:'
  } catch {
    return false
  }
}

// ==================== 别名格式验证 ====================

/**
 * 验证别名格式是否有效（只允许小写字母、数字和连字符）
 * @param slug 要验证的别名
 * @returns 是否为有效格式
 */
export function isValidSlug(slug: string): boolean {
  if (!slug) return false
  return /^[a-z0-9-]+$/.test(slug)
}
