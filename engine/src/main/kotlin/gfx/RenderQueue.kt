package gfx


class RenderQueue {
    private val queue = ArrayDeque<DrawCommand>(1024)

    internal fun submit(cmd: DrawCommand) {
        queue.add(cmd)
    }

    internal fun flush(): List<DrawCommand> {
        val out = queue.toList()
        queue.clear()
        return out
    }
}
