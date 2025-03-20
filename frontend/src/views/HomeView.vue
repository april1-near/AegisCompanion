<!-- src/views/HomeView.vue -->
<template>
  <div class="home-container spring-view">
    <el-card class="profile-card">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="个人信息" name="info">
          <el-form 
            :model="userForm" 
            :rules="formRules"
            ref="userFormRef"
            label-width="100px"
          >
            <el-form-item label="用户名" prop="username">
              <el-input v-model="userForm.username" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="userForm.phone" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="userForm.email" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitUserForm">
                保存修改
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="修改密码" name="password">
          <el-form
            :model="passwordForm"
            :rules="passwordRules"
            ref="passwordFormRef"
            label-width="100px"
          >
            <el-form-item label="旧密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitPasswordForm">
                修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="当前信息" name="currentInfo">
          <el-form :model="currentUser" label-width="100px">
            <el-form-item label="用户名">
              <el-input v-model="currentUser.username" disabled />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="currentUser.phone" disabled />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="currentUser.email" disabled />
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/store/auth'
import { useUserStore } from '@/store/user'
import type { FormInstance } from 'element-plus'

const authStore = useAuthStore()
const userStore = useUserStore()
const activeTab = ref('info')

const userForm = reactive({
  username: authStore.user?.username || '',
  phone: authStore.user?.phone || '',
  email: authStore.user?.email || ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: ''
})

const currentUser = reactive({
  username: authStore.user?.username || '',
  phone: authStore.user?.phone || '',
  email: authStore.user?.email || ''
})

onMounted(() => {
  if (authStore.user) {
    currentUser.username = authStore.user.username
    currentUser.phone = authStore.user.phone || ''
    currentUser.email = authStore.user.email
  }
})

const formRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ]
}

const submitUserForm = async () => {
  try {
    await userStore.updateUserInfo(authStore.user!.id, userForm)
    ElMessage.success('个人信息更新成功')
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

const submitPasswordForm = async () => {
  try {
    await userStore.changePassword(authStore.user!.id, passwordForm)
    ElMessage.success('密码修改成功')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
  } catch (error) {
    ElMessage.error('密码修改失败')
  }
}
</script>

<style lang="scss" scoped>
.spring-view {
  background: linear-gradient(135deg, #f6fbe3 0%, #e7f7f8 100%);
  min-height: calc(100vh - 60px);
  padding: 30px;
}

.profile-card {
  max-width: 800px;
  margin: 0 auto;
  border-radius: 15px;
  box-shadow: 0 4px 12px rgba(168,230,207,0.3);

  :deep(.el-tabs__nav-wrap) {
    padding: 0 20px;
  }

  :deep(.el-form) {
    padding: 20px 40px;
  }
}

.el-button {
  background: #a8e6cf;
  border-color: #a8e6cf;
  color: #fff;

  &:hover {
    background: #8dd3b0;
    border-color: #8dd3b0;
  }
}
</style>
