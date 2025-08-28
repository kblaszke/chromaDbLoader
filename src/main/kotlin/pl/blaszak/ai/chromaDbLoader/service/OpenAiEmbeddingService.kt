package pl.blaszak.ai.chromaDbLoader.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import pl.blaszak.ai.chromaDbLoader.exception.ChromaDbLoaderException
import pl.blaszak.ai.chromaDbLoader.model.EmbeddingRequest
import pl.blaszak.ai.chromaDbLoader.model.EmbeddingResponse

class OpenAiEmbeddingService(private val apiKey: String) {

    private val objectMapper = jacksonObjectMapper()
    private val okHttpClient = OkHttpClient()

    fun getEmbedding(inputText: String): List<Float> {
        require(inputText.isNotBlank()) { "Input text must not be blank" }
        val request = createRequest(inputText)
        okHttpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw ChromaDbLoaderException("Failed to fetch embedding: ${response.code} ${response.message}")
            }
            val responseBody = response.body?.string()
                ?: throw ChromaDbLoaderException("Empty response from OpenAI")
            return objectMapper.readValue<EmbeddingResponse>(responseBody).data.first().embedding
        }
    }

    private fun createRequest(inputText: String): Request =
        createApiRequest(objectMapper.writeValueAsString(EmbeddingRequest(inputText)))

    private fun createApiRequest(requestBody: String): Request =
        Request.Builder()
            .url("https://api.openai.com/v1/embeddings")
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .post(requestBody.toRequestBody("application/json".toMediaType()))
            .build()
}