package zinc.doiche.core.`object`.openai

import zinc.doiche.core.domain.bunta.Bunta
import zinc.doiche.core.`object`.MessageUnion

data class OpenAIRequest(
    val lastMessageUnion: MessageUnion,
    val messageUnionList: List<MessageUnion>
) {
    fun getRequestString(bunta: Bunta): String {
        val model = bunta.model
        val user = lastMessageUnion.author

        return """
            {
                "model": "${model.modelName}",
                "messages": [
                    ${checkSystemPrompt(model.isO1, bunta.prompt.escapeJson())}
                    ${toChatList()}
                    {
                        "role": "user",
                        "content": [
                            {
                                "type": "text",
                                "text": "${user.name}: ${lastMessageUnion.content.escapeJson()}"
                            }
                            ${
                                if(lastMessageUnion.hasImage) {
                                    ",${lastMessageUnion.getImageURLList()}"
                                } else {
                                    ""
                                }
                            }
                        ]
                    }
                ]
            }
        """.trimIndent()
    }

    private fun toChatList(): String {
        if(messageUnionList.isEmpty()) {
            return ""
        }

        return messageUnionList.joinToString(",") { messageUnion ->
            val userUnion = messageUnion.author
            """
                {
                    "role": "${if(userUnion.isAI) "assistant" else "user"}",
                    "content": "${
                        if(userUnion.isAI) {
                            messageUnion.content.escapeJson()
                        } else {
                            "${userUnion.name}: ${messageUnion.content.escapeJson()}"
                        }
                    }"
                }
            """
        }.trimIndent() + ","
    }

    private fun checkSystemPrompt(isO1: Boolean, prompt: String) =
        if(isO1)
            ""
        else
            """
            {
                "role": "system",
                "content": "사용자는 여러 명이고, 한 사용자가 대화할 때는 사용자: 내용 의 형식으로 말한다. 기본적으로 한국어로 답변해야 한다. 당신은 이렇게 말하면 안 된다. 그냥 답변만을 해야 한다. 당신이 분타주라고 가정되었을 경우, 안 좋은 예시는 다음과 같다: '분타주: 이게 내 대답이야.'. 다음은 지시 사항이다. ${prompt.escapeJson()}"
            },
        """

    private fun String.escapeJson() = replace("\"", "\\\"").replace("\n", "\\n")
}
