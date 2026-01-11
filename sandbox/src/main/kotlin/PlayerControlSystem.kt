package com.shoebob

import ecs.System
import ecs.TransformComponent
import ecs.World
import event.Input

class PlayerControlSystem : System {
    private val playerSpeed = 200f

    override fun update(world: World, deltaTime: Float) {
        println("fps: ${1f / deltaTime}")

        world.entityManager.iterateEntities { entity ->
            // TODO: from an api standpoint, this is unacceptable. related to todo in EntityManager
            // TODO: more meta, but create a better way to manage TODOs (trello, fizzy, etc.)
            if (world.entityManager.getComponent<PlayerComponent>(entity) != null) {
                val transform =
                    world.entityManager.getComponent<TransformComponent>(entity) ?: error("player has no transform")

                if (Input.isKeyPressed(87)) { // W
                    transform.position.y += playerSpeed * deltaTime
                }

                if (Input.isKeyPressed(83)) { // S
                    transform.position.y -= playerSpeed * deltaTime
                }

                if (Input.isKeyPressed(65)) { // A
                    transform.position.x += playerSpeed * deltaTime
                }

                if (Input.isKeyPressed(68)) { // D
                    transform.position.x -= playerSpeed * deltaTime
                }
            }
        }
    }
}