<template>
  <div class="p-6">
    <el-card>
      <div class="mb-4">
        <el-input
          v-model="searchKey"
          placeholder="搜索用户..."
          clearable
          style="max-width: 400px"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>
      </div>

      <el-table 
        :data="filteredUsers"
        v-loading="loading"
        stripe
        border
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="200" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="phone" label="手机号" width="140" />
        
        <el-table-column label="角色" width="160">
          <template #default="{ row }">
            <el-select 
              v-model="row.role" 
              size="small"
              @change="(val: string) => updateRole(row.id, val)"
            >
              <el-option label="普通用户" value="USER" />
              <el-option label="维护人员" value="MAINTENANCE" />
              <el-option label="管理员" value="ADMIN" />
            </el-select>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-switch
              v-model="row.isEnabled"
              :active-value="true"
              :inactive-value="false"
              @change="toggleStatus(row.id, row.isEnabled)"
            />
          </template>
        </el-table-column>

        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-tooltip content="重置密码">
              <el-button
                size="small"
                type="danger"
                :icon="Refresh"
                circle
                @click="handleResetPassword(row.id)"
              />
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="mt-4 justify-end"
        v-model:current-page="userStore.currentPage"
        v-model:page-size="userStore.pageSize"
        :total="userStore.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @current-change="userStore.fetchUsers"
        @size-change="userStore.fetchUsers"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import { useAdminUserStore } from '@/store/admin/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useAdminUserStore()
const searchKey = ref('')
const loading = ref(false)

const filteredUsers = computed(() => {
  const key = searchKey.value.toLowerCase()
  return userStore.users.filter(user => 
    user.username.toLowerCase().includes(key) ||
    (user.email?.toLowerCase().includes(key) ?? false) ||
    (user.phone?.includes(key) ?? false)
  )
})

onMounted(() => {
  loadData()
})

const loadData = async () => {
  try {
    loading.value = true
    await userStore.fetchUsers()
  } finally {
    loading.value = false
  }
}

const updateRole = async (id: number, newRole: string) => {
  try {
    await userStore.updateRole(id, newRole)
    ElMessage.success('角色更新成功')
  } catch (error) {
    ElMessage.error('角色更新失败')
  }
}

const toggleStatus = async (id: number, enabled: boolean) => {
  try {
    await userStore.toggleStatus(id, enabled)
    ElMessage.success(enabled ? '用户已启用' : '用户已禁用')
  } catch (error) {
    ElMessage.error('状态更新失败')
  }
}

const handleSearch = () => {
  userStore.currentPage = 1
  userStore.fetchUsers()
}

const handleResetPassword = async (id: string) => {
  try {
    await ElMessageBox.confirm('确定要重置该用户密码吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const newPassword = await userStore.resetPassword(id)
    ElMessageBox.alert(`新密码：${newPassword}`, '密码重置成功', {
      confirmButtonText: '确定',
      type: 'success'
    })
  } catch (error) {
    // 取消操作不处理
  }
}
</script>
