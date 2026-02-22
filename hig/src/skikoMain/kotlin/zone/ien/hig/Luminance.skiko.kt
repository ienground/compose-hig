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
    val bitmap = Bitmap()
    bitmap.allocN32Pixels(width, height)
    val image = Image.makeFromBitmap(this.asSkiaBitmap())
    image.scalePixels(bitmap.peekPixels()!!, SamplingMode.LINEAR, false)
    return bitmap.asComposeImageBitmap()
}

actual fun ImageBitmap.crop(x: Int, y: Int, width: Int, height: Int): ImageBitmap {
    require(x >= 0 && y >= 0 && x + width <= this.width && y + height <= this.height) {
        "Crop bounds out of image bounds: ($x, $y, $width, $height) vs (${this.width}, ${this.height})"
    }

    val srcBitmap = this.asSkiaBitmap()
    val dstBitmap = Bitmap().apply {
        allocN32Pixels(width, height)
    }

    Canvas(dstBitmap).use { canvas ->
        val srcRect = Rect.makeXYWH(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())
        canvas.drawImageRect(Image.makeFromBitmap(srcBitmap), srcRect, null)
    }

    return dstBitmap.asComposeImageBitmap()
}