package pl.blaszak.ai.chromaloader.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

data class EmbeddingRequest(val input: String, val model: String = "text-embedding-3-small")
@JsonIgnoreProperties(ignoreUnknown = true)
data class EmbeddingResponse(val data: List<EmbeddingData>)
@JsonIgnoreProperties(ignoreUnknown = true)
data class EmbeddingData(val embedding: List<Float>, val index: Int)