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

    val objectMapper = jacksonObjectMapper()
    val okHttpClient = OkHttpClient()

    fun getEmbedding(inputText: String): List<Float> {
        val request = createRequest(inputText)
        okHttpClient.newCall(request).execute().use {
            if (!it.isSuccessful) {
                throw ChromaDbLoaderException("Failed to fetch embedding: ${it.code} ${it.message}")
            }
            val responseBody = it.body?.string()
                ?: throw ChromaDbLoaderException("Empty response from OpenAI")

            val embeddingResponse = objectMapper.readValue<EmbeddingResponse>(responseBody)
            return embeddingResponse.data.first().embedding
        }
    }

    private fun createRequest(inputText: String): Request {
        val payload = EmbeddingRequest(inputText)
        val requestBody = objectMapper.writeValueAsString(payload)
        return createApiRequest(requestBody)
    }

    private fun createApiRequest(requestBody: String) = Request.Builder()
        .url("https://api.openai.com/v1/embeddings")
        .header("Authorization", "Bearer $apiKey")
        .header("Content-Type", "application/json")
        .post(requestBody.toRequestBody("application/json".toMediaType()))
        .build()
}