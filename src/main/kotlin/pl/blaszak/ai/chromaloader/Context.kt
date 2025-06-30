package pl.blaszak.ai.chromaloader

import pl.blaszak.ai.chromaloader.service.ChromaDbService
import pl.blaszak.ai.chromaloader.service.MetadataService
import pl.blaszak.ai.chromaloader.service.OpenAiEmbeddingService

object Context {

    private val apiKey = getProperty("app.openai.api.key")
    private val baseChromaUrl = getProperty("app.chromadb.baseurl")

    val openAiEmbeddingService = OpenAiEmbeddingService(getProperty("app.openai.api.key"))
    val chromaDbService = ChromaDbService(getProperty("app.chromadb.baseurl"))
    val metadataService = MetadataService(mapOf(
        "title" to "# ",
        "source" to "**Źródło**: ",
        "author" to "**Autor**: "))


    fun getProperty(key: String): String = (AppProperties[key] ?: error("Missing $key property")) as String
}
