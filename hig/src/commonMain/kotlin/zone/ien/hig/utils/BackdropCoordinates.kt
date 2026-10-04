package zone.ien.hig.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Matrix

internal fun backdropTransform(origin: Offset, xAxis: Offset, yAxis: Offset): Matrix = Matrix().apply {
    this[0, 0] = xAxis.x
    this[0, 1] = xAxis.y
    this[1, 0] = yAxis.x
    this[1, 1] = yAxis.y
    this[3, 0] = origin.x
    this[3, 1] = origin.y
}
