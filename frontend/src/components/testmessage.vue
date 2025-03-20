<template>
  <el-card class="chat-window">
    <template #header>
      <div class="chat-header">
        <span>聊天室</span>
        <el-button type="primary" @click="connect" >
          {{ isConnected ? '已连接' : '连接' }}
        </el-button>
        <el-button type="danger" @click="disconnect" >
          断开
        </el-button>

        <el-button type="danger" @click="subscribeTest" >
          订阅chat 流
        </el-button>

        <el-button type="primary" @click="sendMessage2">
          发送到chat 流
        </el-button>

      </div>
    </template>
    <div class="chat-messages">
      <div v-show="messages">
   <!-- </--   <div v-for="(message, index) in messages" :key="index" class="message"> -->
        <!-- {{ messages }} -->
      </div>
    </div>
    <el-input
      v-model="inputMessage"
      placeholder="输入消息"
      @keyup.enter="sendMessage"
      width="40px"
    >
      <template #append>
        <el-button type="primary" @click="sendMessage">
          发送
        </el-button>
        <br />

      </template>
    </el-input>
  </el-card>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useMessageStore } from '@/store/message';

const chatStore = useMessageStore();
const inputMessage = ref('');
const { messages, isConnected } = chatStore;

// 连接 WebSocket
const connect = () => {
  console.log('点击连接按钮');
  chatStore.connect();
};

// 断开 WebSocket
const disconnect = () => {
  console.log('点击断开按钮');
  chatStore.disconnect();
};

// 发送消息
const sendMessage = () => {
  if (inputMessage.value.trim()) {
    chatStore.sendMessage(inputMessage.value,'/app/hello');
    inputMessage.value = '';
  }
};
// 发送消息
const sendMessage2 = () => {
  if (inputMessage.value.trim()) {
    chatStore.sendMessage(inputMessage.value,'/app/chat.stream');
    inputMessage.value = '';
  }
};

const subscribeTest =()=>{
  chatStore.subscribeTest();
}


</script>

<style scoped>
.chat-window {
  width: 400px;
  margin: 20px auto;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-messages {
  height: 300px;
  overflow-y: auto;
  border: 1px solid #ebeef5;
  padding: 10px;
  margin-bottom: 10px;
}

.message {
  margin-bottom: 10px;
}
</style>
