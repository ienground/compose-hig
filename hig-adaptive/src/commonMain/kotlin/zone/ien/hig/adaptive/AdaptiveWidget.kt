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
 * 기본 적응형 위젯으로, 주어진 내용을 현재 테마에 따라 Material 또는 Cupertino 스타일로 표시합니다.
 * 
 * @param material Material 스타일로 표시할 내용
 * @param cupertino Cupertino 스타일로 표시할 내용
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
 * 다양한 적응형 컴포넌트를 처리하는 고급 적응형 위젯입니다.
 * 
 * @param adaptation [Adaptation] - 어댑테이션 정의
 * @param material Material 스타일로 표시할 내용
 * @param cupertino Cupertino 스타일로 표시할 내용
 * @param adaptationScope 어댑테이션에 대한 사용자 정의 설정 함수
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
