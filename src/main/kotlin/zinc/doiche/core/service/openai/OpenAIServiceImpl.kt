package zinc.doiche.core.service.openai

import zinc.doiche.core.domain.BuntaMessage
import zinc.doiche.core.domain.openai.OpenAIResponse

class OpenAIServiceImpl : OpenAIService {

    override suspend fun requestMessageContext(messageList: List<BuntaMessage>): OpenAIResponse {
        TODO("Not yet implemented")
    }
}