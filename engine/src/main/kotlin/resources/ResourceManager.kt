package resources

import Destructible
import org.slf4j.LoggerFactory

// TODO: asynchronously load resources, especially since that can include compilation in the future (shaders, images)
class ResourceManager internal constructor(
    val maxCacheSizeMb: Int
) : Destructible {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val maxCacheSizeBytes = maxCacheSizeMb * 1024L * 1024L
    private var currentCacheSize: Long = 0

    private val loaders = mutableMapOf<ResourceType, ResourceLoader<*>>()
    private val cache = mutableMapOf<String, ResourceEntry<*>>()

    fun registerLoader(loader: ResourceLoader<*>) {
        logger.debug("registered loader of type {}", loader.type)
        loaders[loader.type] = loader
    }

    fun <T> load(path: String, type: ResourceType): Resource<T> {
        val cached = cache[path]

        if (cached != null) {
            @Suppress("UNCHECKED_CAST")
            val entry = cached as ResourceEntry<T>
            cache[path] = entry.copy(
                refs = entry.refs + 1,
                lastUsed = System.currentTimeMillis()
            )
            return entry.resource
        }

        val loader = loaders[type] ?: error("no loader registered for type $type")

        @Suppress("UNCHECKED_CAST")
        loader as ResourceLoader<T>

        val data = loader.load(path)
        val resource = Resource(data, path, type)
        val size = loader.sizeOf(data)

        validateCache(size)

        cache[path] = ResourceEntry(
            resource = resource,
            size = size,
            refs = 1
        )

        currentCacheSize += size

        return resource
    }

    internal fun free(resource: Resource<*>) {
        val entry = cache[resource.path] ?: return

        if (entry.refs > 0) {
            cache[resource.path] = entry.copy(refs = entry.refs - 1)
        }
    }

    internal fun free(resourcePath: String) {
        free(cache[resourcePath]?.resource ?: return)
    }

    // checks if cache size + incomingSize is greater than maxCacheSize
    // if so, evicts earliest-used asset with zero ref-count
    private fun validateCache(incomingSize: Long) {
        if (currentCacheSize + incomingSize <= maxCacheSizeBytes) return

        val evictable = cache.entries
            .filter { it.value.refs == 0 }
            .sortedBy { it.value.lastUsed }

        for ((key, entry) in evictable) {
            cache.remove(key)
            currentCacheSize -= entry.size
            entry.resource.close()

            logger.debug("evicted resource {}", key)

            if (currentCacheSize + incomingSize <= maxCacheSizeBytes) break
        }
    }

    override fun destroy() {
        for (entry in cache.values) {
            entry.resource.close()
        }

        cache.clear()
        currentCacheSize = 0
    }
}