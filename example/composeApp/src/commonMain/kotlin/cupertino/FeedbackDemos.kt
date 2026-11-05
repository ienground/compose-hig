@file:OptIn(zone.ien.hig.ExperimentalCupertinoApi::class)

package cupertino

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import zone.ien.hig.utils.rememberDefaultBackdrop
import kotlinx.coroutines.delay
import zone.ien.hig.CupertinoActivityIndicator
import zone.ien.hig.CupertinoBadge
import zone.ien.hig.CupertinoBorderedTextField
import zone.ien.hig.CupertinoButton
import zone.ien.hig.CupertinoEmptyState
import zone.ien.hig.CupertinoIcon
import zone.ien.hig.CupertinoProgressIndicator
import zone.ien.hig.CupertinoSlider
import zone.ien.hig.CupertinoStepper
import zone.ien.hig.CupertinoText
import zone.ien.hig.icons.CupertinoIcons
import zone.ien.hig.icons.outlined.Heart
import zone.ien.hig.theme.CupertinoColors
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.theme.systemRed

@Composable
internal fun ProgressDemo() {
    var progress by rememberSaveable { mutableFloatStateOf(0.35f) }
    var running by rememberSaveable { mutableStateOf(false) }
    var showLabel by rememberSaveable { mutableStateOf(true) }
    LaunchedEffect(running) {
        if (running) {
            if (progress >= 1f) progress = 0f
            while (progress < 1f) {
                delay(100)
                progress = (progress + 0.02f).coerceAtMost(1f)
            }
            running = false
        }
    }
    DemoSection("Preview", "Run a sample task or adjust progress manually.") {
        CupertinoProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth(),
            label = if (showLabel) "${(progress * 100).toInt()}% complete" else null)
        CupertinoSlider(value = progress, onValueChange = { running = false; progress = it }, backdrop = LocalDemoBackdrop.current ?: rememberDefaultBackdrop())
        CupertinoButton(onClick = { running = !running }) { CupertinoText(text = if (running) "Pause" else "Run") }
        DemoResult("${(progress * 100).toInt()}% · ${if (running) "Running" else if (progress >= 1f) "Done" else "Idle"}")
        DemoToggle("Show label", showLabel) { showLabel = it }
    }
    DemoSection("Indeterminate", "An activity indicator shows work in progress.") {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
            CupertinoActivityIndicator(size = 20.dp)
            CupertinoActivityIndicator(size = 32.dp, color = CupertinoTheme.colorScheme.accent)
            CupertinoText(text = "Loading")
        }
    }
}

@Composable
internal fun BadgeDemo() {
    var count by rememberSaveable { mutableIntStateOf(3) }
    var text by rememberSaveable { mutableStateOf("New") }
    var accent by rememberSaveable { mutableStateOf(false) }
    DemoSection("Preview") {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CupertinoBadge(containerColor = if (accent) CupertinoTheme.colorScheme.accent else CupertinoColors.systemRed) {
                CupertinoText(text = count.toString())
            }
            CupertinoBadge { CupertinoText(text = text) }
            CupertinoBadge { CupertinoText(text = "99+") }
        }
        DemoResult("Count: $count · Label: $text")
    }
    DemoSection("Properties") {
        CupertinoStepper(value = count, onValueChange = { count = it }, valueRange = 0..120,
            decrementLabel = "Decrease count", incrementLabel = "Increase count")
        CupertinoBorderedTextField(value = text, onValueChange = { text = it }, singleLine = true,
            modifier = Modifier.fillMaxWidth(), placeholder = { CupertinoText(text = "Label") })
        DemoToggle("Accent color", accent) { accent = it }
    }
}

@Composable
internal fun EmptyStateDemo() {
    var showIcon by rememberSaveable { mutableStateOf(true) }
    var showDescription by rememberSaveable { mutableStateOf(true) }
    var showAction by rememberSaveable { mutableStateOf(true) }
    var added by rememberSaveable { mutableStateOf(false) }
    DemoSection("Preview") {
        if (added) {
            CupertinoText(text = "First item", style = CupertinoTheme.typography.headline)
            DemoResult("1 item added")
            CupertinoButton(onClick = { added = false }) { CupertinoText(text = "Remove item") }
        } else {
            CupertinoEmptyState(
                title = { CupertinoText(text = "No saved items") },
                icon = if (showIcon) { { CupertinoIcon(CupertinoIcons.Default.Heart, null) } } else null,
                description = if (showDescription) { { CupertinoText(text = "Add an item to start your collection.") } } else null,
                onAction = if (showAction) { { added = true } } else null,
                action = if (showAction) { { CupertinoText(text = "Add item") } } else null,
            )
        }
    }
    DemoSection("Properties") {
        DemoToggle("Icons", showIcon) { showIcon = it }
        DemoToggle("Description", showDescription) { showDescription = it }
        DemoToggle("Action button", showAction) { showAction = it }
    }
}
