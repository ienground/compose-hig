package cupertino

import RootUiState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.Test
import zone.ien.hig.ExperimentalCupertinoApi
import zone.ien.hig.adaptive.AdaptiveTheme
import zone.ien.hig.adaptive.CupertinoThemeSpec
import zone.ien.hig.adaptive.ExperimentalAdaptiveApi
import zone.ien.hig.adaptive.Theme
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.theme.darkColorScheme

@OptIn(ExperimentalTestApi::class, ExperimentalCupertinoApi::class)
class ComponentCatalogUiTest {
    @Test
    @OptIn(ExperimentalAdaptiveApi::class)
    fun iconCatalogCreatesOnlyDisplayedIconsAndKeepsSelection() = runComposeUiTest {
        val hiddenIcons = listOf("outlined", "filled").map { style ->
            Class.forName("zone.ien.hig.icons.$style.XmarkShieldKt")
                .getDeclaredField("_xmarkShield").apply { isAccessible = true }
        }
        hiddenIcons.forEach { assertNull(it.get(null)) }
        mainClock.autoAdvance = false
        setContent {
            AdaptiveTheme(target = Theme.Cupertino, cupertino = CupertinoThemeSpec.Default()) {
                Box(Modifier.size(400.dp, 800.dp)) { icons.IconsScreen {} }
            }
        }
        mainClock.advanceTimeBy(500)
        onNodeWithContentDescription("Airplane").assertIsDisplayed().performClick()
        mainClock.advanceTimeBy(500)
        onNodeWithText("CupertinoIcon.Outlined.Airplane").assertIsDisplayed()
        hiddenIcons.forEach { assertNull(it.get(null), "화면 밖 아이콘을 미리 생성했습니다") }
        onNodeWithText("Close").performClick()
        mainClock.advanceTimeBy(500)
        onNodeWithText("Filled").performClick()
        mainClock.advanceTimeBy(1000)
        onNodeWithContentDescription("Airtag").assertIsDisplayed().performClick()
        mainClock.advanceTimeBy(500)
        onNodeWithText("CupertinoIcon.Filled.Airtag").assertIsDisplayed()
        hiddenIcons.forEach { assertNull(it.get(null), "화면 밖 아이콘을 미리 생성했습니다") }
    }

    @Test
    fun menuRendersOverPhotoAndKeepsActions() = runComposeUiTest {
        mainClock.autoAdvance = false
        setContent {
            CupertinoTheme {
                Box(Modifier.size(400.dp, 800.dp)) { ComponentDetailScreen(CatalogComponent.Menu) {} }
            }
        }
        mainClock.advanceTimeBy(500)
        onNodeWithText("Show menu").performClick()
        mainClock.advanceTimeBy(1000)
        onNodeWithText("Unavailable").assertIsNotEnabled()
        onAllNodes(isRoot()).fetchSemanticsNodes().indices.forEach { index ->
            val pixels = onAllNodes(isRoot())[index].captureToImage().toPixelMap()
            val output = BufferedImage(pixels.width, pixels.height, BufferedImage.TYPE_INT_ARGB)
            for (y in 0 until pixels.height) for (x in 0 until pixels.width) output.setRGB(x, y, pixels[x, y].toArgb())
            val file = File("build/reports/catalog/Menu-open-$index.png")
            file.parentFile.mkdirs()
            ImageIO.write(output, "png", file)
        }
        onNodeWithText("Share").performClick()
        mainClock.advanceTimeBy(500)
        onNodeWithTag("demo-result").assertTextContains("Share selected")
    }

    @Test
    fun categoryDropdownRendersAndAppliesSelection() = runComposeUiTest {
        mainClock.autoAdvance = false
        setContent {
            CupertinoTheme {
                Box(Modifier.size(400.dp, 800.dp)) { CupertinoWidgetsScreen(RootUiState(), {}, {}) }
            }
        }
        mainClock.advanceTimeBy(500)
        onNodeWithText("All components").performClick()
        mainClock.advanceTimeBy(1000)
        onAllNodes(isRoot()).fetchSemanticsNodes().indices.forEach { index ->
            val pixels = onAllNodes(isRoot())[index].captureToImage().toPixelMap()
            val output = BufferedImage(pixels.width, pixels.height, BufferedImage.TYPE_INT_ARGB)
            for (y in 0 until pixels.height) for (x in 0 until pixels.width) output.setRGB(x, y, pixels[x, y].toArgb())
            val file = File("build/reports/catalog/Category-menu-$index.png")
            file.parentFile.mkdirs()
            ImageIO.write(output, "png", file)
        }
        onNode(hasText("Selection") and hasClickAction()).performClick()
        mainClock.advanceTimeBy(500)
        val count = CatalogComponent.entries.count { it.category == ComponentCategory.Selection }
        onNodeWithText("$count / ${CatalogComponent.entries.size}").assertIsDisplayed()
    }

