// 统一导出所有mock数据
import { uploadedFilesMock, statsDataMock } from './knowledgeData.js'
import { communityCardsMock, communityPageInfoMock } from './communityData.js'
import { aiResponsesMock, keywordResponsesMock } from './chatData.js'

export { uploadedFilesMock, statsDataMock } from './knowledgeData.js'
export { communityCardsMock, communityPageInfoMock } from './communityData.js'
export { aiResponsesMock, keywordResponsesMock } from './chatData.js'

// 默认导出所有mock数据
export default {
  knowledge: {
    uploadedFiles: uploadedFilesMock,
    stats: statsDataMock
  },
  community: {
    cards: communityCardsMock,
    pageInfo: communityPageInfoMock
  },
  chat: {
    responses: aiResponsesMock,
    keywordResponses: keywordResponsesMock
  }
}
