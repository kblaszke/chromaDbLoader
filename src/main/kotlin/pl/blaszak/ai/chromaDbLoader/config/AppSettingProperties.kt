package pl.blaszak.ai.chromaDbLoader.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "")
class AppSettingProperties @ConstructorBinding constructor(
    val spring: Spring,
    val db: Db
) {
    data class Spring(val ai: Ai) {
        data class Ai(val openai: OpenAi) {
            data class OpenAi(
                val apiKey: String,
                val chat: Chat
            ) {
                data class Chat(val options: Options) {
                    data class Options(val model: String, val temperature: Double)
                }
            }
        }
    }

    data class Db(
        val chroma: Chroma
    ) {
        data class Chroma(
            val url: String,
            val collection: String,
            val cleanDataDelay: Long
        )
    }
}