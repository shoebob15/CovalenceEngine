package gfx

import org.joml.*

// TODO: should this be internal?
internal class Camera2D(
    var position: Vector2f = Vector2f(0f),
    zoom: Float = 1f
) {

    // kotlin is such a cool language!
    var zoom = zoom
        set(value) {
            // this means that the viewport was never set in the graphics backend
            assert(lastSize.first != 0 && lastSize.second != 0)
            update(lastSize.first, lastSize.second)

            field = value
        }

    private var lastSize = Pair(0, 0)
    private val view = Matrix4f()
    private val projection = Matrix4f()
    private val viewProjection = Matrix4f()

    fun update(viewportWidth: Int, viewportHeight: Int) {
        lastSize = Pair(viewportWidth, viewportHeight)
        val hw = viewportWidth * 0.5f * zoom
        val hh = viewportHeight * 0.5f * zoom

        projection.identity().ortho(
            -hw, hw,
            -hh, hh,
            -1f, 1f
        )
    }

    fun getProjection(): Matrix4f {
        view.identity()
            .translate(-position.x, -position.y, 0f)

        return projection.mul(view, viewProjection)
    }
}