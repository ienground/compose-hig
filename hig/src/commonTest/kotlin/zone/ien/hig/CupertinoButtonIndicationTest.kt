package zone.ien.hig

import androidx.compose.foundation.background
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.onStart
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCupertinoApi::class, ExperimentalTestApi::class)
class CupertinoButtonIndicationTest {
    @OptIn(InternalComposeApi::class)
    @Test
    fun defaultIconButtonsDoNotKeepIdleInteractionCoroutines() = runComposeUiTest {
        var visible by mutableStateOf(false)
        lateinit var effects: Job
        setContent {
            effects = currentComposer.applyCoroutineContext[Job]!!
            if (visible) Column {
                repeat(12) { CupertinoIconButton(onClick = {}) { CupertinoText(text = "Icon") } }
            }
        }
        fun activeChildren(job: Job): Int = job.children.sumOf { (if (it.isActive) 1 else 0) + activeChildren(it) }
        var initial = 0
        runOnIdle { initial = activeChildren(effects); visible = true }
        runOnIdle {
            assertEquals(initial, activeChildren(effects), "기본 아이콘 버튼은 유휴 상태에서 감시 코루틴을 유지하면 안 됩니다")
        }
    }

    @Test
    fun transparentPlainButtonDoesNotCollectInvisibleIndication() = runComposeUiTest {
        var subscriptions = 0
        val delegate = MutableInteractionSource()
        val source = object : MutableInteractionSource by delegate {
            override val interactions = delegate.interactions.onStart { subscriptions++ }
        }
        setContent {
            CupertinoIconButton(onClick = {}, interactionSource = source) {
                CupertinoText(text = "Icon")
            }
        }
        runOnIdle { assertEquals(1, subscriptions, "투명한 표시 효과는 상호작용을 별도로 구독하면 안 됩니다") }
    }

    @Test
    fun plainButtonFadesContentOnPressAndRestoresItOnRelease() = runComposeUiTest {
        mainClock.autoAdvance = false
        val sources = listOf<MutableInteractionSource>(MutableInteractionSource(), CupertinoButtonInteractionSource())
        setContent {
            CupertinoButtonSample {
                Column {
                    sources.forEachIndexed { index, source ->
                        CupertinoIconButton(
                            onClick = {},
                            modifier = Modifier.testTag("plain-fade-$index"),
                            interactionSource = source,
                            colors = CupertinoButtonDefaults.plainButtonColors(contentColor = Color.White),
                        ) { Box(Modifier.size(24.dp).background(Color.White)) }
                    }
                }
            }
        }
        fun capture() = sources.indices.map { onNodeWithTag("plain-fade-$it").captureToImage() }
        mainClock.advanceTimeBy(100)
        val initial = capture()
        val press = PressInteraction.Press(Offset.Zero)
        runOnIdle { sources.forEach { it.tryEmit(press) } }
        mainClock.advanceTimeBy(300)
        val pressed = capture()
        runOnIdle { sources.forEach { it.tryEmit(PressInteraction.Release(press)) } }
        mainClock.advanceTimeBy(1000)
        val released = capture()
        fun ImageBitmap.centerRed() = toPixelMap()[width / 2, height / 2].red
        sources.indices.forEach { index ->
            assertTrue(pressed[index].centerRed() < initial[index].centerRed() * 0.6f)
            assertTrue(released[index].centerRed() > 0.95f)
        }
    }

    @Test
    fun ownedInteractionSourceKeepsPressStateAndBroadcastEventsInOrder() = runComposeUiTest {
        val source = CupertinoButtonInteractionSource()
        val events = mutableListOf<Interaction>()
        setContent {
            LaunchedEffect(source) { source.interactions.collect { events += it } }
        }
        val first = PressInteraction.Press(Offset.Zero)
        val second = PressInteraction.Press(Offset(1f, 1f))
        val release = PressInteraction.Release(first)
        val cancel = PressInteraction.Cancel(second)
        runOnIdle { assertTrue(source.tryEmit(first)); assertTrue(source.isPressed.value) }
        source.emit(second)
        runOnIdle { assertTrue(source.tryEmit(release)); assertTrue(source.isPressed.value) }
        source.emit(cancel)
        runOnIdle {
            assertFalse(source.isPressed.value)
            assertEquals(listOf<Interaction>(first, second, release, cancel), events)
        }
    }

    @Test
    fun labeledButtonSizesMatchDesignMetrics() = runComposeUiTest {
        setContent {
            Column {
                CupertinoButton(
                    onClick = {},
                    modifier = Modifier.testTag("small-button"),
                    size = CupertinoButtonSize.Small,
                ) {
                    CupertinoText(text = "Small")
                }
                CupertinoButton(
                    onClick = {},
                    modifier = Modifier.testTag("medium-button"),
                    size = CupertinoButtonSize.Regular,
                ) {
                    CupertinoText(text = "Medium")
                }
                CupertinoButton(
                    onClick = {},
                    modifier = Modifier.testTag("large-button"),
                    size = CupertinoButtonSize.Large,
                ) {
                    CupertinoText(text = "Large")
                }
            }
        }

        onNodeWithTag("small-button").assertHeightIsEqualTo(28.dp)
        onNodeWithTag("medium-button").assertHeightIsEqualTo(34.dp)
        onNodeWithTag("large-button").assertHeightIsEqualTo(50.dp)
    }

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
