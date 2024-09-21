package zinc.doiche.core.requester

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface OpenAIRequester {

    @POST("v1/chat/completions")
    suspend fun requestMessageContext(
        @Body body: RequestBody,
        @HeaderMap headerMap: Map<String, String>
    ): Any?
}