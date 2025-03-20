<template>
  <div class="app-container">
    <!-- 左侧导航栏 -->
    <div class="side-nav" :class="{ 'collapsed': isCollapsed }">
      <div class="nav-header">
        <img src="@/assets/vue.svg" class="logo" v-if="!isCollapsed">
        <h2 v-show="!isCollapsed">智慧社区</h2>
        <el-tooltip :content="isCollapsed ? '展开菜单' : '折叠菜单'" placement="right">
          <div class="collapse-btn" @click="isCollapsed = !isCollapsed">
            <el-icon :size="20">
              <component :is="isCollapsed ? 'Expand' : 'Fold'" />
            </el-icon>
          </div>
        </el-tooltip>
      </div>

      <el-scrollbar>
        <el-menu :collapse="isCollapsed" :default-active="activeMenu" class="nav-menu" @select="handleMenuSelect">

          <el-menu-item index="/chat">
            <el-icon>
              <ChatLineRound />
            </el-icon>
            <template #title>智能助手</template>
          </el-menu-item>

<!-- 

          <el-menu-item index="/dashboard">
            <el-icon>
              <PieChart />
            </el-icon>
            <template #title>数据看板</template>
          </el-menu-item> -->

          <el-sub-menu index="parking">
            <template #title>
              <el-icon>
                <Van />
              </el-icon>
              <span>车位管理</span>
            </template>
            <el-menu-item index="/parking/spaces">车位列表</el-menu-item>
            <el-menu-item index="/parking/reservations">我的预约</el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/medical">
            <el-icon>
              <FirstAidKit />
            </el-icon>
            <template #title>社区医疗</template>
          </el-menu-item>

          <el-menu-item index="/activity">
            <el-icon>
              <Calendar />
            </el-icon>
            <template #title>活动中心</template>
          </el-menu-item>

          <el-menu-item index="/ticket">
            <el-icon>
              <DocumentChecked />
            </el-icon>
            <span>工单服务</span>
          </el-menu-item>

          <el-menu-item index="/ticket" v-if="userRole === 'MAINTENANCE'">
            <el-icon>
              <Files />
            </el-icon>
            <template #title>工单处理</template>
          </el-menu-item>
        </el-menu>
      </el-scrollbar>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 顶部导航 -->
      <div class="top-nav">
        <div class="left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentRouteName }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="right"> <el-space :size="20">
            <el-badge :value="messageStore.unreadCount" :max="99" :hidden="messageStore.unreadCount === 0">
              <el-button type="info" circle @click="messageStore.toggleDrawer">
                <el-icon>
                  <Bell />
                </el-icon>
              </el-button>
            </el-badge>
            <el-dropdown>
              <div class="user-panel">
                <!-- 修改头像部分为图标 -->
                <div class="avatar-icon">
                  <el-icon :size="24">
                    <User />
                  </el-icon>
                </div>
                <span class="username">{{ username }}</span>
                <el-icon>
                  <ArrowDown />
                </el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <!-- 新增管理员入口 -->
                  <el-dropdown-item v-if="['ADMIN', 'SUPER_ADMIN'].includes(userRole)" @click="router.push('/admin')">
                    <el-icon>
                      <Monitor />
                    </el-icon>后台管理
                  </el-dropdown-item>
                  <el-dropdown-item @click="router.push('/home')">
                    <el-icon>
                      <User />
                    </el-icon>个人中心
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">
                    <el-icon>
                      <SwitchButton />
                    </el-icon>退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </el-space>
        </div>
      </div>

      <!-- 内容容器 -->
      <div class="content-wrapper">
        <router-view v-slot="{ Component }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>

  <msgDrawer />

</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/store/auth'
import msgDrawer from '@/components/messageDrawer.vue';
import { useWebsocketStore } from '@/store/websocketStore';
import { useMessageStore } from '@/store/message';
import { Monitor } from '@element-plus/icons-vue'


const websocketStore = useWebsocketStore();
const messageStore = useMessageStore();
const router = useRouter()
const route = useRoute()

// 本地状态管理
const isCollapsed = ref(false)

const activeMenu = computed(() => route.path)
const currentRouteName = computed(() => route.meta.title || '当前页面')

const authStore = useAuthStore()
const userAvatar = computed(() => 'https://cube.elemecdn.com/3/7c/3ea6be34...x144.jpg?') // 替换实际头像地址
const username = computed(() => authStore.user?.username || '未登录用户')
const userRole = computed(() => authStore.user?.role)

const handleMenuSelect = (path) => {
  router.push(path)
}

const handleLogout = async () => {
  authStore.logout()

  ElMessage.success('已退出登录')
  router.push('/login')
}

onMounted(async () => {
  if (authStore.token && !authStore.user) {
    await authStore.fetchUser()
  }
  websocketStore.connect();
  messageStore.init();

})
</script>

<style scoped>
.badge {
  :deep(.el-badge__content) {
    transform: scale(0.8) translate(70%, -70%);
  }
}

.app-container {
  display: flex;
  width: 100vw;
  height: 100vh;
  margin: 2.5vh auto;
  background: #f5f7f9;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
}

/* 新增头像图标样式 */
.avatar-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: #f5f7fa;
  color: #606266;
}
/* 调整用户面板对齐 */
.user-panel {
  display: flex;
  align-items: center;
  gap: 8px;
}


/* 左侧导航栏 */
.side-nav {
  width: 240px;
  height: 100%;
  background: #fff;
  box-shadow: 2px 0 12px rgba(0, 0, 0, 0.05);
  transition: width 0.3s;

  &.collapsed {
    width: 64px;
  }
}

.nav-header {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #eee;

  .logo {
    width: 32px;
    margin-right: 12px;
  }

  h2 {
    margin: 0;
    font-size: 18px;
    color: #303133;
  }
}

.collapse-btn {
  cursor: pointer;
  padding: 8px;
  margin-left: auto;

  &:hover {
    background: #f5f7fa;
    border-radius: 4px;
  }
}

.nav-menu {
  border-right: none;

  :deep(.el-sub-menu__title),
  :deep(.el-menu-item) {
    height: 48px;

    &:hover {
      background: #f5f7fa;
    }
  }
}

/* 主内容区域 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  height: 100%;
}

.top-nav {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);

  .left {
    :deep(.el-breadcrumb) {
      font-size: 14px;
    }
  }

  .user-panel {
    display: flex;
    align-items: center;
    cursor: pointer;

    .username {
      margin: 0 8px;
      color: #606266;
    }
  }
}

.content-wrapper {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  background: #fff;
  margin: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

/* 过渡动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}
</style>
