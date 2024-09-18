package zinc.doiche.core.domain

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class BuntaUser(
    @BsonId
    @JsonProperty("_id")
    val objectId: ObjectId,
    val userId: Long,
) {
}