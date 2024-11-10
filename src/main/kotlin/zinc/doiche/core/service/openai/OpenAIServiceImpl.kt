package zinc.doiche.core.service.openai

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.bson.types.ObjectId
import org.slf4j.Logger
import zinc.doiche.core.collector.BuntaUserCollector
import zinc.doiche.core.domain.bunta.Bunta
import zinc.doiche.core.domain.bunta.BuntaMessage
import zinc.doiche.core.`object`.openai.ChatCompletion
import zinc.doiche.core.`object`.openai.OpenAIRequest
import zinc.doiche.core.requester.OpenAIRequester
import zinc.doiche.lib.init.Config
import zinc.doiche.lib.util.toCamelCase

class OpenAIServiceImpl(
    private val logger: Logger,
    private val config: Config,
    private val objectMapper: ObjectMapper,
    private val openAIRequester: OpenAIRequester,
    private val buntaUserCollector: BuntaUserCollector
) : OpenAIService {

    override suspend fun getAIResponseMessage(bunta: Bunta, openAIRequest: OpenAIRequest): Pair<BuntaMessage, String>? {
        val lastMessageUnion = openAIRequest.lastMessageUnion
        val requestString = openAIRequest.getRequestString(bunta)

//        logger.info("Request: \n$requestString")

        return try {
            openAIRequester.requestMessageContext(
                requestString.toRequestBody("application/json".toMediaType()),
                mapOf("Authorization" to "Bearer ${config.aiToken}")
            )?.let { response ->
                val json = objectMapper.writeValueAsString(response).toCamelCase()
                val aiUser = buntaUserCollector.getAIUser() ?: run {
                    logger.error("Failed to get AI user")
                    return null
                }

                objectMapper.readValue<ChatCompletion>(json).let { chat ->
                    chat.choices.firstOrNull()?.let { choice ->
                        BuntaMessage(
                            ObjectId(),
                            lastMessageUnion.buntaMessage.channelObjectId,
                            aiUser.objectId,
                            lastMessageUnion.id,
                            choice.message.content
                        ) to json
                    }
                }
            }
        } catch (e: Exception) {
            logger.error("Failed to get AI response message", e)
            null
        }
    }
}
