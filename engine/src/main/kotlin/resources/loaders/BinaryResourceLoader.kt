package resources.loaders

import org.lwjgl.BufferUtils
import resources.ResourceLoader
import resources.ResourceType
import java.nio.ByteBuffer

class BinaryResourceLoader : ResourceLoader<ByteBuffer> {
    override val type: ResourceType = ResourceType.BINARY

    override fun load(path: String): ByteBuffer {
        val dataArray = javaClass
            .getResourceAsStream(path)
            ?.readAllBytes() ?: error("failed to load resource $path")

        val dataBuffer = BufferUtils.createByteBuffer(dataArray.size)
            .put(dataArray)
            .flip() as ByteBuffer

        return dataBuffer
    }

    override fun sizeOf(data: ByteBuffer): Long {
        return data.position().toLong()
    }
}