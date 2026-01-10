package ecs

interface System {
    fun beforeInvoke()
    operator fun invoke(entity: Entity)
    fun afterInvoke()
}