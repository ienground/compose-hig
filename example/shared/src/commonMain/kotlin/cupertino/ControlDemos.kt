@file:OptIn(zone.ien.hig.ExperimentalCupertinoApi::class)

package cupertino

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import zone.ien.hig.utils.rememberDefaultBackdrop
import zone.ien.hig.CupertinoButton
import zone.ien.hig.CupertinoButtonDefaults
import zone.ien.hig.CupertinoButtonSize
import zone.ien.hig.CupertinoCheckBox
import zone.ien.hig.CupertinoExtraLargeFloatingActionButton
import zone.ien.hig.CupertinoIcon
import zone.ien.hig.CupertinoIconButton
import zone.ien.hig.CupertinoLargeFloatingActionButton
import zone.ien.hig.CupertinoLiquidButton
import zone.ien.hig.CupertinoLiquidButtonDefaults
import zone.ien.hig.CupertinoLiquidIconButton
import zone.ien.hig.CupertinoMediumFloatingActionButton
import zone.ien.hig.CupertinoPageControl
import zone.ien.hig.CupertinoRangeSlider
import zone.ien.hig.CupertinoSegmentedControl
import zone.ien.hig.CupertinoSegmentedControlSize
import zone.ien.hig.CupertinoSegmentedControlTab
import zone.ien.hig.CupertinoSlider
import zone.ien.hig.CupertinoSmallFloatingActionButton
import zone.ien.hig.CupertinoStepper
import zone.ien.hig.CupertinoSwitch
import zone.ien.hig.CupertinoText
import zone.ien.hig.CupertinoTriStateCheckBox
import zone.ien.hig.icons.CupertinoIcons
import zone.ien.hig.icons.outlined.Heart
import zone.ien.hig.theme.CupertinoTheme

@Composable
internal fun ButtonDemo() {
    var enabled by rememberSaveable { mutableStateOf(true) }
    var size by rememberSaveable { mutableStateOf(CupertinoButtonSize.Regular) }
    var variant by rememberSaveable { mutableStateOf("Filled") }
    var clicks by rememberSaveable { mutableIntStateOf(0) }
    var lastAction by rememberSaveable { mutableStateOf("No interaction yet") }
    DemoSection("Preview") {
        CupertinoButton(
            onClick = { clicks++; lastAction = "Preview button" },
            enabled = enabled,
            size = size,
            colors = when (variant) {
                "Tinted" -> CupertinoButtonDefaults.tintedButtonColors()
                "Plain" -> CupertinoButtonDefaults.plainButtonColors()
                else -> CupertinoButtonDefaults.filledButtonColors()
            },
            modifier = Modifier.testTag("preview-button"),
        ) { CupertinoText(text = "Try button") }
        DemoResult("Clicks: $clicks · $lastAction")
    }
    DemoSection("Properties") {
        DemoToggle("Enabled", enabled) { enabled = it }
        DemoChoice("Size", CupertinoButtonSize.entries, size, { size = it }) { it.name }
        DemoChoice("Style", listOf("Filled", "Tinted", "Plain"), variant, { variant = it }) { it }
    }
    DemoSection("Variants", "Disabled buttons ignore interaction.") {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CupertinoButton(onClick = { clicks++; lastAction = "Default button" }) { CupertinoText(text = "Standard") }
            CupertinoButton(onClick = { clicks++ }, enabled = false) { CupertinoText(text = "Disabled") }
            CupertinoIconButton(onClick = { clicks++; lastAction = "Icon button" }) {
                CupertinoIcon(imageVector = CupertinoIcons.Default.Heart, contentDescription = "Like")
            }
        }
    }
}

@Composable
internal fun LiquidButtonDemo() {
    var enabled by rememberSaveable { mutableStateOf(true) }
    var prominent by rememberSaveable { mutableStateOf(true) }
    var size by rememberSaveable { mutableStateOf(CupertinoButtonSize.Large) }
    var clicks by rememberSaveable { mutableIntStateOf(0) }
    DemoSection("Preview", "Press and drag to explore the glass response.") {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CupertinoLiquidButton(
                onClick = { clicks++ }, backdrop = LocalDemoBackdrop.current ?: rememberDefaultBackdrop(), enabled = enabled, size = size,
                colors = if (prominent) CupertinoLiquidButtonDefaults.glassProminentButtonColors()
                    else CupertinoLiquidButtonDefaults.glassButtonColors(),
            ) { CupertinoText(text = "Glass button") }
            CupertinoLiquidIconButton(onClick = { clicks++ }, backdrop = LocalDemoBackdrop.current ?: rememberDefaultBackdrop(), enabled = enabled) {
                CupertinoIcon(imageVector = CupertinoIcons.Default.Heart, contentDescription = "Like")
            }
        }
        DemoResult("Clicks: $clicks")
    }
    DemoSection("Properties") {
        DemoToggle("Enabled", enabled) { enabled = it }
        DemoToggle("Prominent", prominent) { prominent = it }
        DemoChoice("Size", CupertinoButtonSize.entries, size, { size = it }) { it.name }
    }
}

