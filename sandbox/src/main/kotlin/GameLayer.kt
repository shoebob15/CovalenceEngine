package com.shoebob

import AppContext
import Layer
import ecs.Entity
import ecs.EntityManager
import ecs.FramePhase
import ecs.RenderComponent
import ecs.TransformComponent
import ecs.VelocityComponent
import ecs.World
import event.Event
import math.Vector2
import kotlin.random.Random

class GameLayer : Layer {

    private lateinit var context: AppContext
    private lateinit var world: World

    val list = mutableListOf<Entity>()
    override fun onAttach(context: AppContext) {
        world = World(EntityManager(), context.renderer)

        world.entityManager.apply {
            val player = createEntity()
            addComponent(player,
                TransformComponent(Vector2(-350f, 200f),
                    Vector2(10f, 100f),
                    0f
                ))
            addComponent(player, RenderComponent("/pixel.png"))
            addComponent(player, ComputerComponent())

            val computer = createEntity()
            addComponent(computer, TransformComponent(Vector2(350f, 200f),
                Vector2(10f, 100f),
                0f))
            addComponent(computer, RenderComponent("/pixel.png"))
            addComponent(computer, PlayerComponent())

            val ball = createEntity()
            addComponent(ball, TransformComponent(Vector2(0f, Random.nextInt(-200, 200).toFloat()),
                Vector2(10f, 10f),
                0f))
            addComponent(ball, RenderComponent("/pixel.png"))
            addComponent(ball, BallComponent())
            addComponent(ball, VelocityComponent(0.2f, 0.03f))
        }


        world.addSystem(FramePhase.UPDATE, PlayerControlSystem())
        world.addSystem(FramePhase.UPDATE, ComputerSystem())
        world.addSystem(FramePhase.UPDATE, BallSystem())
        world.addSystem(FramePhase.RENDER, RenderSystem())

        this.context = context
    }

    override fun onDetach() { }


    override fun onEvent(event: Event): Boolean {
        return false
    }

    override fun onUpdate(deltaTime: Float) {
        world.update(deltaTime)
    }

    override fun onRender() {
        world.render(context.deltaTime)
    }
}