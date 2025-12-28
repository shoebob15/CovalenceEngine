package gfx.commands

import math.Vector2

internal data class QuadCommand(
    val position: Vector2,
    val size: Vector2,
    val color: Int,
    val depth: Float,
) : DrawCommand
