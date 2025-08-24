package pl.blaszak.ai.chromaDbLoader.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.blaszak.ai.chromaDbLoader.service.ChromaDbLoaderDocumentSpliter
import pl.blaszak.ai.chromaDbLoader.service.ChromaDbService
import pl.blaszak.ai.chromaDbLoader.service.MetadataService
import pl.blaszak.ai.chromaDbLoader.service.OpenAiEmbeddingService

@Configuration
@EnableConfigurationProperties(AppSettingProperties::class)
class ChromaDbLoaderConfiguration(private val appSettings: AppSettingProperties) {

    @Bean
    fun metaDataService(): MetadataService = MetadataService(
        mapOf(
            "title" to "# ",
            "source" to "**Źródło**: ",
            "author" to "**Autor**: "
        )
    )

    @Bean
    fun openAiEmbeddingService() = OpenAiEmbeddingService(appSettings.spring.ai.openai.apiKey)

    @Bean
    fun chromaDbService() = ChromaDbService(appSettings.db.chroma.url)

    @Bean
    fun chromaDbLoaderDocumentSpliter() = ChromaDbLoaderDocumentSpliter()
}