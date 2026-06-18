<template>
  <div class="dashboard p-4 md:p-6">
    <!-- 页面标题 -->
    <div class="mb-4 md:mb-6 hidden md:block">
      <h1 class="text-2xl font-bold text-gray-800">仪表盘</h1>
      <p class="text-gray-500 mt-1">欢迎回来，查看您的博客数据概览</p>
    </div>

    <!-- 统计卡片区域 -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6 gap-4 mb-6">
      <template v-if="loading">
        <!-- 骨架屏 -->
        <div 
          v-for="i in 6" 
          :key="i" 
          class="glass-card p-4 rounded-lg animate-pulse"
        >
          <div class="flex items-center justify-between">
            <div>
              <div class="h-4 bg-gray-200 rounded w-16 mb-2"></div>
              <div class="h-8 bg-gray-200 rounded w-12"></div>
            </div>
            <div class="h-10 w-10 bg-gray-200 rounded-full"></div>
          </div>
        </div>
      </template>
      <template v-else>
        <!-- 统计卡片 -->
        <div 
          v-for="card in statCards" 
          :key="card.key" 
          class="stat-card glass-card p-4 rounded-lg"
        >
          <div class="flex items-center justify-between">
            <div>
              <div class="text-sm text-gray-500">{{ card.label }}</div>
              <div class="text-2xl font-bold mt-1" :class="card.color">
                {{ formatNumber(stats[card.key as keyof DashboardStats]) }}
              </div>
            </div>
            <div 
              class="stat-icon-wrapper w-10 h-10 rounded-full flex items-center justify-center"
              :style="{ background: card.bgGradient }"
            >
              <component :is="card.icon" class="w-5 h-5 text-white" />
            </div>
          </div>
        </div>
      </template>
    </div>

    <!-- 图表区域 -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- 文章发布趋势 -->
      <div class="chart-card glass-card rounded-lg overflow-hidden">
        <div class="chart-header px-5 py-4 border-b border-gray-200/30">
          <h2 class="text-base font-semibold text-gray-800">📈 文章发布趋势</h2>
          <span class="text-xs text-gray-400">最近 12 个月</span>
        </div>
        <div class="chart-body p-4">
          <div v-if="chartsLoading" class="chart-skeleton"></div>
          <div ref="articleTrendRef" class="chart-container"></div>
        </div>
      </div>

      <!-- 分类文章分布 -->
      <div class="chart-card glass-card rounded-lg overflow-hidden">
        <div class="chart-header px-5 py-4 border-b border-gray-200/30">
          <h2 class="text-base font-semibold text-gray-800">🍩 分类文章分布</h2>
          <span class="text-xs text-gray-400">各分类占比</span>
        </div>
        <div class="chart-body p-4">
          <div v-if="chartsLoading" class="chart-skeleton"></div>
          <div ref="categoryDistRef" class="chart-container"></div>
        </div>
      </div>

      <!-- 热门文章 TOP10 -->
      <div class="chart-card glass-card rounded-lg overflow-hidden">
        <div class="chart-header px-5 py-4 border-b border-gray-200/30">
          <h2 class="text-base font-semibold text-gray-800">📊 热门文章 TOP10</h2>
          <span class="text-xs text-gray-400">按浏览量排序</span>
        </div>
        <div class="chart-body p-4">
          <div v-if="chartsLoading" class="chart-skeleton"></div>
          <div ref="topArticlesRef" class="chart-container" style="height: 380px;"></div>
        </div>
      </div>

      <!-- 互动趋势 -->
      <div class="chart-card glass-card rounded-lg overflow-hidden">
        <div class="chart-header px-5 py-4 border-b border-gray-200/30">
          <h2 class="text-base font-semibold text-gray-800">📉 互动趋势</h2>
          <span class="text-xs text-gray-400">浏览 / 点赞 / 评论</span>
        </div>
        <div class="chart-body p-4">
          <div v-if="chartsLoading" class="chart-skeleton"></div>
          <div ref="interactionRef" class="chart-container"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 仪表盘首页
 * 展示博客统计数据和 ECharts 可视化图表
 */
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Document, 
  Folder, 
  PriceTag, 
  ChatDotRound, 
  View, 
  Star 
} from '@element-plus/icons-vue'
import { dashboardApi } from '@/api/dashboard'
import type { DashboardStats, DashboardChartsData } from '@/types'
import * as echarts from 'echarts'

/** 加载状态 */
const loading = ref(true)
const chartsLoading = ref(true)

