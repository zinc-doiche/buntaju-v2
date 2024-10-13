package zinc.doiche.core.`object`

import net.dv8tion.jda.api.entities.Message
import org.bson.types.ObjectId
import zinc.doiche.core.domain.bunta.BuntaMessage

class MessageUnion(
    val buntaMessage: BuntaMessage,
    val discordMessage: Message,
    val author: UserUnion
) {
    val hasImage: Boolean
        get() = discordMessage.attachments.isNotEmpty()

    val content: String
        get() = buntaMessage.content

    val id: Long
        get() = buntaMessage.messageId

    val objectId: ObjectId
        get() = buntaMessage.objectId!!

    fun getImageURLList(): String {
        return discordMessage.attachments.joinToString(",") { attachment ->
            """
                {
                    "type": "image_url",
                    "image_url": {
                        "url": "${attachment.url}"
                    }
                }
            """.trimIndent()
        }
    }

    override fun toString(): String {
        return "MessageUnion(buntaMessage=$buntaMessage, author=$author)"
    }
}