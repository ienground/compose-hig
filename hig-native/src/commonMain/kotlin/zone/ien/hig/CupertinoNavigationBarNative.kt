package zone.ien.hig

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import com.kyant.backdrop.backdrops.LayerBackdrop

data class CupertinoNavigationBarItemData(
    val onClick: () -> Unit,
    val icon: Painter,
    val selectedIcon: Painter? = null,
    val label: String
)

@OptIn(ExperimentalCupertinoApi::class)
@Composable
expect fun CupertinoNavigationBarNative(
    modifier: Modifier = Modifier,
    colors: CupertinoNavigationBarColors = CupertinoNavigationBarDefaults.colors(),
    windowInsets: WindowInsets = CupertinoNavigationBarDefaults.windowInsets,
    backdrop: LayerBackdrop,
    selectedTabIndex: () -> Int,
    onTabSelected: (index: Int) -> Unit,
    items: List<CupertinoNavigationBarItemData>
)