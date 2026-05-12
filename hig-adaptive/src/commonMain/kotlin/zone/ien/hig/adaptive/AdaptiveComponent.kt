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
 * [adaptiveComponent]는 현재 테마에 따라 적절한 컴포넌트를 렌더링하는 함수입니다.
 *
 * 이 함수는 Material Design과 Cupertino 테마를 지원하며, 현재 테마에 따라 적절한 컴포넌트를 렌더링합니다.
 *
 * @param material Material Design 테마에 사용될 컴포저블 블록입니다.
 * @param cupertino Cupertino 테마에 사용될 컴포저블 블록입니다.
 * @return 현재 테마에 따라 선택된 컴포넌트를 반환합니다.
 * @see LocalTheme
 * @see Theme
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
 * [adaptiveComponent]는 적응형 컴포넌트를 지원하는 함수로, 특정 어댑테이션 설정을 기반으로
 * Material Design과 Cupertino 테마에 따라 적절한 컴포넌트를 반환합니다.
 *
 * @param adaptation 적용할 어댑테이션 설정입니다.
 * @param material Material Design 테마에 사용될 컴포저블 블록입니다.
 * @param cupertino Cupertino 테마에 사용될 컴포저블 블록입니다.
 * @param adaptationScope 어댑테이션 설정을 위한 확장 함수입니다.
 * @return 현재 테마에 따라 선택된 컴포넌트를 반환합니다.
 * @see Adaptation
 * @see AdaptationScope
 * @see LocalTheme
 * @see Theme
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