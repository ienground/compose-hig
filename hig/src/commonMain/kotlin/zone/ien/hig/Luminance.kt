package zone.ien.hig

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection

expect fun ImageBitmap.scale(width: Int, height: Int): ImageBitmap
expect fun ImageBitmap.crop(x: Int, y: Int, width: Int, height: Int): ImageBitmap

/** 전체 레이어를 CPU로 읽기 전에 관심 영역을 작은 레이어로 렌더링한다. */
internal suspend fun GraphicsLayer.averageLuminance(
    sampleLayer: GraphicsLayer,
    cropX: Int = 0,
    cropY: Int = 0,
    cropWidth: Int = size.width,
    cropHeight: Int = size.height,
    sampleWidth: Int = 5,
    sampleHeight: Int = sampleWidth,
    defaultColor: Color,
): Double {
    require(sampleWidth > 0 && sampleHeight > 0)
    if (size.width <= 0 || size.height <= 0) return defaultColor.toArgb().toLuminance(defaultColor)
    val left = cropX.coerceIn(0, size.width - 1)
    val top = cropY.coerceIn(0, size.height - 1)
    val width = cropWidth.coerceIn(1, size.width - left)
    val height = cropHeight.coerceIn(1, size.height - top)
    val source = this
    sampleLayer.record(Density(1f), LayoutDirection.Ltr, IntSize(sampleWidth, sampleHeight)) {
        scale(sampleWidth.toFloat() / width, sampleHeight.toFloat() / height, Offset.Zero) {
            translate(-left.toFloat(), -top.toFloat()) {
                drawLayer(source)
            }
        }
    }
    return sampleLayer.toImageBitmap().averageLuminance(
        sampleWidth = sampleWidth, sampleHeight = sampleHeight, defaultColor = defaultColor,
    )
}

fun ImageBitmap.averageLuminance(
    cropX: Int = 0,
    cropY: Int = 0,
    cropWidth: Int = width,
    cropHeight: Int = height,
    sampleWidth: Int = 5,
    sampleHeight: Int = sampleWidth,
    defaultColor: Color
): Double = innerAverageLuminance(
    cropX, cropY,
    cropWidth.coerceIn(0..width),
    cropHeight.coerceIn(0..height),
    sampleWidth,
    sampleHeight,
    defaultColor
)

private fun ImageBitmap.innerAverageLuminance(
    cropX: Int,
    cropY: Int,
    cropWidth: Int,
    cropHeight: Int,
    sampleWidth: Int = 5,
    sampleHeight: Int = sampleWidth,
    defaultColor: Color
): Double {
    val buffer = IntArray(sampleWidth * sampleHeight)
    val cropped = crop(cropX, cropY, cropWidth, cropHeight)
    val thumbnail = cropped.scale(sampleWidth, sampleHeight)
    thumbnail.readPixels(buffer)

    return buffer.sumOf { it.toLuminance(defaultColor) } / buffer.size
}

private fun Int.toLuminance(defaultColor: Color): Double {
    val color = this.takeIf { it != Color.Transparent.toArgb() } ?: defaultColor.toArgb()

    val r = ((color shr 16) and 0xFF) / 255f
    val g = ((color shr 8) and 0xFF) / 255f
    val b = (color and 0xFF) / 255f
    return 0.2126 * r + 0.7152 * g + 0.0722 * b
}

fun Int.toHexString(includeAlpha: Boolean = true): String {
    return if (includeAlpha) {
        "#${this.toUInt().toString(16).padStart(8, '0').uppercase()}"
    } else {
        "#${(this and 0xFFFFFF).toString(16).padStart(6, '0').uppercase()}"
    }
}
