import event.Event

interface Layer : Destructible {
    // method to recieve event from the engine
    // return true if the event was consumed and should not be propagated
    fun onEvent(event: Event, context: AppContext): Boolean

    // TODO: don't pass full app context to onUpdate/Render, only pass objects related to function
    fun onUpdate(deltaTime: Float, context: AppContext)

    fun onRender(context: AppContext)
}