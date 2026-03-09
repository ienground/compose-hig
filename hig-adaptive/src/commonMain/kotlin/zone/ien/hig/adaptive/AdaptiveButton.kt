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



package zone.ien.hig.adaptive

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import com.kyant.backdrop.backdrops.LayerBackdrop
import zone.ien.hig.utils.rememberDefaultLayerBackdrop
import zone.ien.hig.CupertinoButton
import zone.ien.hig.CupertinoButtonColors
import zone.ien.hig.CupertinoButtonDefaults.filledButtonColors
import zone.ien.hig.CupertinoButtonDefaults.plainButtonColors
import zone.ien.hig.CupertinoButtonDefaults.tintedButtonColors
import zone.ien.hig.CupertinoButtonSize
import zone.ien.hig.CupertinoLiquidButton
import zone.ien.hig.CupertinoLiquidButtonColors
import zone.ien.hig.CupertinoLiquidButtonDefaults
import zone.ien.hig.ExperimentalCupertinoApi
import zone.ien.hig.theme.CupertinoTheme

/**
 * Adaptive button that takes [Button] or borderedProminent [CupertinoButton] appearance
 * */
@OptIn(ExperimentalCupertinoApi::class)
@ExperimentalAdaptiveApi
@Composable
fun AdaptiveButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    adaptation: AdaptationScope<CupertinoButtonAdaptation, MaterialButtonAdaptation>.() -> Unit = {},
    content: @Composable (RowScope.() -> Unit)
) {
    AdaptiveWidget(
        adaptation = remember {
            ButtonAdaptation(type = ButtonType.Filled)
        },
        adaptationScope = adaptation,
        material = {
            Button(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                border = it.border,
                interactionSource = interactionSource,
                content = content,
                contentPadding = it.contentPadding,
                shape =  it.shape,
                colors = it.colors,
                elevation = it.elevation
            )
        },
        cupertino = {
            CupertinoLiquidButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                interactionSource = interactionSource,
                content = content,
                size = it.size,
                contentPadding = it.contentPadding ?: it.size.contentPadding,
                shape =  it.shape ?: it.size.shape(CupertinoTheme.shapes),
                colors = it.colors,
                backdrop = it.backdrop,
                isBackgroundAdaptive = it.isBackgroundAdaptive
            )
        }
    )
}

/**
 * Adaptive button that takes [TextButton] or borderless [CupertinoButton] appearance
 * */
@OptIn(ExperimentalCupertinoApi::class)
@ExperimentalAdaptiveApi
@Composable
fun AdaptiveTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    adaptation: AdaptationScope<CupertinoButtonAdaptation, MaterialButtonAdaptation>.() -> Unit = {},
    content: @Composable (RowScope.() -> Unit)
) {
    AdaptiveWidget(
        adaptation = remember {
            ButtonAdaptation(type = ButtonType.Text)
        },
        adaptationScope = adaptation,
        material = {
            TextButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                border = it.border,
                interactionSource = interactionSource,
                content = content,
                contentPadding = it.contentPadding,
                shape =  it.shape,
                colors = it.colors,
                elevation = it.elevation
            )
        },
        cupertino = {
            CupertinoLiquidButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                interactionSource = interactionSource,
                content = content,
                size = it.size,
                contentPadding = it.contentPadding ?: it.size.contentPadding,
                shape =  it.shape ?: it.size.shape(CupertinoTheme.shapes),
                colors = it.colors,
                backdrop = it.backdrop,
                isBackgroundAdaptive = it.isBackgroundAdaptive
            )
        }
    )
}

/**
 * Adaptive button that takes [FilledTonalButton] or bordered [CupertinoButton] appearance
 * */
