// src/store/doctor.ts
import { defineStore } from 'pinia'
import service from '@/utils/request'

export interface Doctor {
  id: number
  name: string
  title: string
  introduction: string
}

export interface Schedule {
  workDate: string
  timeSlots: TimeSlot[]
}

export interface TimeSlot {
  time: string
  status: 'available' | 'booked' | 'closed'
  originalStatus: 'available' | 'booked' | 'closed'
}

export const useDoctorStore = defineStore('doctor', {
  state: () => ({
    doctors: [] as Doctor[],
    currentDoctor: null as Doctor | null,
    schedules: [] as Schedule[],
    loading: false,
    modifiedSlots: new Map<string, 'available' | 'booked' | 'closed'>()
  }),

  actions: {
    async fetchDoctors() {
      this.loading = true
      try {
        const res = await service.get('/admin/doctors')
        this.doctors = res.data
      } finally {
        this.loading = false
      }
    },

    async fetchDoctorDetail(id: number) {
      this.loading = true
      try {
        const res = await service.get(`/admin/doctors/${id}`)
        this.currentDoctor = res.data
        await this.fetchSchedules(id)
      } finally {
        this.loading = false
      }
    },

    async fetchSchedules(id: number) {
      this.loading = true
      try {
        const res = await service.get(`/admin/doctors/schedules/${id}`)
        this.schedules = res.data.map((s: any) => ({
          workDate: s.workDate,
          timeSlots: Object.entries(s.timeSlots).map(([time, status]) => ({
            time,
            status: status as 'available' | 'booked' | 'closed',
            originalStatus: status as 'available' | 'booked' | 'closed'
          }))
        }))
        this.modifiedSlots.clear()
      } finally {
        this.loading = false
      }
    },

    async batchUpdateSchedule(doctorId: number) {
      this.loading = true
      try {
        const updates = Array.from(this.modifiedSlots).reduce((acc, [key, status]) => {
          const [date, time] = key.split('|')
          acc[date] = acc[date] || {}
          acc[date][time] = status
          return acc
        }, {} as Record<string, Record<string, 'available' | 'booked' | 'closed'>>)

        await service.put('/admin/doctors/schedules', {
          doctorId,
          updates
        })
        
        // 更新本地数据
        for (const [date, slots] of Object.entries(updates)) {
          const schedule = this.schedules.find(s => s.workDate === date)
          if (schedule) {
            schedule.timeSlots = schedule.timeSlots.map(slot => ({
              ...slot,
              status: slots[slot.time] || slot.status,
              originalStatus: slots[slot.time] || slot.originalStatus
            }))
          }
        }
        
        this.modifiedSlots.clear()
        return true
      } finally {
        this.loading = false
      }
    },

    async createSchedule(doctorId: number, date: string, timeSlots: string[]) {
      this.loading = true
      try {
        const slots = timeSlots.reduce((acc, time) => {
          acc[time] = 'available'
          return acc
        }, {} as Record<string, 'available'>)

        await service.put('/admin/doctors/schedules', {
          doctorId,
          workDate: date,
          timeSlots: slots
        })

        this.schedules.push({
          workDate: date,
          timeSlots: Object.entries(slots).map(([time, status]) => ({
            time,
            status,
            originalStatus: status
          }))
        })
      } finally {
        this.loading = false
      }
    }
  }
})
