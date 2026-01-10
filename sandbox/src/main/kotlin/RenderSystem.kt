package com.shoebob

import ecs.RenderComponent
import ecs.System
import ecs.TransformComponent
import ecs.World

class RenderSystem : System {
    override fun update(world: World, deltaTime: Float) {
        val em = world.entityManager
        em.iterateEntities { entity ->
            val transform = em.getComponent<TransformComponent>(entity) ?: return@iterateEntities
            val render = em.getComponent<RenderComponent>(entity) ?: return@iterateEntities

            world.renderer.draw(
                render.path,
                transform.position,
                transform.scale,
                transform.rotation
            )
        }
    }
}