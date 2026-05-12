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
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import zone.ien.hig.utils.rememberDefaultBackdrop
import zone.ien.hig.CupertinoButton
import zone.ien.hig.CupertinoButtonSize
import zone.ien.hig.CupertinoLiquidButton
import zone.ien.hig.CupertinoLiquidButtonColors
import zone.ien.hig.CupertinoLiquidButtonDefaults
import zone.ien.hig.ExperimentalCupertinoApi
import zone.ien.hig.theme.CupertinoTheme

/**
 * 적응형 버튼으로, Material [Button] 또는 테두리가 있는 [CupertinoLiquidButton] 모양을 사용합니다.
 *
 * Material Design에서는 [Button] 컴포넌트를 사용하고, Cupertino 테마에서는 [CupertinoLiquidButton] 컴포넌트를 사용하여
 * 운영체제별로 적절한 UI를 제공합니다.
 *
 * @param onClick 버튼이 클릭되었을 때 호출되는 함수
 * @param modifier 버튼에 적용할 Modifier
 * @param enabled 버튼이 활성화되어 있는지 여부
 * @param interactionSource 상호작용 소스
 * @param adaptation [CupertinoButtonAdaptation]와 [MaterialButtonAdaptation]에 대한 사용자 정의 설정 함수
 * @param content 버튼 내부에 표시될 내용
 * @see AdaptiveWidget
 * @see CupertinoButton
 * @see Button
 */
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
 * 적응형 버튼으로, Material [TextButton] 또는 테두리가 없는 [CupertinoLiquidButton] 모양을 사용합니다.
 *
 * Material Design에서는 [TextButton] 컴포넌트를 사용하고, Cupertino 테마에서는 [CupertinoLiquidButton] 컴포넌트를 사용하여
 * 운영체제별로 적절한 UI를 제공합니다.
 *
 * @param onClick 버튼이 클릭되었을 때 호출되는 함수
 * @param modifier 버튼에 적용할 Modifier
 * @param enabled 버튼이 활성화되어 있는지 여부
 * @param interactionSource 상호작용 소스
 * @param adaptation [CupertinoButtonAdaptation]와 [MaterialButtonAdaptation]에 대한 사용자 정의 설정 함수
 * @param content 버튼 내부에 표시될 내용
 * @see AdaptiveWidget
 * @see CupertinoButton
 * @see TextButton
 */
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
 * 적응형 버튼으로, Material [FilledTonalButton] 또는 테두리가 있는 [CupertinoButton] 모양을 사용합니다.
 *
 * Material Design에서는 [FilledTonalButton] 컴포넌트를 사용하고, Cupertino 테마에서는 [CupertinoLiquidButton] 컴포넌트를 사용하여
 * 운영체제별로 적절한 UI를 제공합니다.
 *
 * @param onClick 버튼이 클릭되었을 때 호출되는 함수
 * @param modifier 버튼에 적용할 Modifier
 * @param enabled 버튼이 활성화되어 있는지 여부
 * @param interactionSource 상호작용 소스
 * @param adaptation [CupertinoButtonAdaptation]와 [MaterialButtonAdaptation]에 대한 사용자 정의 설정 함수
 * @param content 버튼 내부에 표시될 내용
 * @see AdaptiveWidget
 * @see CupertinoButton
 * @see FilledTonalButton
 */
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
/**
 * [CupertinoButton]에 대한 적응형 어댑테이션 클래스로, Cupertino 버튼 스타일의 다양한 속성을 관리합니다.
 *
 * @param colors [CupertinoLiquidButtonColors] - 버튼의 색상 설정
 * @param backdrop [LayerBackdrop] - 버튼의 레이어 배경 설정
 * @param isBackgroundAdaptive 배경이 적응형으로 처리되는지 여부
 * @see CupertinoLiquidButtonColors
 * @see LayerBackdrop
 * @see CupertinoButtonSize
 * @see Shape
 * @see PaddingValues
 */
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
/**
 * [MaterialButton]에 대한 적응형 어댑테이션 클래스로, Material 버튼 스타일의 다양한 속성을 관리합니다.
 *
 * @param colors [ButtonColors] - 버튼의 색상 설정
 * @param elevation [ButtonElevation] - 버튼의 그림자 효과
 * @param shape [Shape] - 버튼의 모양
 * @param contentPadding [PaddingValues] - 버튼 내용의 패딩
 * @param border [BorderStroke] - 버튼 테두리 스타일
 * @see ButtonColors
 * @see ButtonElevation
 * @see Shape
 * @see PaddingValues
 * @see BorderStroke
 */
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
/**
 * 버튼 유형에 따라 적응형 어댑테이션을 제공하는 클래스입니다.
 *
 * @param type [ButtonType] - 버튼의 유형 (Filled, Text, Tonal)
 * @see CupertinoButtonAdaptation
 * @see MaterialButtonAdaptation
 */
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
        val backdrop = rememberLayerBackdrop()
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