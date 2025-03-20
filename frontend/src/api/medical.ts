import request from '@/utils/request'

export const getMyAppointments = () => {
  return request({
    url: '/Medical/my-appointments',
    method: 'get'
  })
}

export const getDoctors = () => {
  return request({
    url: '/Medical/doctors',
    method: 'get'
  })
}

export const getDoctorSchedule = (doctorId: number) => {
  return request({
    url: `/Medical/doctors/${doctorId}/schedules`,
    method: 'get'
  })
}

export const createAppointment = (data: { doctorId: number, appointDate: string, timeSlot: string }) => {
  return request({
    url: '/Medical',
    method: 'post',
    data
  })
}

export const cancelAppointment = (id: String) => {
  return request({
    url: `/Medical/${id}`,
    method: 'delete'
  })
}
