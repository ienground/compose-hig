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

package zone.ien.hig

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.key
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import zone.ien.hig.icons.CupertinoIcons
import zone.ien.hig.icons.outlined.Plus
import zone.ien.hig.theme.CupertinoTheme

/**
 * 92×32 포인트의 캡슐형 값 조절기입니다.
 *
 * 양쪽 버튼은 범위 끝에서 비활성화되며, 키보드 포커스가 있는 동안 방향키로도 값을 조절할 수 있습니다.
 *
 * @param value 현재 값
 * @param onValueChange 값이 바뀔 때 호출되는 콜백
 * @param modifier 루트 레이아웃에 적용할 수정자
 * @param valueRange 선택 가능한 값의 범위
 * @param enabled 조절기의 활성화 여부
 * @param decrementLabel 감소 버튼의 접근성 설명
 * @param incrementLabel 증가 버튼의 접근성 설명
 */
@ExperimentalCupertinoApi
@Composable
fun CupertinoStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: IntRange = 0..Int.MAX_VALUE,
    enabled: Boolean = true,
    decrementLabel: String = "Decrease",
    incrementLabel: String = "Increase",
) {
    require(!valueRange.isEmpty()) { "값 범위는 비어 있을 수 없습니다." }
    require(value in valueRange) { "현재 값은 지정된 범위 안에 있어야 합니다." }

    val colorScheme = CupertinoTheme.colorScheme
    val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
    val canDecrement = enabled && value > valueRange.first
    val canIncrement = enabled && value < valueRange.last

    CupertinoSurface(
        modifier = modifier
            .size(width = 92.dp, height = 32.dp)
            .semantics { isTraversalGroup = true }
            .onPreviewKeyEvent { event ->
                if (!enabled || event.type != KeyEventType.KeyDown) {
                    return@onPreviewKeyEvent false
                }

                val delta =
                    when (event.key) {
                        Key.DirectionUp -> 1
                        Key.DirectionDown -> -1
                        Key.DirectionLeft -> if (isLtr) -1 else 1
                        Key.DirectionRight -> if (isLtr) 1 else -1
                        else -> return@onPreviewKeyEvent false
                    }

                when {
                    delta < 0 && canDecrement -> onValueChange(value - 1)
                    delta > 0 && canIncrement -> onValueChange(value + 1)
                }
                true
            }
            .focusable(enabled),
        shape = CircleShape,
        color = colorScheme.tertiarySystemFill,
        contentColor = colorScheme.label,
    ) {
        Box(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxSize()) {
                StepperButton(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    label = decrementLabel,
                    valueDescription = value.toString(),
                    enabled = canDecrement,
                    iconColor = if (canDecrement) colorScheme.label else colorScheme.tertiaryLabel,
                    isIncrement = false,
                    onClick = { onValueChange(value - 1) },
                )
                StepperButton(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    label = incrementLabel,
                    valueDescription = value.toString(),
                    enabled = canIncrement,
                    iconColor = if (canIncrement) colorScheme.label else colorScheme.tertiaryLabel,
                    isIncrement = true,
                    onClick = { onValueChange(value + 1) },
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(width = 1.dp, height = 24.dp)
                    .background(colorScheme.tertiaryLabel, CircleShape),
            )
        }
    }
}

@Composable
private fun StepperButton(
    modifier: Modifier,
    label: String,
    valueDescription: String,
    enabled: Boolean,
    iconColor: Color,
    isIncrement: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .semantics {
                contentDescription = label
                stateDescription = valueDescription
            },
        contentAlignment = Alignment.Center,
    ) {
        if (isIncrement) {
            CupertinoIcon(
                imageVector = CupertinoIcons.Outlined.Plus,
                contentDescription = null,
                modifier = Modifier.size(17.dp),
                tint = iconColor,
            )
        } else {
            Canvas(Modifier.size(17.dp)) {
                val halfLine = size.width * 0.36f
                drawLine(
                    color = iconColor,
                    start = Offset(size.width / 2f - halfLine, size.height / 2f),
                    end = Offset(size.width / 2f + halfLine, size.height / 2f),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}
