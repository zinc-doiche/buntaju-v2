package zinc.doiche.discord.listener

import dev.minn.jda.ktx.events.CoroutineEventListener
import dev.minn.jda.ktx.events.listener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.slf4j.Logger
import zinc.doiche.core.service.bunta.BuntaService
import zinc.doiche.core.service.openai.OpenAIService
import zinc.doiche.lib.init.annotation.Listener

@Listener
fun onChat(
    jda: JDA,
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
    val message = it.message.contentRaw
    val user = it.author

    textChannel.sendMessage("준비 중입니다. 기다려").queue()
}