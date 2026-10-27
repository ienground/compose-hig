package zone.ien.hig

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.uikit.LocalUIViewController
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.LayerBackdrop
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.CGRectMake
import platform.UIKit.NSLayoutConstraint
import platform.UIKit.UITabBar
import zone.ien.hig.navigation.IosBarHeightCache
import zone.ien.hig.navigation.LocalIosTabBarPadding
import zone.ien.hig.navigation.TabBarManager

@Composable
internal fun SyncNativeTabBarItems(
    items: List<CupertinoNavigationBarItemData>,
    selectedTabIndex: () -> Int,
    tabBarManager: TabBarManager,
) {
    val selectedIndex = selectedTabIndex()
    LaunchedEffect(items, selectedIndex) {
        tabBarManager.setItems(items, selectedIndex)
    }
}

@OptIn(ExperimentalForeignApi::class)
internal class LayoutObservingTabBar : UITabBar(CGRectMake(0.0, 0.0, 0.0, 0.0)) {
    var onLayoutChanged: ((CValue<CGRect>) -> Unit)? = null

    private var lastFrame: List<Double>? = null

    override fun layoutSubviews() {
        super.layoutSubviews()
        val currentFrame = frame
        val frameValues = currentFrame.useContents {
            listOf(origin.x, origin.y, size.width, size.height)
        }
        if (lastFrame != frameValues) {
            lastFrame = frameValues
            onLayoutChanged?.invoke(currentFrame)
        }
    }

    override fun safeAreaInsetsDidChange() {
        super.safeAreaInsetsDidChange()
        onLayoutChanged?.invoke(frame)
    }
}

@OptIn(ExperimentalCupertinoApi::class, ExperimentalForeignApi::class)
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
    val density = LocalDensity.current
    val viewController = LocalUIViewController.current

    // Write the measured tab bar height to the CompositionLocal so AdaptiveScaffold
    // can automatically adjust content padding on iOS.
    val iosTabBarPaddingState = LocalIosTabBarPadding.current

    val tabBarView = remember {
        LayoutObservingTabBar().apply {
            translatesAutoresizingMaskIntoConstraints = false
        }
    }
    val isLiquidGlassEnabled = true // TODO: consider parameterize

    val onItemSelectedState by rememberUpdatedState(onTabSelected)

    val tabBarManager = remember(tabBarView) {
        TabBarManager(
            tabBar = tabBarView,
            onItemSelected = { onItemSelectedState(it) },
        )
    }

    SyncNativeTabBarItems(items, selectedTabIndex, tabBarManager)

    val cachedTabBarHeight = remember { IosBarHeightCache.lastTabBarHeight }

    var topLeft by remember { mutableStateOf(DpOffset.Zero) }
    var positionInRoot by remember { mutableStateOf(DpOffset.Zero) }
    var tabBarWidth by remember { mutableStateOf(0.dp) }
    var tabBarHeight by remember {
        val tabBarHeight = cachedTabBarHeight
        if (tabBarHeight.value > 0f) {
            iosTabBarPaddingState.value = PaddingValues(bottom = tabBarHeight)
        }
        mutableStateOf(tabBarHeight)
    }

    DisposableEffect(tabBarView, viewController) {
        tabBarView.onLayoutChanged = { frame ->
            frame.useContents {
                topLeft = DpOffset(x = origin.x.dp, y = origin.y.dp)
                tabBarWidth = size.width.dp

                val safeAreaBottom = if (isLiquidGlassEnabled) 0.dp else viewController.view.safeAreaInsets.useContents { bottom.dp }
                val newTabBarHeight = size.height.dp + safeAreaBottom

                IosBarHeightCache.updateTabBarHeight(size.height.dp)

                if (tabBarHeight != newTabBarHeight) {
                    tabBarHeight = newTabBarHeight
                    if (newTabBarHeight.value > 0f) {
                        iosTabBarPaddingState.value = PaddingValues(bottom = newTabBarHeight)
                    }
                }
            }
        }
        viewController.view.addSubview(tabBarView)

        NSLayoutConstraint.activateConstraints(
            listOf(
                tabBarView.leadingAnchor.constraintEqualToAnchor(viewController.view.leadingAnchor),
                tabBarView.trailingAnchor.constraintEqualToAnchor(viewController.view.trailingAnchor),
                tabBarView.bottomAnchor.constraintEqualToAnchor(
                    if (isLiquidGlassEnabled)
                        viewController.view.bottomAnchor
                    else
                        viewController.view.safeAreaLayoutGuide.bottomAnchor
                ),
            )
        )
        viewController.view.setNeedsLayout()
        viewController.view.layoutIfNeeded()

        onDispose {
            tabBarView.onLayoutChanged = null
            tabBarView.removeFromSuperview()
        }
    }

    // Clean up the padding when this composable leaves the composition
    DisposableEffect(Unit) {
        onDispose {
            iosTabBarPaddingState.value = PaddingValues()
        }
    }

    Box(
        modifier = Modifier
            .onPlaced {
                val positionInRootPx = it.positionInRoot()
                positionInRoot = with(density) {
                    DpOffset(
                        x = positionInRootPx.x.toDp(),
                        y = positionInRootPx.y.toDp(),
                    )
                }
            }
            .graphicsLayer {
                translationX = (topLeft.x - positionInRoot.x).toPx()
                translationY = (topLeft.y - positionInRoot.y).toPx()
            }
            .width(tabBarWidth)
            .height(tabBarHeight)
    )
}
