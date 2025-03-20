// types/room.d.ts
export interface RoomCreateForm {
    roomName: string
    roomType: RoomType
    maxCapacity: number
    openHour: string
    closeHour: string
  }
  
  export interface RoomUpdateForm extends RoomCreateForm {
    id: number
    isActive: boolean
  }
  
  export interface BookingRecord {
    id: number
    userName: string
    roomName: string
    purpose: string
    participantCount: number
    timeRange: string
    bookingStatus: string
  }
  
  export interface BookingApproveParams {
    bookingId: number
    approved: boolean
    comment?: string
  }
  
  export type RoomType = 'MEETING' | 'GYM' | 'MULTIPURPOSE' // 根据实际枚举调整
  
  export interface RoomItem {
    id: number
    roomName: string
    roomType: RoomType
    maxCapacity: number
    openHour: string
    closeHour: string
    isActive: boolean
    createTime: string
  }
  