package zone.ien.hig

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
internal data class CupertinoVerticalBarPlacement(
    val width: Dp,
    val top: Dp,
    val bottom: Dp,
    val horizontalStatusWidth: Dp = 0.dp,
    val centerFromRight: Dp = width / 2,
    val topBarInset: Dp = 0.dp,
)

@Composable
internal expect fun rememberCupertinoVerticalBarPlacement(): State<CupertinoVerticalBarPlacement?>

internal val LocalCupertinoVerticalBarPlacement =
    compositionLocalOf<CupertinoVerticalBarPlacement?> { null }

internal object CupertinoVerticalTopBar
internal object CupertinoVerticalBottomBar

internal val CupertinoVerticalBarControlWidth = 48.dp

internal val LocalCupertinoBarControlWidth = compositionLocalOf<Dp?> { null }

/** 현재 pane이 화면 오른쪽의 시스템 예약 영역에 닿는지 지정합니다. 기본값은 단일 화면입니다. */
val LocalCupertinoRightSystemArea = compositionLocalOf { true }

/**
 * [CupertinoScaffold]의 하단 슬롯에 넣은 커스텀 바를 Duo의 오른쪽 시스템 영역에 배치합니다.
 * 일반 화면에서는 전달받은 Modifier를 유지하며, Duo에서는 바의 중심과 하단 예약 영역을 반영합니다.
 * 하단 슬롯에 직접 배치하는 컴포저블의 루트에 적용합니다.
 */
@ExperimentalCupertinoApi
@Composable
fun Modifier.cupertinoScaffoldBottomBar(): Modifier {
    val placement = LocalCupertinoVerticalBarPlacement.current?.takeIf { it.width > 0.dp }
        ?: return this
    return this
        .layoutId(CupertinoVerticalBottomBar)
        .width(placement.width)
        .absoluteOffset(x = placement.width / 2 - placement.centerFromRight)
        .padding(bottom = placement.bottom)
}
