export interface ParkingSpace {
    id: string
    zoneCode: string
    number: string
    status: 'FREE' | 'RESERVED' | 'OCCUPIED'
    qrCode?: string
    lastStatusTime: string
    createTime: string
    version: number
  }
  
  export interface ParkingSpaceCreateDTO {
    zoneCode: string
    number: string
    status?: 'FREE' | 'RESERVED' | 'OCCUPIED'
    qrCode?: string
  }
  
  export interface ParkingSpaceUpdateDTO extends ParkingSpaceCreateDTO {
    id: string
    version: number
  }
  