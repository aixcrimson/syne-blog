import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface TocItem {
  id: string
  title: string
  level: number
}

export const useTocStore = defineStore('toc', () => {
  const tocItems = ref<TocItem[]>([])
  const activeHeadingId = ref('')
  const showToc = ref(false)

  const setTocItems = (items: TocItem[]) => {
    tocItems.value = items
  }

  const setActiveHeadingId = (id: string) => {
    activeHeadingId.value = id
  }

  const toggleToc = () => {
    showToc.value = !showToc.value
  }

  const closeToc = () => {
    showToc.value = false
  }

  const clear = () => {
    tocItems.value = []
    activeHeadingId.value = ''
    showToc.value = false
  }

  return {
    tocItems,
    activeHeadingId,
    showToc,
    setTocItems,
    setActiveHeadingId,
    toggleToc,
    closeToc,
    clear
  }
})
