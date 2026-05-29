/**
 * 音乐播放器音频标签解析工具
 * 支持从 MP3 (ID3v2) 和 FLAC (METADATA_BLOCK_PICTURE) 文件中解析并提取内嵌封面图片
 */

/**
 * 统一获取音频封面接口
 * 会首先读取文件前 4 字节的魔数来判断格式，随后调用对应的解析器
 */
export async function getAudioCover(url: string): Promise<string | null> {
  try {
    // 读取前 4 字节探测格式
    const initResponse = await fetch(url, {
      headers: {
        Range: 'bytes=0-3'
      }
    })

    if (!initResponse.ok) {
      // 降级使用后缀名判断
      return fallbackByExtension(url)
    }

    const initBuffer = await initResponse.arrayBuffer()
    const initBytes = new Uint8Array(initBuffer)

    if (initBytes.length >= 4) {
      if (initBytes[0] === 0x66 && initBytes[1] === 0x4c && initBytes[2] === 0x61 && initBytes[3] === 0x43) {
        // "fLaC" 魔数
        return getFlacCover(url)
      } else if (initBytes[0] === 0x49 && initBytes[1] === 0x44 && initBytes[2] === 0x33) {
        // "ID3" 魔数
        return getMp3Cover(url)
      }
    }

    return fallbackByExtension(url)
  } catch (e) {
    console.error('检测音频格式失败, 尝试使用后缀名降级解析:', e)
    return fallbackByExtension(url)
  }
}

function fallbackByExtension(url: string): Promise<string | null> {
  const cleanUrl = url.split('?')[0].split('#')[0].toLowerCase()
  if (cleanUrl.endsWith('.flac')) {
    return getFlacCover(url)
  }
  return getMp3Cover(url)
}

/**
 * 1. MP3 (ID3v2) 封面图解析
 */
export async function getMp3Cover(url: string): Promise<string | null> {
  try {
    // 1. 先读取前 128 字节确定 ID3 Tag 大小
    const initResponse = await fetch(url, {
      headers: {
        Range: 'bytes=0-127'
      }
    })

    if (!initResponse.ok) return null
    const initBuffer = await initResponse.arrayBuffer()
    const initBytes = new Uint8Array(initBuffer)

    if (initBytes.length < 10) return null
    if (initBytes[0] !== 0x49 || initBytes[1] !== 0x44 || initBytes[2] !== 0x33) {
      return null
    }

    // 计算 ID3 标签总大小 (Synchsafe)
    const tagSize = ((initBytes[6] & 0x7f) << 21) |
                    ((initBytes[7] & 0x7f) << 14) |
                    ((initBytes[8] & 0x7f) << 7) |
                    (initBytes[9] & 0x7f)

    const totalRequired = tagSize + 10

    let buffer: ArrayBuffer
    if (initBytes.length >= totalRequired) {
      buffer = initBuffer
    } else {
      // 2. 精准请求下载整个 ID3 段
      const response = await fetch(url, {
        headers: {
          Range: `bytes=0-${totalRequired - 1}`
        }
      })
      if (!response.ok) return null
      buffer = await response.arrayBuffer()
    }

    return parseID3(buffer)
  } catch (e) {
    console.error('解析 MP3 封面失败:', e)
    return null
  }
}

