package zone.ien.hig

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class CupertinoTopBlurTest {
    @Test
    fun tallTopBarIsSolidUntilItsBottomWithoutStretchingTheFade() {
        val fadeHeight = 54.dp
        val topPadding = 152.dp
        val height = resolveCupertinoTopBlurHeight(fadeHeight, topPadding)
        assertEquals(topPadding, height - fadeHeight * (1f - CupertinoTopBlurSolidFraction))
    }

    @Test
    fun smallTopPaddingKeepsTheOriginalBlurHeight() {
        assertEquals(54.dp, resolveCupertinoTopBlurHeight(54.dp, 10.dp))
    }

    @Test
    fun disabledBlurRemainsDisabledWithTallTopPadding() {
        assertEquals(0.dp, resolveCupertinoTopBlurHeight(0.dp, 152.dp))
    }
}
