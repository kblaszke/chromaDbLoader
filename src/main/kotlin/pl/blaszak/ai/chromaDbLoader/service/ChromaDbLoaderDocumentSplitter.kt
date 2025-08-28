package pl.blaszak.ai.chromaDbLoader.service

import dev.langchain4j.data.document.Document
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter
import dev.langchain4j.data.document.splitter.DocumentSplitters
import dev.langchain4j.data.segment.TextSegment

class ChromaDbLoaderDocumentSplitter {

    private val paragraphSplitter = DocumentByParagraphSplitter(1000, 0)
    private val recursiveSplitter = DocumentSplitters.recursive(500, 50)

    fun split(content: String): List<String> =
        paragraphSplitter.split(Document.from(content))
            .flatMap { paragraph ->
                recursiveSplitter.split(Document.from(paragraph.text(), paragraph.metadata()))
            }
            .map { it.text() }
}