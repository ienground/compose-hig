package zone.ien.hig

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.click
import androidx.compose.ui.test.isPopup
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.theme.darkColorScheme
import zone.ien.hig.theme.lightColorScheme

@OptIn(ExperimentalTestApi::class, ExperimentalCupertinoApi::class)
class CupertinoIos27GalleryTest {
    @Test
    fun menuRendersShadowOutsideSurfaceAndKeepsInteractions() = runComposeUiTest {
        var dismissCount = 0
        var selectionCount = 0
        var dismissOnClickOutside by mutableStateOf(true)
        setContent {
            CupertinoTheme {
                Box(Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
                    Box(Modifier.size(40.dp)) {
                        CupertinoDropdownMenu(
                            expanded = true,
                            onDismissRequest = { dismissCount++ },
                            modifier = Modifier.testTag("menu"),
                            containerColor = Color.White,
                            properties = PopupProperties(dismissOnClickOutside = dismissOnClickOutside, clippingEnabled = false),
                        ) {
                            MenuAction(onClick = { selectionCount++ }) { CupertinoText(text = "메뉴 항목") }
                        }
                    }
                }
            }
        }
        val popup = onNode(isPopup())
        val surface = onNodeWithTag("menu").fetchSemanticsNode().boundsInRoot
        val bounds = popup.fetchSemanticsNode().boundsInRoot
        val pixels = popup.captureToImage().toPixelMap()
        val x = (surface.right - bounds.left).toInt() + 4
        val y = (surface.center.y - bounds.top).toInt()
        assertTrue(pixels[x, y].red < 1f, "메뉴 본문 밖에도 그림자가 보여야 합니다.")
        onNodeWithText("메뉴 항목").performClick()
        assertEquals(1, selectionCount)
        assertEquals(0, dismissCount)
        popup.performTouchInput { click(Offset(x.toFloat(), y.toFloat())) }
        assertEquals(1, dismissCount)
        runOnIdle { dismissOnClickOutside = false }
        popup.performTouchInput { click(Offset(x.toFloat(), y.toFloat())) }
        assertEquals(1, dismissCount)
    }

    @Test
    fun popoverRendersShadowAndDismissesOutsideSurface() = runComposeUiTest {
        var dismissCount = 0
        var dismissOnClickOutside by mutableStateOf(true)
        setContent {
            CupertinoTheme {
                Box(Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
                    Box(Modifier.size(40.dp)) {
                        CupertinoPopover(
                            expanded = true,
                            onDismissRequest = { dismissCount++ },
                            modifier = Modifier.height(160.dp).testTag("popover"),
                            contentPadding = PaddingValues(0.dp),
                            containerColor = Color.White,
                            properties = PopupProperties(dismissOnClickOutside = dismissOnClickOutside, clippingEnabled = false),
                        ) {}
                    }
                }
            }
        }
        val popup = onNode(isPopup())
        val surfaceBounds = onNodeWithTag("popover").fetchSemanticsNode().boundsInRoot
        val popupBounds = popup.fetchSemanticsNode().boundsInRoot
        val image = popup.captureToImage()
        val pixels = image.toPixelMap()
        val output = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_ARGB)
        for (y in 0 until image.height) {
            for (x in 0 until image.width) output.setRGB(x, y, pixels[x, y].toArgb())
        }
        val destination = File("build/reports/ios27/popover-shadow.png")
        destination.parentFile.mkdirs()
        assertTrue(ImageIO.write(output, "png", destination))

