package resources

internal data class ResourceEntry<T>(
    val resource: Resource<T>,
    val size: Long, // in bytes
    val refs: Int = 0,
    val lastUsed: Long = System.currentTimeMillis()
)
