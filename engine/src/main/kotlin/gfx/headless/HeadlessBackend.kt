package gfx.headless

import gfx.GraphicsBackend
import gfx.Texture
import gfx.commands.DrawCommand
import resources.ImageData

// doesn't do anything gfx-related
internal class HeadlessBackend : GraphicsBackend {
    override fun startFrame() { }

    override fun draw(cmd: DrawCommand) { }
    override fun createTexture(data: ImageData): Texture = HeadlessTexture()

    override fun endFrame() { }

    override fun shouldClose(): Boolean {
        return false
    }

    override fun destroy() { }
}