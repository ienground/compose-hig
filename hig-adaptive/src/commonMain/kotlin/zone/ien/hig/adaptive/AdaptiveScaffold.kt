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



package zone.ien.hig.adaptive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.recalculateWindowInsets
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.movableContentOf
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import zone.ien.hig.LocalCupertinoRightSystemArea
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.unit.dp
import zone.ien.hig.CupertinoSurface
import zone.ien.hig.CupertinoScaffold
import zone.ien.hig.CupertinoScaffoldDefaults
import zone.ien.hig.ExperimentalCupertinoApi
import zone.ien.hig.FabPosition
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.utils.cupertinoSidebarMaterial
import zone.ien.hig.utils.rememberDefaultBackdrop
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop


/**
 * An adaptive scaffold that adapts between Cupertino and Material design based on the platform.
 *
 * This composable provides a scaffold that automatically switches between Cupertino (iOS) and Material (Android)
 * design patterns based on the target platform. The content of the scaffold adapts to the appropriate design
 * guidelines and styles.
 *
 * @param modifier optional [Modifier] for customizing the appearance and behavior
 * @param topBar composable for the top app bar
 * @param bottomBar composable for the bottom app bar
 * @param snackbarHost composable for the snackbar host
 * @param floatingActionButton composable for the floating action button
 * @param floatingActionButtonPosition determines the position of the floating action button
 * @param contentWindowInsets the window insets to be used for the content
 * @param adaptation lambda for customizing the adaptation behavior
 * @param content composable content of the scaffold
 */
@OptIn(ExperimentalCupertinoApi::class)
@ExperimentalAdaptiveApi
@Composable
fun AdaptiveScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    contentWindowInsets: WindowInsets = CupertinoScaffoldDefaults.contentWindowInsets,
    adaptation: AdaptationScope<ScaffoldAdaptation, ScaffoldAdaptation>.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val currentContent by rememberUpdatedState(content)
    val retainedContent = remember { movableContentOf<PaddingValues> { currentContent(it) } }
    val paneWindowInsets = contentWindowInsets.forCurrentPane()
    AdaptiveWidget(
        adaptation = remember {
            ScaffoldAdaptationImpl()
        },
        adaptationScope = adaptation,
        cupertino = {
            CupertinoScaffold(
                modifier = modifier,
                topBar = topBar,
                bottomBar = bottomBar,
                snackbarHost = snackbarHost,
                floatingActionButton = floatingActionButton,
                floatingActionButtonPosition = floatingActionButtonPosition,
                containerColor = it.containerColor,
                contentColor = it.contentColor,
                contentWindowInsets = paneWindowInsets,
                content = retainedContent
            )
        },
        material = {
            Scaffold(
                modifier = modifier,
                topBar = topBar,
                bottomBar = bottomBar,
                snackbarHost = snackbarHost,
                floatingActionButton = floatingActionButton,
                floatingActionButtonPosition = when(floatingActionButtonPosition) {
                    FabPosition.End -> androidx.compose.material3.FabPosition.End
                    else -> androidx.compose.material3.FabPosition.Center
                },
                containerColor = it.containerColor,
                contentColor = it.contentColor,
                contentWindowInsets = paneWindowInsets,
                content = retainedContent
            )
        }
    )
}

/**
 * 사용 가능한 너비가 넓을 때 앞쪽 사이드바를 선택적으로 표시하는 적응형 스캐폴드입니다.
 *
 * 가용 너비가 600 dp 이상이면 사이드바를 표시하고, 더 좁으면 기존 하단 막대를 표시합니다.
 * 기기 종류나 방향 대신 현재 창의 너비로 표시 방식을 결정합니다.
 * 넓은 화면에서는 [topBar]와 [content]를 상세 영역에 배치하고 각 영역의 위치에 맞게
 * 시스템 인셋을 계산합니다. [sidebarContent]에는 [contentWindowInsets]를 적용합니다.
 */
