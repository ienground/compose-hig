package zone.ien.hig

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.uikit.LocalUIViewController
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.window.PopupProperties
import com.kyant.backdrop.Backdrop
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGPointMake
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSAttributedString
import platform.Foundation.NSMakeRange
import platform.Foundation.NSMutableAttributedString
import platform.Foundation.NSMutableAttributedStringMeta
import platform.Foundation.create
import platform.Foundation.setAttributedString
import platform.UIKit.NSForegroundColorAttributeName
import platform.UIKit.UIAction
import platform.UIKit.UIButton
import platform.UIKit.UIButtonTypePlain
import platform.UIKit.UIColor
import platform.UIKit.UIContextMenuConfigurationElementOrder
import platform.UIKit.UIContextMenuConfigurationElementOrderFixed
import platform.UIKit.UIControlStateNormal
import platform.UIKit.UIDevice
import platform.UIKit.UIEvent
import platform.UIKit.UIGestureRecognizer
import platform.UIKit.UIImage
import platform.UIKit.UIMenu
import platform.UIKit.UIMenuElement
import platform.UIKit.UIMenuElementAttributesDestructive
import platform.UIKit.UIMenuElementAttributesDisabled
import platform.UIKit.UIMenuOptionsDestructive
import platform.UIKit.UIMenuOptionsDisplayAsPalette
import platform.UIKit.UIMenuOptionsDisplayInline
import platform.UIKit.UIMenuOptionsSingleSelection
import platform.UIKit.UIMenuOptionsVar
import platform.UIKit.secondaryLabelColor
import platform.UIKit.touchesBegan
import platform.darwin.NSObject

fun HigMenuOptions.toUIMenuOptions() = when (this) {
    HigMenuOptions.DisplayInline -> UIMenuOptionsDisplayInline
    HigMenuOptions.SingleSelection -> UIMenuOptionsSingleSelection
    HigMenuOptions.DisplayAsPalette -> UIMenuOptionsDisplayAsPalette
    HigMenuOptions.Destructive -> UIMenuOptionsDestructive
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@ExperimentalCupertinoApi
@Composable
actual fun CupertinoDropdownMenuNative(
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
    items: List<CupertinoMenuItemData>,
    sections: List<CupertinoMenuSectionData>
) {
    val density = LocalDensity.current
    val viewController = LocalUIViewController.current

    val delegate = remember { CupertinoDropdownMenuDelegate() }
    delegate.items = items
    delegate.sections = sections
    delegate.onDismissRequest = onDismissRequest

    var posX by remember { mutableStateOf(0.0) }
    var posY by remember { mutableStateOf(0.0) }
    var btnW by remember { mutableStateOf(0.0) }
    var btnH by remember { mutableStateOf(0.0) }

    // Insert UIButton at the bottom of the view hierarchy (to prevent touch interception)
    DisposableEffect(viewController) {
        val button = UIButton.buttonWithType(UIButtonTypePlain).apply {
            backgroundColor = UIColor.clearColor
            setTitle("", forState = UIControlStateNormal)
            showsMenuAsPrimaryAction = true
            preferredMenuElementOrder = UIContextMenuConfigurationElementOrderFixed
        }
        viewController.view.insertSubview(button, atIndex = 0)
        delegate.button = button

        onDispose {
            button.removeFromSuperview()
            delegate.button = null
        }
    }

    // 버튼 위치/크기 동기화
    LaunchedEffect(posX, posY, btnW, btnH) {
        delegate.button?.setFrame(CGRectMake(posX, posY, btnW, btnH))
    }

    // 아이템/섹션 변경 시 메뉴 갱신
    LaunchedEffect(items, sections) {
        delegate.button?.menu = delegate.buildMenu()
    }

    // expanded 변경 → 메뉴 표시
    LaunchedEffect(expanded) {
        val button = delegate.button ?: return@LaunchedEffect
        if (!expanded) return@LaunchedEffect

        if (isPerformPrimaryActionAvailable()) {
            button.performPrimaryAction()
        } else {
            val gesture = button.gestureRecognizers
                .orEmpty()
                .filterIsInstance<UIGestureRecognizer>()
                .firstOrNull {
                    it.`class`()?.toString()
                        ?.contains("UITouchDownGestureRecognizer", ignoreCase = true) == true
                }
            gesture?.touchesBegan(emptySet<Nothing>(), UIEvent())
        }
        // 메뉴가 표시된 직후 Compose 측 상태를 닫힘으로 리셋
        onDismissRequest()
    }

    // Compose 레이아웃으로 버튼 위치 추적 (투명 Box)
    Box(
        modifier = modifier
            .onGloballyPositioned { coords ->
                // positionInWindow()는 Compose 윈도우 기준
                // → UIKit view 좌표계로 변환하려면 rootView 기준 offset을 빼야 함
                val rootView = viewController.view
                val windowOrigin = rootView.window
                    ?.convertPoint(
                        point = CGPointMake(0.0, 0.0),
                        fromView = rootView,
                    )

                val pos = coords.positionInWindow()
                val offsetY = windowOrigin?.useContents { y } ?: 0.0

                posX = pos.x.toDouble() / density.density
                // rootView가 window 기준으로 얼마나 내려와 있는지 보정
                posY = (pos.y.toDouble() / density.density) - offsetY
                btnW = coords.size.width.toDouble() / density.density
                btnH = coords.size.height.toDouble() / density.density
            }
    )
}

@OptIn(ExperimentalForeignApi::class)
internal class CupertinoDropdownMenuDelegate : NSObject() {
    var items: List<CupertinoMenuItemData> = emptyList()
    var sections: List<CupertinoMenuSectionData> = emptyList()
    var onDismissRequest: () -> Unit = {}
    var button: UIButton? = null

    fun buildMenu(): UIMenu {
        val topActions: List<UIMenuElement> = items.map { it.toUIAction() }
        val sectionMenus: List<UIMenuElement> = sections.map { section ->
            UIMenu.menuWithTitle(
                title = section.title,
                image = section.icon?.toUIImage()?.resized(20.0),
                identifier = null,
                options = section.options.toUIMenuOptions(),
                children = section.items.map { it.toUIAction() }
            )
        }

        return UIMenu.menuWithTitle(
            title = "",
            children = topActions + sectionMenus,
        )
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun CupertinoMenuItemData.toUIAction(): UIAction {
    val action = UIAction.actionWithTitle(
        title = title,
        image = icon?.toUIImage()?.resized(20.0),
        identifier = null,
    ) { _ -> onClick() }

    var attributes: ULong = 0u
    if (isDestructive) attributes = attributes or UIMenuElementAttributesDestructive
    if (!enabled) attributes = attributes or UIMenuElementAttributesDisabled
    action.attributes = attributes
    return action
}

private fun isPerformPrimaryActionAvailable(): Boolean {
    val systemVersion = UIDevice.currentDevice.systemVersion
    val major = systemVersion.split(".").firstOrNull()?.toIntOrNull() ?: 0
    val minor = systemVersion.split(".").getOrNull(1)?.toIntOrNull() ?: 0
    return major > 17 || (major == 17 && minor >= 4)
}
