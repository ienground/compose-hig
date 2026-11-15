package zone.ien.hig.adaptive

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.SeekableTransitionState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.RectangleShape
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import zone.ien.hig.LocalTopBarTransition
import zone.ien.hig.TopBarTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import kotlin.test.Test
import kotlin.test.assertTrue
import zone.ien.hig.CupertinoTopAppBar
import zone.ien.hig.ExperimentalCupertinoApi

@OptIn(ExperimentalTestApi::class, ExperimentalCupertinoApi::class, ExperimentalAdaptiveApi::class)
class NavigationTopBarTransitionTest {
    private data object Root : NavKey
    private data object Detail : NavKey

    @Test
    fun navigationStaysAtLeadingEdgeDuringPushAndPop() = verifyPosition()

    @Test
    fun unmatchedNavigationStaysAtLeadingEdgeDuringPushAndPop() = verifyPosition(matched = false)

    @Test
    fun actionsResizeAtTrailingEdgeDuringPushAndPop() = verifyPosition(actions = true)

    @Test
    fun materialNavigationStaysAtLeadingEdge() = verifyPosition(material = true)

    @Test
    fun materialActionsResizeAtTrailingEdge() = verifyPosition(actions = true, material = true)

    @Test
    fun shadowSurvivesSeekingAndCancellingBackGesture() = runComposeUiTest {
        val state = SeekableTransitionState(true)
        lateinit var animationScope: CoroutineScope
        setContent {
            animationScope = rememberCoroutineScope()
            SharedTransitionLayout {
                ProvideNavigationTopBarTransition(this) {
                    val transition = rememberTransition(state, label = "BackGesture")
                    transition.AnimatedContent(
                        modifier = Modifier.size(320.dp, 480.dp),
                        transitionSpec = {
                            slideInHorizontally(tween(1200)) { -it / 3 } togetherWith
                                slideOutHorizontally(tween(1200)) { it }
                        },
                    ) { detail ->
                        CompositionLocalProvider(LocalNavAnimatedContentScope provides this) {
                            Box(Modifier.fillMaxSize().background(Color.White)) {
                                Box(Modifier.offset(64.dp, 128.dp)) {
                                    Box(
                                        LocalTopBarTransition.current!!
                                            .modifier(TopBarTransition.Slot.Actions)
                                            .size(if (detail) 80.dp else 48.dp, 48.dp)
                                            .drawBehind {
                                                val outset = 12.dp.toPx()
                                                drawRect(
                                                    Color.Black.copy(alpha = 0.25f),
                                                    Offset(-outset, -outset),
                                                    Size(size.width + outset * 2, size.height + outset * 2),
                                                )
                                            }
                                            .shadow(16.dp, RectangleShape, clip = false)
                                            .background(Color.Red),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        waitForIdle()
        mainClock.autoAdvance = false
        for (fraction in listOf(0.05f, 0.15f, 0.35f, 0.65f, 0.35f, 0.15f)) {
            runOnIdle { animationScope.launch { state.seekTo(fraction, false) } }
            mainClock.advanceTimeByFrame()
            waitForIdle()
            val pixels = onRoot().captureToImage().toPixelMap()
            assertTrue(pixels[88, 120].red < 0.98f, "뒤로가기 진행도 $fraction 에서 그림자가 잘리면 안 됩니다")
        }
        runOnIdle { animationScope.launch { state.snapTo(true) } }
        mainClock.autoAdvance = true
        waitForIdle()
        val pixels = onRoot().captureToImage().toPixelMap()
        assertTrue(pixels[135, 150].green < 0.2f, "뒤로가기 취소 후에는 원래 버튼 너비로 복원되어야 합니다")
    }

    private fun verifyPosition(
        matched: Boolean = true,
        actions: Boolean = false,
        material: Boolean = false,
    ) = runComposeUiTest {
        val stack = NavBackStack<NavKey>(Root)
        setContent {
            CompositionLocalProvider(LocalTheme provides Theme.Material3) {
                MaterialTheme {
                    SharedTransitionLayout {
                        ProvideNavigationTopBarTransition(this) {
                            NavDisplay(
                                backStack = stack,
                                modifier = Modifier.size(320.dp, 480.dp),
                                sharedTransitionScope = this@SharedTransitionLayout,
                                transitionSpec = {
                                    slideInHorizontally(tween(1000)) { it } togetherWith
                                        slideOutHorizontally(tween(1000)) { -it / 3 }
                                },
                                popTransitionSpec = {
                                    slideInHorizontally(tween(1000)) { -it / 3 } togetherWith
                                        slideOutHorizontally(tween(1000)) { it }
                                },
                            ) { key ->
                                NavEntry(key) {
                                    Box(Modifier.fillMaxSize().background(Color.White)) {
                                        val button: @Composable () -> Unit = {
                                            if (matched || key == Detail) {
                                                Box(Modifier.size(if (actions && key == Detail) 80.dp else 40.dp, 40.dp).background(Color.Red))
                                            }
                                        }
                                        if (!matched && key == Root) return@Box
                                        if (material) {
                                            MaterialTheme {
                                                AdaptiveTopAppBar(
                                                    title = {},
                                                    windowInsets = WindowInsets(0),
                                                    navigationIcon = { if (!actions) button() },
                                                    actions = { if (actions) button() },
                                                )
                                            }
                                        } else {
                                            CupertinoTopAppBar(
                                                title = {},
                                                windowInsets = WindowInsets(0),
                                                isBackgroundAdaptive = false,
                                                navigationIcon = { if (!actions) button() },
                                                actions = { if (actions) button() },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        waitForIdle()
        mainClock.autoAdvance = false
        runOnIdle { stack.add(Detail) }
        mainClock.advanceTimeBy(160)
        waitForIdle()
        val pushed = onRoot().captureToImage().toPixelMap()
        val x = if (actions) 288 else 32
        val y = 28
        assertTrue(pushed[x, y].green < 0.8f, "진입 중 상단 버튼이 본문과 함께 밀리면 안 됩니다")
        if (actions) {
            val width = (0 until pushed.width).count { pushed[it, y].green < 0.8f }
            assertTrue(width in 41..79, "액션 너비를 화면 사이에서 보간해야 합니다: $width")
        }
        mainClock.advanceTimeBy(1200)
        waitForIdle()
        runOnIdle { stack.removeAt(stack.lastIndex) }
        mainClock.advanceTimeBy(160)
        waitForIdle()
        val popped = onRoot().captureToImage().toPixelMap()
        assertTrue(popped[x, y].green < 0.95f, "복귀 중 상단 버튼은 가장자리 위치를 유지해야 합니다")
        mainClock.advanceTimeBy(1400)
        mainClock.autoAdvance = true
        waitForIdle()
        val settled = onRoot().captureToImage().toPixelMap()
        assertTrue(if (matched) settled[x, y].green < 0.2f else settled[x, y].green > 0.95f)
    }
}
