package gfx

// doesn't do anything gfx-related
class HeadlessBackend : GraphicsBackend {
    override fun startFrame() { }

    override fun draw(cmd: DrawCommand) { }

    override fun endFrame() { }

    override fun shouldClose(): Boolean {
        return false
    }

    override fun destroy() { }
}