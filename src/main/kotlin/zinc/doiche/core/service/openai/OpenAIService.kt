package zinc.doiche.core.service.openai

import zinc.doiche.core.domain.bunta.BuntaMessage
import zinc.doiche.core.`object`.openai.OpenAIRequest

interface OpenAIService {

    suspend fun getAIResponseMessage(openAIRequest: OpenAIRequest): BuntaMessage?
}