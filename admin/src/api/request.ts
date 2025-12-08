/**
 * Axios 请求配置
 * 统一管理 HTTP 请求，包含 Token 认证和错误处理
 */
import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse, type AxiosError } from 'axios'
import { ElMessage } from 'element-plus'
import router, { getToken, removeToken } from '@/router'

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
 * 请求拦截器
 * - 自动添加 Token 到请求头
 */
request.interceptors.request.use(
  (config) => {
    const token = getToken()
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
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
      ElMessage.error(errorMsg)
      return Promise.reject(new Error(errorMsg))
    }
    
    return data
  },
  (error: AxiosError) => {
    console.error('❌ 响应错误:', error)
    
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 400:
          ElMessage.error('请求参数错误')
          break
        case 401:
          // Token 过期或无效，清除 Token 并跳转登录页
          ElMessage.error('登录已过期，请重新登录')
          removeToken()
          router.push('/login')
          break
        case 403:
          ElMessage.error('没有权限执行此操作')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error((data as any)?.message || `请求失败 (${status})`)
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
