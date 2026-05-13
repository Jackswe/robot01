<template>
  <div class="sidebar">
    <button class="new-chat-btn" @click="$emit('create')">+ 新对话</button>
    <div class="conversation-list">
      <div
        v-for="conv in conversations"
        :key="conv.id"
        class="conv-item"
        :class="{ active: conv.id === activeId }"
        @click="$emit('select', conv.id)"
      >
        <div class="conv-title">{{ conv.title }}</div>
        <div class="conv-time">{{ formatTime(conv.createdAt) }}</div>
        <button class="delete-btn" @click.stop="$emit('delete', conv.id)">×</button>
      </div>
      <div v-if="conversations.length === 0" class="empty-tip">
        暂无对话，点击上方按钮开始
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  conversations: { type: Array, default: () => [] },
  activeId: { type: String, default: null }
})

defineEmits(['select', 'create', 'delete'])

function formatTime(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hour = String(d.getHours()).padStart(2, '0')
  const min = String(d.getMinutes()).padStart(2, '0')
  return `${month}-${day} ${hour}:${min}`
}
</script>

<style scoped>
.sidebar {
  width: 280px;
  background: #1e1e2f;
  color: #fff;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.new-chat-btn {
  margin: 16px;
  padding: 12px;
  background: #4f46e5;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  cursor: pointer;
  transition: background 0.2s;
}

.new-chat-btn:hover {
  background: #4338ca;
}

.conversation-list {
  flex: 1;
  overflow-y: auto;
  padding: 0 8px;
}

.conv-item {
  padding: 12px;
  margin-bottom: 4px;
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  transition: background 0.2s;
}

.conv-item:hover {
  background: rgba(255, 255, 255, 0.08);
}

.conv-item.active {
  background: rgba(255, 255, 255, 0.12);
}

.conv-title {
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  padding-right: 24px;
}

.conv-time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.delete-btn {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #999;
  font-size: 18px;
  cursor: pointer;
  padding: 0 4px;
}

.delete-btn:hover {
  color: #ef4444;
}

.empty-tip {
  text-align: center;
  color: #999;
  padding: 24px;
  font-size: 14px;
}
</style>
