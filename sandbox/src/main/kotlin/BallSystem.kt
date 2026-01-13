package com.shoebob

import ecs.System
import ecs.TransformComponent
import ecs.VelocityComponent
import ecs.World
import kotlin.random.Random
import kotlin.system.exitProcess

class BallSystem : System {
    var ballSpeedX = 0.2f
    var ballSpeedY = 0.03f

    var cpuY = 0f
    var playerY = 0f

    override fun update(world: World, deltaTime: Float) {
        world.entityManager.iterateEntities { entity ->
            if (world.entityManager.getComponent<PlayerComponent>(entity) != null) {
                playerY =
                    world.entityManager.getComponent<TransformComponent>(entity)?.position?.y ?: error("ball has no transform")
            }
            if (world.entityManager.getComponent<ComputerComponent>(entity) != null) {
                cpuY =
                    world.entityManager.getComponent<TransformComponent>(entity)?.position?.y ?: error("ball has no transform")
            }
            if (world.entityManager.getComponent<BallComponent>(entity) != null) {
                val transform = world.entityManager.getComponent<TransformComponent>(entity) ?: return@iterateEntities
                val velocity = world.entityManager.getComponent<VelocityComponent>(entity) ?: return@iterateEntities

                // collide with player
                if (
                    transform.position.x >= 350f &&
                    transform.position.y in (playerY - 50f)..(playerY + 50f)
                ) {
                    val dir = if (Random.nextBoolean()) 1 else -1
                    velocity.x = -ballSpeedX
                    velocity.y = dir * ballSpeedY
                    ballSpeedX += 0.08f
                    ballSpeedY += 0.01f
                }

                // collide with cpu
                if (
                    transform.position.x <= -350f &&
                    transform.position.y in (cpuY - 50f)..(cpuY + 50f)
                ) {
                    val dir = if (Random.nextBoolean()) 1 else -1
                    velocity.x = ballSpeedX
                    velocity.y = dir * ballSpeedY
                    ballSpeedX += 0.08f
                    ballSpeedY += 0.01f
                }
                transform.position.x += velocity.x * deltaTime
                transform.position.y += velocity.y * deltaTime

                if (transform.position.x > 400 || transform.position.x < -400) exitProcess(-1) // crash
            }
        }
    }
}