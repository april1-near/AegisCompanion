<template>
  <div class="spaces-list">
    <div class="zone-buttons">
      <el-button v-for="zone in zones" :key="zone" @click="selectZone(zone)" :type="selectedZone === zone ? 'primary' : 'default'">
        {{ zone }}区
      </el-button>
    </div>
    <el-table :data="spaces" style="width: 100%">
      <el-table-column prop="id" label="车位ID" width="180" />
      <el-table-column prop="zoneCode" label="区域代码" width="180" />
      <el-table-column prop="statusDesc" label="状态" />
      <el-table-column prop="lastStatusTime" label="最后变更时间" />
      <el-table-column label="操作">
        <template v-slot="scope">
          <el-button v-if="scope.row.statusDesc === '空闲可预约'" type="success" @click="reserveSpace(scope.row.id)">预约</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useParkingStore } from '@/store/parking'

const parkingStore = useParkingStore()
const spaces = ref([])
const selectedZone = ref('A')
const zones = ['A', 'B', 'C', 'D']

const fetchSpaces = async () => {
  await parkingStore.fetchAvailableSpaces(selectedZone.value)
  spaces.value = parkingStore.availableSpaces
}

const selectZone = (zone: string) => {
  selectedZone.value = zone
  fetchSpaces()
}

const reserveSpace = async (id: number) => {
  await parkingStore.createReservation(id)
  fetchSpaces()
}

onMounted(fetchSpaces)
</script>

<style scoped>
.spaces-list {
  padding: 20px;
}

.zone-buttons {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}
</style>
