<template>
  <div class="knowledge-base">
    <div class="page-header">
      <h1>知识库管理</h1>
      <p>轻松管理您的知识文档，支持多种格式文件上传</p>
    </div>
    
    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon documents">
          <svg width="32" height="32" viewBox="0 0 32 32" fill="none">
            <path d="M8 2v24a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6H10a2 2 0 0 0-2 2z" stroke="currentColor" stroke-width="2"/>
            <path d="M18 2v6h6" stroke="currentColor" stroke-width="2"/>
          </svg>
        </div>
        <div class="stat-info">
          <h3>{{ statsData.totalDocuments.toLocaleString() }}</h3>
          <p>总文档数</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon storage">
          <svg width="32" height="32" viewBox="0 0 32 32" fill="none">
            <path d="M4 10h24M4 16h24M4 22h24" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </div>
        <div class="stat-info">
          <h3>{{ statsData.storageSpace }}</h3>
          <p>存储空间</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon processing">
          <svg width="32" height="32" viewBox="0 0 32 32" fill="none">
            <path d="M16 2v8M8 10l8-8 8 8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <circle cx="16" cy="20" r="8" stroke="currentColor" stroke-width="2"/>
            <path d="M16 16v8M12 20h8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </div>
        <div class="stat-info">
          <h3>{{ statsData.processing }}</h3>
          <p>处理中</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon indexed">
          <svg width="32" height="32" viewBox="0 0 32 32" fill="none">
            <path d="M16 2l-5 8h10l-5-8z" stroke="currentColor" stroke-width="2"/>
            <path d="M11 10h10v18H11V10z" stroke="currentColor" stroke-width="2"/>
            <path d="M6 28h20" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </div>
        <div class="stat-info">
          <h3>{{ statsData.indexed.toLocaleString() }}</h3>
          <p>已索引</p>
        </div>
      </div>
    </div>
    
    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧上传区域 -->
      <div class="left-section">
        <div class="upload-section">
          <h3>上传文档</h3>
          <div class="upload-area" 
               :class="{ 'drag-over': isDragOver }"
               @drop="handleDrop"
               @dragover.prevent="isDragOver = true"
               @dragleave="isDragOver = false"
               @dragenter.prevent="isDragOver = true">
            <div class="upload-icon">
              <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
                <path d="M24 6v24M12 18l12-12 12 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M6 36h36" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              </svg>
            </div>
            <h4>拖拽文件到此处上传</h4>
            <p>支持 PDF、Word、Excel、PPT、TXT 等多种格式</p>
            <input type="file" ref="fileInput" @change="handleFileSelect" multiple hidden>
            <button class="upload-btn" @click="$refs.fileInput.click()">选择文件</button>
          </div>
        </div>
      </div>
      
      <!-- 右侧文档列表 -->
      <div class="right-section">
        <div class="document-list">
          <div class="list-header">
            <h3>文档列表</h3>
            <div class="search-box">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                <circle cx="9" cy="9" r="7" stroke="currentColor" stroke-width="2"/>
                <path d="M14 14l4 4" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              </svg>
              <input type="text" placeholder="搜索文档...">
            </div>
          </div>
          
          <div class="document-grid">
            <div v-for="(file, index) in uploadedFiles" 
                 :key="index" 
                 class="document-item"
                 :class="{ selected: selectedFile === index }"
                 @click="selectFile(index)">
              <div class="document-icon">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                  <path d="M6 2v16a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V8l-6-6H8a2 2 0 0 0-2 2z" stroke="currentColor" stroke-width="2"/>
                  <path d="M14 2v6h6" stroke="currentColor" stroke-width="2"/>
                  <path d="M10 14h4M10 18h4" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                </svg>
              </div>
              <div class="document-info">
                <h4>{{ file.name }}</h4>
                <p>{{ formatFileSize(file.size) }} • {{ formatDate(file.uploadTime) }}</p>
              </div>
              <div class="document-status" :class="file.status || 'indexed'">
                {{ getStatusText(file.status) }}
              </div>
            </div>
          </div>
          
          <!-- 重建索引按钮 -->
          <div class="action-section" v-if="selectedFile !== null">
            <button class="reindex-btn" @click="reindexFile">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                <path d="M4 10a6 6 0 0 1 6-6v6m-6 0h6m0 0v6m0-6a6 6 0 0 0 6 6v-6m0 0h-6m0 0v-6" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              </svg>
              重建索引
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { knowledgeAPI } from '../services/api.js'

const isDragOver = ref(false)
const fileInput = ref(null)
const selectedFile = ref(null)
const loading = ref(false)