/** 统计数据 */
const stats = ref<DashboardStats>({
  articleCount: 0,
  categoryCount: 0,
  tagCount: 0,
  commentCount: 0,
  totalViews: 0,
  totalLikes: 0
})

/** 图表 DOM 引用 */
const articleTrendRef = ref<HTMLDivElement>()
const categoryDistRef = ref<HTMLDivElement>()
const topArticlesRef = ref<HTMLDivElement>()
const interactionRef = ref<HTMLDivElement>()

/** ECharts 实例列表，用于统一管理 resize 和 dispose */
const chartInstances: echarts.ECharts[] = []

// ==================== 配色方案 ====================

/** 图表主色调 */
const COLORS = {
  primary: '#6366f1',
  primaryLight: '#818cf8',
  success: '#22c55e',
  successLight: '#4ade80',
  warning: '#f59e0b',
  warningLight: '#fbbf24',
  info: '#06b6d4',
  infoLight: '#22d3ee',
  rose: '#f43f5e',
  roseLight: '#fb7185',
  violet: '#8b5cf6',
  violetLight: '#a78bfa',
}

/** 环形图色板 */
const PIE_COLORS = [
  '#6366f1', '#22c55e', '#f59e0b', '#06b6d4', '#f43f5e',
  '#8b5cf6', '#ec4899', '#14b8a6', '#f97316', '#64748b'
]

/**
 * 统计卡片配置
 */
const statCards = [
  { key: 'articleCount', label: '文章总数', icon: Document, color: 'text-indigo-600', bgGradient: 'linear-gradient(135deg, #6366f1, #818cf8)' },
  { key: 'categoryCount', label: '分类总数', icon: Folder, color: 'text-emerald-600', bgGradient: 'linear-gradient(135deg, #22c55e, #4ade80)' },
  { key: 'tagCount', label: '标签总数', icon: PriceTag, color: 'text-amber-600', bgGradient: 'linear-gradient(135deg, #f59e0b, #fbbf24)' },
  { key: 'commentCount', label: '评论总数', icon: ChatDotRound, color: 'text-cyan-600', bgGradient: 'linear-gradient(135deg, #06b6d4, #22d3ee)' },
  { key: 'totalViews', label: '总浏览量', icon: View, color: 'text-rose-600', bgGradient: 'linear-gradient(135deg, #f43f5e, #fb7185)' },
  { key: 'totalLikes', label: '总点赞数', icon: Star, color: 'text-violet-600', bgGradient: 'linear-gradient(135deg, #8b5cf6, #a78bfa)' }
]

/**
 * 格式化数字（大数字显示为 K/M）
 */
const formatNumber = (num: number): string => {
  if (num >= 1000000) {
    return (num / 1000000).toFixed(1) + 'M'
  }
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K'
  }
  return num.toString()
}

// ==================== 图表初始化 ====================

/**
 * 创建 ECharts 实例并纳入统一管理
 */
const createChart = (el: HTMLDivElement): echarts.ECharts => {
  const chart = echarts.init(el, undefined, { renderer: 'canvas' })
  chartInstances.push(chart)
  return chart
}

/**
 * 初始化文章发布趋势图（渐变面积折线图）
 */
const initArticleTrendChart = (data: DashboardChartsData) => {
  if (!articleTrendRef.value) return
  const chart = createChart(articleTrendRef.value)

  const months = data.articleTrend.map(d => d.month.substring(5)) // 只显示 MM
  const counts = data.articleTrend.map(d => d.count)

  chart.setOption({
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255,255,255,0.95)',
      borderColor: '#e5e7eb',
      borderWidth: 1,
      textStyle: { color: '#374151', fontSize: 13 },
      formatter: (params: any) => {
        const p = params[0]
        return `<div style="font-weight:600;margin-bottom:4px">${data.articleTrend[p.dataIndex].month}</div>
                <span style="color:${COLORS.primary}">●</span> 发布 <b>${p.value}</b> 篇`
      }
    },
    grid: { top: 30, right: 20, bottom: 30, left: 45 },
    xAxis: {
      type: 'category',
      data: months,
      axisLine: { lineStyle: { color: '#e5e7eb' } },
      axisTick: { show: false },
      axisLabel: { color: '#9ca3af', fontSize: 11 }
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: '#f3f4f6', type: 'dashed' } },
      axisLabel: { color: '#9ca3af', fontSize: 11 }
    },
    series: [{
      type: 'line',
      data: counts,
      smooth: true,
      symbol: 'circle',
      symbolSize: 6,
      showSymbol: false,
      emphasis: { focus: 'series', itemStyle: { shadowBlur: 10, shadowColor: 'rgba(99,102,241,0.4)' } },
      lineStyle: { width: 3, color: COLORS.primary },
      itemStyle: { color: COLORS.primary, borderColor: '#fff', borderWidth: 2 },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(99,102,241,0.25)' },
          { offset: 1, color: 'rgba(99,102,241,0.02)' }
        ])
      },
      animationDuration: 1200,
      animationEasing: 'cubicOut'
    }]
  })
}

