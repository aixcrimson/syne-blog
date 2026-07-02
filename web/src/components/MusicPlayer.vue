<template>
  <Teleport to="body">
    <div ref="playerRef" class="music-player-container fixed left-4 bottom-4 z-50 transition-all duration-300" :class="musicStore.isCollapsed ? 'w-14 h-14' : ''">
      <!-- 隐藏的音频标签 -->
      <audio
        ref="audioRef"
        :src="musicStore.currentSong.url"
        @timeupdate="onTimeUpdate"
        @loadedmetadata="onLoadedMetadata"
        @ended="onEnded"
      ></audio>

      <!-- 1. 折叠状态 (黑胶唱片盘) -->
      <Transition
        enter-active-class="transition-all duration-300 ease-out"
        enter-from-class="opacity-0 scale-75 -translate-x-10"
        enter-to-class="opacity-100 scale-100 translate-x-0"
        leave-active-class="transition-all duration-200 ease-in"
        leave-from-class="opacity-100 scale-100 translate-x-0"
        leave-to-class="opacity-0 scale-75 -translate-x-10"
      >
        <div
          v-if="musicStore.isCollapsed"
          class="group absolute left-0 bottom-0 flex items-center justify-center w-14 h-14 rounded-full cursor-pointer bg-slate-900 border-2 border-slate-700/80 shadow-[0_8px_32px_rgba(15,23,42,0.35)] dark:border-slate-600/80 hover:scale-105 active:scale-95 transition-transform duration-200"
          @click.stop="musicStore.toggleCollapse"
          title="打开音乐播放器"
        >
          <!-- 旋转唱片 -->
          <div
            class="w-full h-full rounded-full flex items-center justify-center animate-spin-slow overflow-hidden"
            :class="{ 'paused-rotation': !musicStore.isPlaying }"
          >
            <img
              :src="musicStore.currentSong.cover"
              alt="cover"
              class="w-8 h-8 rounded-full object-cover select-none"
            />
          </div>
          <!-- 唱片中心黑点 -->
          <div class="absolute w-2.5 h-2.5 rounded-full bg-slate-950 border border-slate-800 shadow-inner"></div>

          <!-- 播放时的音波跳动特效 -->
          <div
            v-if="musicStore.isPlaying"
            class="absolute -top-1 -right-1 flex gap-[2px] items-end justify-center w-5 h-5 bg-primary-500 rounded-full border-2 border-slate-900 shadow-sm px-[3px] py-[4px]"
          >
            <div class="w-[2px] bg-white rounded-full animate-bar-1"></div>
            <div class="w-[2px] bg-white rounded-full animate-bar-2"></div>
            <div class="w-[2px] bg-white rounded-full animate-bar-3"></div>
          </div>
        </div>
      </Transition>

      <!-- 2. 展开状态 (玻璃拟态面板) -->
      <Transition
        enter-active-class="transition-all duration-300 ease-out"
        enter-from-class="opacity-0 scale-90 translate-y-10"
        enter-to-class="opacity-100 scale-100 translate-y-0"
        leave-active-class="transition-all duration-200 ease-in"
        leave-from-class="opacity-100 scale-100 translate-y-0"
        leave-to-class="opacity-0 scale-90 translate-y-10"
      >
        <div
          v-if="!musicStore.isCollapsed"
          class="flex items-end gap-4"
        >
          <!-- 播放器面板 -->
          <div class="music-player-panel w-80 flex-shrink-0 rounded-2xl border border-slate-200/40 bg-white/60 shadow-2xl backdrop-blur-xl dark:border-slate-800/50 dark:bg-slate-950/60 overflow-hidden flex flex-col">
          <!-- 面板头部 -->
          <div class="flex items-center justify-between px-4 py-3 border-b border-slate-200/30 dark:border-slate-800/30">
            <span class="text-xs font-bold text-slate-500 tracking-wider dark:text-slate-400">MUSIC BOX</span>
            <div class="flex items-center gap-2">
              <!-- 桌面歌词按钮 -->
              <button
                class="p-1 rounded-lg text-slate-500 hover:bg-slate-100 dark:text-slate-400 dark:hover:bg-slate-800"
                :class="{ 'text-primary-500 dark:text-primary-400': musicStore.showFloatingLyrics }"
                @click="musicStore.showFloatingLyrics = !musicStore.showFloatingLyrics"
                title="桌面歌词"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M7 4h10M5 20h14m-12-8h10M5 8h14a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2z" />
                </svg>
              </button>
              <!-- 歌词按钮 -->
              <button
                class="p-1 rounded-lg text-slate-500 hover:bg-slate-100 dark:text-slate-400 dark:hover:bg-slate-800"
                :class="{ 'text-primary-500 dark:text-primary-400': musicStore.showLyrics }"
                @click="musicStore.showLyrics = !musicStore.showLyrics"
                title="歌词面板"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
              </button>
              <!-- 切换歌单按钮 -->
              <button
                class="p-1 rounded-lg text-slate-500 hover:bg-slate-100 dark:text-slate-400 dark:hover:bg-slate-800"
                :class="{ 'text-primary-500 dark:text-primary-400': showPlaylist }"
                @click="showPlaylist = !showPlaylist"
                title="播放列表"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 12h16M4 18h7" />
                </svg>
              </button>
              <!-- 最小化按钮 -->
              <button
                class="p-1 rounded-lg text-slate-500 hover:bg-slate-100 dark:text-slate-400 dark:hover:bg-slate-800"
                @click="musicStore.toggleCollapse"
                title="最小化"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 8.25l-7.5 7.5-7.5-7.5" />
                </svg>
              </button>
            </div>
          </div>


          <!-- 主内容区 -->
          <div class="p-4 flex flex-col gap-4">
            <!-- 歌曲卡片 -->
            <div class="flex items-center gap-3">
              <!-- 大黑胶唱片 -->
              <div
                class="relative w-16 h-16 rounded-full border-4 border-slate-900 bg-slate-800 shadow-md flex items-center justify-center overflow-hidden animate-spin-slow flex-shrink-0"
                :class="{ 'paused-rotation': !musicStore.isPlaying }"
              >
                <img
                  :src="musicStore.currentSong.cover"
                  alt="cover"
                  class="w-10 h-10 rounded-full object-cover select-none"
                />
                <!-- 唱片针轴 -->
                <div class="absolute w-2 h-2 rounded-full bg-slate-950 border border-slate-800 shadow-inner"></div>
              </div>

              <!-- 歌名与艺术家 -->
              <div class="min-w-0 flex-1">
                <h4 class="text-sm font-semibold text-slate-800 dark:text-slate-100 truncate mb-0.5">
                  {{ musicStore.currentSong.title }}
                </h4>
                <p class="text-xs text-slate-500 dark:text-slate-450 truncate">
                  {{ musicStore.currentSong.artist }}
                </p>
              </div>
            </div>

            <!-- 进度条 -->
            <div class="flex flex-col gap-1.5">
              <input
                type="range"
                class="progress-slider w-full h-1 rounded-lg appearance-none cursor-pointer focus:outline-none"
                :style="{
                  background: `linear-gradient(to right, var(--color-primary-600, #2563eb) ${progressPercent}%, rgba(148, 163, 184, 0.2) ${progressPercent}%)`
                }"
                min="0"
                :max="musicStore.duration || 100"
                v-model="seekValue"
                @input="onSeekInput"
                @change="onSeekChange"
              />
              <div class="flex justify-between text-[10px] text-slate-400 dark:text-slate-550 font-mono">
                <span>{{ formatTime(musicStore.currentTime) }}</span>
                <span>{{ formatTime(musicStore.duration) }}</span>
              </div>
            </div>

            <!-- 控制栏 -->
            <div class="flex items-center justify-between">
              <!-- 播放模式切换 -->
              <button
                class="p-1.5 rounded-lg text-slate-500 hover:bg-slate-100 hover:text-slate-800 dark:text-slate-400 dark:hover:bg-slate-800 dark:hover:text-slate-200"
                @click="togglePlayMode"
                :title="playModeTitle"
              >
                <!-- 列表循环 -->
                <svg v-if="musicStore.playMode === 'loop'" class="w-4.5 h-4.5" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 12c0-1.232-.046-2.453-.138-3.662a4.006 4.006 0 00-3.7-3.7 48.656 48.656 0 00-7.324 0 4.006 4.006 0 00-3.7 3.7c-.017.22-.032.441-.046.662M19.5 12l3-3m-3 3l-3-3m-12 3c0 1.232.046 2.453.138 3.662a4.006 4.006 0 003.7 3.7 48.656 48.656 0 007.324 0 4.006 4.006 0 003.7-3.7c.017-.22.032-.441.046-.662M4.5 12l3 3m-3-3l-3 3" />
                </svg>
                <!-- 单曲循环 -->
                <svg v-else-if="musicStore.playMode === 'single'" class="w-4.5 h-4.5" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 12c0-1.232-.046-2.453-.138-3.662a4.006 4.006 0 00-3.7-3.7 48.656 48.656 0 00-7.324 0 4.006 4.006 0 00-3.7 3.7c-.017.22-.032.441-.046.662M19.5 12l3-3m-3 3l-3-3m-12 3c0 1.232.046 2.453.138 3.662a4.006 4.006 0 003.7 3.7 48.656 48.656 0 007.324 0 4.006 4.006 0 003.7-3.7c.017-.22.032-.441.046-.662M4.5 12l3 3m-3-3l-3 3" />
                  <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v6m-1-5.5h1" />
                </svg>
                <!-- 随机播放 -->
                <svg v-else class="w-4.5 h-4.5" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 12c0-1.232-.046-2.453-.138-3.662a4.006 4.006 0 00-3.7-3.7 48.656 48.656 0 00-7.324 0 4.006 4.006 0 00-3.7 3.7c-.017.22-.032.441-.046.662M19.5 12l3-3m-3 3l-3-3m-12 3c0 1.232.046 2.453.138 3.662a4.006 4.006 0 003.7 3.7 48.656 48.656 0 007.324 0 4.006 4.006 0 003.7-3.7c.017-.22.032-.441.046-.662M4.5 12l3 3m-3-3l-3 3" />
                  <path stroke-linecap="round" stroke-linejoin="round" d="M8 8l8 8M16 8l-8 8" />
                </svg>
              </button>

              <!-- 切歌与播放 -->
              <div class="flex items-center gap-3">
                <button
                  class="p-1.5 text-slate-600 hover:text-slate-900 dark:text-slate-400 dark:hover:text-slate-100 hover:scale-105 active:scale-95 transition-transform"
                  @click="musicStore.playPrev"
                  title="上一首"
                >
                  <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M6 6h2v12H6zm3.5 6l8.5 6V6z"/>
                  </svg>
                </button>
                <button
                  class="w-10 h-10 rounded-full bg-primary-600 text-white flex items-center justify-center shadow hover:scale-105 hover:bg-primary-500 active:scale-95 transition-all"
                  @click="musicStore.togglePlay()"
                  :title="musicStore.isPlaying ? '暂停' : '播放'"
                >
                  <svg v-if="!musicStore.isPlaying" class="w-5 h-5 fill-current translate-x-[1px]" viewBox="0 0 24 24">
                    <path d="M8 5v14l11-7z"/>
                  </svg>
                  <svg v-else class="w-5 h-5 fill-current" viewBox="0 0 24 24">
                    <path d="M6 19h4V5H6v14zm8-14v14h4V5h-4z"/>
                  </svg>
                </button>
                <button
                  class="p-1.5 text-slate-600 hover:text-slate-900 dark:text-slate-400 dark:hover:text-slate-100 hover:scale-105 active:scale-95 transition-transform"
                  @click="musicStore.playNext"
                  title="下一首"
                >
                  <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M6 18l8.5-6L6 6zm9-12h2v12h-2z"/>
                  </svg>
                </button>
              </div>

              <!-- 音量控制 -->
              <div class="relative group/vol flex items-center gap-1.5">
                <button
                  class="p-1.5 rounded-lg text-slate-500 hover:bg-slate-100 dark:text-slate-400 dark:hover:bg-slate-800"
                  @click="toggleMute"
                  :title="musicStore.volume === 0 ? '取消静音' : '静音'"
                >
                  <!-- 喇叭图标 -->
                  <svg v-if="musicStore.volume > 50" class="w-4.5 h-4.5" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M19.114 5.636a9 9 0 010 12.728M16.463 8.288a5.25 5.25 0 010 7.424M6.75 8.25l4.72-4.72a.75.75 0 011.28.53v15.88a.75.75 0 01-1.28.53l-4.72-4.72H4.51c-.88 0-1.704-.507-1.938-1.354A9.01 9.01 0 012.25 12c0-.83.112-1.633.322-2.396C2.806 8.756 3.63 8.25 4.51 8.25H6.75z" />
                  </svg>
                  <svg v-else-if="musicStore.volume > 0" class="w-4.5 h-4.5" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M16.463 8.288a5.25 5.25 0 010 7.424M6.75 8.25l4.72-4.72a.75.75 0 011.28.53v15.88a.75.75 0 01-1.28.53l-4.72-4.72H4.51c-.88 0-1.704-.507-1.938-1.354A9.01 9.01 0 012.25 12c0-.83.112-1.633.322-2.396C2.806 8.756 3.63 8.25 4.51 8.25H6.75z" />
                  </svg>
                  <svg v-else class="w-4.5 h-4.5" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M17.25 9.75L19.5 12m0 0l2.25 2.25M19.5 12l2.25-2.25M19.5 12l-2.25 2.25m-10.5-6l4.72-4.72a.75.75 0 011.28.53v15.88a.75.75 0 01-1.28.53l-4.72-4.72H4.51c-.88 0-1.704-.507-1.938-1.354A9.01 9.01 0 012.25 12c0-.83.112-1.633.322-2.396C2.806 8.756 3.63 8.25 4.51 8.25H6.75z" />
                  </svg>
                </button>
                <!-- 音量悬浮滑块 -->
                <div class="w-0 overflow-hidden group-hover/vol:w-20 transition-all duration-300 flex items-center">
                  <input
                    type="range"
                    class="volume-slider w-16 h-1 rounded-lg appearance-none cursor-pointer focus:outline-none ml-1"
                    :style="{
                      background: `linear-gradient(to right, var(--color-primary-600, #2563eb) ${musicStore.volume}%, rgba(148, 163, 184, 0.2) ${musicStore.volume}%)`
                    }"
                    min="0"
                    max="100"
                    v-model="volumeValue"
                    @input="onVolumeInput"
                  />
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 播放列表面板 -->
        <Transition
          enter-active-class="transition-all duration-300 ease-out"
          enter-from-class="opacity-0 scale-95 translate-x-4"
          enter-to-class="opacity-100 scale-100 translate-x-0"
          leave-active-class="transition-all duration-200 ease-in"
          leave-from-class="opacity-100 scale-100 translate-x-0"
          leave-to-class="opacity-0 scale-95 translate-x-4"
        >
          <div
            v-if="showPlaylist"
            class="music-player-panel w-80 flex-shrink-0 h-[26rem] rounded-2xl border border-slate-200/40 bg-white/60 shadow-2xl backdrop-blur-xl dark:border-slate-800/50 dark:bg-slate-950/60 overflow-hidden flex flex-col"
          >
            <!-- 播放列表头部 -->
            <div class="flex items-center justify-between px-4 py-3 border-b border-slate-200/30 dark:border-slate-800/30 flex-shrink-0">
              <span class="text-xs font-bold text-slate-500 tracking-wider dark:text-slate-400">播放列表 PLAYLIST</span>
              <button
                class="p-1 rounded-lg text-slate-500 hover:bg-slate-100 dark:text-slate-400 dark:hover:bg-slate-800"
                @click="showPlaylist = false"
                title="关闭播放列表"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>

            <!-- 播放列表内容滚动区 -->
            <div class="flex-1 overflow-y-auto py-2 scrollbar-none">
              <div
                v-for="(song, index) in musicStore.songs"
                :key="song.id"
                class="flex items-center justify-between px-4 py-3 text-xs cursor-pointer transition-colors hover:bg-slate-100/60 dark:hover:bg-slate-800/60"
                :class="musicStore.currentIndex === index ? 'text-primary-600 dark:text-primary-400 font-semibold bg-primary-50/30 dark:bg-primary-950/10' : 'text-slate-650 dark:text-slate-300'"
                @click="musicStore.selectSong(index)"
              >
                <div class="flex items-center gap-3 truncate pr-4">
                  <span class="font-mono text-slate-400 text-[10px]">{{ String(index + 1).padStart(2, '0') }}</span>
                  <div class="flex flex-col truncate gap-0.5">
                    <span class="truncate text-[13px] font-medium" :class="musicStore.currentIndex === index ? 'text-primary-600 dark:text-primary-400' : 'text-slate-700 dark:text-slate-200'">{{ song.title }}</span>
                    <span class="text-[10px] text-slate-400 truncate">{{ song.artist }}</span>
                  </div>
                </div>
                <div v-if="musicStore.currentIndex === index && musicStore.isPlaying" class="flex gap-[2px] items-end h-3 flex-shrink-0">
                  <div class="w-[2px] h-3 bg-primary-500 rounded-full animate-bar-1"></div>
                  <div class="w-[2px] h-2 bg-primary-500 rounded-full animate-bar-2"></div>
                  <div class="w-[2px] h-3 bg-primary-500 rounded-full animate-bar-3"></div>
                </div>
              </div>
            </div>
          </div>
        </Transition>

          <!-- 歌词面板 -->
          <Transition
            enter-active-class="transition-all duration-300 ease-out"
            enter-from-class="opacity-0 scale-95 translate-x-4"
            enter-to-class="opacity-100 scale-100 translate-x-0"
            leave-active-class="transition-all duration-200 ease-in"
            leave-from-class="opacity-100 scale-100 translate-x-0"
            leave-to-class="opacity-0 scale-95 translate-x-4"
          >
            <div
              v-if="musicStore.showLyrics"
              class="music-player-panel w-80 flex-shrink-0 h-[26rem] rounded-2xl border border-slate-200/40 bg-white/60 shadow-2xl backdrop-blur-xl dark:border-slate-800/50 dark:bg-slate-950/60 overflow-hidden flex flex-col"
            >
              <!-- 歌词面板头部 -->
              <div class="flex items-center justify-between px-4 py-3 border-b border-slate-200/30 dark:border-slate-800/30 flex-shrink-0">
                <span class="text-xs font-bold text-slate-500 tracking-wider dark:text-slate-400">歌词 LYRICS</span>
                <button
                  class="p-1 rounded-lg text-slate-500 hover:bg-slate-100 dark:text-slate-400 dark:hover:bg-slate-800"
                  @click="musicStore.showLyrics = false"
                  title="关闭歌词"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>

              <!-- 歌词内容滚动区 -->
              <div
                ref="lyricsContainerRef"
                class="flex-1 overflow-y-auto px-4 py-6 scrollbar-none flex flex-col items-center gap-4 text-center select-none"
              >
                <template v-if="musicStore.lyrics.length > 0">
                  <div
                    v-for="(line, index) in musicStore.lyrics"
                    :key="index"
                    :ref="el => setLyricRef(el, index)"
                    class="lyric-line transition-all duration-300 text-sm font-medium leading-relaxed max-w-full px-2"
                    :class="[
                      index === currentLyricIndex
                        ? 'text-primary-600 dark:text-primary-400 font-bold scale-105 text-[15px]'
                        : 'text-slate-500/80 dark:text-slate-400/80 hover:text-slate-800 dark:hover:text-slate-200'
                    ]"
                  >
                    {{ line.text }}
                  </div>
                </template>
                <div v-else class="flex-1 flex flex-col items-center justify-center text-slate-400 dark:text-slate-500 gap-2">
                  <svg class="w-8 h-8 opacity-50" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M9 19V6l12-3v13M9 19c0 1.105-1.343 2-3 2s-3-.895-3-2 1.343-2 3-2 3 .895 3 2zm12-3c0 1.105-1.343 2-3 2s-3-.895-3-2 1.343-2 3-2 3 .895 3 2zM9 10l12-3" />
                  </svg>
                  <span class="text-xs">暂无歌词</span>
                </div>
              </div>
            </div>
          </Transition>
        </div>
      </Transition>
    </div>

    <!-- 桌面悬浮歌词 -->
    <Transition
      enter-active-class="transition-all duration-300 ease-out"
      enter-from-class="opacity-0 translate-y-10 scale-95"
      enter-to-class="opacity-100 translate-y-0 scale-100"
      leave-active-class="transition-all duration-200 ease-in"
      leave-from-class="opacity-100 translate-y-0 scale-100"
      leave-to-class="opacity-0 translate-y-10 scale-95"
    >
      <div
        v-if="musicStore.showFloatingLyrics"
        class="fixed left-1/2 bottom-6 sm:bottom-10 -translate-x-1/2 z-[9999] px-4 py-1.5 sm:px-8 sm:py-2.5 max-w-[90vw] sm:max-w-[80vw] md:max-w-[70vw] text-center pointer-events-none select-none flex flex-col gap-1 items-center transition-all duration-300"
      >
        <!-- 原文歌词 (支持卡拉OK染色动效) -->
        <div 
          v-if="currentLyricLine"
          class="karaoke-text text-sm sm:text-base md:text-lg font-extrabold tracking-wide drop-shadow-[0_2px_3px_rgba(255,255,255,0.9)] dark:drop-shadow-[0_2px_4px_rgba(0,0,0,0.95)]"
          :style="{ '--lyric-progress': `${currentLineProgress}%` }"
        >
          {{ currentLyricLine.text }}
        </div>
        <!-- 翻译歌词 -->
        <div 
          v-if="currentLyricLine && currentLyricLine.translation"
          class="text-[10px] sm:text-xs md:text-sm font-semibold text-slate-600 dark:text-slate-350 drop-shadow-[0_1.5px_2px_rgba(255,255,255,0.9)] dark:drop-shadow-[0_1.5px_3px_rgba(0,0,0,0.95)] mt-0.5"
        >
          {{ currentLyricLine.translation }}
        </div>
        <!-- 无歌词或未播放时的占位 -->
        <div 
          v-if="!currentLyricLine"
          class="text-xs sm:text-sm font-semibold text-slate-500 dark:text-slate-450 drop-shadow-[0_1.5px_2px_rgba(255,255,255,0.8)] dark:drop-shadow-[0_1.5px_3px_rgba(0,0,0,0.9)]"
        >
          {{ musicStore.isPlaying ? '暂无歌词' : '音乐暂停中' }}
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue'
import { useMusicStore } from '@/stores/music'

