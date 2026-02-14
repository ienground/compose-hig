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

import androidx.compose.animation.Animatable as ColorAnimatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.drawBackdrop
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import zone.ien.hig.section.CupertinoSectionDefaults
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.theme.darkColorScheme
import zone.ien.hig.theme.lightColorScheme
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Return true if container scroll offset is smaller than [topPadding]
 * */
@Composable
@ExperimentalCupertinoApi
fun LazyListState.isLiquidTopBarTransparent(topPadding: Dp = 0.dp): Boolean {
    val topPaddingPx =
        LocalDensity.current.run {
            remember(topPadding) {
                topPadding.toPx()
            }
        }

    layoutInfo.visibleItemsInfo.first().offset
    return remember {
        derivedStateOf {
            !canScrollBackward ||
                    firstVisibleItemIndex == 0 &&
                    firstVisibleItemScrollOffset < topPaddingPx
        }
    }.value
}

/**
 * Top app bar itself does not produce cupertino thin material glass effect.
 * This effect works only inside [CupertinoScaffold], [CupertinoBottomSheetScaffold], [CupertinoBottomSheetContent].
 * Use this function to achieve this effect with custom top app bar.
 * It will communicate with scaffold and return either [Color.Transparent] if color was
 * successfully applied to scaffold (and top bar itself should be transparent) or passed color
 * if scaffold wasn't found.
 *
 * @param color top bar container color. Alpha is controlled by the [CupertinoScaffold]
 * @param isTransparent if top bar currently should be transparent. See [CupertinoTopAppBar]
 * for use cases example.
 * */
@Composable
@ExperimentalCupertinoApi
fun cupertinoTranslucentLiquidTopBarColor(
    color: Color,
    isTranslucent: Boolean,
    isTransparent: Boolean,
): Color {
    if (!isTranslucent) {
        return color
    }
    val appBarsState = LocalAppBarsState.current ?: return color

    DisposableEffect(appBarsState, color) {
        appBarsState.topBarColor.value = color
        onDispose {
            appBarsState.topBarColor.value = Color.Unspecified
        }
    }

    DisposableEffect(isTransparent, appBarsState) {
        appBarsState.isTopBarTransparent.value = isTransparent
        onDispose {
            appBarsState.isTopBarTransparent.value = true
        }
    }
    return Color.Transparent
}

/**
 * Top app bar with center aligned title
 *
 * @param title the title to be displayed at the center of the top app bar.
 * @param modifier the [Modifier] to be applied to this top app bar.
 * @param navigationIcon the navigation icon displayed at the start of the top app bar. This should
 * typically be an [CupertinoIconButton].
 * @param actions the actions displayed at the end of the top app bar. This should typically be
 * [CupertinoIconButton]s. The default layout here is a [Row], so icons inside will be placed horizontally.
 * @param windowInsets a window insets that app bar will respect.
 * @param isCenterAligned if title should be aligned to the center. Else it will be aligned to the start.
 * @param isTransparent top bar is usually transparent if scroll container reached or almost reached top.
 * [ScrollableState.isTopBarTransparent] and [LazyListState.isTopBarTransparent] can be used to track it
 * @param isTranslucent works only inside [CupertinoScaffold]. Blurred content behind top bar will be
 * visible if top bar is translucent. Simulates iOS app bars material.
 * @param colors [CupertinoTopAppBarColors] that will be used to resolve the colors used for this top app
 * bar in different states. See [CupertinoTopAppBarDefaults.topAppBarColors].
 */
@Composable
@ExperimentalCupertinoApi
fun CupertinoLiquidTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable (RowScope.() -> Unit) = {},
    windowInsets: WindowInsets = LocalLiquidTopAppBarInsets.current ?: CupertinoLiquidTopAppBarDefaults.windowInsets,
    isBackgroundAdaptive: Boolean = true,
    backdrop: LayerBackdrop,
    isCenterAligned: Boolean = true,
    isTransparent: Boolean = false,
    isTranslucent: Boolean = LocalAppBarsState.current != null,
    colors: CupertinoLiquidTopAppBarColors = CupertinoLiquidTopAppBarDefaults.topAppBarColors(),
) {
    val navTitleVisible by LocalNavigationTitleVisible.current
    val transparent = isTransparent || navTitleVisible

    InlineTopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = actions,
        windowInsets = windowInsets,
        colors = colors,
        isBackgroundAdaptive = isBackgroundAdaptive,
        backdrop = backdrop,
        isCenterAligned = isCenterAligned,
        isTransparent = transparent,
        isTranslucent = isTranslucent,
    )
}

