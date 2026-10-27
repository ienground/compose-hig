package zone.ien.hig

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import platform.CoreGraphics.CGRectMake
import platform.Foundation.timeIntervalSince1970
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleActionSheet
import platform.UIKit.UIDatePicker
import platform.UIKit.UIPickerView
import platform.UIKit.UIUserInterfaceIdiomPad
import platform.UIKit.UIViewController
import platform.UIKit.UIScreen
import platform.UIKit.popoverPresentationController

@OptIn(ExperimentalForeignApi::class)
class CupertinoNativeControlsTest {
    @Test
    fun uidatePickerAppliesInclusiveUtcYearBoundsAndUpdatesThem() {
        val picker = UIDatePicker(CGRectMake(0.0, 0.0, 0.0, 0.0))

        picker.applyYearRange(2024..2025)

        assertEquals(1_704_067_200.0, assertNotNull(picker.minimumDate).timeIntervalSince1970, 0.001)
        assertEquals(1_767_225_599.999, assertNotNull(picker.maximumDate).timeIntervalSince1970, 0.001)

        picker.applyYearRange(2026..2027)

        assertEquals(1_767_225_600.0, assertNotNull(picker.minimumDate).timeIntervalSince1970, 0.001)
        assertEquals(1_830_297_599.999, assertNotNull(picker.maximumDate).timeIntervalSince1970, 0.001)
    }

    @Test
    fun uidatePickerWithoutYearRangeHasNoDateBounds() {
        val picker = UIDatePicker(CGRectMake(0.0, 0.0, 0.0, 0.0))
        picker.applyYearRange(2024..2025)

        picker.applyYearRange(null)

        assertNull(picker.minimumDate)
        assertNull(picker.maximumDate)
    }

    @Test
    fun pickerAdapterProvidesItemsUpdatesRowsAndReportsSelection() {
        var selectedIndex = -1
        var replacementSelectedIndex = -1
        val picker = UIPickerView()
        val adapter = NativePickerAdapter { selectedIndex = it }
        picker.dataSource = adapter
        picker.delegate = adapter

        adapter.update(
            picker = picker,
            titles = listOf("First", "Second"),
            selectedIndex = 1,
            enabled = true,
        )

        assertEquals(2L, picker.numberOfRowsInComponent(0L))
        assertEquals("Second", adapter.pickerView(picker, titleForRow = 1L, forComponent = 0L))
        assertEquals(1L, picker.selectedRowInComponent(0L))
        assertTrue(picker.userInteractionEnabled)

        adapter.pickerView(picker, didSelectRow = 0L, inComponent = 0L)
        assertEquals(0, selectedIndex)

        adapter.update(
            picker = picker,
            titles = listOf("Updated"),
            selectedIndex = 0,
            enabled = true,
        )
        assertEquals(1L, picker.numberOfRowsInComponent(0L))
        assertEquals("Updated", adapter.pickerView(picker, titleForRow = 0L, forComponent = 0L))

        val replacementAdapter = NativePickerAdapter { replacementSelectedIndex = it }
        replacementAdapter.update(
            picker = picker,
            titles = listOf("Replacement", "Other"),
            selectedIndex = 1,
            enabled = true,
        )

        assertSame(replacementAdapter, picker.dataSource)
        assertSame(replacementAdapter, picker.delegate)
        assertEquals(2L, picker.numberOfRowsInComponent(0L))
        assertEquals("Replacement", replacementAdapter.pickerView(picker, titleForRow = 0L, forComponent = 0L))
        replacementAdapter.pickerView(picker, didSelectRow = 1L, inComponent = 0L)
        assertEquals(1, replacementSelectedIndex)
        assertEquals(0, selectedIndex)

        replacementAdapter.update(
            picker = picker,
            titles = listOf("Final"),
            selectedIndex = 4,
            enabled = false,
        )

        assertEquals(1L, picker.numberOfRowsInComponent(0L))
        assertEquals("Final", replacementAdapter.pickerView(picker, titleForRow = 0L, forComponent = 0L))
        assertEquals(0L, picker.selectedRowInComponent(0L))
        assertFalse(picker.userInteractionEnabled)
        replacementAdapter.pickerView(picker, didSelectRow = 0L, inComponent = 0L)
        assertEquals(1, replacementSelectedIndex)
    }

    @Test
    fun dismissRequestGuardIsOneShotPerAlertController() {
        var dismissCount = 0
        val guard = AlertControllerDismissRequestGuard { dismissCount++ }

        guard.request()
        guard.request()

        assertEquals(1, dismissCount)
    }

    @Test
    fun dismissRequestGuardsDoNotShareStateBetweenAlertControllers() {
        var dismissCount = 0
        val firstAlert = AlertControllerDismissRequestGuard { dismissCount++ }
        val secondAlert = AlertControllerDismissRequestGuard { dismissCount++ }

        firstAlert.request()
        secondAlert.request()

        assertEquals(2, dismissCount)
    }

    @Test
    fun actionSheetPopoverUsesItsPresenterViewAndBoundsOnIPad() {
        if (UIScreen.mainScreen.traitCollection.userInterfaceIdiom != UIUserInterfaceIdiomPad) return

        val presenter = UIViewController()
        presenter.view.setFrame(CGRectMake(0.0, 0.0, 320.0, 480.0))
        val actionSheet =
            UIAlertController.alertControllerWithTitle(
                title = "Actions",
                message = null,
                preferredStyle = UIAlertControllerStyleActionSheet,
            ).apply {
                anchorPopoverTo(presenter)
            }

        val popover = assertNotNull(actionSheet.popoverPresentationController)
        assertEquals(presenter.view, popover.sourceView)
        popover.sourceRect.useContents {
            assertEquals(320.0, size.width)
            assertEquals(480.0, size.height)
        }
    }
}