const musicStore = useMusicStore()
const audioRef = ref<HTMLAudioElement | null>(null)
const showPlaylist = ref(false)

const playerRef = ref<HTMLElement | null>(null)

const onClickOutside = (event: MouseEvent) => {
  if (musicStore.isCollapsed) return
  const target = event.target as HTMLElement
  
  // 检查是否点击了播放器面板、播放列表或歌词面板等实际的面板元素
  const clickedInsidePanel = target.closest('.music-player-panel')
  
  if (clickedInsidePanel || !document.body.contains(target)) {
    return
  }
  
  musicStore.toggleCollapse()
}

// 歌词滚动相关
const lyricsContainerRef = ref<HTMLElement | null>(null)
const lyricRefs = ref<HTMLElement[]>([])

const setLyricRef = (el: any, index: number) => {
  if (el) {
    lyricRefs.value[index] = el
  }
}

// 计算当前播放对应的歌词行索引
const currentLyricIndex = computed(() => {
  const time = musicStore.currentTime
  const lyrics = musicStore.lyrics
  if (!lyrics || lyrics.length === 0) return -1
  
  // 如果是没有时间戳的纯文本歌词，不进行滚动高亮
  if (lyrics.every(l => l.time < 0)) return -1

  let activeIndex = -1
  for (let i = 0; i < lyrics.length; i++) {
    if (lyrics[i].time <= time) {
      activeIndex = i
    } else {
      break
    }
  }
  return activeIndex
})

