package zinc.doiche.discord.listener

import dev.minn.jda.ktx.events.CoroutineEventListener
import dev.minn.jda.ktx.events.listener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.bson.types.ObjectId
import org.slf4j.Logger
import zinc.doiche.core.domain.bunta.BuntaMessage
import zinc.doiche.core.domain.bunta.BuntaUser
import zinc.doiche.core.`object`.MessageUnion
import zinc.doiche.core.`object`.UserUnion
import zinc.doiche.core.`object`.openai.OpenAIRequest
import zinc.doiche.core.service.bunta.BuntaService
import zinc.doiche.core.service.openai.OpenAIService
import zinc.doiche.lib.init.annotation.Listener

internal var isLocked = false

internal const val MAX_CHARACTER_SIZE: Long = 2000

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
        runWithLocking(event, buntaService, openAIService)
    }.onFailure {
        logger.error("Error: ", it)
        isLocked = false
    }
    isLocked = false
}

private fun TextChannel.sendMessageSequence(textIterator: Iterator<String>) {
    if(textIterator.hasNext()) {
        sendMessage(textIterator.next()).onSuccess {
            sendMessageSequence(textIterator)
        }.queue()
    }
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
    val messageList = buntaService.getMessageUnionListOfBunta(bunta, 10) ?: run {
        textChannel.sendMessage("메시지를 불러오는 중 오류가 발생했습니다.").queue()
        isLocked = false
        return
    }
    val newMessage = BuntaMessage(bunta, buntaUser, message)
    val request = OpenAIRequest(MessageUnion(newMessage, message, UserUnion(user, buntaUser)), messageList)

    openAIService.getAIResponseMessage(bunta, request)?.let { responseMessage ->
        val content = responseMessage.content
        val count = content.chars().count()

        buntaService.saveBuntaMessage(newMessage)
        buntaService.saveBuntaMessage(responseMessage)

        if(count < MAX_CHARACTER_SIZE) {
            textChannel.sendMessage(content).queue()
        } else {
            val charArray = content.toCharArray()
            val textList = mutableListOf<String>()

            for (cutPoint in MAX_CHARACTER_SIZE ..< count step MAX_CHARACTER_SIZE) {
                val text = charArray.sliceArray(cutPoint.toInt() ..< MAX_CHARACTER_SIZE.toInt()).toString()
                textList.add(text)
            }

            textChannel.sendMessageSequence(textList.iterator())
        }
    } ?: textChannel.sendMessage("AI 응답을 받아오는 중 오류가 발생했습니다:\n```${request.getRequestString(bunta)}```").queue()
}