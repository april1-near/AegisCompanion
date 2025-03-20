// stores/bookingAdmin.ts
import { defineStore } from 'pinia'
import request from '@/utils/request'
import type { BookingRecord, BookingApproveParams } from '@/types/room'

export const useBookingAdminStore = defineStore('bookingAdmin', {
  state: () => ({
    bookings: [] as BookingRecord[],
    loading: false,
    pagination: {
      current: 1,
      size: 10,
      total: 0
    },
    queryParams: {
      status: 'PENDING',
      roomId: undefined as number | undefined,
      startTimeBegin: '',
      startTimeEnd: ''
    }
  }),
  actions: {
    async fetchBookings() {
      this.loading = true
      try {
        const response = await request.post('/admin/rooms/bookings', 
          {
            status: this.queryParams.status,
            roomId: this.queryParams.roomId,
            startTimeBegin: this.queryParams.startTimeBegin,
            startTimeEnd: this.queryParams.startTimeEnd
          },
          {
            params: {
              current: this.pagination.current,
              size: this.pagination.size
            }
          }
        )
        
        const responseData = response.data
        console.log('获取审批记录:',response.data)

        this.bookings = responseData?.records || []
        this.pagination.total = responseData?.total || 0
        this.pagination.current = responseData?.current || 1
      } catch (e) {
        console.error('获取审批记录失败:', e)
        throw e
      } finally {
        this.loading = false
      }
    },

    async approveBooking(params: BookingApproveParams) {
      try {
        await request.put('/admin/rooms/approval', params)
        await this.fetchBookings()
        return true
      } catch (e) {
        console.error('审批操作失败:', e)
        throw e
      }
    },
    
    resetQuery() {
      this.queryParams = {
        status: 'PENDING',
        roomId: undefined,
        startTimeBegin: '',
        startTimeEnd: ''
      }
    }
  }
})
