import axios from 'axios'
import mockData from '../mock'

// 环境配置
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api'

// 模块级别的 Mock 配置
const USE_CHAT_MOCK = import.meta.env.VITE_USE_CHAT_MOCK === 'true'
const USE_COMMUNITY_MOCK = import.meta.env.VITE_USE_COMMUNITY_MOCK === 'true'
const USE_KNOWLEDGE_MOCK = import.meta.env.VITE_USE_KNOWLEDGE_MOCK === 'true'

// 创建axios实例
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 300000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 社区相关API
export const communityAPI = {
  // 获取社区卡片数据
  getCards: async () => {
    if (USE_COMMUNITY_MOCK) {
      return mockData.community.cards
    }
    const response = await apiClient.get('/community/cards')
    return response.data
  },

  // 获取社区页面信息
  getPageInfo: async () => {
    if (USE_COMMUNITY_MOCK) {
      return mockData.community.pageInfo
    }
    const response = await apiClient.get('/community/page-info')
    return response.data
  }
}

// 知识库相关API
export const knowledgeAPI = {
  // 获取已上传文件列表
  getUploadedFiles: async () => {
    if (USE_KNOWLEDGE_MOCK) {
      return mockData.knowledge.uploadedFiles
    }
    const response = await apiClient.get('/knowledge/files')
    return response.data
  },

  // 获取统计数据
  getStats: async () => {
    if (USE_KNOWLEDGE_MOCK) {
      return mockData.knowledge.stats
    }
    const response = await apiClient.get('/knowledge/stats')
    return response.data
  }
}

// 聊天相关API
export const chatAPI = {
  // 发送消息（SSE流式响应）
  sendMessage: async (message, options = {}) => {
    if (USE_CHAT_MOCK) {
      // 模拟AI回复
      const { responses, keywordResponses } = mockData.chat

      // 简单的关键词匹配
      if (message.includes('存储') || message.includes('空间')) {
        return keywordResponses['存储']
      } else if (message.includes('备份')) {
        return keywordResponses['备份']
      } else if (message.includes('性能') || message.includes('速度')) {
        return keywordResponses['性能']
      } else if (message.includes('安全') || message.includes('权限')) {
        return keywordResponses['安全']
      }

      // 随机返回通用回复
      return responses[Math.floor(Math.random() * responses.length)]
    }

    // 调用后端SSE接口
    const params = new URLSearchParams({
      message: message,
      chatId: options.chatId || 'default',
      enableTools: options.enableTools || false,
      enableRAG: options.enableRAG || false,
      enableThink: options.enableThinking || false
    })

    const response = await fetch(`${API_BASE_URL}/ai/nas/chat/sse?${params}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'text/event-stream',
        'Accept': 'text/event-stream'
      }
    })

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    return response.body
  },

  // 获取对话列表
  getConversations: async (appType = '') => {
    if (USE_CHAT_MOCK) {
      return []
    }
    const url = appType ? `/ai/history?appType=${encodeURIComponent(appType)}` : '/ai/history'
    const response = await apiClient.get(url)
    return response.data
  },

  // 获取指定对话的消息历史
  getConversationHistory: async (chatId) => {
    if (USE_CHAT_MOCK) {
      return []
    }
    const response = await apiClient.get(`/ai/history/${encodeURIComponent(chatId)}`)
    return response.data
  },

  // 删除指定对话
  deleteConversation: async (chatId) => {
    if (USE_CHAT_MOCK) {
      return { code: 0, message: '删除成功' }
    }
    const response = await apiClient.delete(`/ai/history/${encodeURIComponent(chatId)}`)
    return response.data
  }
}

// 导出API配置
export const apiConfig = {
  USE_CHAT_MOCK,
  USE_COMMUNITY_MOCK,
  USE_KNOWLEDGE_MOCK,
  API_BASE_URL
}
