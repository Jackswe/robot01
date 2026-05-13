<template>
  <div class="app-container">
    <ConversationList
      :conversations="conversations"
      :activeId="activeConversationId"
      @select="selectConversation"
      @create="createNewConversation"
      @delete="removeConversation"
    />
    <ChatWindow
      :conversation="activeConversation"
      :loading="loading"
      @send="handleSend"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import ConversationList from './components/ConversationList.vue'
import ChatWindow from './components/ChatWindow.vue'
import * as api from './api/chat.js'

const conversations = ref([])
const activeConversationId = ref(null)
const loading = ref(false)

const activeConversation = computed(() =>
  conversations.value.find(c => c.id === activeConversationId.value) || null
)

onMounted(async () => {
  try {
    conversations.value = await api.listConversations()
  } catch (e) {
    console.error('加载对话列表失败:', e)
  }
})

async function selectConversation(id) {
  try {
    const conv = await api.getConversation(id)
    const idx = conversations.value.findIndex(c => c.id === id)
    if (idx >= 0) {
      conversations.value[idx] = conv
    }
    activeConversationId.value = id
  } catch (e) {
    console.error('加载对话失败:', e)
  }
}

async function createNewConversation() {
  const conv = await api.createConversation('新对话')
  conversations.value.unshift(conv)
  activeConversationId.value = conv.id
}

async function removeConversation(id) {
  try {
    await api.deleteConversation(id)
    conversations.value = conversations.value.filter(c => c.id !== id)
    if (activeConversationId.value === id) {
      activeConversationId.value = conversations.value.length > 0
        ? conversations.value[0].id
        : null
    }
  } catch (e) {
    console.error('删除对话失败:', e)
  }
}

async function handleSend(message) {
  if (!activeConversationId.value) {
    await createNewConversation()
  }

  const conv = activeConversation.value
  conv.messages.push({ role: 'user', content: message })

  loading.value = true
  try {
    const result = await api.sendMessage(message, activeConversationId.value)
    conv.messages.push(result.message)
    if (result.conversationId && !activeConversationId.value) {
      activeConversationId.value = result.conversationId
    }
  } catch (e) {
    conv.messages.push({ role: 'assistant', content: '抱歉，请求失败，请检查服务是否正常运行。' })
  } finally {
    loading.value = false
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background: #f0f2f5;
  color: #333;
}

.app-container {
  display: flex;
  height: 100vh;
}
</style>
