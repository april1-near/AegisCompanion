<!-- file: src/components/MessageDrawer.vue -->
<template>
  <el-drawer
    v-model="visible"
    title="系统通知"
    :size="380"
    direction="rtl"
    class="message-drawer"
  >
    <el-scrollbar class="drawer-content">
      <div 
        v-for="(msg, index) in messageStore.messages"
        :key="index"
        class="message-item"
        :style="{ borderLeft: `3px solid ${getModuleConfig(msg.module).color}` }"
      >
        <div class="message-header">
          <el-icon :size="18" :color="getModuleConfig(msg.module).color">
            <component :is="getModuleConfig(msg.module).icon" />
          </el-icon>
          <span class="module-name">{{ getModuleConfig(msg.module).label }}</span>
          <span class="time">{{ formatTime(msg.timestamp) }}</span>
        </div>
        <div class="message-body">
          <div class="content">{{ msg.content }}</div>
        </div>
      </div>
    </el-scrollbar>
  </el-drawer>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useMessageStore } from '@/store/message';
import { formatDateTime } from '@/utils/date';

const messageStore = useMessageStore();

const visible = computed({
  get: () => messageStore.drawerVisible,
  set: (val) => (messageStore.drawerVisible = val)
});

const getModuleConfig = (module: keyof typeof messageStore.moduleConfig) => {
  return messageStore.moduleConfig[module];
};

const formatTime = (isoString: string) => 
  formatDateTime(isoString, 'MM-DD HH:mm');
</script>
