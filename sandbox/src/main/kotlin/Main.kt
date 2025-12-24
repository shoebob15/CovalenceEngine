package com.shoebob

import Application
import Layer
import config.appConfig
import config.windowConfig
import event.Event

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
    override fun onEvent(event: Event): Boolean {
        println(event)
        return false
    }

    override fun onUpdate(deltaTime: Float) {
        println("delta: $deltaTime")
    }

    override fun onRender() {
    }

    override fun destroy() {
    }
}