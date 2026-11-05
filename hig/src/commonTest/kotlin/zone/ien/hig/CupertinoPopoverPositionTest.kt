package zone.ien.hig

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import kotlin.test.Test
import kotlin.test.assertEquals

class CupertinoPopoverPositionTest {
    @Test
    fun placesBelowAnchorAndPointsToItsCenter() {
        var placement: CupertinoPopoverPlacement? = null
        val provider = CupertinoPopoverPositionProvider(CupertinoPopoverEdge.Top, Density(1f)) { placement = it }
        assertEquals(
            IntOffset(-20, 40),
            provider.calculatePosition(IntRect(120, 60, 160, 100), IntSize(400, 600), LayoutDirection.Ltr, IntSize(320, 333)),
        )
        assertEquals(100f, placement?.arrowOffset)
        assertEquals(CupertinoPopoverEdge.Top, placement?.edge)
    }

    @Test
    fun flipsAboveWhenThereIsNoRoomBelow() {
        var placement: CupertinoPopoverPlacement? = null
        val provider = CupertinoPopoverPositionProvider(CupertinoPopoverEdge.Top, Density(1f)) { placement = it }
        assertEquals(
            IntOffset(-20, 227),
            provider.calculatePosition(IntRect(120, 500, 160, 540), IntSize(400, 600), LayoutDirection.Ltr, IntSize(320, 333)),
        )
        assertEquals(CupertinoPopoverEdge.Bottom, placement?.edge)
    }

    @Test
    fun clampsAtWindowEdgeWhileKeepingAnchorOffset() {
        var placement: CupertinoPopoverPlacement? = null
        val provider = CupertinoPopoverPositionProvider(CupertinoPopoverEdge.Top, Density(1f)) { placement = it }
        assertEquals(
            IntOffset(124, 40),
            provider.calculatePosition(IntRect(340, 60, 380, 100), IntSize(400, 600), LayoutDirection.Ltr, IntSize(320, 333)),
        )
        assertEquals(176f, placement?.arrowOffset)
    }

    @Test
    fun startEdgeUsesPhysicalRightSideInRtl() {
        val provider = CupertinoPopoverPositionProvider(CupertinoPopoverEdge.Start, Density(1f))
        assertEquals(
            IntOffset(27, 60),
            provider.calculatePosition(IntRect(300, 200, 340, 240), IntSize(600, 600), LayoutDirection.Rtl, IntSize(333, 320)),
        )
    }

    @Test
    fun shadowPaddingDoesNotFlipSurfaceThatFitsBelowAnchor() {
        var placement: CupertinoPopoverPlacement? = null
        val provider = CupertinoPopoverPositionProvider(CupertinoPopoverEdge.Top, Density(1f)) { placement = it }
        assertEquals(
            IntOffset(-20, 240),
            provider.calculatePosition(IntRect(120, 260, 160, 300), IntSize(400, 600), LayoutDirection.Ltr, IntSize(320, 333)),
        )
        assertEquals(CupertinoPopoverEdge.Top, placement?.edge)
        assertEquals(100f, placement?.arrowOffset)
    }

    @Test
    fun shadowPaddingScalesWithDensity() {
        var placement: CupertinoPopoverPlacement? = null
        val provider = CupertinoPopoverPositionProvider(CupertinoPopoverEdge.Top, Density(2f)) { placement = it }
        assertEquals(
            IntOffset(-40, 80),
            provider.calculatePosition(IntRect(240, 120, 320, 200), IntSize(800, 1200), LayoutDirection.Ltr, IntSize(640, 666)),
        )
        assertEquals(200f, placement?.arrowOffset)
    }

    @Test
    fun smallWindowDoesNotProduceInvalidClampRange() {
        val provider = CupertinoPopoverPositionProvider(CupertinoPopoverEdge.Top, Density(3f))
        assertEquals(
            IntOffset.Zero,
            provider.calculatePosition(IntRect(10, 10, 20, 20), IntSize(60, 60), LayoutDirection.Ltr, IntSize(60, 60)),
        )
    }
}
