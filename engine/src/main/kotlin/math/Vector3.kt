package math

import kotlin.math.sqrt

class Vector3(var x: Float, var y: Float, var z: Float) {
    companion object {
        fun distance(a: Vector3, b: Vector3) =
            sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y) + (a.z - b.z) * (a.z - b.z))
    }

    constructor(vector: Vector3): this(vector.x, vector.y, vector.z)

    constructor(c: Float) : this(c, c, c)

    operator fun plus(other: Vector3) = Vector3(this.x + other.x, this.y + other.y, this.z + other.z)

    operator fun minus(other: Vector3) = Vector3(this.x - other.x, this.y - other.y, this.z - other.z)

    operator fun times(scalar: Float) = Vector3(this.x * scalar, this.y * scalar, this.z * scalar)

    fun dot(other: Vector3): Float = this.x * other.x + this.y * other.y + this.z * other.z

    fun magnitude(): Float = sqrt(this.x * this.x + this.y * this.y + this.z * this.z)

    fun normalize() {
        this.x /= magnitude()
        this.y /= magnitude()
        this.z /= magnitude()
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other::class == this::class) {
            val unwrapped = other as Vector3
            return unwrapped.x == this.x && unwrapped.y == this.y && unwrapped.z == this.z
        }
        return false
    }
}