import { defineStore } from 'pinia'
import { getMyAppointments, getDoctors, getDoctorSchedule, createAppointment, cancelAppointment } from '@/api/medical'

export const useMedicalStore = defineStore('medical', {
  state: () => ({
    myAppointments: [],
    doctors: [],
    doctorSchedules: []
  }),
  actions: {
    async fetchMyAppointments() {
      const response = await getMyAppointments()
      this.myAppointments = response.data
    },
    async fetchDoctors() {
      const response = await getDoctors()
      this.doctors = response.data
    },
    async fetchDoctorSchedule(doctorId: number) {
      const response = await getDoctorSchedule(doctorId)
      this.doctorSchedules = response.data
    },
    async createAppointment(doctorId: number, date: string, timeSlot: string) {
      await createAppointment({ doctorId, appointDate: date, timeSlot })
      await this.fetchMyAppointments()
    },
    async cancelAppointment(id: string) {
      await cancelAppointment(id)
      await this.fetchMyAppointments()
    }
  }
})
