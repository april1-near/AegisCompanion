import request from '@/utils/request'

export const getAvailableSpaces = (zone: string) => {
  return request({
    url: '/parking/spaces',
    method: 'get',
    params: { zone }
  })
}

export const createReservation = (data: { spaceId: number }) => {
  return request({
    url: '/parking/reservations',
    method: 'post',
    data
  })
}

export const getCurrentReservation = () => {
  return request({
    url: '/parking/reservations/current',
    method: 'get'
  })
}

export const cancelReservation = (id: number) => {
  return request({
    url: `/parking/reservations/${id}`,
    method: 'delete'
  })
}

export const confirmOccupancy = (id: number) => {
  return request({
    url: `/parking/reservations/${id}/confirm`,
    method: 'post'
  })
}

export const leaveParking = (id: number) => {
  return request({
    url: `/parking/reservations/${id}/leave`,
    method: 'post'
  })
}
