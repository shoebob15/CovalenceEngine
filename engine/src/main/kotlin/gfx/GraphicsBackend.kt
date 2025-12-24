package gfx

import Destructible

typealias DrawCommand = Unit
// redundant, since bgfx *should* handle all graphics backends
// however, i wanted to add headless backend too, so this fits i think
internal interface GraphicsBackend : Destructible {
    fun startFrame()
    fun draw(cmd: DrawCommand)
    fun endFrame()

    fun shouldClose(): Boolean
}