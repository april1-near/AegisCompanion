<template>
    <div class="admin-container">
        <!-- 侧边导航 -->
        <div class="admin-sidebar" :class="{ 'collapsed': isCollapsed }">
            <div class="sidebar-header">
                <el-icon :size="28" color="#409EFF">
                    <Monitor />
                </el-icon>
                <span v-show="!isCollapsed">管理控制台</span>
            </div>
            <el-menu :collapse="isCollapsed" :default-active="$route.path" active-text-color="#409EFF"
                @select="handleSelect">

                <el-menu-item index="/admin/users">
                    <el-icon>
                        <User />
                    </el-icon>
                    <span>用户管理</span>
                </el-menu-item>
                <el-menu-item index="/admin/medical">
                    <el-icon>
                        <FirstAidKit />
                    </el-icon>
                    <span>社区医疗</span>
                </el-menu-item>
                <el-menu-item index="/admin/parking">
                    <el-icon>
                        <Opportunity />
                    </el-icon>
                    <span>车位管理</span>
                </el-menu-item>

                
                <el-sub-menu index="/admin/rooms">
                    <template #title>
                    <el-icon>
                        <Van />
                    </el-icon>
                    <span>社区活动</span>
                    </template>
                    <el-menu-item index="/admin/rooms">活动室管理</el-menu-item>
                    <el-menu-item index="/admin/rooms/activity">活动审批</el-menu-item>
                </el-sub-menu>


                <el-menu-item index="/admin/ticket">
                    <el-icon>
                        <DocumentChecked />
                    </el-icon>
                    <span>工单审批</span>
                </el-menu-item>
            </el-menu>

            <div class="collapse-btn" @click="isCollapsed = !isCollapsed">
                <el-icon :size="20">
                    <component :is="isCollapsed ? 'Expand' : 'Fold'" />
                </el-icon>
            </div>
        </div>


        <!-- 主内容区 -->
        <div class="admin-main">
            <!-- 顶部操作栏 -->
            <div class="admin-header">
                <el-breadcrumb separator="/">
                    <el-breadcrumb-item :to="{ path: '/admin' }">管理首页</el-breadcrumb-item>
                    <el-breadcrumb-item>{{ currentRouteTitle }}</el-breadcrumb-item>
                </el-breadcrumb>

                <div class="admin-actions">
                    <el-dropdown>
                        <span class="user-info">
                            <el-avatar :size="32" :icon="UserFilled" class="admin-avatar" />
                            <span>{{ authStore.user?.username }}</span>
                        </span>
                        <template #dropdown>
                            <el-dropdown-item @click="handleLogout">
                                <el-icon>
                                    <SwitchButton />
                                </el-icon>退出系统
                            </el-dropdown-item>
                        </template>
                    </el-dropdown>
                </div>
            </div>
            <!-- 页面内容 -->
            <div class="content-wrapper">
                <router-view v-slot="{ Component }">
                    <transition name="fade-transform" mode="out-in">
                        <component :is="Component" />
                    </transition>
                </router-view>
            </div>
        </div>
    </div>
</template>


<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import {
    Monitor,
    User,
    FirstAidKit,
    Opportunity,
    Calendar,
    DocumentChecked,
    SwitchButton,
    Expand,
    Fold,
    UserFilled
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const isCollapsed = ref(false)

const currentRouteTitle = computed(() => route.meta.title || '管理页面')

const handleSelect = (path: string) => {
    router.push(path)
}

const handleLogout = () => {
    authStore.logout()
    router.replace('/login')
}
</script>

<style scoped>
.admin-container {
    display: flex;
    height: 100vh;
    width: 100vw;
    overflow: hidden;
    background: #f5f7fa;
}

.admin-sidebar {
    width: 240px;
    background: #fff;
    box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
    transition: width 0.3s;
    display: flex;
    flex-direction: column;

    &.collapsed {
        width: 64px;

        .sidebar-header span {
            display: none;
        }
    }
}

.sidebar-header {
    padding: 18px;
    display: flex;
    align-items: center;
    gap: 12px;
    border-bottom: 1px solid #eee;

    :deep(.el-icon) {
        color: #409EFF;
    }

    span {
        font-size: 18px;
        font-weight: 500;
        color: #303133;
    }
}

.collapse-btn {
    margin-top: auto;
    padding: 16px;
    cursor: pointer;
    border-top: 1px solid #eee;
    text-align: center;

    &:hover {
        background: #f5f7fa;
    }
}

.admin-main {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
}

.admin-header {
    height: 56px;
    padding: 0 24px;
    background: #fff;
    display: flex;
    align-items: center;
    justify-content: space-between;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.admin-avatar {
    background-color: #409EFF;
    color: white;
    margin-right: 8px;
}

.content-wrapper {
    flex: 1;
    padding: 24px;
    overflow-y: auto;
    background: #fff;
    margin: 16px;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

@media (max-width: 768px) {
    .admin-sidebar {
        width: 56px !important;

        .el-menu-item span {
            display: none;
        }
    }

    .content-wrapper {
        margin: 8px;
        padding: 12px;
    }
}

.fade-transform-enter-active,
.fade-transform-leave-active {
    transition: all 0.3s;
}

.fade-transform-enter-from {
    opacity: 0;
    transform: translateX(30px);
}

.fade-transform-leave-to {
    opacity: 0;
    transform: translateX(-30px);
}
</style>