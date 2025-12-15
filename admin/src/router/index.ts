import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw, NavigationGuardNext, RouteLocationNormalized } from 'vue-router'

/**
 * 管理端路由配置
 * 包含登录页和需要认证的管理页面
 */

/**
 * 公开路由 - 无需登录即可访问
 */
const publicRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { 
      title: '登录',
      requiresAuth: false 
    }
  }
]

/**
 * 受保护路由 - 需要登录才能访问
 * 使用 AdminLayout 作为布局组件
 */
const protectedRoutes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('@/components/layout/AdminLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        redirect: '/dashboard'
      },
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '仪表盘', icon: 'Odometer' }
      },
      // 文章管理
      {
        path: 'article',
        name: 'Article',
        redirect: '/article/list',
        meta: { title: '文章管理', icon: 'Document' },
        children: [
          {
            path: 'list',
            name: 'ArticleList',
            component: () => import('@/views/article/list.vue'),
            meta: { title: '文章列表' }
          },
          {
            path: 'create',
            name: 'ArticleCreate',
            component: () => import('@/views/article/edit.vue'),
            meta: { title: '新建文章' }
          },
          {
            path: 'edit/:id',
            name: 'ArticleEdit',
            component: () => import('@/views/article/edit.vue'),
            meta: { title: '编辑文章' }
          }
        ]
      },
      // 分类管理
      {
        path: 'category',
        name: 'Category',
        component: () => import('@/views/category/index.vue'),
        meta: { title: '分类管理', icon: 'Folder' }
      },
      // 标签管理
      {
        path: 'tag',
        name: 'Tag',
        component: () => import('@/views/tag/index.vue'),
        meta: { title: '标签管理', icon: 'PriceTag' }
      },
      // 评论管理
      {
        path: 'comment',
        name: 'Comment',
        component: () => import('@/views/comment/index.vue'),
        meta: { title: '评论管理', icon: 'ChatDotRound' }
      },
      // 导航管理
      {
        path: 'navigation',
        name: 'Navigation',
        component: () => import('@/views/navigation/index.vue'),
        meta: { title: '导航管理', icon: 'Link' }
      },
      // 用户管理
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      // 个人设置
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/views/settings/index.vue'),
        meta: { title: '个人设置', icon: 'Setting' }
      }
    ]
  }
]

/**
 * 合并所有路由
 */
const routes: RouteRecordRaw[] = [
  ...publicRoutes,
  ...protectedRoutes,
  // 404 页面
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    redirect: '/dashboard'
  }
]

/**
 * 创建路由实例
 */
const router = createRouter({
  history: createWebHistory(),
  routes
})

/**
 * Token 存储键名
 */
const TOKEN_KEY = 'token'

/**
 * 获取存储的 Token
 * @returns Token 字符串或 null
 */
export const getToken = (): string | null => {
  return localStorage.getItem(TOKEN_KEY)
}

/**
 * 设置 Token
 * @param token - Token 字符串
 */
export const setToken = (token: string): void => {
  localStorage.setItem(TOKEN_KEY, token)
}

/**
 * 移除 Token
 */
export const removeToken = (): void => {
  localStorage.removeItem(TOKEN_KEY)
}

/**
 * 解析 JWT Token 的 payload 部分
 * @param token - JWT Token 字符串
 * @returns 解析后的 payload 对象，解析失败返回 null
 */
const parseJwtPayload = (token: string): Record<string, any> | null => {
  try {
    const parts = token.split('.')
    if (parts.length !== 3) {
      return null
    }
    // 解码 Base64Url 编码的 payload
    const payload = parts[1]
    const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'))
    return JSON.parse(decoded)
  } catch {
    return null
  }
}

/**
 * 检查 Token 是否过期
 * @param token - JWT Token 字符串
 * @returns 是否过期（true 表示已过期或无效）
 */
const isTokenExpired = (token: string): boolean => {
  const payload = parseJwtPayload(token)
  if (!payload || !payload.exp) {
    // 无法解析或没有过期时间，视为有效（由后端验证）
    return false
  }
  // exp 是 Unix 时间戳（秒），比较当前时间
  const now = Math.floor(Date.now() / 1000)
  return payload.exp < now
}

/**
 * 开发模式：跳过登录验证
 * 设置为 true 时，无需登录即可访问所有页面
 * 生产环境请设置为 false
 */
const DEV_SKIP_AUTH = false

/**
 * 检查 Token 是否存在且有效
 * - 检查 Token 是否存在
 * - 检查 Token 格式是否正确（JWT 格式）
 * - 检查 Token 是否过期
 * @returns Token 是否有效
 */
export const isAuthenticated = (): boolean => {
  // 开发模式跳过验证
  if (DEV_SKIP_AUTH) {
    return true
  }

  const token = getToken()
  
  // Token 不存在
  if (!token) {
    return false
  }
  
  // 检查 Token 是否过期
  if (isTokenExpired(token)) {
    // Token 已过期，清除它
    removeToken()
    return false
  }
  
  return true
}

/**
 * 检查路由是否需要认证
 * @param to - 目标路由
 * @returns 是否需要认证
 */
export const requiresAuthentication = (to: RouteLocationNormalized): boolean => {
  // 检查路由及其所有匹配的父路由
  return to.matched.some(record => record.meta.requiresAuth !== false)
}

/**
 * 路由守卫 - 检查认证状态
 * 
 * 功能说明：
 * 1. 未登录用户访问受保护页面时重定向到登录页
 * 2. 已登录用户访问登录页时重定向到首页
 * 3. Token 过期时自动清除并重定向到登录页
 * 
 * @see Requirements 3.1 - 未登录重定向到登录页
 * @see Requirements 3.6 - Token 过期自动跳转登录页
 */
router.beforeEach((
  to: RouteLocationNormalized, 
  _from: RouteLocationNormalized, 
  next: NavigationGuardNext
) => {
  // 设置页面标题
  const title = to.meta.title as string
  document.title = title ? `${title} - 博客管理系统` : '博客管理系统'

  const authenticated = isAuthenticated()
  const needsAuth = requiresAuthentication(to)

  if (needsAuth && !authenticated) {
    // 需要认证但未登录（或 Token 已过期），重定向到登录页
    // 保存原始目标路径，登录后可跳转回来
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
  } else if (to.path === '/login' && authenticated) {
    // 已登录用户访问登录页，重定向到首页
    next('/dashboard')
  } else {
    // 正常放行
    next()
  }
})

export default router
