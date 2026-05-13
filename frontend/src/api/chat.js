const API_BASE = '/api'

export async function sendMessage(message, conversationId) {
  const res = await fetch(`${API_BASE}/chat`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ message, conversationId })
  })
  if (!res.ok) throw new Error('请求失败')
  return res.json()
}

export async function listConversations() {
  const res = await fetch(`${API_BASE}/conversations`)
  if (!res.ok) throw new Error('请求失败')
  return res.json()
}

export async function createConversation(title) {
  const res = await fetch(`${API_BASE}/conversations`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ title })
  })
  if (!res.ok) throw new Error('请求失败')
  return res.json()
}

export async function getConversation(id) {
  const res = await fetch(`${API_BASE}/conversations/${id}`)
  if (!res.ok) throw new Error('请求失败')
  return res.json()
}

export async function deleteConversation(id) {
  const res = await fetch(`${API_BASE}/conversations/${id}`, {
    method: 'DELETE'
  })
  if (!res.ok) throw new Error('请求失败')
}
