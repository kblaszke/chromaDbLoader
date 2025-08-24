package pl.blaszak.ai.chromaDbLoader

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import pl.blaszak.ai.chromaDbLoader.service.ChromaDbLoaderDocumentSpliter

import pl.blaszak.ai.chromaDbLoader.service.ChromaDbService
import pl.blaszak.ai.chromaDbLoader.service.MetadataService
import pl.blaszak.ai.chromaDbLoader.service.OpenAiEmbeddingService
import java.io.File
import kotlin.collections.forEach
import kotlin.collections.plus
import kotlin.system.exitProcess

@SpringBootApplication
class ChromaDbLoader (
    val metadataService: MetadataService,
    val documentSpliter: ChromaDbLoaderDocumentSpliter,
	val openAiService: OpenAiEmbeddingService,
	val chromaDbService: ChromaDbService
) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(ChromaDbLoader::class.java)

    override fun run(vararg args: String?) {
		if (args.size != 2) {
            logger.error("Usage: ${ChromaDbLoader::class.java.simpleName} collection_name dir_with_md_files")
            exitProcess(1)
        }
        fillDb(args[0].toString(), args[1].toString())
    }

    fun fillDb(collectionName: String, dirPath: String) {
        val files = mdFileList(dirPath)
		files.forEach { file ->
			val content = readMarkdownContent(file)
			val chunks = documentSpliter.split(content)
			val mainMetadata = metadataService.getMetadata(content.take(500).split("\n"))
			logger.info("==== ${file.name} ====")
			chunks.forEachIndexed { i, chunk ->
				val embedding = openAiService.getEmbedding(chunk)
				val metadata = mainMetadata + mapOf("chunk_id" to i.toString())

				chromaDbService.addEmbedding(
					collection = collectionName,
					id = "${file.name}-$i",
					embedding = embedding,
					document = chunk,
					metadata = metadata
				)
				logger.info("Embedding for ${file.name} chunk $i")
				Thread.sleep(250)
			}
		}
    }

    private fun mdFileList(dirPath: String): List<File> {
        val dir = File(dirPath)
        if (!dir.exists()) error("Directory not found: $dir")
        return dir.walkTopDown().filter { it.extension == "md" }.toList()
    }
}

fun readMarkdownContent(file: File): String {
    return file.readText()
}

fun main(args: Array<String>) {
    runApplication<ChromaDbLoader>(*args)
}
