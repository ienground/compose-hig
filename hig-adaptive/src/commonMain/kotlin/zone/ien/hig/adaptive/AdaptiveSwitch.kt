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

/**
 * An adaptive switch that changes its appearance based on the current [Theme].
 *
 * Switches toggle the state of a single item on or off.
 *
 * @param checked whether this switch is checked
 * @param onCheckedChange called when the switch is clicked. If null, the switch is not interactable.
 * @param modifier the [Modifier] to be applied to this switch
 * @param thumbContent content to be drawn inside the thumb
 * @param enabled controls the enabled state of this switch
 * @param interactionSource the [MutableInteractionSource] representing the stream of [Interaction]s
 * @param adaptation configuration block for theme-dependent properties
 * @see AdaptiveWidget
 * @see CupertinoSwitch
 * @see Switch
 */
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
/**
 * Cupertino 스위치에 대한 적응형 어댑테이션 클래스로, 스위치의 다양한 속성을 관리합니다.
 *
 * @param colors [CupertinoSwitchColors] - 스위치의 색상 설정
 * @param backdrop [Backdrop] - 스위치의 배경
 * @see CupertinoSwitchColors
 * @see Backdrop
 */
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
/**
 * Material 스위치에 대한 적응형 어댑테이션 클래스로, 스위치의 다양한 속성을 관리합니다.
 *
 * @param colors [SwitchColors] - 스위치의 색상 설정
 * @see SwitchColors
 * @see SwitchDefaults
 */
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
/**
 * 스위치 유형에 따라 적응형 어댑테이션을 제공하는 클래스입니다.
 *
 * @see Adaptation
 * @see CupertinoSwitchAdaptation
 * @see MaterialSwitchAdaptation
 */
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