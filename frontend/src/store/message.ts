// file: src/store/messageStore.ts
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { useWebsocketStore } from './websocketStore';

type SystemModule = 'medical' | 'parking' | 'ticket' | 'activity';

interface SystemMessage {
  type: string;
  content: string;
  timestamp: string;
  module: SystemModule;
  read?: boolean;
}

export const useMessageStore = defineStore('message', () => {
  const websocketStore = useWebsocketStore();
  const messages = ref<SystemMessage[]>([]);
  const drawerVisible = ref(false);

  // 类型安全的模块配置
  const moduleConfig = {
    medical: {
      icon: 'FirstAidKit',
      color: 'var(--el-color-success)',
      label: '医疗通知'
    },
    parking: {
      icon: 'Van',
      color: 'var(--el-color-primary)',
      label: '停车通知'
    },
    ticket: {
      icon: 'DocumentChecked',
      color: 'var(--el-color-warning)',
      label: '工单通知'
    },
    activity: {
      icon: 'Calendar',
      color: 'var(--el-color-danger)',
      label: '活动通知'
    }
  } as const;

  // 初始化订阅
  const init = () => {
    websocketStore.subscribe('/user/queue/messages', (body) => {
      try {
        const newMsg: SystemMessage = JSON.parse(body);
        newMsg.read = false;
        messages.value.unshift(newMsg);
      } catch (e) {
        console.error('消息解析失败:', e);
      }
    });
  };

  const toggleDrawer = () => {
    drawerVisible.value = !drawerVisible.value;
    messages.value.forEach(msg => msg.read = true);
  };

  const unreadCount = computed(() => 
    messages.value.filter(msg => !msg.read).length
  );

  return {
    messages,
    drawerVisible,
    unreadCount,
    moduleConfig,
    init,
    toggleDrawer
  };
});
