package gfx.opengl

import Window
import config.ApplicationConfig
import event.EventBus
import gfx.DrawCommand
import gfx.GraphicsBackend
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.*
import org.slf4j.LoggerFactory
import resources.ImageData
import resources.ResourceManager
import resources.ResourceType
import java.nio.FloatBuffer

// i really wanted to use modern opengl (4.6), but osx doesn't work with versions higher than 3.3 (lame)
internal class GLBackend(
    val config: ApplicationConfig,
    val eventBus: EventBus,
    val resources: ResourceManager
) : GraphicsBackend {

    private val logger = LoggerFactory.getLogger(javaClass)

    private var currentWindowSize = Pair(config.windowConfig.width, config.windowConfig.height)
    private val resizeCallback = { w: Int, h: Int ->
        currentWindowSize = Pair(w, h)
    }
    private val window: Window = Window(config, eventBus, resizeCallback)

    private val vertices = arrayOf(
         // positions         // colors           // uv coords
         0.5f,  0.5f, 0.0f,   1.0f, 0.0f, 0.0f,   1.0f, 1.0f,
         0.5f, -0.5f, 0.0f,   0.0f, 1.0f, 0.0f,   1.0f, 0.0f,
        -0.5f, -0.5f, 0.0f,   0.0f, 0.0f, 1.0f,   0.0f, 0.0f,
        -0.5f,  0.5f, 0.0f,   1.0f, 1.0f, 0.0f,   0.0f, 1.0f
    )

    private var verticesBuffer: FloatBuffer
    private var vbo = 0
    private var vao = 0

    val program: GLProgram
    val texture: GLTexture

    init {
        GL.createCapabilities()
        logger.info("initializing opengl graphics backend")

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER)
        // TODO: test what mipmap settings are the best
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

        verticesBuffer = BufferUtils.createFloatBuffer(vertices.size)
        verticesBuffer.put(vertices.toFloatArray())
        verticesBuffer.flip()

        program = GLProgram(
            "/shaders/glsl/vert.glsl",
            "/shaders/glsl/frag.glsl",
            resources
        )

        vao = glGenVertexArrays()
        vbo = glGenBuffers()

        glBindVertexArray(vao)
        glBindBuffer(GL_ARRAY_BUFFER, vbo)

        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW)


        // position attribute
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * 4, 0)
        glEnableVertexAttribArray(0)
        // color attribute
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * 4, 3 * 4)
        glEnableVertexAttribArray(1)
        // uv coord attribute
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * 4, 6 * 4)
        glEnableVertexAttribArray(2)

        glEnableVertexAttribArray(0)

        glBindBuffer(GL_ARRAY_BUFFER, 0)

        texture = GLTexture(resources.load<ImageData>("/test.png", ResourceType.IMAGE).data)
        glUniform1i(glGetUniformLocation(program.getHandle(), "texture1"), 0)
        glActiveTexture(GL_TEXTURE0)


    }

    override fun startFrame() {
        // clear
        glViewport(0, 0, currentWindowSize.first, currentWindowSize.second)
        glClearColor(.157f, .157f, .157f, 1f) // #282828
        glClear(GL_COLOR_BUFFER_BIT)

        program.use()
        texture.bind()
        glBindVertexArray(vao)
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4)


        window.update()
    }

    override fun endFrame() {
    }

    override fun <T : DrawCommand> draw(cmd: T) {
    }


    override fun shouldClose(): Boolean {
        return window.shouldClose()
    }

    override fun destroy() {
        glDeleteVertexArrays(vao)
        glDeleteBuffers(vbo)
        program.destroy()

        window.destroy()
    }
}