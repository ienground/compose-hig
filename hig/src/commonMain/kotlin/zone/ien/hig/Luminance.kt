package zone.ien.hig

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun ImageBitmap.averageLuminance(
    cropWidth: Int = width,
    cropHeight: Int = height,
    sampleWidth: Int = 5,
    sampleHeight: Int = sampleWidth
): Float = innerAverageLuminance(
    cropWidth.coerceIn(0..width),
    cropHeight.coerceIn(0..height),
    sampleWidth,
    sampleHeight
)

private suspend fun ImageBitmap.innerAverageLuminance(
    width: Int,
    height: Int,
    sampleWidth: Int = 5,
    sampleHeight: Int = sampleWidth
): Float =
    withContext(Dispatchers.Default.limitedParallelism(1)) {
        try {
            val stepX = width / sampleWidth.toFloat()
            val stepY = height / sampleHeight.toFloat()

            val luminance = (0..sampleHeight).flatMap { gy ->
                (0..sampleWidth).map { gx ->
                    val x = (stepX * gx + stepX * 0.5f).toInt().coerceIn(0, width - 1)
                    val y = (stepY * gy + stepY * 0.5f).toInt().coerceIn(0, height - 1)

                    val pixel = IntArray(1)
                    readPixels(pixel, x, y, 1, 1)
                    pixel[0].toLuminance()
                }
            }.average().toFloat()

            luminance
        } catch (e: Exception) {
            0.5f
        }
    }

private fun Int.toLuminance(): Float {
    val r = ((this shr 16) and 0xFF) / 255f
    val g = ((this shr 8) and 0xFF) / 255f
    val b = (this and 0xFF) / 255f
    return 0.2126f * r + 0.7152f * g + 0.0722f * b
}