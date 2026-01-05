package gfx

import Destructible
import gfx.bgfx.BGFXTexture
import resources.*

// TODO: use this somehow
internal class TextureRegistry (
    private val backend: GraphicsBackend,
    private val resources: ResourceManager
) : Destructible {
    private val textures = mutableMapOf<String, Texture>()

    fun get(path: String): Texture {
        return textures.getOrPut(path) {
            val imageResource = resources.load<ImageData>(path, ResourceType.IMAGE)
            // create texture
            return BGFXTexture(0, 0, 0)
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