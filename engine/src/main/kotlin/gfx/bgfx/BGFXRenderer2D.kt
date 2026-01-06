package gfx.bgfx

import Destructible
import org.joml.*
import org.lwjgl.bgfx.BGFX.*
import org.lwjgl.bgfx.*
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer

internal class BGFXRenderer2D(
    private val program: Short
) : Destructible {

    private val layout = createVertexLayout()
    private val view = Matrix4f()
    private val viewBuf = MemoryUtil.memAllocFloat(16) // 4x4 mat
    private val projBuf = MemoryUtil.memAllocFloat(16) // 4x4 mat

    private val samplerUniform = bgfx_create_uniform(
        "s_tex",
        BGFX_UNIFORM_TYPE_SAMPLER,
        1
    )

    fun begin(viewId: Int, width: Int, height: Int) {
        val viewMat = Matrix4f().identity()

        view.identity()
        view.ortho(0f, width.toFloat(), height.toFloat(), 0f, -1f, 100f)

        bgfx_set_view_transform(
            viewId,
            viewMat.get(viewBuf),
            view.get(projBuf)
        )

        bgfx_set_view_rect(viewId, 0, 0, width, height)
    }

    fun drawQuad(
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        texture: BGFXTexture,
        color: Int
    ) {
        if (bgfx_get_avail_transient_vertex_buffer(4, layout) < 4 ||
            bgfx_get_avail_transient_index_buffer(6, false) < 6
        ) {
            return
        }
        val encoder = bgfx_encoder_begin(false)

        val vb = BGFXTransientVertexBuffer.create()
        bgfx_alloc_transient_vertex_buffer(vb, 4, layout)

        val ib = BGFXTransientIndexBuffer.create()
        bgfx_alloc_transient_index_buffer(ib, 6, false)

        val v = vb.data()
        val i = ib.data()

        // create quad vertices
        putVertex(v, x, y, 0f, 0f, color)
        putVertex(v, x + w, y, 1f, 0f, color)
        putVertex(v, x + w, y + h, 1f, 1f, color)
        putVertex(v, x, y + h, 0f, 1f, color)
        v.flip()

        i.putShort(0).putShort(1).putShort(2)
        i.putShort(0).putShort(2).putShort(3)
        i.flip()

        bgfx_encoder_set_transient_vertex_buffer(encoder, 0, vb, 0, 4)
        bgfx_encoder_set_transient_index_buffer(encoder, ib, 0, 6)
        bgfx_encoder_set_texture(
            encoder, 0, samplerUniform, texture.handle.id.toShort(), BGFX_SAMPLER_NONE
        )

        bgfx_encoder_set_state(
            encoder,
            BGFX_STATE_WRITE_RGB or
                    BGFX_STATE_WRITE_A or
                    BGFX_STATE_BLEND_ALPHA or
                    BGFX_STATE_DEPTH_TEST_ALWAYS,
            0
        )
        bgfx_encoder_submit(encoder, 0, program, 0, 0)
        bgfx_encoder_end(encoder)
    }

    fun end() { }


    // TODO: replace params with PosColorTexVertex
    private fun putVertex(
        buf: ByteBuffer,
        x: Float,
        y: Float,
        u: Float,
        v: Float,
        color: Int
    ) {
        buf.putFloat(x)
        buf.putFloat(y)
        buf.putFloat(u)
        buf.putFloat(v)
        buf.putInt(color)
    }

    private fun createVertexLayout(): BGFXVertexLayout {
        val layout = BGFXVertexLayout.calloc()
        bgfx_vertex_layout_begin(layout, bgfx_get_renderer_type())

        bgfx_vertex_layout_add(
            layout,
            BGFX_ATTRIB_POSITION, // x, y
            2,
            BGFX_ATTRIB_TYPE_FLOAT,
            false,
            false
        )

        bgfx_vertex_layout_add(
            layout,
            BGFX_ATTRIB_TEXCOORD0, // u, v
            2,
            BGFX_ATTRIB_TYPE_FLOAT,
            false,
            false
        )

        bgfx_vertex_layout_add(
            layout,
            BGFX_ATTRIB_COLOR0, // r, g, b, a
            4,
            BGFX_ATTRIB_TYPE_UINT8,
            true,
            false
        )

        bgfx_vertex_layout_end(layout)
        return layout
    }

    override fun destroy() {
        layout.free()
    }
}