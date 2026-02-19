package zone.ien.hig

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun ImageBitmap.averageLuminance(
    cropWidth: Int = width,
    cropHeight: Int = height,
    sampleWidth: Int = 5,
    sampleHeight: Int = sampleWidth,
    defaultColor: Color,
): Float = innerAverageLuminance(
    cropWidth.coerceIn(0..width),
    cropHeight.coerceIn(0..height),
    sampleWidth,
    sampleHeight,
    defaultColor
)

private fun Int.toColorCode(): String {
    val a = (this shr 24) and 0xFF
    val r = (this shr 16) and 0xFF
    val g = (this shr 8) and 0xFF
    val b = this and 0xFF
    return buildString {
        append('#')
        append(a.toHexTwoDigits())
        append(r.toHexTwoDigits())
        append(g.toHexTwoDigits())
        append(b.toHexTwoDigits())
    }
}

private fun Int.toHexTwoDigits(): String = toUInt().toString(16).padStart(2, '0').uppercase()

private suspend fun ImageBitmap.innerAverageLuminance(
    width: Int,
    height: Int,
    sampleWidth: Int = 5,
    sampleHeight: Int = sampleWidth,
    defaultColor: Color
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

                    (pixel[0].takeIf { it != Color.Transparent.toArgb() } ?: defaultColor.toArgb()).toLuminance()
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