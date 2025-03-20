<template>
    <div class="p-6">
      <el-card>
        <div class="mb-4 flex justify-between">
          <el-button type="primary" @click="showCreateDialog">
            <el-icon><Plus /></el-icon> 新增车位
          </el-button>
  
          <el-select 
            v-model="store.zoneFilter" 
            placeholder="选择区域" 
            style="width: 120px"
            @change="store.applyFilter"
          >
            <el-option label="全部区域" value="" />
            <el-option 
              v-for="zone in ['A', 'B', 'C', 'D']"
              :key="zone"
              :label="`区域 ${zone}`"
              :value="zone"
            />
          </el-select>
        </div>
  
        <el-table :data="store.spaces" v-loading="store.loading" border>
          <el-table-column prop="zoneCode" label="区域" width="100" />
          <el-table-column prop="number" label="车位编号" />
          <el-table-column prop="status" label="状态" width="120">
            <template #default="{ row }">
              <el-tag :type="statusTagType[row.status]">
                {{ statusMap[row.status] }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="lastStatusTime" label="最后状态时间" width="180" />
          <el-table-column label="操作" width="220">
            <template #default="{ row }">
              <el-button size="small" @click="showEditDialog(row)">编辑</el-button>
              <el-button 
                size="small" 
                type="danger" 
                @click="handleDelete(row.id)"
              >删除</el-button>
              <el-button 
                v-if="row.status !== 'FREE'"
                size="small"
                type="warning"
                @click="store.forceRelease(row.id)"
              >强制释放</el-button>
            </template>
          </el-table-column>
        </el-table>
  
        <el-pagination
          class="mt-4"
          v-model:current-page="store.currentPage"
          v-model:page-size="store.pageSize"
          :total="store.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="store.updatePagination"
          @size-change="store.updatePagination"
        />
      </el-card>
  
      <el-dialog v-model="dialogVisible" :title="isEditMode ? '编辑车位' : '新增车位'">
        <el-form ref="formRef" :model="formData" label-width="100px">
            <el-form-item 
            label="区域编码" 
            prop="zoneCode"
            :rules="[{ required: true, pattern: /^[A-D]$/, message: '必须输入A-D的大写字母' }]"
          >
            <el-select v-model="formData.zoneCode">
              <el-option 
                v-for="zone in ['A', 'B', 'C', 'D']"
                :key="zone"
                :label="zone"
                :value="zone"
              />
            </el-select>
          </el-form-item>
          <el-form-item 
            label="车位编号" 
            prop="number"
            :rules="{ required: true, message: '不能为空' }"
          >
            <el-input v-model="formData.number" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="formData.status">
              <el-option 
                v-for="(label, value) in statusMap" 
                :key="value"
                :label="label"
                :value="value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="二维码地址">
            <el-input v-model="formData.qrCode" />
          </el-form-item>
          <el-form-item v-if="isEditMode" label="版本号" prop="version">
            <el-input-number v-model="formData.version" :min="1" />
          </el-form-item>
        </el-form>
  
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">
            {{ isEditMode ? '更新' : '创建' }}
          </el-button>
        </template>
      </el-dialog>
    </div>
  </template>
  <script setup lang="ts">
  import { Plus } from '@element-plus/icons-vue'
  import { useParkingManageStore } from '@/store/admin/parking'
  import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
  import { reactive, ref, onMounted } from 'vue'
  import type { ParkingSpace, ParkingSpaceCreateDTO } from '@/types/parking'
  
  // 状态管理
  const store = useParkingManageStore()
  const dialogVisible = ref(false)
  const isEditMode = ref(false)
  const formRef = ref<FormInstance>()
  
  // 状态映射
  const statusMap: Record<'FREE' | 'RESERVED' | 'OCCUPIED', string> = {
    FREE: '空闲',
    RESERVED: '已预约',
    OCCUPIED: '已占用'
  }
  
  const statusTagType: Record<'FREE' | 'RESERVED' | 'OCCUPIED', 'success' | 'warning' | 'danger'> = {
    FREE: 'success',
    RESERVED: 'warning',
    OCCUPIED: 'danger'
  }
  
  // 表单数据
  const formData = reactive({
    id: '',
    zoneCode: '',
    number: '',
    status: 'FREE' as 'FREE' | 'RESERVED' | 'OCCUPIED',
    qrCode: '',
    version: 1
  })
  
  // 初始化加载数据
  onMounted(() => {
    store.fetchAllData()
  })
  
  // 显示创建对话框
  const showCreateDialog = () => {
    isEditMode.value = false
    Object.assign(formData, {
      id: '',
      zoneCode: '',
      number: '',
      status: 'FREE',
      qrCode: '',
      version: 1
    })
    dialogVisible.value = true
  }
  
  // 显示编辑对话框
  const showEditDialog = (row: ParkingSpace) => {
    isEditMode.value = true
    Object.assign(formData, {
      ...row,
      version: Number(row.version)
    })
    dialogVisible.value = true
  }
  
  // 提交表单
  const submitForm = async () => {
    try {
      // 先进行表单验证
      const valid = await formRef.value?.validate()
      if (!valid) return
  
      if (isEditMode.value) {
        await store.updateSpace(formData)
        ElMessage.success('更新成功')
      } else {
        // 创建时使用正确类型
        const createData: ParkingSpaceCreateDTO = {
          zoneCode: formData.zoneCode,
          number: formData.number,
          status: formData.status,
          qrCode: formData.qrCode
        }
        await store.addSpace(createData)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
    } catch (error) {
      ElMessage.error('操作失败，请检查数据')
    }
  }
  
  // 删除车位
  const handleDelete = async (id: string) => {
    try {
      await ElMessageBox.confirm('确定删除该车位？此操作不可逆！', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await store.deleteSpace(id)
      ElMessage.success('删除成功')
    } catch (error) {
      // 用户取消不处理
    }
  }
  </script>
  