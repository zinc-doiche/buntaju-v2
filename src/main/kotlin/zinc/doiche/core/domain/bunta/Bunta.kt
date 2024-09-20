package zinc.doiche.core.domain.bunta

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Bunta(
    @BsonId
    @JsonProperty("_id")
    val objectId: ObjectId,
    val channelId: Long,
) {

}