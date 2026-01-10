package ecs

import gfx.Renderer

class World(
    val entityManager: EntityManager,
    val renderer: Renderer
) {
    private val systems = mutableMapOf<FramePhase, MutableList<System>>()

    fun addSystem(phase: FramePhase, system: System) {
        systems.getOrPut(phase) { mutableListOf() }.add(system)
    }

    fun update(deltaTime: Float) {
        systems[FramePhase.UPDATE]?.forEach {
            it.update(this, deltaTime)
        }
    }

    fun render(deltaTime: Float) {
        systems[FramePhase.RENDER]?.forEach {
            it.update(this, deltaTime)
        }
    }
}