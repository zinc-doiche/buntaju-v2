package zinc.doiche.discord.command

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.bson.types.ObjectId
import zinc.doiche.core.domain.Bunta
import zinc.doiche.core.service.bunta.BuntaService
import zinc.doiche.lib.command.CommandFactory
import zinc.doiche.lib.init.annotation.SlashCommand

@SlashCommand
fun buntaFound(buntaService: BuntaService) = CommandFactory.command(
    "분타설립",
    Commands.slash("분타설립", "분타주가 해당 채널에서 활동할 수 있게 해요.")
) { event ->

    event.deferReply(true).queue()

    val hook = event.hook.setEphemeral(true)
    val textChannel = event.channel as? TextChannel ?: run {
        hook.sendMessage("텍스트 채널에서만 사용할 수 있어요.").queue()
        return@command
    }
    val channelId = textChannel.idLong
    val bunta = buntaService.getBunta(channelId)?.let {
        hook.sendMessage("'${textChannel.name}' 채널은 이미 등록되어 있어요.").queue()
        return@command
    } ?: Bunta(ObjectId(), channelId)

    buntaService.saveBunta(bunta)
    hook.sendMessage("'${textChannel.name}' 채널이 등록되었습니다.").queue()
}
