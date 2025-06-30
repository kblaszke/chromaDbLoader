package pl.blaszak.ai.chromaloader

import java.util.Properties

object AppProperties: Properties() {
    const val CONFIG = "/app.properties"

    init {
        val file = ChromaDbLoader::class.java.getResourceAsStream(CONFIG)
        this.load(file)
    }

}