package pl.blaszak.ai.chromaloader

import org.slf4j.LoggerFactory
import java.io.File
import java.lang.System.exit
import kotlin.coroutines.Continuation

import kotlin.jvm.java

class ChromaDbLoader {

}

val LOGGER = LoggerFactory.getLogger(ChromaDbLoader::class.java)

    fun main(args: Array<String>) {

        if (args.size != 2) {
            println("Usage: ${ChromaDbLoader::class.java.simpleName} collection_name dir_with_md_files")
            exit(1)
        }
        println("Ok")
        val collectionName = args[0]
        val dir = args[1]
        LOGGER.debug("collection_name = $collectionName, dir = $dir")
        fillDb(collectionName, dir)
    }

fun fillDb(collectionName: String, dir: String) {

    val openAiClient = Context.openAiEmbeddingService
    val chromaClient = Context.chromaDbService
    val metadataService = Context.metadataService

    val files = loadMarkdownFiles(dir)

    files.forEach { file ->
        val content = readMarkdownContent(file)
        val chunks = chunkText(content, 300)
        val mainMetadata = metadataService.getMetadata(content.take(500).split("\n"))
        LOGGER.debug("==== ${file.name} ====")
        chunks.forEachIndexed { i, chunk ->
            val embedding = openAiClient.getEmbedding(chunk)
            val metadata = mainMetadata + mapOf("chunk_id" to i.toString())

            chromaClient.addEmbedding(
                collection = collectionName,
                id = "${file.name}-$i",
                embedding = embedding,
                document = chunk,
                metadata = metadata
            )
            LOGGER.debug("Embedding for ${file.name} chunk $i")
            Thread.sleep(250)
        }
    }
}


fun loadMarkdownFiles(dirPath: String): List<File> {
    val dir = File(dirPath)
    if (!dir.exists()) error("Directory not found: $dirPath")
    return dir.walkTopDown().filter { it.extension == "md" }.toList()
}

fun readMarkdownContent(file: File): String {
    return file.readText()
}

fun chunkText(content: String, maxWordsPerChunk: Int = 300): List<String> {
    val words = content.split("\\s+".toRegex())
    val chunks = mutableListOf<String>()
    var i = 0
    while (i < words.size) {
        val chunk = words.subList(i, minOf(i + maxWordsPerChunk, words.size)).joinToString(" ")
        chunks.add(chunk)
        i += maxWordsPerChunk
    }
    return chunks
}