/**
 * 初始化分类文章分布图（环形图）
 */
const initCategoryDistChart = (data: DashboardChartsData) => {
  if (!categoryDistRef.value) return
  const chart = createChart(categoryDistRef.value)

  const pieData = data.categoryDistribution.map((d, i) => ({
    name: d.name,
    value: d.articleCount,
    itemStyle: { color: PIE_COLORS[i % PIE_COLORS.length] }
  }))

  chart.setOption({
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255,255,255,0.95)',
      borderColor: '#e5e7eb',
      borderWidth: 1,
      textStyle: { color: '#374151', fontSize: 13 },
      formatter: (params: any) => {
        return `<span style="color:${params.color}">●</span> ${params.name}<br/>
                文章 <b>${params.value}</b> 篇（${params.percent}%）`
      }
    },
    legend: {
      type: 'scroll',
      orient: 'vertical',
      right: 10,
      top: 'middle',
      itemWidth: 10,
      itemHeight: 10,
      itemGap: 12,
      textStyle: { color: '#6b7280', fontSize: 12 },
      formatter: (name: string) => {
        const item = data.categoryDistribution.find(d => d.name === name)
        return item ? `${name}  ${item.articleCount}` : name
      }
    },
    series: [{
      type: 'pie',
      radius: ['45%', '72%'],
      center: ['35%', '50%'],
      avoidLabelOverlap: true,
      padAngle: 2,
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: {
        label: { show: true, fontSize: 14, fontWeight: 'bold' },
        itemStyle: { shadowBlur: 20, shadowColor: 'rgba(0,0,0,0.15)' }
      },
      data: pieData,
      animationType: 'scale',
      animationDuration: 800,
      animationEasing: 'elasticOut'
    }]
  })
}

/**
 * 初始化热门文章 TOP10（水平渐变柱状图）
 */
const initTopArticlesChart = (data: DashboardChartsData) => {
  if (!topArticlesRef.value) return
  const chart = createChart(topArticlesRef.value)

  // 反转使最高在上
  const reversed = [...data.topArticles].reverse()
  const titles = reversed.map(d => d.title.length > 12 ? d.title.substring(0, 12) + '…' : d.title)
  const views = reversed.map(d => d.views)

  chart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      backgroundColor: 'rgba(255,255,255,0.95)',
      borderColor: '#e5e7eb',
      borderWidth: 1,
      textStyle: { color: '#374151', fontSize: 13 },
      formatter: (params: any) => {
        const p = params[0]
        const article = reversed[p.dataIndex]
        return `<div style="font-weight:600;margin-bottom:4px">${article.title}</div>
                <span style="color:${COLORS.info}">●</span> 浏览量 <b>${article.views.toLocaleString()}</b>`
      }
    },
    grid: { top: 10, right: 30, bottom: 10, left: 120 },
    xAxis: {
      type: 'value',
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: '#f3f4f6', type: 'dashed' } },
      axisLabel: { color: '#9ca3af', fontSize: 11 }
    },
    yAxis: {
      type: 'category',
      data: titles,
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#6b7280', fontSize: 11, width: 100, overflow: 'truncate' }
    },
    series: [{
      type: 'bar',
      data: views,
      barWidth: 16,
      itemStyle: {
        borderRadius: [0, 8, 8, 0],
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: COLORS.info },
          { offset: 1, color: COLORS.infoLight }
        ])
      },
      emphasis: {
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: COLORS.primary },
            { offset: 1, color: COLORS.primaryLight }
          ])
        }
      },
      animationDuration: 1000,
      animationEasing: 'cubicOut',
      animationDelay: (idx: number) => idx * 80
    }]
  })
}

/**
 * 初始化互动趋势图（堆叠面积图）
 */
