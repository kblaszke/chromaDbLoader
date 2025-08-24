package pl.blaszak.ai.chromaDbLoader.service

import dev.langchain4j.data.document.Document
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter
import dev.langchain4j.data.document.splitter.DocumentSplitters
import dev.langchain4j.data.segment.TextSegment

class ChromaDbLoaderDocumentSpliter {

    companion object {
        val paragraphSplitter = DocumentByParagraphSplitter(1000, 0) // maxSize, overlap (w znakach)
        val recursiveSplitter = DocumentSplitters.recursive(500, 50)
    }

    public fun split(content: String): List<String> {
        val doc = Document.from(content)
        val paragraphs: List<TextSegment> = paragraphSplitter.split(doc)

        val chunks: List<TextSegment> = paragraphs.flatMap { paragraph ->
            val paragraphAsDocument = Document.from(paragraph.text(), paragraph.metadata())
            recursiveSplitter.split(paragraphAsDocument) // zwraca List<TextSegment>
        }
        return chunks.map { it.text() }
    }
}