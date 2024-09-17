package zinc.doiche.core.service.openai

import zinc.doiche.core.domain.BuntaMessage

interface OpenAIService {

    suspend fun requestMessageContext(messageList: List<BuntaMessage>): OpenAIResponse
}