<!-- src/views/LoginView.vue -->
<template>
  <div class="login-container">
    <el-card class="login-card" :class="{ 'is-register': isRegister }">
      <h2 class="login-title">{{ isRegister ? '用户注册' : '用户登录' }}</h2>
      <el-form
        ref="loginForm"
        :model="form"
        :rules="rules"
        @keyup.enter="handleSubmit"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item v-if="isRegister" prop="phone">
          <el-input
            v-model="form.phone"
            placeholder="请输入手机号"
            prefix-icon="Phone"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            class="login-button"
            :loading="loading"
            @click="handleSubmit"
          >
            {{ isRegister ? '注册' : '登录' }}
          </el-button>
        </el-form-item>

        <el-form-item>
          <button 
            class="custom-link-btn"
            @click.prevent="toggleForm"
          >
            <span class="btn-text">
              {{ isRegister ? '已有账号？登录' : '没有账号？注册' }}
            </span>
            <el-icon class="arrow-icon"><Right /></el-icon>
          </button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { Right } from '@element-plus/icons-vue'
import { useAuthStore } from '@/store/auth'

const router = useRouter()
const authStore = useAuthStore()

const loginForm = ref<FormInstance>()
const loading = ref(false)
const isRegister = ref(false)

const form = ref({
  username: '',
  password: '',
  phone: ''
})

const rules = ref<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式错误', trigger: 'blur' }
  ]
})

const handleSubmit = async () => {
  if (!loginForm.value) return

  try {
    loading.value = true
    await loginForm.value.validate()

    if (isRegister.value) {
      await authStore.register({
        username: form.value.username,
        password: form.value.password,
        phone: form.value.phone
      })
      ElMessage.success('注册成功')
      isRegister.value = false
    } else {
      await authStore.login({
        username: form.value.username,
        password: form.value.password
      })
      ElMessage.success('登录成功')
      router.push('/home')
    }
  } catch (error) {
    ElMessage.error(isRegister.value ? '注册失败，请检查输入信息' : '登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}

const toggleForm = () => {
  isRegister.value = !isRegister.value
}
</script>

<style scoped lang="scss">
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  position: relative;
}

.login-card {
  width: 400px;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: absolute;
  left: 50%;
  transform: translateX(-25%);
  min-height: 400px;
  background: rgba(255, 255, 255, 0.95);

  &.is-register {
    transform: translateX(-75%);
  }
}

.login-title {
  text-align: center;
  margin-bottom: 35px;
  color: #2c3e50;
  font-weight: 600;
  letter-spacing: 1px;
}

.login-button {
  width: 100%;
  height: 45px;
  font-size: 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.custom-link-btn {
  padding: 0;
  border: none;
  background: transparent;
  display: inline-flex;
  align-items: center;
  color: #666;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  font-size: 14px;

  &:hover {
    color: #409eff;
    
    .btn-text {
      text-decoration: underline;
      text-underline-offset: 3px;
    }
    
    .arrow-icon {
      transform: translateX(3px);
    }
  }

  .btn-text {
    padding: 8px 12px;
    transition: all 0.2s ease;
  }

  .arrow-icon {
    margin-left: 6px;
    transition: transform 0.3s ease;
    font-size: 0.9em;
  }

  &::after {
    content: '';
    position: absolute;
    bottom: -2px;
    left: 0;
    width: 0;
    height: 2px;
    background: #409eff;
    transition: width 0.3s ease;
  }

  &:hover::after {
    width: 100%;
  }
}

/* 卡片切换动画优化 */
.login-card {
  perspective: 1000px;
  transform-style: preserve-3d;
  
  &.is-register {
    transform: translateX(-75%) rotateY(5deg);
    opacity: 0.98;
  }
}

.el-form-item {
  margin-bottom: 25px;
}

.el-input {
  :deep(.el-input__inner) {
    border-radius: 6px;
    padding: 12px 15px;
  }
}



</style>