@OptIn(ExperimentalCupertinoApi::class)
@ExperimentalAdaptiveApi
@Composable
fun AdaptiveScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    contentWindowInsets: WindowInsets = CupertinoScaffoldDefaults.contentWindowInsets,
    sidebarContent: @Composable ColumnScope.() -> Unit,
    adaptation: AdaptationScope<ScaffoldAdaptation, ScaffoldAdaptation>.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val currentContent by rememberUpdatedState(content)
    val retainedContent = remember { movableContentOf<PaddingValues> { currentContent(it) } }
    val paneWindowInsets = contentWindowInsets.forCurrentPane()
    val fallbackBackdrop = rememberDefaultBackdrop()
    BoxWithConstraints(modifier = modifier) {
        if (maxWidth >= AdaptiveSidebarWidthBreakpoint) {
            Row(Modifier.fillMaxSize()) {
                AdaptiveWidget(
                    adaptation = remember { ScaffoldAdaptationImpl() },
                    adaptationScope = adaptation,
                    cupertino = { config ->
                        Box(
                            Modifier
                                .width(AdaptiveSidebarWidth)
                                .fillMaxHeight()
                                .recalculateWindowInsets(),
                        ) {
                            if (config.sidebarBackdrop == null) {
                                Box(
                                    Modifier.fillMaxSize().layerBackdrop(fallbackBackdrop)
                                        .background(CupertinoTheme.colorScheme.systemBackground),
                                )
                            }
                            CupertinoSurface(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .cupertinoSidebarMaterial(config.sidebarBackdrop ?: fallbackBackdrop),
                                color = Color.Transparent,
                                contentColor = CupertinoTheme.colorScheme.label,
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .windowInsetsPadding(paneWindowInsets)
                                        .padding(horizontal = AdaptiveSidebarHorizontalPadding),
                                    content = sidebarContent,
                                )
                            }
                        }
                    },
                    material = {
                        Surface(
                            modifier = Modifier
                                .width(AdaptiveSidebarWidth)
                                .fillMaxHeight()
                                .recalculateWindowInsets(),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .windowInsetsPadding(paneWindowInsets)
                                    .padding(horizontal = AdaptiveSidebarHorizontalPadding),
                                content = sidebarContent,
                            )
                        }
                    },
                )
                AdaptiveWidget(
                    cupertino = {
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(CupertinoTheme.colorScheme.separator),
                        )
                    },
                    material = {
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.outlineVariant),
                        )
                    },
                )
                AdaptiveScaffold(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .recalculateWindowInsets(),
                    topBar = topBar,
                    bottomBar = {},
                    snackbarHost = snackbarHost,
                    floatingActionButton = floatingActionButton,
                    floatingActionButtonPosition = floatingActionButtonPosition,
                    contentWindowInsets = paneWindowInsets,
                    adaptation = adaptation,
                    content = retainedContent,
                )
            }
        } else {
            AdaptiveScaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = topBar,
                bottomBar = bottomBar,
                snackbarHost = snackbarHost,
                floatingActionButton = floatingActionButton,
                floatingActionButtonPosition = floatingActionButtonPosition,
                contentWindowInsets = paneWindowInsets,
                adaptation = adaptation,
                content = retainedContent,
            )
        }
    }
}

/**
 * An adaptive scaffold that adapts between Cupertino and Material design based on the platform.
 *
 * This composable provides a scaffold that automatically switches between Cupertino (iOS) and Material (Android)
 * design patterns based on the target platform. The content of the scaffold adapts to the appropriate design
 * guidelines and styles.
 *
 * @param modifier optional [Modifier] for customizing the appearance and behavior
 * @param topBar composable for the top app bar
 * @param bottomBar composable for the bottom app bar
 * @param snackbarHost composable for the snackbar host
 * @param floatingActionButton composable for the floating action button
 * @param floatingActionButtonPosition determines the position of the floating action button
 * @param containerColor color for the container
 * @param contentColor color for the content
 * @param contentWindowInsets the window insets to be used for the content
 * @param content composable content of the scaffold
 */
@OptIn(ExperimentalCupertinoApi::class)
@ExperimentalAdaptiveApi
@Composable
fun AdaptiveScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    contentWindowInsets: WindowInsets = CupertinoScaffoldDefaults.contentWindowInsets,
    content: @Composable (PaddingValues) -> Unit
) {
    val currentContent by rememberUpdatedState(content)
    val retainedContent = remember { movableContentOf<PaddingValues> { currentContent(it) } }
    val paneWindowInsets = contentWindowInsets.forCurrentPane()
    AdaptiveWidget(

        cupertino = {
            CupertinoScaffold(
                modifier = modifier,
                topBar = topBar,
                bottomBar = bottomBar,
                snackbarHost = snackbarHost,
                floatingActionButton = floatingActionButton,
                floatingActionButtonPosition = floatingActionButtonPosition,
                containerColor = containerColor.takeOrElse {
                    CupertinoScaffoldDefaults.containerColor
                },
                contentColor = contentColor.takeOrElse {
                    CupertinoScaffoldDefaults.contentColor
                },
                contentWindowInsets = paneWindowInsets,
                content = retainedContent
            )
        },
        material = {
            Scaffold(
                modifier = modifier,
                topBar = topBar,
                bottomBar = bottomBar,
                snackbarHost = snackbarHost,
                floatingActionButton = floatingActionButton,
                floatingActionButtonPosition = when (floatingActionButtonPosition) {
                    FabPosition.End -> androidx.compose.material3.FabPosition.End
                    else -> androidx.compose.material3.FabPosition.Center
                },
                containerColor = containerColor.takeOrElse {
                    MaterialTheme.colorScheme.background
                },
                contentColor = contentColor.takeOrElse {
                    MaterialTheme.colorScheme.onBackground
                },
                contentWindowInsets = paneWindowInsets,
                content = retainedContent
            )
        }
    )
}

