package zone.ien.hig

import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class CupertinoLuminanceSamplingTest {
    @Test
    fun nonSquareSamplingDoesNotReadOutsideTheThumbnail() {
        val bitmap = ImageBitmap(12, 8)
        Canvas(bitmap).drawRect(Rect(0f, 0f, 12f, 8f), Paint().apply { color = Color.White })
        assertEquals(
            1.0,
            bitmap.averageLuminance(sampleWidth = 2, sampleHeight = 10, defaultColor = Color.Black),
            0.01,
        )
    }

    @Test
    fun contrastSamplingReadsASmallLayerAndKeepsTheRequestedRegion() = runComposeUiTest {
        lateinit var sample: GraphicsLayer
        lateinit var sampleContrast: () -> Unit
        var complete = false
        var darkLuminance = -1.0
        var lightLuminance = -1.0
        setContent {
            val source = rememberGraphicsLayer()
            sample = rememberGraphicsLayer()
            val scope = rememberCoroutineScope()
            SideEffect {
                sampleContrast = {
                    scope.launch {
                        source.record(Density(1f), LayoutDirection.Ltr, IntSize(1000, 500)) {
                            drawRect(Color.White)
                            drawRect(Color.Black, Offset(500f, 0f), Size(500f, 500f))
                        }
                        darkLuminance = source.averageLuminance(
                            sampleLayer = sample, cropX = 500, cropWidth = 500, defaultColor = Color.White,
                        )
                        source.record(Density(1f), LayoutDirection.Ltr, IntSize(1000, 500)) {
                            drawRect(Color.White)
                        }
                        lightLuminance = source.averageLuminance(sampleLayer = sample, defaultColor = Color.Black)
                        complete = true
                    }
                }
            }
        }
        runOnIdle { sampleContrast() }
        waitUntil(timeoutMillis = 10_000) { complete }
        runOnIdle {
            assertEquals(IntSize(5, 5), sample.size)
            assertEquals(0.0, darkLuminance, 0.01)
            assertEquals(1.0, lightLuminance, 0.01)
        }
    }
}