    @Test
    fun glassToolbarButtonsKeepBackResetAndEditActions() = runComposeUiTest {
        mainClock.autoAdvance = false
        var backCount = 0
        setContent {
            CupertinoTheme {
                Box(Modifier.size(400.dp, 800.dp).testTag("toolbar-screen")) {
                    ComponentDetailScreen(CatalogComponent.TopBar) { backCount++ }
                }
            }
        }
        mainClock.advanceTimeBy(500)
        onNodeWithTag("preview-toolbar-back").performClick()
        mainClock.advanceTimeBy(100)
        onNodeWithTag("demo-result").assertTextContains("Back pressed")
        onNodeWithContentDescription("Edit").performClick()
        mainClock.advanceTimeBy(100)
        onNodeWithTag("demo-result").assertTextContains("Edit pressed")
        onNodeWithTag("reset-demo").assertHeightIsAtLeast(44.dp).performClick()
        mainClock.advanceTimeBy(500)
        onNodeWithTag("demo-result").assertTextContains("Try the toolbar actions")
        val button = onNodeWithTag("preview-toolbar-back")
        button.performTouchInput { down(center) }
        mainClock.advanceTimeBy(250)
        val pixels = onNodeWithTag("toolbar-screen").captureToImage().toPixelMap()
        val output = BufferedImage(pixels.width, pixels.height, BufferedImage.TYPE_INT_ARGB)
        for (y in 0 until pixels.height) for (x in 0 until pixels.width) output.setRGB(x, y, pixels[x, y].toArgb())
        val file = File("build/reports/catalog/Toolbar-pressed.png")
        file.parentFile.mkdirs()
        ImageIO.write(output, "png", file)
        button.performTouchInput { up() }
        mainClock.advanceTimeBy(500)
        onNodeWithTag("navigate-back").assertHeightIsAtLeast(44.dp).performClick()
        runOnIdle { assertEquals(1, backCount) }
    }

    @Test
    fun homeGlassActionOpensAppearanceControls() = runComposeUiTest {
        mainClock.autoAdvance = false
        setContent {
            CupertinoTheme {
                Box(Modifier.size(400.dp, 800.dp)) { CupertinoWidgetsScreen(RootUiState(), {}, {}) }
            }
        }
        mainClock.advanceTimeBy(500)
        onNodeWithContentDescription("Appearance").assertHeightIsAtLeast(44.dp).performClick()
        mainClock.advanceTimeBy(500)
        onNodeWithContentDescription("Dark mode").assertIsDisplayed()
    }

    @Test
    fun pressedSwitchUsesItsPreviewBackdropInsteadOfAnotherSection() = runComposeUiTest {
        mainClock.autoAdvance = false
        setContent {
            CupertinoTheme(colorScheme = zone.ien.hig.theme.lightColorScheme().copy(
                secondarySystemBackground = androidx.compose.ui.graphics.Color.Blue,
                secondarySystemGroupedBackground = androidx.compose.ui.graphics.Color.Red,
            )) {
                Column(Modifier.size(400.dp, 800.dp)) {
                    DemoSection("Preview") {
                        zone.ien.hig.CupertinoSwitch(
                            checked = false, onCheckedChange = {},
                            backdrop = checkNotNull(LocalDemoBackdrop.current),
                            modifier = Modifier.testTag("isolated-switch"),
                        )
                    }
                    DemoSection("Properties") { DemoToggle("Enabled", true) {} }
                }
            }
        }
        mainClock.advanceTimeBy(300)
        val control = onNodeWithTag("isolated-switch")
        control.performTouchInput { down(Offset(20f, center.y)) }
        mainClock.advanceTimeBy(500)
        val pixels = control.captureToImage().toPixelMap()
        val thumb = pixels[20, pixels.height / 2]
        kotlin.test.assertTrue(thumb.blue > thumb.red + 0.25f, "Preview backdrop: $thumb")
        control.performTouchInput { up() }
    }

