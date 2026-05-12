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
 * 현재 [Theme]에 따라 스타일이 결정되는 적응형 스위치입니다.
 *
 * 스위치는 단일 항목을 켜거나 끕니다.
 *
 * @param checked 스위치가 체크되었는지 여부
 * @param onCheckedChange 스위치가 클릭되었을 때 호출되는 함수. null인 경우, 이 스위치는 상호작용이 불가능하며, 다른 요소가 입력 이벤트를 처리하고 상태를 업데이트해야 합니다.
 * @param modifier 스위치에 적용할 [Modifier]
 * @param thumbContent 스위치의 엄지/thumb 안에 표시할 내용
 * @param enabled 스위치의 활성화 상태를 제어합니다. false인 경우, 이 컴포넌트는 사용자 입력에 응답하지 않으며, 시각적으로 비활성화되고 접근성 서비스에서는 비활성화된 상태로 표시됩니다.
 * @param interactionSource [Interaction] 스트림을 나타내는 [MutableInteractionSource]
 * @param adaptation 테마에 따라 다른 속성을 설정하는 구성 블록
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