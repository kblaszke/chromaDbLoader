package pl.blaszak.ai.chromaDbLoader

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import pl.blaszak.ai.chromaDbLoader.service.ChromaDbLoaderDocumentSplitter

import pl.blaszak.ai.chromaDbLoader.service.ChromaDbService
import pl.blaszak.ai.chromaDbLoader.service.MetadataService
import pl.blaszak.ai.chromaDbLoader.service.OpenAiEmbeddingService
import java.io.File
import kotlin.collections.forEach
import kotlin.collections.plus

@SpringBootApplication
class ChromaDbLoader(
	val metadataService: MetadataService,
	val documentSpliter: ChromaDbLoaderDocumentSplitter,
	val openAiService: OpenAiEmbeddingService,
	val chromaDbService: ChromaDbService
) : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(ChromaDbLoader::class.java)

	override fun run(vararg args: String?) {
		require(args.size == 2) {
			"Usage: ${ChromaDbLoader::class.java.simpleName} collection_name dir_with_md_files"
		}
		fillDb(args[0].orEmpty(), args[1].orEmpty())
	}

	private fun fillDb(collectionName: String, dirPath: String) {
		mdFileList(dirPath).forEach { file ->
			val content = file.readText()
			val chunks = documentSpliter.split(content)
			val mainMetadata = metadataService.getMetadata(content.take(500).lines())
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

	private fun mdFileList(dirPath: String): List<File> =
		File(dirPath).walkTopDown().filter { it.extension == "md" }.toList().also {
			require(it.isNotEmpty()) { "No markdown files found in: $dirPath" }
		}
}

fun main(args: Array<String>) {
    runApplication<ChromaDbLoader>(*args)
}