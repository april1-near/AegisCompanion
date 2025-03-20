// src/store/user.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'
import type { ApiResponse } from '@/types/user'
import { useAuthStore } from './auth'
import dayjs from 'dayjs'

interface UserUpdateDTO {
  username?: string
  phone?: string
  email?: string
  isEnabled?: boolean
}

interface PasswordChangeDTO {
  oldPassword: string
  newPassword: string
}

export const useUserStore = defineStore('user', () => {
  const authStore = useAuthStore()

  const updateUserInfo = async (id: number, dto: UserUpdateDTO) => {
    await request.put(`/users/${id}`, dto)
    await authStore.fetchUser()
  }

  const changePassword = async (id: number, dto: PasswordChangeDTO) => {
    await request.put(`/users/${id}/password`, dto)
  }

  return { updateUserInfo, changePassword }
})
