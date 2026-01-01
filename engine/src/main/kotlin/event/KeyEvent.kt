package event

data class KeyEvent(
    val key: Int,
    val scancode: Int,
    val action: Int,
    val mod: Int
) : Event