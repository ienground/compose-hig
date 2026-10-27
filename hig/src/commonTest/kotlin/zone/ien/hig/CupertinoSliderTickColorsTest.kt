package zone.ien.hig

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class CupertinoSliderTickColorsTest {
    @Test
    fun sliderDrawsActiveAndInactiveTicksWithTheirConfiguredColors() = runComposeUiTest {
        setContent {
            SliderTickSamples()
        }

        val enabledTicks = onNodeWithTag("enabled-slider-ticks").captureToImage()
        assertTrue(enabledTicks.countPixels(Color.Red) > 0)
        assertTrue(enabledTicks.countPixels(Color.Yellow) > 0)

        val disabledTicks = onNodeWithTag("disabled-slider-ticks").captureToImage()
        assertTrue(disabledTicks.countPixels(Color.Magenta) > 0)
        assertTrue(disabledTicks.countPixels(Color.Cyan) > 0)
    }

    @Test
    fun rangeSliderDrawsTicksForItsSelectedAndUnselectedSegments() = runComposeUiTest {
        setContent {
            RangeSliderTickSample()
        }

        val ticks = onNodeWithTag("range-slider-ticks").captureToImage()
        assertTrue(ticks.countPixels(Color.Red) > 0)
        assertTrue(ticks.countPixels(Color.Yellow) > 0)
    }
}

@Composable
private fun SliderTickSamples() {
    Column(Modifier.width(300.dp)) {
        CupertinoSlider(
            value = 0.5f,
            onValueChange = {},
            steps = 3,
            modifier = Modifier.testTag("enabled-slider-ticks"),
            backdrop = rememberLayerBackdrop(),
            colors = CupertinoLiquidSliderDefaults.colors(
                activeTickColor = Color.Red,
                inactiveTickColor = Color.Yellow,
                disabledActiveTickColor = Color.Magenta,
                disabledInactiveTickColor = Color.Cyan,
            ),
        )
        CupertinoSlider(
            value = 0.5f,
            onValueChange = {},
            enabled = false,
            steps = 3,
            modifier = Modifier.testTag("disabled-slider-ticks"),
            backdrop = rememberLayerBackdrop(),
            colors = CupertinoLiquidSliderDefaults.colors(
                activeTickColor = Color.Red,
                inactiveTickColor = Color.Yellow,
                disabledActiveTickColor = Color.Magenta,
                disabledInactiveTickColor = Color.Cyan,
            ),
        )
    }
}

@Composable
private fun RangeSliderTickSample() {
    Column(Modifier.width(300.dp)) {
        CupertinoRangeSlider(
            value = 0.25f..0.75f,
            onValueChange = {},
            steps = 3,
            modifier = Modifier.testTag("range-slider-ticks"),
            backdrop = rememberLayerBackdrop(),
            colors = CupertinoLiquidSliderDefaults.colors(
                activeTickColor = Color.Red,
                inactiveTickColor = Color.Yellow,
            ),
        )
    }
}

private fun ImageBitmap.countPixels(color: Color): Int {
    val pixels = toPixelMap()
    var count = 0
    for (x in 0 until width) {
        for (y in 0 until height) {
            if (pixels[x, y] == color) count++
        }
    }
    return count
}
