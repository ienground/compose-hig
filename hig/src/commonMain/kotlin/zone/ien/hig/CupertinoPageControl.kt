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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.drawBackdrop
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.utils.LocalCupertinoBackdrop
import zone.ien.hig.utils.cupertinoGlassEffects
import zone.ien.hig.utils.rememberDefaultBackdrop

/**
 * 캡슐 배경 안에 페이지 선택 점을 표시하는 컨트롤입니다.
 *
 * 최대 9개의 점을 표시하고, 페이지가 더 많으면 현재 페이지 주변의 점을 보여 줍니다.
 *
 * @param pageCount 전체 페이지 수
 * @param currentPage 현재 선택된 0 기반 페이지 인덱스
 * @param onPageSelected 다른 페이지를 선택할 때 호출되는 콜백
 * @param modifier 루트 레이아웃에 적용할 수정자
 * @param pageDescription 페이지 점의 접근성 설명을 만드는 함수. 페이지 인덱스는 0 기반입니다.
 */
@ExperimentalCupertinoApi
@Composable
fun CupertinoPageControl(
    pageCount: Int,
    currentPage: Int,
    onPageSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    pageDescription: (page: Int, pageCount: Int) -> String = { page, count ->
        "Page ${page + 1} of $count"
    },
) {
    require(pageCount > 0) { "페이지 수는 1 이상이어야 합니다." }
    require(currentPage in 0 until pageCount) { "현재 페이지는 전체 페이지 범위 안에 있어야 합니다." }

    val displayedCount = pageCount.coerceAtMost(PageControlTokens.MaxDisplayedPages)
    val firstPage =
        if (pageCount > displayedCount) {
            (currentPage - displayedCount / 2).coerceIn(0, pageCount - displayedCount)
        } else {
            0
        }
    val colorScheme = CupertinoTheme.colorScheme
    val pageSpacing =
        if (pageCount == 7) {
            PageControlTokens.SevenPageSpacing
        } else {
            PageControlTokens.PageSpacing
        }
    val groupWidth =
        (pageSpacing + PageControlTokens.IndicatorSize) * (displayedCount - 1).toFloat() +
            PageControlTokens.IndicatorSize -
            (if (pageCount >= 8) 4.dp else 0.dp)
    val capsuleWidth = groupWidth + PageControlTokens.HorizontalPadding * 2
    val targetWidth = if (pageCount == 7) 18.dp else 16.dp
    val backdrop = LocalCupertinoBackdrop.current ?: rememberDefaultBackdrop()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(PageControlTokens.ComponentHeight),
        contentAlignment = Alignment.Center,
    ) {
        CupertinoSurface(
            modifier = Modifier
                .width(capsuleWidth)
                .height(PageControlTokens.Height)
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { CircleShape },
                    effects = { cupertinoGlassEffects(PageControlTokens.BlurRadius.toPx()) },
                    highlight = { null },
                    shadow = { null },
                    onDrawSurface = {
                        drawRect(Color.White.copy(alpha = PageControlTokens.BackgroundAlpha))
                        drawRect(
                            color = Color.White.copy(alpha = PageControlTokens.HighlightAlpha),
                            blendMode = BlendMode.ColorDodge,
                        )
                    },
                ),
            shape = CircleShape,
            color = Color.Transparent,
            contentColor = colorScheme.label,
        ) {}

        Row(
            modifier = Modifier.selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(displayedCount) { visibleIndex ->
                val page = firstPage + visibleIndex
                val selected = page == currentPage
                val isEdge = visibleIndex == 0 || visibleIndex == displayedCount - 1
                val indicatorSize =
                    if (isEdge && pageCount >= 7 && !selected) {
                        PageControlTokens.SmallIndicatorSize
                    } else {
                        PageControlTokens.IndicatorSize
                    }

                Box(
                    modifier = Modifier
                        .width(targetWidth)
                        .height(PageControlTokens.TouchTargetHeight)
                        .selectable(
                            selected = selected,
                            onClick = { onPageSelected(page) },
                            interactionSource = null,
                            indication = null,
                            role = Role.RadioButton,
                        )
                        .semantics {
                            contentDescription = pageDescription(page, pageCount)
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        Modifier
                            .size(indicatorSize)
                            .background(
                                color = if (selected) colorScheme.label else colorScheme.tertiaryLabel,
                                shape = CircleShape,
                            ),
                    )
                }
            }
        }
    }
}

private object PageControlTokens {
    val ComponentHeight = 44.dp
    val Height = 24.dp
    val TouchTargetHeight = 44.dp
    val HorizontalPadding = 12.dp
    val PageSpacing = 8.dp
    val SevenPageSpacing = 10.dp
    val IndicatorSize = 8.dp
    val SmallIndicatorSize = 6.dp
    const val MaxDisplayedPages = 9

    const val BackgroundAlpha = 0.07f

    const val HighlightAlpha = 0.03f

    val BlurRadius = 50.dp
}
