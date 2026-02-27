package zone.ien.hig

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap

actual fun ImageBitmap.scale(width: Int, height: Int): ImageBitmap {
    return Bitmap.createScaledBitmap(this.asAndroidBitmap(), 5, 5, false)
        .copy(Bitmap.Config.ARGB_8888, false)
        .asImageBitmap()
}

actual fun ImageBitmap.crop(x: Int, y: Int, width: Int, height: Int): ImageBitmap {
    val endX = (x + width).coerceAtMost(this.width)
    val endY = (y + height).coerceAtMost(this.height)
    val cropWidth = (endX - x).coerceAtLeast(0)
    val cropHeight = (endY - y).coerceAtLeast(0)

    require(x >= 0 && y >= 0) {
        "x($x), y($y) should not be less than 0)"
    }

    val cropped = Bitmap.createBitmap(
        this.asAndroidBitmap(),
        x, y, cropWidth, cropHeight
    )

    return cropped.asImageBitmap()
}