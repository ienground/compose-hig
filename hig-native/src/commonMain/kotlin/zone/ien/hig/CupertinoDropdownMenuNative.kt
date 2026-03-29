package zone.ien.hig

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.kyant.backdrop.Backdrop

data class CupertinoMenuItemData(
    val title: String,
    val icon: Painter? = null,
    val isDestructive: Boolean = false,
    val isDisabled: Boolean = false,
    val onClick: () -> Unit,
)

data class CupertinoMenuSectionData(
    val title: String = "",
    val items: List<CupertinoMenuItemData>,
)

@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalCupertinoApi
@Composable
expect fun CupertinoDropdownMenuNative(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    paddingValues: PaddingValues = CupertinoDropdownMenuDefaults.PaddingValues,
    containerColor: Color = CupertinoDropdownMenuDefaults.ContainerColor,
    width: Dp = CupertinoDropdownMenuDefaults.DefaultWidth,
    scrollState: ScrollState = rememberScrollState(),
    properties: PopupProperties = PopupProperties(focusable = true, clippingEnabled = false),
    backdrop: Backdrop,
    items: List<CupertinoMenuItemData> = listOf(),
    sections: List<CupertinoMenuSectionData> = listOf(),
)