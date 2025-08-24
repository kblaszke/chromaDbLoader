package pl.blaszak.ai.chromaDbLoader.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.MediaType.Companion.toMediaType
import org.slf4j.LoggerFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import pl.blaszak.ai.chromaDbLoader.exception.ChromaDbLoaderException
import pl.blaszak.ai.chromaDbLoader.model.ChromaDbRequest

class ChromaDbService(private val baseChromaUrl: String) {

    val logger = LoggerFactory.getLogger(ChromaDbService::class.java)

    val objectMapper = jacksonObjectMapper()
    private val client = OkHttpClient()

    fun addEmbedding(
        collection: String,
        id: String,
        embedding: List<Float>,
        document: String,
        metadata: Map<String, Any?>
    ) {
        val payload = ChromaDbRequest(collection, id, embedding, document, metadata)
        val requestBody = objectMapper.writeValueAsString(payload)
        val request = createRequest(requestBody)
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                logger.error("Error writing to ChromaDB: ${response.code} ${response.message}")
            }
        }
    }

    private fun createRequest(requestBody: String) = Request.Builder()
        .url("$baseChromaUrl/add-embedding")
        .post(requestBody.toRequestBody("application/json".toMediaType()))
        .addHeader("Content-Type", "application/json")
        .build()

    fun listCollections(): List<String> {
        val request = Request.Builder()
            .url("$baseChromaUrl/collections")
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errorBody = response.body?.string()
                logger.error("Error fetching collections: ${response.code} ${response.message}")
                logger.debug("response body: ${errorBody ?: "empty"}")
                return emptyList()
            }

            val responseBody = response.body?.string()
                ?: throw ChromaDbLoaderException("No server response")

            val collections = objectMapper.readTree(responseBody)
            return collections.mapNotNull { it["name"]?.asText() }
        }
    }

}
