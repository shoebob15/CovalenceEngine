## general asset loading architecture

**asset entry class**
- templated w/ generic type T (data)
- path (string)
- size (bytes, long)
- refcount (int)
- last used (long)

**asset manager object**:

max size should be ~1gb

assets - map string:asset entry (string is path)

asset loading ref-counting would be based on manual cleanup ( : Destructible)

when ref-count is zero, asset becomes eligible for cache eviction in order to stay below max size for asset loader
and not take up too much memory. (idk what to do if there is no zero-ref objects in cache...)


smth like this:
val lines = this::class.java.getResourceAsStream("file.txt")?.bufferedReader()?.readLines()