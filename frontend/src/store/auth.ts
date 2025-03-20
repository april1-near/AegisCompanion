// src/store/auth.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { type LoginResponse, type User } from '@/types/user.d'
import request from '@/utils/request'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref<User | null>(null)

  // 监听token变化自动管理WebSocket连

  const login = async (credentials: { username: string; password: string }) => {
    try {
      const { data } = await request.post<LoginResponse>('/auth/login', credentials)
      const tokenResp = data.token
      localStorage.setItem('token', tokenResp)
      token.value = tokenResp
      await fetchUser()
    } catch (error) {
      console.error('登录失败:', error)
      throw error
    }
  }

  const register = async (credentials: { username: string; password: string; phone: string }) => {
    try {
      await request.post('/auth/register', credentials)
    } catch (error) {
      console.error('注册失败:', error)
      throw error
    }
  }

  const fetchUser = async () => {
    try {
      const { data } = await request.get<User>('/users/me')
      user.value = data
    } catch (error) {
      console.error('获取用户信息失败:', error)
      throw error
    }
  }

  const logout = async () => {
    try {
      console.info('正在退出登录')

      token.value = ''
      user.value = null
      localStorage.removeItem('token')
      
      // await request.post('/auth/logout')
    } catch (error) {
      console.error('退出登录失败:', error)
      throw error
    }

    
  }


  return { 
    token,
    user,
    login,
    register,
    logout,
    fetchUser
  }
}, {
  persist: true // 启用持久化
})
