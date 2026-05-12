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

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import zone.ien.hig.utils.rememberDefaultBackdrop
import zone.ien.hig.CupertinoNavigationBar
import zone.ien.hig.CupertinoNavigationBarColors
import zone.ien.hig.CupertinoNavigationBarDefaults
import zone.ien.hig.CupertinoNavigationBarItem
import zone.ien.hig.CupertinoNavigationBarItemData
import zone.ien.hig.CupertinoNavigationBarNative
import zone.ien.hig.ExperimentalCupertinoApi

/**
 * Data class that holds the navigation bar state.
 *
 * This class contains the current selected tab index and the callback to update it.
 *
 * @param selectedTabIndex lambda that returns the currently selected tab index
 * @param onTabSelected callback to be invoked when a tab is selected
 */
internal data class NavigationBarState(
    val selectedTabIndex: () -> Int,
    val onTabSelected: (Int) -> Unit,
)
internal val LocalNavigationBarState = compositionLocalOf<NavigationBarState?> { null }

/**
 * An adaptive navigation bar that adapts between Cupertino and Material design based on the platform.
 *
 * This composable provides a navigation bar that automatically switches between Cupertino (iOS) and Material (Android)
 * design patterns based on the target platform. It supports tab navigation with appropriate styling for each platform.
 *
 * @param modifier optional [Modifier] for customizing the appearance and behavior
 * @param selectedTabIndex lambda that returns the currently selected tab index
 * @param onTabSelected callback to be invoked when a tab is selected
 * @param tabsCount the total number of tabs
 * @param adaptation lambda for customizing the adaptation behavior
 * @param content composable content of the navigation bar items
 */
@OptIn(ExperimentalCupertinoApi::class)
@ExperimentalAdaptiveApi
@Composable
fun AdaptiveNavigationBar(
    modifier: Modifier = Modifier,
    selectedTabIndex: () -> Int,
    onTabSelected: (index: Int) -> Unit,
    tabsCount: Int,
    adaptation: AdaptationScope<CupertinoNavigationBarAdaptation, MaterialNavigationBarAdaptation>.() -> Unit = {},
    content: @Composable RowScope.() -> Unit
) {
    CompositionLocalProvider(
        LocalNavigationBarState provides NavigationBarState(
            selectedTabIndex = selectedTabIndex,
            onTabSelected = onTabSelected,
        )
    ) {
        AdaptiveWidget(
            adaptation = remember {
                NavigationBarAdaptation()
            },
            adaptationScope = adaptation,
            cupertino = {
                CupertinoNavigationBar(
                    modifier = modifier,
                    colors = it.colors,
                    windowInsets = it.windowInsets,
                    backdrop = it.backdrop,
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = onTabSelected,
                    tabsCount = tabsCount,
                    content = content
                )
            },
            material = {
                NavigationBar(
                    modifier = modifier,
                    containerColor = it.containerColor,
                    contentColor = it.contentColor,
                    tonalElevation = it.tonalElevation,
                    windowInsets = it.windowInsets,
                    content = content
                )
            }
        )
    }
}

@OptIn(ExperimentalAdaptiveApi::class, ExperimentalCupertinoApi::class)
@Composable
fun AdaptiveNavigationBarNative(
    modifier: Modifier = Modifier,
    selectedTabIndex: () -> Int,
    onTabSelected: (index: Int) -> Unit,
    adaptation: AdaptationScope<CupertinoNavigationBarAdaptation, MaterialNavigationBarAdaptation>.() -> Unit = {},
    items: List<CupertinoNavigationBarItemData>
) {
    CompositionLocalProvider(
        LocalNavigationBarState provides NavigationBarState(
            selectedTabIndex = selectedTabIndex,
            onTabSelected = onTabSelected,
        )
    ) {
        AdaptiveWidget(
            adaptation = remember {
                NavigationBarAdaptation()
            },
            adaptationScope = adaptation,
            cupertino = {
                CupertinoNavigationBarNative(
                    modifier = modifier,
                    colors = it.colors,
                    windowInsets = it.windowInsets,
                    backdrop = it.backdrop,
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = onTabSelected,
                    items = items
                )
            },
            material = {
                NavigationBar(
                    modifier = modifier,
                    containerColor = it.containerColor,
                    contentColor = it.contentColor,
                    tonalElevation = it.tonalElevation,
                    windowInsets = it.windowInsets,
                    content = {
                        items.forEachIndexed { index, item ->
                            val selected = index == selectedTabIndex()
                            NavigationBarItem(
                                selected = selected,
                                onClick = item.onClick,
                                icon = {
                                    Icon(
                                        painter =
                                            if (selected && item.selectedIcon != null) item.selectedIcon!!
                                            else item.icon
                                        ,
                                        contentDescription = item.label,
                                    )
                                },
                                label = { Text(text = item.label) },
                            )
                        }
                    }
                )
            }
        )
    }
}

/**
 * An adaptive navigation bar item that adapts between Cupertino and Material design based on the platform.
 *
 * This composable provides a navigation bar item that automatically switches between Cupertino (iOS) and Material (Android)
 * design patterns based on the target platform. It's designed to be used within an [AdaptiveNavigationBar].
 *
 * @param index the index of this item in the navigation bar
 * @param onClick callback to be invoked when the item is clicked
 * @param icon composable that draws the icon for the item
 * @param modifier optional [Modifier] for customizing the appearance and behavior
 * @param enabled whether the item is enabled
 * @param label composable that draws the label for the item
 * @param interactionSource the interaction source for the item
 * @param adaptation lambda for customizing the adaptation behavior
 */