private class LiquidClipShape(
    private val offsetDifference: Float,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline =
        Outline.Rectangle(
            Rect(
                top = offsetDifference.coerceAtMost(size.height),
                left = 0f,
                right = size.width,
                bottom = size.height,
            ),
        )
}

private const val NavTitleMaxFontScale = 1.1f
private val NavTitleMaxFontScaleDistance = 150.dp

/**
 * Navigation Title.
 *
 * Should be the first element in the first scrollable container inside
 * [CupertinoScaffold]/[CupertinoBottomSheetScaffold]/[CupertinoBottomSheetContent] body.
 * One container can have only one title.
 * Can behave unexpectedly when this precondition is violated.
 *
 * Navigation title will automatically manage [CupertinoTopAppBar] title visibility
 * and background transparency when usage precondition is fulfilled.
 *
 * @param modifier modifier of the title container
 * @param maxFontScale maximum font scale. Must be >= 1
 * @param maxFontScaleDistance distance of the scroll overflow at which [maxFontScale] is reached
 * @param paddingValues title padding values
 * @param content title content
 * */
@Composable
fun CupertinoLiquidNavigationTitle(
    modifier: Modifier = Modifier,
    maxFontScale: Float = NavTitleMaxFontScale,
    maxFontScaleDistance: Dp = NavTitleMaxFontScaleDistance,
    paddingValues: PaddingValues = CupertinoSectionDefaults.PaddingValues,
    content: @Composable () -> Unit,
) {
    require(maxFontScale >= 1) {
        "maxFontScale must be >= 1."
    }
    var visible by LocalNavigationTitleVisible.current

    val density = LocalDensity.current

    val scaffoldCoordinates by LocalScaffoldCoordinates.current

//    val top = (LocalScaffoldInsets.current ?: WindowInsets.statusBars).getTop(density)

    val topBarHeightPx = (LocalTopBarHeight.current.value ?: 0f)

    val topAppBarExists = topBarHeightPx > Float.MIN_VALUE
//    var size by remember {
//        mutableStateOf(0f)
//    }

//    val topBarHeightPx = remember(density) {
//        density.run {
//            TopAppBarHeight.toPx() + top
//        }
//    }

    var offsetDifference by remember { mutableStateOf(0f) }

// TODO: nav title snap
//
//    LaunchedEffect(scrollableState) {
//        snapshotFlow {
//            scrollableState?.isScrollInProgress == true
//        }.distinctUntilChanged().collect {
//            if (!it) {
//                val diff = offsetDifference.value
//
//                println(diff)
//
//                if (diff > size)
//                    return@collect
//                val scroll = if (diff < size / 2) {
//                   -diff
//                } else size - diff
//
//                try {
//                    scrollableState?.animateScrollBy(scroll)
//                } finally {
//                }
//            }
//        }
//    }

    val maxSizeIncreaseDistancePx = density.run { maxFontScaleDistance.toPx() }

    val insets = LocalScaffoldInsets.current?.getTop(density) ?: 0
    val fontIncrease by remember(maxSizeIncreaseDistancePx, maxFontScale) {
        derivedStateOf {
            val d = offsetDifference + topBarHeightPx - insets
            if (d >= 0) {
                1f
            } else {
                1f + (-d / maxSizeIncreaseDistancePx).coerceIn(0f, 1f) * (maxFontScale - 1)
            }
        }
    }

    val font = CupertinoTheme.typography.largeTitle.copy(fontWeight = FontWeight.Bold)
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(1.2f)
    )

    Box(
        modifier
            .padding(paddingValues)
            .alpha(alpha)
            .onGloballyPositioned {
                val scaffoldTop = (scaffoldCoordinates?.boundsInWindow()?.top ?: 0f)

                offsetDifference = (topBarHeightPx - it.boundsInWindow().top) + scaffoldTop

                visible = !topAppBarExists || offsetDifference < it.size.height
            },
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides font.copy(fontSize = font.fontSize * fontIncrease),
        ) {
            content()
        }
    }
}

