package ecs

import math.Vector2

data class TransformComponent(val position: Vector2, val scale: Vector2, val rotation: Float) : Component