package resources.loaders

import resources.ResourceLoader
import resources.ResourceType
import java.nio.charset.StandardCharsets

class TextResourceLoader : ResourceLoader<List<String>> {
    override val type = ResourceType.TEXT

    override fun load(path: String): List<String> {
        return javaClass
            .getResourceAsStream(path)
            ?.bufferedReader()
            ?.readLines()
            ?: error("failed to load resource $path")
    }

    override fun sizeOf(data: List<String>): Long {
        return data
            .joinToString("\n")
            .toByteArray(StandardCharsets.UTF_8)
            .size.toLong()
    }
}