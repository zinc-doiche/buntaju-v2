package zinc.doiche.lib.command

import dev.minn.jda.ktx.events.listener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import org.slf4j.Logger
import zinc.doiche.lib.init.annotation.Listener

private val commands = HashMap<String, Command>()

@Listener
fun onCommand(
    jda: JDA,
    logger: Logger
) = jda.listener<SlashCommandInteractionEvent> { event ->
    if(!event.user.isBot) {
        val commandName = event.name

        logger.info("[Command] /$commandName Called by ${event.user.name}")
        if(commandName in commands) {
            commands[commandName]?.onCommand(event)
        }
    }
}

object CommandFactory {
    fun register(jda: JDA, command: Command) {
        jda.upsertCommand(command.commandData).queue()
        commands[command.name] = command
    }

    fun command(
        name: String,
        commandData: CommandData,
        onCommand: suspend CoroutineScope.(SlashCommandInteractionEvent) -> Unit
    ) = object: Command {
        override val name = name
        override val commandData = commandData
        override suspend fun onCommand(event: SlashCommandInteractionEvent) = coroutineScope {
            onCommand(event)
        }
    }
}