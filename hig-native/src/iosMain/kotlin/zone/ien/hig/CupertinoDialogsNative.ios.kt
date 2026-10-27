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
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.window.DialogProperties
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyle
import platform.UIKit.UIAlertActionStyleCancel
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertActionStyleDestructive
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleActionSheet
import platform.UIKit.UIAlertControllerStyleAlert

@Composable
@NonRestartableComposable
actual fun CupertinoAlertDialogNative(
    onDismissRequest: () -> Unit,
    title: String?,
    message: String?,
    containerColor: Color,
    shape: Shape,
    properties: DialogProperties,
    buttonsOrientation: Orientation,
    buttons: NativeAlertDialogActionsScope.() -> Unit,
) = UIAlertController(
    onDismissRequest = onDismissRequest,
    title = title,
    message = message,
    style = UIAlertControllerStyleAlert,
    buttons = buttons,
)

@Composable
actual fun CupertinoActionSheetNative(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    title: String?,
    message: String?,
    containerColor: Color,
    secondaryContainerColor: Color,
    properties: DialogProperties,
    buttons: NativeAlertDialogActionsScope.() -> Unit,
) {
    if (visible) {
        UIAlertController(
            onDismissRequest = onDismissRequest,
            title = title,
            message = message,
            style = UIAlertControllerStyleActionSheet,
            buttons = buttons,
            anchorPopover = true,
        )
    }
}

@Composable
internal fun UIAlertController(
    onDismissRequest: () -> Unit,
    title: String?,
    message: String?,
    style: UIAlertActionStyle,
    buttons: NativeAlertDialogActionsScope.() -> Unit,
    anchorPopover: Boolean = false,
) {
    val updatedOnDismissRequest = rememberUpdatedState(onDismissRequest)
    val dismissRequestGuard =
        remember {
            AlertControllerDismissRequestGuard {
                updatedOnDismissRequest.value()
            }
        }
    val buttonsState = remember {
        NativeAlertDialogButtonsState(dismissRequestGuard::request)
    }
    buttonsState.update(buttons)
    val presentationSession = remember { PresentationControllerSession() }

    val titles = buttonsState.actionTitles
    val styles = buttonsState.actionStyles
    val enabled = buttonsState.actionEnabled

    key(titles, styles) {
        PresentationController(
            factory = {
                UIAlertController
                    .alertControllerWithTitle(
                        title = title,
                        message = message,
                        preferredStyle = style,
                    ).apply {
                        buttonsState.createActions().fastForEach {
                            addAction(it)
                        }
                    }
            },
            update = {
                setTitle(title)
                setMessage(message)

                buttonsState.updateEnabledActions(actions.filterIsInstance<UIAlertAction>())
            },
            onDismissRequest = dismissRequestGuard::request,
            title,
            message,
            enabled,
            session = presentationSession,
            configurePresentation = { presenter ->
                if (anchorPopover) anchorPopoverTo(presenter)
            },
        )
    }
}

internal class AlertControllerDismissRequestGuard(
    private val onDismissRequest: () -> Unit,
) {
    private var wasRequested = false

    fun request() {
        if (!wasRequested) {
            wasRequested = true
            onDismissRequest()
        }
    }
}

private data class NativeAlertDialogButtonDefinition(
    val title: String,
    val style: AlertActionStyle,
    val enabled: Boolean,
    val onClick: () -> Unit,
)

internal class NativeAlertDialogButtonsState(
    private val onDismissRequest: () -> Unit,
) : NativeAlertDialogActionsScope {
    private var buttons = emptyList<NativeAlertDialogButtonDefinition>()

    val actionTitles: List<String>
        get() = buttons.fastMap { it.title }

    val actionStyles: List<AlertActionStyle>
        get() = buttons.fastMap { it.style }

    val actionEnabled: List<Boolean>
        get() = buttons.fastMap { it.enabled }

    fun update(definitions: NativeAlertDialogActionsScope.() -> Unit) {
        buttons = emptyList()
        definitions(this)
    }

    override fun action(
        onClick: () -> Unit,
        style: AlertActionStyle,
        enabled: Boolean,
        title: String,
    ) {
        buttons =
            buttons +
            NativeAlertDialogButtonDefinition(
                title = title,
                style = style,
                enabled = enabled,
                onClick = onClick,
            )
    }

    fun createActions(): List<UIAlertAction> =
        buttons.mapIndexed { index, button ->
            UIAlertAction
                .actionWithTitle(
                    title = button.title,
                    style = button.style.ui,
                    handler = {
                        invokeAction(index)
                    },
                ).apply {
                    setEnabled(button.enabled)
                }
        }

    fun updateEnabledActions(actions: List<UIAlertAction>) {
        actions.zip(buttons).fastForEach { (action, button) ->
            action.setEnabled(button.enabled)
        }
    }

    fun invokeAction(index: Int) {
        buttons.getOrNull(index)?.takeIf { it.enabled }?.let { button ->
            button.onClick()
            onDismissRequest()
        }
    }
}

internal val AlertActionStyle.ui: UIAlertActionStyle get() =
    when (this) {
        AlertActionStyle.Default -> UIAlertActionStyleDefault
        AlertActionStyle.Cancel -> UIAlertActionStyleCancel
        AlertActionStyle.Destructive -> UIAlertActionStyleDestructive
    }
