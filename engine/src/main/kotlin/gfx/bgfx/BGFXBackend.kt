package gfx.bgfx

import Window
import config.ApplicationConfig
import event.EventBus
import gfx.DrawCommand
import gfx.GraphicsBackend
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
    // TODO: probably don't use a pair for this, a little ambiguous (WindowStateDesc?)
    private var currentWindowSize = Pair(config.windowConfig.width, config.windowConfig.height)

    // vertex and index stuff
    private var programHandle: Short = 0

    private lateinit var texture: BGFXTexture
    private var renderer: BGFXRenderer2D

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

        // TODO: include shader compilation into resource loading pipeline | related to async resource loading task
        val fragmentShaderBuffer =
            resourceManager.load<ByteBuffer>("/shaders/bgfx/fs_sprite.bin", ResourceType.BINARY)
        val vertexShaderBuffer =
            resourceManager.load<ByteBuffer>("/shaders/bgfx/vs_sprite.bin", ResourceType.BINARY)

        val vertexMemory = bgfx_copy(vertexShaderBuffer.data)
        val fragmentMemory = bgfx_copy(fragmentShaderBuffer.data)

        if (vertexMemory == null || fragmentMemory == null) {
            error("failed to allocate bgfx memory for shaders")
        }

        val vertexShader = bgfx_create_shader(vertexMemory)
        val fragmentShader = bgfx_create_shader(fragmentMemory)

        programHandle = bgfx_create_program(vertexShader, fragmentShader, true)

        renderer = BGFXRenderer2D(programHandle)
    }

    override fun startFrame() {
        window.update()


        renderer.begin(0, currentWindowSize.first, currentWindowSize.second)

        renderer.drawQuad(
            0f,
            0f,
            100f,
            100f,
            texture,
            0xffffffff.toInt()
        )

        bgfx_dbg_text_printf(5, 5, 0x22, "covalence test application (v0.1.0 - ALPHA)")

        bgfx_touch(0)
    }

    override fun <T : DrawCommand> draw(cmd: T) {

    }

    override fun endFrame() {
        renderer.end()
        bgfx_frame(false)
    }

    override fun shouldClose(): Boolean {
        return window.shouldClose()
    }

    override fun destroy() {
        bgfx_shutdown()
        window.destroy()
    }
}