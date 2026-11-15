@file:OptIn(zone.ien.hig.ExperimentalCupertinoApi::class)

package cupertino

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import zone.ien.hig.utils.rememberDefaultBackdrop
import zone.ien.hig.CupertinoActionSheet
import zone.ien.hig.CupertinoActionSheetNative
import zone.ien.hig.CupertinoAlertDialog
import zone.ien.hig.CupertinoAlertDialogNative
import zone.ien.hig.CupertinoButton
import zone.ien.hig.CupertinoDropdownMenu
import zone.ien.hig.CupertinoDropdownMenuNative
import zone.ien.hig.CupertinoLiquidAlertDialog
import zone.ien.hig.CupertinoMenuItemData
import zone.ien.hig.CupertinoMenuSectionData
import zone.ien.hig.CupertinoPopover
import zone.ien.hig.CupertinoPopoverEdge
import zone.ien.hig.CupertinoPopupButton
import zone.ien.hig.CupertinoText
import zone.ien.hig.MenuAction
import zone.ien.hig.MenuDivider
import zone.ien.hig.MenuPickerAction
import zone.ien.hig.MenuSection
import zone.ien.hig.cancel
import zone.ien.hig.default
import zone.ien.hig.destructive
import zone.ien.hig.theme.CupertinoColors
import zone.ien.hig.theme.systemRed

@Composable
internal fun AlertDialogDemo() {
    var kind by rememberSaveable { mutableStateOf("Standard") }
    var visible by rememberSaveable { mutableStateOf(false) }
    var result by rememberSaveable { mutableStateOf("No action yet") }
    val finish: (String) -> Unit = { result = it; visible = false }
    if (visible) {
        when (kind) {
            "Native" -> CupertinoAlertDialogNative(
                onDismissRequest = { finish("Dismissed") },
                title = "Save changes?", message = "Your changes will be saved to this collection.",
            ) {
                cancel(onClick = { finish("Save cancelled") }, title = "Cancel")
                default(onClick = { finish("Changes saved") }, title = "Save")
            }
            "Liquid Glass" -> CupertinoLiquidAlertDialog(
                onDismissRequest = { finish("Dismissed") },
                title = { CupertinoText(text = "Save changes?") },
                message = { CupertinoText(text = "Your changes will be saved to this collection.") },
                backdrop = LocalDemoBackdrop.current ?: rememberDefaultBackdrop(),
            ) {
                cancel(onClick = { finish("Save cancelled") }) { CupertinoText(text = "Cancel") }
                default(onClick = { finish("Changes saved") }) { CupertinoText(text = "Save") }
            }
            else -> CupertinoAlertDialog(
                onDismissRequest = { finish("Dismissed") },
                title = { CupertinoText(text = "Save changes?") },
                message = { CupertinoText(text = "Your changes will be saved to this collection.") },
            ) {
                cancel(onClick = { finish("Save cancelled") }) { CupertinoText(text = "Cancel") }
                default(onClick = { finish("Changes saved") }) { CupertinoText(text = "Save") }
            }
        }
    }
    DemoSection("Preview") {
        CupertinoButton(onClick = { visible = true }) { CupertinoText(text = "Show alert") }
        DemoResult(result)
    }
    DemoSection("Properties") {
        DemoChoice("Implementation", listOf("Standard", "Liquid Glass", "Native"), kind, { kind = it }) { it }
        NativeImplementationNote(kind == "Native")
    }
}

@Composable
internal fun ActionSheetDemo() {
    var native by rememberSaveable { mutableStateOf(false) }
    var visible by rememberSaveable { mutableStateOf(false) }
    var result by rememberSaveable { mutableStateOf("No selection yet") }
    val finish: (String) -> Unit = { result = it; visible = false }
    if (native) {
        CupertinoActionSheetNative(visible = visible, onDismissRequest = { finish("Sheet dismissed") },
            title = "Manage photo", message = "Choose an action for this photo.") {
            default(onClick = { finish("Photo copied") }, title = "Copy")
            destructive(onClick = { finish("Photo deleted") }, title = "Delete")
            cancel(onClick = { finish("Cancelled") }, title = "Cancel")
        }
    } else {
        CupertinoActionSheet(visible = visible, onDismissRequest = { finish("Sheet dismissed") },
            title = { CupertinoText(text = "Manage photo") },
            message = { CupertinoText(text = "Choose an action for this photo.") }) {
            default(onClick = { finish("Photo copied") }) { CupertinoText(text = "Copy") }
            destructive(onClick = { finish("Photo deleted") }) { CupertinoText(text = "Delete") }
            cancel(onClick = { finish("Cancelled") }) { CupertinoText(text = "Cancel") }
        }
    }
    DemoSection("Preview") {
        CupertinoButton(onClick = { visible = true }) { CupertinoText(text = "Show action sheet") }
        DemoResult(result)
    }
    DemoSection("Properties", "Try a standard, destructive or cancel action.") {
        DemoToggle("Native implementation", native) { native = it }
        NativeImplementationNote(native)
    }
}

