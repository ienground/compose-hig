package zone.ien.hig

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UIPickerView
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalForeignApi::class)
class CupertinoPickerAnimationNativeTest {
    @Test
    fun adapterPassesAnimatedAndInstantSelectionRequestsToUIKit() {
        val picker = RecordingPicker()
        val adapter = NativePickerAdapter {}

        adapter.update(
            picker = picker,
            titles = listOf("First", "Second"),
            selectedIndex = 1,
            enabled = true,
            animated = true,
        )
        adapter.update(
            picker = picker,
            titles = listOf("First", "Second"),
            selectedIndex = 1,
            enabled = true,
            animated = false,
        )
        adapter.update(
            picker = picker,
            titles = listOf("First", "Second"),
            selectedIndex = 0,
            enabled = true,
            animated = false,
        )

        assertEquals(listOf(true, false), picker.animationFlags)
        assertEquals(0L, picker.selectedRowInComponent(0L))
    }

    private class RecordingPicker : UIPickerView(CGRectMake(0.0, 0.0, 0.0, 0.0)) {
        val animationFlags = mutableListOf<Boolean>()

        override fun selectRow(row: Long, inComponent: Long, animated: Boolean) {
            animationFlags += animated
            super.selectRow(row = row, inComponent = inComponent, animated = animated)
        }
    }
}
