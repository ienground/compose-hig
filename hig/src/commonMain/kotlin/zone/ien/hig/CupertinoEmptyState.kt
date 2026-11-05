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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.utils.LocalCupertinoBackdrop
import zone.ien.hig.utils.rememberDefaultBackdrop

/**
 * 빈 화면 상태를 중앙 정렬된 내용과 선택 가능한 하단 Liquid Glass 액션으로 표시합니다.
 *
 * 제목과 설명 슬롯에는 [CupertinoText]를 사용하면 이 컴포넌트의 타이포그래피와 색상을 상속합니다.
 * 아이콘 슬롯은 필요한 크기와 배경을 직접 지정합니다.
 *
 * @param title 제목 콘텐츠 슬롯
 * @param modifier 루트 레이아웃에 적용할 수정자
 * @param description 제목 아래에 표시할 설명 슬롯
 * @param icon 제목 위에 표시할 아이콘 슬롯
 * @param onAction 액션 버튼을 눌렀을 때 호출되는 콜백. `null`이면 버튼은 비활성화됩니다.
 * @param action Liquid Glass 액션 버튼 안에 표시할 콘텐츠 슬롯
 */
@ExperimentalCupertinoApi
@Composable
fun CupertinoEmptyState(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    description: (@Composable () -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    onAction: (() -> Unit)? = null,
    action: (@Composable RowScope.() -> Unit)? = null,
) {
    val colorScheme = CupertinoTheme.colorScheme
    val titleStyle =
        CupertinoTheme.typography.title2.copy(
            lineHeight = EmptyStateTokens.TextLineHeight,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = colorScheme.label,
        )
    val descriptionStyle =
        CupertinoTheme.typography.title2.copy(
            lineHeight = EmptyStateTokens.TextLineHeight,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = colorScheme.secondaryLabel,
        )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = EmptyStateTokens.HorizontalPadding)
            .padding(bottom = EmptyStateTokens.BottomPadding),
    ) {
        val hasBoundedHeight = constraints.hasBoundedHeight

        Column(
            modifier = if (hasBoundedHeight) Modifier.fillMaxSize() else Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = (if (hasBoundedHeight) Modifier.weight(1f) else Modifier)
                    .fillMaxWidth()
                    .padding(horizontal = EmptyStateTokens.ContentHorizontalPadding),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(EmptyStateTokens.IconSpacing),
                ) {
                    icon?.invoke()
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(0.dp),
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor provides colorScheme.label,
                        ) {
                            ProvideTextStyle(titleStyle, content = title)
                        }
                        if (description != null) {
                            CompositionLocalProvider(
                                LocalContentColor provides colorScheme.secondaryLabel,
                            ) {
                                ProvideTextStyle(descriptionStyle, content = description)
                            }
                        }
                    }
                }
            }

            val actionContent = action
            if (actionContent != null) {
                CupertinoLiquidButton(
                    onClick = onAction ?: {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(EmptyStateTokens.ActionHeight),
                    enabled = onAction != null,
                    size = CupertinoButtonSize.Large,
                    contentPadding = PaddingValues(
                        horizontal = EmptyStateTokens.ActionHorizontalPadding,
                        vertical = EmptyStateTokens.ActionVerticalPadding,
                    ),
                    backdrop = LocalCupertinoBackdrop.current ?: rememberDefaultBackdrop(),
                ) {
                    ProvideTextStyle(
                        CupertinoTheme.typography.body.copy(
                            fontWeight = FontWeight.Medium,
                            lineHeight = 20.sp,
                        ),
                    ) {
                        actionContent()
                    }
                }
            }
        }
    }
}

private object EmptyStateTokens {
    val HorizontalPadding = 36.dp
    val ContentHorizontalPadding = 16.dp
    val BottomPadding = 36.dp
    val IconSpacing = 16.dp
    val TextLineHeight = 26.sp
    val ActionHeight = 52.dp
    val ActionHorizontalPadding = 20.dp
    val ActionVerticalPadding = 16.dp
}
