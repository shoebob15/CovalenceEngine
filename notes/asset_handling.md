## general asset loading architecture

**asset entry class**
- templated w/ generic type T (data)
- path (string)
- size (bytes, long)
- refcount (int)
- last used (long)

**asset manager object**:

max size should be ~1gb

assets - map string:asset (string is path)

asset loading ref-counting would be based on manual cleanup ( : Destructible)

when ref-count is zero, asset becomes eligible for cache eviction
in order to stay below max size for asset loader
