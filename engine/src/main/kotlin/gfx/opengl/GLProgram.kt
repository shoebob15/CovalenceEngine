package gfx.opengl

import Destructible
import resources.ResourceManager
import resources.ResourceType
import org.lwjgl.opengl.GL20.*
import org.slf4j.LoggerFactory

internal class GLProgram(
    val vertexPath: String,
    val fragmentPath: String,
    val resources: ResourceManager
) : Destructible {

    private val logger = LoggerFactory.getLogger(javaClass)

    private var program = -1

    init {
        logger.debug("loading and compiling vertex shader $vertexPath")
        val vertexShader = glCreateShader(GL_VERTEX_SHADER)
        val vertexShaderSource = resources.load<List<String>>(vertexPath, ResourceType.TEXT)
        glShaderSource(vertexShader, vertexShaderSource.data.joinToString("\n"))
        glCompileShader(vertexShader)

        var compileStatus = glGetShaderi(vertexShader, GL_COMPILE_STATUS)
        if (compileStatus == GL_FALSE) {
            error("error while compiling vertex shader: ${glGetShaderInfoLog(vertexShader)}")
        }

        logger.debug("loading and compiling fragment shader $fragmentPath")
        val fragmentShader = glCreateShader(GL_FRAGMENT_SHADER)
        val fragmentShaderSource = resources.load<List<String>>(fragmentPath, ResourceType.TEXT)
        glShaderSource(fragmentShader, fragmentShaderSource.data.joinToString("\n"))
        glCompileShader(fragmentShader)

        compileStatus = glGetShaderi(fragmentShader, GL_COMPILE_STATUS)
        if (compileStatus == GL_FALSE) {
            error("error while compiling fragment shader: ${glGetShaderInfoLog(fragmentShader)}")
        }

        logger.debug("creating opengl program")
        program = glCreateProgram()
        glAttachShader(program, vertexShader)
        glAttachShader(program, fragmentShader)
        glLinkProgram(program)

        // clean up compiled shaders
        glDeleteShader(vertexShader)
        glDeleteShader(fragmentShader)
    }

    fun use() {
        glUseProgram(program)
    }

    fun getHandle(): Int {
        return program
    }

    // TODO: uniform funs

    override fun destroy() {
        glDeleteProgram(program)
    }
}