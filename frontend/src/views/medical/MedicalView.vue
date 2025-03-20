<template>
  <div class="medical-view">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>我的预约</span>
          </div>
          <div v-for="appointment in appointments" :key="appointment.id" class="appointment-card">
            <p><strong>医生:</strong> {{ appointment.doctorName }}</p>
            <p><strong>预约时间:</strong> {{ appointment.appointDate }} {{ appointment.timeSlot }}</p>
            <p><strong>状态:</strong> {{ appointment.statusDesc }}</p>
            <el-button v-if="appointment.statusDesc === '待确认'" type="danger" @click="cancelAppointment(appointment.id.toString())">取消预约</el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>创建预约</span>
          </div>
          <div v-for="doctor in doctors" :key="doctor.id" class="doctor-card">
            <p><strong>医生:</strong> {{ doctor.name }}</p>
            <div v-if="schedules[doctor.id] && schedules[doctor.id].length">
              <el-card v-for="schedule in schedules[doctor.id]" :key="schedule.workDate" class="schedule-card">
                <div slot="header" class="clearfix">
                  <span>{{ schedule.workDate }}</span>
                </div>
                <div v-for="(status, timeSlot) in schedule.timeSlots" :key="timeSlot" class="time-slot">
                  <el-button v-if="status === 'available'" type="success" @click="createAppointment(doctor.id, schedule.workDate, timeSlot)">预约 {{ timeSlot }}</el-button>
                  <el-tag v-else>{{ timeSlot }} - {{ status }}</el-tag>
                </div>
              </el-card>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useMedicalStore } from '@/store/medical'
import dayjs from 'dayjs'

const medicalStore = useMedicalStore()
interface Appointment {
  id: number;
  doctorName: string;
  appointDate: string;
  timeSlot: string;
  statusDesc: string;
}

const appointments = ref<Appointment[]>([])
interface Doctor {
  id: number;
  name: string;
}

const doctors = ref<Doctor[]>([])
interface Schedule {
  workDate: string;
  timeSlots: Record<string, string>;
}

const schedules = ref<Record<number, Schedule[]>>({})

onMounted(async () => {
  await fetchMyAppointments()
  await fetchDoctors()
  await fetchSchedules()
})

const fetchMyAppointments = async () => {
  await medicalStore.fetchMyAppointments()
  appointments.value = medicalStore.myAppointments
}

const fetchDoctors = async () => {
  await medicalStore.fetchDoctors()
  doctors.value = medicalStore.doctors
}

const fetchSchedules = async () => {
  for (const doctor of doctors.value) {
    await medicalStore.fetchDoctorSchedule(doctor.id)
    schedules.value[doctor.id] = medicalStore.doctorSchedules
  }
}

const createAppointment = async (doctorId: number, date: string, timeSlot: string) => {
  const formattedDate = dayjs(date).format('YYYY-MM-DD')
  await medicalStore.createAppointment(doctorId, formattedDate, timeSlot)
  await fetchMyAppointments()
}

const cancelAppointment = async (id: string) => {
  await medicalStore.cancelAppointment(id)
  await fetchMyAppointments()
}
</script>

<style scoped>
.medical-view {
  padding: 20px;
}

.appointment-card,
.schedule-card,
.doctor-card {
  margin-bottom: 20px;
}

.time-slot {
  margin-top: 10px;
}
</style>
