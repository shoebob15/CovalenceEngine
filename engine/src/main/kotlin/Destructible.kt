// interface for classes that occupy memory/resources and need to be released to prevent memor leaking
// aimed towards classes that use jni/lwjgl apis
interface Destructible {
    // cleans up resources, memory, etc.
    fun destroy()
}
