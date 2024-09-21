package zinc.doiche.core.service.openai

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.bson.types.ObjectId
import org.slf4j.Logger
import zinc.doiche.core.domain.bunta.BuntaMessage
import zinc.doiche.core.`object`.openai.ChatCompletion
import zinc.doiche.core.`object`.openai.OpenAIRequest
import zinc.doiche.core.requester.OpenAIRequester
import zinc.doiche.lib.init.Config
import zinc.doiche.lib.util.toJson
import zinc.doiche.lib.util.toPrettyJson

class OpenAIServiceImpl(
    private val logger: Logger,
    private val config: Config,
    private val objectMapper: ObjectMapper,
    private val openAIRequester: OpenAIRequester,
) : OpenAIService {

    override suspend fun getAIResponseMessage(openAIRequest: OpenAIRequest): BuntaMessage? {
        val lastMessageUnion = openAIRequest.lastMessageUnion
        val user = lastMessageUnion.author

        return openAIRequester.requestMessageContext(
            """
                {
                    "model": "gpt-4o-mini",
                    "messages": [
                        {
                            "role": "system",
                            "content": "input 형식은 '사용자: 내용' \n output 형식은 '내용'. \n 정보를 사고 파는 조직인 개방파의 일원인 당신은 '분타주'로 표현된다. 이전까지의 대화 목록은 다음과 같다. \n\n ${openAIRequest.messageHistoryString}"
                        },
                        {
                            "role": "user",
                            "content": "${user.name}: ${lastMessageUnion.content}"
                        }
                    ]
                }
            """.trimIndent().toRequestBody("application/json".toMediaType()),
            mapOf(
                "Content-Type" to "application/json",
                "Authorization" to "Bearer ${config.aiToken}"
            )
        )?.let { response ->
            objectMapper.readValue<ChatCompletion>(response.toJson()).let { chat ->
                chat.choices.firstOrNull()?.let { choice ->
                    BuntaMessage(
                        ObjectId(),
                        lastMessageUnion.buntaMessage.channelObjectId,
                        ObjectId(),
                        lastMessageUnion.id,
                        choice.message.content
                    )
                }
            }
        }
    }
}