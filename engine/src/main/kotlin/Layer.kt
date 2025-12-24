import event.Event

interface Layer : Destructible {
    // method to recieve event from the engine
    // return true if the event was consumed and should not be propagated
    fun onEvent(event: Event): Boolean
    fun onUpdate(deltaTime: Float)
    fun onRender()
}