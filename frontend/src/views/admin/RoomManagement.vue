<!-- views/admin/RoomManagement.vue -->
<template>
  <div class="room-management">
    <el-card>
      <template #header>
        <div class="flex justify-between items-center">
          <span>活动室管理</span>
          <el-button type="primary" @click="showCreateDialog">新增活动室</el-button>
        </div>
      </template>

      <el-table :data="store.rooms" v-loading="store.loading">
        <el-table-column prop="roomName" label="名称" />
        <el-table-column prop="roomType" label="类型" />
        <el-table-column prop="maxCapacity" label="最大容量" />
        <el-table-column label="开放时间">
          <template #default="{ row }">{{ row.openHour }} - {{ row.closeHour }}</template>
        </el-table-column>
        <el-table-column prop="isActive" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.isActive ? 'success' : 'danger'">
              {{ row.isActive ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button size="small" @click="editRoom(row)">编辑</el-button>
            <el-button 
              size="small" 
              :type="row.isActive ? 'danger' : 'success'"
              @click="toggleStatus(row.id, !row.isActive)"
            >
              {{ row.isActive ? '停用' : '启用' }}
            </el-button>
            <el-popconfirm title="确认删除该活动室？" @confirm="store.deleteRoom(row.id)">
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 创建/编辑对话框 -->
    <el-dialog v-model="store.dialogVisible" :title="store.currentRoom ? '编辑活动室' : '新建活动室'">
      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-form-item label="活动室名称" prop="roomName">
          <el-input v-model="form.roomName" />
        </el-form-item>
        <el-form-item label="类型" prop="roomType">
          <el-select v-model="form.roomType">
            <el-option 
              v-for="t in roomTypes" 
              :key="t.value" 
              :label="t.label" 
              :value="t.value" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="最大容量" prop="maxCapacity">
          <el-input-number v-model="form.maxCapacity" :min="1" />
        </el-form-item>
        <el-form-item label="开放时间" required>
          <el-time-picker
            v-model="openTimeRange"
            is-range
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="HH:mm"
          />
        </el-form-item>
        <el-form-item v-if="store.currentRoom" label="启用状态">
          <el-switch v-model="form.isActive" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="store.dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { useRoomAdminStore } from '@/store/admin/activity/roomAdmin'
import { ElMessage, type FormInstance } from 'element-plus'
import { computed, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import type { RoomItem, RoomType } from '@/types/room'

const store = useRoomAdminStore()
const formRef = ref<FormInstance>()

const roomTypes = [
  { value: 'MEETING', label: '会议室' },
  { value: 'SPORTS', label: '运动场馆' },
  { value: 'ENTERTAINMENT', label: '娱乐设施' }
]
  
const form = reactive({
  roomName: '',
  roomType: 'MEETING' as RoomType,
  maxCapacity: 10,
  openHour: '08:00',
  closeHour: '22:00',
  isActive: true
})

const openTimeRange = computed({
  get: () => [
    dayjs(form.openHour, 'HH:mm').toDate(), 
    dayjs(form.closeHour, 'HH:mm').toDate()
  ],
  set: ([start, end]: Date[]) => {
    form.openHour = dayjs(start).format('HH:mm')
    form.closeHour = dayjs(end).format('HH:mm')
  }
})

const rules = {
  roomName: [{ required: true, message: '请输入活动室名称', trigger: 'blur' }],
  roomType: [{ required: true, message: '请选择活动室类型', trigger: 'change' }],
  maxCapacity: [{ required: true, message: '请输入最大容量', trigger: 'blur' }]
}

const showCreateDialog = () => {
  Object.assign(form, {
    roomName: '',
    roomType: 'MEETING',
    maxCapacity: 10,
    openHour: '08:00',
    closeHour: '22:00',
    isActive: true
  })
  store.dialogVisible = true
}

const editRoom = (room: RoomItem) => {
  Object.assign(form, {
    roomName: room.roomName,
    roomType: room.roomType,
    maxCapacity: room.maxCapacity,
    openHour: room.openHour,
    closeHour: room.closeHour,
    isActive: room.isActive
  })
  store.currentRoom = room
  store.dialogVisible = true
}

const toggleStatus = (id: number, active: boolean) => {
  store.toggleRoomStatus(id, active)
}

const submitForm = async () => {
  try {
    await formRef.value?.validate()
    if (store.currentRoom) {
      await store.updateRoom({ ...form, id: store.currentRoom.id })
    } else {
      await store.createRoom(form)
    }
    store.dialogVisible = false
    ElMessage.success('操作成功')
  } catch (e) {
    console.error(e)
  }
}

// 初始化加载
store.fetchRooms()
</script>
