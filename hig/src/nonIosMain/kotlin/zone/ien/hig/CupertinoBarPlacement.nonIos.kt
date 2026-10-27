package zone.ien.hig

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState

@Composable
internal actual fun rememberCupertinoVerticalBarPlacement(): State<CupertinoVerticalBarPlacement?> =
    rememberUpdatedState(null)
