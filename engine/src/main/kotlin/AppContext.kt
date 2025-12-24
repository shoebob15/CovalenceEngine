import event.EventBus

typealias InputManager = Unit
typealias ResourceManager = Unit
typealias Renderer = Unit

class AppContext(
    val renderer: Renderer,
    val input: InputManager,
    val resources: ResourceManager,
    val eventBus: EventBus,
    var deltaTime: Float
)
