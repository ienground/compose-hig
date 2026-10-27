package zone.ien.hig

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class CupertinoSliderRecompositionTest {
    @Test
    fun sliderTapUsesUpdatedRangeStepsAndCallback() = runComposeUiTest {
        var value by mutableFloatStateOf(0.5f)
        var valueRange by mutableStateOf(0f..1f)
        var steps by mutableIntStateOf(0)
        val oldValues = mutableListOf<Float>()
        val newValues = mutableListOf<Float>()
        var onValueChange by mutableStateOf<(Float) -> Unit>({
            oldValues += it
            value = it
        })

        setContent {
            Box(Modifier.width(300.dp)) {
                CupertinoSlider(
                    value = value,
                    onValueChange = onValueChange,
                    valueRange = valueRange,
                    steps = steps,
                    modifier = Modifier.testTag("slider"),
                    backdrop = rememberLayerBackdrop(),
                )
            }
        }

        runOnIdle {
            value = 125f
            valueRange = 100f..200f
            steps = 1
            onValueChange = {
                newValues += it
                value = it
            }
        }
        onNodeWithTag("slider").performTouchInput { click(center) }

        runOnIdle {
            assertTrue(oldValues.isEmpty())
            assertEquals(listOf(150f), newValues)
            assertEquals(150f, value)
        }
    }

    @Test
    fun rangeSliderTapUsesUpdatedRangeStepsAndCallback() = runComposeUiTest {
        var value by mutableStateOf(0.2f..0.8f)
        var valueRange by mutableStateOf(0f..1f)
        var steps by mutableIntStateOf(0)
        val oldValues = mutableListOf<ClosedFloatingPointRange<Float>>()
        val newValues = mutableListOf<ClosedFloatingPointRange<Float>>()
        var onValueChange by mutableStateOf<(ClosedFloatingPointRange<Float>) -> Unit>({
            oldValues += it
            value = it
        })

        setContent {
            Box(Modifier.width(300.dp)) {
                CupertinoRangeSlider(
                    value = value,
                    onValueChange = onValueChange,
                    valueRange = valueRange,
                    steps = steps,
                    modifier = Modifier.testTag("range-slider"),
                    backdrop = rememberLayerBackdrop(),
                )
            }
        }

        runOnIdle {
            value = 20f..80f
            valueRange = 0f..100f
            steps = 3
            onValueChange = {
                newValues += it
                value = it
            }
        }
        onNodeWithTag("range-slider").performTouchInput { click(center) }

        runOnIdle {
            assertTrue(oldValues.isEmpty())
            assertEquals(listOf(20f..50f), newValues)
            assertEquals(20f..50f, value)
        }
    }

    @Test
    fun sliderDragUsesCallbackProvidedAfterRecomposition() = runComposeUiTest {
        var value by mutableFloatStateOf(0.5f)
        val oldValues = mutableListOf<Float>()
        val newValues = mutableListOf<Float>()
        var onValueChange by mutableStateOf<(Float) -> Unit>({
            oldValues += it
            value = it
        })

        setContent {
            Box(Modifier.width(300.dp)) {
                CupertinoSlider(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.testTag("slider"),
                    backdrop = rememberLayerBackdrop(),
                    thumb = { animation, _ ->
                        Box(
                            Modifier
                                .size(24.dp)
                                .then(animation.modifier)
                                .testTag("slider-thumb"),
                        )
                    },
                )
            }
        }

        runOnIdle {
            onValueChange = {
                newValues += it
                value = it
            }
        }
        onNodeWithTag("slider-thumb").performTouchInput {
            down(center)
            moveBy(Offset(60f, 0f))
            up()
        }

        runOnIdle {
            assertTrue(oldValues.isEmpty())
            assertTrue(newValues.isNotEmpty())
        }
    }

    @Test
    fun rangeSliderDragUsesCallbackProvidedAfterRecomposition() = runComposeUiTest {
        var value by mutableStateOf(0.25f..0.75f)
        val oldValues = mutableListOf<ClosedFloatingPointRange<Float>>()
        val newValues = mutableListOf<ClosedFloatingPointRange<Float>>()
        var onValueChange by mutableStateOf<(ClosedFloatingPointRange<Float>) -> Unit>({
            oldValues += it
            value = it
        })

        setContent {
            Box(Modifier.width(300.dp)) {
                CupertinoRangeSlider(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.testTag("range-slider"),
                    backdrop = rememberLayerBackdrop(),
                )
            }
        }

        runOnIdle {
            onValueChange = {
                newValues += it
                value = it
            }
        }
        onNodeWithTag("range-slider").performTouchInput {
            val startThumb = Offset(width * 0.25f, center.y)
            down(startThumb)
            moveBy(Offset(40f, 0f))
            up()
        }

        runOnIdle {
            assertTrue(oldValues.isEmpty())
            assertTrue(newValues.isNotEmpty())
        }
    }
}
