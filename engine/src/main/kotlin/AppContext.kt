import ecs.World
import resources.ResourceManager
import event.EventBus
import gfx.Renderer

typealias InputManager = Unit

data class AppContext(
    val renderer: Renderer,
    val input: InputManager,
    val resources: ResourceManager,
    val eventBus: EventBus,
    var deltaTime: Float
)