function parseID3(buffer: ArrayBuffer): string | null {
  const bytes = new Uint8Array(buffer)
  const view = new DataView(buffer)

  const majorVersion = bytes[3]
  const flags = bytes[5]
  const tagSize = ((bytes[6] & 0x7f) << 21) |
                  ((bytes[7] & 0x7f) << 14) |
                  ((bytes[8] & 0x7f) << 7) |
                  (bytes[9] & 0x7f)

  let offset = 10

  if ((flags & 0x40) !== 0) {
    if (offset + 4 > bytes.length) return null
    let extHeaderSize = 0
    if (majorVersion === 3) {
      extHeaderSize = view.getUint32(offset)
      offset += 4 + extHeaderSize
    } else {
      extHeaderSize = ((bytes[offset] & 0x7f) << 21) |
                      ((bytes[offset + 1] & 0x7f) << 14) |
                      ((bytes[offset + 2] & 0x7f) << 7) |
                      (bytes[offset + 3] & 0x7f)
      offset += extHeaderSize
    }
  }

  while (offset + 10 < bytes.length && offset < tagSize + 10) {
    const frameId = String.fromCharCode(bytes[offset], bytes[offset + 1], bytes[offset + 2], bytes[offset + 3])
    if (bytes[offset] === 0 && bytes[offset + 1] === 0 && bytes[offset + 2] === 0 && bytes[offset + 3] === 0) {
      break
    }

    let frameSize = 0
    if (majorVersion === 3) {
      frameSize = view.getUint32(offset + 4)
    } else {
      frameSize = ((bytes[offset + 4] & 0x7f) << 21) |
                  ((bytes[offset + 5] & 0x7f) << 14) |
                  ((bytes[offset + 6] & 0x7f) << 7) |
                  (bytes[offset + 7] & 0x7f)
    }

    offset += 10

    if (frameId === 'APIC') {
      if (offset + frameSize > bytes.length) return null
      const encoding = bytes[offset]

      let mimeOffset = offset + 1
      while (mimeOffset < offset + frameSize && bytes[mimeOffset] !== 0) {
        mimeOffset++
      }

      const mimeType = String.fromCharCode(...bytes.slice(offset + 1, mimeOffset))
      let descOffset = mimeOffset + 2

      if (encoding === 0 || encoding === 3) {
        while (descOffset < offset + frameSize && bytes[descOffset] !== 0) {
          descOffset++
        }
        descOffset += 1
      } else {
        while (descOffset + 1 < offset + frameSize && (bytes[descOffset] !== 0 || bytes[descOffset + 1] !== 0)) {
          descOffset += 2
        }
        descOffset += 2
      }

      const imgDataStart = descOffset
      const imgDataEnd = offset + frameSize
      if (imgDataStart >= imgDataEnd) return null

      const imgBytes = bytes.slice(imgDataStart, imgDataEnd)
      const blob = new Blob([imgBytes], { type: mimeType })
      return URL.createObjectURL(blob)
    }

    offset += frameSize
  }
  return null
}

/**
 * 2. FLAC (Metadata Block Picture) 封面图解析
 */
export async function getFlacCover(url: string): Promise<string | null> {
  try {
    // 1. 先发起 Range 请求获取前 8KB 的数据进行元数据块检测
    const initResponse = await fetch(url, {
      headers: {
        Range: 'bytes=0-8191'
      }
    })

    if (!initResponse.ok) return null
    let buffer = await initResponse.arrayBuffer()
    let bytes = new Uint8Array(buffer)

    // 检测 fLaC 魔数
    if (bytes.length < 4 || bytes[0] !== 0x66 || bytes[1] !== 0x4c || bytes[2] !== 0x61 || bytes[3] !== 0x43) {
      return null
    }

    let offset = 4
    let isLastBlock = false

    while (offset + 4 <= bytes.length && !isLastBlock) {
      const headerByte = bytes[offset]
      isLastBlock = (headerByte & 0x80) !== 0
      const blockType = headerByte & 0x7f
      const blockLength = (bytes[offset + 1] << 16) | (bytes[offset + 2] << 8) | bytes[offset + 3]

      const blockDataStart = offset + 4
      const blockDataEnd = blockDataStart + blockLength

      // 如果当前数据块的结尾超出了已下载的字节大小，需要精准拉取更大的 Range
      if (blockDataEnd > bytes.length) {
        // 如果服务器没有遵循 Range（直接返回了 200 OK 全量文件），则说明下载其实是完整的，直接中断以防越界
        if (bytes.length >= blockDataEnd) {
          break
        }

        const newResponse = await fetch(url, {
          headers: {
            Range: `bytes=0-${blockDataEnd - 1}`
          }
        })
        if (!newResponse.ok) return null
        buffer = await newResponse.arrayBuffer()
        bytes = new Uint8Array(buffer)
      }

      // 如果是 Picture 块 (Block Type 6)
      if (blockType === 6) {
        return parseFlacPictureBlock(buffer, blockDataStart, blockLength)
      }

      offset = blockDataEnd
    }

    return null
  } catch (e) {
    console.error('解析 FLAC 封面失败:', e)
    return null
  }
}

