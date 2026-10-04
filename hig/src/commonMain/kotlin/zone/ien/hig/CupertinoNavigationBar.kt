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

/**
 * Contains the implementation of the Cupertino navigation bar.
 */
package zone.ien.hig

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.util.lerp
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberCombinedBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.backdrop.shadow.Shadow
import com.kyant.capsule.ContinuousCapsule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.utils.CupertinoGlassDefaults
import zone.ien.hig.utils.DampedDragAnimation
import zone.ien.hig.utils.InteractiveHighlight
import zone.ien.hig.utils.glassEdge
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sign

private val NavBarPadding = 4.dp
private val NavBarItemGap = 0.dp
private val NavBarItemMinWidth = 90.dp  // Fixed width when items are few

/**
 * Cupertino 하단 탐색 탭 막대입니다.
 *
 * 탭 콘텐츠에는 [CupertinoNavigationBarItem]을 사용합니다. 배경 유리 효과는 [backdrop]에서 읽습니다.
 *
 * 강조 탭은 강조 인덱스를 받는 오버로드와 인덱스를 지정하는 항목 오버로드에서 선택할 수 있습니다.
 * [trailingAction]은 탭과 분리된 trailing 액션입니다. 액션의 너비와 높이는 탭 capsule 높이에 맞춰집니다.
 */
