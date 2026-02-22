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
    require(x >= 0 && y >= 0 && x + width <= this.width && y + height <= this.height) {
        "Crop bounds out of image: ($x, $y $width x $height)"
    }

    val cropped = Bitmap.createBitmap(
        this.asAndroidBitmap(),
        x, y, width, height
    )

    return cropped.asImageBitmap()
}