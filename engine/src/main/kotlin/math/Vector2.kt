package math

import kotlin.math.sqrt

class Vector2(val x: Float, val y: Float) {
    companion object {
        fun distance(a: Vector2, b: Vector2) = sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y))
    }

    constructor(): this(0f, 0f)

    constructor(vector: Vector2): this(vector.x, vector.y)

    constructor(c: Float): this(c, c)

    operator fun plus(other: Vector2) = Vector2(this.x + other.x, this.y + other.y)

    operator fun minus(other: Vector2) = Vector2(this.x - other.x, this.y - other.y)

    operator fun times(scalar: Float) = Vector2(this.x * scalar, this.y * scalar)

    fun dot(other: Vector2): Float = this.x * other.x + this.y * other.y

    fun magnitude(): Float = sqrt(this.x * this.x + this.y * this.y)

    fun normalize(): Vector2 {
        val magnitude = magnitude()
        if (magnitude > 0f) {
            return Vector2(this.x / magnitude, this.y / magnitude)
        }

        return this
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other::class == this::class) {
            val unwrapped = other as Vector2
            return unwrapped.x == this.x && unwrapped.y == this.y
        }
        return false
    }
}