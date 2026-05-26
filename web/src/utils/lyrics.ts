export interface LyricLine {
  time: number // 单位为秒，如果没有时间戳则为 -1
  text: string
  translation?: string
}

/**
 * 辅助函数：将双语歌词行拆分为原文和翻译部分。
 * 支持：
 * 1. 斜杠或竖线分隔（例如 "キセキ / 奇迹"）
 * 2. 空格分隔，左侧为日文假名，右侧为中文/英文（例如 "キセキ 奇迹"）
 */
export function splitBilingualLine(text: string): { original: string; translation?: string } {
  const trimmed = text.trim()
  if (!trimmed) return { original: '' }
  
  // 1. 如果有斜杠或竖线，优先以此进行切分
  if (!trimmed.includes('://')) {
    const parts = trimmed.split(/\s*[\/|｜]\s*/)
    if (parts.length >= 2) {
      return {
        original: parts[0].trim(),
        translation: parts.slice(1).join(' / ').trim()
      }
    }
  }

  // 2. 尝试检测以空格分隔的日文-中文/英文混合排版
  const hasKana = (str: string) => /[\u3040-\u309f\u30a0-\u30ff]/.test(str)
  const hasTranslationChars = (str: string) => /[a-zA-Z\u4e00-\u9fa5]/.test(str)

  const parts = trimmed.split(/\s+/)
  if (parts.length >= 2) {
    // 自左向右扫描，找到第一个索引 i，使得右侧不包含假名但包含翻译字符（汉字/英文），且左侧确实包含假名。
    for (let i = 1; i < parts.length; i++) {
      const leftSide = parts.slice(0, i).join(' ')
      const rightSide = parts.slice(i).join(' ')

      if (hasKana(leftSide) && hasTranslationChars(rightSide) && !hasKana(rightSide)) {
        return {
          original: leftSide.trim(),
          translation: rightSide.trim()
        }
      }
    }
  }

  return { original: trimmed }
}

/**
 * 解析 LRC 格式的歌词字符串为 LyricLine 对象数组。
 * 支持单行多个时间戳、过滤元数据标签，并合并/分组相同时间戳的行或行内双语翻译。
 */
export function parseLyrics(lyricStr: string): LyricLine[] {
  if (!lyricStr) return []

  const lines = lyricStr.split(/\r?\n/)
  const rawList: { time: number; text: string }[] = []

  for (const line of lines) {
    const trimmed = line.trim()
    if (!trimmed) continue

    // 匹配行中所有的时间戳，例如 [00:12.34]、[00:12.340] 或 [00:12]
    const matches = [...trimmed.matchAll(/\[(\d{2}):(\d{2})(?:\.(\d{2,3}))?\]/g)]
    if (matches.length > 0) {
      // 获取最后一个时间戳闭合括号后面的文本内容
      const lastBracketIndex = trimmed.lastIndexOf(']')
      const text = trimmed.slice(lastBracketIndex + 1).trim()

      for (const match of matches) {
        const minutes = parseInt(match[1], 10)
        const seconds = parseInt(match[2], 10)
        const msStr = match[3] || '0'
        // 将毫秒统一归一化为毫秒数（例如 "3" -> 300ms, "34" -> 340ms, "345" -> 345ms）
        const ms = parseInt(msStr.padEnd(3, '0').slice(0, 3), 10)
        const time = minutes * 60 + seconds + ms / 1000
        rawList.push({ time, text })
      }
    } else {
      // 跳过 LRC 元数据标签，如 [ti:标题]、[ar:歌手]、[al:专辑]、[by:歌词作者] 等
      if (trimmed.startsWith('[') && trimmed.includes(':') && trimmed.endsWith(']')) {
        continue
      }
      // 如果是纯文本歌词，则以 time = -1 写入
      rawList.push({ time: -1, text: trimmed })
    }
  }

  // 过滤并排序带时间戳的原始歌词
  const timedRaw = rawList.filter(r => r.time >= 0).sort((a, b) => a.time - b.time)
  const untimedRaw = rawList.filter(r => r.time < 0)

  const mergedList: LyricLine[] = []

  for (const item of timedRaw) {
    const { original, translation } = splitBilingualLine(item.text)

    // 检查是否已经存在时间戳非常接近的行（时间差小于 0.05 秒）
    const existing = mergedList.find(x => Math.abs(x.time - item.time) < 0.05)
    if (existing) {
      if (!existing.translation) {
        existing.translation = original
      } else {
        existing.translation += ' / ' + original
      }
    } else {
      mergedList.push({
        time: item.time,
        text: original,
        translation: translation
      })
    }
  }

  if (mergedList.length > 0) {
    return mergedList
  }

  // 降级为纯文本歌词的处理
  return untimedRaw.map(r => {
    const { original, translation } = splitBilingualLine(r.text)
    return {
      time: -1,
      text: original,
      translation: translation
    }
  })
}
