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

    @Test
    fun shadowPaddingKeepsLeadingAlignmentAndBodyBounds() {
        var menuBounds: IntRect? = null
        val paddedProvider = provider.copy(shadowPadding = 16.dp, onPositionCalculated = { _, menu, _ -> menuBounds = menu })
        assertEquals(
            IntOffset(84, 84),
            paddedProvider.calculatePosition(
                IntRect(100, 100, 140, 148), IntSize(430, 932), LayoutDirection.Ltr, IntSize(292, 232),
            ),
        )
        assertEquals(IntRect(100, 100, 360, 300), menuBounds)
    }

    @Test
    fun shadowPaddingKeepsTrailingAlignmentInRtl() {
        assertEquals(
            IntOffset(54, 84),
            provider.copy(shadowPadding = 16.dp).calculatePosition(
                IntRect(280, 100, 330, 148), IntSize(430, 932), LayoutDirection.Rtl, IntSize(292, 232),
            ),
        )
    }

    @Test
    fun shadowPaddingKeepsBodyInsideSafeMargins() {
        assertEquals(
            IntOffset(122, 682),
            provider.copy(shadowPadding = 16.dp).calculatePosition(
                IntRect(360, 850, 398, 898), IntSize(430, 932), LayoutDirection.Ltr, IntSize(292, 232),
            ),
        )
    }

    @Test
    fun shadowPaddingScalesWithDensity() {
        assertEquals(
            IntOffset(168, 168),
            provider.copy(density = Density(2f), shadowPadding = 16.dp).calculatePosition(
                IntRect(200, 200, 280, 296), IntSize(860, 1864), LayoutDirection.Ltr, IntSize(584, 464),
            ),
        )
    }
}
