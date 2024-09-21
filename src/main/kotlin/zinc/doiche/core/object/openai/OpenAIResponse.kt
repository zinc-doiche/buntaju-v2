package zinc.doiche.core.`object`.openai

class ChatCompletion(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val systemFingerprint: String,
    val choices: List<Choice>,
    val usage: Usage
) {
    override fun toString(): String {
        return "ChatCompletion(id='$id', `object`='$`object`', created=$created, model='$model', systemFingerprint='$systemFingerprint', choices=$choices, usage=$usage)"
    }
}

class Choice(
    val index: Int,
    val message: Message,
    val logprobs: Any?,
    val finishReason: String
) {
    override fun toString(): String {
        return "Choice(index=$index, message=$message, logprobs=$logprobs, finishReason='$finishReason')"
    }
}

class Message(
    val role: String,
    val content: String,
    val refusal : Any?
) {
    override fun toString(): String {
        return "Message(role='$role', content='$content', refusal=$refusal)"
    }
}

class Usage(
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int,
    val completionTokensDetails: CompletionTokensDetails
) {
    override fun toString(): String {
        return "Usage(promptTokens=$promptTokens, completionTokens=$completionTokens, totalTokens=$totalTokens, completionTokensDetails=$completionTokensDetails)"
    }
}

class CompletionTokensDetails(
    val reasoningTokens: Int
) {
    override fun toString(): String {
        return "CompletionTokensDetails(reasoningTokens=$reasoningTokens)"
    }
}
