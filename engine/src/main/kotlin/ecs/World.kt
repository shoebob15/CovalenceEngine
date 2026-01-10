package ecs

import kotlin.reflect.KClass

class World {

    @PublishedApi internal val entityManager = EntityManager()
    internal val systemManager = SystemManager(entityManager)


    fun createEntity(): Entity = entityManager.createEntity()

    fun deleteEntity(entity: Entity) = entityManager.deleteEntity(entity)

    fun addComponent(entity: Entity, component: Component) =
        entityManager.addComponent(entity, component)

    fun removeComponent(entity: Entity, type: KClass<out Component>) =
        entityManager.removeComponent(entity, type)

    inline fun <reified T : Component> getComponent(entity: Entity): T? =
        entityManager.getComponent(entity)

    fun getComponents(entity: Entity): Array<Component?> =
        entityManager.getComponents(entity)

    fun createSystem(system: System) = systemManager.addSystem(system)

    fun removeSystem(system: System) = systemManager.removeSystem(system)


}