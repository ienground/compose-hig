package zone.ien.hig

import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCupertinoApi::class, ExperimentalTestApi::class)
class CupertinoPickerAnimatedScrollTest {
    @Test
    fun infinitePickerAnimationUsesLogicalIndicesForConsecutiveScrolls() = runComposeUiTest {
        val state = CupertinoPickerState(infinite = true, initiallySelectedItemIndex = 42)
        lateinit var launchCommand: (suspend () -> Unit) -> Job
        var animationFinished = false

        setContent {
            val scope = rememberCoroutineScope()
            CupertinoWheelPicker(
                state = state,
                items = listOf("First", "Second", "Third"),
                withRotation = false,
            ) { CupertinoText(it) }
            SideEffect {
                launchCommand = { command -> scope.launch { command() } }
            }
        }

        runOnIdle {
            launchCommand {
                state.animateScrollToItem(43)
                animationFinished = true
            }
        }
        waitUntil(timeoutMillis = 5_000) { animationFinished }
        runOnIdle { assertEquals(43, state.selectedItemIndex) }

        animationFinished = false
        runOnIdle {
            launchCommand {
                state.animateScrollToItem(44)
                animationFinished = true
            }
        }
        waitUntil(timeoutMillis = 5_000) { animationFinished }
        runOnIdle {
            assertTrue(animationFinished)
            assertEquals(44, state.selectedItemIndex)
            assertEquals(2, state.currentSelectedItem(itemsCount = 3))
        }
    }
}
