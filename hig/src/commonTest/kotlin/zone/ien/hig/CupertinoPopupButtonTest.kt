package zone.ien.hig

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.test.assertEquals
import zone.ien.hig.utils.rememberDefaultBackdrop
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import zone.ien.hig.theme.LocalColorScheme
import zone.ien.hig.theme.lightColorScheme

@OptIn(ExperimentalTestApi::class, ExperimentalCupertinoApi::class, InternalCupertinoApi::class)
class CupertinoPopupButtonTest {
    @Test
    fun popupButtonSamplesContentAtItsScreenPositionAndUpdatesWhileOpen() =
        assertPopupSamplesContent(usePopupButton = true)

    @Test
    fun dropdownSamplesContentAtItsScreenPositionAndUpdatesWhileOpen() =
        assertPopupSamplesContent(usePopupButton = false)

    private fun assertPopupSamplesContent(usePopupButton: Boolean) = runComposeUiTest {
        mainClock.autoAdvance = false
        var anchorY by mutableStateOf(80.dp)
        var lowerColor by mutableStateOf(Color.Blue)
        setContent {
            CompositionLocalProvider(
                LocalColorScheme provides lightColorScheme(),
                LocalCupertinoRightSystemArea provides false,
            ) {
                CupertinoScaffold(modifier = Modifier.size(360.dp, 600.dp)) {
                    Box(Modifier.fillMaxSize().drawBehind {
                        drawRect(Color.Red)
                        drawRect(lowerColor, topLeft = Offset(0f, 120.dp.toPx()),
                            size = Size(size.width, size.height - 120.dp.toPx()))
                        drawRect(Color.Green, topLeft = Offset(0f, 320.dp.toPx()),
                            size = Size(size.width, size.height - 320.dp.toPx()))
                    }) {
                        Box(Modifier.offset(x = 60.dp, y = anchorY)) {
                            if (usePopupButton) {
                                CupertinoPopupButton(
                                    expanded = true,
                                    onExpandedChange = {},
                                    title = { CupertinoText(text = "Open") },
                                ) {
                                    MenuItem(Modifier.width(210.dp).height(140.dp).testTag("glass-menu")) {}
                                }
                            } else {
                                Box(Modifier.size(100.dp, 44.dp))
                                CupertinoDropdownMenu(expanded = true, onDismissRequest = {}) {
                                    MenuItem(Modifier.width(210.dp).height(140.dp).testTag("glass-menu")) {}
                                }
                            }
                        }
                    }
                }
            }
        }
        mainClock.advanceTimeBy(1000)
        var image = onNodeWithTag("glass-menu").captureToImage()
        var center = image.toPixelMap()[image.width / 2, image.height / 2]
        assertTrue(center.blue > center.red + 0.08f, "빨간 앵커 아래 메뉴 위치의 파란 배경을 읽어야 합니다: $center")

        runOnIdle { lowerColor = Color.Yellow }
        mainClock.advanceTimeBy(100)
        image = onNodeWithTag("glass-menu").captureToImage()
        center = image.toPixelMap()[image.width / 2, image.height / 2]
        assertTrue(center.red > center.blue + 0.08f, "열린 메뉴에 변경된 배경이 반영되어야 합니다: $center")

        runOnIdle { anchorY = 380.dp }
        mainClock.advanceTimeBy(100)
        image = onNodeWithTag("glass-menu").captureToImage()
        center = image.toPixelMap()[image.width / 2, image.height / 2]
        assertTrue(center.green > center.red + 0.08f, "이동한 메뉴는 새 위치의 배경을 읽어야 합니다: $center")
    }

    @Test
    fun pickerCheckmarkDoesNotOverlapOrWrapLabelsInLtr() =
        assertPickerCheckmark(LayoutDirection.Ltr, withIcons = false)

    @Test
    fun pickerCheckmarkDoesNotOverlapOrWrapLabelsInRtl() =
        assertPickerCheckmark(LayoutDirection.Rtl, withIcons = false)

    @Test
    fun pickerCheckmarkDoesNotOverlapOrWrapLabelsWithIconsInLtr() =
        assertPickerCheckmark(LayoutDirection.Ltr, withIcons = true)

    @Test
    fun pickerCheckmarkDoesNotOverlapOrWrapLabelsWithIconsInRtl() =
        assertPickerCheckmark(LayoutDirection.Rtl, withIcons = true)

