package event

data class WindowResizeEvent(
    val width: Int,
    val height: Int
) : Event