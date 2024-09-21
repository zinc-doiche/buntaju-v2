package zinc.doiche.core.`object`.openai

import zinc.doiche.core.`object`.MessageUnion

data class OpenAIRequest(
    val lastMessageUnion: MessageUnion,
    val messageUnionList: List<MessageUnion>
) {
    val messageHistoryString: String
        get() = messageUnionList.joinToString("\n") { "${it.author.name}: ${it.content}" }
}