    private fun assertPickerCheckmark(direction: LayoutDirection, withIcons: Boolean) = runComposeUiTest {
        mainClock.autoAdvance = false
        var selectedLines = 0
        var unselectedLines = 0
        setContent {
            CompositionLocalProvider(LocalColorScheme provides lightColorScheme()) {
                CompositionLocalProvider(LocalLayoutDirection provides direction) {
                    CupertinoDropdownMenu(
                        expanded = true,
                        onDismissRequest = {},
                        backdrop = rememberDefaultBackdrop(),
                        modifier = Modifier.testTag("menu"),
                        width = if (withIcons) 320.dp else CupertinoDropdownMenuDefaults.DefaultWidth,
                    ) {
                        MenuPickerAction(
                            isSelected = true,
                            onClick = {},
                            modifier = Modifier.testTag("selected-row"),
                            leadingIcon = if (withIcons) { {
                                Box(Modifier.testTag("leading-icon")) { CupertinoDropdownMenuDefaults.PickerLeadingIcon() }
                            } } else null,
                            trailingIcon = if (withIcons) { {
                                Box(Modifier.testTag("trailing-icon")) { CupertinoDropdownMenuDefaults.PickerLeadingIcon() }
                            } } else null,
                            selectionIcon = {
                                Box(Modifier.testTag("checkmark")) { CupertinoDropdownMenuDefaults.PickerLeadingIcon() }
                            },
                        ) {
                            CupertinoText(text = "All components", modifier = Modifier.testTag("selected-label"),
                                onTextLayout = { selectedLines = it.lineCount })
                        }
                        MenuPickerAction(isSelected = false, onClick = {}) {
                            CupertinoText(text = "Presentation", modifier = Modifier.testTag("unselected-label"),
                                onTextLayout = { unselectedLines = it.lineCount })
                        }
                        MenuAction(onClick = {}) {
                            CupertinoText(text = "Settings", modifier = Modifier.testTag("action-label"))
                        }
                    }
                }
            }
        }
        mainClock.advanceTimeBy(1000)
        val check = onNodeWithTag("checkmark", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val selected = onNodeWithTag("selected-label", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val unselected = onNodeWithTag("unselected-label", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val action = onNodeWithTag("action-label", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val row = onNodeWithTag("selected-row").fetchSemanticsNode().boundsInRoot
        val menu = onNodeWithTag("menu").fetchSemanticsNode().boundsInRoot
        assertEquals(1, selectedLines)
        assertEquals(1, unselectedLines)
        assertTrue(abs(check.center.y - selected.center.y) < 1f)
        assertTrue(row.left > menu.left && row.right < menu.right)
        if (withIcons) {
            val leading = onNodeWithTag("leading-icon", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
            val trailing = onNodeWithTag("trailing-icon", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
            if (direction == LayoutDirection.Ltr) {
                assertTrue(check.right < leading.left && leading.right < selected.left && selected.right < trailing.left)
            } else {
                assertTrue(check.left > leading.right && leading.left > selected.right && selected.left > trailing.right)
            }
        }
        if (direction == LayoutDirection.Ltr) {
            assertTrue(check.right < selected.left)
            assertEquals(selected.left, unselected.left, 1f)
            assertEquals(selected.left, action.left, 1f)
        } else {
            assertTrue(check.left > selected.right)
            assertEquals(selected.right, unselected.right, 1f)
            assertEquals(selected.right, action.right, 1f)
        }
    }

    @Test
    fun opensPickerAndLetsSelectionCloseIt() = runComposeUiTest {
        var selected = false
        var isExpanded = false
        setContent {
            CompositionLocalProvider(LocalColorScheme provides lightColorScheme()) {
                var expanded by remember { mutableStateOf(false) }
                isExpanded = expanded
                CupertinoPopupButton(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    title = { CupertinoText(text = "선택값") },
                ) {
                    MenuPickerAction(
                        isSelected = true,
                        onClick = { selected = true; expanded = false },
                        title = { CupertinoText(text = "항목") },
                    )
                }
            }
        }
        onNodeWithText("선택값").performClick()
        runOnIdle { assertTrue(isExpanded) }
        onNodeWithText("항목").assertIsSelected().performClick()
        runOnIdle {
            assertTrue(selected)
            assertFalse(isExpanded)
        }
    }

    @Test
    fun disabledButtonDoesNotExposeAnExpandedMenu() = runComposeUiTest {
        var changed = false
        setContent {
            CompositionLocalProvider(LocalColorScheme provides lightColorScheme()) {
                CupertinoPopupButton(
                    expanded = true,
                    onExpandedChange = { changed = true },
                    enabled = false,
                    title = { CupertinoText(text = "선택값") },
                ) {
                    MenuAction(onClick = {}, title = { CupertinoText(text = "항목") })
                }
            }
        }
        onNodeWithText("선택값").performClick()
        onNodeWithText("항목").assertDoesNotExist()
        runOnIdle { assertFalse(changed) }
    }
}
