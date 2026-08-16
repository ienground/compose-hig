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

@file:OptIn(ExperimentalFoundationApi::class)

package zone.ien.hig


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.kyant.capsule.ContinuousRoundedRectangle
import zone.ien.hig.section.CupertinoSectionDefaults
import zone.ien.hig.swipebox.AnchorsEffect
import zone.ien.hig.swipebox.CupertinoSwipeActionPosition
import zone.ien.hig.swipebox.CupertinoSwipeBoxActionsBuilder
import zone.ien.hig.swipebox.DismissFullyExpandedEffect
import zone.ien.hig.swipebox.HapticFeedbackEffect
import zone.ien.hig.swipebox.LocalSwipeActionPosition
import zone.ien.hig.swipebox.LocalSwipeBoxItemExpanding
import zone.ien.hig.swipebox.LocalSwipeBoxItemFullSwipe
import zone.ien.hig.swipebox.LocalSwipeBoxItemRevealScale
import zone.ien.hig.swipebox.LocalSwipeBoxItemWidth
import zone.ien.hig.swipebox.LocalSwipeBoxState
import zone.ien.hig.swipebox.SwipeBoxStates
import zone.ien.hig.swipebox.rememberCupertinoSwipeBoxState
import zone.ien.hig.theme.CupertinoTheme
import kotlin.math.roundToInt

/**
 * Default parameters and constants used by [CupertinoSwipeBox].
 */
object CupertinoSwipeBoxDefaults {
    val allowFullSwipe = true
    val velocityThreshold = Float.POSITIVE_INFINITY
    val actionItemHorizontalPadding = 4.dp
    val actionItemVerticalPadding = 10.dp
    val actionItemSize = 52.dp
    val actionItemWidth = actionItemHorizontalPadding + actionItemSize + actionItemHorizontalPadding
    val actionItemHeight = actionItemVerticalPadding + actionItemSize + actionItemVerticalPadding
    val animationSpec: SpringSpec<Float> = SpringSpec(
        stiffness = Spring.StiffnessMedium,
        dampingRatio = Spring.DampingRatioNoBouncy
    )
}

/**
 * Swipe box container that displays actions for a list item with iOS HIG swipe gestures and haptic feedback.
 *
 * Actions are defined using the [actionItemBuilder] DSL block where start and end actions can be declared
 * via [CupertinoSwipeBoxActionsBuilder.startActions] and [CupertinoSwipeBoxActionsBuilder.endActions].
 *
 * @param state Swipe box state controlling drag offsets and anchors. See [rememberCupertinoSwipeBoxState].
 * @param modifier Modifier applied to the outer container.
 * @param itemWidth Base width of individual action items when revealed.
 * @param height Height of the action item area.
 * @param startToEndFullSwipeEnabled Whether swiping fully from start to end automatically triggers the primary start action.
 * @param endToStartFullSwipeEnabled Whether swiping fully from end to start automatically triggers the primary end action.
 * @param actionItemBuilder DSL builder for configuring start and end action items using [CupertinoSwipeBoxItem].
 * @param content Foreground content of the swipe box (e.g. list item row).
 *
 * @see CupertinoSwipeBoxItem
 * */
