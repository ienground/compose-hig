package zone.ien.hig

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertPositionInRootIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals
import zone.ien.hig.theme.LocalColorScheme
import zone.ien.hig.theme.lightColorScheme

@OptIn(ExperimentalTestApi::class, ExperimentalCupertinoApi::class, InternalCupertinoApi::class)
class CupertinoNavigationBarDesignTest {
    @Test
    fun verticallyCenteredTitleDoesNotQueryAlignmentLines() = runComposeUiTest {
        var alignmentLineMerges = 0
        val titleAlignmentLine = HorizontalAlignmentLine { first, second ->
            alignmentLineMerges++
            maxOf(first, second)
        }
        setContent {
            CompositionLocalProvider(LocalColorScheme provides lightColorScheme()) {
                CupertinoTopAppBar(
                    modifier = Modifier.width(402.dp),
                    title = {
                        Box(Modifier.testTag("centered-title")) {
                            repeat(2) {
                                Layout(content = {}) { _, _ ->
                                    layout(
                                        width = 80.dp.roundToPx(),
                                        height = 22.dp.roundToPx(),
                                        alignmentLines = mapOf(titleAlignmentLine to 0),
                                    ) {}
                                }
                            }
                        }
                    },
                    windowInsets = WindowInsets(0),
                    isBackgroundAdaptive = false,
                )
            }
        }
        onNodeWithTag("centered-title").assertPositionInRootIsEqualTo(161.dp, 11.dp)
        runOnIdle {
            assertEquals(0, alignmentLineMerges, "세로 중앙 정렬에는 제목 기준선이 필요하지 않습니다.")
        }
    }

    @Test
    fun leadingTitleWithoutButtonsKeepsSixteenPointMargin() = runComposeUiTest {
        setContent {
            CompositionLocalProvider(LocalColorScheme provides lightColorScheme()) {
                CupertinoTopAppBar(
                    modifier = Modifier.width(402.dp),
                    title = { Box(Modifier.size(80.dp, 22.dp).testTag("leading-title")) },
                    windowInsets = WindowInsets(0),
                    isCenterAligned = false,
                    isBackgroundAdaptive = false,
                )
            }
        }
        onNodeWithTag("leading-title").assertPositionInRootIsEqualTo(16.dp, 11.dp)
    }

    @Test
    fun toolbarUsesReferenceMarginsAndSubtitleTypography() = runComposeUiTest {
        var titleStyle = TextStyle.Default
        var subtitleStyle = TextStyle.Default
        setContent {
            CompositionLocalProvider(LocalColorScheme provides lightColorScheme()) {
                CupertinoTopAppBar(
                    modifier = Modifier.width(402.dp),
                    windowInsets = WindowInsets(0),
                    isBackgroundAdaptive = false,
                    navigationIcon = { Box(Modifier.size(44.dp).testTag("back")) },
                    actions = { Box(Modifier.size(44.dp).testTag("action")) },
                    title = {
                        titleStyle = LocalTextStyle.current
                        CupertinoText(text = "제목")
                    },
                    subtitle = {
                        subtitleStyle = LocalTextStyle.current
                        CupertinoText(text = "설명")
                    },
                )
            }
        }
        onNodeWithTag("back").assertPositionInRootIsEqualTo(16.dp, 0.dp)
        onNodeWithTag("action").assertPositionInRootIsEqualTo(342.dp, 0.dp)
        runOnIdle {
            assertEquals(15.sp, titleStyle.fontSize)
            assertEquals(FontWeight.SemiBold, titleStyle.fontWeight)
            assertEquals(12.sp, subtitleStyle.fontSize)
            assertEquals(16.sp, subtitleStyle.lineHeight)
            assertEquals(FontWeight.Medium, subtitleStyle.fontWeight)
        }
    }

    @Test
    fun horizontalTabPreservesIconSizeWithinReferenceSlot() = runComposeUiTest {
        var labelStyle = TextStyle.Default
        setContent {
            CompositionLocalProvider(LocalColorScheme provides lightColorScheme()) {
                Row {
                    CupertinoNavigationBarItem(
                        onClick = {},
                        icon = { Box(Modifier.size(20.dp).testTag("tab-icon")) },
                        label = {
                            labelStyle = LocalTextStyle.current
                            CupertinoText(text = "탐색")
                        },
                    )
                }
            }
        }
        onNodeWithTag("tab-icon", useUnmergedTree = true)
            .assertWidthIsEqualTo(20.dp)
            .assertHeightIsEqualTo(20.dp)
        runOnIdle {
            assertEquals(10.sp, labelStyle.fontSize)
            assertEquals(12.sp, labelStyle.lineHeight)
            assertEquals(FontWeight.SemiBold, labelStyle.fontWeight)
        }
    }

    @Test
    fun verticalTabKeepsItsCompactIconSize() = runComposeUiTest {
        setContent {
            CompositionLocalProvider(LocalColorScheme provides lightColorScheme()) {
                CompositionLocalProvider(
                    LocalCupertinoVerticalBarPlacement provides CupertinoVerticalBarPlacement(
                        width = 72.dp,
                        top = 0.dp,
                        bottom = 0.dp,
                        centerFromRight = 36.dp,
                    ),
                ) {
                    Row {
                        CupertinoNavigationBarItem(
                            onClick = {},
                            icon = { Box(Modifier.fillMaxSize().testTag("vertical-icon")) },
                        )
                    }
                }
            }
        }
        onNodeWithTag("vertical-icon", useUnmergedTree = true)
            .assertWidthIsEqualTo(20.dp)
            .assertHeightIsEqualTo(20.dp)
    }
}
