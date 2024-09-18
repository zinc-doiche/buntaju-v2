package zinc.doiche.core.service.openai

import zinc.doiche.core.domain.BuntaMessage
import zinc.doiche.core.domain.openai.OpenAIResponse

interface OpenAIService {

    suspend fun requestMessageContext(messageList: List<BuntaMessage>): OpenAIResponse
}