import org.lwjgl.system.MemoryStack
import org.lwjgl.util.remotery.Remotery.*
import org.slf4j.LoggerFactory
import kotlin.use

// TODO: don't enable/include debug tools in release builds
internal class Profiler: Destructible {
    private var handle: Long = 0 // remotery handle
    private val logger = LoggerFactory.getLogger(this::class.java)

    init {
        logger.debug("initializing remotery profiler")
        MemoryStack.stackPush().use { stack ->
            val pp = stack.mallocPointer(1)
            val result = rmt_CreateGlobalInstance(pp)
            if (result == RMT_ERROR_NONE) {
                handle = pp.get(0)
                logger.debug("remotery running on port ${rmt_Settings()?.port()}")
            }
        }

    }

    fun beginScope(name: String) {
        rmt_BeginCPUSample(name, 0, null)
    }

    fun endScope() {
        rmt_EndCPUSample()
    }

    override fun destroy() {
        logger.debug("destroying remotery profiler")
        rmt_DestroyGlobalInstance(handle)
    }
}