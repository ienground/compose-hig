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

package zone.ien.hig.swipebox

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.kyant.capsule.ContinuousCapsule
import com.kyant.capsule.ContinuousRoundedRectangle
import zone.ien.hig.CupertinoIcon
import zone.ien.hig.CupertinoSwipeBoxDefaults
import zone.ien.hig.CupertinoText
import zone.ien.hig.ExperimentalCupertinoApi
import zone.ien.hig.LocalContentColor
import zone.ien.hig.ProvideTextStyle
import zone.ien.hig.cupertinoTween
import zone.ien.hig.theme.CupertinoColors
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.theme.White
import kotlinx.coroutines.launch

/**
 * TODO javadocs
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
@ExperimentalCupertinoApi
fun RowScope.CupertinoSwipeBoxItem(
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    restoreOnClick: Boolean = true,
    onClickLabel: String? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    icon: ImageVector? = null,
    label: String? = null,
    weight: Float = 1f,
    shape: Shape = ContinuousCapsule(),
) {
    val state = LocalSwipeBoxState.current
    val actionPosition = LocalSwipeActionPosition.current
    val isFullSwipeActionItem = LocalSwipeBoxItemFullSwipe.current
    val itemWidth = LocalSwipeBoxItemWidth.current
    val revealScale = LocalSwipeBoxItemRevealScale.current
    val isExpanding = LocalSwipeBoxItemExpanding.current
    val collapsedItemSize =
        (itemWidth - CupertinoSwipeBoxDefaults.actionItemHorizontalPadding * 2)
            .coerceAtLeast(0.dp)
    val isFullSwipeSettled =
        state.settledValue == SwipeBoxStates.EndFullyExpanded ||
            state.settledValue == SwipeBoxStates.StartFullyExpanded
    val isFullSwipeTarget =
        state.targetValue == SwipeBoxStates.EndFullyExpanded ||
            state.targetValue == SwipeBoxStates.StartFullyExpanded
    val shouldRenderItem =
        !isFullSwipeSettled || isFullSwipeActionItem

    val zIndex = if (isFullSwipeActionItem) 1f else 0f

    val coroutineScope = rememberCoroutineScope()
    val currentOnClick by rememberUpdatedState(onClick)

    val animatedItemWidth by animateDpAsState(
        targetValue = if (shouldRenderItem) itemWidth * weight else 0.dp,
        animationSpec = cupertinoTween(),
    )
    val animatedRevealScale by animateFloatAsState(
        targetValue = revealScale,
        animationSpec = spring(
            dampingRatio = 0.68f,
            stiffness = 500f,
        ),
    )
    val animatedItemAlpha by animateFloatAsState(
        targetValue =
            if (isFullSwipeTarget && !isFullSwipeActionItem) {
                0.35f
            } else {
                revealScale
            },
        animationSpec = cupertinoTween(),
    )

    val animHorizontalBias by animateFloatAsState(
        when {
            isFullSwipeActionItem &&
                (state.targetValue == SwipeBoxStates.EndFullyExpanded) &&
                (actionPosition == CupertinoSwipeActionPosition.End) -> -1f
            isFullSwipeActionItem &&
                (state.targetValue == SwipeBoxStates.StartFullyExpanded) &&
                (actionPosition == CupertinoSwipeActionPosition.Start) -> 1f
            else -> 0f
        },
        animationSpec = cupertinoTween(),
    )

    // Set content color and typography style using CompositionLocalProvider
    CompositionLocalProvider(LocalContentColor provides CupertinoColors.White) {
        ProvideTextStyle(CupertinoTheme.typography.footnote) {
            Box(
                modifier =
                    modifier
                        .then(
                            if (isFullSwipeActionItem) {
                                Modifier.weight(weight)
                            } else {
                                Modifier.width(animatedItemWidth)
                            }
                        )
                        .zIndex(zIndex)
                        .fillMaxHeight()
                        .padding(
                            horizontal = CupertinoSwipeBoxDefaults.actionItemHorizontalPadding,
                            vertical = CupertinoSwipeBoxDefaults.actionItemVerticalPadding,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .then(
                            if (isExpanding) {
                                Modifier
                                    .fillMaxWidth()
                                    .requiredHeight(collapsedItemSize)
                            } else {
                                Modifier.requiredSize(collapsedItemSize)
                            }
                        )
                        .graphicsLayer {
                            val buttonScale =
                                maxOf(revealScale, animatedRevealScale).coerceIn(0f, 1f)
                            scaleX = buttonScale
                            scaleY = buttonScale
                            alpha = animatedItemAlpha
                            transformOrigin = TransformOrigin(
                                pivotFractionX =
                                    if (actionPosition == CupertinoSwipeActionPosition.End) {
                                        1f
                                    } else {
                                        0f
                                    },
                                pivotFractionY = 0.5f,
                            )
                        }
                        .clip(shape)
                        .background(color)
                        .clickable(
                            enabled = enabled,
                            indication = LocalIndication.current,
                            interactionSource = interactionSource,
                            onClick = {
                                currentOnClick()
                                if (restoreOnClick) {
                                    coroutineScope.launch {
                                        state.animateTo(SwipeBoxStates.Resting)
                                    }
                                }
                            },
                            onClickLabel = onClickLabel,
                            role = Role.Button,
                        )
                        .padding(horizontal = 10.dp),
                // TODO: hardcoded removal
                contentAlignment =
                    BiasAlignment(
                        verticalBias = 0f,
                        horizontalBias = animHorizontalBias,
                    ),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    icon?.let {
                        CupertinoIcon(
                            imageVector = it,
                            contentDescription = onClickLabel,
                            tint = CupertinoColors.White,
                            modifier = Modifier.requiredSize(16.dp),
                        )
                    }

                    label?.let {
                        CupertinoText(
                            it,
                            fontSize = 12.sp,
                            maxLines = 1,
                        )
                    }
                }
            }
            }
        }
    }
}
