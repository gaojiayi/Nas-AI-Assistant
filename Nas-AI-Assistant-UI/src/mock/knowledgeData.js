// 知识库管理页面的mock数据

export const uploadedFilesMock = [
  {
    name: 'NAS配置指南.pdf',
    size: 2048576,
    uploadTime: new Date('2024-03-15T10:30:00'),
    status: 'indexed'
  },
  {
    name: '技术文档.docx',
    size: 1024000,
    uploadTime: new Date('2024-03-14T15:20:00'),
    status: 'indexed'
  },
  {
    name: '数据分析.xlsx',
    size: 3072000,
    uploadTime: new Date('2024-03-13T09:15:00'),
    status: 'processing'
  },
  {
    name: '产品介绍.pptx',
    size: 5120000,
    uploadTime: new Date('2024-03-12T14:45:00'),
    status: 'indexed'
  }
]

export const statsDataMock = {
  totalDocuments: 1234,
  storageSpace: '2.4GB',
  processing: 12,
  indexed: 1222
}
