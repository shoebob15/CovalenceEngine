# ecs plan!
## entities 
entity is represented by a 32-bit integer handle.
it should never be hashed (too slow), and instead be indexed in an array.

## components
components are data. pretty simple. make them type safe, but that's about it.

## systems
systems declare the following functions:
- startFrame() (not strictly a function, but similar)
- invoke(entity: Entity) 
- endFrame()

# entity manager
the entity manager tracks the entities in a given world. manages a fixed-size array of component arrays indexed by entity handles.
provides an api to get, modify, and remove components associated with entities.