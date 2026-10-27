package zone.ien.hig

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import zone.ien.hig.utils.LocalCupertinoBackdrop

@OptIn(ExperimentalTestApi::class)
class CupertinoHazeBackdropTest {
    @Test
    fun hazeUsesBackdropWithinAreasAndPreservesTintAlpha() = runComposeUiTest {
        setContent {
            HazeBackdropSample()
        }

        val image = onNodeWithTag("haze-backdrop").captureToImage()
        val pixels = image.toPixelMap()
        val inside = pixels[image.width / 4, image.height / 2]
        val outside = pixels[image.width * 3 / 4, image.height / 2]

        assertEquals(1f, inside.red)
        assertTrue(abs(inside.green - 0.25f) < 0.03f)
        assertTrue(abs(inside.blue - 0.25f) < 0.03f)
        assertEquals(0f, outside.alpha)
    }
}

@Composable
private fun HazeBackdropSample() {
    val backdrop = rememberLayerBackdrop { drawRect(Color.Red) }
    val area =
        with(androidx.compose.ui.platform.LocalDensity.current) {
            Rect(left = 0f, top = 0f, right = 50.dp.toPx(), bottom = 100.dp.toPx())
        }

    Box(Modifier.requiredSize(100.dp)) {
        Box(Modifier.matchParentSize().layerBackdrop(backdrop))
        CompositionLocalProvider(LocalCupertinoBackdrop provides backdrop) {
            Box(
                Modifier
                    .matchParentSize()
                    .haze(
                        area,
                        backgroundColor = Color.Black,
                        tint = Color.White.copy(alpha = 0.25f),
                        blurRadius = 0.dp,
                    ).testTag("haze-backdrop"),
            )
        }
    }
}
