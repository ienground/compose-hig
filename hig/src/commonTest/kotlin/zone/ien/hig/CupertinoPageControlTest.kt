package zone.ien.hig

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.LayoutDirection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCupertinoApi::class, ExperimentalTestApi::class)
class CupertinoPageControlTest {
    @Test
    fun selectedPageIsExposedAndCanChange() = runComposeUiTest {
        var currentPage by mutableIntStateOf(4)

        setContent {
            CupertinoPageControl(
                pageCount = 9,
                currentPage = currentPage,
                onPageSelected = { currentPage = it },
            )
        }

        onNodeWithContentDescription("Page 5 of 9").assertIsSelected()
        onNodeWithContentDescription("Page 9 of 9").performClick()

        assertEquals(8, currentPage)
        onNodeWithContentDescription("Page 9 of 9").assertIsSelected()
    }

    @Test
    fun pageOrderFollowsRightToLeftLayoutDirection() = runComposeUiTest {
        setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                CupertinoPageControl(
                    pageCount = 3,
                    currentPage = 1,
                    onPageSelected = {},
                )
            }
        }

        val firstPageCenter =
            onNodeWithContentDescription("Page 1 of 3")
                .fetchSemanticsNode()
                .boundsInRoot
                .center
                .x
        val lastPageCenter =
            onNodeWithContentDescription("Page 3 of 3")
                .fetchSemanticsNode()
                .boundsInRoot
                .center
                .x

        assertTrue(firstPageCenter > lastPageCenter)
    }
}
