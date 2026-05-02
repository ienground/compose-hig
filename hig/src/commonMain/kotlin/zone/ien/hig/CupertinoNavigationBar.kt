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

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
import com.kyant.shapes.Capsule
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.utils.DampedDragAnimation
import zone.ien.hig.utils.InteractiveHighlight
import zone.ien.hig.utils.rememberDefaultBackdrop
import kotlin.math.abs
import kotlin.math.sign

private val NavBarPadding = 4.dp
private val NavBarItemGap = 0.dp  // 아이템 사이 고정 간격. 필요 시 조절

@Composable
@ExperimentalCupertinoApi
fun CupertinoNavigationBar(
    modifier: Modifier = Modifier,
    colors: CupertinoNavigationBarColors = CupertinoNavigationBarDefaults.colors(),
    windowInsets: WindowInsets = WindowInsets.navigationBars,
    backdrop: LayerBackdrop,
    selectedTabIndex: () -> Int,
    onTabSelected: (index: Int) -> Unit,
    tabsCount: Int,
    content: @Composable RowScope.() -> Unit,
) {
    val isLightTheme = !isSystemInDarkTheme()
    val tabsBackdrop = rememberLayerBackdrop()
    val accentColor = colors.accentColor
    val containerColor = colors.containerColor.copy(0.6f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = CupertinoNavigationBarDefaults.BottomPadding)
            .wrapContentWidth()
            .windowInsetsPadding(windowInsets)
    ) {
        Box(
            contentAlignment = Alignment.CenterStart,
        ) {
            val density = LocalDensity.current

            val paddingPx = with(density) { NavBarPadding.toPx() }
            val itemWidthPx = with(density) { CupertinoNavigationBarItemWidth.toPx() }
            val gapPx = with(density) { NavBarItemGap.toPx() }

            // NavBar 전체 너비 = padding*2 + itemWidth*n + gap*(n-1)
            // CupertinoNavigationBarItemWidth 바꾸면 이 값이 자동으로 연동됨
            val rowWidth = paddingPx * 2f + itemWidthPx * tabsCount + gapPx * (tabsCount - 1)

            // 아이템 i 왼쪽 X (BoxWithConstraints 절대좌표)
            // = paddingPx + (itemWidthPx + gapPx) * i
            fun itemLeftX(index: Float): Float = paddingPx + (itemWidthPx + gapPx) * index
            fun itemCenterX(index: Float): Float = itemLeftX(index) + itemWidthPx / 2f

            // indicator 이동 step = itemWidth + gap
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

            // ── 배경 Row ──────────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .graphicsLayer {
                        translationX = panelOffset
                    }
                    .drawBackdrop(
                        backdrop = backdrop,
                        shape = { Capsule() },
                        effects = {
                            vibrancy()
                            blur(2.dp.toPx())
                            lens(24.dp.toPx(), 24.dp.toPx())
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
                    .height(64.dp)
                    .padding(NavBarPadding),
                horizontalArrangement = Arrangement.spacedBy(NavBarItemGap),
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )

            // ── 액센트 컬러 오버레이 Row ──────────────────────────────────────────
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
                            shape = { Capsule() },
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
                        .height(56.dp)
                        .padding(horizontal = NavBarPadding)
                        .graphicsLayer(colorFilter = ColorFilter.tint(accentColor)),
                    horizontalArrangement = Arrangement.spacedBy(NavBarItemGap),
                    verticalAlignment = Alignment.CenterVertically,
                    content = content
                )
            }

            // ── 슬라이딩 선택 indicator Box ───────────────────────────────────────
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
                        shape = { Capsule() },
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
                            drawRect(
                                if (isLightTheme) Color.Black.copy(0.1f)
                                else Color.White.copy(0.1f),
                                alpha = 1f - progress
                            )
                            drawRect(Color.Black.copy(alpha = 0.03f * progress))
                        }
                    )
                    .align(Alignment.CenterStart)
                    .height(56.dp)
                    .width(CupertinoNavigationBarItemWidth)
            )
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
    val scale = LocalLiquidBottomTabScale.current

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(Capsule())
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Tab,
                onClick = onClick
            )
            .fillMaxHeight()
            .width(CupertinoNavigationBarItemWidth)
            .graphicsLayer {
                val s = scale()
                scaleX = s
                scaleY = s
            }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(18.dp)
        ) {
            icon()
        }
        ProvideTextStyle(
            value = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold)
        ) {
            label?.invoke()
        }
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
        get() = CupertinoTheme.colorScheme.tertiarySystemBackground

    @Composable
    @ReadOnlyComposable
    fun colors(
        accentColor: Color = CupertinoTheme.colorScheme.accent,
        containerColor: Color = CupertinoTheme.colorScheme.systemBackground,
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
}

internal val LocalLiquidBottomTabScale = staticCompositionLocalOf { { 1f } }
internal val CupertinoNavigationBarItemWidth = 90.dp