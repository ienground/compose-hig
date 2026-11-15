package zone.ien.hig.adaptive

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.ui.LocalNavAnimatedContentScope
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
        val state = rememberSharedContentState(this@NavigationTopBarTransition to slot)
        val transition = visibilityScope.transition
        val isRunning = { transition.currentState != transition.targetState }
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
