package gfx.commands

import gfx.TextureHandle
import math.Vector2

internal data class TexturedQuadCommand(
    val position: Vector2,
    val size: Vector2,
    val textureHandle: TextureHandle,
    val depth: Float
) : DrawCommand