// 计算当前歌词行对象
const currentLyricLine = computed(() => {
  const index = currentLyricIndex.value
  const lyrics = musicStore.lyrics
  if (index === -1 || !lyrics || lyrics.length === 0) return null
  return lyrics[index]
})

// 计算当前行歌词播放的进度百分比（用于卡拉OK式文字染色）
const currentLineProgress = computed(() => {
  const index = currentLyricIndex.value
  const lyrics = musicStore.lyrics
  if (index === -1 || !lyrics || lyrics.length === 0) return 0
  
  const currentLine = lyrics[index]
  if (currentLine.time < 0) return 0
  
  let duration = 5 // 默认行长为 5 秒
  if (index < lyrics.length - 1) {
    duration = lyrics[index + 1].time - currentLine.time
  } else if (musicStore.duration > currentLine.time) {
    duration = musicStore.duration - currentLine.time
  }
  
  if (duration <= 0) return 100
  
  const elapsed = musicStore.currentTime - currentLine.time
  const progress = Math.max(0, Math.min(100, (elapsed / duration) * 100))
  return progress
})

// 监听当前歌词行变化，并平滑滚动到容器中间
watch(currentLyricIndex, (newIndex) => {
  if (newIndex !== -1 && lyricRefs.value[newIndex] && lyricsContainerRef.value) {
    const activeEl = lyricRefs.value[newIndex]
    const container = lyricsContainerRef.value
    
    container.scrollTo({
      top: activeEl.offsetTop - container.clientHeight / 2 + activeEl.clientHeight / 2,
      behavior: 'smooth'
    })
  }
})

