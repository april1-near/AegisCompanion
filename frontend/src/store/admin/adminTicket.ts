// src/store/adminTicket.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export interface AdminTicket {
  id: string
  title: string
  description: string
  type: string
  stateDesc: string
  creatorName: string
  assigneeName?: string
  createTime: string
  reviewerId?: string
  logs: AdminTicketLog[]
}

export interface AdminTicketLog {
  fromState: string
  toState: string
  createTime: string
  remark?: string
}

export interface ReviewForm {
  isApproved: boolean
  remark: string
}

export const useAdminTicketStore = defineStore('adminTicket', () => {
  const allTickets = ref<AdminTicket[]>([])

  const fetchAllTickets = async () => {
    const response = await request.get('/tickets/admin/all')
    allTickets.value = response.data.map(convertTicket)
  }

  const handleReview = async (ticketId: string, form: ReviewForm) => {
    await request.post(`/tickets/${ticketId}/handle-review`, form)
    await fetchAllTickets()
  }

  const convertTicket = (ticket: any): AdminTicket => ({
    ...ticket,
    id: ticket.id.toString(),
    createTime: ticket.createTime,
    logs: ticket.logs?.map((log: any) => ({
      ...log,
      createTime: log.createTime
    })) || []
  })

  return {
    allTickets,
    fetchAllTickets,
    handleReview
  }
})
