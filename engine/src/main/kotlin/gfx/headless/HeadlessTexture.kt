package gfx.headless

import gfx.Texture
import gfx.TextureHandle

internal class HeadlessTexture(
    override val width: Int = 0,
    override val height: Int = 0
) : Texture {
    override val handle: TextureHandle = TextureHandle(0)

    override fun destroy() {
        return
    }
}