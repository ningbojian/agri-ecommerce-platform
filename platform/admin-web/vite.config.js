import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// 三个后端服务分别代理，前端统一以 /pro-api、/user-api、/sup-api 访问。
const proxy = (prefix, port) => ({
  [prefix]: {
    target: `http://127.0.0.1:${port}`,
    changeOrigin: true,
    rewrite: path => path.replace(new RegExp(`^${prefix}`), '')
  }
})

const apiProxy = {
  ...proxy('/pro-api', 8201),
  ...proxy('/user-api', 8202),
  ...proxy('/sup-api', 8203)
}

export default defineConfig({
  plugins: [vue()],
  server: {
    host: '127.0.0.1',
    port: 5173,
    proxy: apiProxy
  },
  preview: {
    host: '127.0.0.1',
    port: 4173,
    proxy: apiProxy
  },
  build: {
    outDir: 'dist',
    chunkSizeWarningLimit: 1500
  }
})
