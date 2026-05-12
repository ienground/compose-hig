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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import zone.ien.hig.CupertinoScaffold
import zone.ien.hig.CupertinoScaffoldDefaults
import zone.ien.hig.ExperimentalCupertinoApi
import zone.ien.hig.FabPosition


/**
 * 적응형 [Scaffold] 컴포넌트로, Material Design과 Cupertino 테마에 따라 다른 레이아웃을 제공합니다.
 *
 * [AdaptiveScaffold]는 Material Design의 [Scaffold] 컴포넌트와 Cupertino의 [CupertinoScaffold] 컴포넌트를 사용하여
 * 운영체제별로 적절한 레이아웃을 제공합니다.
 *
 * @param modifier 요소에 적용할 Modifier
 * @param topBar 상단 탑바 컴포넌트
 * @param bottomBar 하단 바 컴포넌트
 * @param snackbarHost 스낵바 호스트 컴포넌트
 * @param floatingActionButton 플로팅 액션 버튼 컴포넌트
 * @param floatingActionButtonPosition 플로팅 액션 버튼 위치
 * @param contentWindowInsets 내용 패딩 설정
 * @param adaptation [ScaffoldAdaptation]에 대한 사용자 정의 설정 함수
 * @param content 컨텐츠 영역 컴포넌트
 * @see AdaptiveWidget
 * @see CupertinoScaffold
 * @see Scaffold
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
                contentWindowInsets = contentWindowInsets,
                content = content
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
                contentWindowInsets = contentWindowInsets,
                content = content
            )
        }
    )
}

/**
 * 적응형 [Scaffold] 컴포넌트로, Material Design과 Cupertino 테마에 따라 다른 레이아웃을 제공합니다.
 *
 * [AdaptiveScaffold]는 Material Design의 [Scaffold] 컴포넌트와 Cupertino의 [CupertinoScaffold] 컴포넌트를 사용하여
 * 운영체제별로 적절한 레이아웃을 제공합니다.
 *
 * @param modifier 요소에 적용할 Modifier
 * @param topBar 상단 탑바 컴포넌트
 * @param bottomBar 하단 바 컴포넌트
 * @param snackbarHost 스낵바 호스트 컴포넌트
 * @param floatingActionButton 플로팅 액션 버튼 컴포넌트
 * @param floatingActionButtonPosition 플로팅 액션 버튼 위치
 * @param containerColor 스크린 컨테이너의 배경 색상
 * @param contentColor 스크린 내용의 색상
 * @param contentWindowInsets 내용 패딩 설정
 * @param content 컨텐츠 영역 컴포넌트
 * @see AdaptiveWidget
 * @see CupertinoScaffold
 * @see Scaffold
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
                contentWindowInsets = contentWindowInsets,
                content = content
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
                contentWindowInsets = contentWindowInsets,
                content = content
            )
        }
    )
}

@Stable
/**
 * [Scaffold] 컴포넌트에 대한 적응형 어댑테이션 클래스로, Scaffold 스타일의 다양한 속성을 관리합니다.
 *
 * @param contentColor 스크린 내용의 색상
 * @param containerColor 스크린 컨테이너의 배경 색상
 * @see Color
 */
class ScaffoldAdaptation internal constructor(
    contentColor: Color,
    containerColor: Color
) {
    var contentColor by mutableStateOf(contentColor)
    var containerColor by mutableStateOf(containerColor)
}
@OptIn(ExperimentalAdaptiveApi::class)
@Stable
/**
 * [Scaffold] 컴포넌트에 대한 적응형 어댑테이션을 구현하는 클래스입니다.
 *
 * @see Adaptation
 * @see ScaffoldAdaptation
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