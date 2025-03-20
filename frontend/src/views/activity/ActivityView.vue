<!-- src/views/ActivityView.vue -->
<template>
  <div class="activity-container">
    <!-- 左侧预约列表 -->
    <div class="booking-list">
      <div class="header">
        <h3>我的预约记录</h3>
        <el-button type="primary" @click="showRoomSelector" :icon="Plus">
          新建预约
        </el-button>
      </div>

      <el-card shadow="hover" v-for="booking in store.userBookings" :key="booking.id" class="booking-card">
        <template #header>
          <div class="card-header">
            <span>
              {{ booking.roomName !== '未知活动室' ? `${booking.roomName} - ` : '' }}
              {{ booking.purpose }}
            </span>
            <el-tag :type="statusTypeMap[booking.status]">
              {{ booking.status }}
            </el-tag>
          </div>
        </template>

        <div class="booking-info">
          <div class="time-range">
            <el-icon>
              <Clock />
            </el-icon>
            {{ booking.timeRange }}
          </div>
          <div class="participants">
            <el-icon>
              <User />
            </el-icon>
            参与人数：{{ booking.participantCount }}
          </div>
        </div>

        <template #footer v-if="booking.status === '待审批'">
          <el-button type="danger" size="small" @click="handleCancel(booking.id)">
            取消预约
          </el-button>
        </template>
      </el-card>
    </div>

    <!-- 活动室选择弹窗 -->
    <el-dialog v-model="showRoomDialog" title="选择活动室" width="80%">
      <div class="room-grid">
        <el-card v-for="room in store.availableRooms" :key="room.id" class="room-card">
          <template #header>
            <div class="room-header">
              <h4>{{ room.roomName }}</h4>
              <el-tag effect="plain">{{ room.roomType }}</el-tag>
            </div>
          </template>

          <div class="room-info">
            <div class="capacity">
              <el-icon>
                <User />
              </el-icon>
              最大容量：{{ room.maxCapacity }}人
            </div>
            <div class="time-range">
              <el-icon>
                <Clock />
              </el-icon>
              开放时间：{{ room.openHour }} ~ {{ room.closeHour }}
            </div>
          </div>

          <template #footer>
            <el-button type="primary" size="small" @click="showBookingForm(room)">
              立即预约
            </el-button>
          </template>
        </el-card>
      </div>
    </el-dialog>

    <!-- 预约表单弹窗 -->
    <el-dialog v-model="showFormDialog" :title="`预约 ${selectedRoom?.roomName}`">
      <el-form :model="form" :rules="rules" ref="bookingForm" label-width="100px">
        <el-form-item label="用途说明" prop="purpose">
          <el-input v-model="form.purpose" type="textarea" :rows="3" maxlength="200" show-word-limit />
        </el-form-item>

        <el-form-item label="参与人数" prop="participantCount">
          <el-input-number v-model="form.participantCount" :min="1" :max="selectedRoom?.maxCapacity" />
        </el-form-item>

        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker v-model="form.startTime" type="datetime" placeholder="选择开始时间"
            value-format="YYYY-MM-DD HH:mm" />
        </el-form-item>

        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker v-model="form.endTime" type="datetime" placeholder="选择结束时间" value-format="YYYY-MM-DD HH:mm" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showFormDialog = false">取消</el-button>
        <el-button type="primary" @click="submitBooking">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Clock, User, Plus } from '@element-plus/icons-vue'
import { useActivityStore } from '@/store/activity'
import type { CommunityRoom } from '@/store/activity'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'

const store = useActivityStore()

// 状态管理
const showRoomDialog = ref(false)
const showFormDialog = ref(false)
const selectedRoom = ref<CommunityRoom | null>(null)
const bookingForm = ref<FormInstance>()

// 表单数据
const form = ref({
  purpose: '',
  participantCount: 1,
  startTime: '',
  endTime: ''
})

// 状态类型映射
const statusTypeMap: Record<string, string> = {
  '待审批': 'warning',
  '已通过': 'success',
  '已拒绝': 'danger',
  '已完成': 'info',
  '已取消': 'info'
}

// 表单验证规则
const rules = {
  purpose: [
    { required: true, message: '请输入用途说明', trigger: 'blur' },
    { max: 200, message: '长度不能超过200字', trigger: 'blur' }
  ],
  participantCount: [
    { required: true, message: '请填写参与人数', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ]
}

// 初始化加载数据
onMounted(() => {
  store.fetchUserBookings()
})

// 显示活动室选择
const showRoomSelector = async () => {
  try {
    await store.fetchAvailableRooms()
    showRoomDialog.value = true
  } catch (error) {
    ElMessage.error('获取活动室列表失败')
  }
}

// 显示预约表单
const showBookingForm = (room: CommunityRoom) => {
  selectedRoom.value = room
  showRoomDialog.value = false
  showFormDialog.value = true
}

// 提交预约
const submitBooking = async () => {
  if (!selectedRoom.value) return

  try {
    await bookingForm.value?.validate()

    await store.applyBooking(
      selectedRoom.value.id,
      form.value.purpose,
      form.value.participantCount,
      form.value.startTime,
      form.value.endTime
    )

    ElMessage.success('预约申请已提交')
    showFormDialog.value = false
    form.value = {
      purpose: '',
      participantCount: 1,
      startTime: '',
      endTime: ''
    }
  } catch (error) {
    console.error('预约失败:', error)
  }
}

// 取消预约
const handleCancel = (id: string) => {
  ElMessageBox.confirm('确定要取消该预约吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await store.cancelBooking(id)
    ElMessage.success('预约已取消')
  })
}
</script>

<style lang="scss" scoped>
.activity-container {
  display: flex;
  gap: 20px;
  height: 100%;

  .booking-list {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 16px;

    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
    }

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .booking-info {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 12px;

      .time-range,
      .participants {
        display: flex;
        align-items: center;
        gap: 8px;
        color: var(--el-text-color-secondary);
      }
    }
  }

  .room-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 20px;

    .room-card {
      transition: transform 0.3s;

      &:hover {
        transform: translateY(-5px);
      }

      .room-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }

      .room-info {
        display: flex;
        flex-direction: column;
        gap: 8px;

        .capacity,
        .time-range {
          display: flex;
          align-items: center;
          gap: 8px;
          color: var(--el-text-color-secondary);
        }
      }
    }
  }
}

.booking-card {
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
  transition: box-shadow 0.3s, transform 0.3s;

  &:hover {
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    transform: translateY(-5px);
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: bold;
    font-size: 16px;
  }

  .booking-info {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
    padding: 10px 0;

    .time-range,
    .participants {
      display: flex;
      align-items: center;
      gap: 8px;
      color: var(--el-text-color-secondary);
    }
  }
}
</style>
