package zinc.doiche.discord.listener

import dev.minn.jda.ktx.events.CoroutineEventListener
import dev.minn.jda.ktx.events.listener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.bson.types.ObjectId
import org.slf4j.Logger
import zinc.doiche.core.domain.bunta.BuntaMessage
import zinc.doiche.core.domain.bunta.BuntaUser
import zinc.doiche.core.service.bunta.BuntaService
import zinc.doiche.core.service.openai.OpenAIService
import zinc.doiche.lib.init.Config
import zinc.doiche.lib.init.annotation.Listener
import zinc.doiche.lib.util.toPrettyJson

@Listener
fun onChat(
    jda: JDA,
    config: Config,
    buntaService: BuntaService,
    openAIService: OpenAIService,
    logger: Logger
): CoroutineEventListener = jda.listener<MessageReceivedEvent> {
    val channelUnion = it.channel

    if(it.author.isBot || !channelUnion.type.isMessage) {
        return@listener
    }

    logger.info(it.message.contentRaw)

    val textChannel = channelUnion.asTextChannel()
    val bunta = buntaService.getBunta(textChannel.idLong) ?: return@listener
    val user = it.author
    val buntaUser = buntaService.getBuntaUser(user.idLong) ?: run {
        BuntaUser(ObjectId(), user.idLong).apply {
            buntaService.saveBuntaUser(this)
        }
    }
    val message = it.message

    BuntaMessage(
        ObjectId(),
        bunta.objectId,
        buntaUser.objectId,
        message.idLong,
        content = message.contentRaw
    ).apply {
        buntaService.saveBuntaMessage(this)
    }

    openAIService.requestMessageContext(
        """
            {
                "model": "gpt-4o-mini",
                "messages": [
                    {
                        "role": "system",
                        "content": "input 형식은 '사용자: 내용' n/ output 형식은 '내용'"
                    },
                    {
                        "role": "user",
                        "content": "${user.name}: ${message.contentRaw}"
                    }
                ]
            }
        """.trimIndent().toRequestBody("application/json".toMediaType()),
        mapOf(
            "Content-Type" to "application/json",
            "Authorization" to "Bearer ${config.aiToken}"
        )
    ).toPrettyJson().let { json ->
        logger.info(json)
    }
}