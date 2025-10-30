/**
 * Day.js 配置文件
 * 统一配置 dayjs 实例和插件
 */
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn' // 中文语言包
import relativeTime from 'dayjs/plugin/relativeTime' // 相对时间插件
import updateLocale from 'dayjs/plugin/updateLocale' // 更新语言包插件
import isBetween from 'dayjs/plugin/isBetween' // 判断是否在时间范围内
import isSameOrBefore from 'dayjs/plugin/isSameOrBefore' // 判断是否相同或之前
import isSameOrAfter from 'dayjs/plugin/isSameOrAfter' // 判断是否相同或之后
import duration from 'dayjs/plugin/duration' // 时间长度
import customParseFormat from 'dayjs/plugin/customParseFormat' // 自定义解析格式

// 注册插件
dayjs.extend(relativeTime)
dayjs.extend(updateLocale)
dayjs.extend(isBetween)
dayjs.extend(isSameOrBefore)
dayjs.extend(isSameOrAfter)
dayjs.extend(duration)
dayjs.extend(customParseFormat)

// 设置中文为默认语言
dayjs.locale('zh-cn')

// 自定义相对时间的显示文本
dayjs.updateLocale('zh-cn', {
  relativeTime: {
    future: '%s后',
    past: '%s前',
    s: '刚刚',
    m: '1分钟',
    mm: '%d分钟',
    h: '1小时',
    hh: '%d小时',
    d: '1天',
    dd: '%d天',
    M: '1个月',
    MM: '%d个月',
    y: '1年',
    yy: '%d年',
  },
})

/**
 * 导出配置好的 dayjs 实例
 */
export default dayjs

