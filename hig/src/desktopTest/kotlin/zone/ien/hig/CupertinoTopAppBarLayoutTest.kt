package zone.ien.hig

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import zone.ien.hig.theme.CupertinoTheme

@OptIn(ExperimentalTestApi::class, ExperimentalCupertinoApi::class)
class CupertinoTopAppBarLayoutTest {
    @Test
    fun wideActionsDoNotWrapShortTitleOrExpandBar() = assertTitleLayout(actionsWidth = 154.dp)

    @Test
    fun wideActionsDoNotOverlapTitleInRtl() = assertTitleLayout(
        actionsWidth = 154.dp,
        direction = LayoutDirection.Rtl,
    )

    @Test
    fun wideNavigationDoesNotWrapOrOverlapTitle() = assertTitleLayout(
        navigationWidth = 154.dp,
        actionsWidth = 48.dp,
    )

    @Test
    fun balancedControlsKeepTitleAtBarCenter() = assertTitleLayout(
        navigationWidth = 48.dp,
        actionsWidth = 48.dp,
        expectCentered = true,
    )

    @Test
    fun shortActionsKeepTitleAtBarCenter() = assertTitleLayout(
        actionsWidth = 48.dp,
        expectCentered = true,
    )

    private fun assertTitleLayout(
        navigationWidth: Dp = 0.dp,
        actionsWidth: Dp,
        direction: LayoutDirection = LayoutDirection.Ltr,
        expectCentered: Boolean = false,
    ) = runComposeUiTest {
        var titleLines = 0
        setContent {
            CupertinoTheme {
                CompositionLocalProvider(LocalLayoutDirection provides direction) {
                    Box {
                        CupertinoTopAppBar(
                            modifier = Modifier.width(393.dp).testTag("bar"),
                            windowInsets = WindowInsets(0),
                            isBackgroundAdaptive = false,
                            title = {
                                CupertinoText(
                                    text = "로컬 갤러리",
                                    modifier = Modifier.testTag("title"),
                                    onTextLayout = { titleLines = it.lineCount },
                                )
                            },
                            navigationIcon = {
                                if (navigationWidth > 0.dp) {
                                    Box(Modifier.size(navigationWidth, 48.dp).testTag("navigation"))
                                }
                            },
                            actions = {
                                Box(Modifier.size(actionsWidth, 48.dp).testTag("actions"))
                            },
                        )
                    }
                }
            }
        }

        onNodeWithTag("bar").assertHeightIsEqualTo(58.dp)
        runOnIdle { assertEquals(1, titleLines, "짧은 제목은 넓은 액션 옆에서도 한 줄로 표시해야 합니다.") }
        val bar = onNodeWithTag("bar").fetchSemanticsNode().boundsInRoot
        val title = onNodeWithTag("title").fetchSemanticsNode().boundsInRoot
        val actions = onNodeWithTag("actions").fetchSemanticsNode().boundsInRoot
        assertTrue(title.left >= bar.left && title.right <= bar.right)
        if (direction == LayoutDirection.Ltr) {
            assertTrue(title.right <= actions.left, "제목이 오른쪽 액션과 겹치면 안 됩니다.")
        } else {
            assertTrue(title.left >= actions.right, "RTL 제목이 왼쪽 액션과 겹치면 안 됩니다.")
        }
        if (navigationWidth > 0.dp) {
            val navigation = onNodeWithTag("navigation").fetchSemanticsNode().boundsInRoot
            assertTrue(title.left >= navigation.right, "제목이 내비게이션과 겹치면 안 됩니다.")
        }
        if (expectCentered) {
            assertEquals(bar.center.x, title.center.x, absoluteTolerance = 0.5f)
        }
    }
}