@OptIn(ExperimentalFoundationApi::class, InternalCupertinoApi::class)
@Composable
fun CupertinoSwipeBox(
    state: AnchoredDraggableState<SwipeBoxStates> = rememberCupertinoSwipeBoxState(),
    modifier: Modifier = Modifier,
    itemWidth: Dp = CupertinoSwipeBoxDefaults.actionItemWidth,
    height: Dp = CupertinoSwipeBoxDefaults.actionItemHeight,
    startToEndFullSwipeEnabled: Boolean = CupertinoSwipeBoxDefaults.allowFullSwipe,
    endToStartFullSwipeEnabled: Boolean = CupertinoSwipeBoxDefaults.allowFullSwipe,
    actionItemBuilder: CupertinoSwipeBoxActionsBuilder.() -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val density = LocalDensity.current
    var parentWidth by remember { mutableStateOf(0) }
    val actionItems = CupertinoSwipeBoxActionsBuilder().apply(actionItemBuilder)
    val startActionsSize = actionItems.startActions.size
    val endActionsSize = actionItems.endActions.size
    val isStartActionItemSupplied = startActionsSize != 0
    val isEndActionItemSupplied = endActionsSize != 0
    val startFullSwipeAction = actionItems.startActions.firstOrNull()?.onClick
    val endFullSwipeAction = actionItems.endActions.lastOrNull()?.onClick

    val hapticFeedback = LocalHapticFeedback.current
    var hasTriggeredHapticFeedback by remember { mutableStateOf(false) }
    var anchorsInitialized by remember { mutableStateOf(false) }

    // Store state of fully expanded
    val isFullyExpandedStart = remember { mutableStateOf(false) }
    val isFullyExpandedEnd = remember { mutableStateOf(false) }

    AnchorsEffect(
        parentWidth = parentWidth,
        fullExpansionStart = startToEndFullSwipeEnabled,
        isStartActionItemSupplied = isStartActionItemSupplied,
        fullExpansionEnd = endToStartFullSwipeEnabled,
        isEndActionItemSupplied = isEndActionItemSupplied,
        swipeBoxState = state,
        density = density,
        amountOfStartActionItems = startActionsSize,
        amountOfEndActionItems = endActionsSize,
        actionItemWidth = itemWidth,
        actionRowOuterPadding = CupertinoSwipeBoxDefaults.actionItemHorizontalPadding * 2,
    ) { anchorsInitialized = it }

    HapticFeedbackEffect(
        fullExpansionStart = startToEndFullSwipeEnabled,
        fullExpansionEnd = endToStartFullSwipeEnabled,
        isFullyExpandedStart = isFullyExpandedStart,
        isFullyExpandedEnd = isFullyExpandedEnd,
        swipeBoxState = state,
        hapticFeedback = hapticFeedback,
        hasTriggeredHapticFeedback = hasTriggeredHapticFeedback
    ) { hasTriggeredHapticFeedback = it }

    DismissFullyExpandedEffect(
        swipeBoxState = state,
        isStartActionItemSupplied = isStartActionItemSupplied,
        fullExpansionStart = startToEndFullSwipeEnabled,
        isEndActionItemSupplied = isEndActionItemSupplied,
        fullExpansionEnd = endToStartFullSwipeEnabled,
        startFullExpansionOnClick = startFullSwipeAction,
        endFullExpansionOnClick = endFullSwipeAction
    )

    CompositionLocalProvider(
        LocalSwipeBoxState provides state
    ) {
        Box(
            modifier = modifier.then(
                Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { coordinates ->
                        parentWidth = coordinates.size.width
                    }
            )
        ) {
            val offset by remember {
                derivedStateOf {
                    if (anchorsInitialized) state.offset else 0f
                }
            }
            val revealedWidth = with(density) { kotlin.math.abs(offset).toDp() }
            val isSwiping = offset != 0f
            val foregroundColor by animateColorAsState(
                targetValue = if (isSwiping) {
                    CupertinoTheme.colorScheme.secondarySystemFill
                } else {
                    LocalContainerColor.current
                },
                animationSpec = cupertinoTween(),
            )
            val foregroundCornerRadius by animateDpAsState(
                targetValue = if (isSwiping) 18.dp else 0.dp,
                animationSpec = cupertinoTween(),
            )

            if (offset > 0 && isStartActionItemSupplied) {
                CompositionLocalProvider(
                    LocalSwipeActionPosition provides CupertinoSwipeActionPosition.Start
                ) {
                    Box(
                        modifier = Modifier
                            .height(height)
                            .width(revealedWidth)
                            .clipToBounds()
                            .align(Alignment.CenterStart)
                    ) {
                        val actionRowOuterPadding =
                            CupertinoSwipeBoxDefaults.actionItemHorizontalPadding * 2
                        val revealedActionContentWidth =
                            (revealedWidth - actionRowOuterPadding).coerceAtLeast(0.dp)
                        val normalActionRowWidth =
                            itemWidth * startActionsSize + actionRowOuterPadding
                        val actionRowWidth =
                            if (revealedWidth > normalActionRowWidth) {
                                revealedWidth
                            } else {
                                normalActionRowWidth
                            }
                        SwipeActionRow(
                            width = actionRowWidth,
                            alignToEnd = false,
                        ) {
                            actionItems.startActions.forEachIndexed { index, swipeAction ->
                                val revealScale =
                                    ((revealedActionContentWidth - itemWidth * index) / itemWidth)
                                        .coerceIn(0f, 1f)
                                CompositionLocalProvider(
                                    LocalSwipeBoxItemFullSwipe provides (index == 0),
                                    LocalSwipeBoxItemExpanding provides
                                        (
                                            index == 0 &&
                                                (
                                                    revealedWidth > normalActionRowWidth ||
                                                        state.targetValue ==
                                                        SwipeBoxStates.StartFullyExpanded
                                                    )
                                            ),
                                    LocalSwipeBoxItemWidth provides itemWidth,
                                    LocalSwipeBoxItemRevealScale provides revealScale,
                                ) {
                                    key(swipeAction.key) {
                                        swipeAction.content(this)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (offset < 0 && isEndActionItemSupplied) {
                CompositionLocalProvider(
                    LocalSwipeActionPosition provides CupertinoSwipeActionPosition.End
                ) {
                    Box(
                        modifier = Modifier
                            .height(height)
                            .width(revealedWidth)
                            .clipToBounds()
                            .align(Alignment.CenterEnd)
                    ) {
                        val actionRowOuterPadding =
                            CupertinoSwipeBoxDefaults.actionItemHorizontalPadding * 2
                        val revealedActionContentWidth =
                            (revealedWidth - actionRowOuterPadding).coerceAtLeast(0.dp)
                        val normalActionRowWidth =
                            itemWidth * endActionsSize + actionRowOuterPadding
                        val actionRowWidth =
                            if (revealedWidth > normalActionRowWidth) {
                                revealedWidth
                            } else {
                                normalActionRowWidth
                            }
                        SwipeActionRow(
                            width = actionRowWidth,
                            alignToEnd = true,
                        ) {
                            actionItems.endActions.forEachIndexed { index, swipeAction ->
                                val revealOrder = actionItems.endActions.lastIndex - index
                                val revealScale =
                                    ((revealedActionContentWidth - itemWidth * revealOrder) / itemWidth)
                                        .coerceIn(0f, 1f)
                                CompositionLocalProvider(
                                    LocalSwipeBoxItemFullSwipe provides
                                        (index == actionItems.endActions.lastIndex),
                                    LocalSwipeBoxItemExpanding provides
                                        (
                                            index == actionItems.endActions.lastIndex &&
                                                (
                                                    revealedWidth > normalActionRowWidth ||
                                                        state.targetValue ==
                                                        SwipeBoxStates.EndFullyExpanded
                                                    )
                                        ),
                                    LocalSwipeBoxItemWidth provides itemWidth,
                                    LocalSwipeBoxItemRevealScale provides revealScale,
                                ) {
                                    key(swipeAction.key) {
                                        swipeAction.content(this)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (anchorsInitialized) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(state.requireOffset().roundToInt(), 0) }
                        .anchoredDraggable(
                            state = state,
                            orientation = Orientation.Horizontal
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .height(height)
                            .clip(ContinuousRoundedRectangle(foregroundCornerRadius))
                            .background(foregroundColor)
                            .padding(
                                start = CupertinoSectionDefaults.PaddingValues
                                    .calculateStartPadding(LocalLayoutDirection.current),
                                end = CupertinoSectionDefaults.PaddingValues
                                    .calculateStartPadding(LocalLayoutDirection.current)
                            )
                    ) {
                        content()
                    }
                }
            }
        }
    }
}

@Composable
private fun SwipeActionRow(
    width: Dp,
    alignToEnd: Boolean,
    content: @Composable RowScope.() -> Unit,
) {
    Layout(
        modifier = Modifier.fillMaxSize(),
        content = {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = CupertinoSwipeBoxDefaults.actionItemHorizontalPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (alignToEnd) Arrangement.End else Arrangement.Start,
                content = content,
            )
        },
    ) { measurables, constraints ->
        val rowWidth = width.roundToPx()
        val rowHeight = constraints.maxHeight
        val placeable = measurables.single().measure(
            Constraints.fixed(rowWidth, rowHeight),
        )

        layout(constraints.maxWidth, rowHeight) {
            val x = if (alignToEnd) constraints.maxWidth - rowWidth else 0
            placeable.placeRelative(x, 0)
        }
    }
}
