package com.shoebob

import ecs.System
import ecs.TransformComponent
import ecs.World
import event.Input

class PlayerControlSystem : System {
    private val playerSpeed = 0.4f

    override fun update(world: World, deltaTime: Float) {
        world.entityManager.iterateEntities { entity ->
            // TODO: from an api standpoint, this is unacceptable. related to todo in EntityManager
            // TODO: more meta, but create a better way to manage TODOs (trello, fizzy, etc.)
            if (world.entityManager.getComponent<PlayerComponent>(entity) != null) {
                val transform =
                    world.entityManager.getComponent<TransformComponent>(entity) ?: error("player has no transform")

                // this code is absolute garbage
                val halfScreen = 400f
                val limit = transform.scale.y * 2f

                if (Input.isKeyPressed(87)) { // w
                    if (transform.position.y + limit < halfScreen) {
                        transform.position.y += playerSpeed * deltaTime
                    }
                }

                if (Input.isKeyPressed(83)) { // s
                    if (transform.position.y - limit > -halfScreen) {
                        transform.position.y -= playerSpeed * deltaTime
                    }
                }
            }
        }
    }
}