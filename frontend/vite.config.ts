import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import { nodePolyfills } from 'vite-plugin-node-polyfills';

export default defineConfig({
  plugins: [vue(),

    nodePolyfills({
      // 配置需要 polyfill 的模块
      globals: {
        global: true,
      },
    }),

  ],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
      '#': path.resolve(__dirname, './types')
    }
  },

  server: {
    proxy: {
      '/ws': {
        target: 'http://localhost:8090',
        ws: true, 
        changeOrigin: true,
        // rewrite: (path) => path.replace(/^\/ws/, '/ws'),
      },
      '/api':
      {
        target: 'http://localhost:8090/api/v1',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    },
    
  }

})
