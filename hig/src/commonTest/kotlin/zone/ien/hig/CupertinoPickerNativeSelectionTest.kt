package zone.ien.hig

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCupertinoApi::class, ExperimentalTestApi::class, InternalCupertinoApi::class)
class CupertinoPickerNativeSelectionTest {
    @Test
    fun nativeSelectionUpdatesFiniteStateWithoutALazyListLayout() {
        val state = CupertinoPickerState(infinite = false, initiallySelectedItemIndex = 0)
        state.setNativePickerActive(true, itemsCount = 5)

        state.selectFromNative(2)

        assertEquals(2, state.selectedItemIndex)
        assertEquals(2, state.currentSelectedItem(itemsCount = 5))
        assertEquals(2, state.lazyListState.firstVisibleItemIndex)
    }

    @Test
    fun nativeSelectionKeepsLogicalIndexForInfiniteState() {
        val state = CupertinoPickerState(infinite = true, initiallySelectedItemIndex = 0)
        state.setNativePickerActive(true, itemsCount = 5)

        state.selectFromNative(4)

        assertEquals(4, state.selectedItemIndex)
        assertEquals(4, state.currentSelectedItem(itemsCount = 5))
        assertEquals(Int.MAX_VALUE / 2 + 4, state.lazyListState.firstVisibleItemIndex)
    }

    @Test
    fun scrollCommandsCompleteAndUpdateSelectionWhileNativePickerIsActive() = runComposeUiTest {
        val state = CupertinoPickerState(infinite = false, initiallySelectedItemIndex = 0)
        lateinit var launchCommand: (suspend () -> Unit) -> Job
        var scrollCompleted = false
        var animationCompleted = false

        setContent {
            val scope = rememberCoroutineScope()
            DisposableEffect(state) {
                state.setNativePickerActive(true, itemsCount = 5)
                onDispose { state.setNativePickerActive(false, itemsCount = 0) }
            }
            androidx.compose.runtime.SideEffect {
                launchCommand = { command -> scope.launch { command() } }
            }
        }

        runOnIdle {
            launchCommand {
                state.scrollToItem(3)
                scrollCompleted = true
            }
        }
        runOnIdle {
            assertTrue(scrollCompleted)
            assertEquals(3, state.selectedItemIndex)
            assertEquals(3, state.lazyListState.firstVisibleItemIndex)
        }

        runOnIdle {
            launchCommand {
                state.animateScrollToItem(1)
                animationCompleted = true
            }
        }
        runOnIdle {
            assertTrue(animationCompleted)
            assertEquals(1, state.selectedItemIndex)
            assertEquals(1, state.lazyListState.firstVisibleItemIndex)
        }
    }

    @Test
    fun infiniteProgrammaticSelectionCanExceedNativeItemCount() = runComposeUiTest {
        val state = CupertinoPickerState(infinite = true, initiallySelectedItemIndex = 0)
        lateinit var launchCommand: (suspend () -> Unit) -> Job
        var scrollCompleted = false

        setContent {
            val scope = rememberCoroutineScope()
            DisposableEffect(state) {
                state.setNativePickerActive(true, itemsCount = 12)
                onDispose { state.setNativePickerActive(false, itemsCount = 0) }
            }
            androidx.compose.runtime.SideEffect {
                launchCommand = { command -> scope.launch { command() } }
            }
        }

        runOnIdle {
            launchCommand {
                state.scrollToItem(15)
                scrollCompleted = true
            }
        }
        runOnIdle {
            assertTrue(scrollCompleted)
            assertEquals(15, state.selectedItemIndex)
            assertEquals(3, state.currentSelectedItem(itemsCount = 12))
            assertEquals(Int.MAX_VALUE / 2 + 15, state.lazyListState.firstVisibleItemIndex)
        }

        scrollCompleted = false
        runOnIdle {
            launchCommand {
                state.scrollToItem(-1)
                scrollCompleted = true
            }
        }
        runOnIdle {
            assertTrue(scrollCompleted)
            assertEquals(-1, state.selectedItemIndex)
            assertEquals(11, state.currentSelectedItem(itemsCount = 12))
            assertEquals(Int.MAX_VALUE / 2 - 1, state.lazyListState.firstVisibleItemIndex)
        }
    }

    @Test
    fun infiniteNativeSelectionMustStayWithinRenderedItems() {
        val state = CupertinoPickerState(infinite = true, initiallySelectedItemIndex = 0)
        state.setNativePickerActive(true, itemsCount = 3)

        assertFailsWith<IllegalArgumentException> { state.selectFromNative(3) }

        assertEquals(0, state.selectedItemIndex)
    }

    @Test
    fun nativeSelectionIsRestoredWhenTheComposeWheelIsMounted() = runComposeUiTest {
        val state = CupertinoPickerState(infinite = false, initiallySelectedItemIndex = 0)
        val wheelVisible = mutableStateOf(false)

        setContent {
            if (wheelVisible.value) {
                CupertinoWheelPicker(
                    state = state,
                    items = listOf("First", "Second", "Third"),
                    withRotation = false,
                ) { CupertinoText(it) }
            }
        }

        runOnIdle {
            state.setNativePickerActive(true, itemsCount = 3)
            state.selectFromNative(2)
            state.setNativePickerActive(false, itemsCount = 0)
            assertEquals(2, state.selectedItemIndex)
        }
        runOnIdle { wheelVisible.value = true }
        runOnIdle {
            assertEquals(2, state.selectedItemIndex)
            assertEquals(2, state.lazyListState.firstVisibleItemIndex)
            assertFalse(state.nativeSelectionIsApplied)
        }
    }

    @Test
    fun invalidNativeSelectionKeepsThePreviousIndex() {
        val state = CupertinoPickerState(infinite = false, initiallySelectedItemIndex = 0)
        state.setNativePickerActive(true, itemsCount = 3)
        state.selectFromNative(2)

        assertFailsWith<IllegalArgumentException> { state.selectFromNative(3) }

        assertEquals(2, state.selectedItemIndex)
        assertEquals(2, state.lazyListState.firstVisibleItemIndex)
    }
}
