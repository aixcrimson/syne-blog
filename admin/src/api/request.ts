/**
 * Axios 请求配置
 * 统一管理 HTTP 请求，包含 Token 认证和错误处理
 */
import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse, type AxiosError } from 'axios'
import { ElMessage } from 'element-plus'
import router, { getToken, setToken, removeToken } from '@/router'

/**
 * 业务错误码映射
 */
const businessErrorMessages: Record<number, string> = {
  1001: '用户名或密码错误',
  1002: '用户已被禁用',
  2001: '文章不存在',
  2002: '分类不存在',
  2003: '分类下存在文章，无法删除',
  2004: '名称已存在',
  2005: '别名已存在',
}

/**
 * 创建 axios 实例
 */
const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})

/**
 * 解析 JWT Token 的过期时间（秒级 Unix 时间戳）
 * @returns 过期时间戳（秒），解析失败返回 null
 */
const getTokenExpiration = (token: string): number | null => {
  try {
    const parts = token.split('.')
    if (parts.length !== 3) return null
    const payload = JSON.parse(atob(parts[1].replace(/-/g, '+').replace(/_/g, '/')))
    return payload.exp ?? null
  } catch {
    return null
  }
}

/**
 * Token 刷新阈值：剩余有效期小于此值时触发刷新（单位：秒）
 * 设为 1 天，确保在过期前有充足的刷新窗口
 */
const REFRESH_THRESHOLD_SECONDS = 86400

/** 是否正在刷新 Token（防止并发刷新） */
let isRefreshing = false

/** 挂起的请求队列 */
let requests: ((token: string | null) => void)[] = []

/**
 * 请求拦截器
 * - 自动添加 Token 到请求头
 * - Token 快过期时自动静默刷新
 */
request.interceptors.request.use(
  async (config) => {
    const token = getToken()
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`

      // 检查 Token 是否快过期，如果是则静默刷新
      // 跳过 refresh 接口自身，避免死循环
      if (config.url?.includes('/auth/refresh')) {
        return config
      }

      const exp = getTokenExpiration(token)
      if (exp) {
        const remainingSeconds = exp - Math.floor(Date.now() / 1000)
        if (remainingSeconds > 0 && remainingSeconds < REFRESH_THRESHOLD_SECONDS) {
          if (!isRefreshing) {
            isRefreshing = true
            try {
              // 动态导入避免循环依赖
              const { authApi } = await import('./auth')
              const newToken = await authApi.refreshToken()
              if (newToken) {
                // 更新 localStorage 和当前请求头
                setToken(newToken)
                config.headers.Authorization = `Bearer ${newToken}`
                console.log('🔄 Token 已静默刷新')
                // 执行挂起的请求队列
                requests.forEach(cb => cb(newToken))
                requests = []
              }
            } catch (err) {
              console.warn('⚠️ Token 静默刷新失败，继续使用当前 Token:', err)
              requests.forEach(cb => cb(null))
              requests = []
            } finally {
              isRefreshing = false
            }
          } else {
            // 正在刷新，将后续请求挂起并包装为一个 Promise
            return new Promise((resolve) => {
              requests.push((newToken: string | null) => {
                if (newToken) {
                  config.headers.Authorization = `Bearer ${newToken}`
                }
                resolve(config)
              })
            })
          }
        }
      }
    }
    
    console.log('📤 请求发送:', config.method?.toUpperCase(), config.url)
    
    return config
  },
  (error: AxiosError) => {
    console.error('❌ 请求错误:', error)
    return Promise.reject(error)
  }
)


/**
 * 响应拦截器
 * - 统一处理响应数据
 * - 处理业务错误码
 * - 401 自动跳转登录页
 */
request.interceptors.response.use(
  (response: AxiosResponse) => {
    console.log('📥 响应接收:', response.config.url, response.status)
    
    const { data } = response
    
    // 标准响应格式: { code, data, message }
    if (data && typeof data === 'object' && 'code' in data) {
      const { code, data: responseData, message } = data
      
      // 成功
      if (code === 200 || code === 0) {
        return responseData
      }
      
      // 业务错误 - 使用映射的错误信息或后端返回的信息
      const errorMsg = businessErrorMessages[code] || message || '请求失败'
      
      if (code === 401) {
        ElMessage.error(errorMsg || '登录已过期，请重新登录')
        removeToken()
        router.push('/login')
        return Promise.reject(new Error(errorMsg))
      }

      ElMessage.error(errorMsg)
      return Promise.reject(new Error(errorMsg))
    }
    
    return data
  },
  (error: AxiosError) => {
    console.error('❌ 响应错误:', error)
    
    if (error.response) {
      const { status, data } = error.response
      // 优先使用后端返回的友好提示
      const backendMsg = (data as any)?.message

      switch (status) {
        case 400:
          ElMessage.error(backendMsg || '请求参数有误，请检查后重试')
          break
        case 401:
        case 403:
          // Token 过期、无效或无权限，清除 Token 并跳转登录页
          ElMessage.error(backendMsg || (status === 401 ? '登录已过期，请重新登录' : '没有权限执行此操作，请重新登录'))
          removeToken()
          router.push('/login')
          break
        case 404:
          ElMessage.error(backendMsg || '请求的资源不存在')
          break
        case 500:
          ElMessage.error(backendMsg || '服务器内部错误，请稍后重试')
          break
        default:
          ElMessage.error(backendMsg || `请求失败 (${status})`)
      }

      // 用后端消息替换 axios 默认的英文 message，便于业务层复用且不再重复提示
      if (backendMsg) {
        error.message = backendMsg
      }
    } else if (error.request) {
      ElMessage.error('网络错误，请检查网络连接')
    } else {
      ElMessage.error(error.message || '请求失败')
    }
    
    return Promise.reject(error)
  }
)

/**
 * 封装 GET 请求
 */
export function get<T = any>(url: string, params?: any, config?: AxiosRequestConfig): Promise<T> {
  return request.get(url, { params, ...config })
}

/**
 * 封装 POST 请求
 */
export function post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
  return request.post(url, data, config)
}

/**
 * 封装 PUT 请求
 */
export function put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
  return request.put(url, data, config)
}

/**
 * 封装 DELETE 请求
 */
export function del<T = any>(url: string, params?: any, config?: AxiosRequestConfig): Promise<T> {
  return request.delete(url, { params, ...config })
}

/**
 * 封装 PATCH 请求
 */
export function patch<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
  return request.patch(url, data, config)
}

export default request
