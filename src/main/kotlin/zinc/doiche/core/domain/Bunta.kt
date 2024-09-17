package zinc.doiche.core.domain

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Bunta(
    @BsonId
    val objectId: ObjectId,
    val channelId: Long,
) {

}