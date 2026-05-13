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

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.window.DialogProperties
import zone.ien.hig.theme.CupertinoColors
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.theme.systemGray7

/**
 * Native analog for the compose [CupertinoAlertDialog].
 *
 * @param onDismissRequest called when dialog is already dismissed. Must not be ignored
 * @param title alert dialog title
 * @param message alert dialog message
 * @param containerColor not used in native dialog
 * @param shape not used in native dialog
 * @param properties not used. To enable dismissOnClickOutside behavior
 * add an action with [AlertActionStyle.Cancel] that would receive a cancel callback.
 * @param buttonsOrientation not used. iOS automatically picks most suitable layout
 * based on buttons width and count
 * @param buttons actions builder block
 */
@Composable
expect fun CupertinoAlertDialogNative(
    onDismissRequest: () -> Unit,
    title: String?,
    message: String? = null,
    containerColor: Color = CupertinoColors.systemGray7,
    shape: Shape = CupertinoDialogsDefaults.Shape,
    properties: DialogProperties = DialogProperties(),
    buttonsOrientation: Orientation = Orientation.Horizontal,
    buttons: NativeAlertDialogActionsScope.() -> Unit,
)

/**
 * Native analog for the compose [CupertinoActionSheet].
 *
 * @param visible whether the action sheet is visible
 * @param onDismissRequest called when dialog is already dismissed. Must not be ignored
 * @param title alert dialog title
 * @param message alert dialog message
 * @param containerColor not used in native dialog
 * @param secondaryContainerColor not used in native dialog
 * @param properties not used. To enable dismissOnClickOutside behavior
 * add an action with [AlertActionStyle.Cancel] that would receive a cancel callback.
 * @param buttons actions builder block
 */
@Composable
expect fun CupertinoActionSheetNative(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    title: String? = null,
    message: String? = null,
    containerColor: Color = CupertinoColors.systemGray7,
    secondaryContainerColor: Color = CupertinoTheme.colorScheme.tertiarySystemBackground,
    properties: DialogProperties = DialogProperties(),
    buttons: NativeAlertDialogActionsScope.() -> Unit,
)

/**
 * Scope for building native alert dialog actions.
 */
interface NativeAlertDialogActionsScope {
    /**
     * Alert controller button
     *
     * @param onClick callback when button is clicked
     * @param style the style of the action button
     * @param enabled whether the button is enabled
     * @param title the title of the button
     */
    fun action(
        onClick: () -> Unit,
        style: AlertActionStyle = AlertActionStyle.Default,
        enabled: Boolean = true,
        title: String,
    )
}

/**
 * Alert controller button with default style
 *
 * @param onClick callback when button is clicked
 * @param enabled whether the button is enabled
 * @param title the title of the button
 */
fun NativeAlertDialogActionsScope.default(
    onClick: () -> Unit,
    enabled: Boolean = true,
    title: String,
) = action(
    onClick = onClick,
    style = AlertActionStyle.Default,
    enabled = enabled,
    title = title,
)

/**
 * Alert controller button with destructive style
 *
 * @param onClick callback when button is clicked
 * @param enabled whether the button is enabled
 * @param title the title of the button
 */
fun NativeAlertDialogActionsScope.destructive(
    onClick: () -> Unit,
    enabled: Boolean = true,
    title: String,
) = action(
    onClick = onClick,
    style = AlertActionStyle.Destructive,
    enabled = enabled,
    title = title,
)

/**
 * Alert controller button with cancel style
 *
 * @param onClick callback when button is clicked
 * @param enabled whether the button is enabled
 * @param title the title of the button
 */
fun NativeAlertDialogActionsScope.cancel(
    onClick: () -> Unit,
    enabled: Boolean = true,
    title: String,
) = action(
    onClick = onClick,
    style = AlertActionStyle.Cancel,
    enabled = enabled,
    title = title,
)

/**
 * A button for a native alert dialog.
 *
 * @param onClick callback when the button is clicked
 * @param style the style of the action button
 * @param enabled whether the button is enabled
 * @param title the title of the button
 */
internal class CupertinoAlertDialogButtonNative(
    val onClick: () -> Unit,
    val style: AlertActionStyle,
    val enabled: Boolean,
    val title: String,
)

/**
 * Converts native alert dialog actions to regular dialog actions.
 *
 * @param native the native dialog actions to convert
 */
internal fun AlertDialogActionsScope.fromNative(native: NativeAlertDialogActionsScope.() -> Unit) {
    val buttons = mutableListOf<CupertinoAlertDialogButtonNative>()

    object: NativeAlertDialogActionsScope {
        override fun action(
            onClick: () -> Unit,
            style: AlertActionStyle,
            enabled: Boolean,
            title: String,
        ) {
            buttons.add(
                CupertinoAlertDialogButtonNative(
                    onClick = onClick,
                    style = style,
                    title = title,
                    enabled = enabled,
                ),
            )
        }
    }.apply(native)

    buttons.forEach {
        action(
            onClick = it.onClick,
            style = it.style,
            enabled = it.enabled,
        ) {
            CupertinoText(it.title)
        }
    }
}
