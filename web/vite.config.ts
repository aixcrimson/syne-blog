import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd())

  return {
    plugins: [vue()],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      }
    },
    server: {
      port: 3000,
      host: '0.0.0.0',
      proxy: {
        '/api': {
          target: env.VITE_PROXY_TARGET || 'http://localhost:8080',
          changeOrigin: true,
          secure: false // HTTPS 代理可能需要这个
        }
      }
    },

    // 生产环境 esbuild 配置：移除 console / debugger
    esbuild: {
      drop: mode === 'production' ? ['console', 'debugger'] : []
    },

    // ========== 构建优化 ==========
    build: {
      // 启用 CSS 代码分割
      cssCodeSplit: true,
      // 生产环境移除 console.log（使用内置 esbuild，无需额外安装）
      minify: 'esbuild',
      rollupOptions: {
        output: {
          // 手动分包策略：利用浏览器并发下载，加快首屏加载
          manualChunks(id) {
            if (!id.includes('node_modules')) return

            // Element Plus 单独打包 (体积最大的依赖)
            if (id.includes('element-plus') || id.includes('@element-plus')) {
              return 'vendor-element'
            }

            // Markdown 渲染 + 代码高亮 (仅文章详情页需要)
            if (id.includes('highlight.js') || id.includes('markdown-it')) {
              return 'vendor-markdown'
            }

            // Vue 生态系统核心
            if (id.includes('vue') || id.includes('pinia') || id.includes('vue-router')) {
              return 'vendor-vue'
            }

            // 其他第三方库
            return 'vendor-core'
          }
        }
      }
    }
  }
})
