package gfx.bgfx

import Window
import config.ApplicationConfig
import event.EventBus
import gfx.DrawCommand
import gfx.GraphicsBackend
import gfx.PosColorVertex
import gfx.Texture
import org.joml.Matrix4f
import org.joml.Matrix4x3f
import org.joml.Vector3f
import org.lwjgl.bgfx.BGFX.*
import org.lwjgl.bgfx.BGFXInit
import org.lwjgl.bgfx.BGFXPlatform.bgfx_render_frame
import org.lwjgl.bgfx.BGFXVertexLayout
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import org.slf4j.LoggerFactory
import resources.ImageData
import resources.ResourceManager
import resources.ResourceType
import util.Color
import java.nio.ByteBuffer
import java.nio.FloatBuffer


internal class BGFXBackend(
    config: ApplicationConfig,
    eventBus: EventBus,
    resourceManager: ResourceManager
) : GraphicsBackend {

    private val cubeVertices = arrayOf(
        PosColorVertex(-1.0f, 1.0f, 1.0f, Color(1.0f, 0.0f, 0.0f, 1.0f)),
        PosColorVertex(1.0f, 1.0f, 1.0f, Color(1.0f, 1.0f, 0.0f, 1.0f)),
        PosColorVertex(-1.0f, -1.0f, 1.0f, Color(1.0f, 0.0f, 0.0f, 1.0f)),
        PosColorVertex(1.0f, -1.0f, 1.0f, Color(0.0f, 1.0f, 1.0f, 1.0f)),
        PosColorVertex(-1.0f, 1.0f, -1.0f, Color(1.0f, 0.0f, 0.0f, 1.0f)),
        PosColorVertex(1.0f, 1.0f, -1.0f, Color(1.0f, 0.0f, 1.0f, 1.0f)),
        PosColorVertex(-1.0f, -1.0f, -1.0f, Color(1.0f, 1.0f, 0.0f, 1.0f)),
        PosColorVertex(1.0f, -1.0f, -1.0f, Color(1.0f, 0.0f, 0.0f, 1.0f)),
    )

    private val cubeIndices = arrayOf(
        0, 1, 2,
        1, 3, 2,
        4, 6, 5,
        5, 6, 7,
        0, 2, 4,
        4, 2, 6,
        1, 5, 3,
        5, 7, 3,
        0, 4, 1,
        4, 5, 1,
        2, 3, 6,
        6, 3, 7,
    )

    private val logger = LoggerFactory.getLogger(javaClass)

    private val resizeCallback: (Int, Int) -> Unit = { width: Int, height: Int ->
        currentWindowSize = Pair(width, height)
        bgfx_reset(
            width,
            height,
            BGFX_RESET_VSYNC,
            BGFX_TEXTURE_FORMAT_RGBA16
        )
    }


    private val window = Window(config, eventBus, resizeCallback)
    private var currentWindowSize = Pair(config.windowConfig.width, config.windowConfig.height)

    // vertex and index stuff
    private var vertexLayout: BGFXVertexLayout? = null
    private var vertexBuffer: ByteBuffer? = null
    private var vbh: Short = 0
    private var indexBuffer: ByteBuffer? = null
    private var ibh: Short = 0
    private var programHandle: Short = 0

    // matrix stuff
    private var view = Matrix4x3f()
    private var viewBuffer: FloatBuffer? = null
    private var projection = Matrix4f()
    private var projectionBuffer: FloatBuffer? = null
    private var model = Matrix4x3f()
    private var modelBuffer: FloatBuffer? = null



    // TODO: make this crossplatform
    init {
        bgfx_render_frame(-1) // osx fix (threading etc.)
        MemoryStack.stackPush().use { stack ->
            logger.debug("initializing bgfx")
            val init = BGFXInit.calloc(stack)
            bgfx_init_ctor(init)
            init.callback(BGFXCallbacks.createCallbacks(stack))
            init.type(BGFX_RENDERER_TYPE_OPENGL)

            init.resolution().apply {
                width(config.windowConfig.width)
                height(config.windowConfig.height)
                reset(BGFX_RESET_VSYNC)
            }

            init.platformData().apply {
                nwh(window.getNativeWindowHandle())
            }

            if (!bgfx_init(init)) {
                error("failed to initialize bgfx backend")
            }
        }

        logger.debug("using bgfx backend with ${bgfx_get_renderer_name(bgfx_get_renderer_type())}")
        bgfx_set_debug(BGFX_DEBUG_STATS)
        bgfx_set_view_clear(
            0,
            BGFX_CLEAR_COLOR or BGFX_CLEAR_DEPTH,
            0x28282801,
            1.0f,
            0
        )

        vertexLayout = createVertexLayout()
        indexBuffer = MemoryUtil.memAlloc(cubeIndices.size * 2)
        vertexBuffer = MemoryUtil.memAlloc(8 * (3 * 4 + 4))

        vbh = createVertexBuffer()
        ibh = createIndexBuffer()

        viewBuffer = MemoryUtil.memAllocFloat(16)
        projectionBuffer = MemoryUtil.memAllocFloat(16)
        modelBuffer = MemoryUtil.memAllocFloat(16)
        val fragmentShaderBuffer = resourceManager.load<ByteBuffer>("/shaders/fs_simple.bin", ResourceType.BINARY)
        val vertexShaderBuffer = resourceManager.load<ByteBuffer>("/shaders/vs_simple.bin", ResourceType.BINARY)

        val vertexMemory = bgfx_copy(vertexShaderBuffer.data)
        val fragmentMemory = bgfx_copy(fragmentShaderBuffer.data)

        // TODO: unwrap safely
        val vertexShader = bgfx_create_shader(vertexMemory!!)
        val fragmentShader = bgfx_create_shader(fragmentMemory!!)

        programHandle = bgfx_create_program(vertexShader, fragmentShader, true)
    }

    override fun startFrame() {
        window.update()

        lookAt(Vector3f(0.0f, 0.0f, 0.0f), Vector3f(0.0f, 0.0f, -35.0f), view)
        perspective(60.0f, 800, 600, 0.1f, 100.0f, projection)

        bgfx_set_view_transform(0, view.get4x4(viewBuffer), projection.get(projectionBuffer));

        bgfx_dbg_text_printf(0, 1, 0x4f, "covalence test application (v0.1.0 - ALPHA)")
        bgfx_set_view_rect(0, 0, 0, currentWindowSize.first, currentWindowSize.second)

        bgfx_set_state(
            BGFX_STATE_WRITE_R
                    or BGFX_STATE_WRITE_G
                    or BGFX_STATE_WRITE_B
                    or BGFX_STATE_WRITE_A
                    or BGFX_STATE_WRITE_Z
                    or BGFX_STATE_DEPTH_TEST_LESS
                    or BGFX_STATE_CULL_CW
                    or BGFX_STATE_MSAA,
            0
        )

        val encoder = bgfx_encoder_begin(false)

        bgfx_encoder_set_vertex_buffer(encoder, 0, vbh, 0, 8)
        bgfx_encoder_set_index_buffer(encoder, ibh, 0, 36)

        bgfx_encoder_set_state(encoder, BGFX_STATE_DEFAULT, 0)

        bgfx_encoder_submit(encoder, 0, programHandle, 0, 0)

        bgfx_encoder_end(encoder)
    }

    override fun draw(cmd: DrawCommand) {
        when (cmd) {
            is DrawCommand.Clear -> {

            }

            is DrawCommand.Quad -> {

            }

            is DrawCommand.TexturedQuad -> {

            }
        }
    }

    override fun createTexture(data: ImageData): Texture {
        val mem = bgfx_copy(data.pixels)

        val handle = bgfx_create_texture_2d(
            data.width,
            data.height,
            false,
            1,
            BGFX_TEXTURE_FORMAT_RGBA8,
            BGFX_TEXTURE_NONE,
            mem
        )

        return BGFXTexture(
            handle,
            data.width,
            data.height
        )

    }

    override fun endFrame() {
        bgfx_frame(false)
    }

    override fun shouldClose(): Boolean {
        return window.shouldClose()
    }

    override fun destroy() {
        bgfx_shutdown()
        window.destroy()
    }

    private fun createVertexLayout(): BGFXVertexLayout {
        val layout = BGFXVertexLayout.calloc()

        bgfx_vertex_layout_begin(layout, bgfx_get_renderer_type())

        bgfx_vertex_layout_add(
            layout,
            BGFX_ATTRIB_POSITION,
            3,
            BGFX_ATTRIB_TYPE_FLOAT,
            false,
            false
        )

        bgfx_vertex_layout_add(
            layout,
            BGFX_ATTRIB_COLOR0,
            4,
            BGFX_ATTRIB_TYPE_FLOAT,
            false,
            false
        )

        bgfx_vertex_layout_end(layout)

        return layout
    }

    fun createVertexBuffer(): Short {
        // 4 bytes (float) * 7 floats per vertex * len
        val buffer = MemoryUtil.memAlloc(4 * 7 * cubeVertices.size)
        for (vertex in cubeVertices) {
            buffer.putFloat(vertex.x)
            buffer.putFloat(vertex.y)
            buffer.putFloat(vertex.z)
            buffer.putFloat(vertex.color.r)
            buffer.putFloat(vertex.color.g)
            buffer.putFloat(vertex.color.b)
            buffer.putFloat(vertex.color.a)
        }

        buffer.flip()

        val memory = bgfx_copy(buffer)
        // TODO: safely unwrap these and error() if null
        return bgfx_create_vertex_buffer(memory!!, vertexLayout!!, BGFX_BUFFER_NONE)
    }

    fun createIndexBuffer(): Short {
        // 2 bytes per short * length of index array
        val buffer = MemoryUtil.memAlloc(2 * cubeIndices.size)
        for (index in cubeIndices) {
            buffer.putShort(index.toShort())
        }

        buffer.flip()

        val memory = bgfx_copy(buffer)
        // TODO: here too
        return bgfx_create_index_buffer(memory!!, BGFX_BUFFER_NONE)
    }

    fun lookAt(at: Vector3f, eye: Vector3f, dest: Matrix4x3f) {
        dest.setLookAtLH(eye.x, eye.y, eye.z, at.x, at.y, at.z, 0.0f, 1.0f, 0.0f)
    }

    fun perspective(fov: Float, width: Int, height: Int, near: Float, far: Float, dest: Matrix4f) {
        val fovRadians = fov * Math.PI.toFloat() / 180.0f
        val aspect = width / height.toFloat()
        dest.setPerspectiveLH(fovRadians, aspect, near, far, false)
    }
}