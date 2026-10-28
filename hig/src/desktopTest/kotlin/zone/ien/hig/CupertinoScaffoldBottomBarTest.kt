package zone.ien.hig

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertPositionInRootIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import zone.ien.hig.theme.CupertinoTheme

@OptIn(ExperimentalTestApi::class, ExperimentalCupertinoApi::class)
class CupertinoScaffoldBottomBarTest {
    @Test
    fun nestedScaffoldReservesRightInsetOnlyOnce() = assertNestedRightInset(LayoutDirection.Ltr)

    @Test
    fun nestedScaffoldReservesPhysicalRightInsetOnlyOnceInRtl() = assertNestedRightInset(LayoutDirection.Rtl)

    private fun assertNestedRightInset(direction: LayoutDirection) = runComposeUiTest {
        var nestedTopBarOwnsRightArea = false
        setContent {
            CupertinoTheme {
                CompositionLocalProvider(LocalLayoutDirection provides direction) {
                    CupertinoScaffold(
                        modifier = Modifier.size(240.dp, 320.dp),
                        contentWindowInsets = WindowInsets(right = 72.dp),
                    ) {
                        CupertinoScaffold(
                            modifier = Modifier.fillMaxSize(),
                            topBar = {
                                nestedTopBarOwnsRightArea = LocalCupertinoRightSystemArea.current
                                Box(Modifier.fillMaxWidth().height(32.dp).testTag("nested-topbar"))
                            },
                            contentWindowInsets = WindowInsets(right = 72.dp),
                        ) { innerPadding ->
                            Box(Modifier.fillMaxSize().padding(innerPadding)) {
                                Box(Modifier.fillMaxSize().testTag("nested-body"))
                            }
                        }
                    }
                }
            }
        }
        runOnIdle { assertTrue(nestedTopBarOwnsRightArea) }
        onNodeWithTag("nested-topbar").assertWidthIsEqualTo(240.dp)
        onNodeWithTag("nested-body").assertWidthIsEqualTo(168.dp)
    }

    @Test
    fun customBottomBarUsesRightRailAndCameraCenter() = assertRightRail(LayoutDirection.Ltr)

    @Test
    fun customBottomBarStaysOnPhysicalRightInRtl() = assertRightRail(LayoutDirection.Rtl)

    private fun assertRightRail(direction: LayoutDirection) = runComposeUiTest {
        var parentData: Any? = null
        setContent {
            CompositionLocalProvider(
                LocalLayoutDirection provides direction,
                LocalCupertinoVerticalBarPlacement provides CupertinoVerticalBarPlacement(
                    width = 72.dp, top = 80.dp, bottom = 24.dp, centerFromRight = 60.dp,
                ),
            ) {
                Layout(
                    modifier = Modifier.size(240.dp, 320.dp),
                    content = {
                        Box(Modifier.cupertinoScaffoldBottomBar(), contentAlignment = Alignment.TopCenter) {
                            Box(Modifier.size(48.dp, 60.dp).testTag("action"))
                        }
                    },
                ) { measurables, constraints ->
                    val measurable = measurables.single()
                    parentData = measurable.layoutId
                    val bar = measurable.measure(Constraints(maxWidth = constraints.maxWidth, maxHeight = constraints.maxHeight))
                    layout(constraints.maxWidth, constraints.maxHeight) {
                        bar.place(constraints.maxWidth - bar.width, constraints.maxHeight - bar.height)
                    }
                }
            }
        }
        runOnIdle { assertEquals(CupertinoVerticalBottomBar, parentData) }
        onNodeWithTag("action")
            .assertPositionInRootIsEqualTo(156.dp, 236.dp)
            .assertWidthIsEqualTo(48.dp)
            .assertHeightIsEqualTo(60.dp)
    }

    @Test
    fun regularBottomBarKeepsItsOriginalLayout() = runComposeUiTest {
        var parentData: Any? = null
        setContent {
            Layout(content = { Box(Modifier.cupertinoScaffoldBottomBar().size(120.dp, 52.dp).testTag("bar")) }) {
                    measurables, constraints ->
                parentData = measurables.single().layoutId
                val bar = measurables.single().measure(constraints.copy(minWidth = 0, minHeight = 0))
                layout(bar.width, bar.height) { bar.place(0, 0) }
            }
        }
        runOnIdle { assertNull(parentData) }
        onNodeWithTag("bar").assertWidthIsEqualTo(120.dp).assertHeightIsEqualTo(52.dp)
    }
}
