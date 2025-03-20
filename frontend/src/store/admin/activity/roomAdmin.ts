// stores/roomAdmin.ts
import { defineStore } from 'pinia'
import request from '@/utils/request'
import type { RoomItem, RoomCreateForm, RoomUpdateForm } from '@/types/room'

export const useRoomAdminStore = defineStore('roomAdmin', {
  state: () => ({
    rooms: [] as RoomItem[],
    loading: false,
    dialogVisible: false,
    currentRoom: null as RoomItem | null
  }),
  actions: {
    async fetchRooms() {
      this.loading = true
      try {
        const { data } = await request.get('/admin/rooms')
        this.rooms = data
      } finally {
        this.loading = false
      }
    },

    async createRoom(form: RoomCreateForm) {
      await request.post('/admin/rooms', form)
      await this.fetchRooms()
    },

    async updateRoom(form: RoomUpdateForm) {
      await request.put(`/admin/rooms/${form.id}`, form)
      await this.fetchRooms()
    },

    async deleteRoom(id: number) {
      await request.delete(`/admin/rooms/${id}`)
      await this.fetchRooms()
    },

    async toggleRoomStatus(id: number, active: boolean) {
        try {
          const url = `/admin/rooms/${id}/${active ? 'activate' : 'deactivate'}`
          await request.post(url)
          
          // 本地更新状态
          const index = this.rooms.findIndex(r => r.id === id)
          if (index > -1) {
            this.rooms[index].isActive = active
          }
        } catch (e) {
          console.error('状态更新失败', e)
          throw e
        }
      }
      
  }
})
