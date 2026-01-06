package gfx

import math.Vector2

internal sealed interface DrawCommand {
    // TODO: draw commands should not contain path/string, do a hash/handle of some kind
    // tldr: make resource system more tightly coupled to gfx system, rewrite it prob
    data class TexturedQuad(
        val path: String,
        val position: Vector2,
        val size: Vector2 = Vector2(1f),
        val rotation: Float = 0f
    ) : DrawCommand
    // possibly other primitives, tbd
}