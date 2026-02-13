package zone.ien.hig

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun ImageBitmap.averageLuminance(sampleWidth: Int = 5, sampleHeight: Int = 5): Float =
    withContext(Dispatchers.Default.limitedParallelism(1)) {
        try {
            val buffer = IntArray(sampleWidth * sampleHeight)
            readPixels(
                buffer = buffer,
                startX = 0,
                startY = 0,
                width = sampleWidth.coerceAtMost(width),
                height = sampleHeight.coerceAtMost(height)
            )

            buffer.map { color ->
                val r = ((color shr 16) and 0xFF) / 255f
                val g = ((color shr 8) and 0xFF) / 255f
                val b = (color and 0xFF) / 255f
                0.2126f * r + 0.7152f * g + 0.0722f * b
            }.average().toFloat()

        } catch (e: Exception) {
            0.5f
        }
    }