package gfx.headless

import gfx.GraphicsBackend
import gfx.DrawCommand
import gfx.Texture
import gfx.TextureHandle
import resources.ImageData


// doesn't do anything gfx-related - for headless applications (servers, bots, etc.)
internal class HeadlessBackend : GraphicsBackend {
    override fun startFrame() { }

    override fun endFrame() { }

    override fun draw(cmd: DrawCommand) {
        TODO("Not yet implemented")
    }

    override fun shouldClose(): Boolean {
        return false
    }

    override fun createTexture(data: ImageData): Texture {
        return HeadlessTexture()
    }

    override fun destroy() { }

    private class HeadlessTexture() : Texture {
        override val handle = TextureHandle(0)
        override val width = 0
        override val height = 0

        override fun destroy() { }
    }
}