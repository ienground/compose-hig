package zone.ien.hig

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.DpSize
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UIAlertActionStyleCancel
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleAlert
import platform.UIKit.UIButton
import platform.UIKit.UIContextMenuInteraction
import platform.UIKit.UITabBar
import platform.UIKit.UITabBarItem
import platform.UIKit.UIMenu
import platform.UIKit.UIDatePicker
import platform.UIKit.UIDatePickerStyle
import platform.UIKit.contextMenuInteraction
import zone.ien.hig.navigation.TabBarManager
import zone.ien.hig.navigation.UITabBarDelegate

@OptIn(ExperimentalForeignApi::class, ExperimentalTestApi::class)
class CupertinoNavigationRegressionTest {
    @Test
    fun tabBarManagerClearsItemsAndCanSetNewItemsAfterward() {
        val tabBar = UITabBar()
        val manager = TabBarManager(tabBar) {}
        manager.setItems(listOf(tabItem("First")), selectedIndex = 0)

        manager.setItems(emptyList(), selectedIndex = 0)

        assertTrue(tabBar.items.isNullOrEmpty())
        assertNull(tabBar.selectedItem)

        manager.setItems(listOf(tabItem("Replacement")), selectedIndex = 0)

        assertEquals(1, tabBar.items?.size)
        assertEquals(0L, tabBar.selectedItem?.tag)
    }

    @Test
    fun tabBarSelectionInvokesSelectionAndItemCallbacksInOrder() {
        val tabBar = UITabBar()
        val callbackOrder = mutableListOf<String>()
        val manager =
            TabBarManager(tabBar) { index ->
                callbackOrder += "selected:$index"
            }
        manager.setItems(
            listOf(
                tabItem("First") { callbackOrder += "item" },
            ),
            selectedIndex = 0,
        )

        val nativeItem = assertNotNull(tabBar.items?.filterIsInstance<UITabBarItem>()?.singleOrNull())
        (assertNotNull(tabBar.delegate) as UITabBarDelegate).tabBar(tabBar, nativeItem)

        assertEquals(listOf("selected:0", "item"), callbackOrder)
    }

    @Test
    fun stableSelectionLambdaKeepsUIKitTabBarInSyncWithChangedIndex() = runComposeUiTest {
        val selectedIndex = mutableIntStateOf(0)
        val selectedTabIndex = { selectedIndex.intValue }
        val tabBar = UITabBar()
        val manager = TabBarManager(tabBar) {}
        val items = listOf(tabItem("First"), tabItem("Second"))

        setContent {
            SyncNativeTabBarItems(items, selectedTabIndex, manager)
        }

        runOnIdle { selectedIndex.intValue = 1 }
        runOnIdle { assertEquals(1L, tabBar.selectedItem?.tag) }
    }

    @Test
    fun supportedIOSVersionsBefore174RequireComposeDropdownFallback() {
        assertTrue(shouldUseComposeDropdownFallback(15, 3))
        assertTrue(shouldUseComposeDropdownFallback(17, 3))
        assertFalse(shouldUseComposeDropdownFallback(17, 4))
    }

    @Test
    fun externallyCollapsedNativeMenuClearsVisibilityAndDismissesUIKitInteraction() {
        val button = UIButton(frame = CGRectMake(0.0, 0.0, 40.0, 40.0)).apply {
            showsMenuAsPrimaryAction = true
            menu = UIMenu.menuWithTitle(title = "", children = emptyList<platform.UIKit.UIMenuElement>())
        }
        val delegate = CupertinoDropdownMenuDelegate().apply { isMenuVisible = true }

        val interaction: UIContextMenuInteraction? = button.contextMenuInteraction
        assertNotNull(interaction)
        dismissNativeDropdownMenu(button, delegate)

        assertFalse(delegate.isMenuVisible)
    }

    @Test
    fun nativeAlertDialogActionsUseUpdatedDefinitionsAndCallbacks() {
        var oldClickCount = 0
        var updatedClickCount = 0
        var dismissCount = 0
        val buttonState = NativeAlertDialogButtonsState { dismissCount++ }

        buttonState.update {
            action(
                onClick = { oldClickCount++ },
                title = "Old action",
            )
        }
        val initialAlert =
            UIAlertController.alertControllerWithTitle(
                title = null,
                message = null,
                preferredStyle = UIAlertControllerStyleAlert,
            ).apply {
                buttonState.createActions().forEach { addAction(it) }
            }

        assertEquals(
            "Old action",
            initialAlert.actions.filterIsInstance<UIAlertAction>().single().title,
        )

        buttonState.update {
            action(
                onClick = { updatedClickCount++ },
                style = AlertActionStyle.Cancel,
                enabled = false,
                title = "Updated action",
            )
        }
        val updatedAction = buttonState.createActions().single()

        assertEquals("Updated action", updatedAction.title)
        assertFalse(updatedAction.enabled)
        assertEquals(UIAlertActionStyleCancel, updatedAction.style)
        buttonState.invokeAction(0)
        assertEquals(0, oldClickCount)
        assertEquals(0, updatedClickCount)
        assertEquals(0, dismissCount)

        buttonState.update {
            action(
                onClick = { updatedClickCount++ },
                style = AlertActionStyle.Cancel,
                title = "Updated action",
            )
        }
        assertTrue(buttonState.createActions().single().enabled)
        buttonState.invokeAction(0)
        assertEquals(1, updatedClickCount)
        assertEquals(1, dismissCount)
    }

    @Test
    fun tabBarLayoutObserverReportsFrameChangesAfterInitialLayout() {
        val tabBar = LayoutObservingTabBar()
        val measuredFrames = mutableListOf<Pair<Double, Double>>()
        tabBar.onLayoutChanged = { frame ->
            frame.useContents { measuredFrames += size.width to size.height }
        }

        tabBar.setFrame(CGRectMake(0.0, 0.0, 400.0, 80.0))
        tabBar.setNeedsLayout()
        tabBar.layoutIfNeeded()
        tabBar.setFrame(CGRectMake(0.0, 0.0, 700.0, 100.0))
        tabBar.setNeedsLayout()
        tabBar.layoutIfNeeded()

        assertTrue(measuredFrames.size >= 2)
        assertEquals(700.0 to 100.0, measuredFrames.last())
    }

    @Test
    fun uidatePickerMeasuresCurrentStyleAfterEachStyleChange() {
        val picker = UIDatePicker(CGRectMake(0.0, 0.0, 0.0, 0.0))

        val wheelSize: DpSize = picker.measureSizeForStyle(UIDatePickerStyle.UIDatePickerStyleWheels)
        val inlineSize = picker.measureSizeForStyle(UIDatePickerStyle.UIDatePickerStyleInline)

        assertNotEquals(wheelSize, inlineSize)
        assertEquals(inlineSize, picker.measureSizeForStyle(UIDatePickerStyle.UIDatePickerStyleInline))
    }

    private fun tabItem(
        label: String,
        onClick: () -> Unit = {},
    ) =
        CupertinoNavigationBarItemData(
            onClick = onClick,
            icon = ColorPainter(Color.White),
            label = label,
        )
}
