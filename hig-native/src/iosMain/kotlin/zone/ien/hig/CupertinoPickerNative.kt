/*
 * Copyright (c) 2023-2024. Compose Cupertino project and open source contributors.
 * Copyright (c) 2025. Scott Lanoue.
 * Copyright (c) 2026. IENGROUND of IENLAB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package zone.ien.hig

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import androidx.compose.ui.util.fastMap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCSignatureOverride
import zone.ien.hig.theme.CupertinoTheme
import platform.UIKit.UIPickerView
import platform.UIKit.UIPickerViewDataSourceProtocol
import platform.UIKit.UIPickerViewDelegateProtocol
import platform.darwin.NSObject

@Composable
@OptIn(InternalCupertinoApi::class)
@ExperimentalCupertinoApi
fun <T> CupertinoPickerNative(
    state: CupertinoPickerState,
    items: List<T>,
    modifier: Modifier = Modifier,
    height: Dp = CupertinoPickerDefaults.Height,
    containerColor: Color =
        LocalContainerColor.current.takeOrElse {
            CupertinoTheme.colorScheme.secondarySystemGroupedBackground
        },
    enabled: Boolean = true,
    content: (T) -> String,
) {
    DisposableEffect(state, items.size) {
        state.setNativePickerActive(true, items.size)
        onDispose { state.setNativePickerActive(false, 0) }
    }

    val titles = items.fastMap(content)
    val selectedIndex = if (items.isEmpty()) 0 else state.currentSelectedItem(items.size)
    val adapter = remember(state) {
        NativePickerAdapter { state.selectFromNative(it) }
    }

    UIKitView<UIPickerView>(
        factory = {
            UIPickerView().apply {
                dataSource = adapter
                delegate = adapter
            }
        },
        update = {
            adapter.update(
                picker = it,
                titles = titles,
                selectedIndex = selectedIndex,
                enabled = enabled,
            )
            it.backgroundColor = containerColor.toUIColor()
        },
        modifier = modifier.height(height),
        properties =
            UIKitInteropProperties(
                isInteractive = true,
                isNativeAccessibilityEnabled = true,
            ),
    )
}

@OptIn(ExperimentalForeignApi::class)
internal class NativePickerAdapter(
    private val onSelectionChanged: (Int) -> Unit,
) : NSObject(), UIPickerViewDataSourceProtocol, UIPickerViewDelegateProtocol {
    private var titles: List<String> = emptyList()
    private var enabled = true

    fun update(
        picker: UIPickerView,
        titles: List<String>,
        selectedIndex: Int,
        enabled: Boolean,
    ) {
        picker.dataSource = this
        picker.delegate = this
        this.titles = titles
        this.enabled = enabled
        picker.userInteractionEnabled = enabled
        picker.reloadAllComponents()
        if (titles.isNotEmpty()) {
            picker.selectRow(
                row = selectedIndex.coerceIn(0, titles.lastIndex).toLong(),
                inComponent = 0L,
                animated = false,
            )
        }
    }

    override fun numberOfComponentsInPickerView(pickerView: UIPickerView): Long = 1L

    override fun pickerView(
        pickerView: UIPickerView,
        numberOfRowsInComponent: Long,
    ): Long = if (numberOfRowsInComponent == 0L) titles.size.toLong() else 0L

    @ObjCSignatureOverride
    override fun pickerView(
        pickerView: UIPickerView,
        titleForRow: Long,
        forComponent: Long,
    ): String? =
        if (forComponent == 0L) {
            titles.getOrNull(titleForRow.toInt())
        } else {
            null
        }

    @ObjCSignatureOverride
    override fun pickerView(
        pickerView: UIPickerView,
        didSelectRow: Long,
        inComponent: Long,
    ) {
        if (enabled && inComponent == 0L) onSelectionChanged(didSelectRow.toInt())
    }
}
