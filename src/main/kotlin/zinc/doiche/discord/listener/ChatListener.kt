package zinc.doiche.discord.listener

import dev.minn.jda.ktx.events.CoroutineEventListener
import dev.minn.jda.ktx.events.listener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.requests.RestAction
import org.bson.types.ObjectId
import org.slf4j.Logger
import zinc.doiche.core.domain.bunta.BuntaMessage
import zinc.doiche.core.domain.bunta.BuntaUser
import zinc.doiche.core.`object`.MessageUnion
import zinc.doiche.core.`object`.UserUnion
import zinc.doiche.core.`object`.openai.OpenAIRequest
import zinc.doiche.core.service.bunta.BuntaService
import zinc.doiche.core.service.openai.OpenAIService
import zinc.doiche.discord.command.debug
import zinc.doiche.lib.init.annotation.Listener
import kotlin.math.min

internal var isLocked = false

internal const val MAX_STRING_LENGTH: Int = 1000

private lateinit var tempLogger: Logger

@Listener
fun onChat(
    jda: JDA,
    buntaService: BuntaService,
    openAIService: OpenAIService,
    logger: Logger
): CoroutineEventListener = jda.listener<MessageReceivedEvent> { event ->
    val channelUnion = event.channel

    if(isLocked || event.author.isBot || !channelUnion.type.isMessage) {
        return@listener
    }
    isLocked = true

    runCatching {
        tempLogger = logger
        runWithLocking(event, buntaService, openAIService)
    }.onFailure {
        logger.error("Error: ", it)
        isLocked = false
    }
    isLocked = false
}

private suspend fun runWithLocking(
    event: MessageReceivedEvent,
    buntaService: BuntaService,
    openAIService: OpenAIService
) {
    val channelUnion = event.channel
    val textChannel = channelUnion.asTextChannel()
    val user = event.author
    val message = event.message
    val bunta = buntaService.getBunta(textChannel.idLong) ?: run {
        isLocked = false
        return
    }
    val buntaUser = buntaService.getBuntaUser(user.idLong) ?: run {
        BuntaUser(ObjectId(), user.idLong).apply {
            buntaService.saveBuntaUser(this)
        }
    }

    // 입력 제대로 들어갔는지 확인하기 용이함
    textChannel.sendTyping().queue()

    val messageList = buntaService.getMessageUnionListOfBunta(bunta, 10) ?: run {
        textChannel.sendMessage("메시지를 불러오는 중 오류가 발생했습니다.").queue()
        isLocked = false
        return
    }
    val newMessage = BuntaMessage(bunta, buntaUser, message)
    val request = OpenAIRequest(MessageUnion(newMessage, message, UserUnion(user, buntaUser)), messageList)

    openAIService.getAIResponseMessage(bunta, request)?.let { responsePair ->
        val responseMessage = responsePair.first
        val content = responseMessage.content

        buntaService.saveBuntaMessage(newMessage)
        buntaService.saveBuntaMessage(responseMessage)
        textChannel.sendMessageSequence(content).queue()

        if(debug) {
            textChannel.sendMessageSequence(
                "### Request:\n\n```${request.getRequestString(bunta)}\n\n```" +
                "### Response:\n\n```${responsePair.second}\n\n```"
            ).queue()
        }

    } ?: textChannel
        .sendMessageSequence("AI 응답을 받아오는 중 오류가 발생했습니다:\n```${request.getRequestString(bunta)}```")
        .queue()
}

private fun TextChannel.sendMessageSequence(text: String): RestAction<Message> {
    val length = text.length

    return if(length < MAX_STRING_LENGTH) {
        sendMessage(text)
    } else {
        val textList = mutableListOf<String>()
        var inCodeBlock = false;

        for (cutPoint in 0 .. length step MAX_STRING_LENGTH) {
            val slicedText = text.slice(cutPoint..< min(cutPoint + MAX_STRING_LENGTH, length))
            val codeCount = slicedText.split("```").count()

            if(codeCount % 2 != 0) {
                if(inCodeBlock) "```$slicedText```" else slicedText
            } else {
                inCodeBlock = !inCodeBlock
                if(!inCodeBlock) "```$slicedText" else "$slicedText```"
            }.let {
                textList.add(it)
            }
        }

//        tempLogger.info(textList.joinToString(", "))
        sendMessageSequence(textList.iterator())
    }
}

private fun TextChannel.sendMessageSequence(
    textIterator: Iterator<String>,
    restAction: RestAction<Message>? = null
): RestAction<Message> {
    if(textIterator.hasNext()) {
        val next = textIterator.next()

        if(next.isNotEmpty()) {
            return restAction?.onSuccess {
                sendMessageSequence(textIterator, sendMessage(next)).queue()
            } ?: sendMessageSequence(textIterator, sendMessage(next))
        }
    }
    return restAction!!
}
