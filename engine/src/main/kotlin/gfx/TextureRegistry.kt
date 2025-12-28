package gfx

import Destructible
import resources.*

internal class TextureRegistry (
    private val backend: GraphicsBackend,
    private val resources: ResourceManager
) : Destructible {
    private val textures = mutableMapOf<String, Texture>()

    fun get(path: String): Texture {
        return textures.getOrPut(path) {
            val imageResource = resources.load<ImageData>(path, ResourceType.IMAGE)
            backend.createTexture(imageResource.data)
        }
    }

    fun release(path: String) {
        textures.remove(path)?.destroy()
        resources.free(path)
    }

    override fun destroy() {
        textures.values.forEach { it.destroy() }
        textures.clear()
    }
}