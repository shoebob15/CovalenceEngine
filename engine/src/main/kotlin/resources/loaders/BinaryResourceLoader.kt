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

    // TODO: find a way to get the size of a bytebuffer (this isn't actually how)
    // i don't feel like doing it rn
    override fun sizeOf(data: ByteBuffer): Long {
        return data.remaining().toLong()
    }
}