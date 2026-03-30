// 统一导出所有mock数据

export { uploadedFilesMock, statsDataMock } from './knowledgeData.js'
export { communityCardsMock, communityPageInfoMock } from './communityData.js'

// 默认导出所有mock数据
export default {
  knowledge: {
    uploadedFiles: uploadedFilesMock,
    stats: statsDataMock
  },
  community: {
    cards: communityCardsMock,
    pageInfo: communityPageInfoMock
  }
}
