// src/types/parking.d.ts
export type ParkingStatus = 'FREE' | 'RESERVED' | 'OCCUPIED';

export interface ParkingSpace {
  id: string
  zoneCode: string
  number: string
  status: ParkingStatus  // 修改点：使用统一类型
  qrCode?: string
  lastStatusTime: string
  createTime: string
  version: number
}

export interface ParkingSpaceCreateDTO {
  zoneCode: string
  number: string
  status?: ParkingStatus  // 修改点：使用统一类型
  qrCode?: string
}

export interface ParkingSpaceUpdateDTO extends ParkingSpaceCreateDTO {
  id: string
  version: number
}