    @Test
    fun switchRefractsPhotoWhilePressedAndChangesOnDrag() = runComposeUiTest {
        mainClock.autoAdvance = false
        setContent {
            CupertinoTheme {
                Box(Modifier.size(400.dp, 800.dp).testTag("switch-screen")) {
                    ComponentDetailScreen(CatalogComponent.Switch) {}
                }
            }
        }
        mainClock.advanceTimeBy(300)
        val control = onNodeWithTag("preview-switch")
        control.performTouchInput { down(Offset(20f, center.y)) }
        mainClock.advanceTimeBy(500)
        val pixels = onNodeWithTag("switch-screen").captureToImage().toPixelMap()
        val output = BufferedImage(pixels.width, pixels.height, BufferedImage.TYPE_INT_ARGB)
        for (y in 0 until pixels.height) for (x in 0 until pixels.width) output.setRGB(x, y, pixels[x, y].toArgb())
        val file = File("build/reports/catalog/Switch-pressed.png")
        file.parentFile.mkdirs()
        ImageIO.write(output, "png", file)
        control.performTouchInput { moveTo(Offset(52f, center.y), delayMillis = 200) }
        mainClock.advanceTimeBy(250)
        control.performTouchInput { up() }
        mainClock.advanceTimeBy(500)
        onNodeWithTag("demo-result").assertTextContains("checked = true · Changes: 1")
        onNodeWithContentDescription("Enabled").performClick()
        mainClock.advanceTimeBy(100)
        control.assertIsNotEnabled()
    }

    @Test
    fun initialPickerSelectionSurvivesFirstMeasurementAtFractionalDensity() = runComposeUiTest {
        mainClock.autoAdvance = false
        setContent {
            androidx.compose.runtime.CompositionLocalProvider(
                androidx.compose.ui.platform.LocalDensity provides androidx.compose.ui.unit.Density(2.75f, 1.2f),
            ) {
                CupertinoTheme {
                    Column(Modifier.size(320.dp, 700.dp)) {
                        val state = zone.ien.hig.rememberCupertinoTimePickerState(initialHour = 9, initialMinute = 30)
                        zone.ien.hig.CupertinoTimePicker(state = state, modifier = Modifier.fillMaxWidth())
                        zone.ien.hig.CupertinoText(text = "${state.hour}:${state.minute}", modifier = Modifier.testTag("initial-time"))
                    }
                }
            }
        }
        mainClock.advanceTimeBy(1000)
        onNodeWithTag("initial-time").assertTextContains("9:30")
    }

    @Test
    fun navigationIndicatorFollowsDragToAnotherTab() = runComposeUiTest {
        mainClock.autoAdvance = false
        setContent { CupertinoTheme { Box(Modifier.size(400.dp, 800.dp)) { ComponentDetailScreen(CatalogComponent.NavigationBar) {} } } }
        mainClock.advanceTimeBy(300)
        val bar = onNodeWithTag("preview-navigation")
        bar.performTouchInput { down(Offset(center.x - 85f, center.y)) }
        mainClock.advanceTimeBy(250)
        bar.performTouchInput { moveTo(Offset(center.x + 85f, center.y), delayMillis = 200) }
        mainClock.advanceTimeBy(250)
        val pixels = bar.captureToImage().toPixelMap()
        val output = BufferedImage(pixels.width, pixels.height, BufferedImage.TYPE_INT_ARGB)
        for (y in 0 until pixels.height) for (x in 0 until pixels.width) output.setRGB(x, y, pixels[x, y].toArgb())
        val file = File("build/reports/catalog/NavigationBar-pressed.png")
        file.parentFile.mkdirs()
        ImageIO.write(output, "png", file)
        bar.performTouchInput { up() }
        mainClock.advanceTimeBy(500)
        onNodeWithTag("demo-result").assertTextContains("selectedTabIndex = 2", substring = true)
    }

