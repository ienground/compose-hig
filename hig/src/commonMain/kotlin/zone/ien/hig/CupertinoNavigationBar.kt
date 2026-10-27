/*
 * Copyright (c) 2023-2024. Compose Cupertino project and open source contributors.
 * Copyright (c) 2025. Scott Lanoue.
 * Copyright (c) 2025. Kyant.
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
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.util.lerp
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.BackdropEffectScope
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberCombinedBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.colorControls
import com.kyant.backdrop.effects.runtimeShaderEffect
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.backdrop.shadow.Shadow
import com.kyant.capsule.ContinuousCapsule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.utils.CupertinoGlassDefaults
import zone.ien.hig.utils.DampedDragAnimation
import zone.ien.hig.utils.InteractiveHighlight
import zone.ien.hig.utils.glassEdge
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sign

private val NavBarPadding = 4.dp
private val NavBarHorizontalPadding = 8.dp
private val NavBarItemGap = 0.dp
private val NavBarItemMinWidth = 86.dp
private val NavBarHeight = 62.dp
private val NavBarItemHeight = NavBarHeight - NavBarPadding * 2
private val NavBarVerticalItemHeight = 52.dp
private val NavBarBlurRadius = CupertinoGlassDefaults.blurRadius
private val NavBarSelectionBlurRadius = CupertinoGlassDefaults.blurRadius
private val NavBarRefractionHeight = CupertinoGlassDefaults.refractionHeight
private val NavBarRefractionAmount = CupertinoGlassDefaults.refractionAmount

/**
 * Cupertino 탐색 탭 막대입니다.
 *
 * Duo의 세로 도구 모음이 활성화되면 오른쪽 아래에 아이콘 탭을 세로로 배치합니다.
 * 탭을 누르면 렌즈가 반응하고, 0.5초 유지하면 막대와 라벨을 펼칩니다. 드래그는 손을 뗄 때 확정합니다.
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
    val verticalPlacement = LocalCupertinoVerticalBarPlacement.current?.takeIf { it.width > 0.dp }
    if (verticalPlacement != null) {
        val verticalSelectionColor = CupertinoGlassDefaults.selection
        val density = LocalDensity.current
        val animationScope = rememberCoroutineScope()
        val onSelected by rememberUpdatedState(onTabSelected)
        val selectedIndex = selectedTabIndex()
        val currentSelectedIndex by rememberUpdatedState(selectedTabIndex)
        val lastIndex = (tabsCount - 1).coerceAtLeast(0)
        val motion = remember(animationScope, tabsCount) {
            CupertinoVerticalNavigationBarState(animationScope, selectedIndex, tabsCount)
        }
        LaunchedEffect(motion, selectedIndex) {
            motion.select(selectedIndex)
        }
        val expansion = motion.expansionProgress
        val barWidth = CupertinoVerticalBarControlWidth + 30.dp * expansion
        val itemSize = barWidth - NavBarPadding * 2
        val itemHeight = NavBarVerticalItemHeight + 12.dp * expansion
        val itemStep by rememberUpdatedState(with(density) { itemHeight.toPx() })
        val paddingPx = with(density) { NavBarPadding.toPx() }
        val tabsBackdrop = rememberLayerBackdrop()
        Column(
            modifier = modifier
                .layoutId(CupertinoVerticalBottomBar)
                .width(verticalPlacement.width)
                .absoluteOffset(x = verticalPlacement.width / 2 - verticalPlacement.centerFromRight)
                .padding(bottom = verticalPlacement.bottom),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CompositionLocalProvider(
                LocalCupertinoNavItemWidth provides itemSize,
                LocalCupertinoVerticalNavMotion provides motion,
                LocalCupertinoNavigationBarOptions provides CupertinoNavigationBarOptions(
                    prominentTabIndex = null,
                    isCollapsed = false,
                    selectedTabIndex = selectedIndex,
                    colors = colors,
                ),
            ) {
                Box(
                    Modifier.requiredWidth(barWidth)
                        .pointerInput(motion, density) {
                            awaitEachGesture {
                                val down = awaitFirstDown(false, PointerEventPass.Initial)
                                awaitPointerEvent(PointerEventPass.Main)
                                if (!down.isConsumed || tabsCount == 0) return@awaitEachGesture
                                val startIndex = ((down.position.y - paddingPx) / itemStep)
                                    .toInt().coerceIn(0, lastIndex)
                                if (motion.enabledTabs[startIndex] == false) return@awaitEachGesture
                                var dragPosition = startIndex.toFloat()
                                val grabOffset = down.position.y - paddingPx - (startIndex + 0.5f) * itemStep
                                var distance = Offset.Zero
                                var dragging = false
                                var released = false
                                motion.press(startIndex)
                                try {
                                    while (true) {
                                        val event = awaitPointerEvent(PointerEventPass.Initial)
                                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                        if (change.isConsumed || event.changes.count { it.pressed } > 1) break
                                        if (!change.pressed) {
                                            if (dragging) {
                                                change.consume()
                                                val index = dragPosition.roundToInt().coerceIn(0, lastIndex)
                                                val target = if (motion.enabledTabs[index] != false) index else currentSelectedIndex()
                                                motion.release(target)
                                                if (target != currentSelectedIndex()) onSelected(target)
                                            } else {
                                                awaitPointerEvent(PointerEventPass.Main)
                                                motion.release(currentSelectedIndex())
                                            }
                                            released = true
                                            break
                                        }
                                        val delta = change.positionChange()
                                        distance += delta
                                        if (!dragging && distance.getDistance() > viewConfiguration.touchSlop) {
                                            dragging = true
                                        }
                                        if (delta != Offset.Zero) {
                                            dragPosition = ((change.position.y - paddingPx - grabOffset) / itemStep - 0.5f)
                                                .coerceIn(0f, lastIndex.toFloat())
                                            motion.dragTo(dragPosition)
                                        }
                                        if (dragging) change.consume()
                                    }
                                } finally {
                                    if (!released) motion.release(currentSelectedIndex())
                                }
                            }
                        },
                ) {
                    FlowRow(
                        modifier = Modifier
                            .drawBackdrop(
                                backdrop = backdrop,
                                shape = { ContinuousCapsule() },
                                effects = {
                                    colorControls(saturation = CupertinoGlassDefaults.saturation)
                                    blur(NavBarBlurRadius.toPx())
                                    lens(NavBarRefractionHeight.toPx(), NavBarRefractionAmount.toPx(), depthEffect = true)
                                },
                                innerShadow = { InnerShadow(radius = 3.dp, alpha = 0.3f) },
                                shadow = {
                                    Shadow(
                                        radius = 2.dp,
                                        offset = DpOffset(0.dp, 1.dp),
                                        color = Color.Black.copy(alpha = 0.16f),
                                    )
                                },
                                layerBlock = {
                                    val scale = lerp(1f, 1f + 4.dp.toPx() / size.height, motion.pressProgress)
                                    scaleX = scale
                                    scaleY = scale
                                },
                                onDrawSurface = {
                                    drawRect(colors.containerColor)
                                },
                            )
                            .padding(NavBarPadding),
                        maxItemsInEachRow = 1,
                        horizontalArrangement = Arrangement.Center,
                    ) { content() }
                    CompositionLocalProvider(
                        LocalContentColor provides colors.accentColor,
                        androidx.compose.material3.LocalContentColor provides colors.accentColor,
                        LocalCupertinoNavBarOverlay provides true,
                        LocalLiquidBottomTabScale provides { lerp(1f, 1.12f, motion.pressProgress) },
                    ) {
                        FlowRow(
                            modifier = Modifier
                                .matchParentSize()
                                .clearAndSetSemantics {}
                                .alpha(0f)
                                .layerBackdrop(tabsBackdrop)
                                .drawBackdrop(
                                    backdrop = backdrop,
                                    shape = { ContinuousCapsule() },
                                    effects = {
                                        val progress = motion.pressProgress
                                        colorControls(saturation = CupertinoGlassDefaults.saturation)
                                        blur(NavBarSelectionBlurRadius.toPx())
                                        lens(
                                            lerp(NavBarRefractionHeight.toPx(), 24.dp.toPx(), progress),
                                            lerp(NavBarRefractionAmount.toPx(), 24.dp.toPx(), progress),
                                        )
                                    },
                                    highlight = { Highlight.Default.copy(alpha = 0.45f * motion.pressProgress) },
                                    onDrawSurface = { drawRect(colors.containerColor) },
                                )
                                .padding(NavBarPadding),
                            maxItemsInEachRow = 1,
                            horizontalArrangement = Arrangement.Center,
                        ) { content() }
                    }
                    if (tabsCount > 0) {
                        Box(
                            Modifier
                                .graphicsLayer {
                                    translationX = NavBarPadding.toPx()
                                    translationY = NavBarPadding.toPx() + motion.value * itemStep
                                }
                                .width(itemSize)
                                .height(itemHeight)
                                .drawBackdrop(
                                    backdrop = rememberCombinedBackdrop(backdrop, tabsBackdrop),
                                    shape = { ContinuousCapsule() },
                                    effects = {
                                        verticalNavigationLens(motion.pressProgress)
                                    },
                                    highlight = {
                                        Highlight.Default.copy(alpha = lerp(0.08f, 0.45f, motion.pressProgress))
                                    },
                                    shadow = { Shadow(alpha = lerp(0.04f, 0.6f, motion.pressProgress)) },
                                    innerShadow = {
                                        InnerShadow(radius = 2.dp + 2.dp * motion.pressProgress, alpha = 0.35f * motion.pressProgress)
                                    },
                                    layerBlock = {
                                        val expansionProgress = motion.expansionProgress
                                        val animation = motion.dragAnimation
                                        scaleX = 1f + (animation.scaleY - 1f) * lerp(0.85f, 0.4f, expansionProgress)
                                        scaleY = 1f + (animation.scaleX - 1f) * lerp(0.8f, 0.5f, expansionProgress)
                                        val velocity = abs(animation.velocity / 10f).coerceIn(0f, 0.2f)
                                        scaleY /= 1f - velocity * 0.75f
                                        scaleX *= 1f - velocity * 0.25f
                                    },
                                    onDrawSurface = {
                                        drawRect(verticalSelectionColor, alpha = 1f - motion.pressProgress)
                                    },
                                ),
                        )
                    }
                }
            }
            if (trailingAction != null) {
                Box(Modifier.size(itemSize), contentAlignment = Alignment.Center, content = trailingAction)
            }
        }
        return
    }
    if (trailingAction != null) {
        val density = LocalDensity.current
        var navigationHeight by remember(density) { mutableStateOf(NavBarHeight) }
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
                mutableIntStateOf(with(density) { NavBarHeight.roundToPx() })
            }
            val indicatorHeight = with(density) {
                (barHeightPx - (NavBarPadding * 2).roundToPx()).coerceAtLeast(NavBarItemHeight.roundToPx()).toDp()
            }

            val paddingPx = with(density) { NavBarHorizontalPadding.toPx() }
            val indicatorOverflowPx = with(density) { NavBarPadding.toPx() }
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

            // 공간이 충분하면 고정 항목 너비를 사용하고, 부족하면 균등하게 나눕니다.
            val itemWidthPx = if (calculatedItemWidthPx >= minItemWidthPx) {
                minItemWidthPx
            } else {
                calculatedItemWidthPx
            }
            val itemWidthDp: Dp = with(density) { itemWidthPx.toDp() }
            val indicatorWidthPx = itemWidthPx + indicatorOverflowPx * 2f

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
                                colorControls(saturation = CupertinoGlassDefaults.saturation)
                                blur(NavBarBlurRadius.toPx())
                                lens(
                                    NavBarRefractionHeight.toPx(),
                                    NavBarRefractionAmount.toPx(),
                                    depthEffect = true,
                                )
                            },
                            innerShadow = { InnerShadow(radius = 3.dp, alpha = 0.3f) },
                            shadow = {
                                Shadow(
                                    radius = 2.dp,
                                    offset = DpOffset(0.dp, 1.dp),
                                    color = Color.Black.copy(alpha = 0.16f),
                                )
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
                        .then(interactiveHighlight.modifier)
                        .wrapContentWidth()
                        .heightIn(min = NavBarHeight)
                        .onSizeChanged { size ->
                            if (barHeightPx != size.height) barHeightPx = size.height
                        }
                        .padding(horizontal = NavBarHorizontalPadding, vertical = NavBarPadding),
                    horizontalArrangement = Arrangement.spacedBy(NavBarItemGap),
                    verticalAlignment = Alignment.CenterVertically,
                    content = content
                )

                // ── Accent color overlay Row ──────────────────────────────────────────────────────
                CompositionLocalProvider(
                    LocalContentColor provides accentColor,
                    androidx.compose.material3.LocalContentColor provides accentColor,
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
                                    colorControls(saturation = CupertinoGlassDefaults.saturation)
                                    blur(NavBarSelectionBlurRadius.toPx())
                                    lens(
                                        lerp(NavBarRefractionHeight.toPx(), 24.dp.toPx(), progress),
                                        lerp(NavBarRefractionAmount.toPx(), 24.dp.toPx(), progress),
                                    )
                                },
                                highlight = {
                                    val progress = dampedDragAnimation.pressProgress
                                    Highlight.Default.copy(alpha = progress)
                                },
                                onDrawSurface = { drawRect(containerColor) }
                            )
                            .then(interactiveHighlight.modifier)
                            .wrapContentWidth()
                            .align(Alignment.CenterStart)
                            .height(indicatorHeight)
                            .padding(horizontal = NavBarHorizontalPadding),
                        horizontalArrangement = Arrangement.spacedBy(NavBarItemGap),
                        verticalAlignment = Alignment.CenterVertically,
                        content = content
                    )
                }

                // ── Sliding selection indicator Box ───────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            val leftX = itemLeftX(dampedDragAnimation.value) - indicatorOverflowPx
                            translationX = if (isLtr) {
                                leftX + panelOffset
                            } else {
                                rowWidth - leftX - indicatorWidthPx + panelOffset
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
                                Highlight.Default.copy(alpha = lerp(0.08f, 1f, progress))
                            },
                            shadow = {
                                val progress = dampedDragAnimation.pressProgress
                                Shadow(alpha = lerp(0.04f, 1f, progress))
                            },
                            innerShadow = {
                                val progress = dampedDragAnimation.pressProgress
                                InnerShadow(
                                    radius = 2.dp + 2.dp * progress,
                                    alpha = 0.35f * progress
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
                        .width(with(density) { indicatorWidthPx.toDp() })
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
                                            colorControls(saturation = CupertinoGlassDefaults.saturation)
                                            blur(NavBarBlurRadius.toPx())
                                            lens(
                                                NavBarRefractionHeight.toPx(),
                                                NavBarRefractionAmount.toPx(),
                                                depthEffect = true,
                                            )
                                        },
                                        innerShadow = { InnerShadow(radius = 3.dp, alpha = 0.3f) },
                                        shadow = {
                                            Shadow(
                                                radius = 2.dp,
                                                offset = DpOffset(0.dp, 1.dp),
                                                color = Color.Black.copy(alpha = 0.16f),
                                            )
                                        },
                                        onDrawSurface = { drawRect(surfaceTint) },
                                    ),
                            )
                        }
                        content.invoke(rowScope)
                    },
                ) { measurables, constraints ->
                    val minimumHeightPx = with(density) { NavBarHeight.roundToPx() }
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
    val isVertical = (LocalCupertinoVerticalBarPlacement.current?.width ?: 0.dp) > 0.dp
    val verticalMotion = LocalCupertinoVerticalNavMotion.current
    val isOverlay = LocalCupertinoNavBarOverlay.current
    val isCollapsed = !isVertical && navigationBarOptions?.isCollapsed == true && navigationBarOptions.prominentTabIndex != null
    val selectedIndex = navigationBarOptions?.selectedTabIndex ?: -1
    val prominentIndex = navigationBarOptions?.prominentTabIndex ?: -1
    val itemIndex = index ?: -1
    val isProminent = !isVertical && index != null && itemIndex == prominentIndex
    val isManaged = (isVertical || navigationBarOptions?.usesManagedLayout == true) && index != null
    val isSelected = isManaged && itemIndex == selectedIndex
    DisposableEffect(verticalMotion, index, enabled, isOverlay) {
        if (verticalMotion != null && index != null && !isOverlay) {
            verticalMotion.enabledTabs[index] = enabled
        }
        onDispose {
            if (verticalMotion != null && index != null && !isOverlay) {
                verticalMotion.enabledTabs.remove(index)
            }
        }
    }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animationScope = rememberCoroutineScope()
    val interactiveHighlight = remember(animationScope) {
        InteractiveHighlight(animationScope) { size, _ -> Offset(size.width / 2f, size.height / 2f) }
    }
    LaunchedEffect(interactionSource, enabled, isManaged, verticalMotion, isOverlay) {
        if (!enabled || !isManaged || verticalMotion != null || isOverlay) {
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
        targetValue = if (isManaged && verticalMotion == null && enabled && isPressed) 0.985f else 1f,
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
    val iconContentColor = if (isOverlay) {
        LocalContentColor.current
    } else if (isManaged) {
        itemColors?.iconColor(isSelected && verticalMotion == null, enabled) ?: CupertinoTheme.colorScheme.secondaryLabel
    } else {
        Color.Unspecified
    }
    val labelContentColor = if (isOverlay) {
        LocalContentColor.current
    } else if (isManaged) {
        itemColors?.textColor(isSelected && verticalMotion == null, enabled) ?: CupertinoTheme.colorScheme.secondaryLabel
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
        isVertical -> Color.Transparent
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
        verticalArrangement = if (isVertical) Arrangement.Center else Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
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
                            backdrop = navigationBarOptions?.backdrop ?: rememberLayerBackdrop(),
                            shape = { ContinuousCapsule() },
                            effects = {
                                colorControls(saturation = CupertinoGlassDefaults.saturation)
                                blur(NavBarBlurRadius.toPx())
                                lens(
                                    NavBarRefractionHeight.toPx(),
                                    lerp(NavBarRefractionAmount.toPx(), CupertinoGlassDefaults.pressedRefractionAmount.toPx(), interactiveHighlight.pressProgress),
                                    depthEffect = true,
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
                        .glassEdge(ContinuousCapsule(), softShadow = true)
                } else {
                    Modifier
                },
            )
            .then(
                if (isManaged && !isProminent && itemFillColor != Color.Transparent
                ) {
                    Modifier
                        .drawBackdrop(
                            backdrop = navigationBarOptions?.backdrop ?: rememberLayerBackdrop(),
                            shape = { ContinuousCapsule() },
                            effects = {
                                colorControls(saturation = CupertinoGlassDefaults.saturation)
                                blur(NavBarSelectionBlurRadius.toPx())
                                lens(
                                    NavBarRefractionHeight.toPx(),
                                    lerp(NavBarRefractionAmount.toPx(), CupertinoGlassDefaults.pressedRefractionAmount.toPx(), interactiveHighlight.pressProgress),
                                    depthEffect = true,
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
                        .glassEdge(ContinuousCapsule(), softShadow = true)
                } else {
                    Modifier
                },
            )
            .then(if (isManaged && verticalMotion == null && enabled && !isOverlay) interactiveHighlight.modifier.then(interactiveHighlight.gestureModifier) else Modifier)
            .clip(ContinuousCapsule())
            .then(
                if (isOverlay) {
                    Modifier
                } else if (isManaged) {
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
            .heightIn(min = if (isVertical) {
                NavBarVerticalItemHeight + 12.dp * (verticalMotion?.expansionProgress ?: 0f)
            } else NavBarItemHeight)
            .width(if (verticalMotion != null) itemWidth else animatedItemWidth)
            .graphicsLayer {
                val s = scale() * pressScale
                scaleX = s
                scaleY = s
            }
            .then(
                if (isManaged && verticalMotion == null && enabled && !isOverlay) {
                    Modifier.graphicsLayer(interactiveHighlight.layerBlock)
                } else {
                    Modifier
                },
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            propagateMinConstraints = true,
            modifier = Modifier
                .size(20.dp)
        ) {
            if (isManaged) {
                CompositionLocalProvider(LocalContentColor provides managedIconColor) {
                    icon()
                }
            } else {
                icon()
            }
        }
        if (!isVertical || verticalMotion != null) ProvideTextStyle(
            value = TextStyle(
                fontFamily = FontFamily.Default,
                fontSize = 11.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.sp,
            )
        ) {
            Box(
                if (verticalMotion != null) {
                    Modifier.height(13.dp * verticalMotion.expansionProgress)
                        .graphicsLayer { alpha = verticalMotion.expansionProgress }
                } else Modifier,
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    if (verticalMotion != null) Modifier.requiredHeight(13.dp) else Modifier,
                    contentAlignment = Alignment.Center,
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
        } else {
            Box(Modifier.size(0.dp)) { label?.invoke() }
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

    val windowInsets = WindowInsets(left = 16.dp, right = 16.dp)
    val BottomPadding = 20.dp
}

internal val LocalLiquidBottomTabScale = staticCompositionLocalOf { { 1f } }
internal val LocalCupertinoNavItemWidth = staticCompositionLocalOf { NavBarItemMinWidth }

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

private val LocalCupertinoVerticalNavMotion = staticCompositionLocalOf<CupertinoVerticalNavigationBarState?> { null }
private val LocalCupertinoNavBarOverlay = staticCompositionLocalOf { false }

private fun BackdropEffectScope.verticalNavigationLens(progress: Float) {
    if (progress <= 0f) return
    // 렌즈 경계에서 배경 선을 이어 주고, 안쪽으로 진입하면서 굴절시킵니다.
    runtimeShaderEffect(
        key = "CupertinoVerticalNavigationLens",
        shaderString = """
uniform shader content;
uniform float2 size;
uniform float refractionHeight;
uniform float refractionAmount;

half4 sampleContent(float2 coord) {
    return content.eval(clamp(coord, float2(0.5), size - 0.5));
}

half4 main(float2 coord) {
    float2 halfSize = size * 0.5;
    float2 centered = coord - halfSize;
    float radius = min(halfSize.x, halfSize.y);
    float2 corner = abs(centered) - (halfSize - radius);
    float distance = length(max(corner, 0.0)) + min(max(corner.x, corner.y), 0.0) - radius;
    if (-distance >= refractionHeight) return content.eval(coord);

    float edge = clamp(1.0 + distance / refractionHeight, 0.0, 1.0);
    float displacement = 0.6 * edge * (1.0 - edge) * refractionAmount;
    float2 normal = sign(centered) * (max(corner.x, corner.y) > 0.0
        ? normalize(max(corner, 0.0))
        : (corner.x > corner.y ? float2(1.0, 0.0) : float2(0.0, 1.0)));
    float2 refracted = coord + displacement * normal;
    float dispersion = 0.2 * (centered.x * centered.y) / (halfSize.x * halfSize.y);
    float2 spread = displacement * normal * dispersion;

    half4 red = sampleContent(refracted + spread);
    half4 orange = sampleContent(refracted + spread * (2.0 / 3.0));
    half4 yellow = sampleContent(refracted + spread * (1.0 / 3.0));
    half4 green = sampleContent(refracted);
    half4 cyan = sampleContent(refracted - spread * (1.0 / 3.0));
    half4 blue = sampleContent(refracted - spread * (2.0 / 3.0));
    half4 purple = sampleContent(refracted - spread);
    return half4(
        (red.r + orange.r + yellow.r) / 3.5 + purple.r / 7.0,
        orange.g / 7.0 + (yellow.g + green.g + cyan.g) / 3.5,
        (cyan.b + blue.b + purple.b) / 3.0,
        (red.a + orange.a + yellow.a + green.a + cyan.a + blue.a + purple.a) / 7.0
    );
}
""",
        uniformShaderName = "content",
    ) {
        setFloatUniform("size", size.width, size.height)
        setFloatUniform("refractionHeight", 16.dp.toPx() * progress)
        setFloatUniform("refractionAmount", 24.dp.toPx() * progress)
    }
}

@Stable
internal class CupertinoVerticalNavigationBarState(
    private val scope: CoroutineScope,
    initialIndex: Int,
    tabsCount: Int,
) {
    private val lastIndex = (tabsCount - 1).coerceAtLeast(0)
    val dragAnimation = DampedDragAnimation(
        animationScope = scope,
        initialValue = initialIndex.coerceIn(0, lastIndex).toFloat(),
        valueRange = 0f..lastIndex.toFloat(),
        visibilityThreshold = 0.001f,
        initialScale = 1f,
        pressedScale = CupertinoVerticalBarControlWidth /
            (CupertinoVerticalBarControlWidth - NavBarPadding * 2) * (78f / 56f),
        onDragStarted = {},
        onDragStopped = {},
        onDrag = { _, _ -> },
    )
    val value: Float get() = dragAnimation.value
    val pressProgress: Float get() = dragAnimation.pressProgress
    private val expansionAnimation = Animatable(0f)
    private var expansionJob: Job? = null
    val expansionProgress: Float get() = expansionAnimation.value.coerceIn(0f, 1f)
    val enabledTabs = mutableStateMapOf<Int, Boolean>()
    var isPressed by mutableStateOf(false)
        private set
    private var targetPosition = value

    fun press(index: Int) {
        isPressed = true
        targetPosition = index.coerceIn(0, lastIndex).toFloat()
        dragAnimation.press()
        dragAnimation.updateValue(targetPosition)
        expansionJob?.cancel()
        expansionJob = scope.launch {
            launch { expansionAnimation.animateTo(0f, spring(1f, 1000f, 0.001f)) }
            val startTime = withFrameNanos { it }
            var elapsed = 0L
            while (elapsed < 500_000_000L) {
                elapsed = withFrameNanos { it - startTime }
            }
            expansionAnimation.animateTo(1f, spring(1f, 1000f, 0.001f))
        }
    }

    fun dragTo(value: Float): Job {
        targetPosition = value.coerceIn(0f, lastIndex.toFloat())
        return dragAnimation.snapToValue(targetPosition)
    }

    fun release(index: Int) {
        isPressed = false
        targetPosition = index.coerceIn(0, lastIndex).toFloat()
        dragAnimation.updateValue(targetPosition)
        dragAnimation.release()
        expansionJob?.cancel()
        expansionJob = scope.launch {
            expansionAnimation.animateTo(0f, spring(1f, 1000f, 0.001f))
        }
    }

    fun select(index: Int) {
        val target = index.coerceIn(0, lastIndex).toFloat()
        if (isPressed || targetPosition == target) return
        targetPosition = target
        dragAnimation.animateToValue(target)
    }
}
