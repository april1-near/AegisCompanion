<template>
  <div class="chat-container">
    <!-- 手动重连按钮 -->
    <div v-if="!websocketStore.isConnected" class="reconnect">
      <el-button type="warning" @click="handleReconnect">重新连接</el-button>
    </div>

    <!-- 消息列表区域 -->
    <div class="message-list">
      <div 
        v-for="(msg, index) in chatStore.messages" 
        :key="index" 
        class="message-item"
        :class="msg.role"
      >
        <div class="message-content">
          <!-- 助理消息在左 -->
          <template v-if="msg.role === 'assistant'">
            <el-avatar class="avatar">
              <el-icon :size="24"><ChatDotSquare /></el-icon>
            </el-avatar>
            <div class="bubble">
              <el-card shadow="hover" class="card-style">
                <div v-text="msg.content" class="message-text"></div>
              </el-card>
            </div>
          </template>

          <!-- 用户消息在右 -->
          <template v-else>
            <div class="bubble">
              <el-card shadow="hover" class="card-style user-card">
                <div v-text="msg.content" class="message-text"></div>
              </el-card>
            </div>
            <el-avatar class="avatar">
              <el-icon :size="24"><User /></el-icon>
            </el-avatar>
          </template>
        </div>
      </div>
    </div>

    <!-- 输入框固定区域 -->
    <div class="input-area">
      <el-input class="input"
        v-model="inputMessage"
        :disabled="chatStore.isSending"
        placeholder="输入消息..."
        @keyup.enter.prevent="sendMessage"
      >
        <template #append>
          <el-button 
            type="primary" 
            :disabled="!canSend"
            @click="sendMessage"
            :icon="ChatRound"
          />
        </template>
      </el-input>
    </div>
    
  </div>
</template>

<script setup lang="ts">

import { ref, computed, onMounted } from 'vue'
import { useChatStore } from '@/store/chatStore'
import { useWebsocketStore } from '@/store/websocketStore'
import { ElMessage } from 'element-plus'
import { User, ChatDotSquare, ChatRound } from '@element-plus/icons-vue'

const chatStore = useChatStore()
const websocketStore = useWebsocketStore()
const inputMessage = ref('')

// 初始化WebSocket
onMounted(() => {
  if (!websocketStore.isConnected) {
    websocketStore.connect()
  }
  chatStore.init()
})

// 发送消息逻辑
const sendMessage = async () => {
  if (!canSend.value) return
  await chatStore.sendMessage(inputMessage.value)
  inputMessage.value = ''
}

// 手动重连
const handleReconnect = () => {
  websocketStore.reconnect()
  ElMessage.success('正在尝试重新连接...')
}

// 发送按钮状态计算
const canSend = computed(() => {
  return inputMessage.value.trim() && !chatStore.isSending
})
</script>
<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: linear-gradient(150deg, #f8f9fa 0%, #e9ecef 100%);
  position: relative;
}

/* 空状态艺术字 */
.message-list:empty::before {
  content: "✨ 开始与智慧社区助手对话 ✨";
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 2.2rem;
  font-weight: 600;
  color: rgba(64, 158, 255, 0.15);
  font-family: 'Helvetica Neue', system-ui;
  letter-spacing: 2px;
  text-shadow: 3px 3px 6px rgba(255, 255, 255, 0.8),
               -3px -3px 6px rgba(0, 0, 0, 0.05);
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-image: 
    radial-gradient(circle at 10% 10%, rgba(100, 149, 237, 0.03) 0%, transparent 50%),
    radial-gradient(circle at 90% 90%, rgba(103, 194, 58, 0.03) 0%, transparent 50%);
}

/* 消息气泡保持原有布局 */
.message-item {
  margin: 12px 0;
}

.message-content {
  display: flex;
  align-items: start;
  max-width: 95%;
  margin: 0 auto;
}

.assistant .message-content {
  justify-content: flex-start;
}

.user .message-content {
  justify-content: flex-end;
}

/* 卡片视觉增强 */
.card-style {
  border-radius: 16px;
  border: none;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 
    0 2px 8px -2px rgba(0, 0, 0, 0.03),
    0 4px 12px -4px rgba(0, 0, 0, 0.06);
  transition: transform 0.2s ease;
}

.card-style:hover {
  transform: translateY(-2px);
}

.user-card {
  background: linear-gradient(45deg, #409eff, #79bbff);
}

/* 字体排版优化 */
.message-text {
  white-space: pre-wrap;
  line-height: 1.7;
  font-size: 1.1rem;
  color: #303133;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  letter-spacing: 0.3px;
  text-shadow: 0 1px 1px rgba(255, 255, 255, 0.5);
}

.user-card .message-text {
  color: white;
  text-shadow: 0 1px 1px rgba(0, 0, 0, 0.1);
}

.avatar {
  background: linear-gradient(45deg, #67c23a, #85ce61);
  box-shadow: 0 2px 8px rgba(103, 194, 58, 0.15);
}

.input-area {
  position: sticky;
  bottom: 0;
  background: rgba(255, 255, 255, 0.98);
  padding: 20px;
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.03);
  backdrop-filter: blur(6px);
}

.input {
  height: 56px;
  border-radius: 28px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}
</style>
