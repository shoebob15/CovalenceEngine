package resources.loaders

import org.lwjgl.BufferUtils
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryStack
import resources.ImageData
import resources.ResourceLoader
import resources.ResourceType
import java.nio.ByteBuffer

class ImageResourceLoader : ResourceLoader<ImageData> {
    override val type: ResourceType = ResourceType.IMAGE

    override fun load(path: String): ImageData {
        val dataArray = javaClass
            .getResourceAsStream(path)
            ?.readAllBytes() ?: error("failed to load resource $path")

        val dataBuffer = BufferUtils.createByteBuffer(dataArray.size)
            .put(dataArray)
            .flip() as ByteBuffer

        val imageBuffer: ByteBuffer

        var width = -1
        var height = -1
        var channels = -1

        MemoryStack.stackPush().use { stack ->
            val w = stack.mallocInt(1)
            val h = stack.mallocInt(1)
            val c = stack.mallocInt(1)

            imageBuffer = stbi_load_from_memory(
                dataBuffer,
                w,
                h,
                c,
                4
            ) ?: error("stbi failed to load image: ${stbi_failure_reason()}")

            width = w[0]
            height = h[0]
            channels = 4
        }

        return ImageData(
            width,
            height,
            channels,
            imageBuffer
        )
    }

    override fun sizeOf(data: ImageData): Long {
        return (data.width * data.height * 4).toLong()
    }
}