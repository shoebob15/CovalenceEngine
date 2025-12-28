package gfx

import Destructible
import gfx.commands.DrawCommand
import resources.ImageData

// redundant, since bgfx *should* handle all graphics backends
// however, i wanted to add headless backend too, so this fits i think
internal interface GraphicsBackend : Destructible {
    fun startFrame()
    fun endFrame()

    fun draw(cmd: DrawCommand)
    fun createTexture(data: ImageData) : Texture

    fun shouldClose(): Boolean
}