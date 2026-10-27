package zone.ien.hig

import androidx.compose.foundation.background
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCupertinoApi::class, ExperimentalTestApi::class)
class CupertinoButtonIndicationTest {
    @Test
    fun filledButtonDrawsItsConfiguredIndicationColorWhilePressed() = runComposeUiTest {
        val interactionSource = MutableInteractionSource()

        setContent {
            CupertinoButtonSample(
            ) {
                CupertinoButton(
                    onClick = {},
                    modifier = Modifier.testTag("filled-button"),
                    interactionSource = interactionSource,
                    colors = CupertinoButtonDefaults.filledButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White,
                        indicationColor = Color.Magenta,
                    ),
                ) {
                    CupertinoText("Filled")
                }
            }
        }

        interactionSource.tryEmit(PressInteraction.Press(Offset.Zero))
        mainClock.advanceTimeBy(120)

        assertTrue(onNodeWithTag("filled-button").captureToImage().hasMagentaPixels())
    }

    @Test
    fun plainButtonDrawsItsConfiguredIndicationColorWhilePressed() = runComposeUiTest {
        val interactionSource = MutableInteractionSource()

        setContent {
            CupertinoButtonSample(
            ) {
                CupertinoButton(
                    onClick = {},
                    modifier = Modifier.testTag("plain-button"),
                    interactionSource = interactionSource,
                    colors = CupertinoButtonDefaults.plainButtonColors(
                        contentColor = Color.White,
                        indicationColor = Color.Magenta,
                    ),
                ) {
                    CupertinoText("Plain")
                }
            }
        }

        interactionSource.tryEmit(PressInteraction.Press(Offset.Zero))
        mainClock.advanceTimeBy(120)

        assertTrue(onNodeWithTag("plain-button").captureToImage().hasMagentaPixels())
    }

    @Test
    fun iconButtonDrawsItsConfiguredIndicationColorWhilePressed() = runComposeUiTest {
        val interactionSource = MutableInteractionSource()

        setContent {
            CupertinoButtonSample(
            ) {
                CupertinoIconButton(
                    onClick = {},
                    modifier = Modifier.testTag("icon-button"),
                    interactionSource = interactionSource,
                    colors = CupertinoButtonDefaults.plainButtonColors(
                        contentColor = Color.White,
                        indicationColor = Color.Magenta,
                    ),
                ) {
                    CupertinoText("Icon")
                }
            }
        }

        interactionSource.tryEmit(PressInteraction.Press(Offset.Zero))
        mainClock.advanceTimeBy(120)

        assertTrue(onNodeWithTag("icon-button").captureToImage().hasMagentaPixels())
    }
}

@Composable
@OptIn(ExperimentalCupertinoApi::class)
private fun CupertinoButtonSample(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalIndication provides rememberCupertinoIndication(Color.Gray)) {
        Box(
            Modifier
                .background(Color.Black)
                .padding(24.dp),
        ) {
            content()
        }
    }
}

private fun ImageBitmap.hasMagentaPixels(): Boolean {
    val pixels = toPixelMap()
    for (x in 0 until width) {
        for (y in 0 until height) {
            val pixel = pixels[x, y]
            if (pixel.red > 0.01f && pixel.blue > 0.01f && pixel.green < 0.01f) return true
        }
    }
    return false
}
