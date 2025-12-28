package resources

import java.nio.ByteBuffer


data class ImageData(
    val width: Int,
    val height: Int,
    val channels: Int,
    val pixels: ByteBuffer // rgba8
)