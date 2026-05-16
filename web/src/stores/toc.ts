import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

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

    // 默认折叠所有有子标题的项
    const newCollapsed = new Set<string>()
    for (let i = 0; i < items.length; i++) {
      if (i < items.length - 1 && items[i + 1].level > items[i].level) {
        newCollapsed.add(items[i].id)
      }
    }
    collapsedHeadingIds.value = newCollapsed
  }

  const setActiveHeadingId = (id: string) => {
    activeHeadingId.value = id
    expandToHeading(id)
  }

  const expandToHeading = (id: string) => {
    if (!id) return
    
    const stack: {id: string, level: number}[] = []
    let parents: string[] = []
    
    for (const item of tocItems.value) {
      while (stack.length > 0 && stack[stack.length - 1].level >= item.level) {
        stack.pop()
      }
      stack.push({ id: item.id, level: item.level })
      
      if (item.id === id) {
        stack.pop() // remove the item itself
        parents = stack.map(s => s.id)
        break
      }
    }
    
    if (parents.length > 0) {
      let changed = false
      const newSet = new Set(collapsedHeadingIds.value)
      for (const parentId of parents) {
        if (newSet.has(parentId)) {
          newSet.delete(parentId)
          changed = true
        }
      }
      if (changed) {
        collapsedHeadingIds.value = newSet
      }
    }
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
    collapsedHeadingIds.value.clear()
  }

  // 目录折叠逻辑
  const collapsedHeadingIds = ref<Set<string>>(new Set())

  const toggleHeadingCollapse = (id: string) => {
    const newSet = new Set(collapsedHeadingIds.value)
    if (newSet.has(id)) {
      newSet.delete(id)
    } else {
      newSet.add(id)
    }
    collapsedHeadingIds.value = newSet
  }

  const hasChildrenMap = computed(() => {
    const map = new Map<string, boolean>()
    for (let i = 0; i < tocItems.value.length; i++) {
      const item = tocItems.value[i]
      const hasChild = i < tocItems.value.length - 1 && tocItems.value[i + 1].level > item.level
      map.set(item.id, hasChild)
    }
    return map
  })

  const visibleTocItems = computed(() => {
    const visible = []
    let currentCollapsedLevel = -1
    for (const item of tocItems.value) {
      if (currentCollapsedLevel !== -1) {
        if (item.level > currentCollapsedLevel) {
          continue
        } else {
          currentCollapsedLevel = -1
        }
      }
      
      visible.push(item)
      
      if (collapsedHeadingIds.value.has(item.id)) {
        currentCollapsedLevel = item.level
      }
    }
    return visible
  })

  return {
    tocItems,
    activeHeadingId,
    showToc,
    collapsedHeadingIds,
    hasChildrenMap,
    visibleTocItems,
    setTocItems,
    setActiveHeadingId,
    toggleToc,
    closeToc,
    clear,
    toggleHeadingCollapse
  }
})