// 当歌词列表发生变化（如切换歌曲）时，清空行引用并重置滚动位置到顶部
watch(() => musicStore.lyrics, () => {
  lyricRefs.value = []
  nextTick(() => {
    if (lyricsContainerRef.value) {
      lyricsContainerRef.value.scrollTop = 0
    }
  })
})

const seekValue = ref(0)
const volumeValue = ref(50)
const lastVolume = ref(50)
const isSeeking = ref(false)

// 播放模式提示文本
const playModeTitle = computed(() => {
  const modes = {
    loop: '列表循环',
    single: '单曲循环',
    shuffle: '随机播放'
  }
  return modes[musicStore.playMode]
})

// 计算播放进度百分比，用于进度条高亮
const progressPercent = computed(() => {
  if (!musicStore.duration) return 0
  return (seekValue.value / musicStore.duration) * 100
})

// 初始化与绑定
onMounted(() => {
  musicStore.init()
  volumeValue.value = musicStore.volume
  lastVolume.value = musicStore.volume > 0 ? musicStore.volume : 50

  if (audioRef.value) {
    audioRef.value.volume = musicStore.volume / 100
  }

  document.addEventListener('click', onClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', onClickOutside)
})

// 统一监听歌曲 URL 与播放状态的变化，避免并发冲突
watch(
  [() => musicStore.currentSong.url, () => musicStore.isPlaying],
  async ([newUrl, isPlaying], [oldUrl, oldIsPlaying]) => {
    if (!audioRef.value) return

    // 等待 Vue DOM 更新以确保 audio 标签的 :src 已经更新为最新的 URL
    await nextTick()

    // 1. 如果歌曲 URL 发生了变化 (切歌)
    if (newUrl !== oldUrl) {
      audioRef.value.load()
      if (isPlaying) {
        try {
          await audioRef.value.play()
        } catch (err: any) {
          console.warn('切歌播放失败:', err)
          if (err.name !== 'AbortError') {
            musicStore.togglePlay(false)
          }
        }
      }
    } 
    // 2. 如果歌曲 URL 没变，但播放状态发生了变化 (播放/暂停)
    else if (isPlaying !== oldIsPlaying) {
      if (isPlaying) {
        try {
          await audioRef.value.play()
        } catch (err: any) {
          console.warn('播放状态切换失败:', err)
          if (err.name !== 'AbortError') {
            musicStore.togglePlay(false)
          }
        }
      } else {
        audioRef.value.pause()
      }
    }
  }
)

// 监听音量改变
watch(() => musicStore.volume, (val) => {
  if (audioRef.value) {
    audioRef.value.volume = val / 100
  }
  volumeValue.value = val
})

// 音频事件回调
const onTimeUpdate = () => {
  if (audioRef.value) {
    musicStore.setCurrentTime(audioRef.value.currentTime)
    if (!isSeeking.value) {
      seekValue.value = audioRef.value.currentTime
    }
  }
}

const onLoadedMetadata = () => {
  if (audioRef.value) {
    musicStore.setDuration(audioRef.value.duration)
  }
}

const onEnded = () => {
  if (musicStore.playMode === 'single') {
    if (audioRef.value) {
      audioRef.value.currentTime = 0
      audioRef.value.play().catch(() => musicStore.togglePlay(false))
    }
  } else {
    musicStore.playNext()
  }
}

// 拖拽进度控制
const onSeekInput = () => {
  isSeeking.value = true
}

const onSeekChange = (e: Event) => {
  const val = Number((e.target as HTMLInputElement).value)
  if (audioRef.value) {
    audioRef.value.currentTime = val
    musicStore.setCurrentTime(val)
  }
  isSeeking.value = false
}

// 音量滑动调节
const onVolumeInput = () => {
  musicStore.setVolume(Number(volumeValue.value))
}

// 切换静音
const toggleMute = () => {
  if (musicStore.volume > 0) {
    lastVolume.value = musicStore.volume
    musicStore.setVolume(0)
  } else {
    musicStore.setVolume(lastVolume.value)
  }
}

// 切换播放模式
const togglePlayMode = () => {
  const modes: ('loop' | 'single' | 'shuffle')[] = ['loop', 'single', 'shuffle']
  const nextIdx = (modes.indexOf(musicStore.playMode) + 1) % modes.length
  musicStore.setPlayMode(modes[nextIdx])
}

// 辅助方法：时间格式化 (00:00)
const formatTime = (seconds: number) => {
  if (isNaN(seconds) || seconds === 0) return '00:00'
  const m = Math.floor(seconds / 60)
  const s = Math.floor(seconds % 60)
  return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
}
</script>

<style scoped>
/* 慢速旋转 */
@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.animate-spin-slow {
  animation: spin 20s linear infinite;
}

.paused-rotation {
  animation-play-state: paused;
}

/* 进度条与音量条样式美化 */
.progress-slider, .volume-slider {
  outline: none;
}

.progress-slider::-webkit-slider-runnable-track,
.volume-slider::-webkit-slider-runnable-track {
  height: 4px;
}

.progress-slider::-webkit-slider-thumb,
.volume-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: var(--color-primary-600, #2563eb);
  cursor: pointer;
  margin-top: -3px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
  transition: transform 0.1s;
}

.progress-slider::-webkit-slider-thumb:hover,
.volume-slider::-webkit-slider-thumb:hover {
  transform: scale(1.35);
}

/* 音符跳动特效动画 */
@keyframes bounce-bar {
  0%, 100% {
    height: 4px;
  }
  50% {
    height: 14px;
  }
}

.animate-bar-1 {
  animation: bounce-bar 0.8s ease-in-out infinite;
}

.animate-bar-2 {
  animation: bounce-bar 0.5s ease-in-out infinite 0.15s;
}

.animate-bar-3 {
  animation: bounce-bar 0.7s ease-in-out infinite 0.3s;
}

/* 隐藏歌词滚动条 */
.scrollbar-none::-webkit-scrollbar {
  display: none;
}
.scrollbar-none {
  -ms-overflow-style: none;  /* IE and Edge */
  scrollbar-width: none;  /* Firefox */
}

/* 歌词行动画 */
.lyric-line {
  transition: all 0.3s ease-in-out;
  transform-origin: center;
}

/* 卡拉OK歌词文字平滑染色 */
.karaoke-text {
  background-image: linear-gradient(
    to right,
    var(--color-primary-600, #2563eb) var(--lyric-progress, 0%),
    #475569 var(--lyric-progress, 0%)
  );
  background-size: 100% 100%;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  display: inline-block;
  transition: background-image 0.1s linear;
}

.dark .karaoke-text {
  background-image: linear-gradient(
    to right,
    var(--color-primary-400, #60a5fa) var(--lyric-progress, 0%),
    #cbd5e1 var(--lyric-progress, 0%)
  );
}
</style>