@Composable
@ExperimentalCupertinoApi
fun CupertinoNavigationBar(
    modifier: Modifier = Modifier,
    colors: CupertinoNavigationBarColors = CupertinoNavigationBarDefaults.colors(),
    windowInsets: WindowInsets = CupertinoNavigationBarDefaults.windowInsets,
    backdrop: LayerBackdrop,
    selectedTabIndex: () -> Int,
    onTabSelected: (index: Int) -> Unit,
    tabsCount: Int,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    trailingAction: (@Composable BoxScope.() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    if (trailingAction != null) {
        val density = LocalDensity.current
        var navigationHeight by remember(density) { mutableStateOf(64.dp) }
        Row(
            modifier = modifier.fillMaxWidth().windowInsetsPadding(windowInsets),
            verticalAlignment = Alignment.Bottom,
        ) {
            CupertinoNavigationBar(
                modifier = Modifier.weight(1f).onSizeChanged {
                    navigationHeight = with(density) {
                        (it.height.toDp() - CupertinoNavigationBarDefaults.BottomPadding).coerceAtLeast(0.dp)
                    }
                },
                colors = colors,
                windowInsets = WindowInsets(0, 0, 0, 0),
                backdrop = backdrop,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = onTabSelected,
                tabsCount = tabsCount,
                horizontalAlignment = Alignment.Start,
                content = content,
            )
            Box(
                Modifier.padding(start = 12.dp, bottom = CupertinoNavigationBarDefaults.BottomPadding)
                    .size(navigationHeight),
                contentAlignment = Alignment.Center,
                propagateMinConstraints = true,
                content = trailingAction,
            )
        }
        return
    }
    val navigationBarOptions = LocalCupertinoNavigationBarOptions.current
    val prominentTabIndex = navigationBarOptions?.prominentTabIndex
    if (prominentTabIndex != null) {
        CupertinoProminentNavigationBar(
            modifier = modifier,
            colors = colors,
            windowInsets = windowInsets,
            backdrop = backdrop,
            selectedTabIndex = selectedTabIndex,
            onTabSelected = onTabSelected,
            tabsCount = tabsCount,
            options = navigationBarOptions,
            content = content,
        )
        return
    }
    val displayedTabsCount = tabsCount
    val tabsBackdrop = rememberLayerBackdrop()
    val accentColor = colors.accentColor
    val containerColor = colors.containerColor
    val selectionColor = CupertinoGlassDefaults.selection

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = CupertinoNavigationBarDefaults.BottomPadding)
            .wrapContentWidth(horizontalAlignment)
            .windowInsetsPadding(windowInsets)
    ) {
            // Calculate actual available width after applying windowInsets
            BoxWithConstraints(
                contentAlignment = Alignment.CenterStart,
            ) {
            val density = LocalDensity.current
            var barHeightPx by remember(density) {
                mutableIntStateOf(with(density) { 64.dp.roundToPx() })
            }
            val indicatorHeight = with(density) {
                (barHeightPx - (NavBarPadding * 2).roundToPx()).coerceAtLeast(56.dp.roundToPx()).toDp()
            }

            val paddingPx = with(density) { NavBarPadding.toPx() }
            val gapPx = with(density) { NavBarItemGap.toPx() }
            val minItemWidthPx = with(density) { NavBarItemMinWidth.toPx() }

            // Calculate item width based on available width
            // NavBar total width = padding*2 + itemWidth*n + gap*(n-1)
            // → itemWidth = (availableWidth - padding*2 - gap*(n-1)) / n
            val availableWidthPx = constraints.maxWidth.toFloat()
            val calculatedItemWidthPx = if (displayedTabsCount > 0) {
                (availableWidthPx - paddingPx * 2f - gapPx * (displayedTabsCount - 1)) / displayedTabsCount
            } else {
                0f
            }

            // If evenly distributed width is greater than or equal to minimum width (90dp), use fixed width, otherwise use evenly distributed width
            val itemWidthPx = if (calculatedItemWidthPx >= minItemWidthPx) {
                minItemWidthPx
            } else {
                calculatedItemWidthPx
            }
            val itemWidthDp: Dp = with(density) { itemWidthPx.toDp() }

            // NavBar total width = padding*2 + itemWidth*n + gap*(n-1)
            val rowWidth = (paddingPx * 2f + itemWidthPx * displayedTabsCount + gapPx * (displayedTabsCount - 1)).coerceAtLeast(1f)

            fun itemLeftX(index: Float): Float = paddingPx + (itemWidthPx + gapPx) * index
            fun itemCenterX(index: Float): Float = itemLeftX(index) + itemWidthPx / 2f

            val tabStep = itemWidthPx + gapPx

            val offsetAnimation = remember { Animatable(0f) }
            val panelOffset by remember(density) {
                derivedStateOf {
                    val fraction = (offsetAnimation.value / rowWidth).fastCoerceIn(-1f, 1f)
                    with(density) {
                        4.dp.toPx() * fraction.sign * EaseOut.transform(abs(fraction))
                    }
                }
            }

            val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
            val animationScope = rememberCoroutineScope()
            var currentIndex by remember(selectedTabIndex) {
                mutableIntStateOf(selectedTabIndex())
            }
            val dampedDragAnimation = remember(animationScope) {
                DampedDragAnimation(
                    animationScope = animationScope,
                    initialValue = selectedTabIndex().toFloat(),
                    valueRange = 0f..(tabsCount - 1).toFloat(),
                    visibilityThreshold = 0.001f,
                    initialScale = 1f,
                    pressedScale = 78f / 56f,
                    onDragStarted = {},
                    onDragStopped = {
                        val targetIndex = targetValue.fastRoundToInt().fastCoerceIn(0, tabsCount - 1)
                        currentIndex = targetIndex
                        animateToValue(targetIndex.toFloat())
                        animationScope.launch {
                            offsetAnimation.animateTo(
                                0f,
                                spring(1f, 300f, 0.5f)
                            )
                        }
                    },
                    onDrag = { _, dragAmount ->
                        updateValue(
                            (targetValue + dragAmount.x / tabStep * if (isLtr) 1f else -1f)
                                .fastCoerceIn(0f, (tabsCount - 1).toFloat())
                        )
                        animationScope.launch {
                            offsetAnimation.snapTo(offsetAnimation.value + dragAmount.x)
                        }
                    }
                )
            }

            LaunchedEffect(selectedTabIndex) {
                snapshotFlow { selectedTabIndex() }
                    .collectLatest { index ->
                        currentIndex = index
                    }
            }
            LaunchedEffect(dampedDragAnimation) {
                snapshotFlow { currentIndex }
                    .drop(1)
                    .collectLatest { index ->
                        dampedDragAnimation.animateToValue(index.toFloat())
                        onTabSelected(index)
                    }
            }

            val interactiveHighlight = remember(animationScope) {
                InteractiveHighlight(
                    animationScope = animationScope,
                    position = { size, _ ->
                        val cx = itemCenterX(dampedDragAnimation.value)
                        Offset(
                            if (isLtr) cx + panelOffset
                            else size.width - cx + panelOffset,
                            size.height / 2f
                        )
                    }
                )
            }

            // Pass dynamic itemWidthDp via CompositionLocal
            CompositionLocalProvider(
                LocalCupertinoNavItemWidth provides itemWidthDp,
            ) {
                // ── Background Row ───────────────────────────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .graphicsLayer {
                            translationX = panelOffset
                        }
                        .drawBackdrop(
                            backdrop = backdrop,
                            shape = { ContinuousCapsule() },
                            effects = {
                                vibrancy()
                                blur(CupertinoGlassDefaults.blurRadius.toPx())
                            },
                            layerBlock = {
                                val progress = dampedDragAnimation.pressProgress
                                val scale = lerp(1f, 1f + 16.dp.toPx() / size.width, progress)
                                scaleX = scale
                                scaleY = scale
                            },
                            onDrawSurface = {
                                drawRect(containerColor)
                            }
                        )
                        .glassEdge(ContinuousCapsule())
                        .then(interactiveHighlight.modifier)
                        .wrapContentWidth()
                        .heightIn(min = 64.dp)
                        .onSizeChanged { size ->
                            if (barHeightPx != size.height) barHeightPx = size.height
                        }
                        .padding(NavBarPadding),
                    horizontalArrangement = Arrangement.spacedBy(NavBarItemGap),
                    verticalAlignment = Alignment.CenterVertically,
                    content = content
                )

                // ── Accent color overlay Row ──────────────────────────────────────────────────────
                CompositionLocalProvider(
                    LocalLiquidBottomTabScale provides {
                        lerp(1f, 1.2f, dampedDragAnimation.pressProgress)
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .clearAndSetSemantics {}
                            .alpha(0f)
                            .layerBackdrop(tabsBackdrop)
                            .graphicsLayer {
                                translationX = panelOffset
                            }
                            .drawBackdrop(
                                backdrop = backdrop,
                                shape = { ContinuousCapsule() },
                                effects = {
                                    val progress = dampedDragAnimation.pressProgress
                                    vibrancy()
                                    blur(8.dp.toPx())
                                    lens(
                                        24.dp.toPx() * progress,
                                        24.dp.toPx() * progress
                                    )
                                },
                                highlight = {
                                    val progress = dampedDragAnimation.pressProgress
                                    Highlight.Default.copy(alpha = progress)
                                },
                                onDrawSurface = {}
                            )
                            .then(interactiveHighlight.modifier)
                            .wrapContentWidth()
                            .height(indicatorHeight)
                            .padding(horizontal = NavBarPadding)
                            .graphicsLayer(colorFilter = ColorFilter.tint(accentColor)),
                        horizontalArrangement = Arrangement.spacedBy(NavBarItemGap),
                        verticalAlignment = Alignment.CenterVertically,
                        content = content
                    )
                }

                // ── Sliding selection indicator Box ───────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            val leftX = itemLeftX(dampedDragAnimation.value)
                            translationX = if (isLtr) {
                                leftX + panelOffset
                            } else {
                                rowWidth - leftX - itemWidthPx + panelOffset
                            }
                        }
                        .then(interactiveHighlight.gestureModifier)
                        .then(dampedDragAnimation.modifier)
                        .drawBackdrop(
                            backdrop = rememberCombinedBackdrop(backdrop, tabsBackdrop),
                            shape = { ContinuousCapsule() },
                            effects = {
                                val progress = dampedDragAnimation.pressProgress
                                lens(
                                    10.dp.toPx() * progress,
                                    14.dp.toPx() * progress,
                                    chromaticAberration = true
                                )
                            },
                            highlight = {
                                val progress = dampedDragAnimation.pressProgress
                                Highlight.Default.copy(alpha = progress)
                            },
                            shadow = {
                                val progress = dampedDragAnimation.pressProgress
                                Shadow(alpha = progress)
                            },
                            innerShadow = {
                                val progress = dampedDragAnimation.pressProgress
                                InnerShadow(
                                    radius = 8.dp * progress,
                                    alpha = progress
                                )
                            },
                            layerBlock = {
                                scaleX = dampedDragAnimation.scaleX
                                scaleY = dampedDragAnimation.scaleY
                                val velocity = dampedDragAnimation.velocity / 10f
                                scaleX /= 1f - (velocity * 0.75f).fastCoerceIn(-0.2f, 0.2f)
                                scaleY *= 1f - (velocity * 0.25f).fastCoerceIn(-0.2f, 0.2f)
                            },
                            onDrawSurface = {
                                val progress = dampedDragAnimation.pressProgress
                                drawRect(selectionColor, alpha = 1f - progress)
                                drawRect(Color.Black.copy(alpha = 0.03f * progress))
                            }
                        )
                        .align(Alignment.CenterStart)
                        .height(indicatorHeight)
                        .width(itemWidthDp)  // ★ Apply dynamic width
                )
        }
        }
}
}

