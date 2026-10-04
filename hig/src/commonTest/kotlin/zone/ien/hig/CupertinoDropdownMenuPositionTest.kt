package zone.ien.hig

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class CupertinoDropdownMenuPositionTest {
    private val provider = DropdownMenuPositionProvider(
        contentOffset = DpOffset.Zero,
        density = Density(1f),
        safePadding = 32.dp,
        verticalMargin = 0.dp,
    )

    @Test
    fun alignsWithLeadingAnchorWithoutPaddingOffset() {
        assertEquals(
            IntOffset(100, 100),
            provider.calculatePosition(
                IntRect(100, 100, 140, 148),
                IntSize(430, 932),
                LayoutDirection.Ltr,
                IntSize(260, 200),
            ),
        )
    }

    @Test
    fun alignsWithTrailingAnchorInRtl() {
        assertEquals(
            IntOffset(70, 100),
            provider.calculatePosition(
                IntRect(280, 100, 330, 148),
                IntSize(430, 932),
                LayoutDirection.Rtl,
                IntSize(260, 200),
            ),
        )
    }

    @Test
    fun keepsBottomRightMenuInsideSafeMargins() {
        assertEquals(
            IntOffset(138, 698),
            provider.calculatePosition(
                IntRect(360, 850, 398, 898),
                IntSize(430, 932),
                LayoutDirection.Ltr,
                IntSize(260, 200),
            ),
        )
    }
}
