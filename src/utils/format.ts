import dayjs from './dayjs'

/**
 * 格式化日期
 * @param dateString 日期字符串
 * @param format 格式类型
 * @returns 格式化后的日期字符串
 */
export function formatDate(dateString: string, format: 'full' | 'date' | 'relative' = 'date'): string {
  const date = dayjs(dateString)
  
  if (format === 'full') {
    // 完整日期时间：2024-03-15 14:30:25
    return date.format('YYYY-MM-DD HH:mm:ss')
  }
  
  if (format === 'date') {
    // 只有日期：2024-03-15
    return date.format('YYYY-MM-DD')
  }
  
  // 相对时间
  const now = dayjs()
  const days = now.diff(date, 'day')
  
  // 超过30天显示具体日期
  if (days > 30) {
    return date.format('YYYY-MM-DD')
  }
  
  // 30天内显示相对时间
  return date.fromNow()
}

/**
 * 格式化数字
 * @param num 数字
 * @returns 格式化后的字符串
 */
export function formatNumber(num: number): string {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

/**
 * 截断文本
 * @param text 文本
 * @param maxLength 最大长度
 * @param suffix 后缀
 * @returns 截断后的文本
 */
export function truncate(text: string, maxLength: number, suffix: string = '...'): string {
  if (text.length <= maxLength) {
    return text
  }
  return text.substring(0, maxLength) + suffix
}

/**
 * 转换为 URL 友好的字符串
 * @param text 文本
 * @returns URL 友好的字符串
 */
export function slugify(text: string): string {
  return text
    .toLowerCase()
    .replace(/[^\w\u4e00-\u9fa5]+/g, '-')
    .replace(/^-+|-+$/g, '')
}

/**
 * 获取友好的时间描述
 * @param dateString 日期字符串
 * @returns 友好的时间描述，如 "今天 14:30"、"昨天 09:15"、"2024-03-15"
 */
export function formatFriendlyTime(dateString: string): string {
  const date = dayjs(dateString)
  const now = dayjs()
  
  const isToday = date.isSame(now, 'day')
  const isYesterday = date.isSame(now.subtract(1, 'day'), 'day')
  const isThisYear = date.isSame(now, 'year')
  
  if (isToday) {
    return `今天 ${date.format('HH:mm')}`
  } else if (isYesterday) {
    return `昨天 ${date.format('HH:mm')}`
  } else if (isThisYear) {
    return date.format('MM-DD HH:mm')
  } else {
    return date.format('YYYY-MM-DD')
  }
}

/**
 * 计算阅读时长
 * @param wordCount 字数
 * @param wordsPerMinute 每分钟阅读字数（默认300）
 * @returns 阅读时长描述，如 "5分钟阅读"
 */
export function formatReadingTime(wordCount: number, wordsPerMinute: number = 300): string {
  const minutes = Math.ceil(wordCount / wordsPerMinute)
  return `${minutes}分钟阅读`
}

/**
 * 判断日期是否在指定天数内
 * @param dateString 日期字符串
 * @param days 天数
 * @returns 是否在指定天数内
 */
export function isWithinDays(dateString: string, days: number): boolean {
  const date = dayjs(dateString)
  const now = dayjs()
  return now.diff(date, 'day') <= days
}

/**
 * 格式化时间段
 * @param startDate 开始时间
 * @param endDate 结束时间（可选，默认为当前时间）
 * @returns 时间段描述，如 "2小时30分钟"
 */
export function formatDuration(startDate: string, endDate?: string): string {
  const start = dayjs(startDate)
  const end = endDate ? dayjs(endDate) : dayjs()
  
  const duration = dayjs.duration(end.diff(start))
  const days = duration.days()
  const hours = duration.hours()
  const minutes = duration.minutes()
  
  const parts: string[] = []
  if (days > 0) parts.push(`${days}天`)
  if (hours > 0) parts.push(`${hours}小时`)
  if (minutes > 0) parts.push(`${minutes}分钟`)
  
  return parts.length > 0 ? parts.join('') : '刚刚'
}

