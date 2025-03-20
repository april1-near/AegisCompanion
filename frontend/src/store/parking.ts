import { defineStore } from 'pinia'
import { getAvailableSpaces, getCurrentReservation, createReservation, cancelReservation, confirmOccupancy, leaveParking } from '@/api/parking'

export const useParkingStore = defineStore('parking', {
  state: () => ({
    availableSpaces: [],
    currentReservation: []
  }),
  actions: {
    async fetchAvailableSpaces(zone: string) {
      const response = await getAvailableSpaces(zone)
      this.availableSpaces = response.data
    },
    async fetchCurrentReservation() {
      const response = await getCurrentReservation()
      this.currentReservation = response.data
    },
    async createReservation(spaceId: number) {
      await createReservation({ spaceId })
      await this.fetchCurrentReservation()
    },
    async cancelReservation(id: number) {
      await cancelReservation(id)
      await this.fetchCurrentReservation()
    },
    async confirmOccupancy(id: number) {
      await confirmOccupancy(id)
      await this.fetchCurrentReservation()
    },
    async leaveParking(id: number) {
      await leaveParking(id)
      await this.fetchCurrentReservation()
    }
  }
})
