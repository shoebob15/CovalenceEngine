package gfx.bgfx

import org.lwjgl.bgfx.*
import org.lwjgl.system.MemoryStack

object BGFXCallbacks {
    fun createCallbacks(stack: MemoryStack): BGFXCallbackInterface {
        return BGFXCallbackInterface.malloc(stack)
            .vtbl(
                BGFXCallbackVtbl.malloc(stack)
                    .fatal { _this: Long, _filePath: Long, _line: Short, _code: Int, _str: Long ->
                        error("fatal bgfx error in $_filePath at :$_line: $_code")
                    }
            )
    }
}
