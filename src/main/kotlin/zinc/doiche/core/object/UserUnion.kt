package zinc.doiche.core.`object`

import net.dv8tion.jda.api.entities.User
import org.bson.types.ObjectId
import zinc.doiche.core.domain.bunta.BuntaUser

class UserUnion(
    val discordUser: User,
    val buntaUser: BuntaUser
) {
    val isAI: Boolean
        get() = discordUser.isBot

    val name: String
        get() = discordUser.globalName ?: discordUser.name

    val id: Long
        get() = discordUser.idLong

    val objectId: ObjectId
        get() = buntaUser.objectId

    override fun toString(): String {
        return "UserUnion(discordUser=$discordUser, buntaUser=$buntaUser)"
    }

}