@Stable
class CupertinoLiquidTopAppBarColors internal constructor(
    private val lightContainerColor: Color,
    private val darkContainerColor: Color,
    internal val lightNavigationIconContentColor: Color,
    internal val darkNavigationIconContentColor: Color,
    internal val lightTitleContentColor: Color,
    internal val darkTitleContentColor: Color,
    internal val lightActionIconContentColor: Color,
    internal val darkActionIconContentColor: Color,
) {
    @Composable
    internal fun containerColor(isDark: Boolean = isSystemInDarkTheme()): State<Color> = rememberUpdatedState(if (isDark) darkContainerColor else lightContainerColor)

    @Composable
    internal fun navigationIconContentColor(isDark: Boolean = isSystemInDarkTheme()): State<Color> = rememberUpdatedState(if (isDark) darkNavigationIconContentColor else lightNavigationIconContentColor)

    @Composable
    internal fun titleContentColor(isDark: Boolean = isSystemInDarkTheme()): State<Color> = rememberUpdatedState(if (isDark) darkTitleContentColor else lightTitleContentColor)

    @Composable
    internal fun actionIconContentColor(isDark: Boolean = isSystemInDarkTheme()): State<Color> = rememberUpdatedState(if (isDark) darkActionIconContentColor else lightActionIconContentColor)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is CupertinoLiquidTopAppBarColors) return false

        if (lightContainerColor != other.lightContainerColor) return false
        if (darkContainerColor != other.darkContainerColor) return false
        if (lightNavigationIconContentColor != other.lightNavigationIconContentColor) return false
        if (darkNavigationIconContentColor != other.darkNavigationIconContentColor) return false
        if (lightTitleContentColor != other.lightTitleContentColor) return false
        if (darkTitleContentColor != other.darkTitleContentColor) return false
        if (lightActionIconContentColor != other.lightActionIconContentColor) return false
        if (darkActionIconContentColor != other.darkActionIconContentColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = lightContainerColor.hashCode()
        result = 31 * result + darkContainerColor.hashCode()
        result = 31 * result + lightNavigationIconContentColor.hashCode()
        result = 31 * result + darkNavigationIconContentColor.hashCode()
        result = 31 * result + lightTitleContentColor.hashCode()
        result = 31 * result + darkTitleContentColor.hashCode()
        result = 31 * result + lightActionIconContentColor.hashCode()
        result = 31 * result + darkActionIconContentColor.hashCode()

        return result
    }
}

internal val LocalLiquidTopAppBarInsets = compositionLocalOf<WindowInsets?> { null }

@ExperimentalCupertinoApi
@Composable
private fun InlineTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    windowInsets: WindowInsets,
    colors: CupertinoLiquidTopAppBarColors,
    isCenterAligned: Boolean,
    backdrop: LayerBackdrop,
    isBackgroundAdaptive: Boolean = true,
    isTransparent: Boolean,
    isTranslucent: Boolean,
) {
    val lightContainerColor by colors.containerColor(isDark = false)
    val lightNavigationIconContentColor by colors.navigationIconContentColor(isDark = false)
    val lightTitleContentColor by colors.titleContentColor(isDark = false)
    val lightActionIconContentColor by colors.actionIconContentColor(isDark = false)
    val darkContainerColor by colors.containerColor(isDark = true)
    val darkNavigationIconContentColor by colors.navigationIconContentColor(isDark = true)
    val darkTitleContentColor by colors.titleContentColor(isDark = true)
    val darkActionIconContentColor by colors.actionIconContentColor(isDark = true)

    val isLightTheme = !isSystemInDarkTheme()

    val containerColorAnimation = remember { ColorAnimatable(if (isLightTheme) lightContainerColor else darkContainerColor) }
    val navigationIconContentColorAnimation = remember { ColorAnimatable(if (isLightTheme) lightNavigationIconContentColor else darkNavigationIconContentColor) }
    val titleContentColorAnimation = remember { ColorAnimatable(if (isLightTheme) lightTitleContentColor else darkTitleContentColor) }
    val actionIconContentColorAnimation = remember { ColorAnimatable(if (isLightTheme) lightActionIconContentColor else darkActionIconContentColor) }

    val topBarColor = cupertinoTranslucentLiquidTopBarColor(
        color = containerColorAnimation.value,
        isTranslucent = isTranslucent,
        isTransparent = isTransparent,
    )

    val navTitleVisible by LocalNavigationTitleVisible.current

    val animatedAlpha by animateFloatAsState(
        targetValue = if (isTransparent || topBarColor.alpha == 0f) 0f else 1f,
        label = "gradientAlpha",
        animationSpec = tween(300)
    )
    var remainContainerColor by remember { mutableStateOf(topBarColor) }

    var height by remember { mutableStateOf(0f) }

    LaunchedEffect(topBarColor) {
        if (topBarColor.alpha != 0f) {
            remainContainerColor = topBarColor
        }
    }

    if (isBackgroundAdaptive) {
        LaunchedEffect(backdrop.graphicsLayer) {
            while (isActive) {
                val averageLuminance = backdrop.graphicsLayer.toImageBitmap().averageLuminance(sampleWidth = 5, cropHeight = height.toInt())

                launch {
                    println("TopBar: lum $averageLuminance $height")
                    containerColorAnimation.animateTo(
                        if (averageLuminance > 0.5f) lightContainerColor else darkContainerColor,
                        tween(300)
                    )
                    titleContentColorAnimation.animateTo(
                        if (averageLuminance > 0.5f) lightTitleContentColor else darkTitleContentColor,
                        tween(300)
                    )
                    navigationIconContentColorAnimation.animateTo(
                        if (averageLuminance > 0.5f) lightNavigationIconContentColor else darkNavigationIconContentColor,
                        tween(300)
                    )
                    actionIconContentColorAnimation.animateTo(
                        if (averageLuminance > 0.5f) lightActionIconContentColor else darkActionIconContentColor,
                        tween(300)
                    )
                }

                delay(300)
            }
        }
    }



    Box(
        modifier = Modifier.height(IntrinsicSize.Min)
    ) {
        TopAppBarLayout(
            modifier =
                modifier
                    .onGloballyPositioned {
                        height = it.size.height.toFloat()
                    }
                    .drawWithContent {
                        drawRect(
                            brush = Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0.5f to remainContainerColor.copy(alpha = 0.5f),
                                    1f to Color.Transparent
                                )
                            ),
                            alpha = animatedAlpha,
                            topLeft = Offset.Zero,
                            size = size
                        )
                        drawContent()
                    }
                    .windowInsetsPadding(windowInsets),
            heightPx = LocalDensity.current.run { LiquidTopAppBarHeight.toPx() },
            navigationIconContentColor = navigationIconContentColorAnimation.value,
            titleContentColor = titleContentColorAnimation.value,
            actionIconContentColor = actionIconContentColorAnimation.value,
            title = {
                AnimatedVisibility(
                    visible = !navTitleVisible,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    title()
                }
            },
            titleTextStyle = CupertinoTheme.typography.headline,
            titleAlpha = 1f,
            titleVerticalArrangement = Arrangement.Center,
            titleHorizontalArrangement = if (isCenterAligned) Arrangement.Center else Arrangement.Start,
            titleBottomPadding = LocalDensity.current.run { 16.dp.roundToPx() },
            hideTitleSemantics = false,
            navigationIcon = navigationIcon,
            actions = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    content = actions
                )
            },
        )
    }
}

