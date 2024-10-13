package zinc.doiche.core.`object`.openai

enum class Model(
    val modelName: String
) {
    GPT_4O_MINI("gpt-4o-mini"),
    GPT_4O("gpt-4o"),
    O1_PREVIEW("o1-preview"),
    O1_MINI("o1-mini");

    val isO1: Boolean
        get() = modelName.startsWith("o1")

    companion object {
        fun fromModelName(model: String): Model? {
            return Model.entries.firstOrNull { it.modelName == model }
        }
    }
}