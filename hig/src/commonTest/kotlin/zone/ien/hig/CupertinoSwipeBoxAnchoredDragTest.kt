package zone.ien.hig

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import zone.ien.hig.swipebox.CupertinoSwipeBoxItem
import zone.ien.hig.swipebox.SwipeBoxStates
import zone.ien.hig.swipebox.rememberCupertinoSwipeBoxState

@OptIn(ExperimentalCupertinoApi::class, ExperimentalFoundationApi::class, ExperimentalTestApi::class)
class CupertinoSwipeBoxAnchoredDragTest {
    @Test
    fun horizontalSwipeOpensPartialAndFullAnchorsAndCanClose() = runComposeUiTest {
        lateinit var swipeState: AnchoredDraggableState<SwipeBoxStates>
        val visitedStates = mutableStateListOf<SwipeBoxStates>()

        setContent {
            val state = rememberCupertinoSwipeBoxState(
                key = "swipe-box",
                positionalThreshold = { distance -> distance * 0.4f },
            )
            SideEffect { swipeState = state }
            LaunchedEffect(state) {
                snapshotFlow { state.currentValue }
                    .distinctUntilChanged()
                    .collect { visitedStates += it }
            }

            Box(Modifier.width(320.dp).height(100.dp)) {
                CupertinoSwipeBox(
                    state = state,
                    modifier = Modifier.fillMaxSize().testTag("swipe-box"),
                    actionItemBuilder = {
                        start {
                            CupertinoSwipeBoxItem(
                                color = Color.Red,
                                onClick = {},
                                label = "Delete",
                            )
                        }
                        end {
                            CupertinoSwipeBoxItem(
                                color = Color.Blue,
                                onClick = {},
                                label = "Archive",
                            )
                        }
                    },
                ) {
                    Box(Modifier.fillMaxSize().testTag("swipe-row"))
                }
            }
        }

        waitForIdle()
        onNodeWithTag("swipe-row").performTouchInput {
            down(Offset(20f, center.y))
            moveTo(Offset(95f, center.y), delayMillis = 1_000)
            up()
        }
        waitUntil(timeoutMillis = 3_000) {
            swipeState.settledValue == SwipeBoxStates.StartVisible
        }
        runOnIdle { assertEquals(SwipeBoxStates.StartVisible, swipeState.settledValue) }

        onNodeWithTag("swipe-row").performTouchInput {
            down(Offset(100f, center.y))
            moveTo(Offset(-150f, center.y), delayMillis = 1_000)
            up()
        }
        waitUntil(timeoutMillis = 3_000) {
            swipeState.settledValue == SwipeBoxStates.Resting
        }
        runOnIdle { assertEquals(SwipeBoxStates.Resting, swipeState.settledValue) }

        onNodeWithTag("swipe-row").performTouchInput {
            down(Offset(20f, center.y))
            moveTo(Offset(190f, center.y), delayMillis = 2_000)
            up()
        }
        waitUntil(timeoutMillis = 5_000) {
            visitedStates.contains(SwipeBoxStates.StartFullyExpanded) &&
                swipeState.settledValue == SwipeBoxStates.Resting
        }
        runOnIdle {
            assertEquals(SwipeBoxStates.Resting, swipeState.settledValue)
            assertTrue(visitedStates.contains(SwipeBoxStates.StartFullyExpanded))
        }
    }
}
