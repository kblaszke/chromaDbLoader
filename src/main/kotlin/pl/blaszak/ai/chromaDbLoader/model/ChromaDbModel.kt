package pl.blaszak.ai.chromaDbLoader.model

data class ChromaDbRequest(val collection: String,
                           val ids: List<String>,
                           val embeddings: List<List<Float>>,
                           val documents: List<String>,
                           val metadatas: List<Map<String, Any?>>) {
    constructor(collection: String, id: String, embedding: List<Float>, document: String, metadata: Map<String, Any?>) :
            this(collection, listOf(id), listOf(embedding), listOf(document), listOf(metadata))
}