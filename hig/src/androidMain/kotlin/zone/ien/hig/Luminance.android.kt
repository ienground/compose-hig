/*
 * Copyright (c) 2023-2024. Compose Cupertino project and open source contributors.
 * Copyright (c) 2025. Scott Lanoue.
 * Copyright (c) 2026. IENGROUND of IENLAB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zone.ien.hig

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap

/**
 * Scales an image bitmap to the specified dimensions.
 *
 * @param width The target width
 * @param height The target height
 * @return The scaled [ImageBitmap]
 */
actual fun ImageBitmap.scale(width: Int, height: Int): ImageBitmap {
    return Bitmap.createScaledBitmap(this.asAndroidBitmap(), 5, 5, false)
        .copy(Bitmap.Config.ARGB_8888, false)
        .asImageBitmap()
}

/**
 * Crops an image bitmap to the specified region.
 *
 * @param x The x-coordinate of the top-left corner of the crop region
 * @param y The y-coordinate of the top-left corner of the crop region
 * @param width The width of the crop region
 * @param height The height of the crop region
 * @return The cropped [ImageBitmap]
 */
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