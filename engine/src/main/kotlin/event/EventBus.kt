package event

class EventBus {
    private val queue = ArrayDeque<Event>()

    fun post(event: Event) {
        queue.addLast(event)
    }

    fun flush(dispatch: (Event) -> Unit) {
        while (queue.isNotEmpty()) dispatch(queue.removeFirst())
    }
}