package zone.ien.hig

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kyant.backdrop.backdrops.LayerBackdrop

@OptIn(markerClass = [ExperimentalCupertinoApi::class])
@Composable
actual fun CupertinoNavigationBarNative(
    modifier: Modifier,
    colors: CupertinoNavigationBarColors,
    windowInsets: WindowInsets,
    backdrop: LayerBackdrop,
    selectedTabIndex: () -> Int,
    onTabSelected: (index: Int) -> Unit,
    items: List<CupertinoNavigationBarItemData>
) {
    CupertinoNavigationBar(
        modifier = modifier,
        colors = colors,
        windowInsets = windowInsets,
        backdrop = backdrop,
        selectedTabIndex = selectedTabIndex,
        onTabSelected = onTabSelected,
        tabsCount = items.size
    ) {
        items.forEachIndexed { index, item ->
            CupertinoNavigationBarItem(
                onClick = item.onClick,
                icon = {
                    Icon(
                        painter =
                            if (index == selectedTabIndex() && item.selectedIcon != null) item.selectedIcon
                            else item.icon
                        ,
                        contentDescription = item.label,
                    )
                },
                label = { Text(text = item.label) },
            )
        }
    }
}