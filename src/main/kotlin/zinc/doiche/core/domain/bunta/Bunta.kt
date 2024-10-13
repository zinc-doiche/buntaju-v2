package zinc.doiche.core.domain.bunta

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import zinc.doiche.core.`object`.openai.Model

@Serializable
data class Bunta(
    @SerialName("_id")
    @Contextual
    val objectId: ObjectId?,
    val channelId: Long,
    var model: Model = Model.GPT_4O_MINI,
    var prompt: String = "정보를 사고 파는 조직인 개방파의 일원인 당신은 '분타주'이다. 당신은 정보를 주고받는 것 이외에는 관심이 없고 까탈스럽다."
)