function parseFlacPictureBlock(buffer: ArrayBuffer, offset: number, blockLength: number): string | null {
  const bytes = new Uint8Array(buffer)
  const dataView = new DataView(buffer, offset, blockLength)

  try {
    // 2. MIME Length (uint32) at offset 4
    const mimeLength = dataView.getUint32(4)

    // 3. MIME Type String (ASCII) starts at offset 8
    const mimeTypeBytes = bytes.slice(offset + 8, offset + 8 + mimeLength)
    const mimeType = String.fromCharCode(...mimeTypeBytes)

    // 4. Description Length (uint32) starts at offset 8 + mimeLength
    const descLength = dataView.getUint32(8 + mimeLength)

    // 9. Picture Data Length (uint32) starts at offset 28 + mimeLength + descLength
    const picDataLength = dataView.getUint32(28 + mimeLength + descLength)

    // 10. Picture Data bytes start at offset 32 + mimeLength + descLength
    const picDataStart = offset + 32 + mimeLength + descLength

    if (picDataStart + picDataLength > bytes.length) {
      return null
    }

    const picBytes = bytes.slice(picDataStart, picDataStart + picDataLength)
    const blob = new Blob([picBytes], { type: mimeType })
    return URL.createObjectURL(blob)
  } catch (err) {
    console.error('解析 FLAC PICTURE 块失败:', err)
    return null
  }
}

/**
 * 统一获取音频歌词接口
 * 会首先读取文件前 4 字节的魔数来判断格式，随后调用对应的解析器，最后通过同名 .lrc 文件降级
 */
export async function getAudioLyrics(url: string): Promise<string | null> {
  try {
    const initResponse = await fetch(url, {
      headers: {
        Range: 'bytes=0-3'
      }
    })

    if (!initResponse.ok) {
      return fallbackLyricsByExtension(url)
    }

    const initBuffer = await initResponse.arrayBuffer()
    const initBytes = new Uint8Array(initBuffer)

    let lyrics: string | null = null

    if (initBytes.length >= 4) {
      if (initBytes[0] === 0x66 && initBytes[1] === 0x4c && initBytes[2] === 0x61 && initBytes[3] === 0x43) {
        // "fLaC" 魔数
        lyrics = await getFlacLyrics(url)
      } else if (initBytes[0] === 0x49 && initBytes[1] === 0x44 && initBytes[2] === 0x33) {
        // "ID3" 魔数
        lyrics = await getMp3Lyrics(url)
      }
    }

    if (!lyrics) {
      return fallbackLyricsByExtension(url)
    }

    return lyrics
  } catch (e) {
    console.error('检测音频格式失败, 尝试使用后缀名降级解析歌词:', e)
    return fallbackLyricsByExtension(url)
  }
}

async function fallbackLyricsByExtension(url: string): Promise<string | null> {
  // 如果是本地路径，尝试去拉取同名的 .lrc 文件
  if (url.startsWith('/') || url.startsWith(window.location.origin)) {
    const baseUrl = url.split('?')[0].split('#')[0]
    const lrcUrl = baseUrl.replace(/\.[a-zA-Z0-9]+$/, '.lrc')
    try {
      const response = await fetch(lrcUrl)
      if (response.ok) {
        return await response.text()
      }
    } catch (e) {
      // 忽略错误
    }
  }
  return null
}

/**
 * MP3 歌词解析 (提取 USLT 帧)
 */
