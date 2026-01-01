package gfx

import util.Color

// TODO: replace x, y, w, h with transform or smth else
internal sealed interface DrawCommand {
    data class Clear(val color: Color)
    data class Quad(val x: Float, val y: Float, val width: Float, val height: Float)
    data class TexturedQuad(val x: Float, val y: Float, val width: Float, val height: Float, val handle: TextureHandle)
    // possibly other primitives, tbd
}