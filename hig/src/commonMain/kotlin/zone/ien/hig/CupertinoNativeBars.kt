package zone.ien.hig

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

/**
 * 화면의 내비게이션 바(와 탭 바·툴바)를 플랫폼이 직접 그릴 때, 그 바들이 차지하는 콘텐츠 여백입니다.
 *
 * 예: iOS에서 UIKit `UINavigationController`가 띄운 Compose 화면. 바는 UIKit이 그리고 Compose는 그 아래
 * 콘텐츠만 그립니다. 앱은 이런 화면을 감싸는 곳에서 이 값을 제공합니다.
 *
 * ```
 * CompositionLocalProvider(LocalCupertinoNativeBarsPadding provides nativeBarsPadding) { screen() }
 * ```
 *
 * 기본값 `null`이면 지금처럼 Compose가 바를 그립니다. 값이 있으면 다음처럼 동작합니다.
 * - [CupertinoTopAppBar]와 [CupertinoNavigationTitle]은 그리지 않습니다. 제목과 버튼은 플랫폼 바가 보입니다.
 * - [CupertinoScaffold]는 `topBar`를 그리지 않고 `hasNavigationTitle`을 끕니다. 콘텐츠 여백은
 *   [withCupertinoNativeBars] 규칙을 따릅니다. `contentWindowInsets`가 여백을 두는 변은 이 값으로 바꾸고,
 *   0인 변은 그대로 0입니다(`bottomBar`가 있으면 아래 여백은 그 높이). 그래서 여백 없는 틀로 쓰는 스캐폴드
 *   (`contentWindowInsets = WindowInsets(0)`)는 여백을 더하지 않습니다.
 *   이 값에는 키보드가 가린 영역도 들어 있으므로, 콘텐츠 안에서는 키보드 인셋을 소비한 것으로 둡니다.
 *   그래서 콘텐츠의 `imePadding()`은 0이 됩니다.
 * - [cupertinoScrollEdge]는 아무 일도 하지 않습니다. 스크롤 가장자리 효과는 플랫폼 바가 그립니다.
 * - 시트([CupertinoBottomSheetContent], [CupertinoBottomSheetScaffold]의 시트) 안에서는 `null`로 되돌립니다.
 *   시트는 자기 상단 바를 그립니다.
 *
 * 값은 레이아웃 시점에 읽습니다. 큰 제목이 접히는 동안처럼 여백이 프레임마다 바뀌면, 같은 [PaddingValues]
 * 객체가 최신 값을 돌려주게 합니다. 그러면 콘텐츠는 다시 그리지 않고 다시 배치만 됩니다.
 */
val LocalCupertinoNativeBarsPadding: ProvidableCompositionLocal<PaddingValues?> =
    staticCompositionLocalOf { null }

/**
 * 이 인셋을 플랫폼 내비게이션 바([LocalCupertinoNativeBarsPadding])에 맞춘 값입니다. 스캐폴드처럼 콘텐츠 여백을
 * 받는 컴포넌트를 만들 때 `contentWindowInsets`에 씁니다.
 *
 * - 플랫폼 바가 없으면 이 인셋 그대로입니다.
 * - 있으면 이 인셋이 여백을 두는 변(0보다 큰 변)은 플랫폼 바 여백으로 바꾸고, 0인 변은 그대로 0입니다.
 *   그래서 기본값(`WindowInsets.systemBars`)은 시스템 바 대신 플랫폼 바 여백이 되고, 여백 없는 틀로 쓰는
 *   `WindowInsets(0)`은 그대로 0입니다.
 *
 * 값은 레이아웃 시점에 읽습니다.
 */
@Composable
fun WindowInsets.withCupertinoNativeBars(): WindowInsets {
    val padding = LocalCupertinoNativeBarsPadding.current ?: return this
    return remember(padding, this) { NativeBarsWindowInsets(padding, requested = this) }
}

/** [requested]가 여백을 두는 변만 [padding]으로 바꾼 [WindowInsets]입니다. 레이아웃 시점에 읽습니다. */
@Stable
private class NativeBarsWindowInsets(
    private val padding: PaddingValues,
    private val requested: WindowInsets,
) : WindowInsets {
    override fun getTop(density: Density): Int =
        if (requested.getTop(density) > 0) with(density) { padding.calculateTopPadding().roundToPx() } else 0

    override fun getBottom(density: Density): Int =
        if (requested.getBottom(density) > 0) with(density) { padding.calculateBottomPadding().roundToPx() } else 0

    override fun getLeft(density: Density, layoutDirection: LayoutDirection): Int =
        if (requested.getLeft(density, layoutDirection) > 0) {
            with(density) { padding.calculateLeftPadding(layoutDirection).roundToPx() }
        } else {
            0
        }

    override fun getRight(density: Density, layoutDirection: LayoutDirection): Int =
        if (requested.getRight(density, layoutDirection) > 0) {
            with(density) { padding.calculateRightPadding(layoutDirection).roundToPx() }
        } else {
            0
        }

    override fun equals(other: Any?): Boolean =
        other is NativeBarsWindowInsets && other.padding === padding && other.requested == requested

    override fun hashCode(): Int = 31 * padding.hashCode() + requested.hashCode()
}

/**
 * 플랫폼 바가 있을 때 [CupertinoScaffold] 콘텐츠가 받는 여백입니다. 한 객체가 레이아웃 시점에 [insets]
 * (플랫폼 바 여백에서 바깥이 소비한 만큼을 뺀 값)를 읽어, 여백이 바뀌어도 콘텐츠를 다시 그리지 않습니다.
 */
@Stable
internal class NativeBarsContentPadding(
    private val insets: WindowInsets,
    private val screenDensity: Density,
) : PaddingValues {
    /** 스캐폴드 하단 바의 높이(px). 없으면 `null`이고 아래 여백은 [insets]를 따릅니다. */
    var bottomBarHeight: Int? by mutableStateOf(null)

    override fun calculateTopPadding(): Dp = insets.getTop(screenDensity).pxToDp()

    override fun calculateBottomPadding(): Dp = (bottomBarHeight ?: insets.getBottom(screenDensity)).pxToDp()

    override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp =
        insets.getLeft(screenDensity, layoutDirection).pxToDp()

    override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp =
        insets.getRight(screenDensity, layoutDirection).pxToDp()

    private fun Int.pxToDp(): Dp = with(screenDensity) { this@pxToDp.toDp() }
}
