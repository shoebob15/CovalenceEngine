import resources.ResourceManager
import config.ApplicationConfig
import ecs.World
import event.EngineInitializationEvent
import event.Event
import event.EventBus
import event.Input
import gfx.Renderer
import gfx.bgfx.BGFXBackend
import gfx.opengl.GLBackend
import org.lwjgl.util.remotery.Remotery.*
import org.slf4j.LoggerFactory
import resources.ResourceType
import resources.loaders.BinaryResourceLoader
import resources.loaders.ImageResourceLoader
import resources.loaders.TextResourceLoader

class Application(
    config: ApplicationConfig
) {
    private val eventBus = EventBus()
    private val resourceManager = ResourceManager(config.maxCacheSize).apply {
        registerLoader(TextResourceLoader())
        registerLoader(ImageResourceLoader())
        registerLoader(BinaryResourceLoader())
    }

    private val graphicsBackend = GLBackend(config, eventBus, resourceManager)

    private val context = AppContext(
        Renderer(graphicsBackend),
        resourceManager,
        eventBus,
        0f
    )

    private val layerStack = LayerStack(context)

    private val profiler = Profiler()
    private val logger = LoggerFactory.getLogger(javaClass)

    private var running: Boolean = false
    private val targetFrameTime = 1_000_000_000L / 120
    private var lastFrameTime = System.nanoTime()

    fun run() {
        running = true

        eventBus.post(EngineInitializationEvent())

        while (running) {
            val frameStart = System.nanoTime()

            profiler.beginScope("main loop")
            context.deltaTime = (frameStart - lastFrameTime) / 1_000_000f // change to float in ms
            lastFrameTime = frameStart

            profiler.beginScope("begin frame")
            graphicsBackend.startFrame()

            if (graphicsBackend.shouldClose()) stop()

            profiler.beginScope("event dispatcher")
            eventBus.flush { event ->
                dispatchToLayers(event)
                Input.consumeEvent(event)
            }
            profiler.endScope()

            profiler.beginScope("update layers/ecs")
            for (layer in layerStack.getLayers()) {
                layer.onUpdate(context.deltaTime)
                layer.onRender()
            }
            profiler.endScope()

            graphicsBackend.endFrame()
            profiler.endScope()

            limitFrameRate(frameStart)
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

    private fun limitFrameRate(frameStart: Long) {
        val frameTime = System.nanoTime() - frameStart
        val remaining = targetFrameTime - frameTime

        if (remaining > 0) {
            Thread.sleep(
                remaining / 1_000_000,
                (remaining % 1_000_000).toInt()
            )
        }
    }

}