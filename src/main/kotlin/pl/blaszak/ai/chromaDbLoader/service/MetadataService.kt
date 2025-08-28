package pl.blaszak.ai.chromaDbLoader.service

class MetadataService(private val metaAttrMap: Map<String, String>) {

    fun getMetadata(lines: List<String>): Map<String, Any?> =
        metaAttrMap.mapNotNull { (key, prefix) ->
            lines.firstOrNull { it.startsWith(prefix) }
                ?.let { key to it.removePrefix(prefix).trim() }
        }.toMap()
}