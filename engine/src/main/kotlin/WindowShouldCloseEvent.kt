import event.Event

// since data classes need to have at least one parameter, use dummy parameter
internal data class WindowShouldCloseEvent(val e: Boolean? = false) : Event