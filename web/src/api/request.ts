/**
 * Axios 请求配置
 * 统一管理 HTTP 请求
 */
import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse, type AxiosError } from 'axios'
import { ElMessage } from 'element-plus'

/**
 * 创建 axios 实例
 */
const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api', // API 基础路径
  timeout: 15000, // 请求超时时间（15秒）
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})

/**
 * 请求拦截器
 * 在请求发送前进行统一处理
 */
request.interceptors.request.use(
  (config) => {
    // 从本地存储获取 token
    const token = localStorage.getItem('token')
    
    // 如果有 token，添加到请求头
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    // 可以在这里添加其他请求头
    // config.headers['X-Custom-Header'] = 'custom-value'
    
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
 * 统一处理响应数据和错误
 */
request.interceptors.response.use(
  (response: AxiosResponse) => {
    console.log('📥 响应接收:', response.config.url, response.status)
    
    // 根据业务需求自定义响应结构
    const { data } = response
    
    // 如果后端返回的是标准格式: { code, data, message }
    if (data && typeof data === 'object' && 'code' in data) {
      const { code, data: responseData, message } = data
      
      // 成功
      if (code === 200 || code === 0) {
        return responseData
      }
      
      // 业务错误
      ElMessage.error(message || '请求失败')
      return Promise.reject(new Error(message || '请求失败'))
    }
    
    // 如果后端直接返回数据
    return data
  },
  (error: AxiosError) => {
    console.error('❌ 响应错误:', error)
    
    // 处理不同的 HTTP 状态码
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 400:
          ElMessage.error('请求参数错误')
          break
        case 401:
          ElMessage.error('未授权，请先登录')
          // 可以跳转到登录页
          // router.push('/login')
          localStorage.removeItem('token')
          break
        case 403:
          ElMessage.error('拒绝访问')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        case 502:
          ElMessage.error('网关错误')
          break
        case 503:
          ElMessage.error('服务暂时不可用')
          break
        case 504:
          ElMessage.error('网关超时')
          break
        default:
          ElMessage.error((data as any)?.message || `请求失败 (${status})`)
      }
    } else if (error.request) {
      // 请求已发出但没有收到响应
      ElMessage.error('网络错误，请检查网络连接')
    } else {
      // 请求配置出错
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

/**
 * 导出 axios 实例（用于特殊场景）
 */
export default request

