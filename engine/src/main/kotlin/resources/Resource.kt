package resources

data class Resource<T>(
    val data: T,
    val path: String,
    val type: ResourceType
) : AutoCloseable {
    override fun close() {
        // loaders can override function if needed
    }
}
