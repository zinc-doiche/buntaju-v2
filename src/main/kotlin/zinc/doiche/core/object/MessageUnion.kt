package zinc.doiche.core.`object`

import org.bson.types.ObjectId
import zinc.doiche.core.domain.bunta.BuntaMessage

class MessageUnion(
    val buntaMessage: BuntaMessage,
    val author: UserUnion
) {
    val content: String
        get() = buntaMessage.content

    val id: Long
        get() = buntaMessage.messageId

    val objectId: ObjectId
        get() = buntaMessage.objectId
}