@Composable
internal fun FloatingActionButtonDemo() {
    var result by rememberSaveable { mutableStateOf("Choose a button size") }
    DemoSection("Sizes") {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CupertinoSmallFloatingActionButton(onClick = { result = "Small pressed" }, backdrop = LocalDemoBackdrop.current ?: rememberDefaultBackdrop()) {
                CupertinoIcon(CupertinoIcons.Default.Heart, "Small like")
            }
            CupertinoMediumFloatingActionButton(onClick = { result = "Medium pressed" }, backdrop = LocalDemoBackdrop.current ?: rememberDefaultBackdrop()) {
                CupertinoIcon(CupertinoIcons.Default.Heart, "Medium like")
            }
            CupertinoLargeFloatingActionButton(onClick = { result = "Large pressed" }, backdrop = LocalDemoBackdrop.current ?: rememberDefaultBackdrop()) {
                CupertinoIcon(CupertinoIcons.Default.Heart, "Large like")
            }
            CupertinoExtraLargeFloatingActionButton(onClick = { result = "ExtraLarge pressed" }, backdrop = LocalDemoBackdrop.current ?: rememberDefaultBackdrop()) {
                CupertinoIcon(CupertinoIcons.Default.Heart, "Extra large like")
            }
        }
        DemoResult(result)
    }
}

@Composable
internal fun SwitchDemo() {
    var enabled by rememberSaveable { mutableStateOf(true) }
    var checked by rememberSaveable { mutableStateOf(false) }
    var changes by rememberSaveable { mutableIntStateOf(0) }
    DemoSection("Preview", "Tap or drag the thumb to change the value.") {
        CupertinoSwitch(checked = checked, onCheckedChange = { checked = it; changes++ }, enabled = enabled,
            backdrop = LocalDemoBackdrop.current ?: rememberDefaultBackdrop(), modifier = Modifier.testTag("preview-switch"))
        DemoResult("checked = $checked · Changes: $changes")
    }
    DemoSection("Properties") { DemoToggle("Enabled", enabled) { enabled = it } }
    DemoSection("Disabled states") {
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            CupertinoSwitch(checked = false, onCheckedChange = {}, enabled = false, backdrop = LocalDemoBackdrop.current ?: rememberDefaultBackdrop())
            CupertinoSwitch(checked = true, onCheckedChange = {}, enabled = false, backdrop = LocalDemoBackdrop.current ?: rememberDefaultBackdrop())
        }
    }
}

@Composable
internal fun CheckboxDemo() {
    var enabled by rememberSaveable { mutableStateOf(true) }
    var first by rememberSaveable { mutableStateOf(true) }
    var second by rememberSaveable { mutableStateOf(false) }
    val state = if (first && second) ToggleableState.On else if (!first && !second) ToggleableState.Off else ToggleableState.Indeterminate
    DemoSection("Preview", "Select one item to see the indeterminate state.") {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CupertinoTriStateCheckBox(state = state, enabled = enabled, onClick = {
                val select = state != ToggleableState.On
                first = select; second = select
            })
            CupertinoText(text = "Select all")
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CupertinoCheckBox(checked = first, onCheckedChange = { first = it }, enabled = enabled)
            CupertinoText(text = "Photos")
            CupertinoCheckBox(checked = second, onCheckedChange = { second = it }, enabled = enabled)
            CupertinoText(text = "Videos")
        }
        DemoResult("State: $state · ${listOf(first, second).count { it }} / 2 selected")
    }
    DemoSection("Properties") { DemoToggle("Enabled", enabled) { enabled = it } }
}

