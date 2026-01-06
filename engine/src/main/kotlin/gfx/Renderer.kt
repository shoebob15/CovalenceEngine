package gfx

import math.Vector2

class Renderer internal constructor(
    private val backend: GraphicsBackend
) {

    fun draw(path: String, position: Vector2, size: Vector2 = Vector2(1f), rotation: Float = 0f) {
        backend.draw(DrawCommand.TexturedQuad(path, position, size, rotation))
    }
}