@Composable
private fun TopAppBarLayout(
    modifier: Modifier,
    heightPx: Float,
    navigationIconContentColor: Color,
    titleContentColor: Color,
    actionIconContentColor: Color,
    title: @Composable () -> Unit,
    titleTextStyle: TextStyle,
    titleAlpha: Float,
    titleVerticalArrangement: Arrangement.Vertical,
    titleHorizontalArrangement: Arrangement.Horizontal,
    titleBottomPadding: Int,
    hideTitleSemantics: Boolean,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable () -> Unit,
) {
    Layout(
        {
            Box(
                Modifier
                    .layoutId("navigationIcon"),
//                    .padding(start = TopAppBarHorizontalPadding)
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides navigationIconContentColor,
                    content = navigationIcon,
                )
            }
            Box(
                modifier =
                    Modifier
                        .layoutId("title")
                        .padding(horizontal = LiquidTopAppBarHorizontalPadding)
                        .graphicsLayer { alpha = titleAlpha }
                        .then(if (hideTitleSemantics) Modifier.clearAndSetSemantics { } else Modifier),
            ) {
                ProvideTextStyle(value = titleTextStyle) {
                    CompositionLocalProvider(
                        LocalContentColor provides titleContentColor,
                        content = title,
                    )
                }
            }
            Box(
                modifier =
                    Modifier
                        .layoutId("actionIcons")
                        .padding(end = LiquidTopAppBarHorizontalPadding),
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides actionIconContentColor,
                    content = actions,
                )
            }
        },
        modifier = modifier,
    ) { measurables, constraints ->
        val navigationIconPlaceable =
            measurables
                .first { it.layoutId == "navigationIcon" }
                .measure(constraints.copy(minWidth = 0))
        val actionIconsPlaceable =
            measurables
                .first { it.layoutId == "actionIcons" }
                .measure(constraints.copy(minWidth = 0))

        val maxTitleWidth =
            if (constraints.maxWidth == Constraints.Infinity) {
                constraints.maxWidth
            } else {
                (constraints.maxWidth - navigationIconPlaceable.width - actionIconsPlaceable.width)
                    .coerceAtLeast(0)
            }

        val layoutHeight = heightPx.roundToInt()

        val titlePlaceable =
            measurables
                .first { it.layoutId == "title" }
                .measure(constraints.copy(minWidth = 0, maxWidth = maxTitleWidth))

        // Locate the title's baseline.
        val titleBaseline =
            if (titlePlaceable[LastBaseline] != AlignmentLine.Unspecified) {
                titlePlaceable[LastBaseline]
            } else {
                0
            }

        layout(constraints.maxWidth, layoutHeight) {
            // Navigation icon
            navigationIconPlaceable.placeRelative(
                x = 0,
                y = (layoutHeight - navigationIconPlaceable.height) / 2,
            )

            // Title
            titlePlaceable.placeRelative(
                x =
                    when (titleHorizontalArrangement) {
                        Arrangement.Center -> (constraints.maxWidth - titlePlaceable.width) / 2
                        Arrangement.End ->
                            constraints.maxWidth - titlePlaceable.width - actionIconsPlaceable.width
                        // Arrangement.Start.
                        // An TopAppBarTitleInset will make sure the title is offset in case the
                        // navigation icon is missing.
                        else -> max(
                            LiquidTopAppBarTitleInset.roundToPx(),
                            navigationIconPlaceable.width
                        )
                    },
                y =
                    when (titleVerticalArrangement) {
                        Arrangement.Center -> (layoutHeight - titlePlaceable.height) / 2
                        // Apply bottom padding from the title's baseline only when the Arrangement is
                        // "Bottom".
                        Arrangement.Bottom ->
                            if (titleBottomPadding == 0) {
                                layoutHeight - titlePlaceable.height
                            } else {
                                layoutHeight - titlePlaceable.height -
                                        max(
                                            0,
                                            titleBottomPadding - titlePlaceable.height + titleBaseline,
                                        )
                            }
                        // Arrangement.Top
                        else -> 0
                    },
            )

            // Action icons
            actionIconsPlaceable.placeRelative(
                x = constraints.maxWidth - actionIconsPlaceable.width,
                y = (layoutHeight - actionIconsPlaceable.height) / 2,
            )
        }
    }
}

