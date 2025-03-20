<template>
  <div class="admin-manage">
    <el-button type="primary" @click="addParkingSpace">添加车位</el-button>
    <el-table :data="spaces" style="width: 100%">
      <el-table-column prop="id" label="车位ID" width="180" />
      <el-table-column prop="zoneCode" label="区域代码" width="180" />
      <el-table-column prop="statusDesc" label="状态" />
      <el-table-column label="操作">
        <template v-slot:default="scope">
          <el-button type="danger" @click="deleteParkingSpace(scope.row.id)">删除</el-button>
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

onMounted(async () => {
  await parkingStore.fetchAvailableSpaces('default')
  spaces.value = parkingStore.availableSpaces
})

const addParkingSpace = () => {
  // 添加车位逻辑
}

const deleteParkingSpace = async (id: number) => {
  // 删除车位逻辑
  await parkingStore.fetchAvailableSpaces('default')
  spaces.value = parkingStore.availableSpaces
}
</script>

<style scoped>
.admin-manage {
  padding: 20px;
}
</style>
