package zinc.doiche.core.domain

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class BuntaMessage(
    @BsonId
    val objectId: ObjectId,
    val channelObjectId: ObjectId,
    val senderObjectId: ObjectId,
    val messageId: Long,
    val content: String,
)
