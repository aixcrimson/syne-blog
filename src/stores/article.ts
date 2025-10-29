import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Article, PaginationParams, PaginationResult } from '@/types'
import { mockArticles } from '@/mock/articles'

export const useArticleStore = defineStore('article', () => {
  // 状态
  const articles = ref<Article[]>(mockArticles)
  const currentArticle = ref<Article | null>(null)

  // 计算属性
  const totalArticles = computed(() => articles.value.length)
  
  const latestArticles = computed(() => {
    return articles.value
      .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
      .slice(0, 6)
  })

  // 动作
  const getArticleById = (id: number): Article | undefined => {
    return articles.value.find(article => article.id === id)
  }

  const getArticlesByPage = (params: PaginationParams): PaginationResult<Article> => {
    const { page, pageSize } = params
    const start = (page - 1) * pageSize
    const end = start + pageSize
    
    const sortedArticles = [...articles.value].sort(
      (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
    )
    
    const data = sortedArticles.slice(start, end)
    const total = sortedArticles.length
    const totalPages = Math.ceil(total / pageSize)

    return {
      data,
      total,
      page,
      pageSize,
      totalPages
    }
  }

  const setCurrentArticle = (id: number) => {
    const article = getArticleById(id)
    if (article) {
      currentArticle.value = article
      // 增加浏览量
      article.views++
    } else {
      currentArticle.value = null
    }
  }

  const searchArticles = (keyword: string): Article[] => {
    if (!keyword.trim()) {
      return articles.value
    }
    
    const lowerKeyword = keyword.toLowerCase()
    return articles.value.filter(article => 
      article.title.toLowerCase().includes(lowerKeyword) ||
      article.summary.toLowerCase().includes(lowerKeyword) ||
      article.content.toLowerCase().includes(lowerKeyword) ||
      article.tags.some(tag => tag.toLowerCase().includes(lowerKeyword))
    )
  }

  const getArticlesByCategory = (category: string): Article[] => {
    return articles.value.filter(article => article.category === category)
  }

  const getArticlesByTag = (tag: string): Article[] => {
    return articles.value.filter(article => article.tags.includes(tag))
  }

  const getAllCategories = computed(() => {
    const categories = new Set(articles.value.map(article => article.category))
    return Array.from(categories)
  })

  const getAllTags = computed(() => {
    const tags = new Set(articles.value.flatMap(article => article.tags))
    return Array.from(tags)
  })

  return {
    articles,
    currentArticle,
    totalArticles,
    latestArticles,
    getAllCategories,
    getAllTags,
    getArticleById,
    getArticlesByPage,
    setCurrentArticle,
    searchArticles,
    getArticlesByCategory,
    getArticlesByTag
  }
})

