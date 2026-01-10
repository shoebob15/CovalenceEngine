package ecs

class SystemManager internal constructor(
    private val entityManager: EntityManager
) {
    private val systems = mutableListOf<System>()

    fun addSystem(system: System) {
        systems.add(system)
    }

    fun removeSystem(system: System) {
        systems.remove(system)
    }

    internal fun updateSystems() {
        systems.forEach { system ->
            system.beforeInvoke()
            entityManager.iterateEntities { entity ->
                system(entity)
            }
            system.afterInvoke()
        }
    }
}