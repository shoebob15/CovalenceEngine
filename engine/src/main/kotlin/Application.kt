import config.ApplicationConfig
import event.Event
import event.EventBus
import gfx.BGFXBackend
import org.lwjgl.util.remotery.Remotery.*
import org.slf4j.LoggerFactory

class Application(val config: ApplicationConfig) {
    private val eventBus = EventBus()
    private val layerStack = LayerStack()
    private val graphicsBackend = BGFXBackend(config, eventBus)

    private val context = AppContext(
        Renderer,
        InputManager,
        ResourceManager,
        eventBus,
        0f
    )

    private val profiler = Profiler()
    private val logger = LoggerFactory.getLogger(this::class.java)

    private var running: Boolean = false
    private var lastFrameTime = System.nanoTime()


    fun run() {
        running = true

        while (running) {
            profiler.beginScope("main loop")
            val currentTime = System.nanoTime()
            context.deltaTime = (currentTime - lastFrameTime) / 1_000_000f // change to float in ms

            profiler.beginScope("begin frame")
            graphicsBackend.startFrame()

            if (graphicsBackend.shouldClose()) stop()

            profiler.beginScope("event dispatcher")
            eventBus.flush { event -> dispatchToLayers(event) }
            profiler.endScope()

            profiler.beginScope("update layers")
            for (layer in layerStack.getLayers()) {
                layer.onUpdate(context.deltaTime)
                layer.onRender()
            }
            profiler.endScope()

            graphicsBackend.endFrame()
            profiler.endScope()

            lastFrameTime = System.nanoTime()
            profiler.endScope()
        }
    }

    fun stop() {
        logger.info("shutting down engine")
        running = false
        profiler.destroy()
    }

    fun <T : Layer> pushLayer(layer: T) {
        logger.info("pushing new layer to layer stack")
        layerStack.pushLayer(layer)
    }

    private fun dispatchToLayers(event: Event) {
        rmt_BeginCPUSample("dispatch event ${event::class.qualifiedName}", 0, null)
        // dispatch to top layer first
        for (layer in layerStack.getLayers().asReversed()) {
            if (layer.onEvent(event)) break
        }
        rmt_EndCPUSample()
    }
}