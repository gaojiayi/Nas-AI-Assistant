import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd())
  
  return {
    plugins: [vue()],
    build: {
      mode: 'production'
    },
    define: {
      'import.meta.env.VITE_USE_COMMUNITY_MOCK': JSON.stringify(env.VITE_USE_COMMUNITY_MOCK || 'true'),
      'import.meta.env.VITE_USE_KNOWLEDGE_MOCK': JSON.stringify(env.VITE_USE_KNOWLEDGE_MOCK || 'true'),
      'import.meta.env.VITE_USE_CHAT_MOCK': JSON.stringify(env.VITE_USE_CHAT_MOCK || 'false'),
      'import.meta.env.VITE_API_BASE_URL': JSON.stringify(env.VITE_API_BASE_URL || '/api')
    },
    server: {
      port: 5123,
      proxy: {
        '/api': {
          target: 'http://localhost:8123',
          changeOrigin: true
        }
      }
    }
  }
})
