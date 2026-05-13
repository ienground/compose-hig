package zone.ien.hig

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Image
import org.jetbrains.skia.Rect
import org.jetbrains.skia.SamplingMode
import org.jetbrains.skia.impl.use

actual fun ImageBitmap.scale(width: Int, height: Int): ImageBitmap {
    val srcBitmap = this.asSkiaBitmap()
    val dstBitmap = Bitmap()
    dstBitmap.allocN32Pixels(width, height)

    val image = Image.makeFromBitmap(srcBitmap)
    image.scalePixels(dstBitmap.peekPixels()!!, SamplingMode.LINEAR, false)

    val result = dstBitmap.asComposeImageBitmap()
    return result
}

actual fun ImageBitmap.crop(x: Int, y: Int, width: Int, height: Int): ImageBitmap {
    val endX = (x + width).coerceAtMost(this.width)
    val endY = (y + height).coerceAtMost(this.height)
    val cropWidth = (endX - x).coerceAtLeast(0)
    val cropHeight = (endY - y).coerceAtLeast(0)

    require(x >= 0 && y >= 0) { "x($x), y($y) should not be less than 0" }

    val srcBitmap = this.asSkiaBitmap()
    val dstBitmap = Bitmap().apply {
        allocN32Pixels(cropWidth, cropHeight)
    }

    Canvas(dstBitmap).use { canvas ->
        val srcRect = Rect.makeXYWH(x.toFloat(), y.toFloat(), cropWidth.toFloat(), cropHeight.toFloat())
        val dstRect = Rect.makeXYWH(0f, 0f, cropWidth.toFloat(), cropHeight.toFloat())  // ← added
        canvas.drawImageRect(Image.makeFromBitmap(srcBitmap), srcRect, dstRect)  // ← dst specified
    }

    return dstBitmap.asComposeImageBitmap()
}