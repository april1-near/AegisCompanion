// src/store/activity.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'
import dayjs from 'dayjs'

interface BookingRecord {
  id: string
  purpose: string
  timeRange: string
  status: string
  startTime: string
  endTime: string
  roomName: string
  participantCount: number
  rejectReason?: string
}

export interface CommunityRoom {
  id: string
  roomName: string
  roomType: string
  maxCapacity: number
  openHour: string
  closeHour: string
  isActive: boolean
}

export const useActivityStore = defineStore('activity', () => {
  const availableRooms = ref<CommunityRoom[]>([])
  const userBookings = ref<BookingRecord[]>([])

  const fetchAvailableRooms = async () => {
    const response = await request.get('/room-bookings/available-rooms')
    availableRooms.value = response.data
  }

  const fetchUserBookings = async () => {
    const response = await request.get('/room-bookings')
    userBookings.value = response.data.records.map((record: any) => ({
      id: record.id.toString(),
      purpose: record.purpose,
      timeRange: record.timeRange,
      status: record.status,
      startTime: record.timeRange.split(' ~ ')[0],
      endTime: record.timeRange.split(' ~ ')[1],
      roomName: record.roomName || '未知活动室',
      participantCount: record.participantCount
    }))
  }

  const applyBooking = async (
    roomId: string,
    purpose: string,
    participantCount: number,
    startTime: string,
    endTime: string
  ) => {
    await request.post('/room-bookings', {
      roomId: Number(roomId),
      purpose,
      participantCount,
      startTime: dayjs(startTime).format('YYYY-MM-DD HH:mm'),
      endTime: dayjs(endTime).format('YYYY-MM-DD HH:mm')
    })
    await fetchUserBookings()
  }

  const cancelBooking = async (bookingId: string) => {
    await request.delete(`/room-bookings/${bookingId}`)
    await fetchUserBookings()
  }

  return { 
    availableRooms, 
    userBookings,
    fetchAvailableRooms,
    fetchUserBookings,
    applyBooking,
    cancelBooking
  }
})
