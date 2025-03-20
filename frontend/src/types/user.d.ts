// src/types/user.d.ts
export interface User {
  id: number
  username: string
  email: string
  phone?: string
  role: 'USER' | 'MAINTENANCE' | 'ADMIN' | 'SUPER_ADMIN'
  isEnabled: boolean
  createTime: string
}

export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

export interface LoginResponse {
  token: string;
}

export interface UserUpdateDTO {
  username?: string
  phone?: string
  email?: string
  isEnabled?: boolean
}

export interface PasswordChangeDTO {
  oldPassword: string
  newPassword: string
}
