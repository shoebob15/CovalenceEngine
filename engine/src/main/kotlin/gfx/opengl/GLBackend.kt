package gfx.opengl

import Window
import config.ApplicationConfig
import event.EventBus
import gfx.Camera2D
import gfx.DrawCommand
import gfx.GraphicsBackend
import gfx.Texture
import gfx.TextureHandle
import gfx.TextureRegistry
import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.*
import org.slf4j.LoggerFactory
import resources.ImageData
import resources.ResourceManager
import java.nio.FloatBuffer

// i really wanted to use modern opengl (4.6), but osx doesn't work with versions higher than 3.3 (lame)
// TODO: batch render quads
internal class GLBackend(
    private val config: ApplicationConfig,
    private val eventBus: EventBus,
    private val resources: ResourceManager
) : GraphicsBackend {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val camera = Camera2D().also {
        it.update(
            config.windowConfig.width,
            config.windowConfig.height
        )
    }
    private val viewBuffer = BufferUtils.createFloatBuffer(16)

    private val textureRegistry = TextureRegistry(this, resources)

    private var currentWindowSize = Pair(config.windowConfig.width, config.windowConfig.height)
    private val resizeCallback = { w: Int, h: Int ->
        currentWindowSize = Pair(w, h)
        camera.update(w, h)
    }
    private val window: Window = Window(config, eventBus, resizeCallback)


    // TODO: abstract vertice code (especially vertex attributes) to external quad class
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

    init {
        GL.createCapabilities()
        logger.info("initializing opengl graphics backend")

        verticesBuffer = BufferUtils.createFloatBuffer(vertices.size)
        verticesBuffer.put(vertices.toFloatArray())
        verticesBuffer.flip()

        program = GLProgram(
            "/shaders/glsl/vert.glsl",
            "/shaders/glsl/frag.glsl",
            resources
        )
        program.use()

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

        glBindBuffer(GL_ARRAY_BUFFER, 0)

        glUniform1i(glGetUniformLocation(program.getHandle(), "texture1"), 0)
        glActiveTexture(GL_TEXTURE0)
    }

    override fun startFrame() {
        camera.position.x -= 1f
        // clear
        glViewport(0, 0, currentWindowSize.first, currentWindowSize.second)
        // TODO: create clear color command or smth in api
        glClearColor(.157f, .157f, .157f, 1f) // #282828
        glClear(GL_COLOR_BUFFER_BIT)

        program.use()
        glBindVertexArray(vao)
    }

    override fun endFrame() {
        window.update()
    }

    override fun draw(cmd: DrawCommand) {
        when (cmd) {
            is DrawCommand.TexturedQuad -> {
                val texture = textureRegistry.get(cmd.path)
                bindTexture(texture.handle)

                val model = Matrix4f()
                    .translate(cmd.position.x, cmd.position.y, 0f)
                    .rotateZ(cmd.rotation)
                    .scale(cmd.size.x * texture.width, cmd.size.y * texture.height, 1f)

                val mvp = camera.getProjection()
                    .mul(model, Matrix4f())

                mvp.get(viewBuffer.clear())
                glUniformMatrix4fv(
                    glGetUniformLocation(program.getHandle(), "uViewProjection"),
                    false,
                    viewBuffer
                )

                glDrawArrays(GL_TRIANGLE_FAN, 0, 4)
            }
        }
    }

    private fun bindTexture(handle: TextureHandle) {
        glBindTexture(GL_TEXTURE_2D, handle.id)
    }

    override fun shouldClose(): Boolean {
        return window.shouldClose()
    }

    override fun createTexture(data: ImageData): Texture {
        return GLTexture(data)
    }

    override fun destroy() {
        glDeleteVertexArrays(vao)
        glDeleteBuffers(vbo)
        program.destroy()

        window.destroy()
    }
}