@Composable
internal fun SliderDemo() {
    var enabled by rememberSaveable { mutableStateOf(true) }
    var discrete by rememberSaveable { mutableStateOf(false) }
    var value by rememberSaveable { mutableFloatStateOf(40f) }
    var lower by rememberSaveable { mutableFloatStateOf(20f) }
    var upper by rememberSaveable { mutableFloatStateOf(80f) }
    var finished by rememberSaveable { mutableStateOf("Drag to change the value") }
    DemoSection("Preview") {
        CupertinoText(text = "Value: ${value.toInt()}")
        CupertinoSlider(value = value, onValueChange = { value = it }, valueRange = 0f..100f,
            steps = if (discrete) 4 else 0, enabled = enabled, backdrop = LocalDemoBackdrop.current ?: rememberDefaultBackdrop(),
            onValueChangeFinished = { finished = "Committed: ${value.toInt()}" })
        CupertinoText(text = "Range: ${lower.toInt()} – ${upper.toInt()}")
        CupertinoRangeSlider(value = lower..upper, onValueChange = { lower = it.start; upper = it.endInclusive },
            valueRange = 0f..100f, steps = if (discrete) 4 else 0, enabled = enabled, backdrop = LocalDemoBackdrop.current ?: rememberDefaultBackdrop(),
            onValueChangeFinished = { finished = "Committed: ${lower.toInt()} – ${upper.toInt()}" })
        DemoResult(finished)
    }
    DemoSection("Properties") {
        DemoToggle("Enabled", enabled) { enabled = it }
        DemoToggle("Snap in steps of 20", discrete) { discrete = it }
    }
}

@Composable
internal fun StepperDemo() {
    var enabled by rememberSaveable { mutableStateOf(true) }
    var maximum by rememberSaveable { mutableIntStateOf(5) }
    var value by rememberSaveable { mutableIntStateOf(2) }
    DemoSection("Preview", "Buttons stop at the minimum and maximum.") {
        CupertinoStepper(value = value, onValueChange = { value = it }, valueRange = 0..maximum,
            enabled = enabled, decrementLabel = "Decrease quantity", incrementLabel = "Increase quantity")
        DemoResult("Quantity: $value · Range: 0..$maximum")
    }
    DemoSection("Properties") {
        DemoToggle("Enabled", enabled) { enabled = it }
        DemoChoice("Maximum", listOf(3, 5, 10), maximum, { maximum = it; value = value.coerceAtMost(it) }) { "$it" }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CupertinoButton(onClick = { value = 0 }) { CupertinoText(text = "Set minimum") }
            CupertinoButton(onClick = { value = maximum }) { CupertinoText(text = "Set maximum") }
        }
    }
}

@Composable
internal fun SegmentedControlDemo() {
    var selected by rememberSaveable { mutableIntStateOf(0) }
    var size by rememberSaveable { mutableStateOf(CupertinoSegmentedControlSize.Small) }
    val tabs = listOf("All", "Favorites", "Recent")
    DemoSection("Preview") {
        CupertinoSegmentedControl(selectedTabIndex = selected, size = size, backdrop = LocalDemoBackdrop.current ?: rememberDefaultBackdrop(),
            modifier = Modifier.fillMaxWidth()) {
            tabs.forEachIndexed { index, title ->
                CupertinoSegmentedControlTab(isSelected = selected == index, onClick = { selected = index }) {
                    CupertinoText(text = title)
                }
            }
        }
        CupertinoText(text = when (selected) {
            1 -> "3 favorite photos"
            2 -> "5 recent photos"
            else -> "12 photos"
        }, style = CupertinoTheme.typography.headline)
        DemoResult("selectedTabIndex = $selected · ${tabs[selected]}")
    }
    DemoSection("Properties") {
        DemoChoice("Size", CupertinoSegmentedControlSize.entries, size, { size = it }) { it.name }
    }
}

@Composable
internal fun PageControlDemo() {
    var count by rememberSaveable { mutableIntStateOf(5) }
    var page by rememberSaveable { mutableIntStateOf(0) }
    DemoSection("Preview", "Tap a dot or use the buttons to turn the page.") {
        CupertinoText(text = "Page ${page + 1}", modifier = Modifier.padding(vertical = 24.dp),
            style = CupertinoTheme.typography.title1)
        CupertinoPageControl(pageCount = count, currentPage = page, onPageSelected = { page = it },
            modifier = Modifier.fillMaxWidth(), pageDescription = { index, total -> "Page ${index + 1} of $total" })
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CupertinoButton(onClick = { page-- }, enabled = page > 0) { CupertinoText(text = "Previous") }
            CupertinoButton(onClick = { page++ }, enabled = page < count - 1) { CupertinoText(text = "Next") }
        }
        DemoResult("currentPage = $page · pageCount = $count")
    }
    DemoSection("Properties") {
        DemoChoice("Page count", listOf(1, 3, 5, 10), count, { count = it; page = page.coerceAtMost(it - 1) }) { "$it" }
    }
}
