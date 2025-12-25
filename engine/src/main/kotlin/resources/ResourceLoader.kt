package resources

interface ResourceLoader<T> {
    val type: ResourceType

    // load raw data from path
    fun load(path: String): T

    // estimate size for cache (bytes)
    fun sizeOf(data: T): Long
}