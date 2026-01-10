package ecs

import kotlin.reflect.KClass


// TODO: make entity manager more robust with bit-based tagging system
// schema-based? idk what it's called, but i read an article abt it
class EntityManager() {

    private val maxEntities = 1_000

    // both components and componentCount array are indexed by entity handle
    @PublishedApi internal val components: Array<Array<Component?>> =
            Array(maxEntities) { arrayOfNulls(8) }
    @PublishedApi internal val componentCounts = IntArray(maxEntities)

    private val slotQueue = ArrayDeque<Int>(maxEntities)

    // next empty slot to fill when creating a new entity
    // TODO: prefill slotQueue with maxEntities slots instead of keeping counter
    private var nextEntity = 0

    fun createEntity(): Entity {
        if (nextEntity >= maxEntities) {
            error("entity limit of $maxEntities reached")
        }

        // if queue is not empty, get next slot from queue
        if (slotQueue.count() != 0) {
            val slot = slotQueue.removeLast()
            componentCounts[slot] = 0
            return Entity(slot)
        }

        val handle = nextEntity
//        assertDead(Entity(handle))

        componentCounts[handle] = 0
        nextEntity += 1
        return Entity(handle)
    }

    fun deleteEntity(entity: Entity) {
        assertAlive(entity)
        components[entity.handle] = arrayOfNulls(8)
        slotQueue.addFirst(entity.handle)
    }

    fun addComponent(entity: Entity, component: Component) {
        assertAlive(entity)

        val handle = entity.handle
        val count = componentCounts[handle]
        val array = components[handle]

        // grow array of components if needed
        if (count == array.size) {
            components[handle] = array.copyOf(array.size * 2)
        }

        components[handle][count] = component
        componentCounts[handle] = count + 1
    }


    fun removeComponent(entity: Entity, type: KClass<out Component>) {
        assertAlive(entity)

        val handle = entity.handle
        val count = componentCounts[handle]
        val array = components[handle]

        for (i in 0 until count) {
            val component = array[i] ?: continue
            if (component::class == type) {
                val last = count - 1
                array[i] = array[last]
                array[last] = null
                componentCounts[handle] = last
                return
            }
        }
    }

    inline fun <reified T : Component> getComponent(entity: Entity): T? {
        assertAlive(entity)

        val handle = entity.handle
        val count = componentCounts[handle]
        val array = components[handle]

        for (i in 0 until count) {
            val component = array[i]
            if (component is T) return component
        }

        return null
    }

    fun getComponents(entity: Entity): Array<Component?> {
        assertAlive(entity)

        return components.copyOf()[entity.handle]
    }

    fun iterateEntities(perform: (Entity) -> Unit) {
        for (i in 0..nextEntity) {
            if (isAlive(Entity(i))) perform(Entity(i))
        }
    }

    // TODO: make assertations like these be debug-only
    @PublishedApi internal fun assertAlive(entity: Entity) {
        if (slotQueue.contains(entity.handle)) {
            error("alive assertation failed for entity ${entity.handle}")
        }
    }

//    @PublishedApi internal fun assertDead(entity: Entity) {
//        if (!slotQueue.contains(entity.handle)) {
//            error("dead assertation failed for entity ${entity.handle}")
//        }
//    }

    private fun isAlive(entity: Entity): Boolean {
        return !slotQueue.contains(entity.handle)
    }
}