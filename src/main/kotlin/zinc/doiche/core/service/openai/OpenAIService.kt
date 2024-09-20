package zinc.doiche.core.service.openai

import okhttp3.RequestBody
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface OpenAIService {

    @POST("v1/chat/completions")
    suspend fun requestMessageContext(
        body: RequestBody,
        @HeaderMap headerMap: Map<String, String>
    ): Map<String, Any>
}