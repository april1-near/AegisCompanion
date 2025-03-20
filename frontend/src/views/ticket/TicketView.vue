<!-- src/views/TicketView.vue 用户界面 -->
<template>
  <div class="ticket-container">
    <div class="header">
      <h2>我的工单</h2>
      <el-button type="primary" @click="showCreateDialog" :icon="Plus">新建工单</el-button>
    </div>

    <div class="card-list">
      <el-card v-for="ticket in store.myTickets" :key="ticket.id" class="ticket-card">
        <template #header>
          <div class="card-header">
            <div class="title-wrap">
              <h3>{{ ticket.title }}</h3>
              <el-tag :type="statusStyleMap[ticket.stateDesc] || 'info'">
                {{ ticket.stateDesc }}
              </el-tag>
            </div>
            <span class="time">{{ ticket.createTime }}</span>
          </div>
        </template>

        <div class="card-content">
          <div class="meta-info">
            <div class="info-item">
              <el-icon>
                <List />
              </el-icon>
              类型：{{ ticket.type }}
            </div>
            <div class="info-item">
              <el-icon>
                <User />
              </el-icon>
              处理人：{{ ticket.assigneeName || '待分配' }}
            </div>
          </div>
          <el-collapse>
            <el-collapse-item title="详情描述">
              <p>{{ ticket.description }}</p>
            </el-collapse-item>
            <el-collapse-item title="处理日志">
              <div v-for="(log, index) in ticket.logs" :key="index" class="log-item">
                <div class="log-time">{{ log.createTime }}</div>
                <div class="log-content">
                  {{ log.fromState }} → {{ log.toState }}
                  <span v-if="log.remark">（{{ log.remark }}）</span>
                </div>
              </div>
            </el-collapse-item>
          </el-collapse>
        </div>

        <template #footer v-if="ticket.stateDesc === '待确认'">
          <el-button type="success" @click="handleConfirm(ticket.id)">
            确认完成
          </el-button>
        </template>
      </el-card>
    </div>

    <!-- 创建工单对话框 -->
    <el-dialog v-model="showCreateForm" title="新建工单" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item label="工单标题" prop="title" label-width="80px">
          <el-input v-model="form.title" placeholder="请输入问题标题" @blur="handleBlur('title')" />
        </el-form-item>
        <el-form-item label="问题类型" prop="type" label-width="80px">
          <el-select v-model="form.type" placeholder="请选择问题类型">
            <el-option label="电路维修" value="电路维修" />
            <el-option label="管道维修" value="管道维修" />
            <el-option label="门窗维修" value="门窗维修" />
            <el-option label="电器维修" value="电器维修" />
            <el-option label="水暖维修" value="水暖维修" />
          </el-select>
        </el-form-item>
        <el-form-item label="问题描述" prop="description" label-width="80px">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请详细描述您遇到的问题" @blur="handleBlur('description')" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateForm = false">取消</el-button>
        <el-button type="primary" @click="submitForm">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Plus, List, User } from '@element-plus/icons-vue'
import { useTicketStore } from '@/store/ticket'
import { ElMessage } from 'element-plus'

const store = useTicketStore()
const showCreateForm = ref(false)
const formRef = ref()
const form = ref({
  title: '',
  type: '',
  description: ''
})

const statusStyleMap: { [key: string]: string } = {
  '已创建': 'info',
  '已自动分配': 'warning',
  '处理中': 'primary',
  '待审核': 'info',
  '审核通过': 'success',
  '审核未通过': 'danger',
  '用户已确认': 'success',
  '已完成': 'success'
}

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择问题类型', trigger: 'change' }],
  description: [{ required: true, message: '请输入问题描述', trigger: 'blur' }]
}

const showCreateDialog = () => {
  showCreateForm.value = true
}

const handleBlur = (field: string) => {
  formRef.value.validateField(field)
}

const submitForm = async () => {
  try {
    await formRef.value.validate()
    await store.createTicket(form.value)
    ElMessage.success('工单创建成功')
    showCreateForm.value = false
    formRef.value.resetFields()
  } catch (error) {
    console.error('创建失败:', error)
  }
}

const handleConfirm = async (id: string) => {
  try {
    await store.confirmCompletion(id)
    ElMessage.success('工单已确认完成')
  } catch (error) {
    ElMessage.error('确认失败')
  }
}

store.fetchMyTickets()
</script>

<style lang="scss" scoped>
.ticket-container {
  padding: 20px;

  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
  }

  .card-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 16px;
  }

  .ticket-card {
    margin-bottom: 16px;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .title-wrap {
        display: flex;
        align-items: center;
        gap: 8px;
      }

      .time {
        font-size: 12px;
        color: #999;
      }
    }

    .card-content {
      .meta-info {
        display: flex;
        flex-wrap: wrap;
        gap: 12px;
        margin-bottom: 12px;

        .info-item {
          display: flex;
          align-items: center;
          gap: 4px;
          color: #666;
        }
      }

      .log-item {
        padding: 8px 0;
        border-bottom: 1px solid #eee;

        .log-time {
          font-size: 12px;
          color: #999;
        }
      }
    }
  }
}
</style>
