// file: src/store/websocketStore.ts
import { defineStore } from 'pinia';
import { ref } from 'vue';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { useAuthStore } from './auth';
import { ElMessage } from 'element-plus';

type Subscription = {
  id: string;
  destination: string;
  callback: (body: string) => void;
};

export const useWebsocketStore = defineStore('websocket', () => {
  const authStore = useAuthStore();
  const client = ref<Client | null>(null);
  const isConnected = ref(false);
  const subscriptions = ref<Subscription[]>([]);

  const connect = () => {
    if (isConnected.value) return;

    const socket = new SockJS(`/ws?token=${encodeURIComponent(authStore.token)}`);
    
    client.value = new Client({
      webSocketFactory: () => socket,
      connectHeaders: { Authorization: `Bearer ${authStore.token}` },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        isConnected.value = true;
        // 重新注册所有订阅
        subscriptions.value.forEach(sub => {
          client.value?.subscribe(sub.destination, message => sub.callback(message.body));
        });
      },
      onStompError: (frame) => {
        ElMessage.error(`连接错误: ${frame.headers.message}`);
      },
      onWebSocketClose: () => {
        isConnected.value = false;
      }
    });

    client.value.activate();
  };

  const subscribe = (destination: string, callback: (body: string) => void) => {
    const subId = `sub-${Date.now()}`;
    subscriptions.value.push({ id: subId, destination, callback });
    
    if (client.value?.connected) {
      return client.value.subscribe(destination, message => callback(message.body));
    }
  };

  const unsubscribe = (subId: string) => {
    subscriptions.value = subscriptions.value.filter(sub => sub.id !== subId);
  };

  const disconnect = () => {
    client.value?.deactivate();
    isConnected.value = false;
    subscriptions.value = [];
  };


  // 在websocketStore.ts中添加
const reconnect = () => {
  if (client.value) {
    client.value.deactivate()
  }
  connect()
}


  return {
    client,
    isConnected,
    connect,
    disconnect,
    subscribe,
    unsubscribe,
    reconnect
  };
});
