/// <reference types="vitest" />
import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

/**
 * Vite 配置文件
 * - 端口 3001（与 web 端 3000 区分）
 * - 代理 /api 到后端 8080 (开发环境) 或 生产环境地址
 */
export default defineConfig(({ mode }) => {
  // 加载环境变量，process.cwd() 是项目根目录
  const env = loadEnv(mode, process.cwd())

  return {
    plugins: [vue()],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      }
    },
    server: {
      port: 3001,
      host: '0.0.0.0',
      proxy: {
        '/api': {
          // 使用环境变量中的 VITE_PROXY_TARGET，如果未定义则回退到 localhost
          target: env.VITE_PROXY_TARGET || 'http://localhost:8080',
          changeOrigin: true,
          secure: true
        }
      }
    },
    test: {
      environment: 'jsdom',
      globals: true
    }
  }
})
