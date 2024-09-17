package zinc.doiche.core.domain

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class BuntaUser(
    @BsonId
    val objectId: ObjectId,
    val userId: Long,
) {
}