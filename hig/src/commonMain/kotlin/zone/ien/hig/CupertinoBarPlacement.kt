package zone.ien.hig

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
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
