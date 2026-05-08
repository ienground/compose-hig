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

/**
 * An adaptive switch that adapts between Cupertino and Material design based on the platform.
 *
 * Switches toggle the state of a single item on or off.
 * This composable automatically switches between Cupertino (iOS) and Material (Android) design patterns
 * based on the current theme.
 *
 * @param checked whether or not this switch is checked
 * @param onCheckedChange called when this switch is clicked. If `null`, then this switch will not
 * be interactable, unless something else handles its input events and updates its state.
 * @param modifier the [Modifier] to be applied to this switch
 * @param thumbContent content that will be drawn inside the thumb
 * @param enabled controls the enabled state of this switch. When `false`, this component will not
 * respond to user input, and it will appear visually disabled and disabled to accessibility
 * services.
 * @param interactionSource the [MutableInteractionSource] representing the stream of [Interaction]s
 * @param adaptation configuration block for theme-dependent properties for this switch
 */
package zone.ien.hig.adaptive

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import zone.ien.hig.utils.rememberDefaultBackdrop
import zone.ien.hig.CupertinoSwitch
import zone.ien.hig.CupertinoSwitchColors
import zone.ien.hig.CupertinoSwitchDefaults

@ExperimentalAdaptiveApi
@Composable
fun AdaptiveSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    thumbContent: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    adaptation: AdaptationScope<CupertinoSwitchAdaptation, MaterialSwitchAdaptation>.() -> Unit = {},
) {
    AdaptiveWidget(
        adaptation =
            remember {
                SwitchAdaptation()
            },
        adaptationScope = adaptation,
        material = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = modifier,
                thumbContent = thumbContent,
                enabled = enabled,
                interactionSource = interactionSource,
                colors = it.colors,
            )
        },
        cupertino = {
            CupertinoSwitch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
                modifier = modifier,
                thumbContent = thumbContent,
                interactionSource = interactionSource,
                colors = it.colors,
                backdrop = it.backdrop
            )
        },
    )
}

/**
 * Cupertino switch adaptation.
 *
 * Container class for Cupertino switch adaptation properties.
 *
 * @param colors the colors to be used for the switch
 * @param backdrop backdrop to use for the switch
 */
@Stable
class CupertinoSwitchAdaptation internal constructor(
    colors: CupertinoSwitchColors,
    backdrop: Backdrop
) {
    var colors by mutableStateOf(colors)
    var backdrop by mutableStateOf(backdrop)
}

/**
 * Material switch adaptation.
 *
 * Container class for Material switch adaptation properties.
 *
 * @param colors the colors to be used for the switch
 */
@Stable
class MaterialSwitchAdaptation internal constructor(
    colors: SwitchColors,
) {
    var colors by mutableStateOf(colors)
}

/**
 * Switch adaptation implementation.
 *
 * Implementation of [Adaptation] for switch adaptation.
 */
@OptIn(ExperimentalAdaptiveApi::class)
@Stable
private class SwitchAdaptation: Adaptation<CupertinoSwitchAdaptation, MaterialSwitchAdaptation>() {
    @Composable
    override fun rememberCupertinoAdaptation(): CupertinoSwitchAdaptation {
        val colors = CupertinoSwitchDefaults.colors()
        val backdrop = rememberLayerBackdrop()

        return remember(colors, backdrop) {
            CupertinoSwitchAdaptation(
                colors = colors,
                backdrop = backdrop
            )
        }
    }

    @Composable
    override fun rememberMaterialAdaptation(): MaterialSwitchAdaptation {
        val colors = SwitchDefaults.colors()

        return remember(colors) {
            MaterialSwitchAdaptation(colors)
        }
    }
}
