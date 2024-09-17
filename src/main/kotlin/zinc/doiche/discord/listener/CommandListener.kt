package zinc.doiche.discord.listener

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.SubscribeEvent
import org.slf4j.Logger
import zinc.doiche.core.service.bunta.BuntaService
import zinc.doiche.core.service.openai.OpenAIService
import zinc.doiche.lib.init.annotation.Listener

@Listener
class CommandListener(
    val buntaService: BuntaService,
    val openAIService: OpenAIService,
    val logger: Logger
) {

    @SubscribeEvent
    fun onCommand(event: SlashCommandInteractionEvent) {

    }
}