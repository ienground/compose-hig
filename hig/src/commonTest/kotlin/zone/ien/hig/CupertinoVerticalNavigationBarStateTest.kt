package zone.ien.hig

import androidx.compose.runtime.BroadcastFrameClock
import androidx.compose.runtime.snapshots.Snapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CupertinoVerticalNavigationBarStateTest {
    @Test
    fun shortPressShowsLensWithoutExpandingLabels() = withAnimationClock {
        val state = CupertinoVerticalNavigationBarState(scope, 0, 4)
        state.press(0)
        advance(10)
        assertTrue(state.pressProgress > 0.9f)
        assertTrue(state.dragAnimation.scaleY > 1.2f)
        assertEquals(0f, state.expansionProgress)
        advance(19)
        assertEquals(0f, state.expansionProgress)
    }

    @Test
    fun heldPressExpandsAfterHalfASecond() = withAnimationClock {
        val state = CupertinoVerticalNavigationBarState(scope, 0, 4)
        state.press(0)
        advance(29)
        assertEquals(0f, state.expansionProgress)
        advance(12)
        assertTrue(state.expansionProgress > 0.9f)
        advance(60)
        assertEquals(1f, state.expansionProgress, 0.001f)
    }

    @Test
    fun shortPressReleaseCancelsPendingExpansion() = withAnimationClock {
        val state = CupertinoVerticalNavigationBarState(scope, 0, 4)
        state.press(0)
        advance(20)
        state.release(0)
        repeat(80) {
            advance(1)
            assertEquals(0f, state.expansionProgress)
        }
    }

    @Test
    fun newPressRestartsExpansionDelay() = withAnimationClock {
        val state = CupertinoVerticalNavigationBarState(scope, 0, 4)
        state.press(0)
        advance(20)
        state.release(0)
        state.press(1)
        advance(20)
        assertEquals(0f, state.expansionProgress)
        advance(22)
        assertTrue(state.expansionProgress > 0.9f)
    }

    @Test
    fun pressingWhileCollapsingDoesNotLeaveLabelsExpanded() = withAnimationClock {
        val state = CupertinoVerticalNavigationBarState(scope, 0, 4)
        state.press(0)
        advance(90)
        state.release(0)
        advance(2)
        state.press(1)
        advance(29)
        assertEquals(0f, state.expansionProgress, 0.001f)
        advance(13)
        assertTrue(state.expansionProgress > 0.9f)
    }

    @Test
    fun draggingTracksPointerWithoutWaitingForAnimationFrames() = withAnimationClock {
        val state = CupertinoVerticalNavigationBarState(scope, 0, 4)
        state.press(0)
        state.dragTo(1.4f).join()
        assertEquals(1.4f, state.value, 0.001f)
        state.dragTo(0.25f).join()
        assertEquals(0.25f, state.value, 0.001f)
    }

    @Test
    fun releasingDragDoesNotJumpToPreviousAnimatedPosition() = withAnimationClock {
        val state = CupertinoVerticalNavigationBarState(scope, 0, 4)
        state.press(0)
        advance(30)
        state.dragTo(1.4f)
        state.release(1)
        assertEquals(1.4f, state.value, 0.001f)
        advance(1)
        assertTrue(state.value > 1.3f)
        advance(120)
        assertEquals(1f, state.value, 0.001f)
    }

    @Test
    fun selectionCommittedAfterPointerUpWinsOverPendingReturn() = withAnimationClock {
        val state = CupertinoVerticalNavigationBarState(scope, 0, 2)
        state.press(1)
        advance(60)
        state.release(0)
        state.select(1)
        advance(120)
        assertEquals(1f, state.value, 0.001f)
    }

    @Test
    fun newPressInterruptsPendingRelease() = withAnimationClock {
        val state = CupertinoVerticalNavigationBarState(scope, 0, 4)
        state.press(3)
        advance(8)
        state.release(3)
        advance(2)
        state.press(1)
        advance(120)

        assertTrue(state.isPressed)
        assertEquals(1f, state.value, 0.001f)
        assertEquals(1f, state.pressProgress, 0.001f)
        assertEquals(1f, state.expansionProgress, 0.001f)

        state.release(1)
        advance(120)
        assertFalse(state.isPressed)
        assertEquals(0f, state.pressProgress, 0.001f)
        assertEquals(0f, state.expansionProgress, 0.001f)
    }

    @Test
    fun externalSelectionDoesNotMoveIndicatorDuringScrubbing() = withAnimationClock {
        val state = CupertinoVerticalNavigationBarState(scope, 0, 4)
        state.press(0)
        state.dragTo(1.4f)
        state.select(0)
        advance(120)
        assertEquals(1.4f, state.value, 0.001f)

        state.release(0)
        advance(120)
        assertEquals(0f, state.value, 0.001f)
        assertEquals(0f, state.pressProgress, 0.001f)
    }

    @Test
    fun oneTabAndOutOfBoundsDragsSettleWithoutStuckPress() = withAnimationClock {
        val state = CupertinoVerticalNavigationBarState(scope, 0, 1)
        state.press(0)
        state.dragTo(8f)
        state.release(0)
        advance(120)
        assertEquals(0f, state.value)
        assertEquals(0f, state.pressProgress, 0.001f)
        assertTrue(state.dragAnimation.velocity.isFinite())
    }

    @Test
    fun rapidSelectionChangesSettleOnLatestTab() = withAnimationClock {
        val state = CupertinoVerticalNavigationBarState(scope, 0, 4)
        state.select(3)
        advance(4)
        state.select(1)
        advance(120)
        assertEquals(1f, state.value, 0.001f)
        assertEquals(0f, state.pressProgress, 0.001f)
    }

    private fun withAnimationClock(block: suspend AnimationClock.() -> Unit) = runTest {
        val clock = BroadcastFrameClock()
        val scope = CoroutineScope(coroutineContext + clock)
        try {
            AnimationClock(scope, clock).block()
        } finally {
            scope.coroutineContext.cancelChildren()
        }
    }

    private class AnimationClock(val scope: CoroutineScope, val clock: BroadcastFrameClock) {
        private var time = 0L

        suspend fun advance(frames: Int) {
            repeat(frames) {
                yield()
                time += 16_666_667L
                clock.sendFrame(time)
                yield()
                Snapshot.sendApplyNotifications()
            }
        }
    }
}
