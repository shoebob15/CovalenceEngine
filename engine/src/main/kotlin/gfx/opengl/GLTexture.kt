package gfx.opengl

import gfx.Texture
import gfx.TextureHandle
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30.*
import org.lwjgl.stb.STBImage.stbi_image_free
import resources.ImageData

internal class GLTexture(
    val data: ImageData
) : Texture {

    override val handle = TextureHandle(glGenTextures())

    override val height = data.height
    override val width = data.width

    init {
        glBindTexture(GL_TEXTURE_2D, handle.id)

        // texture params
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

        glTexImage2D(
            GL_TEXTURE_2D,
            0,
            GL_RGBA8,
            data.width,
            data.height,
            0,
            GL_RGBA,
            GL_UNSIGNED_BYTE,
            data.pixels
        )
        glGenerateMipmap(GL_TEXTURE_2D)
    }

    override fun destroy() {
        stbi_image_free(data.pixels)
        glDeleteTextures(handle.id)
    }
}