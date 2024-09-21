package zinc.doiche.discord.listener

import dev.minn.jda.ktx.events.CoroutineEventListener
import dev.minn.jda.ktx.events.listener
import net.dv8tion.jda.api.JDA
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

    runCatching lock@ {
        val textChannel = channelUnion.asTextChannel()
        val user = event.author
        val message = event.message
        val bunta = buntaService.getBunta(textChannel.idLong) ?: return@lock
        val buntaUser = buntaService.getBuntaUser(user.idLong) ?: run {
            BuntaUser(ObjectId(), user.idLong).apply {
                buntaService.saveBuntaUser(this)
            }
        }
        val messageList = run {
            buntaService.getBuntaMessageListOfBunta(bunta, 9).map {
                buntaService.getBuntaUser(it.senderObjectId)?.let { findUser ->
                    jda.getUserById(findUser.userId)?.let { discordUser ->
                        UserUnion(discordUser, findUser)
                    }
                }?.let { userUnion ->
                    MessageUnion(it, userUnion)
                } ?: run {
                    textChannel.sendMessage("메시지를 불러오는 중 오류가 발생했습니다.").queue()
                    logger.error("BuntaMessage: {}", it)
                    isLocked = false
                    return@lock
                }
            }
        }
        val newMessage = BuntaMessage(
            ObjectId(),
            bunta.objectId,
            buntaUser.objectId,
            message.idLong,
            content = message.contentRaw
        )

        openAIService.getAIResponseMessage(
            OpenAIRequest(
                MessageUnion(newMessage, UserUnion(user, buntaUser)),
                messageList
            )
        )?.let { responseMessage ->
            buntaService.saveBuntaMessage(newMessage)
            buntaService.saveBuntaMessage(responseMessage)
            textChannel.sendMessage(responseMessage.content).queue()
        }
    }.onFailure {
        logger.error("Error: ", it)
        isLocked = false
    }
    isLocked = false
}