        assertTrue(popupBounds.left < surfaceBounds.left, "왼쪽 그림자가 Popup 내부에 들어가야 합니다.")
        assertTrue(popupBounds.top < surfaceBounds.top, "위쪽 그림자가 Popup 내부에 들어가야 합니다.")
        assertTrue(popupBounds.right > surfaceBounds.right, "오른쪽 그림자가 Popup 내부에 들어가야 합니다.")
        assertTrue(popupBounds.bottom > surfaceBounds.bottom, "아래쪽 그림자가 Popup 내부에 들어가야 합니다.")
        val centerX = (surfaceBounds.center.x - popupBounds.left).toInt()
        val centerY = (surfaceBounds.center.y - popupBounds.top).toInt()
        val shadowPoints = listOf(
            (surfaceBounds.left - popupBounds.left).toInt() - 8 to centerY,
            (surfaceBounds.right - popupBounds.left).toInt() + 8 to centerY,
            centerX to (surfaceBounds.top - popupBounds.top).toInt() - 8,
            centerX to (surfaceBounds.bottom - popupBounds.top).toInt() + 8,
        )
        shadowPoints.forEach { (x, y) ->
            val color = pixels[x, y]
            assertTrue(color.alpha > 0f && color.red < 0.99f, "본문 밖 ($x, $y)에 그림자가 그려져야 합니다.")
        }
        onNodeWithTag("popover").performTouchInput { click(center) }
        assertEquals(0, dismissCount)
        val shadowPoint = shadowPoints.first()
        popup.performTouchInput { click(Offset(shadowPoint.first.toFloat(), shadowPoint.second.toFloat())) }
        assertEquals(1, dismissCount)
        runOnIdle { dismissOnClickOutside = false }
        popup.performTouchInput { click(Offset(shadowPoint.first.toFloat(), shadowPoint.second.toFloat())) }
        assertEquals(1, dismissCount)
    }

    @Test
    fun rendersLightControls() = renderGallery(dark = false)

    @Test
    fun rendersDarkControls() = renderGallery(dark = true)

    private fun renderGallery(dark: Boolean) = runComposeUiTest {
        setContent {
            CupertinoTheme(colorScheme = if (dark) darkColorScheme() else lightColorScheme()) {
                Column(
                    modifier = Modifier
                        .size(430.dp, 740.dp)
                        .background(CupertinoTheme.colorScheme.systemBackground)
                        .padding(24.dp)
                        .testTag("gallery"),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    CupertinoText(text = "iOS 27", style = CupertinoTheme.typography.largeTitle)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        CupertinoButtonSize.entries.take(3).forEach { size ->
                            CupertinoButton(onClick = {}, size = size) { CupertinoText(text = "버튼") }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        CupertinoSwitch(checked = true, onCheckedChange = {})
                        CupertinoSwitch(checked = false, onCheckedChange = {})
                        CupertinoSwitch(checked = true, onCheckedChange = {}, enabled = false)
                    }
                    CupertinoSegmentedControl(selectedTabIndex = 0, modifier = Modifier.fillMaxWidth()) {
                        CupertinoSegmentedControlTab(isSelected = true, onClick = {}) { CupertinoText(text = "전체") }
                        CupertinoSegmentedControlTab(isSelected = false, onClick = {}) { CupertinoText(text = "즐겨찾기") }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        CupertinoStepper(value = 4, onValueChange = {}, valueRange = 0..10)
                        CupertinoBadge { CupertinoText(text = "4") }
                        CupertinoPopupButton(expanded = false, onExpandedChange = {}, title = { CupertinoText(text = "이름") }) {}
                    }
                    CupertinoPageControl(pageCount = 6, currentPage = 2, onPageSelected = {}, modifier = Modifier.fillMaxWidth())
                    CupertinoProgressIndicator(progress = 0.4f, label = "진행률 40%")
                    CupertinoEmptyState(
                        title = { CupertinoText(text = "저장된 항목 없음") },
                        modifier = Modifier.fillMaxWidth().height(230.dp),
                        description = { CupertinoText(text = "새 항목을 추가하면 여기에 표시됩니다.") },
                        onAction = {},
                        action = { CupertinoText(text = "항목 추가") },
                    )
                }
            }
        }
        val image = onNodeWithTag("gallery").captureToImage()
        val pixels = image.toPixelMap()
        val output = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_ARGB)
        for (y in 0 until image.height) {
            for (x in 0 until image.width) output.setRGB(x, y, pixels[x, y].toArgb())
        }
        val destination = File("build/reports/ios27/${if (dark) "dark" else "light"}.png")
        destination.parentFile.mkdirs()
        assertTrue(ImageIO.write(output, "png", destination))
    }
}
