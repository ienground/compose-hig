package zone.ien.hig

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.uikit.LocalUIViewController
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.window.PopupProperties
import com.kyant.backdrop.Backdrop
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGPoint
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UIAction
import platform.UIKit.UIButton
import platform.UIKit.UIButtonTypePlain
import platform.UIKit.UIColor
import platform.UIKit.UIContextMenuConfiguration
import platform.UIKit.UIContextMenuConfigurationElementOrderFixed
import platform.UIKit.UIContextMenuInteraction
import platform.UIKit.UIContextMenuInteractionAnimatingProtocol
import platform.UIKit.UIContextMenuInteractionDelegateProtocol
import platform.UIKit.UIControlStateNormal
import platform.UIKit.UIDevice
import platform.UIKit.UIEvent
import platform.UIKit.UIGestureRecognizer
import platform.UIKit.UIMenu
import platform.UIKit.UIMenuElement
import platform.UIKit.UIMenuElementAttributesDestructive
import platform.UIKit.UIMenuElementAttributesDisabled
import platform.UIKit.UIMenuOptionsDestructive
import platform.UIKit.UIMenuOptionsDisplayAsPalette
import platform.UIKit.UIMenuOptionsDisplayInline
import platform.UIKit.UIMenuOptionsSingleSelection
import platform.UIKit.addInteraction
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

    // Insert UIButton at the bottom of the view hierarchy
    DisposableEffect(viewController) {
        val button = UIButton.buttonWithType(UIButtonTypePlain).apply {
            backgroundColor = UIColor.clearColor
            setTitle("", forState = UIControlStateNormal)
            showsMenuAsPrimaryAction = true
            preferredMenuElementOrder = UIContextMenuConfigurationElementOrderFixed
            addInteraction(UIContextMenuInteraction(delegate))
        }
        viewController.view.insertSubview(button, atIndex = 0)
        delegate.button = button

        onDispose {
            button.removeFromSuperview()
            delegate.button = null
        }
    }

    // Refresh menu when items/sections change
    LaunchedEffect(items, sections) {
        delegate.button?.menu = delegate.buildMenu()
    }

    // Show menu when expanded changes
    LaunchedEffect(expanded) {
        val button = delegate.button ?: return@LaunchedEffect
        if (!expanded) return@LaunchedEffect

        delegate.isMenuVisible = true // Prevent frame updates while menu is visible
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
    }

    Box(
        modifier = modifier
            .onGloballyPositioned { coords ->
                if (delegate.isMenuVisible) return@onGloballyPositioned 

                val d = density.density
                val pos = coords.positionInRoot()

                val newX = kotlin.math.round(pos.x.toDouble() / d)
                val newY = kotlin.math.round(pos.y.toDouble() / d)
                val newW = kotlin.math.round(coords.size.width.toDouble() / d)
                val newH = kotlin.math.round(coords.size.height.toDouble() / d)

                delegate.button?.let { btn ->
                    btn.frame.useContents {
                        if (kotlin.math.abs(origin.x - newX) >= 1.0 || 
                            kotlin.math.abs(origin.y - newY) >= 1.0 ||
                            kotlin.math.abs(size.width - newW) >= 1.0 ||
                            kotlin.math.abs(size.height - newH) >= 1.0) {
                            btn.setFrame(CGRectMake(newX, newY, newW, newH))
                        }
                    }
                }
            }
    )
}

@OptIn(ExperimentalForeignApi::class)
internal class CupertinoDropdownMenuDelegate : NSObject(), UIContextMenuInteractionDelegateProtocol {
    var items: List<CupertinoMenuItemData> = emptyList()
    var sections: List<CupertinoMenuSectionData> = emptyList()
    var onDismissRequest: () -> Unit = {}
    var button: UIButton? = null
    var isMenuVisible: Boolean = false

    override fun contextMenuInteraction(
        interaction: UIContextMenuInteraction,
        configurationForMenuAtLocation: CValue<CGPoint>
    ): UIContextMenuConfiguration? = null

    override fun contextMenuInteraction(
        interaction: UIContextMenuInteraction,
        willEndForConfiguration: UIContextMenuConfiguration,
        animator: UIContextMenuInteractionAnimatingProtocol?
    ) {
        isMenuVisible = false
        onDismissRequest()
    }

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
    if (isDestructive) attributes = attributes or platform.UIKit.UIMenuElementAttributesDestructive
    if (!enabled) attributes = attributes or platform.UIKit.UIMenuElementAttributesDisabled
    action.attributes = attributes
    return action
}

private fun isPerformPrimaryActionAvailable(): Boolean {
    val systemVersion = UIDevice.currentDevice.systemVersion
    val major = systemVersion.split(".").firstOrNull()?.toIntOrNull() ?: 0
    val minor = systemVersion.split(".").getOrNull(1)?.toIntOrNull() ?: 0
    return major > 17 || (major == 17 && minor >= 4)
}
