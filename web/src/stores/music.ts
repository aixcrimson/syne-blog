import { defineStore } from 'pinia'
import { ref, computed, watch, nextTick } from 'vue'
import { getAudioCover, getAudioLyrics } from '@/utils/id3'
import { parseLyrics, type LyricLine } from '@/utils/lyrics'
import defaultCover from '@/assets/images/default-cover.svg'

export interface Song {
  id: number
  title: string
  artist: string
  url: string
  cover: string
}

export const useMusicStore = defineStore('music', () => {
  // 内置歌曲列表
  const songs = ref<Song[]>([
    {
      id: 0,
      title: '知我',
      artist: '国风堂 / 哦漏',
      url: '/audio/知我 - 国风堂_哦漏.mp3',
      cover: defaultCover
    },
    {
      id: 1,
      title: '高橋李依-キセキ',
      artist: '高橋李依',
      url: '/audio/高橋李依-キセキ.mp3',
      cover: defaultCover
    },
    {
      id: 2,
      title: '小さな恋のうた',
      artist: '高橋李依',
      url: '/audio/高橋李依-小さな恋のうた.mp3',
      cover: defaultCover
    },
    {
      id: 3,
      title: '心做し',
      artist: 'GUMI',
      url: '/audio/GUMI - 心做し.mp3',
      cover: defaultCover
    },
    {
      id: 4,
      title: 'RADWIMPS-スパークル (Movie ver.)',
      artist: 'RADWIMPS',
      url: '/audio/RADWIMPS-スパークル (Movie ver.).mp3',
      cover: defaultCover
    },
    {
      id: 5,
      title: '星降る海',
      artist: 'Aqu3ra&月見ヤチヨ',
      url: '/audio/Aqu3ra&月見ヤチヨ-星降る海.mp3',
      cover: defaultCover
    },
    {
      id: 6,
      title: 'ray',
      artist: '日本群星&夏吉ゆうこ&早見沙織',
      url: '/audio/日本群星&夏吉ゆうこ&早見沙織-ray (超かぐや姫！ Version).mp3',
      cover: defaultCover
    },
    {
      id: 7,
      title: '星座になれたら',
      artist: '結束バンド',
      url: '/audio/結束バンド-星座になれたら.mp3',
      cover: defaultCover
    },
    {
      id: 8,
      title: '転がる岩、君に朝が降る',
      artist: '結束バンド',
      url: '/audio/結束バンド-転がる岩、君に朝が降る.mp3',
      cover: defaultCover
    },
    {
      id: 9,
      title: '斜陽',
      artist: 'ヨルシカ',
      url: '/audio/ヨルシカ-斜陽.mp3',
      cover: defaultCover
    },
    {
      id: 10,
      title: '花に亡霊',
      artist: 'ヨルシカ',
      url: '/audio/ヨルシカ-花に亡霊.mp3',
      cover: defaultCover
    },
    {
      id: 11,
      title: '都落ち',
      artist: 'ヨルシカ',
      url: '/audio/ヨルシカ-都落ち.mp3',
      cover: defaultCover
    }
  ])

  // 状态
  const currentIndex = ref(0)
  const isPlaying = ref(false)
  const volume = ref(50)
  const currentTime = ref(0)
  const duration = ref(0)
  const playMode = ref<'loop' | 'single' | 'shuffle'>('loop')
  const isCollapsed = ref(true)

  // 歌词状态
  const lyrics = ref<LyricLine[]>([])
  const showLyrics = ref(false)
  const showFloatingLyrics = ref(false)

  // 暂存暂停前的歌词显示状态
  const savedShowLyrics = ref(false)
  const savedShowFloatingLyrics = ref(false)
  let isInternalChange = false

  // 计算属性
  const currentSong = computed(() => songs.value[currentIndex.value] || songs.value[0])

  // 初始化（加载本地存储设置）
  const init = () => {
    const savedIndex = localStorage.getItem('music_currentIndex')
    if (savedIndex !== null) {
      const idx = parseInt(savedIndex, 10)
      if (idx >= 0 && idx < songs.value.length) {
        currentIndex.value = idx
      }
    }

    const savedVolume = localStorage.getItem('music_volume')
    if (savedVolume !== null) {
      volume.value = parseInt(savedVolume, 10)
    }

    const savedPlayMode = localStorage.getItem('music_playMode')
    if (savedPlayMode !== null && ['loop', 'single', 'shuffle'].includes(savedPlayMode)) {
      playMode.value = savedPlayMode as 'loop' | 'single' | 'shuffle'
    }

    const savedCollapsed = localStorage.getItem('music_isCollapsed')
    if (savedCollapsed !== null) {
      isCollapsed.value = savedCollapsed === 'true'
    }

    const savedFloatingLyrics = localStorage.getItem('music_showFloatingLyrics')
    if (savedFloatingLyrics !== null) {
      const isTrue = savedFloatingLyrics === 'true'
      savedShowFloatingLyrics.value = isTrue
      if (isPlaying.value) {
        showFloatingLyrics.value = isTrue
      } else {
        showFloatingLyrics.value = false
      }
    }

    // 异步提取歌曲内嵌封面
    void loadEmbeddedCovers()

    // 加载歌词
    void loadLyrics()
  }

  const loadEmbeddedCovers = async () => {
    for (let i = 0; i < songs.value.length; i++) {
      const song = songs.value[i]
      if (song.url.startsWith('/') || song.url.startsWith(window.location.origin)) {
        try {
          const coverUrl = await getAudioCover(song.url)
          if (coverUrl) {
            songs.value[i].cover = coverUrl
          }
        } catch (err) {
          console.error(`读取歌曲 ${song.title} 内嵌封面失败:`, err)
        }
      }
    }
  }

  const loadLyrics = async () => {
    const song = currentSong.value
    lyrics.value = []
    if (!song) return

    try {
      const rawLyrics = await getAudioLyrics(song.url)
      if (rawLyrics) {
        lyrics.value = parseLyrics(rawLyrics)
      }
    } catch (err) {
      console.error(`加载歌曲 ${song.title} 歌词失败:`, err)
    }
  }

  // 监听歌曲切换自动加载歌词
  watch(currentIndex, () => {
    void loadLyrics()
  })

  // 监听 showLyrics 的变化，保存用户手动修改的状态
  watch(showLyrics, (val) => {
    if (!isInternalChange) {
      savedShowLyrics.value = val
    }
  })

  // 监听 showFloatingLyrics 的变化，保存用户手动修改的状态并同步到本地存储
  watch(showFloatingLyrics, (val) => {
    if (!isInternalChange) {
      savedShowFloatingLyrics.value = val
      localStorage.setItem('music_showFloatingLyrics', val.toString())
    }
  })

  // 监听播放状态的变化以自动控制歌词显示/隐藏
  watch(isPlaying, (playing) => {
    isInternalChange = true
    if (!playing) {
      showLyrics.value = false
      showFloatingLyrics.value = false
    } else {
      showLyrics.value = savedShowLyrics.value
      showFloatingLyrics.value = savedShowFloatingLyrics.value
    }
    nextTick(() => {
      isInternalChange = false
    })
  })

  // 动作
  const togglePlay = (forceState?: boolean) => {
    isPlaying.value = forceState !== undefined ? forceState : !isPlaying.value
  }

  const setVolume = (val: number) => {
    volume.value = Math.max(0, Math.min(100, val))
    localStorage.setItem('music_volume', volume.value.toString())
  }

  const setCurrentTime = (time: number) => {
    currentTime.value = time
  }

  const setDuration = (dur: number) => {
    duration.value = dur
  }

  const setPlayMode = (mode: 'loop' | 'single' | 'shuffle') => {
    playMode.value = mode
    localStorage.setItem('music_playMode', mode)
  }

  const selectSong = (index: number) => {
    if (index >= 0 && index < songs.value.length) {
      currentIndex.value = index
      localStorage.setItem('music_currentIndex', index.toString())
      isPlaying.value = true
    }
  }

  const playNext = () => {
    if (songs.value.length <= 1) return

    if (playMode.value === 'shuffle') {
      let nextIndex = currentIndex.value
      while (nextIndex === currentIndex.value) {
        nextIndex = Math.floor(Math.random() * songs.value.length)
      }
      currentIndex.value = nextIndex
    } else {
      currentIndex.value = (currentIndex.value + 1) % songs.value.length
    }
    localStorage.setItem('music_currentIndex', currentIndex.value.toString())
    isPlaying.value = true
  }

  const playPrev = () => {
    if (songs.value.length <= 1) return

    if (playMode.value === 'shuffle') {
      let prevIndex = currentIndex.value
      while (prevIndex === currentIndex.value) {
        prevIndex = Math.floor(Math.random() * songs.value.length)
      }
      currentIndex.value = prevIndex
    } else {
      currentIndex.value = (currentIndex.value - 1 + songs.value.length) % songs.value.length
    }
    localStorage.setItem('music_currentIndex', currentIndex.value.toString())
    isPlaying.value = true
  }

  const toggleCollapse = () => {
    isCollapsed.value = !isCollapsed.value
    localStorage.setItem('music_isCollapsed', isCollapsed.value.toString())
  }

  return {
    songs,
    currentIndex,
    isPlaying,
    volume,
    currentTime,
    duration,
    playMode,
    isCollapsed,
    currentSong,
    lyrics,
    showLyrics,
    showFloatingLyrics,
    init,
    togglePlay,
    setVolume,
    setCurrentTime,
    setDuration,
    setPlayMode,
    selectSong,
    playNext,
    playPrev,
    toggleCollapse,
    loadLyrics
  }
})
