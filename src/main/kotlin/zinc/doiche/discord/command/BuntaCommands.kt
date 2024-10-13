package zinc.doiche.discord.command

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.bson.types.ObjectId
import zinc.doiche.core.domain.bunta.Bunta
import zinc.doiche.core.`object`.openai.Model
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

@SlashCommand
fun changeModel(
    buntaService: BuntaService
) = CommandFactory.command(
    "모델변경",
    Commands.slash("모델변경", "분타가 사용할 모델을 변경해요.")
        .addOptions(
            OptionData(OptionType.STRING, "model", "사용할 모델", true).apply {
                Model.entries.forEach { model ->
                    addChoice(model.modelName, model.modelName)
                }
            }
        )
) { event ->
    event.deferReply(true).queue()

    val textChannel = event.channel as? TextChannel ?: return@command
    val modelName = event.getOption("model")?.asString ?: return@command
    val bunta = buntaService.getBunta(textChannel.idLong) ?: run {
        event.hook.sendMessage("분타가 등록되어 있지 않아요.").queue()
        return@command
    }
    val model = Model.fromModelName(modelName) ?: run {
        event.hook.sendMessage("해당 모델을 찾을 수 없어요.").queue()
        return@command
    }

    if(bunta.model == model) {
        event.hook.sendMessage("이미 해당 모델을 사용하고 있어요.").queue()
        return@command
    }

    bunta.model = model
    buntaService.updateBunta(bunta)
    event.hook.sendMessage("모델이 변경되었습니다.").queue()
}

@SlashCommand
fun changePrompt(
    buntaService: BuntaService
) = CommandFactory.command(
    "프롬프트변경",
    Commands.slash("프롬프트변경", "분타가 사용할 프롬프트를 변경해요.")
        .addOptions(
            OptionData(OptionType.STRING, "prompt", "사용할 프롬프트", true)
        )
) { event ->
    event.deferReply(true).queue()

    val textChannel = event.channel as? TextChannel ?: return@command
    val bunta = buntaService.getBunta(textChannel.idLong) ?: run {
        event.hook.sendMessage("분타가 등록되어 있지 않아요.").queue()
        return@command
    }
    val prompt = event.getOption("prompt")?.asString ?: return@command

    bunta.prompt = prompt
    buntaService.updateBunta(bunta)
    event.hook.sendMessage("프롬프트가 변경되었습니다.").queue()
}

@SlashCommand
fun prompt(
    buntaService: BuntaService
) = CommandFactory.command(
    "프롬프트",
    Commands.slash("프롬프트", "분타가 사용하는 프롬프트를 확인해요.")
) { event ->
    event.deferReply(true).queue()

    val textChannel = event.channel as? TextChannel ?: return@command
    val bunta = buntaService.getBunta(textChannel.idLong) ?: run {
        event.hook.sendMessage("분타가 등록되어 있지 않아요.").queue()
        return@command
    }

    event.hook.sendMessage("분타가 사용하는 프롬프트는 다음과 같아요.\n\n```\n${bunta.prompt}\n```").queue()
}

