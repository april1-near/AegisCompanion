import { defineStore } from 'pinia'
import service from '@/utils/request'
import type { ParkingSpace, ParkingSpaceCreateDTO, ParkingSpaceUpdateDTO } from '@/types/parking'

interface ParkingState {
  allSpaces: ParkingSpace[]
  filteredSpaces: ParkingSpace[]
  currentPage: number
  pageSize: number
  zoneFilter: string
  loading: boolean
}

export const useParkingManageStore = defineStore('parkingManage', {
  state: (): ParkingState => ({
    allSpaces: [],
    filteredSpaces: [],
    currentPage: 1,
    pageSize: 10,
    zoneFilter: '',
    loading: false
  }),

  actions: {
    async fetchAllData() {
      this.loading = true
      try {
        const response = await service.get('/admin/parking/spaces', {
          params: { pageNum: 1, pageSize: 1000 }
        })
        this.allSpaces = response.data.records
        this.applyFilter()
      } finally {
        this.loading = false
      }
    },

    applyFilter() {
      let result = this.allSpaces
      if (this.zoneFilter) {
        result = result.filter(s => s.zoneCode === this.zoneFilter)
      }
      this.filteredSpaces = result
      this.updatePagination()
    },

    updatePagination() {
      const start = (this.currentPage - 1) * this.pageSize
      this.filteredSpaces = this.filteredSpaces.slice(start, start + this.pageSize)
    },

    async addSpace(dto: ParkingSpaceCreateDTO) {
      const response = await service.post('/admin/parking/spaces', dto)
      await this.fetchAllData()
      return response.data
    },

    async updateSpace(dto: ParkingSpaceUpdateDTO) {
      await service.put(`/admin/parking/spaces/${dto.id}`, dto)
      await this.fetchAllData()
    },

    async deleteSpace(id: string) {
      await service.delete(`/admin/parking/spaces/${id}`)
      await this.fetchAllData()
    },

    async forceRelease(spaceId: string) {
      await service.post(`/admin/parking/release/${spaceId}`)
      await this.fetchAllData()
    }
  },

  getters: {
    total: (state) => state.filteredSpaces.length,
    spaces: (state) => state.filteredSpaces
  }
})
