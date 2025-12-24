internal class LayerStack {
    private var layers = mutableListOf<Layer>()

    fun pushLayer(layer: Layer) = layers.add(layer)

    fun popLayer() = layers.removeLast()

    internal fun getLayers(): List<Layer> = layers

}
