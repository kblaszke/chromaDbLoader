package pl.blaszak.ai.chromaDbLoader.service

import kotlin.collections.iterator

class MetadataService(val metaAttrMap: Map<String, String>) {

    fun getMetadata(fileName: String, lines: List<String>): Map<String, Any?> =
        buildMap {
            put("fileName", fileName)
            metaAttrMap.forEach { (key, prefix) ->
                lines.firstOrNull { it.startsWith(prefix)}
                    ?.let {put(key, it.removePrefix(prefix).trim())}
            }
        }
}