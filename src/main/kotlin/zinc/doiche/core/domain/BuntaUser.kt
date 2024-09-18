package zinc.doiche.core.domain

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class BuntaUser(
    @BsonId
    @JsonProperty("_id")
    val objectId: ObjectId,
    val userId: Long,
) {
}