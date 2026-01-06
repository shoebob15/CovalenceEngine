package com.shoebob

import AppContext
import Application
import Layer
import config.appConfig
import config.windowConfig
import event.Event
import math.Vector2
import resources.ImageData
import resources.ResourceType
import java.nio.ByteBuffer

fun main() {
    val sandbox = Application(appConfig {
        title = "Sandbox App"

        windowConfig {
            width = 800
            height = 600
        }
    })

    sandbox.pushLayer(TestLayer())

    sandbox.run()
}

class TestLayer : Layer {
    override fun onEvent(event: Event, context: AppContext): Boolean {
        return false
    }

    override fun onUpdate(deltaTime: Float, context: AppContext) {

    }

    override fun onRender(context: AppContext) {
        context.renderer.draw("/test2.png", Vector2(5f), size = Vector2(5f))
    }

    override fun destroy() {
    }
}