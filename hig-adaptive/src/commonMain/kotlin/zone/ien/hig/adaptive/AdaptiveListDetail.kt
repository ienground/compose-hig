package zone.ien.hig.adaptive

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldRole
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirectiveWithTwoPanesOnMediumWidth
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.LocalListDetailSceneScope
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import zone.ien.hig.LocalCupertinoRightSystemArea
import zone.ien.hig.cupertinoTween

/**
 * 중간 너비부터 목록·상세를 함께 표시하고 좁은 창에서도 같은 Scene을 유지합니다.
 * 목록·상세 역할, 선호 너비와 미선택 안내는 호출 앱의 entry 메타데이터로 지정합니다.
 * 기본 뒤로가기는 넓은 창에서도 선택한 상세를 제거하고 미선택 안내로 돌아갑니다.
 * [shouldHandleSinglePaneLayout]을 끄면 좁은 창에서는 NavDisplay의 기존 화면 전환을 사용합니다.
 * 이 경우 단일 화면과 분할 화면 사이에서 Scene이 변경되므로 유지할 상태는 rememberSaveable이나
 * 화면 상태 소유자에 보관해야 합니다.
 */
@ExperimentalMaterial3AdaptiveApi
@Composable
fun <T : Any> rememberAdaptiveListDetailSceneStrategy(
    directive: PaneScaffoldDirective = calculatePaneScaffoldDirectiveWithTwoPanesOnMediumWidth(
        currentWindowAdaptiveInfoV2(),
    ).copy(horizontalPartitionSpacerSize = 0.dp),
    backNavigationBehavior: BackNavigationBehavior = BackNavigationBehavior.PopLatest,
    shouldHandleSinglePaneLayout: Boolean = true,
): ListDetailSceneStrategy<T> = rememberListDetailSceneStrategy(
    shouldHandleSinglePaneLayout = shouldHandleSinglePaneLayout,
    backNavigationBehavior = backNavigationBehavior,
    directive = directive,
)

/** Cupertino 이징을 사용한 pane 페이드(160ms)와 위치·크기 전환(240ms) 메타데이터입니다. */
@ExperimentalMaterial3AdaptiveApi
fun adaptivePaneAnimation(
    enterTransition: EnterTransition = fadeIn(cupertinoTween(160)),
    exitTransition: ExitTransition = fadeOut(cupertinoTween(160)),
    boundsAnimationSpec: FiniteAnimationSpec<IntRect> = cupertinoTween(240),
): Map<String, Any> = ListDetailSceneStrategy.paneAnimation(
    enterTransition = enterTransition,
    exitTransition = exitTransition,
    boundsAnimationSpec = boundsAnimationSpec,
)

/** Scene의 존재 여부 대신 실제 목표 배치에서 목록과 상세가 함께 표시되는지 확인합니다. */
@ExperimentalMaterial3AdaptiveApi
val isAdaptiveListDetailExpanded: Boolean
    @Composable get() {
        val target = LocalListDetailSceneScope.current
            ?.scaffoldTransitionScope?.scaffoldStateTransition?.targetState ?: return false
        return target[ListDetailPaneScaffoldRole.List] != PaneAdaptedValue.Hidden &&
            target[ListDetailPaneScaffoldRole.Detail] != PaneAdaptedValue.Hidden
    }

/**
 * entry 콘텐츠에 역할별 시스템 영역 정책을 적용합니다.
 * 두 pane에서는 실제 오른쪽 pane에만 오른쪽 바와 인셋을 적용합니다.
 * 단일 pane은 기존 동작을 유지하며 RTL 배치도 반영합니다.
 */
@ExperimentalMaterial3AdaptiveApi
@Composable
fun AdaptiveListDetailPane(
    role: ThreePaneScaffoldRole,
    content: @Composable () -> Unit,
) {
    val rightRole = if (LocalLayoutDirection.current == LayoutDirection.Ltr)
        ListDetailPaneScaffoldRole.Detail else ListDetailPaneScaffoldRole.List
    val ownsRightSystemArea = !isAdaptiveListDetailExpanded || role == rightRole
    CompositionLocalProvider(
        LocalCupertinoRightSystemArea provides (LocalCupertinoRightSystemArea.current && ownsRightSystemArea),
        content = content,
    )
}
