<!-- views/admin/BookingApproval.vue -->
<template>
    <div class="booking-approval-container">
      <el-card class="main-card">
        <template #header>
          <div class="card-header">
            <h2 class="header-title">会议室预约审批</h2>
            <div class="header-actions">
              <el-button type="primary" @click="store.fetchBookings" :icon="Refresh" circle />
            </div>
          </div>
        </template>
  
        <!-- 增强型查询表单 -->
        <el-form 
          :model="store.queryParams" 
          ref="queryForm"
          class="advanced-search-form"
          label-position="top"
        >
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :md="8" :lg="6">
              <el-form-item label="审批状态">
                <el-select
                  v-model="store.queryParams.status"
                  placeholder="全部状态"
                  clearable
                  class="full-width"
                >
                  <el-option label="待审批" value="PENDING" />
                  <el-option label="已通过" value="APPROVED" />
                  <el-option label="已拒绝" value="REJECTED" />
                  <el-option label="已取消" value="CANCELED" />
                </el-select>
              </el-form-item>
            </el-col>
  
            <el-col :xs="24" :sm="12" :md="8" :lg="6">
              <el-form-item label="活动室ID">
                <el-input
                  v-model.number="store.queryParams.roomId"
                  placeholder="输入活动室编号"
                  clearable
                  type="number"
                  class="full-width"
                />
              </el-form-item>
            </el-col>
  
            <el-col :xs="24" :sm="24" :md="16" :lg="12">
              <el-form-item label="预约时间段">
                <el-date-picker
                  v-model="timeRange"
                  type="daterange"
                  range-separator="-"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  value-format="YYYY-MM-DD"
                  class="full-width"
                />
              </el-form-item>
            </el-col>
          </el-row>
  
          <el-form-item class="form-actions">
            <el-button type="primary" @click="store.fetchBookings" :icon="Search">
              查询
            </el-button>
            <el-button @click="handleReset" :icon="RefreshLeft">
              重置
            </el-button>
          </el-form-item>
        </el-form>
  
        <!-- 数据表格 -->
        <el-table 
          :data="store.bookings" 
          v-loading="store.loading"
          stripe
          height="60vh"
          class="data-table"
        >
          <el-table-column prop="userName" label="申请人" min-width="120" />
          <el-table-column prop="roomName" label="活动室" min-width="150" />
          <el-table-column prop="purpose" label="使用目的" min-width="200" show-overflow-tooltip />
          <el-table-column prop="participantCount" label="人数" width="100" align="center" />
          <el-table-column prop="timeRange" label="预约时间" min-width="220" />
          
          <el-table-column label="状态" width="130" align="center">
            <template #default="{row}">
              <el-tag 
                :type="statusTagType(row.bookingStatus)"
                effect="light"
                round
                class="status-tag"
              >
                {{ statusText(row.bookingStatus) }}
              </el-tag>
            </template>
          </el-table-column>
  
          <el-table-column label="操作" width="180" align="center" fixed="right">
            <template #default="{row}">
              <div class="action-buttons">
                <template v-if="row.bookingStatus === '待审批'">
                  <el-button 
                    type="success" 
                    size="small" 
                    @click="handleApproveAction(row.id, true)"
                    :icon="CircleCheck"
                  >
                    通过
                  </el-button>
                  <el-button 
                    type="danger" 
                    size="small" 
                    @click="handleApproveAction(row.id, false)"
                    :icon="CircleClose"
                  >
                    拒绝
                  </el-button>
                </template>
                <span v-else class="processed-text">已处理</span>
              </div>
            </template>
          </el-table-column>
        </el-table>
  
        <!-- 分页控件 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="store.pagination.current"
            v-model:page-size="store.pagination.size"
            :total="store.pagination.total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="store.fetchBookings"
            @size-change="handleSizeChange"
            background
          />
        </div>
      </el-card>
  
      <!-- 审批备注对话框 -->
      <el-dialog 
        v-model="commentDialog.visible" 
        :title="`审批操作 - ${currentAction === 'approve' ? '通过申请' : '拒绝申请'}`"
        width="500px"
        center
      >
        <el-input
          v-model="commentDialog.comment"
          type="textarea"
          :placeholder="currentAction === 'approve' 
            ? '请输入通过备注（可选）' 
            : '请说明拒绝原因（必填）'"
          :rows="4"
          :maxlength="200"
          show-word-limit
          clearable
        />
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="commentDialog.visible = false">取消</el-button>
            <el-button 
              type="primary" 
              @click="confirmApprove"
              :disabled="currentAction === 'reject' && !commentDialog.comment"
            >
              确认提交
            </el-button>
          </div>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <script setup lang="ts">
  import { useBookingAdminStore } from '@/store/admin/activity/bookingAdmin'
  import { ElMessage } from 'element-plus'
  import { reactive, ref, computed } from 'vue'
  import { 
    Refresh,
    Search,
    RefreshLeft,
    CircleCheck,
    CircleClose 
  } from '@element-plus/icons-vue'
  
  const store = useBookingAdminStore()
  const currentBookingId = ref<number>()
  const currentAction = ref<'approve' | 'reject'>('approve')
  
  const commentDialog = reactive({
    visible: false,
    comment: ''
  })
  
  // 时间范围处理
  const timeRange = computed({
    get: () => [store.queryParams.startTimeBegin, store.queryParams.startTimeEnd],
    set: (val) => {
      store.queryParams.startTimeBegin = val?.[0] || ''
      store.queryParams.startTimeEnd = val?.[1] || ''
    }
  })
  
  const statusText = (status: string) => {
    const map: Record<string, string> = {
      PENDING: '待审批',
      APPROVED: '已通过',
      REJECTED: '已拒绝',
      CANCELED: '已取消',
      COMPLETED: '已完成'
    }
    return map[status] || status
  }
  
  const statusTagType = (status: string) => {
    const map: Record<string, string> = {
      PENDING: 'warning',
      APPROVED: 'success',
      REJECTED: 'danger',
      CANCELED: 'info',
      COMPLETED: ''
    }
    return map[status] || 'info'
  }
  
  const handleApproveAction = (id: number, isApprove: boolean) => {
    currentBookingId.value = id
    currentAction.value = isApprove ? 'approve' : 'reject'
    commentDialog.visible = true
  }
  
  const confirmApprove = async () => {
    try {
      if (!currentBookingId.value) return
  
      const success = await store.approveBooking({
        bookingId: currentBookingId.value,
        approved: currentAction.value === 'approve',
        comment: commentDialog.comment
      })
  
      if (success) {
        commentDialog.visible = false
        commentDialog.comment = ''
        ElMessage.success('审批操作已完成')
      }
    } catch (e) {
      ElMessage.error('操作失败，请重试')
    }
  }
  
  const handleSizeChange = (size: number) => {
    store.pagination.size = size
    store.fetchBookings()
  }
  
  const handleReset = () => {
    store.resetQuery()
    store.fetchBookings()
  }
  
  // 初始化加载
  store.fetchBookings()
  </script>
  
  <style lang="scss" scoped>
  .booking-approval-container {
    padding: 20px;
    height: 100%;
  
    .main-card {
      height: 100%;
      box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
  
      :deep(.el-card__body) {
        height: calc(100% - 55px);
        display: flex;
        flex-direction: column;
      }
    }
  
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
  
      .header-title {
        margin: 0;
        font-size: 18px;
        color: #333;
      }
    }
  
    .advanced-search-form {
      margin-bottom: 20px;
  
      .full-width {
        width: 100%;
      }
  
      .form-actions {
        margin-top: 10px;
        display: flex;
        justify-content: flex-end;
        gap: 12px;
      }
    }
  
    .data-table {
      flex: 1;
      margin-bottom: 20px;
  
      .status-tag {
        font-weight: 500;
        letter-spacing: 0.5px;
      }
  
      .action-buttons {
        display: flex;
        gap: 8px;
        justify-content: center;
  
        .processed-text {
          color: #999;
          font-size: 13px;
        }
      }
    }
  
    .pagination-container {
      display: flex;
      justify-content: center;
    }
  }
  
  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
  </style>
  