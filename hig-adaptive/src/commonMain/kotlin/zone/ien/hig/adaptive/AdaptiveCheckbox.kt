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

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import zone.ien.hig.CupertinoCheckBox
import zone.ien.hig.CupertinoCheckboxColors
import zone.ien.hig.CupertinoCheckboxDefaults
import zone.ien.hig.CupertinoTriStateCheckBox

/**
 * An adaptive checkbox component that provides different checkbox styles based on Material Design and Cupertino themes.
 *
 * [AdaptiveCheckbox] uses the Material Design [Checkbox] component and the Cupertino [CupertinoCheckBox] component
 * to provide an appropriate checkbox for each operating system.
 *
 * @param checked the current checked state of the checkbox
 * @param onCheckedChange callback to be invoked when the checkbox state changes
 * @param modifier the [Modifier] to be applied to the element
 * @param enabled controls the enabled state of the checkbox
 * @param interactionSource the [MutableInteractionSource] representing the stream of [Interaction]s
 * @param adaptation configuration block for [CupertinoCheckBoxAdaptation] and [MaterialCheckBoxAdaptation]
 * @see AdaptiveWidget
 * @see CupertinoCheckBox
 * @see Checkbox
 */
@ExperimentalAdaptiveApi
@Composable
fun AdaptiveCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    adaptation: AdaptationScope<CupertinoCheckBoxAdaptation, MaterialCheckBoxAdaptation>.() -> Unit = {}
) {
    AdaptiveWidget(
        adaptation = remember { CheckBoxAdaptation() },
        adaptationScope = adaptation,
        material = {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = modifier,
                enabled = enabled,
                interactionSource = interactionSource,
                colors = it.colors
            )
        },
        cupertino = {
            CupertinoCheckBox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = modifier,
                enabled = enabled,
                interactionSource = interactionSource,
                colors = it.colors
            )
        }
    )
}

/**
 * An adaptive tri-state checkbox component that provides different checkbox styles based on Material Design and Cupertino themes.
 *
 * [AdaptiveTriStateCheckbox] uses the Material Design [TriStateCheckbox] component and the Cupertino [CupertinoTriStateCheckBox] component
 * to provide an appropriate tri-state checkbox for each operating system.
 *
 * @param state The state of the checkbox (Unchecked, Checked, Indeterminate)
 * @param onClick The function to be called when the checkbox is clicked
 * @param modifier The modifier to be applied to the element
 * @param enabled Whether the checkbox is enabled
 * @param interactionSource The interaction source
 * @param adaptation Custom settings function for [CupertinoCheckBoxAdaptation] and [MaterialCheckBoxAdaptation]
 * @see AdaptiveWidget
 * @see CupertinoTriStateCheckBox
 * @see TriStateCheckbox
 */
@ExperimentalAdaptiveApi
@Composable
fun AdaptiveTriStateCheckbox(
    state: ToggleableState,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    adaptation: AdaptationScope<CupertinoCheckBoxAdaptation, MaterialCheckBoxAdaptation>.() -> Unit = {}
) {
    AdaptiveWidget(
        adaptation = remember { CheckBoxAdaptation() },
        adaptationScope = adaptation,
        material = {
            TriStateCheckbox(
                state = state,
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                interactionSource = interactionSource,
                colors = it.colors
            )
        },
        cupertino = {
            CupertinoTriStateCheckBox(
                state = state,
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                interactionSource = interactionSource,
                colors = it.colors
            )
        }
    )
}

@Stable
/**
 * An adaptive adaptation class for Material Design checkboxes that manages checkbox color properties.
 *
 * @param colors [CheckboxColors] - The color configuration for the checkbox
 * @see CheckboxColors
 * @see CheckboxDefaults
 */
class MaterialCheckBoxAdaptation(
    colors: CheckboxColors
) {
    var colors: CheckboxColors by mutableStateOf(colors)
}

@Stable
/**
 * Cupertino 체크박스에 대한 적응형 어댑테이션 클래스로, 체크박스 색상 속성을 관리합니다.
 *
 * @param colors [CupertinoCheckboxColors] - 체크박스의 색상 설정
 * @see CupertinoCheckboxColors
 * @see CupertinoCheckboxDefaults
 */
class CupertinoCheckBoxAdaptation(
    colors: CupertinoCheckboxColors
){
    var colors: CupertinoCheckboxColors by mutableStateOf(colors)
}

/**
 * Adaptation class for checkbox components that manages theme-specific values for checkboxes.
 *
 * This class handles the adaptation between Cupertino and Material design for checkboxes,
 * providing appropriate color styling for both design systems.
 */
@OptIn(ExperimentalAdaptiveApi::class)
@Stable
/**
 * 체크박스 컴포넌트에 대한 적응형 어댑테이션을 구현하는 클래스입니다.
 *
 * @see Adaptation
 * @see CupertinoCheckBoxAdaptation
 * @see MaterialCheckBoxAdaptation
 */
private class CheckBoxAdaptation: Adaptation<CupertinoCheckBoxAdaptation, MaterialCheckBoxAdaptation>(){

    @Composable
    override fun rememberCupertinoAdaptation(): CupertinoCheckBoxAdaptation {
        val colors = CupertinoCheckboxDefaults.colors()

        return remember(colors) {
            CupertinoCheckBoxAdaptation(colors)
        }
    }

    @Composable
    override fun rememberMaterialAdaptation(): MaterialCheckBoxAdaptation {
        val colors = CheckboxDefaults.colors()

        return remember(colors) {
            MaterialCheckBoxAdaptation(colors)
        }
    }
}
