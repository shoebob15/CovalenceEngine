package ecs

// entity is just an identifier
// TODO: there shouldn't be a performance difference between value classes and typealises, but test performance
@JvmInline
value class Entity(val handle: Int)