// internal val TopTitleAlphaEasing = CubicBezierEasing(.8f, 0f, .8f, .15f)

private val LiquidTopAppBarHorizontalPadding = 16.dp
private val LiquidTopAppBarHeight = 44.dp

// A title inset when the App-Bar is a Medium or Large one. Also used to size a spacer when the
// navigation icon is missing.
private val LiquidTopAppBarTitleInset = 16.dp - LiquidTopAppBarHorizontalPadding

@Immutable
object CupertinoLiquidTopAppBarDefaults {
    /**
     * Default insets to be used and consumed by the top app bars
     */
    val windowInsets: WindowInsets @Composable get() = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)

    @Composable
    fun divider() {
        CupertinoHorizontalDivider()
    }

    /**
     * Creates a [CupertinoLiquidTopAppBarColors] . The default implementation
     * animates between the provided colors according to the Material Design specification.
     *
     * Note: top app bar itself does not produce cupertino thin material glass effect.
     * This effect works only inside [CupertinoScaffold], [CupertinoBottomSheetScaffold], [CupertinoBottomSheetContent].
     * To achieve this effect with custom top app bar use [cupertinoTranslucentTopBarColor]
     * function that will communicate with scaffold and return either
     * [Color.Transparent] if color was successfully applied to scaffold (and top bar itself
     * should be transparent) or passed color if scaffold wasn't found.
     *
     * todo
     * @param containerColor the container color
     * @param scrolledContainerColor the container color when content is scrolled behind it
     * @param navigationIconContentColor the content color used for the navigation icon
     * @param titleContentColor the content color used for the title
     * @param actionIconContentColor the content color used for actions
     * @return the resulting [CupertinoLiquidTopAppBarColors] used for the top app bar
     */
    @Composable
    @ReadOnlyComposable
    fun topAppBarColors(
        lightContainerColor: Color = lightColorScheme().systemBackground,
        darkContainerColor: Color = darkColorScheme().systemBackground,
        lightNavigationIconContentColor: Color = lightColorScheme().accent,
        darkNavigationIconContentColor: Color = darkColorScheme().accent,
        lightTitleContentColor: Color = lightColorScheme().label,
        darkTitleContentColor: Color = darkColorScheme().label,
        lightActionIconContentColor: Color = lightColorScheme().accent,
        darkActionIconContentColor: Color = darkColorScheme().accent,
    ): CupertinoLiquidTopAppBarColors =
        CupertinoLiquidTopAppBarColors(
            lightContainerColor,
            darkContainerColor,
            lightNavigationIconContentColor,
            darkNavigationIconContentColor,
            lightTitleContentColor,
            darkTitleContentColor,
            lightActionIconContentColor,
            darkActionIconContentColor,
        )
}