@OptIn(ExperimentalCupertinoApi::class, ExperimentalAdaptiveApi::class)
@Composable
fun RowScope.AdaptiveNavigationBarItem(
    index: Int,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    adaptation: AdaptationScope<CupertinoNavigationBarItemAdaptation, MaterialNavigationBarItemAdaptation>.() -> Unit = {},
) {
    val navState = LocalNavigationBarState.current
    val selected = navState?.let { it.selectedTabIndex() == index } ?: false
    val resolvedOnClick: () -> Unit = {
        navState?.onTabSelected(index)
        onClick()
    }

    AdaptiveWidget(
        adaptation = remember {
            NavigationBarItemAdaptation()
        },
        adaptationScope = adaptation,
        cupertino = {
            CupertinoNavigationBarItem(
                onClick = resolvedOnClick,
                icon = icon,
                modifier = modifier,
                enabled = enabled,
                label = label,
                interactionSource = interactionSource,
            )
        },
        material = {
            NavigationBarItem(
                selected = selected,
                onClick = resolvedOnClick,
                icon = icon,
                modifier = modifier,
                enabled = enabled,
                label = label,
                alwaysShowLabel = it.alwaysShowLabel,
                colors = it.colors,
                interactionSource = interactionSource
            )
        }
    )
}

/**
 * Material navigation bar adaptation.
 *
 * Container class for Material navigation bar adaptation properties.
 *
 * @param containerColor color for the container
 * @param contentColor color for the content
 * @param tonalElevation elevation for the navigation bar
 * @param windowInsets window insets to be used for the navigation bar
 */
class MaterialNavigationBarAdaptation internal constructor(
    containerColor: Color,
    contentColor: Color,
    tonalElevation: Dp = NavigationBarDefaults.Elevation,
    windowInsets: WindowInsets,
) {
    var containerColor: Color by mutableStateOf(containerColor)
    var contentColor: Color by mutableStateOf(contentColor)
    var tonalElevation: Dp by mutableStateOf(tonalElevation)
    var windowInsets: WindowInsets by mutableStateOf(windowInsets)
}

/**
 * Cupertino navigation bar adaptation.
 *
 * Container class for Cupertino navigation bar adaptation properties.
 *
 * @param colors colors to be used for the navigation bar
 * @param windowInsets window insets to be used for the navigation bar
 * @param backdrop backdrop to use for the navigation bar
 */
@OptIn(ExperimentalCupertinoApi::class)
class CupertinoNavigationBarAdaptation internal constructor(
    colors: CupertinoNavigationBarColors,
    windowInsets: WindowInsets,
    backdrop: LayerBackdrop
) {
    var colors: CupertinoNavigationBarColors by mutableStateOf(colors)
    var windowInsets: WindowInsets by mutableStateOf(windowInsets)
    var backdrop: LayerBackdrop by mutableStateOf(backdrop)
}

@Stable
class MaterialNavigationBarItemAdaptation internal constructor(
    colors: NavigationBarItemColors,
    alwaysShowLabel: Boolean
) {
    var colors: NavigationBarItemColors by mutableStateOf(colors)
    var alwaysShowLabel by mutableStateOf(alwaysShowLabel)
}

@Stable
@OptIn(ExperimentalCupertinoApi::class)
class CupertinoNavigationBarItemAdaptation internal constructor()

@OptIn(ExperimentalAdaptiveApi::class)
@Stable
private class NavigationBarAdaptation: Adaptation<CupertinoNavigationBarAdaptation, MaterialNavigationBarAdaptation>() {
    @OptIn(ExperimentalCupertinoApi::class)
    @Composable
    override fun rememberCupertinoAdaptation(): CupertinoNavigationBarAdaptation {
        val colors = CupertinoNavigationBarDefaults.colors()
        val windowInsets = CupertinoNavigationBarDefaults.windowInsets
        val backdrop = rememberLayerBackdrop()

        return remember(colors, windowInsets, backdrop) {
            CupertinoNavigationBarAdaptation(
                colors = colors,
                windowInsets = windowInsets,
                backdrop = backdrop
            )
        }
    }

    @Composable
    override fun rememberMaterialAdaptation(): MaterialNavigationBarAdaptation {
        val containerColor = NavigationBarDefaults.containerColor
        val contentColor = contentColorFor(containerColor)
        val tonalElevation = NavigationBarDefaults.Elevation
        val windowInsets = NavigationBarDefaults.windowInsets

        return remember(containerColor, contentColor, tonalElevation, windowInsets) {
            MaterialNavigationBarAdaptation(
                containerColor = containerColor,
                contentColor = contentColor,
                tonalElevation = tonalElevation,
                windowInsets = windowInsets
            )
        }
    }
}

@OptIn(ExperimentalAdaptiveApi::class)
@Stable
private class NavigationBarItemAdaptation: Adaptation<CupertinoNavigationBarItemAdaptation, MaterialNavigationBarItemAdaptation>() {

    @OptIn(ExperimentalCupertinoApi::class)
    @Composable
    override fun rememberCupertinoAdaptation(): CupertinoNavigationBarItemAdaptation {
        return remember { CupertinoNavigationBarItemAdaptation() }
    }

    @Composable
    override fun rememberMaterialAdaptation(): MaterialNavigationBarItemAdaptation {
        val colors = NavigationBarItemDefaults.colors()
        val alwaysShowLabel = true

        return remember(colors, alwaysShowLabel) {
            MaterialNavigationBarItemAdaptation(
                colors = colors,
                alwaysShowLabel = alwaysShowLabel
            )
        }
    }
}