package zone.ien.hig

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
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
class CupertinoBottomSheetBackdropTest {
    @Test
    fun sheetAndToolbarReflectChangesBehindTheSheetBeforeScrolling() = assertBackdropUpdates(dark = false)

    @Test
    fun darkSheetAndToolbarReflectChangesBehindTheSheetBeforeScrolling() = assertBackdropUpdates(dark = true)

    @Test
    fun opaqueContentContainerStillCoversTheSheetBackdrop() = runComposeUiTest {
        setContent {
            SheetBackdropSample(background = Color.Red, contentContainerColor = Color.Green)
        }
        val image = onNodeWithTag("scaffold").captureToImage()
        image.saveSheetImage("opaque")
        assertEquals(Color.Green, image.toPixelMap()[image.width / 2, image.height * 7 / 8])
    }

    private fun assertBackdropUpdates(dark: Boolean) = runComposeUiTest {
        var background by mutableStateOf(Color.Red)
        setContent {
            SheetBackdropSample(background = background, dark = dark)
        }

        val redToolbar = onNodeWithTag("toolbar").captureToImage().let {
            it.toPixelMap()[it.width / 2, it.height / 2]
        }
        onNodeWithTag("scaffold").captureToImage().saveSheetImage(if (dark) "dark" else "light")
        val redSheet = onNodeWithTag("scaffold").captureToImage().let {
            it.toPixelMap()[it.width / 2, it.height * 7 / 8]
        }
        assertTrue(redToolbar.alpha > 0.99f, "탑바 backdrop에는 시트 뒤 화면이 포함되어야 합니다.")

        runOnIdle { background = Color.Blue }

        val blueToolbar = onNodeWithTag("toolbar").captureToImage().let {
            it.toPixelMap()[it.width / 2, it.height / 2]
        }
        val blueSheet = onNodeWithTag("scaffold").captureToImage().let {
            it.toPixelMap()[it.width / 2, it.height * 7 / 8]
        }
        assertTrue(redToolbar.red - blueToolbar.red > 0.2f, "탑바가 변경된 배경을 반영해야 합니다.")
        assertTrue(blueToolbar.blue - redToolbar.blue > 0.2f)
        assertTrue(redSheet.red - blueSheet.red > 0.25f, "시트 배경에 글래스 투명도가 유지되어야 합니다.")
        assertTrue(blueSheet.blue - redSheet.blue > 0.25f)
    }
}

private fun ImageBitmap.saveSheetImage(name: String) {
    val pixels = toPixelMap()
    val output = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    for (y in 0 until height) {
        for (x in 0 until width) output.setRGB(x, y, pixels[x, y].toArgb())
    }
    val destination = File("build/reports/bottom-sheet/$name.png")
    destination.parentFile.mkdirs()
    assertTrue(ImageIO.write(output, "png", destination))
}

@OptIn(ExperimentalCupertinoApi::class)
@Composable
private fun SheetBackdropSample(
    background: Color,
    dark: Boolean = false,
    contentContainerColor: Color? = null,
) {
    CupertinoTheme(colorScheme = if (dark) darkColorScheme() else lightColorScheme()) {
        Box(Modifier.size(320.dp, 480.dp).testTag("scaffold")) {
            CupertinoBottomSheetScaffold(
                modifier = Modifier.fillMaxSize(),
                windowInsets = WindowInsets(0),
                scaffoldState = rememberCupertinoBottomSheetScaffoldState(
                    rememberCupertinoSheetState(
                        initialValue = CupertinoSheetValue.Expanded,
                        presentationStyle = PresentationStyle.Modal(
                            detents = setOf(PresentationDetent.Height(280.dp)),
                        ),
                    ),
                ),
                sheetContent = {
                    CupertinoBottomSheetContent(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = contentContainerColor ?: CupertinoBottomSheetDefaults.containerColor,
                        topBar = {
                            Box(Modifier.testTag("toolbar")) {
                                CupertinoTopAppBar(
                                    title = {},
                                    isBackgroundAdaptive = false,
                                    isBackgroundGradient = false,
                                )
                            }
                        },
                    ) {
                        Box(Modifier.fillMaxSize())
                    }
                },
            ) {
                Box(Modifier.fillMaxSize().background(background))
            }
        }
    }
}
