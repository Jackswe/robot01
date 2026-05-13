<template>
  <div class="chat-window">
    <div v-if="!conversation" class="no-conversation">
      <div class="welcome-icon">🤖</div>
      <h2>Robot01 AI 问答系统</h2>
      <p>选择左侧对话或创建新对话开始聊天</p>
    </div>

    <template v-else>
      <div class="chat-header">
        <h3>{{ conversation.title }}</h3>
      </div>

      <div class="message-list" ref="messageListRef">
        <div
          v-for="(msg, idx) in conversation.messages"
          :key="idx"
          class="message"
          :class="msg.role"
        >
          <div class="avatar">{{ msg.role === 'user' ? '👤' : '🤖' }}</div>
          <div class="message-content">{{ msg.content }}</div>
        </div>
        <div v-if="loading" class="message assistant">
          <div class="avatar">🤖</div>
          <div class="message-content typing">
            <span></span><span></span><span></span>
          </div>
        </div>
      </div>

      <div class="input-area">
        <input
          v-model="inputText"
          type="text"
          placeholder="输入你的问题..."
          :disabled="loading"
          @keydown.enter="send"
        />
        <button :disabled="loading || !inputText.trim()" @click="send">
          发送
        </button>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'

const props = defineProps({
  conversation: { type: Object, default: null },
  loading: { type: Boolean, default: false }
})

const emit = defineEmits(['send'])

const inputText = ref('')
const messageListRef = ref(null)

watch(() => props.conversation?.messages?.length, () => {
  nextTick(() => {
    if (messageListRef.value) {
      messageListRef.value.scrollTop = messageListRef.value.scrollHeight
    }
  })
})

watch(() => props.conversation?.id, () => {
  inputText.value = ''
  nextTick(() => {
    if (messageListRef.value) {
      messageListRef.value.scrollTop = messageListRef.value.scrollHeight
    }
  })
})

function send() {
  const text = inputText.value.trim()
  if (!text || props.loading) return
  emit('send', text)
  inputText.value = ''
}
</script>

<style scoped>
.chat-window {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #fff;
}

.no-conversation {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
}

.welcome-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.no-conversation h2 {
  color: #333;
  margin-bottom: 8px;
}

.chat-header {
  padding: 16px 24px;
  border-bottom: 1px solid #eee;
}

.chat-header h3 {
  font-size: 16px;
  font-weight: 600;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}

.message {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.message.user {
  flex-direction: row-reverse;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.message-content {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 12px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.message.user .message-content {
  background: #4f46e5;
  color: #fff;
  border-bottom-right-radius: 4px;
}

.message.assistant .message-content {
  background: #f0f2f5;
  color: #333;
  border-bottom-left-radius: 4px;
}

.typing {
  display: flex;
  gap: 4px;
  align-items: center;
  padding: 16px 20px;
}

.typing span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #999;
  animation: typing 1.4s infinite;
}

.typing span:nth-child(2) { animation-delay: 0.2s; }
.typing span:nth-child(3) { animation-delay: 0.4s; }

@keyframes typing {
  0%, 60%, 100% { opacity: 0.3; transform: scale(1); }
  30% { opacity: 1; transform: scale(1.2); }
}

.input-area {
  padding: 16px 24px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 12px;
}

.input-area input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 15px;
  outline: none;
  transition: border-color 0.2s;
}

.input-area input:focus {
  border-color: #4f46e5;
}

.input-area button {
  padding: 12px 24px;
  background: #4f46e5;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  cursor: pointer;
  transition: background 0.2s;
}

.input-area button:hover:not(:disabled) {
  background: #4338ca;
}

.input-area button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
