import { defineStore } from 'pinia'
import service from '@/utils/request'
import type { User } from '@/types/user'

interface UserState {
  users: User[]
  total: number
  currentPage: number
  pageSize: number
}

export const useAdminUserStore = defineStore('adminUser', {
  state: (): UserState => ({
    users: [],
    total: 0,
    currentPage: 1,
    pageSize: 10
  }),
  actions: {
    async fetchUsers() {
      try {
        const response = await service.get('/admin/users', {
          params: { 
            page: this.currentPage,
            size: this.pageSize,
            timestamp: Date.now()
          }
        })
        
        this.users = response.data.records.map((user: any) => ({
          ...user,
          id: user.id.toString(),
          isEnabled: user.enabled
        }))
        
        this.total = response.data.total
      } catch (error) {
        console.error('获取用户失败:', error)
        throw error
      }
    },

    async updateRole(id: number, newRole: string) {
      await service.put(`/admin/users/${id}/role`, null, { 
        params: { newRole } 
      })
      const user = this.users.find(u => u.id === id)
      if (user) user.role = newRole as User['role']
    },

    async resetPassword(id: string) {
      const response = await service.post(`/admin/users/${id}/reset-password`)
      return response.data
    },

    async toggleStatus(id: number, enabled: boolean) {
      await service.put(`/admin/users/${id}/status`, null, { 
        params: { enabled } 
      })
      const user = this.users.find(u => u.id === id)
      if (user) user.isEnabled = enabled
    }
  }
})