@Composable
@OptIn(ExperimentalCupertinoApi::class)
private fun CupertinoProminentNavigationBar(
    modifier: Modifier,
    colors: CupertinoNavigationBarColors,
    windowInsets: WindowInsets,
    backdrop: LayerBackdrop,
    selectedTabIndex: () -> Int,
    onTabSelected: (index: Int) -> Unit,
    tabsCount: Int,
    options: CupertinoNavigationBarOptions,
    content: @Composable RowScope.() -> Unit,
) {
    val prominentIndex = options.prominentTabIndex ?: return
    require(prominentIndex in 0 until tabsCount) {
        "강조 탭 인덱스는 존재하는 탭 인덱스여야 합니다."
    }
    val selectedIndex = selectedTabIndex().coerceIn(0, (tabsCount - 1).coerceAtLeast(0))
    val isCollapsed = options.isCollapsed && prominentIndex in 0 until tabsCount
    val displayedRegularCount = if (isCollapsed) {
        if (selectedIndex == prominentIndex) 0 else 1
    } else {
        (tabsCount - 1).coerceAtLeast(0)
    }
    val surfaceTint = colors.containerColor
    val selectionColor = CupertinoGlassDefaults.selection

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = CupertinoNavigationBarDefaults.BottomPadding)
            .windowInsetsPadding(windowInsets),
        contentAlignment = Alignment.Center,
    ) {
        val density = LocalDensity.current
        val availableWidthPx = constraints.maxWidth.toFloat()
        val horizontalPaddingPx = with(density) { NavBarPadding.toPx() }
        val prominentWidthPx = with(density) {
            64.dp.toPx().coerceAtMost((availableWidthPx - horizontalPaddingPx * 2f).coerceAtLeast(0f))
        }
        val gapPx = if (displayedRegularCount > 0) with(density) { 8.dp.toPx() } else 0f
        val regularWidthPx = if (displayedRegularCount > 0) {
            ((availableWidthPx - horizontalPaddingPx * 2f - prominentWidthPx - gapPx) / displayedRegularCount)
                .coerceAtLeast(0f)
                .coerceAtMost(with(density) { NavBarItemMinWidth.toPx() })
        } else {
            0f
        }
        val regularBarWidthPx = if (displayedRegularCount > 0) {
            horizontalPaddingPx * 2f + regularWidthPx * displayedRegularCount
        } else {
            0f
        }
        val prominentLeftPx = if (displayedRegularCount > 0) regularBarWidthPx + gapPx else horizontalPaddingPx
        val layoutWidthPx = if (displayedRegularCount > 0) {
            prominentLeftPx + prominentWidthPx
        } else {
            prominentWidthPx + horizontalPaddingPx * 2f
        }.coerceAtMost(availableWidthPx)
        val layoutWidthDp = with(density) { layoutWidthPx.toDp() }
        val regularWidthDp = with(density) { regularWidthPx.toDp() }
        val prominentWidthDp = with(density) { prominentWidthPx.toDp() }
        val itemOptions = options.copy(
            isCollapsed = isCollapsed,
            selectedTabIndex = selectedIndex,
            backdrop = backdrop,
            containerColor = surfaceTint,
            usesManagedLayout = true,
        )
        val regularTabIndices = remember(tabsCount, prominentIndex) {
            (0 until tabsCount).filter { index -> index != prominentIndex }
        }
        val updatedOnTabSelected by rememberUpdatedState(onTabSelected)
        val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
        var canScrubTabs = false
        var hasScrubbedTabs = false
        var scrubPosition = regularTabIndices.indexOf(selectedIndex).coerceAtLeast(0).toFloat()
        val regularStartPx = if (isLtr) 0f else layoutWidthPx - regularBarWidthPx
        val animationScope = rememberCoroutineScope()
        val regularDragAnimation = remember(
            animationScope,
            tabsCount,
            prominentIndex,
            selectedIndex,
            isCollapsed,
            regularWidthPx,
            regularBarWidthPx,
            layoutWidthPx,
            isLtr,
        ) {
            val selectedRegularPosition = regularTabIndices.indexOf(selectedIndex)
            val initialPosition = selectedRegularPosition.coerceAtLeast(0).toFloat()
            val lastPosition = regularTabIndices.lastIndex.coerceAtLeast(0).toFloat()
            DampedDragAnimation(
                animationScope = animationScope,
                initialValue = initialPosition,
                valueRange = 0f..lastPosition,
                visibilityThreshold = 0.001f,
                initialScale = 1f,
                pressedScale = 1.035f,
                onDragStarted = { position ->
                    canScrubTabs = !isCollapsed && regularTabIndices.size > 1 &&
                        position.x in regularStartPx..(regularStartPx + regularBarWidthPx)
                    hasScrubbedTabs = false
                    if (canScrubTabs) {
                        scrubPosition = if (regularWidthPx > 0f) {
                            val positionInRegularBar = position.x - regularStartPx
                            val logicalPosition = if (isLtr) {
                                (positionInRegularBar - horizontalPaddingPx) / regularWidthPx - 0.5f
                            } else {
                                (regularBarWidthPx - horizontalPaddingPx - positionInRegularBar) /
                                    regularWidthPx - 0.5f
                            }
                            logicalPosition.fastCoerceIn(0f, lastPosition)
                        } else {
                            0f
                        }
                        updateValue(scrubPosition)
                    }
                },
                onDragStopped = {
                    if (canScrubTabs && hasScrubbedTabs) {
                        val targetPosition = scrubPosition.fastRoundToInt()
                            .fastCoerceIn(0, regularTabIndices.lastIndex)
                        updatedOnTabSelected(regularTabIndices[targetPosition])
                    }
                    canScrubTabs = false
                },
                onDrag = { _, dragAmount ->
                    if (canScrubTabs && regularWidthPx > 0f && dragAmount.x != 0f) {
                        scrubPosition =
                            (scrubPosition + dragAmount.x / regularWidthPx * if (isLtr) 1f else -1f)
                                .fastCoerceIn(0f, lastPosition)
                        hasScrubbedTabs = true
                        updateValue(scrubPosition)
                    }
                },
            ).apply {
                onDragCancelled = {
                    canScrubTabs = false
                    hasScrubbedTabs = false
                }
            }
        }
        val regularDragModifier = if (!isCollapsed && regularTabIndices.size > 1) {
            regularDragAnimation.modifier
        } else {
            Modifier
        }

        CompositionLocalProvider(
            LocalCupertinoNavigationBarOptions provides itemOptions,
            LocalCupertinoNavItemWidth provides regularWidthDp,
            LocalCupertinoProminentNavItemWidth provides prominentWidthDp,
        ) {
            Row {
                val rowScope = this
                Layout(
                    modifier = Modifier
                        .width(layoutWidthDp)
                        .then(regularDragModifier),
                    content = {
                        if (displayedRegularCount > 0) {
                            Box(
                                Modifier
                                    .layoutId(CupertinoNavigationBarRegularBackground)
                                    .drawBackdrop(
                                        backdrop = backdrop,
                                        shape = { ContinuousCapsule() },
                                        effects = {
                                            vibrancy()
                                            blur(CupertinoGlassDefaults.blurRadius.toPx())
                                        },
                                        onDrawSurface = { drawRect(surfaceTint) },
                                    )
                                    .glassEdge(ContinuousCapsule()),
                            )
                        }
                        content.invoke(rowScope)
                    },
                ) { measurables, constraints ->
                    val minimumHeightPx = with(density) { 64.dp.roundToPx() }
                    val intrinsicHeightPx = measurables.mapNotNull { measurable ->
                        val index = measurable.layoutId as? Int ?: return@mapNotNull null
                        val isVisible = if (isCollapsed) {
                            index == prominentIndex || (selectedIndex != prominentIndex && index == selectedIndex)
                        } else {
                            index in 0 until tabsCount
                        }
                        if (!isVisible) return@mapNotNull null
                        val maxWidth = if (index == prominentIndex) prominentWidthPx else regularWidthPx
                        measurable.maxIntrinsicHeight(maxWidth.roundToInt().coerceAtLeast(0))
                    }.maxOrNull() ?: 0
                    val heightPx = maxOf(minimumHeightPx, intrinsicHeightPx, constraints.minHeight)
                        .coerceAtMost(constraints.maxHeight)
                    val backgroundWidthPx = regularBarWidthPx.roundToInt()
                    val background = measurables.firstOrNull {
                        it.layoutId == CupertinoNavigationBarRegularBackground
                    }?.measure(
                        Constraints.fixed(
                            width = backgroundWidthPx,
                            height = heightPx,
                        ),
                    )
                    val itemPlaceables = measurables.mapNotNull { measurable ->
                        val index = measurable.layoutId as? Int ?: return@mapNotNull null
                        val maxWidth = if (index == prominentIndex) prominentWidthPx else regularWidthPx
                        index to measurable.measure(
                            Constraints(
                                maxWidth = maxWidth.roundToInt().coerceAtLeast(0),
                                maxHeight = heightPx,
                            ),
                        )
                    }.toMap()

                    layout(layoutWidthPx.roundToInt(), heightPx) {
                        background?.placeRelative(0, 0)
                        itemPlaceables.forEach { (index, placeable) ->
                            val isProminent = index == prominentIndex
                            val isVisible = if (isCollapsed) {
                                isProminent || (selectedIndex != prominentIndex && index == selectedIndex)
                            } else {
                                index in 0 until tabsCount
                            }
                            if (isVisible) {
                                val x = if (isProminent) {
                                    prominentLeftPx.roundToInt()
                                } else {
                                    val itemPosition = if (isCollapsed) 0 else if (index < prominentIndex) index else index - 1
                                    (horizontalPaddingPx + regularWidthPx * itemPosition).roundToInt()
                                }
                                placeable.placeRelative(x, (heightPx - placeable.height) / 2)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.CupertinoNavigationBarItem(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val navigationBarOptions = LocalCupertinoNavigationBarOptions.current
    val index = LocalCupertinoNavItemIndex.current
    val isCollapsed = navigationBarOptions?.isCollapsed == true && navigationBarOptions.prominentTabIndex != null
    val selectedIndex = navigationBarOptions?.selectedTabIndex ?: -1
    val prominentIndex = navigationBarOptions?.prominentTabIndex ?: -1
    val itemIndex = index ?: -1
    val isProminent = index != null && itemIndex == prominentIndex
    val isManaged = navigationBarOptions?.usesManagedLayout == true && index != null
    val isSelected = isManaged && itemIndex == selectedIndex
    val isPressed by interactionSource.collectIsPressedAsState()
    val animationScope = rememberCoroutineScope()
    val interactiveHighlight = remember(animationScope) {
        InteractiveHighlight(animationScope) { size, _ -> Offset(size.width / 2f, size.height / 2f) }
    }
    LaunchedEffect(interactionSource, enabled, isManaged) {
        if (!enabled || !isManaged) {
            interactiveHighlight.release()
            return@LaunchedEffect
        }
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> interactiveHighlight.press()
                is PressInteraction.Release, is PressInteraction.Cancel -> interactiveHighlight.release()
            }
        }
    }
    val pressScale by animateFloatAsState(
        targetValue = if (isManaged && enabled && isPressed) 0.985f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = 700f),
        label = "CupertinoNavigationBarItemPressScale",
    )
    val isVisible = !isCollapsed || index == null || itemIndex == prominentIndex || itemIndex == selectedIndex
    val scale = LocalLiquidBottomTabScale.current
    val itemWidth = if (isProminent) {
        LocalCupertinoProminentNavItemWidth.current
    } else {
        LocalCupertinoNavItemWidth.current
    }
    val selectionColor = CupertinoGlassDefaults.selection
    val pressedSelectionColor = selectionColor.copy(alpha = (selectionColor.alpha * 2f).coerceAtMost(1f))
    val itemColors = navigationBarOptions?.colors
    val iconContentColor = if (isManaged) {
        itemColors?.iconColor(isSelected, enabled) ?: CupertinoTheme.colorScheme.secondaryLabel
    } else {
        Color.Unspecified
    }
    val labelContentColor = if (isManaged) {
        itemColors?.textColor(isSelected, enabled) ?: CupertinoTheme.colorScheme.secondaryLabel
    } else {
        Color.Unspecified
    }
    val disabledAlpha = if (enabled) 1f else 0.5f
    val managedIconColor = if (isManaged) {
        iconContentColor.copy(alpha = iconContentColor.alpha * disabledAlpha)
    } else {
        Color.Transparent
    }
    val managedLabelColor = if (isManaged) {
        labelContentColor.copy(alpha = labelContentColor.alpha * disabledAlpha)
    } else {
        Color.Transparent
    }
    val itemFillColor = when {
        isManaged && enabled && isPressed -> pressedSelectionColor
        isManaged && isSelected -> selectionColor
        else -> Color.Transparent
    }
    val panelTint = navigationBarOptions?.containerColor ?: CupertinoGlassDefaults.panelTint
    val animatedItemWidth by animateDpAsState(
        targetValue = if (isVisible) itemWidth else 0.dp,
        animationSpec = spring(dampingRatio = 0.86f, stiffness = 520f),
        label = "CupertinoNavigationBarItemWidth",
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .then(
                if (navigationBarOptions?.usesManagedLayout == true && index != null) {
                    Modifier.layoutId(index)
                } else {
                    Modifier
                },
            )
            .then(
                if (navigationBarOptions?.usesManagedLayout == true && isProminent) {
                    Modifier
                        .drawBackdrop(
                            backdrop = navigationBarOptions.backdrop ?: rememberLayerBackdrop(),
                            shape = { ContinuousCapsule() },
                            effects = {
                                vibrancy()
                                blur(CupertinoGlassDefaults.blurRadius.toPx())
                                lens(
                                    8.dp.toPx(),
                                    lerp(12.dp.toPx(), 24.dp.toPx(), interactiveHighlight.pressProgress),
                                )
                            },
                            highlight = {
                                Highlight.Default.copy(
                                    alpha = 0.55f + interactiveHighlight.pressProgress * 0.2f,
                                )
                            },
                            onDrawSurface = {
                                drawRect(panelTint)
                                if (itemFillColor != Color.Transparent) drawRect(itemFillColor)
                            },
                        )
                        .glassEdge(ContinuousCapsule())
                } else {
                    Modifier
                },
            )
            .then(
                if (isManaged && !isProminent && itemFillColor != Color.Transparent
                ) {
                    Modifier
                        .drawBackdrop(
                            backdrop = navigationBarOptions.backdrop ?: rememberLayerBackdrop(),
                            shape = { ContinuousCapsule() },
                            effects = {
                                vibrancy()
                                blur(CupertinoGlassDefaults.blurRadius.toPx())
                                lens(
                                    8.dp.toPx(),
                                    lerp(12.dp.toPx(), 24.dp.toPx(), interactiveHighlight.pressProgress),
                                )
                            },
                            highlight = {
                                Highlight.Default.copy(
                                    alpha = 0.5f + interactiveHighlight.pressProgress * 0.2f,
                                )
                            },
                            onDrawSurface = {
                                drawRect(itemFillColor)
                            },
                        )
                        .glassEdge(ContinuousCapsule())
                } else {
                    Modifier
                },
            )
            .then(if (isManaged && enabled) interactiveHighlight.modifier.then(interactiveHighlight.gestureModifier) else Modifier)
            .clip(ContinuousCapsule())
            .then(
                if (isManaged) {
                    Modifier.selectable(
                        selected = isSelected,
                        enabled = enabled,
                        interactionSource = interactionSource,
                        indication = null,
                        role = Role.Tab,
                        onClick = onClick,
                    )
                } else {
                    Modifier.clickable(
                        enabled = enabled,
                        interactionSource = interactionSource,
                        indication = null,
                        role = Role.Tab,
                        onClick = onClick,
                    )
                },
            )
            .heightIn(min = 56.dp)
            .width(animatedItemWidth)
            .graphicsLayer {
                val s = scale() * pressScale
                scaleX = s
                scaleY = s
            }
            .then(
                if (isManaged && enabled) {
                    Modifier.graphicsLayer(interactiveHighlight.layerBlock)
                } else {
                    Modifier
                },
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(if (isProminent) 26.dp else 24.dp)
        ) {
            if (isManaged) {
                CompositionLocalProvider(LocalContentColor provides managedIconColor) {
                    icon()
                }
            } else {
                icon()
            }
        }
        ProvideTextStyle(
            value = TextStyle(fontSize = 10.sp, lineHeight = 12.sp, fontWeight = FontWeight.Bold)
        ) {
            if (isManaged) {
                CompositionLocalProvider(LocalContentColor provides managedLabelColor) {
                    label?.invoke()
                }
            } else {
                label?.invoke()
            }
        }
    }
}

/**
 * 강조 탭을 일반 탭과 분리된 끝쪽 유리 컨트롤로 표시하는 Cupertino 하단 탐색 막대입니다.
 *
 * [prominentTabIndex]를 지정해야 강조 표현이 적용됩니다. [isCollapsed]가 참이면 선택된 일반 탭과
 * 강조 탭을 유지합니다. 항목은 [CupertinoNavigationBarItem]의 인덱스 지정 오버로드로 만듭니다.
 */
@Composable
@ExperimentalCupertinoApi
fun CupertinoNavigationBar(
    modifier: Modifier = Modifier,
    colors: CupertinoNavigationBarColors = CupertinoNavigationBarDefaults.colors(),
    windowInsets: WindowInsets = CupertinoNavigationBarDefaults.windowInsets,
    backdrop: LayerBackdrop,
    selectedTabIndex: () -> Int,
    onTabSelected: (index: Int) -> Unit,
    tabsCount: Int,
    prominentTabIndex: Int?,
    isCollapsed: Boolean = false,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    trailingAction: (@Composable BoxScope.() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    require(prominentTabIndex == null || prominentTabIndex in 0 until tabsCount) {
        "prominentTabIndex는 존재하는 탭 인덱스여야 합니다."
    }

    CompositionLocalProvider(
        LocalCupertinoNavigationBarOptions provides CupertinoNavigationBarOptions(
            prominentTabIndex = prominentTabIndex,
            isCollapsed = isCollapsed,
            selectedTabIndex = selectedTabIndex(),
            backdrop = backdrop,
            colors = colors,
        ),
    ) {
        CupertinoNavigationBar(
            modifier = modifier,
            colors = colors,
            windowInsets = windowInsets,
            backdrop = backdrop,
            selectedTabIndex = selectedTabIndex,
            onTabSelected = onTabSelected,
            tabsCount = tabsCount,
            horizontalAlignment = horizontalAlignment,
            trailingAction = trailingAction,
            content = content,
        )
    }
}

/**
 * [CupertinoNavigationBar]의 인덱스를 지정하는 항목입니다.
 *
 * 이 인덱스를 사용하면 막대가 접힐 때 선택된 항목과 강조 항목을 표시할 수 있습니다.
 */
@Composable
fun RowScope.CupertinoNavigationBarItem(
    index: Int,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    CompositionLocalProvider(LocalCupertinoNavItemIndex provides index) {
        CupertinoNavigationBarItem(
            onClick = onClick,
            icon = icon,
            modifier = modifier,
            enabled = enabled,
            label = label,
            interactionSource = interactionSource,
        )
    }
}

@Stable
@ExperimentalCupertinoApi
class CupertinoNavigationBarColors internal constructor(
    internal val accentColor: Color,
    internal val containerColor: Color,
    private val selectedIconColor: Color,
    private val selectedTextColor: Color,
    private val unselectedIconColor: Color,
    private val unselectedTextColor: Color,
    private val disabledIconColor: Color,
    private val disabledTextColor: Color,
) {
    @Composable
    internal fun iconColor(selected: Boolean, enabled: Boolean): Color =
        when {
            !enabled -> disabledIconColor
            selected -> selectedIconColor
            else -> unselectedIconColor
        }

    @Composable
    internal fun textColor(selected: Boolean, enabled: Boolean): Color =
        when {
            !enabled -> disabledTextColor
            selected -> selectedTextColor
            else -> unselectedTextColor
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is CupertinoNavigationBarColors) return false
        if (selectedIconColor != other.selectedIconColor) return false
        if (unselectedIconColor != other.unselectedIconColor) return false
        if (selectedTextColor != other.selectedTextColor) return false
        if (unselectedTextColor != other.unselectedTextColor) return false
        if (disabledIconColor != other.disabledIconColor) return false
        return disabledTextColor == other.disabledTextColor
    }

    override fun hashCode(): Int {
        var result = selectedIconColor.hashCode()
        result = 31 * result + unselectedIconColor.hashCode()
        result = 31 * result + selectedTextColor.hashCode()
        result = 31 * result + unselectedTextColor.hashCode()
        result = 31 * result + disabledIconColor.hashCode()
        result = 31 * result + disabledTextColor.hashCode()
        return result
    }
}

@ExperimentalCupertinoApi
@Immutable
object CupertinoNavigationBarDefaults {
    val containerColor: Color
        @Composable
        @ReadOnlyComposable
        get() = CupertinoGlassDefaults.tint

    @Composable
    @ReadOnlyComposable
    fun colors(
        accentColor: Color = CupertinoTheme.colorScheme.accent,
        containerColor: Color = CupertinoGlassDefaults.tint,
        selectedIconColor: Color = CupertinoTheme.colorScheme.accent,
        selectedTextColor: Color = CupertinoTheme.colorScheme.accent,
        unselectedIconColor: Color = CupertinoTheme.colorScheme.secondaryLabel,
        unselectedTextColor: Color = CupertinoTheme.colorScheme.secondaryLabel,
        disabledIconColor: Color = CupertinoTheme.colorScheme.tertiaryLabel,
        disabledTextColor: Color = CupertinoTheme.colorScheme.tertiaryLabel,
    ) = CupertinoNavigationBarColors(
        accentColor = accentColor,
        containerColor = containerColor,
        selectedIconColor = selectedIconColor,
        selectedTextColor = selectedTextColor,
        unselectedIconColor = unselectedIconColor,
        unselectedTextColor = unselectedTextColor,
        disabledIconColor = disabledIconColor,
        disabledTextColor = disabledTextColor,
    )

    val windowInsets = WindowInsets(left = 36.dp, right = 36.dp)
    val BottomPadding = 24.dp
}

internal val LocalLiquidBottomTabScale = staticCompositionLocalOf { { 1f } }
internal val LocalCupertinoNavItemWidth = staticCompositionLocalOf { 90.dp }  // ★ Dynamic width transmission

@OptIn(ExperimentalCupertinoApi::class)
private data class CupertinoNavigationBarOptions(
    val prominentTabIndex: Int?,
    val isCollapsed: Boolean,
    val selectedTabIndex: Int = 0,
    val backdrop: LayerBackdrop? = null,
    val containerColor: Color? = null,
    val colors: CupertinoNavigationBarColors? = null,
    val usesManagedLayout: Boolean = false,
)

private val LocalCupertinoNavigationBarOptions = staticCompositionLocalOf<CupertinoNavigationBarOptions?> { null }
private val LocalCupertinoNavItemIndex = staticCompositionLocalOf<Int?> { null }
private val LocalCupertinoProminentNavItemWidth = staticCompositionLocalOf { 64.dp }
private object CupertinoNavigationBarRegularBackground