    @Test
    fun englishPickersFitNarrowPreviewAndPreserveSelection() {
        val locale = java.util.Locale.getDefault()
        java.util.Locale.setDefault(java.util.Locale.US)
        try {
            runComposeUiTest {
                mainClock.autoAdvance = false
                setContent {
                    CupertinoTheme {
                        Column(Modifier.size(280.dp, 740.dp).padding(8.dp).testTag("pickers")) {
                            val date = kotlin.time.Instant.parse("2026-09-30T00:00:00Z").toEpochMilliseconds()
                            zone.ien.hig.CupertinoDatePicker(
                                state = zone.ien.hig.rememberCupertinoDatePickerState(initialSelectedDateMillis = date),
                                style = zone.ien.hig.DatePickerStyle.Wheel(), modifier = Modifier.fillMaxWidth())
                            zone.ien.hig.CupertinoTimePicker(
                                state = zone.ien.hig.rememberCupertinoTimePickerState(initialHour = 8, is24Hour = false),
                                modifier = Modifier.fillMaxWidth())
                            zone.ien.hig.CupertinoDateTimePicker(
                                state = zone.ien.hig.rememberCupertinoDateTimePickerState(
                                    initialSelectedDateMillis = date, initialHour = 8, is24Hour = false),
                                style = zone.ien.hig.DatePickerStyle.Wheel(), modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
                mainClock.advanceTimeBy(300)
                onNodeWithText("September").assertIsDisplayed()
                val pixels = onNodeWithTag("pickers").captureToImage().toPixelMap()
                val output = BufferedImage(pixels.width, pixels.height, BufferedImage.TYPE_INT_ARGB)
                for (y in 0 until pixels.height) for (x in 0 until pixels.width) output.setRGB(x, y, pixels[x, y].toArgb())
                val file = File("build/reports/catalog/Pickers-English-narrow.png")
                file.parentFile.mkdirs()
                ImageIO.write(output, "png", file)
            }
        } finally {
            java.util.Locale.setDefault(locale)
        }
    }

    @Test
    fun actionSheetRendersAndReportsSelection() = runComposeUiTest {
        mainClock.autoAdvance = false
        setContent { CupertinoTheme { Box(Modifier.size(400.dp, 800.dp)) { ComponentDetailScreen(CatalogComponent.ActionSheet) {} } } }
        mainClock.advanceTimeBy(100)
        onNodeWithText("Show action sheet").performClick()
        mainClock.advanceTimeBy(1000)
        onNodeWithText("Copy").assertIsDisplayed().assertHeightIsAtLeast(48.dp)
        onNodeWithText("Cancel").assertHeightIsAtLeast(48.dp)
        onAllNodes(isRoot()).fetchSemanticsNodes().indices.forEach { index ->
            val pixels = onAllNodes(isRoot())[index].captureToImage().toPixelMap()
            val output = BufferedImage(pixels.width, pixels.height, BufferedImage.TYPE_INT_ARGB)
            for (y in 0 until pixels.height) for (x in 0 until pixels.width) output.setRGB(x, y, pixels[x, y].toArgb())
            val file = File("build/reports/catalog/ActionSheet-open-$index.png")
            file.parentFile.mkdirs()
            ImageIO.write(output, "png", file)
        }
        onNodeWithText("Copy").performClick()
        mainClock.advanceTimeBy(500)
        onNodeWithTag("demo-result").assertTextContains("Photo copied")
    }

    @Test
    fun rendersEveryDetailAtPhoneWidth() = runComposeUiTest {
        // 유리 배경의 휘도 샘플링은 계속 실행되므로 필요한 프레임만 진행한다.
        mainClock.autoAdvance = false
        var selected by mutableStateOf(CatalogComponent.Button)
        setContent {
            CupertinoTheme {
                Box(Modifier.size(400.dp, 800.dp).testTag("capture")) {
                    key(selected) { ComponentDetailScreen(selected, navigateBack = {}) }
                }
            }
        }
        CatalogComponent.entries.forEach { component ->
            runOnIdle { selected = component }
            mainClock.advanceTimeBy(100)
            onNodeWithTag("component-detail").assertIsDisplayed()
            val pixels = onNodeWithTag("capture").captureToImage().toPixelMap()
            val output = BufferedImage(pixels.width, pixels.height, BufferedImage.TYPE_INT_ARGB)
            for (y in 0 until pixels.height) for (x in 0 until pixels.width) output.setRGB(x, y, pixels[x, y].toArgb())
            val file = File("build/reports/catalog/${component.name}.png")
            file.parentFile.mkdirs()
            ImageIO.write(output, "png", file)
        }
    }

    @Test
    fun buttonReportsClicksAndResetRestoresInitialState() = runComposeUiTest {
        mainClock.autoAdvance = false
        setContent { CupertinoTheme { Box(Modifier.size(400.dp, 800.dp)) { ComponentDetailScreen(CatalogComponent.Button) {} } } }
        mainClock.advanceTimeBy(100)
        onNodeWithTag("preview-button").performClick()
        mainClock.advanceTimeByFrame()
        onNodeWithTag("demo-result").assertTextContains("Clicks: 1", substring = true)
        onNodeWithTag("reset-demo").performClick()
        mainClock.advanceTimeByFrame()
        onNodeWithTag("demo-result").assertTextContains("Clicks: 0", substring = true)
    }

    @Test
    fun catalogSearchFindsApiAndClearRestoresCatalog() = runComposeUiTest {
        mainClock.autoAdvance = false
        setContent { CupertinoTheme { Box(Modifier.size(400.dp, 800.dp)) { CupertinoWidgetsScreen(RootUiState(), {}, {}) } } }
        mainClock.advanceTimeBy(100)
        onNodeWithText("Search components").performTextInput("CupertinoStepper")
        mainClock.advanceTimeBy(100)
        onNodeWithText("Stepper", useUnmergedTree = true).assertIsDisplayed()
        onNodeWithText("Cancel").performClick()
        mainClock.advanceTimeBy(500)
        onNodeWithText("28 / 28").assertIsDisplayed()
        val pixels = onAllNodes(isRoot())[0].captureToImage().toPixelMap()
        val output = BufferedImage(pixels.width, pixels.height, BufferedImage.TYPE_INT_ARGB)
        for (y in 0 until pixels.height) for (x in 0 until pixels.width) output.setRGB(x, y, pixels[x, y].toArgb())
        ImageIO.write(output, "png", File("build/reports/catalog/Home.png"))
    }

    @Test
    fun darkLargeTextControlsRemainScrollable() = runComposeUiTest {
        mainClock.autoAdvance = false
        setContent {
            androidx.compose.runtime.CompositionLocalProvider(
                androidx.compose.ui.platform.LocalDensity provides androidx.compose.ui.unit.Density(1f, 1.6f),
            ) {
                CupertinoTheme(colorScheme = darkColorScheme()) {
                    Box(Modifier.size(360.dp, 780.dp)) { ComponentDetailScreen(CatalogComponent.Button) {} }
                }
            }
        }
        mainClock.advanceTimeBy(100)
        onNodeWithTag("preview-button").performClick()
        mainClock.advanceTimeByFrame()
        onNodeWithTag("demo-result").assertTextContains("Clicks: 1", substring = true)
        onNodeWithTag("component-detail").performSemanticsAction(SemanticsActions.ScrollBy) { scrollBy ->
            scrollBy(0f, 10_000f)
        }
        mainClock.advanceTimeBy(100)
        onNodeWithText("API").assertIsDisplayed()
    }

    @Test
    fun navigationTabsKeepSelectionAcrossImplementations() = runComposeUiTest {
        mainClock.autoAdvance = false
        setContent { CupertinoTheme { Box(Modifier.size(400.dp, 800.dp)) { ComponentDetailScreen(CatalogComponent.NavigationBar) {} } } }
        mainClock.advanceTimeBy(100)
        onNodeWithContentDescription("Favorites").performClick()
        mainClock.advanceTimeBy(300)
        onNodeWithTag("demo-result").assertTextContains("selectedTabIndex = 1", substring = true)
        mainClock.advanceTimeBy(1000)
        val composePixels = onNodeWithTag("preview-navigation").captureToImage().toPixelMap()
        onNodeWithContentDescription("Native implementation").performClick()
        mainClock.advanceTimeBy(1000)
        val nativePixels = onNodeWithTag("preview-navigation").captureToImage().toPixelMap()
        assertEquals(composePixels.width, nativePixels.width)
        assertEquals(composePixels.height, nativePixels.height)
        for (y in 0 until composePixels.height) for (x in 0 until composePixels.width) {
            assertEquals(composePixels[x, y], nativePixels[x, y], "Navigation pixel ($x, $y)")
        }
        onNodeWithTag("demo-result").assertTextContains("selectedTabIndex = 1", substring = true)
    }

    @Test
    fun alertConfirmAndPopupSelectionReportResults() = runComposeUiTest {
        mainClock.autoAdvance = false
        var selected by mutableStateOf(CatalogComponent.AlertDialog)
        setContent { CupertinoTheme { Box(Modifier.size(400.dp, 800.dp)) { key(selected) { ComponentDetailScreen(selected) {} } } } }
        mainClock.advanceTimeBy(100)
        onNodeWithText("Show alert").performClick()
        mainClock.advanceTimeBy(500)
        onNodeWithText("Save").performClick()
        mainClock.advanceTimeBy(500)
        onNodeWithTag("demo-result").assertTextContains("Changes saved")
        runOnIdle { selected = CatalogComponent.PopupButton }
        mainClock.advanceTimeBy(100)
        onNodeWithText("Name").performClick()
        mainClock.advanceTimeBy(500)
        onNodeWithText("Recent").performClick()
        mainClock.advanceTimeBy(500)
        onNodeWithTag("demo-result").assertTextContains("Sort: Recent", substring = true)
    }
}
