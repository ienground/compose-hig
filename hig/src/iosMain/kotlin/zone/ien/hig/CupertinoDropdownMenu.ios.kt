package zone.ien.hig

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.uikit.LocalUIViewController
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.window.PopupProperties
import com.kyant.backdrop.Backdrop

@Composable
@ExperimentalCupertinoApi
actual fun CupertinoDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier,
    offset: DpOffset,
    paddingValues: PaddingValues,
    containerColor: Color,
    width: Dp,
    scrollState: ScrollState,
    properties: PopupProperties,
    backdrop: Backdrop,
    content: @Composable (CupertinoMenuScope.() -> Unit)
) {
    val density = LocalDensity.current
    val viewController = LocalUIViewController.current


}