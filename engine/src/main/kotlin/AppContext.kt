import resources.ResourceManager
import event.EventBus

typealias InputManager = Unit
typealias Renderer = Unit

data class AppContext(
    val renderer: Renderer,
    val input: InputManager,
    val resources: ResourceManager,
    val eventBus: EventBus,
    var deltaTime: Float
)