@OptIn(ExperimentalCupertinoApi::class)
@ExperimentalAdaptiveApi
@Composable
fun AdaptiveTonalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    adaptation: AdaptationScope<CupertinoButtonAdaptation, MaterialButtonAdaptation>.() -> Unit = {},
    content: @Composable (RowScope.() -> Unit)
) {
    AdaptiveWidget(
        adaptation = remember {
            ButtonAdaptation(type = ButtonType.Tonal)
        },
        adaptationScope = adaptation,
        material = {
            FilledTonalButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                border = it.border,
                interactionSource = interactionSource,
                content = content,
                contentPadding = it.contentPadding,
                shape =  it.shape,
                colors = it.colors,
                elevation = it.elevation
            )
        },
        cupertino = {
            CupertinoLiquidButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                interactionSource = interactionSource,
                content = content,
                size = it.size,
                contentPadding = it.contentPadding ?: it.size.contentPadding,
                shape =  it.shape ?: it.size.shape(CupertinoTheme.shapes),
                colors = it.colors,
                backdrop = it.backdrop,
                isBackgroundAdaptive = it.isBackgroundAdaptive
            )
        }
    )
}


@Stable
class CupertinoButtonAdaptation internal constructor(
    colors: CupertinoLiquidButtonColors,
    backdrop: LayerBackdrop,
    isBackgroundAdaptive: Boolean,
) {
    var colors: CupertinoLiquidButtonColors by mutableStateOf(colors)
    var backdrop: LayerBackdrop by mutableStateOf(backdrop)
    var isBackgroundAdaptive: Boolean by mutableStateOf(isBackgroundAdaptive)
    var size: CupertinoButtonSize by mutableStateOf(CupertinoButtonSize.Regular)
    var shape: Shape? by mutableStateOf(null)
    var contentPadding: PaddingValues? by mutableStateOf(null)
}

@Stable
class MaterialButtonAdaptation internal constructor(
    colors: ButtonColors,
    elevation: ButtonElevation?,
    shape: Shape,
    contentPadding: PaddingValues,
    border: BorderStroke?,
) {
    var colors: ButtonColors by mutableStateOf(colors)
    var elevation: ButtonElevation? by mutableStateOf(elevation)
    var shape: Shape by mutableStateOf(shape)
    var contentPadding: PaddingValues by mutableStateOf(contentPadding)
    var border: BorderStroke? by mutableStateOf(border)
}

private enum class ButtonType {
    Filled, Text, Tonal
}

@ExperimentalAdaptiveApi
private class ButtonAdaptation(
    private val type: ButtonType,
): Adaptation<CupertinoButtonAdaptation, MaterialButtonAdaptation>() {

    @Composable
    override fun rememberCupertinoAdaptation(): CupertinoButtonAdaptation {
        val colors = when (type) {
            ButtonType.Filled -> CupertinoLiquidButtonDefaults.glassButtonColors()
            ButtonType.Text -> CupertinoLiquidButtonDefaults.glassButtonColors()
            ButtonType.Tonal -> CupertinoLiquidButtonDefaults.glassProminentButtonColors()
        }
        val backdrop = rememberDefaultLayerBackdrop()
        val isBackgroundAdaptive = true

        return remember(colors, backdrop, isBackgroundAdaptive) {
            CupertinoButtonAdaptation(
                colors = colors,
                backdrop = backdrop,
                isBackgroundAdaptive
            )
        }
    }

    @Composable
    override fun rememberMaterialAdaptation(): MaterialButtonAdaptation {
        val colors = when(type) {
            ButtonType.Filled -> ButtonDefaults.buttonColors()
            ButtonType.Text -> ButtonDefaults.textButtonColors()
            ButtonType.Tonal -> ButtonDefaults.filledTonalButtonColors()
        }

        val elevation = ButtonDefaults.buttonElevation()
        val shape = ButtonDefaults.shape
        val border: BorderStroke? = null

        return remember(colors, elevation, elevation, shape, border) {
            MaterialButtonAdaptation(
                colors = colors,
                elevation = elevation,
                shape = shape,
                contentPadding = ButtonDefaults.ContentPadding,
                border = border
            )
        }
    }
}