import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import zinc.doiche.core.`object`.openai.ChatCompletion
import zinc.doiche.lib.util.ObjectIdModule
import zinc.doiche.lib.util.toPrettyJson


fun main() {
    """
        {
          "id" : "chatcmpl-A9hpQQijuSSUI6QDauLO6M0CwYUCf",
          "object" : "chat.completion",
          "created" : 1726876792,
          "model" : "gpt-4o-mini-2024-07-18",
          "choices" : [ {
            "index" : 0,
            "message" : {
              "role" : "assistant",
              "content" : "I'm Here!",
              "refusal" : null
            },
            "logprobs" : null,
            "finish_reason" : "stop"
          } ],
          "usage" : {
            "prompt_tokens" : 80,
            "completion_tokens" : 14,
            "total_tokens" : 94,
            "completion_tokens_details" : {
              "reasoning_tokens" : 0
            }
          },
          "system_fingerprint" : "fp_1bb46167f9"
        }
    """.trimIndent().let {
        ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .registerModule(ObjectIdModule())
            .registerKotlinModule()
            .readValue(it, ChatCompletion::class.java)
    }.let {
        println(it)
        it
    }.let {
        println()
        println()
        println()
        println(it.toPrettyJson())
    }
}
