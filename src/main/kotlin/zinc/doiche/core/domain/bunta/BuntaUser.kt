package zinc.doiche.core.domain.bunta

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class BuntaUser(
    @SerialName("_id")
    @Contextual
    val objectId: ObjectId,
    val userId: Long,
) {
}