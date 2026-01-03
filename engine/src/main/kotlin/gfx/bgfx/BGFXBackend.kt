package gfx.bgfx

import Window
import config.ApplicationConfig
import event.EventBus
import gfx.DrawCommand
import gfx.GraphicsBackend
import org.joml.*
import org.lwjgl.bgfx.BGFX.*
import org.lwjgl.bgfx.*
import org.lwjgl.bgfx.BGFXPlatform.bgfx_render_frame
import org.lwjgl.system.*
import org.slf4j.LoggerFactory
import resources.*
import java.nio.*


internal class BGFXBackend(
    val config: ApplicationConfig,
    val eventBus: EventBus,
    val resourceManager: ResourceManager
) : GraphicsBackend {

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

    private val resource = resourceManager.load<ImageData>("/profile.png", ResourceType.IMAGE)

    private val window = Window(config, eventBus, resizeCallback)
    private var currentWindowSize = Pair(config.windowConfig.width, config.windowConfig.height)

    // vertex and index stuff
    private var vertexLayout: BGFXVertexLayout? = null
    private var programHandle: Short = 0

    // matrix stuff
    private var view = Matrix4x3f()
    private var viewBuffer: FloatBuffer? = null
    private var projection = Matrix4f()
    private var projectionBuffer: FloatBuffer? = null

    private lateinit var texture: BGFXTexture

    init {
        if (Platform.get() == Platform.MACOSX) {
            bgfx_render_frame(-1) // osx fix (threading etc.)
        }

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

        viewBuffer = MemoryUtil.memAllocFloat(16)
        projectionBuffer = MemoryUtil.memAllocFloat(16)

        // TODO: include shader compilation into resource loading pipeline | related to async resource loading task
        val fragmentShaderBuffer =
            resourceManager.load<ByteBuffer>("/shaders/fs_sprite.bin", ResourceType.BINARY)
        val vertexShaderBuffer =
            resourceManager.load<ByteBuffer>("/shaders/vs_sprite.bin", ResourceType.BINARY)

        val vertexMemory = bgfx_copy(vertexShaderBuffer.data)
        val fragmentMemory = bgfx_copy(fragmentShaderBuffer.data)

        if (vertexMemory == null || fragmentMemory == null) {
            error("failed to allocate bgfx memory for shaders")
        }

        val vertexShader = bgfx_create_shader(vertexMemory)
        val fragmentShader = bgfx_create_shader(fragmentMemory)

        programHandle = bgfx_create_program(vertexShader, fragmentShader, true)

        texture = createTexture(resource.data)
    }

    override fun startFrame() {
        window.update()

        val renderer = BGFXRenderer2D(programHandle)

        renderer.drawQuad(
            0f,
            0f,
            100f,
            100f,
            texture,
            0xfffffffff.toInt()
        )

        lookAt(Vector3f(0.0f, 0.0f, 0.0f), Vector3f(10.0f, 20.0f, -35.0f), view)
        ortho(0.0f, 1280.0f, 720.0f, 0.0f, 0.0f, 100.0f, view);

        bgfx_set_view_transform(0, view.get4x4(viewBuffer), projection.get(projectionBuffer));

        bgfx_dbg_text_printf(0, 200, 0x4f, "covalence test application (v0.1.0 - ALPHA)")
        bgfx_set_view_rect(0, 0, 0, currentWindowSize.first, currentWindowSize.second)
    }

    override fun <T : DrawCommand> draw(cmd: T) {
        when (cmd) {
            is DrawCommand.Clear -> {

            }

            is DrawCommand.Quad -> {

            }

            is DrawCommand.TexturedQuad -> {

            }
        }
    }

    override fun createTexture(data: ImageData): BGFXTexture {
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



    fun lookAt(at: Vector3f, eye: Vector3f, dest: Matrix4x3f) {
        dest.setLookAtLH(eye.x, eye.y, eye.z, at.x, at.y, at.z, 0.0f, 1.0f, 0.0f)
    }

    fun ortho(left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float, dest: Matrix4x3f) {
        dest.setOrthoLH(left, right, bottom, top, zNear, zFar, false)
    }
}