export async function getMp3Lyrics(url: string): Promise<string | null> {
  try {
    const initResponse = await fetch(url, {
      headers: {
        Range: 'bytes=0-127'
      }
    })

    if (!initResponse.ok) return null
    const initBuffer = await initResponse.arrayBuffer()
    const initBytes = new Uint8Array(initBuffer)

    if (initBytes.length < 10) return null
    if (initBytes[0] !== 0x49 || initBytes[1] !== 0x44 || initBytes[2] !== 0x33) {
      return null
    }

    // 计算 ID3 标签总大小
    const tagSize = ((initBytes[6] & 0x7f) << 21) |
                    ((initBytes[7] & 0x7f) << 14) |
                    ((initBytes[8] & 0x7f) << 7) |
                    (initBytes[9] & 0x7f)

    const totalRequired = tagSize + 10

    let buffer: ArrayBuffer
    if (initBytes.length >= totalRequired) {
      buffer = initBuffer
    } else {
      const response = await fetch(url, {
        headers: {
          Range: `bytes=0-${totalRequired - 1}`
        }
      })
      if (!response.ok) return null
      buffer = await response.arrayBuffer()
    }

    return parseID3Lyrics(buffer)
  } catch (e) {
    console.error('读取 MP3 歌词失败:', e)
    return null
  }
}

function parseID3Lyrics(buffer: ArrayBuffer): string | null {
  const bytes = new Uint8Array(buffer)
  const view = new DataView(buffer)

  const majorVersion = bytes[3]
  const flags = bytes[5]
  const tagSize = ((bytes[6] & 0x7f) << 21) |
                  ((bytes[7] & 0x7f) << 14) |
                  ((bytes[8] & 0x7f) << 7) |
                  (bytes[9] & 0x7f)

  let offset = 10

  if ((flags & 0x40) !== 0) {
    if (offset + 4 > bytes.length) return null
    let extHeaderSize = 0
    if (majorVersion === 3) {
      extHeaderSize = view.getUint32(offset)
      offset += 4 + extHeaderSize
    } else {
      extHeaderSize = ((bytes[offset] & 0x7f) << 21) |
                      ((bytes[offset + 1] & 0x7f) << 14) |
                      ((bytes[offset + 2] & 0x7f) << 7) |
                      (bytes[offset + 3] & 0x7f)
      offset += extHeaderSize
    }
  }

  while (offset + 10 < bytes.length && offset < tagSize + 10) {
    const frameId = String.fromCharCode(bytes[offset], bytes[offset + 1], bytes[offset + 2], bytes[offset + 3])
    if (bytes[offset] === 0 && bytes[offset + 1] === 0 && bytes[offset + 2] === 0 && bytes[offset + 3] === 0) {
      break
    }

    let frameSize = 0
    if (majorVersion === 3) {
      frameSize = view.getUint32(offset + 4)
    } else {
      frameSize = ((bytes[offset + 4] & 0x7f) << 21) |
                  ((bytes[offset + 5] & 0x7f) << 14) |
                  ((bytes[offset + 6] & 0x7f) << 7) |
                  (bytes[offset + 7] & 0x7f)
    }

    offset += 10

    if (frameId === 'USLT') {
      if (offset + frameSize > bytes.length) return null
      const encoding = bytes[offset]

      // USLT 帧格式: [encoding] (1B) + [language] (3B) + [descriptor] (terminated) + [lyrics]
      let descOffset = offset + 4
      if (encoding === 0 || encoding === 3) {
        // 单字节终止符 (0x00)
        while (descOffset < offset + frameSize && bytes[descOffset] !== 0) {
          descOffset++
        }
        descOffset += 1
      } else {
        // 双字节终止符 (0x00 0x00)
        while (descOffset + 1 < offset + frameSize && (bytes[descOffset] !== 0 || bytes[descOffset + 1] !== 0)) {
          descOffset += 2
        }
        descOffset += 2
      }

      if (descOffset >= offset + frameSize) return null
      const lyricsBytes = bytes.slice(descOffset, offset + frameSize)
      return decodeText(lyricsBytes, encoding)
    }

    offset += frameSize
  }

  return null
}

