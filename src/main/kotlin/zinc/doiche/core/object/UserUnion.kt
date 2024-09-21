package zinc.doiche.core.`object`

import net.dv8tion.jda.api.entities.User
import org.bson.types.ObjectId
import zinc.doiche.core.domain.bunta.BuntaUser

class UserUnion(
    val discordUser: User,
    val buntaUser: BuntaUser
) {
    val name: String
        get() = discordUser.name

    val id: Long
        get() = discordUser.idLong

    val objectId: ObjectId
        get() = buntaUser.objectId

}
