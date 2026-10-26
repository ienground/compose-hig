package zone.ien.hig

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.uikit.LocalUIViewController
import androidx.compose.ui.unit.dp
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.UIKit.UIView
import platform.UIKit.UIViewAutoresizingFlexibleWidth
import platform.UIKit.UIViewAutoresizingFlexibleHeight
import zone.ien.hig.uikit.HIGStatusRegion
import zone.ien.hig.uikit.HIGObserveHinge
import zone.ien.hig.uikit.HIGRemoveHingeObserver

@OptIn(ExperimentalForeignApi::class)
@Composable
internal actual fun rememberCupertinoVerticalBarPlacement(): State<CupertinoVerticalBarPlacement?> {
    val controller = LocalUIViewController.current
    val placement = remember(controller) { mutableStateOf<CupertinoVerticalBarPlacement?>(null) }
    DisposableEffect(controller) {
        val parent = controller.view
        var hasHinge = false
        val observer = object : UIView(parent.bounds) {
            override fun layoutSubviews() {
                super.layoutSubviews()
                updatePlacement()
            }

            override fun safeAreaInsetsDidChange() {
                super.safeAreaInsetsDidChange()
                updatePlacement()
            }

            override fun didMoveToWindow() {
                super.didMoveToWindow()
                updatePlacement()
            }

            private fun updatePlacement() {
                placement.value = HIGStatusRegion(parent).useContents {
                    val hasVerticalSafeArea = hasHinge && parent.safeAreaInsets.useContents { right > 0.0 }
                    if (!hasVerticalSafeArea && (size.height <= size.width || size.width <= 0.0)) {
                        return@useContents if (hasHinge) CupertinoVerticalBarPlacement(
                            width = 0.dp,
                            top = (size.height / 2 - 22.0).coerceAtLeast(0.0).dp,
                            bottom = 0.dp,
                            horizontalStatusWidth = (size.width + 8.0).dp,
                        ) else null
                    }
                    val statusTop = origin.y
                    val statusBottom = origin.y + size.height
                    val statusWidth = size.width
                    val viewHeight = parent.bounds.useContents { size.height }
                    parent.safeAreaInsets.useContents {
                        CupertinoVerticalBarPlacement(
                            width = maxOf(statusWidth, right, 64.0).dp,
                            top = (if (statusTop < viewHeight / 2) statusBottom + 8.0 else top + 8.0).dp,
                            bottom = (if (statusTop >= viewHeight / 2) viewHeight - statusTop + 8.0 else bottom + 16.0).dp,
                        )
                    }
                }
            }
        }.apply {
            userInteractionEnabled = false
            autoresizingMask = UIViewAutoresizingFlexibleWidth or UIViewAutoresizingFlexibleHeight
        }
        parent.addSubview(observer)
        val hingeObserver = HIGObserveHinge(parent) {
            hasHinge = it
            observer.setNeedsLayout()
        }
        onDispose {
            HIGRemoveHingeObserver(parent, hingeObserver)
            observer.removeFromSuperview()
        }
    }
    return placement
}
