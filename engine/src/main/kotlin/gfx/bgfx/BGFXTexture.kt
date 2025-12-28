package gfx.bgfx

import gfx.Texture
import gfx.TextureHandle
import org.lwjgl.bgfx.BGFX.bgfx_destroy_texture

internal class BGFXTexture(
    private val bgfxHandle: Short,
    override val width: Int,
    override val height: Int
) : Texture {

    override val handle = TextureHandle(bgfxHandle.toInt())

    override fun destroy() {
        bgfx_destroy_texture(bgfxHandle)
    }
}
