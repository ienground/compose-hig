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
 * Creates an adaptive component that chooses between Material and Cupertino implementations based on the current theme.
 *
 * This function provides a way to define platform-specific implementations for a composable component.
 * The implementation that is used depends on the current theme set by [AdaptiveTheme].
 *
 * @param material The Material implementation of the component. This is used when the current theme is [Theme.Material3].
 * @param cupertino The Cupertino implementation of the component. This is used when the current theme is [Theme.Cupertino].
 * @return The result of the selected implementation based on the current theme.
 */
@Composable
@ExperimentalAdaptiveApi
fun <T> adaptiveComponent(
    material: @Composable () -> T,
    cupertino: @Composable () -> T,
): T {
    return when (LocalTheme.current) {
        Theme.Cupertino -> cupertino()
        else -> material()
    }
}

/**
 * Creates an adaptive component using the provided [adaptation] to customize both Material and Cupertino implementations.
 *
 * This function allows for more complex adaptations where the Material and Cupertino implementations need different
 * customization parameters. It uses the provided [adaptation] to configure both implementations.
 *
 * @param adaptation The adaptation object that defines how to customize both Material and Cupertino implementations.
 * @param material The Material implementation of the component. This is used when the current theme is [Theme.Material3].
 * @param cupertino The Cupertino implementation of the component. This is used when the current theme is [Theme.Cupertino].
 * @param adaptationScope The scope for customizing both Material and Cupertino implementations using [AdaptationScope].
 * @return The result of the selected implementation based on the current theme.
 */
@Composable
@ExperimentalAdaptiveApi
fun <C, M, T> adaptiveComponent(
    adaptation: Adaptation<C, M>,
    material: @Composable (M) -> T,
    cupertino: @Composable (C) -> T,
    adaptationScope: AdaptationScope<C, M>.() -> Unit,
): T {
    adaptation.adaptationScope()

    return when (LocalTheme.current) {
        Theme.Cupertino -> cupertino(adaptation.rememberUpdatedCupertinoAdaptation())
        else -> material(adaptation.rememberUpdatedMaterialAdaptation())
    }
}