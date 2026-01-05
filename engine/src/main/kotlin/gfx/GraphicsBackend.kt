package gfx

import Destructible
import gfx.DrawCommand
import resources.ImageData

// redundant, since bgfx *should* handle all graphics backends
// however, i wanted to add headless backend too, so this fits i think
internal interface GraphicsBackend : Destructible {
    fun startFrame()
    fun endFrame()

    fun <T : DrawCommand> draw(cmd: T)

    fun shouldClose(): Boolean
}