package pl.blaszak.ai.chromaDbLoader.service

import kotlin.collections.iterator

class MetadataService(val metaAttrMap: Map<String, String>) {

    fun getMetadata(lines: List<String>): Map<String, Any?> {
        val theMap = HashMap<String, String>()
        for (line in lines) {
            for ((key, value) in metaAttrMap) {
                if (line.startsWith(value)) {
                    theMap.put(key, line.substring(value.length).trim())
                }
            }
        }
        return theMap
    }
}