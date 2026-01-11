package com.shoebob

import Application
import config.appConfig
import config.windowConfig

fun main() {
    val sandbox = Application(appConfig {
        title = "Sandbox App"

        windowConfig {
            width = 800
            height = 600
        }
    })

    sandbox.pushLayer(GameLayer())

    sandbox.run()
}