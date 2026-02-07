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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.util.lerp
import com.kyant.backdrop.Backdrop
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.utils.DampedDragAnimation
import zone.ien.hig.utils.InteractiveHighlight
import kotlin.math.abs
import kotlin.math.sign

/**
 * Return true if container can't scroll forward
 * */
inline val ScrollableState.isNavigationBarTransparent: Boolean
    get() = !canScrollForward

internal val LocalLiquidBottomTabScale =
    staticCompositionLocalOf { { 1f } }

/**
 * Navigation bar itself does not produce cupertino thin material glass effect.
 * This effect works only inside [CupertinoScaffold], [CupertinoBottomSheetScaffold], [CupertinoBottomSheetContent].
 * Use this function to achieve this effect with custom bottom bar.
 * It will communicate with scaffold and return either [Color.Transparent] if color was
 * successfully applied to scaffold (and navigation bar itself should be transparent) or passed color
 * if scaffold wasn't found.
 *
 * @param color navigation bar container color. Alpha is controlled by the [CupertinoScaffold]
 * @param isTransparent if navigation bar currently should be transparent. See [CupertinoNavigationBar]
 * for use cases example.
 * */
@Composable
@ExperimentalCupertinoApi
fun cupertinoTranslucentBottomBarColor(
    color: Color,
    isTranslucent: Boolean,
    isTransparent: Boolean,
): Color {
    if (!isTranslucent) {
        return color
    }

    val appBarsState = LocalAppBarsState.current ?: return color

    DisposableEffect(appBarsState, color) {
        appBarsState.bottomBarColor.value = color
        onDispose {
            appBarsState.bottomBarColor.value = Color.Unspecified
        }
    }

    DisposableEffect(isTransparent, appBarsState) {
        appBarsState.isBottomBarTransparent.value = isTransparent
        onDispose {
            appBarsState.isBottomBarTransparent.value = true
        }
    }
    return Color.Transparent
}

/**
 * Cupertino bottom navigation tab bar
 *
 * [CupertinoNavigationBarItem]s should be used as navigation bar content
 *
 * Note: navigation bar itself does not produce cupertino thin material glass effect.
 * This effect works only inside [CupertinoScaffold], [CupertinoBottomSheetScaffold], [CupertinoBottomSheetContent].
 * To achieve this effect with custom bottom bar use [cupertinoTranslucentTopBarColor]
 * function that will communicate with scaffold and return either
 * [Color.Transparent] if color was successfully applied to scaffold (and top bar itself
 * should be transparent) or passed color if scaffold wasn't found.
 *
 * @param modifier the [Modifier] to be applied to this top app bar.
 * @param windowInsets a window insets that app bar will respect.
 * @param containerColor color of the navigation bar background.
 * @param isTransparent navigation bar is usually transparent if scroll container reached bottom.
 * [ScrollableState.isNavigationBarTransparent] and [LazyListState.isNavigationBarTransparent] can be used to track it
 * @param isTranslucent works only inside [CupertinoScaffold]. Blurred content behind navigation bar will be
 * visible if navigation bar is translucent. Simulates iOS app bars material.
 * @param divider top divider when [isTransparent] is false
 * @param content navigation bar content, usually [CupertinoNavigationBarItem]
 *
 * @see CupertinoNavigationBarItem
 */
@Composable
@ExperimentalCupertinoApi
fun CupertinoNavigationBar(
    modifier: Modifier = Modifier,
    containerColor: Color = CupertinoNavigationBarDefaults.containerColor,
    windowInsets: WindowInsets = WindowInsets.navigationBars,
    isTransparent: Boolean = false,
    isTranslucent: Boolean = LocalAppBarsState.current != null,
    divider: @Composable () -> Unit = {
        CupertinoNavigationBarDefaults.divider()
    },
    content: @Composable RowScope.() -> Unit,
) {
    val color =
        cupertinoTranslucentBottomBarColor(
            color = containerColor,
            isTranslucent = isTranslucent,
            isTransparent = isTransparent,
        )

    CupertinoSurface(
        modifier = modifier,
        color = color,
    ) {
        Column(
            Modifier.windowInsetsPadding(windowInsets),
        ) {
            if (!isTransparent) {
                divider()
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(CupertinoNavigationBarTokens.Height)
                    .selectableGroup(),
                horizontalArrangement = Arrangement.SpaceBetween,
                content = content,
            )
        }
    }
}

