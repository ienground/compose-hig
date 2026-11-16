package zone.ien.hig.adaptive

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.Transition
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
import androidx.navigationevent.DirectNavigationEventInput
import androidx.navigationevent.NavigationEvent
import androidx.navigationevent.NavigationEventDispatcher
import androidx.navigationevent.NavigationEventDispatcherOwner
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
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
    fun actionsCrossfadeAtTrailingEdgeDuringPushAndPop() = verifyPosition(actions = true)

    @Test
    fun materialNavigationStaysAtLeadingEdge() = verifyPosition(material = true)

    @Test
    fun materialActionsCrossfadeAtTrailingEdge() = verifyPosition(actions = true, material = true)

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

    @Test
    fun actionsStayOpaqueWhileSeekingWithoutLeavingTheirPosition() = verifyActionsGesture(detailOffset = 0)

    @Test
    fun incomingActionsDoNotSlideUpFromThePreviousActionsPosition() = verifyActionsGesture(detailOffset = 12)

    private fun verifyActionsGesture(detailOffset: Int) = runComposeUiTest {
        val state = SeekableTransitionState(true)
        lateinit var animationScope: CoroutineScope
        setContent {
            animationScope = rememberCoroutineScope()
            SharedTransitionLayout {
                ProvideNavigationTopBarTransition(this) {
                    val transition = rememberTransition(state, label = "ActionsBackGesture")
                    transition.AnimatedContent(
                        modifier = Modifier.size(320.dp, 480.dp),
                        transitionSpec = {
                            slideInHorizontally(tween(1200)) { -it / 3 } togetherWith
                                slideOutHorizontally(tween(1200)) { it }
                        },
                    ) { detail ->
                        CompositionLocalProvider(LocalNavAnimatedContentScope provides this) {
                            Box(Modifier.fillMaxSize().background(Color.White)) {
                                CupertinoTopAppBar(
                                    modifier = Modifier.offset(y = if (detail) detailOffset.dp else 0.dp),
                                    title = {},
                                    windowInsets = WindowInsets(0),
                                    isBackgroundAdaptive = false,
                                    actions = {
                                        Box(Modifier.size(48.dp).background(if (detail) Color.Red else Color.Blue))
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
        waitForIdle()
        mainClock.autoAdvance = false
        for (fraction in listOf(0.01f, 0.05f, 0.15f, 0.35f, 0.65f, 0.85f, 0.35f, 0.05f)) {
            runOnIdle { animationScope.launch { state.seekTo(fraction, false) } }
            mainClock.advanceTimeByFrame()
            waitForIdle()
            val pixels = onRoot().captureToImage().toPixelMap()
            for (y in listOf(1 + detailOffset, 24 + detailOffset, 46 + detailOffset)) {
                val color = pixels[288, y]
                assertTrue(color.red > 0.95f && color.green < 0.05f && color.blue < 0.05f,
                    "스와이프 진행도 $fraction 에서 기존 액션이 원래 영역에 남아 있어야 합니다: y=$y, 색상=$color")
            }
            if (detailOffset > 0) {
                val incoming = pixels[288, 1]
                assertTrue(incoming.red > 0.95f && incoming.green > 0.95f && incoming.blue > 0.95f,
                    "스와이프 진행도 $fraction 에서 완료 전에는 다음 화면의 액션이 미리 나타나면 안 됩니다: $incoming")
            }
        }
        runOnIdle { animationScope.launch { state.snapTo(true) } }
        mainClock.autoAdvance = true
        waitForIdle()
        val color = onRoot().captureToImage().toPixelMap()[288, 24]
        assertTrue(color.red > 0.95f && color.blue < 0.05f, "스와이프 취소 후 새로고침 액션이 완전히 복원되어야 합니다: $color")
    }

    @Test
    fun actionsRemainOpaqueUntilPredictiveBackIsCommitted() = verifyCommittedActions(cancel = true)

    @Test
    fun actionsAnimateOnlyAtTheEndOfCommittedBack() = verifyCommittedActions(cancel = false)

    @Test
    fun actionsAnimateAtTheEndOfAlmostCompletedBack() = verifyCommittedActions(cancel = false, progress = 0.98f)

    @Test
    fun incomingActionsFadeInWhenPreviousScreenHasNoActions() =
        verifyCommittedActions(cancel = false, detailHasActions = false)

    @Test
    fun incomingActionsFadeInAfterNearlyCompletedBack() =
        verifyCommittedActions(cancel = false, progress = 0.98f, detailHasActions = false)

    private fun verifyCommittedActions(
        cancel: Boolean,
        progress: Float = 0.5f,
        detailHasActions: Boolean = true,
    ) = runComposeUiTest {
        val dispatcher = NavigationEventDispatcher()
        val owner = object : NavigationEventDispatcherOwner {
            override val navigationEventDispatcher = dispatcher
        }
        val input = DirectNavigationEventInput()
        dispatcher.addInput(input)
        val stack = NavBackStack<NavKey>(Root, Detail)
        lateinit var screenTransition: Transition<*>
        setContent {
            CompositionLocalProvider(LocalNavigationEventDispatcherOwner provides owner) {
                SharedTransitionLayout {
                    ProvideNavigationTopBarTransition(this) {
                        NavDisplay(
                            backStack = stack,
                            modifier = Modifier.size(320.dp, 480.dp),
                            sharedTransitionScope = this@SharedTransitionLayout,
                            popTransitionSpec = {
                                slideInHorizontally(tween(1200)) { -it / 3 } togetherWith
                                    slideOutHorizontally(tween(1200)) { it }
                            },
                            predictivePopTransitionSpec = {
                                slideInHorizontally(tween(1200)) { -it / 3 } togetherWith
                                    slideOutHorizontally(tween(1200)) { it }
                            },
                        ) { key ->
                            NavEntry(key) {
                                screenTransition = LocalNavAnimatedContentScope.current.transition.parentTransition!!
                                Box(Modifier.fillMaxSize().background(Color.White)) {
                                    CupertinoTopAppBar(
                                        title = {},
                                        windowInsets = WindowInsets(0),
                                        isBackgroundAdaptive = false,
                                        actions = if (key == Root || detailHasActions) {
                                            { Box(Modifier.size(48.dp).background(if (key == Detail) Color.Red else Color.Blue)) }
                                        } else null,
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
        runOnIdle { input.backStarted(NavigationEvent(swipeEdge = NavigationEvent.EDGE_LEFT)) }
        for (fraction in if (cancel) listOf(0.05f, 0.35f, 0.75f, 0.98f) else listOf(progress)) {
            runOnIdle { input.backProgressed(NavigationEvent(swipeEdge = NavigationEvent.EDGE_LEFT, progress = fraction)) }
            mainClock.advanceTimeBy(160)
            waitForIdle()
            val color = onRoot().captureToImage().toPixelMap()[288, 24]
            if (detailHasActions) {
                assertTrue(color.red > 0.95f && color.green < 0.05f && color.blue < 0.05f,
                    "스와이프를 잡고 있는 동안 액션은 완전히 유지되어야 합니다: 진행도=$fraction, 색상=$color")
            } else {
                assertTrue(color.red > 0.95f && color.green > 0.95f && color.blue > 0.95f,
                    "스와이프 완료 전에는 새 액션을 미리 표시하면 안 됩니다: $color")
            }
        }
        if (cancel) {
            runOnIdle { input.backCancelled() }
            mainClock.advanceTimeBy(1500)
            waitForIdle()
            val color = onRoot().captureToImage().toPixelMap()[288, 24]
            assertTrue(color.red > 0.95f && color.blue < 0.05f, "취소하면 기존 액션을 유지해야 합니다: $color")
        } else {
            runOnIdle { input.backCompleted() }
            var heldFrames = 0
            var mixedFrames = 0
            var outgoingFadeFrames = 0
            var appearingFrames = 0
            var scaledIncomingFrames = 0
            var scaledOutgoingFrames = 0
            var firstAppearance: Float? = null
            var previousAppearance = 0f
            repeat(65) {
                mainClock.advanceTimeByFrame()
                waitForIdle()
                val pixels = onRoot().captureToImage().toPixelMap()
                val color = pixels[288, 24]
                val outside = pixels[262, 24]
                if (outside.blue > outside.red + 0.05f) scaledIncomingFrames++
                if (outside.red > outside.green + 0.05f) scaledOutgoingFrames++
                if (!detailHasActions) {
                    val alpha = 1f - color.red
                    if (screenTransition.currentState != screenTransition.targetState) {
                        assertTrue(alpha < 0.05f, "화면 전환이 끝나기 전에는 새 액션이 나타나면 안 됩니다: $alpha")
                    }
                    assertTrue(alpha + 0.04f >= previousAppearance,
                        "화면 전환이 끝나도 진입 페이드가 다시 초기화되면 안 됩니다: $previousAppearance -> $alpha")
                    previousAppearance = alpha
                    if (alpha > 0.05f && firstAppearance == null) firstAppearance = alpha
                    if (alpha in 0.05f..0.95f) appearingFrames++
                }
                if (color.red > 0.95f && color.blue < 0.05f) heldFrames++
                if (color.red > color.green + 0.1f && color.blue > color.green + 0.1f) mixedFrames++
                if (color.red > color.green + 0.1f && color.green > 0.05f) outgoingFadeFrames++
            }
            if (detailHasActions && progress < 0.9f) {
                assertTrue(heldFrames >= 10, "뒤로가기 확정 후에도 전환 마지막까지 기존 액션을 유지해야 합니다: $heldFrames")
            }
            if (!detailHasActions) {
                assertTrue(appearingFrames >= 8, "새 액션은 바로 표시되지 않고 중간 투명도를 거쳐 나타나야 합니다: $appearingFrames")
                assertTrue(scaledIncomingFrames >= 3, "새 액션은 큰 크기에서 원래 크기로 줄어들어야 합니다: $scaledIncomingFrames")
                assertTrue((firstAppearance ?: 1f) < 0.95f, "첫 표시 프레임부터 완전히 불투명하면 안 됩니다: $firstAppearance")
            } else if (progress < 0.9f) {
                assertTrue(outgoingFadeFrames in 3..20, "기존 액션은 마지막에만 짧게 페이드아웃해야 합니다: $outgoingFadeFrames")
                assertTrue(scaledOutgoingFrames >= 3, "기존 액션은 커지면서 사라져야 합니다: $scaledOutgoingFrames")
                assertTrue(mixedFrames <= 20, "두 액션이 오래 겹쳐 보이면 안 됩니다: $mixedFrames")
            } else {
                assertTrue(mixedFrames <= 12, "남은 전환 시간보다 긴 페이드를 추가하면 안 됩니다: $mixedFrames")
            }
            val color = onRoot().captureToImage().toPixelMap()[288, 24]
            assertTrue(color.blue > 0.95f && color.red < 0.05f, "복귀한 화면의 액션으로 교체되어야 합니다: $color")
        }
        mainClock.autoAdvance = true
    }

    @Test
    fun emptyNavigationSlotDoesNotMoveOutgoingButtonDown() = verifyEmptySlotPosition(actions = false)

    @Test
    fun emptyActionsSlotDoesNotMoveOutgoingButtonsDown() = verifyEmptySlotPosition(actions = true)

    private fun verifyEmptySlotPosition(actions: Boolean) = runComposeUiTest {
        val stack = NavBackStack<NavKey>(Root, Detail)
        setContent {
            SharedTransitionLayout {
                ProvideNavigationTopBarTransition(this) {
                    NavDisplay(
                        backStack = stack,
                        modifier = Modifier.size(320.dp, 480.dp),
                        sharedTransitionScope = this@SharedTransitionLayout,
                        popTransitionSpec = {
                            slideInHorizontally(tween(1000)) { -it / 3 } togetherWith
                                slideOutHorizontally(tween(1000)) { it }
                        },
                    ) { key ->
                        NavEntry(key) {
                            Box(Modifier.fillMaxSize().background(Color.White)) {
                                val button: @Composable () -> Unit = {
                                    if (key == Detail) Box(Modifier.size(40.dp).background(Color.Red))
                                }
                                CupertinoTopAppBar(
                                    title = {},
                                    windowInsets = WindowInsets(0),
                                    isBackgroundAdaptive = false,
                                    navigationIcon = if (!actions && key == Detail) button else null,
                                    actions = if (actions && key == Detail) ({ button() }) else null,
                                )
                            }
                        }
                    }
                }
            }
        }
        waitForIdle()
        val x = if (actions) 288 else 32
        val initial = onRoot().captureToImage().toPixelMap()
        val top = (0 until initial.height).first { initial[x, it].red > initial[x, it].green + 0.2f }
        mainClock.autoAdvance = false
        runOnIdle { stack.removeAt(stack.lastIndex) }
        repeat(4) {
            mainClock.advanceTimeByFrame()
            waitForIdle()
            val pixels = onRoot().captureToImage().toPixelMap()
            val color = pixels[x, top]
            assertTrue(color.red > color.green + 0.02f,
                "빈 영역으로 돌아가도 버튼의 위쪽 경계는 아래로 움직이면 안 됩니다: 프레임=$it, 색상=$color")
        }
        mainClock.autoAdvance = true
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
                                                    navigationIcon = if (!actions && (matched || key == Detail)) button else null,
                                                    actions = if (actions && (matched || key == Detail)) ({ button() }) else null,
                                                )
                                            }
                                        } else {
                                            CupertinoTopAppBar(
                                                title = {},
                                                windowInsets = WindowInsets(0),
                                                isBackgroundAdaptive = false,
                                                navigationIcon = if (!actions && (matched || key == Detail)) button else null,
                                                actions = if (actions && (matched || key == Detail)) ({ button() }) else null,
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
        mainClock.advanceTimeBy(900)
        waitForIdle()
        val pushed = onRoot().captureToImage().toPixelMap()
        val x = if (actions) 288 else 32
        val y = 28
        assertTrue(pushed[x, y].red > pushed[x, y].green + 0.2f, "진입 중 상단 버튼이 본문과 함께 밀리면 안 됩니다")
        if (actions) {
            val width = (0 until pushed.width).count { pushed[it, y].red > pushed[it, y].green + 0.2f }
            assertTrue(width == 40 || width == 80, "각 액션은 중간 너비로 줄어들지 않고 원래 너비를 유지해야 합니다: $width")
        }
        mainClock.advanceTimeBy(1200)
        waitForIdle()
        if (actions) {
            val visible = onRoot().captureToImage().toPixelMap()
            val width = (0 until visible.width).count { visible[it, y].red > visible[it, y].green + 0.2f }
            assertTrue(width == 80, "진입이 끝나면 새 액션의 전체 너비를 표시해야 합니다: $width")
        }
        runOnIdle { stack.removeAt(stack.lastIndex) }
        mainClock.advanceTimeBy(900)
        waitForIdle()
        val popped = onRoot().captureToImage().toPixelMap()
        if (matched) {
            assertTrue(popped[x, y].red > popped[x, y].green + 0.05f, "복귀 중 상단 버튼은 가장자리 위치를 유지해야 합니다")
        }
        mainClock.advanceTimeBy(1400)
        mainClock.autoAdvance = true
        waitForIdle()
        val settled = onRoot().captureToImage().toPixelMap()
        assertTrue(if (matched) settled[x, y].green < 0.2f else settled[x, y].green > 0.95f)
    }
}
