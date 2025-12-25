import config.ApplicationConfig
import event.EventBus
import event.KeyEvent
import event.WindowResizeEvent
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.*
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFWNativeCocoa.glfwGetCocoaWindow
import org.lwjgl.system.MemoryUtil.NULL
import org.slf4j.LoggerFactory

internal class Window(
    private val config: ApplicationConfig,
    private val eventBus: EventBus
) : Destructible {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private var handle: Long = 0L

    init {
        GLFWErrorCallback.createPrint(System.err).set()

        logger.debug("initializing glfw")
        if (!glfwInit()) {
            error("failed to initialize glfw")
        }

        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API)
        glfwWindowHint(GLFW_COCOA_RETINA_FRAMEBUFFER, GLFW_FALSE)

        logger.info("creating window")
        handle = glfwCreateWindow(
            config.windowConfig.width,
            config.windowConfig.height,
            config.title,
            NULL,
            NULL
        )

        if (handle == NULL) {
            error("failed to create glfw window")
        }

        glfwSetKeyCallback(handle) { _, key, scancode, action, mods ->
            eventBus.post(KeyEvent(key, scancode, action, mods))
        }

        glfwSetWindowSizeCallback(handle) { _, w, h ->
            eventBus.post(WindowResizeEvent(w, h))
        }

        glfwShowWindow(handle)
    }

    fun update() {
        glfwPollEvents()
    }

    fun shouldClose(): Boolean = glfwWindowShouldClose(handle)

    // TODO: make crossplatform
    fun getNativeWindowHandle(): Long = glfwGetCocoaWindow(handle)

    fun getWindowSize(): Int {
        TODO("not implemented")
    }

    override fun destroy() {
        logger.info("destroying window")
        glfwFreeCallbacks(handle)
        glfwDestroyWindow(handle)
        glfwTerminate()
        glfwSetErrorCallback(null)?.free()
    }
}