@Composable
internal fun MenuDemo() {
    var native by rememberSaveable { mutableStateOf(false) }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var result by rememberSaveable { mutableStateOf("No selection yet") }
    val finish: (String) -> Unit = { result = it; expanded = false }
    DemoSection("Preview", "Choose an action. Disabled items ignore interaction.") {
        Box {
            CupertinoButton(onClick = { expanded = true }) { CupertinoText(text = "Show menu") }
            if (native) {
                CupertinoDropdownMenuNative(expanded = expanded, onDismissRequest = { expanded = false },
                    sections = listOf(
                        CupertinoMenuSectionData(title = "Manage photo", items = listOf(
                            CupertinoMenuItemData(title = "Share", onClick = { finish("Share selected") }),
                            CupertinoMenuItemData(title = "Unavailable", enabled = false, onClick = { finish("Disabled selected") }),
                            CupertinoMenuItemData(title = "Delete", isDestructive = true, onClick = { finish("Delete selected") }),
                        )),
                    ))
            } else {
                CupertinoDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    MenuSection(title = { CupertinoText(text = "Manage photo") }) {
                        MenuAction(onClick = { finish("Share selected") }) { CupertinoText(text = "Share") }
                        MenuAction(onClick = { finish("Disabled selected") }, enabled = false) { CupertinoText(text = "Unavailable") }
                    }
                    MenuDivider()
                    MenuAction(onClick = { finish("Delete selected") }, contentColor = CupertinoColors.systemRed) {
                        CupertinoText(text = "Delete")
                    }
                }
            }
        }
        DemoResult(result)
    }
    DemoSection("Properties") {
        DemoToggle("Native implementation", native) { native = it }
        NativeImplementationNote(native)
    }
}

@Composable
internal fun PopupButtonDemo() {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var enabled by rememberSaveable { mutableStateOf(true) }
    var sort by rememberSaveable { mutableStateOf("Name") }
    val original = listOf("Landscape", "Family", "Travel")
    val items = if (sort == "Name") original.sorted() else original.reversed()
    DemoSection("Preview", "Choose how to sort the collection.") {
        CupertinoPopupButton(expanded = expanded, onExpandedChange = { expanded = it }, enabled = enabled,
            title = { CupertinoText(text = sort) }) {
            listOf("Name", "Recent").forEach { option ->
                MenuPickerAction(isSelected = sort == option, onClick = { sort = option; expanded = false },
                    title = { CupertinoText(text = option) })
            }
        }
        items.forEach { CupertinoText(text = it) }
        DemoResult("Sort: $sort · expanded = $expanded")
    }
    DemoSection("Properties") { DemoToggle("Enabled", enabled) { enabled = it } }
}

@Composable
internal fun PopoverDemo() {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var edge by rememberSaveable { mutableStateOf(CupertinoPopoverEdge.Top) }
    var anchor by rememberSaveable { mutableStateOf("Center") }
    var width by rememberSaveable { mutableStateOf(240) }
    var result by rememberSaveable { mutableStateOf("Open the popover to inspect its position") }
    DemoSection("Preview", "The popover adjusts to the space around its anchor.") {
        Box(Modifier.fillMaxWidth(), contentAlignment = when (anchor) {
            "Start" -> Alignment.CenterStart
            "End" -> Alignment.CenterEnd
            else -> Alignment.Center
        }) {
            Box {
                CupertinoButton(onClick = { expanded = true; result = "Popover opened" }) { CupertinoText(text = "Show popover") }
                CupertinoPopover(expanded = expanded, onDismissRequest = { expanded = false; result = "Dismissed outside" },
                    edge = edge, width = width.dp, backdrop = LocalDemoBackdrop.current ?: rememberDefaultBackdrop(), contentPadding = PaddingValues(20.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        CupertinoText(text = "More information")
                        CupertinoText(text = "The arrow points to the anchor.")
                        CupertinoButton(onClick = { expanded = false; result = "Confirmed" }) { CupertinoText(text = "OK") }
                    }
                }
            }
        }
        DemoResult(result)
    }
    DemoSection("Properties") {
        DemoChoice("Edge", CupertinoPopoverEdge.entries, edge, { edge = it }) { it.name }
        DemoChoice("Anchor", listOf("Start", "Center", "End"), anchor, { anchor = it }) { it }
        DemoChoice("Width", listOf(160, 240, 320), width, { width = it }) { "${it}dp" }
    }
}