@Stable
/**
 * [Scaffold] component adaptive adaptation class that manages various Scaffold style properties.
 *
 * @param contentColor Color for the screen content
 * @param containerColor Background color for the screen container
 * @see Color
 */
class ScaffoldAdaptation internal constructor(
    contentColor: Color,
    containerColor: Color
) {
    var contentColor by mutableStateOf(contentColor)
    var containerColor by mutableStateOf(containerColor)
    var sidebarBackdrop: LayerBackdrop? by mutableStateOf(null)
}

/**
 * Implementation of [Adaptation] for [ScaffoldAdaptation].
 *
 * This class manages the adaptation between Cupertino and Material design for scaffolds,
 * providing appropriate container and content colors for both design systems.
 */
@OptIn(ExperimentalAdaptiveApi::class)
@Stable
/**
 * Implementation of [Adaptation] for [ScaffoldAdaptation].
 *
 * This class manages the adaptation between Cupertino and Material design for scaffolds,
 * providing appropriate container and content colors for both design systems.
 */
private class ScaffoldAdaptationImpl :
    Adaptation<ScaffoldAdaptation, ScaffoldAdaptation>() {

    @Composable
    override fun rememberCupertinoAdaptation(): ScaffoldAdaptation {
        val contentColor = CupertinoScaffoldDefaults.contentColor
        val containerColor = CupertinoScaffoldDefaults.containerColor

        return remember(contentColor, containerColor) {
            ScaffoldAdaptation(
                contentColor = contentColor,
                containerColor = containerColor
            )
        }
    }

    @Composable
    override fun rememberMaterialAdaptation(): ScaffoldAdaptation {
        val containerColor = MaterialTheme.colorScheme.background
        val contentColor = contentColorFor(containerColor)

        return remember(contentColor, containerColor) {
            ScaffoldAdaptation(
                contentColor = contentColor,
                containerColor = containerColor
            )
        }
    }
}

private val AdaptiveSidebarWidth = 256.dp
private val AdaptiveSidebarHorizontalPadding = 12.dp
private val AdaptiveSidebarWidthBreakpoint = 600.dp

@Composable
private fun WindowInsets.forCurrentPane(): WindowInsets =
    if (LocalCupertinoRightSystemArea.current) this
    else only(WindowInsetsSides.Left + WindowInsetsSides.Vertical)

/**
 * 중간 너비부터 두 콘텐츠를 나란히 배치하고 좁은 창에서는 세로로 배치합니다.
 * 동일한 콘텐츠 슬롯을 유지하므로 너비가 바뀌어도 내부 remember 상태가 유지됩니다.
 * [firstPaneFraction]은 가로 배치에서 첫 콘텐츠가 차지할 비율입니다.
 * 높이가 제한된 세로 배치에서는 첫 콘텐츠를 절반 이내로 측정해 두 콘텐츠의 공간을 확보합니다.
 */
@ExperimentalAdaptiveApi
@Composable
fun AdaptivePaneLayout(
    modifier: Modifier = Modifier,
    firstPaneFraction: Float = 0.5f,
    minHorizontalWidth: Dp = AdaptiveSidebarWidthBreakpoint,
    first: @Composable () -> Unit,
    second: @Composable () -> Unit,
) {
    require(firstPaneFraction > 0f && firstPaneFraction < 1f)
    Layout(
        modifier = modifier,
        content = { Box { first() }; Box { second() } },
    ) { measurables, constraints ->
        val horizontal = constraints.hasBoundedWidth && constraints.maxWidth >= minHorizontalWidth.roundToPx()
        val firstWidth = if (horizontal) (constraints.maxWidth * firstPaneFraction).toInt() else constraints.maxWidth
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val firstPane = measurables[0].measure(loose.copy(
            maxWidth = firstWidth,
            maxHeight = if (!horizontal && constraints.hasBoundedHeight) constraints.maxHeight / 2 else constraints.maxHeight,
        ))
        val secondPane = measurables[1].measure(loose.copy(
            maxWidth = if (horizontal) constraints.maxWidth - firstWidth else constraints.maxWidth,
            maxHeight = if (horizontal || !constraints.hasBoundedHeight) constraints.maxHeight
                else (constraints.maxHeight - firstPane.height).coerceAtLeast(0),
        ))
        val width = constraints.constrainWidth(if (horizontal) constraints.maxWidth else maxOf(firstPane.width, secondPane.width))
        val height = constraints.constrainHeight(if (horizontal) maxOf(firstPane.height, secondPane.height) else firstPane.height + secondPane.height)
        layout(width, height) {
            firstPane.placeRelative(0, 0)
            secondPane.placeRelative(if (horizontal) firstWidth else 0, if (horizontal) 0 else firstPane.height)
        }
    }
}
