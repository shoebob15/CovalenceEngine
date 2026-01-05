package gfx.headless

import gfx.GraphicsBackend
import gfx.Texture
import gfx.DrawCommand
import resources.ImageData

// doesn't do anything gfx-related
internal class HeadlessBackend : GraphicsBackend {
    override fun startFrame() { }

    override fun <T : DrawCommand> draw(cmd: T) { }

    override fun endFrame() { }

    override fun shouldClose(): Boolean {
        return false
    }

    override fun destroy() { }
}