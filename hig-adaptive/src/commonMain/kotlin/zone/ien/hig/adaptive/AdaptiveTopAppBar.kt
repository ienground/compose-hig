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

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import zone.ien.hig.utils.rememberDefaultBackdrop
import zone.ien.hig.CupertinoTopAppBar
import zone.ien.hig.CupertinoTopAppBarColors
import zone.ien.hig.CupertinoTopAppBarDefaults
import zone.ien.hig.ExperimentalCupertinoApi

/**
 * An adaptive top app bar that adapts between Cupertino and Material design based on the platform.
 *
 * This composable provides a top app bar that automatically switches between Cupertino (iOS) and Material (Android)
 * design patterns based on the target platform. The content adapts to appropriate design guidelines and styles.
 *
 * @param title composable for the title
 * @param modifier optional [Modifier] for customizing the appearance and behavior
 * @param navigationIcon composable for the navigation icon
 * @param actions composable for the actions
 * @param windowInsets the window insets to be used for the content
 * @param adaptation lambda for customizing the adaptation behavior
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalCupertinoApi::class)
@ExperimentalAdaptiveApi
@Composable
fun AdaptiveTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable (RowScope.() -> Unit) = {},
    windowInsets: WindowInsets = CupertinoTopAppBarDefaults.windowInsets,
    adaptation: AdaptationScope<CupertinoTopAppBarAdaptation, MaterialTopAppBarAdaptation>.() -> Unit = {},
) {
    AdaptiveWidget(
        adaptation =
            remember {
                TopAppBarAdaptation()
            },
        adaptationScope = adaptation,
        cupertino = {
            CupertinoTopAppBar(
                title = title,
                modifier = modifier,
                navigationIcon = navigationIcon,
                actions = actions,
                windowInsets = windowInsets,
                colors = it.colors,
                backdrop = it.backdrop
            )
        },
        material = {
            SingleRowTopAppBar(
                title = title,
                isCenterAligned = it.isCenterAligned,
                colors = it.colors,
                modifier = modifier,
                navigationIcon = navigationIcon,
                actions = actions,
                windowInsets = windowInsets,
                scrollBehavior = it.scrollBehavior,
            )
        },
    )
}

/**
 * A single row top app bar.
 *
 * This private composable renders either a CenterAlignedTopAppBar or a TopAppBar based on the isCenterAligned parameter.
 *
 * @param title composable for the title
 * @param isCenterAligned whether the title should be centered
 * @param colors the colors to be used for the top app bar
 * @param modifier optional [Modifier] for customizing the appearance and behavior
 * @param navigationIcon composable for the navigation icon
 * @param actions composable for the actions
 * @param windowInsets the window insets to be used for the content
 * @param scrollBehavior the scroll behavior for the top app bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SingleRowTopAppBar(
    title: @Composable () -> Unit,
    isCenterAligned: Boolean,
    colors: TopAppBarColors,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable (RowScope.() -> Unit) = {},
    windowInsets: WindowInsets = CupertinoTopAppBarDefaults.windowInsets,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    if (isCenterAligned) {
        CenterAlignedTopAppBar(
            title = title,
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = actions,
            windowInsets = windowInsets,
            colors = colors,
            scrollBehavior = scrollBehavior,
        )
    } else {
        TopAppBar(
            title = title,
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = actions,
            windowInsets = windowInsets,
            colors = colors,
            scrollBehavior = scrollBehavior,
        )
    }
}

/**
 * Material top app bar adaptation.
 *
 * Container class for Material top app bar adaptation properties.
 *
 * @param colors the colors to be used for the top app bar
 * @param isCenterAligned whether the title should be centered
 * @param scrollBehavior the scroll behavior for the top app bar
 */
@Stable
@OptIn(ExperimentalMaterial3Api::class)
class MaterialTopAppBarAdaptation internal constructor(
    colors: TopAppBarColors,
    isCenterAligned: Boolean = false,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    var isCenterAligned: Boolean by mutableStateOf(isCenterAligned)
    var colors: TopAppBarColors by mutableStateOf(colors)
    var scrollBehavior: TopAppBarScrollBehavior? by mutableStateOf(scrollBehavior)
}

/**
 * Cupertino top app bar adaptation.
 *
 * Container class for Cupertino top app bar adaptation properties.
 *
 * @param colors the colors to be used for the top app bar
 * @param backdrop backdrop to use for the bar
 */
@Stable
class CupertinoTopAppBarAdaptation internal constructor(
    colors: CupertinoTopAppBarColors,
    backdrop: LayerBackdrop
) {
    var colors: CupertinoTopAppBarColors by mutableStateOf(colors)
    var backdrop: LayerBackdrop by mutableStateOf(backdrop)
}

/**
 * Top app bar adaptation.
 *
 * Implementation of [Adaptation] for top app bar adaptation.
 */
@OptIn(ExperimentalAdaptiveApi::class)
@Stable
private class TopAppBarAdaptation: Adaptation<CupertinoTopAppBarAdaptation, MaterialTopAppBarAdaptation>() {
    @Composable
    override fun rememberCupertinoAdaptation(): CupertinoTopAppBarAdaptation {
        val colors = CupertinoTopAppBarDefaults.topAppBarColors()
        val backdrop = rememberLayerBackdrop()

        return remember(colors, backdrop) {
            CupertinoTopAppBarAdaptation(
                colors = colors,
                backdrop = backdrop
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun rememberMaterialAdaptation(): MaterialTopAppBarAdaptation {
        val colors = TopAppBarDefaults.topAppBarColors()

        return remember(colors) {
            MaterialTopAppBarAdaptation(
                colors = colors,
            )
        }
    }
}
