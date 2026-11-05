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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import zone.ien.hig.theme.CupertinoTheme

/**
 * 4포인트 높이의 선형 진행 표시기입니다.
 *
 * 진행률은 0에서 1 사이의 분수로 지정하며, RTL 레이아웃에서는 시작 가장자리에서 채워집니다.
 *
 * @param progress 0에서 1 사이의 진행률
 * @param modifier 루트 레이아웃에 적용할 수정자
 * @param label 표시기 위에 선택적으로 표시할 레이블
 */
@ExperimentalCupertinoApi
@Composable
fun CupertinoProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    label: String? = null,
) {
    require(progress.isFinite() && progress in 0f..1f) {
        "진행률은 0에서 1 사이의 유한한 값이어야 합니다."
    }

    val colorScheme = CupertinoTheme.colorScheme
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ProgressIndicatorTokens.LabelSpacing),
    ) {
        if (label != null) {
            CupertinoText(
                text = label,
                style = CupertinoTheme.typography.body,
                color = colorScheme.label,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ProgressIndicatorTokens.TrackHeight)
                .clip(CircleShape)
                .background(colorScheme.systemFill)
                .progressSemantics(progress),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .then(
                        if (progress == 0f) {
                            Modifier.width(ProgressIndicatorTokens.ZeroProgressWidth)
                        } else {
                            Modifier
                                .widthIn(min = ProgressIndicatorTokens.ZeroProgressWidth)
                                .fillMaxWidth(progress)
                        },
                    )
                    .fillMaxHeight()
                    .background(colorScheme.accent, CircleShape),
            )
        }
    }
}

private object ProgressIndicatorTokens {
    val TrackHeight = 4.dp
    val LabelSpacing = 3.dp
    val ZeroProgressWidth = 6.dp
}
