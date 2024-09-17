package zinc.doiche.discord.listener

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.SubscribeEvent
import org.slf4j.Logger
import zinc.doiche.core.service.bunta.BuntaService
import zinc.doiche.core.service.openai.OpenAIService
import zinc.doiche.lib.init.annotation.Listener

@Listener
class ChatListener(
    val buntaService: BuntaService,
    val openAIService: OpenAIService,
    val logger: Logger
) {
    @SubscribeEvent
    fun onMessageReceived(event: MessageReceivedEvent) {
//        logger.info("Message received: ${event.message.contentRaw}")
    }
}