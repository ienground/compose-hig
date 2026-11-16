package zone.ien.hig

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertPositionInRootIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import zone.ien.hig.theme.CupertinoTheme
import kotlin.test.Test

/** [LocalCupertinoNativeBarsPadding]: the platform draws the navigation bar above these components. */
@OptIn(ExperimentalTestApi::class, ExperimentalCupertinoApi::class)
class CupertinoNativeBarsTest {

    private val nativeBars = PaddingValues(top = 100.dp, bottom = 30.dp)

    @Composable
    private fun NativeBars(padding: PaddingValues? = nativeBars, content: @Composable () -> Unit) {
        CupertinoTheme {
            CompositionLocalProvider(LocalCupertinoNativeBarsPadding provides padding, content = content)
        }
    }

    @Composable
    private fun Screen(contentWindowInsets: WindowInsets = WindowInsets(top = 20.dp, bottom = 10.dp)) {
        CupertinoScaffold(
            modifier = Modifier.size(240.dp, 400.dp),
            topBar = {
                // The adaptive background samples continuously and would keep the test from idling.
                CupertinoTopAppBar(title = { CupertinoText("Title") }, modifier = Modifier.testTag("top-bar"), isBackgroundAdaptive = false)
                Box(Modifier.fillMaxWidth().height(44.dp).testTag("custom-top-bar"))
            },
            contentWindowInsets = contentWindowInsets,
            hasNavigationTitle = true,
        ) { padding ->
            Box(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).cupertinoScrollEdge(rememberScrollState())) {
                CupertinoNavigationTitle(modifier = Modifier.testTag("navigation-title")) { CupertinoText("Large") }
            }
            Box(Modifier.fillMaxSize().padding(padding)) {
                Box(Modifier.fillMaxSize().testTag("body"))
            }
        }
    }

    @Test
    fun scaffoldLeavesTheTopBarToThePlatform() = runComposeUiTest {
        setContent { NativeBars { Screen() } }

        onNodeWithTag("top-bar").assertDoesNotExist()
        onNodeWithTag("custom-top-bar").assertDoesNotExist()
        onNodeWithTag("navigation-title").assertDoesNotExist()
        // The sides contentWindowInsets pads are replaced by the platform bars' padding.
        onNodeWithTag("body").assertPositionInRootIsEqualTo(0.dp, 100.dp)
        onNodeWithTag("body").assertHeightIsEqualTo(400.dp - 100.dp - 30.dp)
    }

    @Test
    fun withoutPlatformBarsTheScaffoldIsUnchanged() = runComposeUiTest {
        setContent { NativeBars(padding = null) { Screen() } }

        onNodeWithTag("top-bar").assertExists()
        onNodeWithTag("custom-top-bar").assertExists()
        onNodeWithTag("navigation-title").assertExists()
    }

    @Test
    fun aBottomBarStillTakesTheBottomPadding() = runComposeUiTest {
        setContent {
            NativeBars {
                CupertinoScaffold(
                    modifier = Modifier.size(240.dp, 400.dp),
                    bottomBar = { Box(Modifier.fillMaxWidth().height(50.dp)) },
                    contentWindowInsets = WindowInsets(top = 20.dp, bottom = 10.dp),
                ) { padding ->
                    Box(Modifier.fillMaxSize().padding(padding).testTag("body"))
                }
            }
        }
        onNodeWithTag("body").assertPositionInRootIsEqualTo(0.dp, 100.dp)
        onNodeWithTag("body").assertHeightIsEqualTo(400.dp - 100.dp - 50.dp)
    }

    @Test
    fun aContainerWithoutInsetsStaysWithoutInsets() = runComposeUiTest {
        // e.g. an app-wide wrapper that hosts every screen in a scaffold with WindowInsets(0)
        setContent {
            NativeBars {
                CupertinoScaffold(modifier = Modifier.size(240.dp, 400.dp), contentWindowInsets = WindowInsets(0)) { padding ->
                    Box(Modifier.fillMaxSize().padding(padding).testTag("body"))
                }
            }
        }
        onNodeWithTag("body").assertPositionInRootIsEqualTo(0.dp, 0.dp)
        onNodeWithTag("body").assertHeightIsEqualTo(400.dp)
    }

    @Test
    fun onlyTheRequestedSidesUseThePlatformBars() = runComposeUiTest {
        setContent {
            NativeBars {
                CupertinoScaffold(modifier = Modifier.size(240.dp, 400.dp), contentWindowInsets = WindowInsets(top = 20.dp)) { padding ->
                    Box(Modifier.fillMaxSize().padding(padding).testTag("body"))
                }
            }
        }
        onNodeWithTag("body").assertPositionInRootIsEqualTo(0.dp, 100.dp)
        onNodeWithTag("body").assertHeightIsEqualTo(300.dp)
    }

    @Test
    fun aScreenInsideAContainerGetsThePaddingOnce() = runComposeUiTest {
        setContent {
            NativeBars {
                CupertinoScaffold(modifier = Modifier.size(240.dp, 400.dp), contentWindowInsets = WindowInsets(0)) { outer ->
                    Box(Modifier.fillMaxSize().padding(outer)) { Screen() }
                }
            }
        }
        onNodeWithTag("top-bar").assertDoesNotExist()
        onNodeWithTag("body").assertPositionInRootIsEqualTo(0.dp, 100.dp)
        onNodeWithTag("body").assertHeightIsEqualTo(400.dp - 100.dp - 30.dp)
    }

    @Test
    fun sheetsKeepTheirOwnTopBar() = runComposeUiTest {
        setContent {
            NativeBars {
                Box(Modifier.size(240.dp, 400.dp)) {
                    CupertinoBottomSheetContent(
                        topBar = {
                            CupertinoTopAppBar(
                                title = { CupertinoText("Sheet") },
                                modifier = Modifier.testTag("sheet-top-bar"),
                                isBackgroundAdaptive = false,
                            )
                        },
                    ) { padding -> Box(Modifier.fillMaxSize().padding(padding)) }
                }
            }
        }
        onNodeWithTag("sheet-top-bar").assertExists()
    }
}
