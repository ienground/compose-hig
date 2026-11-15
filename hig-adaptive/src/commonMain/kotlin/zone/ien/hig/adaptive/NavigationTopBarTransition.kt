package zone.ien.hig.adaptive

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.State
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneInfo
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigationevent.NavigationEventTransitionState
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import zone.ien.hig.LocalTopBarTransition
import zone.ien.hig.TopBarTransition

/**
 * Navigation 3의 화면 전환에 Cupertino·Material 상단 버튼의 공유 전환을 연결합니다.
 *
 * [sharedTransitionScope]를 내부 NavDisplay의 sharedTransitionScope에도 전달해야 합니다.
 * 상단 바는 해당 NavDisplay의 entry 안에서 구성해야 합니다.
 *
 * @param sharedTransitionScope NavDisplay와 공유할 SharedTransitionLayout의 범위
 * @param content 내비게이션 디스플레이
 */
@Composable
fun ProvideNavigationTopBarTransition(
    sharedTransitionScope: SharedTransitionScope,
    content: @Composable () -> Unit,
) {
    val transition = remember(sharedTransitionScope) { NavigationTopBarTransition(sharedTransitionScope) }
    CompositionLocalProvider(LocalTopBarTransition provides transition, content = content)
}

internal class NavigationTopBarTransition(
    private val scope: SharedTransitionScope,
) : TopBarTransition {
    @Composable
    override fun modifier(slot: TopBarTransition.Slot): Modifier = with(scope) {
        val visibilityScope = LocalNavAnimatedContentScope.current
        val transition = visibilityScope.transition
        val isRunning = { transition.currentState != transition.targetState }
        if (slot == TopBarTransition.Slot.Actions) {
            val overlayZIndex = if (transition.currentState == EnterExitState.Visible) 2f else 1f
            return@with Modifier
                .renderInSharedTransitionScopeOverlay(renderInOverlay = isRunning, zIndexInOverlay = overlayZIndex)
                .skipToLookaheadPosition(enabled = isRunning)
                .then(navigationTopBarFade(visibilityScope))
        }
        val state = rememberSharedContentState(this@NavigationTopBarTransition to slot)
        Modifier
            .sharedBounds(
                sharedContentState = state,
                animatedVisibilityScope = visibilityScope,
                enter = EnterTransition.None,
                exit = ExitTransition.None,
                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                zIndexInOverlay = 1f,
            )
            .renderInSharedTransitionScopeOverlay(
                renderInOverlay = { !state.isMatchFound && isRunning() },
                zIndexInOverlay = 1f,
            )
            .skipToLookaheadPosition(enabled = { !state.isMatchFound && isRunning() })
            .then(navigationTopBarFade(visibilityScope))
    }
}

@Composable
internal expect fun navigationTopBarFade(scope: AnimatedVisibilityScope): Modifier

private const val TopBarFadeDurationMillis = 200
private const val TopBarAppearDurationMillis = 300
internal const val TopBarHiddenScale = 1.15f

/** 제스처 중에는 유지하고, 확정된 화면 전환 마지막에만 짧게 페이드합니다. */
@Composable
internal fun navigationTopBarOpacity(scope: AnimatedVisibilityScope): State<Float> {
    val transition = scope.transition
    val contentTransition = transition.parentTransition ?: transition
    val dispatcher = LocalNavigationEventDispatcherOwner.current?.navigationEventDispatcher
    val history = dispatcher?.history?.collectAsState()
    val gestureFlow = remember(dispatcher) {
        dispatcher?.transitionState?.map { it is NavigationEventTransitionState.InProgress }?.distinctUntilChanged()
    }
    val gestureInProgress = gestureFlow?.collectAsState(
        initial = dispatcher?.transitionState?.value is NavigationEventTransitionState.InProgress,
    )
    val fadeStarted = remember(contentTransition.currentState, contentTransition.targetState) { mutableStateOf(false) }
    val finishReady = remember(contentTransition, history, gestureInProgress) {
        derivedStateOf {
            val navigationHistory = history?.value
            val actualScene = navigationHistory?.let {
                (it.mergedHistory.getOrNull(it.currentIndex) as? SceneInfo<*>)?.scene
            }
            val targetScene = contentTransition.targetState as? Scene<*>
            val committed = actualScene == null || targetScene == null ||
                (actualScene::class == targetScene::class && actualScene.key == targetScene.key)
            val remaining = contentTransition.totalDurationNanos - contentTransition.playTimeNanos
            contentTransition.currentState != contentTransition.targetState &&
                gestureInProgress?.value != true && committed &&
                remaining <= TopBarFadeDurationMillis * 1_000_000L
        }
    }
    LaunchedEffect(finishReady.value) {
        if (finishReady.value) {
            // 초기 측정에서 본문 전환 길이가 등록된 뒤 종료 여부를 확인합니다.
            if (contentTransition.totalDurationNanos <= TopBarFadeDurationMillis * 1_000_000L) {
                withFrameNanos { }
            }
            if (finishReady.value) fadeStarted.value = true
        }
    }
    // 본문에 별도 애니메이션이 없어도 마지막 페이드를 위한 콘텐츠 수명을 유지합니다.
    transition.animateFloat(
        transitionSpec = { tween(durationMillis = TopBarFadeDurationMillis) },
        label = "TopBarLifetime",
    ) { if (it == EnterExitState.Visible) 1f else 0f }
    val waiting = contentTransition.currentState != contentTransition.targetState && !fadeStarted.value
    val opacity = transition.animateFloat(
        transitionSpec = { tween(durationMillis = TopBarFadeDurationMillis) },
        label = "TopBarOpacity",
    ) {
        val state = if (waiting) transition.currentState else it
        if (state == EnterExitState.Visible) 1f else 0f
    }
    // 새 화면의 버튼은 본문 전환이 완료된 뒤 제자리에서 나타납니다.
    val appearing = remember(transition) { Animatable(0f) }
    val showIncoming = transition.currentState == EnterExitState.Visible ||
        (contentTransition.currentState == contentTransition.targetState && transition.targetState == EnterExitState.Visible)
    LaunchedEffect(appearing, showIncoming) {
        appearing.animateTo(
            targetValue = if (showIncoming) 1f else 0f,
            animationSpec = tween(durationMillis = TopBarAppearDurationMillis),
        )
    }
    return remember(transition, contentTransition, opacity, appearing) {
        derivedStateOf {
            if (transition.targetState == EnterExitState.Visible) {
                appearing.value
            } else if (contentTransition.currentState == contentTransition.targetState) {
                0f
            } else {
                opacity.value * appearing.value
            }
        }
    }
}