/**
 * FLAC 歌词解析 (读取 Vorbis Comment 块)
 */
export async function getFlacLyrics(url: string): Promise<string | null> {
  try {
    const initResponse = await fetch(url, {
      headers: {
        Range: 'bytes=0-8191'
      }
    })

    if (!initResponse.ok) return null
    let buffer = await initResponse.arrayBuffer()
    let bytes = new Uint8Array(buffer)

    if (bytes.length < 4 || bytes[0] !== 0x66 || bytes[1] !== 0x4c || bytes[2] !== 0x61 || bytes[3] !== 0x43) {
      return null
    }

    let offset = 4
    let isLastBlock = false

    while (offset + 4 <= bytes.length && !isLastBlock) {
      const headerByte = bytes[offset]
      isLastBlock = (headerByte & 0x80) !== 0
      const blockType = headerByte & 0x7f
      const blockLength = (bytes[offset + 1] << 16) | (bytes[offset + 2] << 8) | bytes[offset + 3]

      const blockDataStart = offset + 4
      const blockDataEnd = blockDataStart + blockLength

      if (blockDataEnd > bytes.length) {
        if (bytes.length >= blockDataEnd) {
          break
        }
        const newResponse = await fetch(url, {
          headers: {
            Range: `bytes=0-${blockDataEnd - 1}`
          }
        })
        if (!newResponse.ok) return null
        buffer = await newResponse.arrayBuffer()
        bytes = new Uint8Array(buffer)
      }

      if (blockType === 4) {
        // Vorbis Comment
        return parseVorbisCommentLyrics(buffer, blockDataStart, blockLength)
      }

      offset = blockDataEnd
    }

    return null
  } catch (e) {
    console.error('读取 FLAC 歌词失败:', e)
    return null
  }
}

function parseVorbisCommentLyrics(buffer: ArrayBuffer, offset: number, blockLength: number): string | null {
  const bytes = new Uint8Array(buffer)
  const dataView = new DataView(buffer, offset, blockLength)

  try {
    let localOffset = 0

    // 1. Vendor Length (32-bit LE)
    if (localOffset + 4 > blockLength) return null
    const vendorLength = dataView.getUint32(localOffset, true)
    localOffset += 4 + vendorLength

    // 2. User Comment List Length (32-bit LE)
    if (localOffset + 4 > blockLength) return null
    const userCommentListLength = dataView.getUint32(localOffset, true)
    localOffset += 4

    for (let i = 0; i < userCommentListLength; i++) {
      if (localOffset + 4 > blockLength) return null
      const commentLength = dataView.getUint32(localOffset, true)
      localOffset += 4

      if (localOffset + commentLength > blockLength) return null
      const commentBytes = bytes.slice(offset + localOffset, offset + localOffset + commentLength)
      localOffset += commentLength

      const comment = new TextDecoder('utf-8').decode(commentBytes)
      const eqIdx = comment.indexOf('=')
      if (eqIdx !== -1) {
        const fieldName = comment.slice(0, eqIdx).toUpperCase()
        const fieldValue = comment.slice(eqIdx + 1)
        if (fieldName === 'LYRICS' || fieldName === 'UNSYNCEDLYRICS' || fieldName === 'LYRIC') {
          return fieldValue
        }
      }
    }
  } catch (err) {
    console.error('解析 Vorbis Comment 歌词失败:', err)
  }

  return null
}

function decodeText(bytes: Uint8Array, encoding: number): string {
  let charset = 'utf-8'
  if (encoding === 1) charset = 'utf-16le'
  if (encoding === 2) charset = 'utf-16be'
  if (encoding === 3) charset = 'utf-8'
  if (encoding === 0) charset = 'iso-8859-1'
  try {
    const decoder = new TextDecoder(charset)
    return decoder.decode(bytes)
  } catch (e) {
    return String.fromCharCode(...bytes)
  }
}

