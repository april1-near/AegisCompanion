// src/store/ticket.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export interface Ticket {
  id: string
  title: string
  description: string
  type: string
  stateDesc: string
  creatorName: string
  assigneeName?: string
  createTime: string
  logs: TicketLog[]
}

export interface TicketLog {
  fromState: string
  toState: string
  createTime: string
  remark?: string
}

export const useTicketStore = defineStore('ticket', () => {
  const myTickets = ref<Ticket[]>([])
  const assignedTickets = ref<Ticket[]>([])

  // 普通用户接口
  const fetchMyTickets = async () => {
    const response = await request.get('/tickets/my')
    myTickets.value = response.data.map(convertTicket)
  }

  const createTicket = async (dto: { title: string; description: string; type: string }) => {
    await request.post('/tickets', dto)
    await fetchMyTickets()
  }

  const confirmCompletion = async (ticketId: string) => {
    await request.post(`/tickets/${ticketId}/confirm`)
    await fetchMyTickets()
  }

  // 维修人员接口
  const fetchAssignedTickets = async () => {
    const response = await request.get('/tickets/assigned')
    assignedTickets.value = response.data.map(convertTicket)
  }

  const startProcessing = async (ticketId: string) => {
    await request.post(`/tickets/${ticketId}/start-processing`)
    await fetchAssignedTickets()
  }

  const submitForReview = async (ticketId: string) => {
    await request.post(`/tickets/${ticketId}/submit-review`)
    await fetchAssignedTickets()
  }

// src/store/ticket.ts
const convertTicket = (ticket: any): Ticket => ({
    ...ticket,
    id: ticket.id.toString(),
    // 直接使用原始时间字符串
    createTime: ticket.createTime,
    logs: ticket.logs?.map((log: any) => ({
      ...log,
      // 直接使用原始时间字符串
      createTime: log.createTime
    })) || []
  })
  

  return {
    myTickets,
    assignedTickets,
    fetchMyTickets,
    createTicket,
    confirmCompletion,
    fetchAssignedTickets,
    startProcessing,
    submitForReview
  }
})
