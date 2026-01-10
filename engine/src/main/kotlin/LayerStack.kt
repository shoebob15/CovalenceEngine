internal class LayerStack(
    private val context: AppContext
) {
    private var layers = mutableListOf<Layer>()

    fun pushLayer(layer: Layer) {
        layers.add(layer)
        layer.onAttach(context)
    }

    fun popLayer() = layers.removeLast().onDetach()

    internal fun getLayers(): List<Layer> = layers

}