const uploadedFiles = ref([])
const statsData = ref({
  totalDocuments: 0,
  storageSpace: '0GB',
  processing: 0,
  indexed: 0
})

// 加载知识库数据
const loadKnowledgeData = async () => {
  loading.value = true
  try {
    const [filesData, stats] = await Promise.all([
      knowledgeAPI.getUploadedFiles(),
      knowledgeAPI.getStats()
    ])
    uploadedFiles.value = filesData
    statsData.value = stats
  } catch (error) {
    console.error('加载知识库数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadKnowledgeData()
})

const handleDrop = (e) => {
  e.preventDefault()
  isDragOver.value = false
  
  const files = Array.from(e.dataTransfer.files)
  files.forEach(file => {
    uploadedFiles.value.push({
      name: file.name,
      size: file.size,
      uploadTime: new Date(),
      status: 'processing'
    })
  })
}

const handleFileSelect = (e) => {
  const files = Array.from(e.target.files)
  files.forEach(file => {
    uploadedFiles.value.push({
      name: file.name,
      size: file.size,
      uploadTime: new Date(),
      status: 'processing'
    })
  })
}

const selectFile = (index) => {
  selectedFile.value = index
}

const reindexFile = () => {
  if (selectedFile.value !== null) {
    const file = uploadedFiles.value[selectedFile.value]
    file.status = 'processing'
    
    // 模拟重建索引过程
    setTimeout(() => {
      file.status = 'indexed'
    }, 2000)
  }
}

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const formatDate = (date) => {
  return new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date)
}

const getStatusText = (status) => {
  const statusMap = {
    'processing': '处理中',
    'indexed': '已索引',
    'failed': '失败'
  }
  return statusMap[status] || '已索引'
}
</script>

<style scoped>
.knowledge-base {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  border-radius: 20px;
  padding: 40px;
  min-height: calc(100vh - 48px);
  color: #ffffff;
}

.page-header {
  margin-bottom: 32px;
}

.page-header h1 {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-header p {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.7);
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 32px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  background: rgba(255, 255, 255, 0.08);
  transform: translateY(-4px);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
}

.stat-icon.documents {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.storage {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.processing {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.indexed {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info h3 {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 4px;
}

.stat-info p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
}

/* 主要内容区域 */
.main-content {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 32px;
  margin-top: 32px;
}

/* 左侧上传区域 */
.left-section {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 32px;
}

.left-section h3 {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 24px;
}

.upload-area {
  border: 2px dashed rgba(102, 126, 234, 0.5);
  border-radius: 16px;
  padding: 40px 24px;
  text-align: center;
  background: rgba(255, 255, 255, 0.05);
  transition: all 0.3s ease;
  cursor: pointer;
}

.upload-area.drag-over {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.1);
  transform: scale(1.02);
}

.upload-icon {
  margin-bottom: 20px;
  color: #667eea;
}

.upload-area h4 {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
}

.upload-area p {
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 20px;
  font-size: 14px;
}

.upload-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  padding: 10px 20px;
  color: #ffffff;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.upload-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(102, 126, 234, 0.3);
}

/* 右侧文档列表 */
.right-section {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 32px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.list-header h3 {
  font-size: 20px;
  font-weight: 600;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  padding: 8px 12px;
  width: 240px;
}

.search-box svg {
  color: rgba(255, 255, 255, 0.6);
}

.search-box input {
  background: none;
  border: none;
  color: #ffffff;
  font-size: 14px;
  outline: none;
  width: 100%;
}

.search-box input::placeholder {
  color: rgba(255, 255, 255, 0.6);
}

.document-grid {
  display: grid;
  gap: 12px;
  max-height: 400px;
  overflow-y: auto;
}

.document-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.document-item:hover {
  background: rgba(255, 255, 255, 0.08);
}

.document-item.selected {
  background: rgba(102, 126, 234, 0.2);
  border-color: rgba(102, 126, 234, 0.5);
}

.document-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  flex-shrink: 0;
}

.document-info {
  flex: 1;
}

.document-info h4 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 4px;
}

.document-info p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
}

.document-status {
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}

.document-status.indexed {
  background: rgba(67, 233, 123, 0.2);
  color: #43e97b;
}

.document-status.processing {
  background: rgba(79, 172, 254, 0.2);
  color: #4facfe;
}

.document-status.failed {
  background: rgba(239, 68, 68, 0.2);
  color: #ef4444;
}

.action-section {
  margin-top: 20px;
  text-align: center;
}

.reindex-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  padding: 12px 24px;
  color: #ffffff;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.reindex-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(102, 126, 234, 0.3);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .main-content {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .knowledge-base {
    padding: 20px;
  }
}
</style>
