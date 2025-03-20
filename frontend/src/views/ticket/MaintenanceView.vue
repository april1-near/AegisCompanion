<!-- src/views/MaintenanceView.vue 维修人员界面 -->
<template>
    <div class="maintenance-container">
        <div class="header">
            <h2>待处理工单</h2>
            <el-button type="primary" @click="refreshList" :icon="Refresh">刷新列表</el-button>
        </div>

        <div class="card-list">
            <el-card v-for="ticket in store.assignedTickets" :key="ticket.id" class="ticket-card">
                <template #header>
                    <div class="card-header">
                        <div class="title-wrap">
                            <h3>{{ ticket.title }}</h3>
                            <el-tag :type="statusStyleMap[ticket.stateDesc]">
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
                                <User />
                            </el-icon>
                            提交人：{{ ticket.creatorName }}
                        </div>
                        <div class="info-item">
                            <el-icon>
                                <List />
                            </el-icon>
                            类型：{{ ticket.type }}
                        </div>
                    </div>
                    <el-collapse>
                        <el-collapse-item title="问题描述">
                            <p>{{ ticket.description }}</p>
                        </el-collapse-item>
                        <el-collapse-item title="处理日志">
                            <div v-for="(log, index) in ticket.logs" :key="index" class="log-item">
                                <!-- 在 TicketView.vue 和 MaintenanceView.vue 中 -->
                                <div class="log-item">
                                    <!-- 直接显示 createTime -->
                                    <div class="log-time">{{ log.createTime }}</div>
                                    <div class="log-content">
                                        {{ log.fromState }} → {{ log.toState }}
                                        <span v-if="log.remark">（{{ log.remark }}）</span>
                                    </div>
                                </div>

                            </div>
                        </el-collapse-item>
                    </el-collapse>
                </div>

                <template #footer>
                    <div class="actions">
                        <el-button v-if="ticket.stateDesc === '已自动分配'" type="primary" @click="handleStart(ticket.id)">
                            开始处理
                        </el-button>
                        <el-button v-if="ticket.stateDesc === '处理中'" type="success"
                            @click="handleSubmitReview(ticket.id)">
                            提交审核
                        </el-button>
                    </div>
                </template>
            </el-card>
        </div>
    </div>
</template>

<script setup lang="ts">
import { Refresh, User, List } from '@element-plus/icons-vue'
import { useTicketStore } from '@/store/ticket'
import { ElMessage } from 'element-plus'

const store = useTicketStore()

const statusStyleMap: { [key: string]: string } = {
    '已自动分配': 'warning',
    '处理中': 'primary',
    '待审核': 'info',
    '审核通过': 'success',
    '审核未通过': 'danger'
}

const handleStart = async (id: string) => {
    try {
        await store.startProcessing(id)
        ElMessage.success('已开始处理')
    } catch (error) {
        ElMessage.error('操作失败')
    }
}

const handleSubmitReview = async (id: string) => {
    try {
        await store.submitForReview(id)
        ElMessage.success('已提交审核')
    } catch (error) {
        ElMessage.error('提交失败')
    }
}

const refreshList = () => {
    store.fetchAssignedTickets()
    ElMessage.success('列表已刷新')
}

store.fetchAssignedTickets()
</script>

<style lang="scss" scoped>
.maintenance-container {
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

        .actions {
            display: flex;
            gap: 8px;
            justify-content: flex-end;
        }
    }
}
</style>