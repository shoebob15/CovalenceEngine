package gfx.opengl

import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30.*
import resources.ImageData

class GLTexture(
    val data: ImageData
) {

    var texture = -1

    init {
        texture = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, texture)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

        glTexImage2D(
            GL_TEXTURE_2D,
            0,
            GL_RGBA,
            data.width,
            data.height,
            0,
            GL_RGBA,
            GL_UNSIGNED_BYTE,
            data.pixels
        )
        glGenerateMipmap(GL_TEXTURE_2D)
    }

    fun bind() {
        glBindTexture(GL_TEXTURE_2D, texture)
    }

}