@Composable
fun CupertinoNavigationBar(
    selectedTabIndex: () -> Int,
    onTabSelected: (index: Int) -> Unit,
    backdrop: Backdrop,
    tabsCount: Int,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val isLightTheme = !isSystemInDarkTheme()
    val accentColor =
        if (isLightTheme) Color(0xFF0088FF)
        else Color(0xFF0091FF)
    val containerColor =
        if (isLightTheme) Color(0xFFFAFAFA).copy(0.4f)
        else Color(0xFF121212).copy(0.4f)

    val tabsBackdrop = rememberLayerBackdrop()

    BoxWithConstraints(
        modifier,
        contentAlignment = Alignment.CenterStart
    ) {
        val density = LocalDensity.current
        val tabWidth = with(density) {
            (constraints.maxWidth.toFloat() - 8f.dp.toPx()) / tabsCount
        }

        val offsetAnimation = remember { Animatable(0f) }
        val panelOffset by remember(density) {
            derivedStateOf {
                val fraction = (offsetAnimation.value / constraints.maxWidth).fastCoerceIn(-1f, 1f)
                with(density) {
                    4f.dp.toPx() * fraction.sign * EaseOut.transform(abs(fraction))
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
                        (targetValue + dragAmount.x / tabWidth * if (isLtr) 1f else -1f)
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
                position = { size, offset ->
                    Offset(
                        if (isLtr) (dampedDragAnimation.value + 0.5f) * tabWidth + panelOffset
                        else size.width - (dampedDragAnimation.value + 0.5f) * tabWidth + panelOffset,
                        size.height / 2f
                    )
                }
            )
        }

        Row(
            Modifier
                .graphicsLayer {
                    translationX = panelOffset
                }
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { RoundedCornerShape(100.dp) },
                    effects = {
                        vibrancy()
                        blur(8f.dp.toPx())
                        lens(24f.dp.toPx(), 24f.dp.toPx())
                    },
                    layerBlock = {
                        val progress = dampedDragAnimation.pressProgress
                        val scale = lerp(1f, 1f + 16f.dp.toPx() / size.width, progress)
                        scaleX = scale
                        scaleY = scale
                    },
                    onDrawSurface = { drawRect(containerColor) }
                )
                .then(interactiveHighlight.modifier)
                .height(64f.dp)
                .fillMaxWidth()
                .padding(4f.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )

        CompositionLocalProvider(
            LocalLiquidBottomTabScale provides {
                lerp(1f, 1.2f, dampedDragAnimation.pressProgress)
            }
        ) {
            Row(
                Modifier
                    .clearAndSetSemantics {}
                    .alpha(0f)
                    .layerBackdrop(tabsBackdrop)
                    .graphicsLayer {
                        translationX = panelOffset
                    }
                    .drawBackdrop(
                        backdrop = backdrop,
                        shape = { RoundedCornerShape(100.dp) },
                        effects = {
                            val progress = dampedDragAnimation.pressProgress
                            vibrancy()
                            blur(8f.dp.toPx())
                            lens(
                                24f.dp.toPx() * progress,
                                24f.dp.toPx() * progress
                            )
                        },
                        highlight = {
                            val progress = dampedDragAnimation.pressProgress
                            Highlight.Default.copy(alpha = progress)
                        },
                        onDrawSurface = { drawRect(containerColor) }
                    )
                    .then(interactiveHighlight.modifier)
                    .height(56f.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 4f.dp)
                    .graphicsLayer(colorFilter = ColorFilter.tint(accentColor)),
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }

        Box(
            Modifier
                .padding(horizontal = 4f.dp)
                .graphicsLayer {
                    translationX =
                        if (isLtr) dampedDragAnimation.value * tabWidth + panelOffset
                        else size.width - (dampedDragAnimation.value + 1f) * tabWidth + panelOffset
                }
                .then(interactiveHighlight.gestureModifier)
                .then(dampedDragAnimation.modifier)
                .drawBackdrop(
                    backdrop = rememberCombinedBackdrop(backdrop, tabsBackdrop),
                    shape = { RoundedCornerShape(100.dp) },
                    effects = {
                        val progress = dampedDragAnimation.pressProgress
                        lens(
                            10f.dp.toPx() * progress,
                            14f.dp.toPx() * progress,
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
                            radius = 8f.dp * progress,
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
                .height(56f.dp)
                .fillMaxWidth(1f / tabsCount)
        )
    }
}

/**
 * Item of the [CupertinoNavigationBar]
 *
 * @param selected if tab with this item is selected
 * @param onClick action performed on item click
 * @param icon item icon
 * @param modifier modifier applied to item container
 * @param enabled if this item is clickable
 * @param label item label located below the [icon]
 * @param alwaysShowLabel if label should always be visible. If this flag is false then
 * label will only be visible if this item is selected
 * @param colors item colors. See [CupertinoNavigationBarDefaults.itemColors]
 * @param interactionSource interaction source of the item click modifier
 * */
/*
@Composable
@ExperimentalCupertinoApi
fun RowScope.CupertinoNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    alwaysShowLabel: Boolean = true,
    pressIndicationEnabled: Boolean = false,
    colors: CupertinoNavigationBarItemColors = CupertinoNavigationBarDefaults.itemColors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val pressed by interactionSource.collectIsPressedAsState()

    Column(
        modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                enabled = enabled,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = null,
            ).weight(1f)
            .padding(top = 6.dp)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        val iconColor = colors.iconColor(selected, enabled)
        val textColor = colors.textColor(selected, enabled)

        ProvideTextStyle(
            value = CupertinoTheme.typography.caption2,
        ) {
            val alpha =
                if (pressIndicationEnabled && pressed && !selected) {
                    textColor.alpha * CupertinoButtonTokens.PressedPlainButonAlpha
                } else {
                    textColor.alpha
                }

            CompositionLocalProvider(
                LocalContentColor provides iconColor.copy(alpha = alpha),
            ) {
                Box(
                    modifier = Modifier.size(CupertinoIconDefaults.MediumSize),
                    contentAlignment = Alignment.Center,
                ) {
                    icon()
                }

                if (label != null && (alwaysShowLabel || selected)) {
                    label()
                }
            }
        }
    }
}

 */
@Composable
@ExperimentalCupertinoApi
fun RowScope.CupertinoNavigationBarItem(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
//    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
//    alwaysShowLabel: Boolean = true,
//    pressIndicationEnabled: Boolean = false,
//    colors: CupertinoNavigationBarItemColors = CupertinoNavigationBarDefaults.itemColors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val pressed by interactionSource.collectIsPressedAsState()

    val scale = LocalLiquidBottomTabScale.current
    Column(
        modifier
            .clip(RoundedCornerShape(100.dp))
            .clickable(
                interactionSource = null,
                indication = null,
                role = Role.Tab,
                onClick = onClick
            )
            .fillMaxHeight()
            .weight(1f)
            .graphicsLayer {
                val scale = scale()
                scaleX = scale
                scaleY = scale
            },
        verticalArrangement = Arrangement.spacedBy(2f.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = {
            val contentColor = if (!isSystemInDarkTheme()) Color.Black else Color.White

            Box(
                modifier = Modifier.size(28.dp),
                contentAlignment = Alignment.Center
            ) {
                CompositionLocalProvider(LocalContentColor provides contentColor) {
                    icon()
                }
            }
            ProvideTextStyle(
                value = TextStyle(contentColor, 12.sp)
            ) {
                label?.invoke()
            }
        }
    )
}

@Stable
@ExperimentalCupertinoApi
class CupertinoNavigationBarItemColors internal constructor(
    private val selectedIconColor: Color,
    private val selectedTextColor: Color,
    private val unselectedIconColor: Color,
    private val unselectedTextColor: Color,
    private val disabledIconColor: Color,
    private val disabledTextColor: Color,
) {
    /**
     * Represents the icon color for this item, depending on whether it is [selected].
     *
     * @param selected whether the item is selected
     * @param enabled whether the item is enabled
     */
    @Composable
    internal fun iconColor(
        selected: Boolean,
        enabled: Boolean,
    ): Color =
        when {
            !enabled -> disabledIconColor
            selected -> selectedIconColor
            else -> unselectedIconColor
        }

    /**
     * Represents the text color for this item, depending on whether it is [selected].
     *
     * @param selected whether the item is selected
     * @param enabled whether the item is enabled
     */
    @Composable
    internal fun textColor(
        selected: Boolean,
        enabled: Boolean,
    ): Color =
        when {
            !enabled -> disabledTextColor
            selected -> selectedTextColor
            else -> unselectedTextColor
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is CupertinoNavigationBarItemColors) return false

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
    /**
     * Default container color of the [CupertinoNavigationBar]
     *
     * Note: navigation bar itself does not produce cupertino thin material glass effect.
     * This effect works only inside [CupertinoScaffold], [CupertinoBottomSheetScaffold], [CupertinoBottomSheetContent].
     * To achieve this effect with custom top app bar use [cupertinoTranslucentTopBarColor]
     * function that will communicate with scaffold and return either
     * [Color.Transparent] if color was successfully applied to scaffold (and top bar itself
     * should be transparent) or passed color if scaffold wasn't found.
     * */
    val containerColor: Color
        @Composable
        @ReadOnlyComposable
        get() = CupertinoTheme.colorScheme.tertiarySystemBackground

    @Composable
    @ReadOnlyComposable
    fun itemColors(
        selectedIconColor: Color = CupertinoTheme.colorScheme.accent,
        selectedTextColor: Color = CupertinoTheme.colorScheme.accent,
        unselectedIconColor: Color = CupertinoTheme.colorScheme.secondaryLabel,
        unselectedTextColor: Color = CupertinoTheme.colorScheme.secondaryLabel,
        disabledIconColor: Color = CupertinoTheme.colorScheme.tertiaryLabel,
        disabledTextColor: Color = CupertinoTheme.colorScheme.tertiaryLabel,
    ) = CupertinoNavigationBarItemColors(
        selectedIconColor = selectedIconColor,
        selectedTextColor = selectedTextColor,
        unselectedIconColor = unselectedIconColor,
        unselectedTextColor = unselectedTextColor,
        disabledIconColor = disabledIconColor,
        disabledTextColor = disabledTextColor,
    )

    @Composable
    fun divider() {
        CupertinoHorizontalDivider()
    }
}

internal object CupertinoNavigationBarTokens {
    val Height = 49.dp
}
