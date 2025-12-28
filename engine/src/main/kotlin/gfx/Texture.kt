package gfx

import Destructible

// gpu representation of a texture
internal interface Texture : Destructible {
    val handle: TextureHandle
    val width: Int
    val height: Int
}