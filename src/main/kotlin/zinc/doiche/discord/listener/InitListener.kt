package zinc.doiche.discord.listener

import dev.minn.jda.ktx.events.listener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.StatusChangeEvent
import org.bson.types.ObjectId
import zinc.doiche.core.domain.bunta.BuntaUser
import zinc.doiche.core.service.bunta.BuntaService
import zinc.doiche.lib.init.annotation.Listener

@Listener
fun onInit(
    jda: JDA,
    buntaService: BuntaService
) = jda.listener<StatusChangeEvent> { event ->
    if(event.newStatus != JDA.Status.CONNECTED) {
        return@listener
    }
    val selfUserId = jda.selfUser.idLong

    buntaService.getBuntaUser(selfUserId) ?: run {
        BuntaUser(ObjectId(), selfUserId).apply {
            buntaService.saveBuntaUser(this)
        }
    }
}