<template>
  <div class="reservations">
    <el-card v-for="reservation in reservations" :key="reservation.id" class="reservation-card">
      <div slot="header" class="card-header">
        <span>预约号: {{ reservation.id }}</span>
        <el-tag :type="getStatusTagType(reservation.status)">{{ formatStatus(reservation.status) }}</el-tag>
      </div>
      <div class="card-content">
        <p><strong>用户ID:</strong> {{ reservation.userId }}</p>
        <p><strong>车位ID:</strong> {{ reservation.spaceId }}</p>
        <p><strong>预约时间:</strong> {{ reservation.reserveTime }}</p>
        <p><strong>过期时间:</strong> {{ reservation.expireTime }}</p>
        <p><strong>实际使用时间:</strong> {{ formatActualUseTime(reservation.actualUseTime) }}</p>
        <div class="card-actions">
          <el-button v-if="reservation.status === 'active'" type="danger" @click="cancelReservation(reservation.id)">取消预约</el-button>
          <el-button v-if="reservation.status === 'active'" type="success" @click="confirmOccupancy(reservation.id)">确认到达</el-button>
          <el-button v-if="reservation.status === 'arrived'" type="warning" @click="leaveParking(reservation.id)">确认离开</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useParkingStore } from '@/store/parking'

const parkingStore = useParkingStore()
interface Reservation {
  id: number;
  userId: number;
  spaceId: number;
  reserveTime: string;
  expireTime: string;
  actualUseTime: string | null;
  status: string;
}

const reservations = ref<Reservation[]>([])

onMounted(async () => {
  await parkingStore.fetchCurrentReservation()
  reservations.value = parkingStore.currentReservation ?? []
})

const cancelReservation = async (id: number) => {
  await parkingStore.cancelReservation(id)
  reservations.value = parkingStore.currentReservation ?? []
}

const confirmOccupancy = async (id: number) => {
  await parkingStore.confirmOccupancy(id)
  reservations.value = parkingStore.currentReservation ?? []
}

const leaveParking = async (id: number) => {
  await parkingStore.leaveParking(id)
  reservations.value = parkingStore.currentReservation ?? []
}

const formatActualUseTime = (actualUseTime: string | null) => {
  return actualUseTime ? new Date(actualUseTime).toLocaleTimeString() : '未使用'
}

const formatStatus = (status: string) => {
  const statusMap: { [key: string]: string } = {
    active: '生效中',
    arrived: '已到达车位',
    completed: '已完成使用',
    expired: '已过期',
    canceled: '已取消'
  }
  return statusMap[status] || status
}

const getStatusTagType = (status: string) => {
  const tagTypeMap: { [key: string]: string } = {
    active: 'success',
    arrived: 'info',
    completed: 'primary',
    expired: 'warning',
    canceled: 'danger'
  }
  return tagTypeMap[status] || 'default'
}
</script>

<style scoped>
.reservations {
  padding: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.reservation-card {
  width: 300px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #f5f7fa;
  padding: 10px;
}

.card-content {
  padding: 10px;
}

.card-actions {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
}
</style>
