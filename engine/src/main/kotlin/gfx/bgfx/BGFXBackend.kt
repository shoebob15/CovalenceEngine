package gfx.bgfx

import Window
import config.ApplicationConfig
import event.EventBus
import gfx.GraphicsBackend
import gfx.commands.DrawCommand
import gfx.Texture
import gfx.commands.QuadCommand
import gfx.commands.TexturedQuadCommand
import org.lwjgl.bgfx.BGFX.*
import org.lwjgl.bgfx.*
import org.lwjgl.bgfx.BGFXPlatform.bgfx_render_frame
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import org.slf4j.LoggerFactory
import resources.ImageData

internal class BGFXBackend(
    config: ApplicationConfig,
    eventBus: EventBus
) : GraphicsBackend, BGFXFatalCallbackI {
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

    // TODO: make this crossplatform
    init {
        bgfx_render_frame(-1) // osx fix (threading etc.)
        MemoryStack.stackPush().use { stack ->
            logger.debug("initializing bgfx")
            val init = BGFXInit.malloc(stack)
            bgfx_init_ctor(init)

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
    }

    override fun startFrame() {
        window.update()

        bgfx_set_view_rect(0, 0, 0, currentWindowSize.first, currentWindowSize.second)
        bgfx_touch(0) // submit empty to force clear
    }

    override fun draw(cmd: DrawCommand) {
        when (cmd) {
            is QuadCommand -> {

            }

            is TexturedQuadCommand -> {

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

    override fun invoke(
        _this: Long,
        _filePath: Long,
        _line: Short,
        _code: Int,
        _str: Long
    ) {
        // lwjgl bindings are special sometimes
        logger.error("bgfx had a fatal error: ${MemoryUtil.memASCII(_str)}")
    }
}