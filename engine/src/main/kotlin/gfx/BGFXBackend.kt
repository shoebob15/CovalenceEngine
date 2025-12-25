package gfx

import Window
import config.ApplicationConfig
import event.EventBus
import org.lwjgl.bgfx.BGFX.*
import org.lwjgl.bgfx.BGFXFatalCallbackI
import org.lwjgl.bgfx.BGFXInit
import org.lwjgl.bgfx.BGFXPlatform.bgfx_render_frame
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import org.slf4j.LoggerFactory

internal class BGFXBackend(
    config: ApplicationConfig,
    eventBus: EventBus
) : GraphicsBackend, BGFXFatalCallbackI {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private var window = Window(config, eventBus)

    // TODO: make this crossplatform
    init {
        bgfx_render_frame(-1) // osx fix
        MemoryStack.stackPush().use { stack ->
            logger.debug("initializing bgfx")
            val init = BGFXInit.malloc(stack)
            bgfx_init_ctor(init)

            init.resolution().apply {
                width(800)
                height(600)
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
            BGFX_CLEAR_COLOR or BGFX_CLEAR_DEPTH, // why is kotlin bitwise like this
            0x28282801,
            1.0f,
            0
        )
    }

    override fun startFrame() {
        window.update()

        bgfx_set_view_rect(0, 0, 0, 800, 600)
        bgfx_touch(0) // submit empty to force clear
    }

    override fun draw(cmd: DrawCommand) {
        TODO("Not yet implemented")
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