import ecs.World
import resources.ResourceManager
import event.EventBus
import event.Input
import gfx.Renderer

data class AppContext(
    val renderer: Renderer,
    val resources: ResourceManager,
    val eventBus: EventBus,
    var deltaTime: Float
)
