package com.shoebob

import ecs.System
import ecs.TransformComponent
import ecs.World
import kotlin.system.exitProcess

class ComputerSystem : System {
    private val playerSpeed = 0.1f

    private var ballY = 0f

    override fun update(world: World, deltaTime: Float) {
        world.entityManager.iterateEntities { entity ->
            if (world.entityManager.getComponent<BallComponent>(entity) != null) {
                ballY =
                    world.entityManager.getComponent<TransformComponent>(entity)?.position?.y ?: error("ball has no transform")
            }

            if (world.entityManager.getComponent<ComputerComponent>(entity) != null) {
                val transform =
                    world.entityManager.getComponent<TransformComponent>(entity) ?: error("computer has no transform")

                // this code is even more garbage
                val halfScreen = 400f
                val limit = transform.scale.y * 2f

                if (transform.position.y < ballY) { // up
                    if (transform.position.y + limit < halfScreen) {
                        transform.position.y += playerSpeed * deltaTime
                    }
                }

                if (transform.position.y > ballY) { // s
                    if (transform.position.y - limit > -halfScreen) {
                        transform.position.y -= playerSpeed * deltaTime
                    }
                }
            }
        }
    }
}