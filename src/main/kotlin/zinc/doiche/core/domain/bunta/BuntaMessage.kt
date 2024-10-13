package zinc.doiche.core.domain.bunta

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.dv8tion.jda.api.entities.Message
import org.bson.types.ObjectId

@Serializable
data class BuntaMessage(
    @SerialName("_id")
    @Contextual
    val objectId: ObjectId?,

    @Contextual
    val channelObjectId: ObjectId,

    @Contextual
    val senderObjectId: ObjectId,
    val messageId: Long,
    val content: String,
) {
    constructor(bunta: Bunta, buntaUser: BuntaUser, message: Message) : this(
        ObjectId.get(),
        bunta.objectId!!,
        buntaUser.objectId,
        message.idLong,
        message.contentRaw
    )

    override fun toString(): String {
        return "BuntaMessage(objectId=$objectId, channelObjectId=$channelObjectId, senderObjectId=$senderObjectId, messageId=$messageId, content='$content')"
    }
}
