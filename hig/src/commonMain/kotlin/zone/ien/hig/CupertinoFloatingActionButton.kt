package zone.ien.hig

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop

@Composable
fun CupertinoSmallFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: CupertinoLiquidButtonColors = CupertinoLiquidButtonDefaults.glassProminentButtonColors(),
    shape: Shape = CircleShape,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    backdrop: Backdrop,
    isBackgroundAdaptive: Boolean = true,
    content: @Composable () -> Unit
) {
    CupertinoFloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(42.dp),
        colors = colors,
        size = CupertinoButtonSize.Small,
        shape = shape,
        interactionSource = interactionSource,
        backdrop = backdrop,
        isBackgroundAdaptive = isBackgroundAdaptive,
        content = content
    )
}

@Composable
fun CupertinoMediumFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: CupertinoLiquidButtonColors = CupertinoLiquidButtonDefaults.glassProminentButtonColors(),
    shape: Shape = CircleShape,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    backdrop: Backdrop,
    isBackgroundAdaptive: Boolean = true,
    content: @Composable () -> Unit
) {
    CupertinoFloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(50.dp),
        colors = colors,
        size = CupertinoButtonSize.Regular,
        shape = shape,
        interactionSource = interactionSource,
        backdrop = backdrop,
        isBackgroundAdaptive = isBackgroundAdaptive,
        content = content
    )
}

@Composable
fun CupertinoLargeFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: CupertinoLiquidButtonColors = CupertinoLiquidButtonDefaults.glassProminentButtonColors(),
    shape: Shape = CircleShape,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    backdrop: Backdrop,
    isBackgroundAdaptive: Boolean = true,
    content: @Composable () -> Unit
) {
    CupertinoFloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(64.dp),
        colors = colors,
        size = CupertinoButtonSize.Large,
        shape = shape,
        interactionSource = interactionSource,
        backdrop = backdrop,
        isBackgroundAdaptive = isBackgroundAdaptive,
        content = content
    )
}

@Composable
fun CupertinoExtraLargeFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: CupertinoLiquidButtonColors = CupertinoLiquidButtonDefaults.glassProminentButtonColors(),
    shape: Shape = CircleShape,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    backdrop: Backdrop,
    isBackgroundAdaptive: Boolean = true,
    content: @Composable () -> Unit
) {
    CupertinoFloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(64.dp),
        colors = colors,
        size = CupertinoButtonSize.ExtraLarge,
        shape = shape,
        interactionSource = interactionSource,
        backdrop = backdrop,
        isBackgroundAdaptive = isBackgroundAdaptive,
        content = content
    )
}

@OptIn(ExperimentalCupertinoApi::class)
@Composable
private fun CupertinoFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: CupertinoLiquidButtonColors,
    size: CupertinoButtonSize,
    shape: Shape = CircleShape,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    backdrop: Backdrop,
    isBackgroundAdaptive: Boolean = true,
    content: @Composable () -> Unit
) {
    CupertinoLiquidButton(
        onClick = onClick,
        modifier = modifier,
        colors = colors,
        size = size,
        shape = shape,
        interactionSource = interactionSource,
        backdrop = backdrop,
        isBackgroundAdaptive = isBackgroundAdaptive,
        content = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    )
}