package event

import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE

// TODO: create an actual input class w/ callbacks, this is temporary
object Input {
    private val keys = Array(348) { false }

    internal fun consumeEvent(event: Event) {
        println(event)
        // TODO: replace GLFW constants with native constants
        if (event is KeyEvent) {
            if (event.action == GLFW_PRESS) keys[event.key] = true
            if (event.action == GLFW_RELEASE) keys[event.key] = false
        }
    }

    fun isKeyPressed(key: Int): Boolean {
        return keys[key]
    }
}