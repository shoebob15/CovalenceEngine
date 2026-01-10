package ecs

interface System {
    fun update(world: World, deltaTime: Float)
}