package com.shoebob

import AppContext
import Layer
import ecs.Entity
import ecs.EntityManager
import ecs.FramePhase
import ecs.RenderComponent
import ecs.TransformComponent
import ecs.World
import event.Event
import event.Input
import math.Vector2

class GameLayer : Layer {

    private lateinit var context: AppContext
    private lateinit var world: World

    val list = mutableListOf<Entity>()
    override fun onAttach(context: AppContext) {
        world = World(EntityManager(), context.renderer)

        val player = world.entityManager.createEntity()
        world.entityManager.addComponent(player, TransformComponent(Vector2(5f), Vector2(5f), 0f))
        world.entityManager.addComponent(player, RenderComponent("/test.png"))
        world.entityManager.addComponent(player, PlayerComponent())

        world.addSystem(FramePhase.UPDATE, PlayerControlSystem())
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