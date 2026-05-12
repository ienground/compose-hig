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

import androidx.compose.runtime.Composable

/**
 * A composable that adapts between Cupertino and Material design based on the current theme.
 *
 * This function provides a way to conditionally render either Cupertino or Material design composables
 * based on the current theme, allowing for a consistent UX across different platforms.
 *
 * @param material composable for the material design implementation
 * @param cupertino composable for the cupertino design implementation
 * @param content The content that will be rendered based on the current theme
 */
@Composable
@ExperimentalAdaptiveApi
fun AdaptiveWidget(
    material: @Composable () -> Unit,
    cupertino: @Composable () -> Unit,
) {
    when (LocalTheme.current) {
        Theme.Cupertino -> cupertino()
        else -> material()
    }
}

/**
 * A composable that adapts between Cupertino and Material design based on the current theme with adaptation support.
 *
 * This function provides a way to conditionally render either Cupertino or Material design composables
 * based on the current theme, with support for custom adaptation of design properties.
 *
 * @param adaptation the adaptation object to manage theme-specific values
 * @param material composable for the material design implementation with adaptation
 * @param cupertino composable for the cupertino design implementation with adaptation
 * @param adaptationScope lambda that allows customization of the adaptation behavior
 * @param content The content that will be rendered based on the current theme and adaptation
 */
@Composable
@ExperimentalAdaptiveApi
fun <C, M> AdaptiveWidget(
    adaptation: Adaptation<C, M>,
    material: @Composable (M) -> Unit,
    cupertino: @Composable (C) -> Unit,
    adaptationScope: AdaptationScope<C, M>.() -> Unit,
) {
    adaptation.adaptationScope()

    when (LocalTheme.current) {
        Theme.Cupertino -> cupertino(adaptation.rememberUpdatedCupertinoAdaptation())
        else -> material(adaptation.rememberUpdatedMaterialAdaptation())
    }
}
