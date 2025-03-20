<!-- src/views/medical/DoctorManageView.vue -->
<template>
    <div class="doctor-manage p-6">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card>
            <template #header>
              <div class="flex justify-between items-center">
                <span>医生列表</span>
                <el-button type="primary" @click="refreshDoctors">刷新</el-button>
              </div>
            </template>
  
            <el-table 
              :data="doctorStore.doctors"
              highlight-current-row
              @current-change="handleDoctorSelect"
              v-loading="doctorStore.loading"
            >
              <el-table-column prop="name" label="姓名" />
              <el-table-column prop="title" label="职称" />
            </el-table>
          </el-card>
        </el-col>
  
        <el-col :span="16">
          <el-card v-if="currentDoctor">
            <template #header>
              <div class="flex justify-between items-center">
                <span>{{ currentDoctor.name }} 排班管理</span>
                <div class="flex items-center gap-4">
                  <el-button type="primary" @click="showCreateDialog">
                    <el-icon><circle-plus /></el-icon>新增排班日
                  </el-button>
                  <span class="text-sm text-gray-500">
                    已修改 {{ modifiedCount }} 个时间段
                  </span>
                  <el-button 
                    type="primary"
                    :disabled="modifiedCount === 0"
                    @click="saveAllChanges"
                    :loading="doctorStore.loading"
                  >
                    保存所有修改
                  </el-button>
                </div>
              </div>
            </template>
  
            <el-collapse v-model="activeDates" accordion>
              <el-collapse-item 
                v-for="schedule in doctorStore.schedules" 
                :key="schedule.workDate"
                :name="schedule.workDate"
              >
                <template #title>
                  <div class="flex items-center gap-4">
                    <span class="font-medium">{{ schedule.workDate }}</span>
                    <template v-for="status in statusKeys" :key="status">
                      <el-tag 
                        v-if="getStatusCount(schedule, status)"
                        :type="statusTypeMap[status]"
                        size="small"
                      >
                        {{ statusLabelMap[status] }}: {{ getStatusCount(schedule, status) }}
                      </el-tag>
                    </template>
                  </div>
                </template>
  
                <div class="grid grid-cols-3 gap-4 p-4">
                  <div 
                    v-for="slot in schedule.timeSlots"
                    :key="slot.time"
                    class="flex items-center gap-2"
                  >
                    <span class="w-20">{{ slot.time }}</span>
                    <el-select
                      v-model="slot.status"
                      :class="{ 'modified': isModified(schedule.workDate, slot) }"
                      @change="handleStatusChange(schedule.workDate, slot)"
                    >
                      <el-option
                        v-for="item in statusOptions"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </div>
                </div>
              </el-collapse-item>
            </el-collapse>
  
            <el-dialog v-model="createDialogVisible" title="新增排班日" width="400px">
              <el-form label-width="80px">
                <el-form-item label="日期" required>
                  <el-date-picker
                    v-model="newScheduleDate"
                    type="date"
                    value-format="YYYY-MM-DD"
                  />
                </el-form-item>
                <el-form-item label="时间段">
                  <el-time-picker
                    v-model="timeRanges"
                    is-range
                    range-separator="-"
                    start-placeholder="开始时间"
                    end-placeholder="结束时间"
                    value-format="HH:mm"
                    :disabled-hours="disabledHours"
                  />
                  <div class="mt-2">
                    <el-button @click="addTimeSlot">添加时间段</el-button>
                  </div>
                  <div class="time-slots-list">
                    <div v-for="(slot, index) in newTimeSlots" :key="index" class="flex items-center gap-2">
                      <span>{{ slot }}</span>
                      <el-button type="danger" text @click="removeTimeSlot(index)">删除</el-button>
                    </div>
                  </div>
                </el-form-item>
              </el-form>
  
              <template #footer>
                <el-button @click="createDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="createNewSchedule">创建</el-button>
              </template>
            </el-dialog>
          </el-card>
  
          <el-empty v-else description="请从左侧选择医生" />
        </el-col>
      </el-row>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, computed } from 'vue'
  import { CirclePlus } from '@element-plus/icons-vue'
  import { ElMessage } from 'element-plus'
  import { useDoctorStore } from '@/store/admin/doctor'
  import type { Doctor, Schedule, TimeSlot } from '@/store/admin/doctor'
  
  const doctorStore = useDoctorStore()
  const activeDates = ref<string[]>([])
  const createDialogVisible = ref(false)
  const newScheduleDate = ref('')
  const timeRanges = ref<[string, string]>()
  const newTimeSlots = ref<string[]>([])
  
  const statusOptions = [
    { value: 'available', label: '可预约' },
    { value: 'booked', label: '已预约' },
    { value: 'closed', label: '已关闭' }
  ] as const
  
  const statusKeys = ['available', 'booked', 'closed'] as const
  const statusTypeMap = { available: 'success', booked: 'warning', closed: 'info' } as const
  const statusLabelMap = { available: '可约', booked: '已约', closed: '关闭' } as const
  
  const currentDoctor = ref<Doctor | null>(null)
  const modifiedCount = computed(() => doctorStore.modifiedSlots.size)
  
  const getStatusCount = (schedule: Schedule, status: typeof statusKeys[number]) => {
    return schedule.timeSlots.filter(slot => slot.status === status).length
  }
  
  const disabledHours = () => [0,1,2,3,4,5,6,7,8,20,21,22,23]
  
  const handleDoctorSelect = async (doctor: Doctor) => {
    currentDoctor.value = doctor
    await doctorStore.fetchDoctorDetail(doctor.id)
    activeDates.value = doctorStore.schedules.map(s => s.workDate)
  }
  
  const handleStatusChange = (date: string, slot: TimeSlot) => {
    const key = `${date}|${slot.time}`
    if (slot.status === slot.originalStatus) {
      doctorStore.modifiedSlots.delete(key)
    } else {
      doctorStore.modifiedSlots.set(key, slot.status)
    }
  }
  
  const isModified = (date: string, slot: TimeSlot) => {
    return doctorStore.modifiedSlots.has(`${date}|${slot.time}`)
  }
  
  const saveAllChanges = async () => {
    try {
      if (!currentDoctor.value) return
      await doctorStore.batchUpdateSchedule(currentDoctor.value.id)
      ElMessage.success('排班更新成功')
      await doctorStore.fetchSchedules(currentDoctor.value.id)
    } catch (error) {
      ElMessage.error('排班更新失败')
    }
  }
  
  const showCreateDialog = () => {
    newScheduleDate.value = ''
    timeRanges.value = undefined
    newTimeSlots.value = []
    createDialogVisible.value = true
  }
  
  const addTimeSlot = () => {
    if (!timeRanges.value) return
    const [start, end] = timeRanges.value
    newTimeSlots.value.push(`${start}-${end}`)
  }
  
  const removeTimeSlot = (index: number) => {
    newTimeSlots.value.splice(index, 1)
  }
  
  const createNewSchedule = async () => {
    if (!currentDoctor.value || !newScheduleDate.value) return
    await doctorStore.createSchedule(
      currentDoctor.value.id,
      newScheduleDate.value,
      newTimeSlots.value
    )
    createDialogVisible.value = false
    ElMessage.success('排班日创建成功')
  }

  doctorStore.fetchDoctors();
  
  const refreshDoctors = () => doctorStore.fetchDoctors()
  </script>
  
  <style scoped>
  .modified .el-select__wrapper {
    box-shadow: 0 0 0 1px var(--el-color-primary) inset;
  }
  
  .doctor-manage {
    height: calc(100vh - 120px);
    overflow: auto;
  
    :deep(.el-collapse-item__header) {
      background-color: #f8fafc;
      padding: 0 16px;
    }
  
    :deep(.el-collapse-item__content) {
      padding-bottom: 0;
    }
    
    .time-slots-list {
      max-height: 200px;
      overflow-y: auto;
      margin-top: 8px;
      padding: 8px;
      border: 1px solid #ebeef5;
      border-radius: 4px;
    }
  }
  </style>
  