const initInteractionChart = (data: DashboardChartsData) => {
  if (!interactionRef.value) return
  const chart = createChart(interactionRef.value)

  const months = data.interactionTrend.map(d => d.month.substring(5))

  /** 创建面积系列的公共配置 */
  const createAreaSeries = (name: string, data: number[], color: string, lightColor: string) => ({
    name,
    type: 'line' as const,
    smooth: true,
    symbol: 'circle',
    symbolSize: 4,
    showSymbol: false,
    lineStyle: { width: 2, color },
    itemStyle: { color, borderColor: '#fff', borderWidth: 1 },
    areaStyle: {
      color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
        { offset: 0, color: lightColor.replace(')', ',0.3)').replace('rgb', 'rgba') },
        { offset: 1, color: lightColor.replace(')', ',0.02)').replace('rgb', 'rgba') }
      ])
    },
    emphasis: { focus: 'series' as const },
    data,
    animationDuration: 1200,
    animationEasing: 'cubicOut' as const
  })

  chart.setOption({
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255,255,255,0.95)',
      borderColor: '#e5e7eb',
      borderWidth: 1,
      textStyle: { color: '#374151', fontSize: 13 }
    },
    legend: {
      bottom: 0,
      itemWidth: 12,
      itemHeight: 8,
      itemGap: 20,
      textStyle: { color: '#6b7280', fontSize: 12 }
    },
    grid: { top: 20, right: 20, bottom: 40, left: 50 },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: months,
      axisLine: { lineStyle: { color: '#e5e7eb' } },
      axisTick: { show: false },
      axisLabel: { color: '#9ca3af', fontSize: 11 }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: '#f3f4f6', type: 'dashed' } },
      axisLabel: { color: '#9ca3af', fontSize: 11 }
    },
    series: [
      createAreaSeries('浏览', data.interactionTrend.map(d => d.views), COLORS.primary, 'rgb(99,102,241)'),
      createAreaSeries('点赞', data.interactionTrend.map(d => d.likes), COLORS.success, 'rgb(34,197,94)'),
      createAreaSeries('评论', data.interactionTrend.map(d => d.comments), COLORS.warning, 'rgb(245,158,11)')
    ]
  })
}

// ==================== 数据加载 ====================

/**
 * 加载仪表盘统计数据
 */
const loadDashboardData = async () => {
  loading.value = true
  try {
    const data = await dashboardApi.getDashboardData()
    stats.value = data.stats
  } catch (error) {
    console.error('加载仪表盘数据失败:', error)
    ElMessage.error('加载数据失败，请刷新重试')
  } finally {
    loading.value = false
  }
}

/**
 * 加载图表数据并初始化图表
 */
const loadChartsData = async () => {
  chartsLoading.value = true
  try {
    const data = await dashboardApi.getChartsData()
    await nextTick()
    initArticleTrendChart(data)
    initCategoryDistChart(data)
    initTopArticlesChart(data)
    initInteractionChart(data)
  } catch (error) {
    console.error('加载图表数据失败:', error)
    ElMessage.error('加载图表数据失败')
  } finally {
    chartsLoading.value = false
  }
}

/**
 * 统一处理窗口 resize，对所有图表实例调用 resize
 */
const handleResize = () => {
  chartInstances.forEach(chart => {
    if (!chart.isDisposed()) {
      chart.resize()
    }
  })
}

onMounted(() => {
  loadDashboardData()
  loadChartsData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chartInstances.forEach(chart => {
    if (!chart.isDisposed()) {
      chart.dispose()
    }
  })
  chartInstances.length = 0
})
</script>

<style scoped>
/* 统计卡片悬浮效果 */
.stat-card {
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}
.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
}

/* 统计图标包裹器 */
.stat-icon-wrapper {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}
.stat-card:hover .stat-icon-wrapper {
  transform: scale(1.1);
}

/* 图表卡片 */
.chart-card {
  transition: box-shadow 0.3s ease;
}
.chart-card:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
}

/* 图表头部 */
.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* 图表容器 */
.chart-container {
  width: 100%;
  height: 300px;
}

/* 图表骨架屏 */
.chart-skeleton {
  width: 100%;
  height: 300px;
  background: linear-gradient(90deg, #f3f4f6 25%, #e5e7eb 50%, #f3f4f6 75%);
  background-size: 200% 100%;
  animation: skeleton-shimmer 1.5s ease-in-out infinite;
  border-radius: 8px;
}

@keyframes skeleton-shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

/* 主题色 */
.text-indigo-600 { color: #6366f1; }
.text-emerald-600 { color: #22c55e; }
.text-amber-600 { color: #f59e0b; }
.text-cyan-600 { color: #06b6d4; }
.text-rose-600 { color: #f43f5e; }
.text-violet-600 { color: #8b5cf6; }
</style>
