<!-- src/views/admin/TicketManagement.vue -->
<template>
    <div class="admin-ticket-container">
      <div class="header">
        <h2>工单管理</h2>
        <el-button type="primary" @click="refreshList">刷新列表</el-button>
      </div>
  
      <el-table :data="store.allTickets" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="type" label类型 width="120" />
        <el-table-column label="状态" width="140">
          <template #default="{row}">
            <el-tag :type="statusStyleMap[row.stateDesc] || 'info'">
              {{ row.stateDesc }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="creatorName" label="创建人" width="120" />
        <el-table-column prop="assigneeName" label="处理人" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="120">
          <template #default="{row}">
            <el-button 
              v-if="row.stateDesc === '待审核'"
              size="small" 
              @click="showReviewDialog(row)"
            >
              审核
            </el-button>
          </template>
        </el-table-column>
  
        <el-table-column type="expand">
          <template #default="{row}">
            <div class="detail-panel">
              <div class="detail-section">
                <h4>问题描述</h4>
                <p>{{ row.description }}</p>
              </div>
              
              <div class="detail-section">
                <h4>处理日志</h4>
                <div v-for="(log, index) in row.logs" :key="index" class="log-item">
                  <div class="log-time">{{ log.createTime }}</div>
                  <div class="log-content">
                    {{ log.fromState }} → {{ log.toState }}
                    <span v-if="log.remark" class="remark">（{{ log.remark }}）</span>
                  </div>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
      </el-table>
  
      <!-- 审核对话框 -->
      <el-dialog v-model="showReview" title="工单审核" width="500px">
        <el-form :model="reviewForm" :rules="reviewRules" ref="reviewFormRef">
          <el-form-item label="审核结果" prop="isApproved">
            <el-radio-group v-model="reviewForm.isApproved">
              <el-radio-button :label="true">通过</el-radio-button>
              <el-radio-button :label="false">驳回</el-radio-button>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item 
            label="审核备注" 
            prop="remark"
            :rules="[
              { required: !reviewForm.isApproved, message: '驳回时必须填写备注' }
            ]"
          >
            <el-input 
              v-model="reviewForm.remark" 
              type="textarea" 
              :rows="3" 
              placeholder="请输入审核意见"
            />
          </el-form-item>
        </el-form>
        
        <template #footer>
          <el-button @click="showReview = false">取消</el-button>
          <el-button type="primary" @click="submitReview">提交</el-button>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, onMounted } from 'vue'
  import { useAdminTicketStore } from '@/store/admin/adminTicket'
  import { ElMessage, type FormInstance } from 'element-plus'
import type { AdminTicket, ReviewForm } from '@/store/admin/adminTicket'
  
  const store = useAdminTicketStore()
  const loading = ref(false)
  const showReview = ref(false)
  const reviewFormRef = ref<FormInstance>()
  const currentTicketId = ref('')
  
  const reviewForm = ref<ReviewForm>({
    isApproved: true,
    remark: ''
  })
  
  const statusStyleMap: Record<string, string> = {
    '已创建': 'info',
    '已自动分配': 'warning',
    '处理中': 'primary',
    '待审核': 'warning',
    '审核通过': 'success',
    '审核未通过': 'danger',
    '已完成': 'success'
  }
  
  const reviewRules = {
    isApproved: [{ required: true, message: '请选择审核结果', trigger: 'change' }]
  }
  
  const showReviewDialog = (ticket: AdminTicket) => {
    currentTicketId.value = ticket.id
    reviewForm.value = { isApproved: true, remark: '' }
    showReview.value = true
  }
  
  const submitReview = async () => {
    try {
      await reviewFormRef.value?.validate()
      await store.handleReview(currentTicketId.value, reviewForm.value)
      ElMessage.success('审核操作成功')
      showReview.value = false
      await refreshList()
    } catch (error) {
      console.error('审核提交失败:', error)
    }
  }
  
  const refreshList = async () => {
    try {
      loading.value = true
      await store.fetchAllTickets()
    } finally {
      loading.value = false
    }
  }
  
  onMounted(() => {
    refreshList()
  })
  </script>
  
  <style lang="scss" scoped>
  .admin-ticket-container {
    padding: 20px;
  
    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
    }
  
    .detail-panel {
      padding: 16px 40px;
  
      .detail-section {
        margin-bottom: 24px;
  
        h4 {
          margin-bottom: 8px;
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
  
        .remark {
          color: #666;
          font-style: italic;
        }
      }
    }
  }
  </style>
  