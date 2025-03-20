// file: src/store/chatStore.ts
import { defineStore } from 'pinia';
import { ref } from 'vue';
import { useWebsocketStore } from './websocketStore';
import { ElMessage } from 'element-plus';

interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
  timestamp: string;
  streaming?: boolean;
}

export const useChatStore = defineStore('chat', () => {
  const websocketStore = useWebsocketStore();
  const messages = ref<ChatMessage[]>([]);
  const isSending = ref(false);
  let streamCallback: ((chunk: string) => void) | null = null;

  const init = () => {
    websocketStore.subscribe('/user/queue/ai-stream', (chunk) => {
      if (chunk === '[BLANK]') {
        finalizeStream();
        return;
      }
      
      if (streamCallback) {
        streamCallback(chunk);
      } else {
        appendNewMessage(chunk);
      }
    });
  };

  const appendNewMessage = (chunk: string) => {
    const lastMsg = messages.value[messages.value.length - 1];
    if (lastMsg?.role === 'assistant' && lastMsg.streaming) {
      lastMsg.content += chunk;
    } else {
      messages.value.push({
        role: 'assistant',
        content: chunk,
        timestamp: new Date().toISOString(),
        streaming: true
      });
    }
  };

  const finalizeStream = () => {
    const lastMsg = messages.value[messages.value.length - 1];
    if (lastMsg?.streaming) {
      lastMsg.streaming = false;
    }
    streamCallback = null;
  };

  const sendMessage = async (message: string) => {
    if (!message.trim() || isSending.value) return;

    try {
      isSending.value = true;
      messages.value.push({
        role: 'user',
        content: message.trim(),
        timestamp: new Date().toISOString()
      });

      messages.value.push({
        role: 'assistant',
        content: '',
        timestamp: new Date().toISOString(),
        streaming: true
      });

      websocketStore.client?.publish({
        destination: '/app/chat.stream',
        body: message
      });
    } catch (error) {
      ElMessage.error('消息发送失败');
    } finally {
      isSending.value = false;
    }
  };

  return {
    messages,
    isSending,
    init,
    sendMessage
  };
});
