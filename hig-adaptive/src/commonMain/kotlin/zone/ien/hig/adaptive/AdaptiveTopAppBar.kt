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
 * 적응형 상단 앱바로, 현재 테마에 따라 Material 또는 Cupertino 스타일로 표시됩니다.
 *
 * [AdaptiveTopAppBar]는 Material Design의 [TopAppBar] 또는 [CenterAlignedTopAppBar] 컴포넌트와
 * Cupertino의 [CupertinoTopAppBar] 컴포넌트를 사용하여 운영체제별로 적절한 상단 앱바를 제공합니다.
 *
 * @param title 상단 앱바에 표시할 제목
 * @param modifier 상단 앱바에 적용할 Modifier
 * @param navigationIcon 네비게이션 아이콘
 * @param actions 앱바에 표시할 액션 버튼들
 * @param windowInsets 윈도우 인셋 설정
 * @param adaptation [CupertinoTopAppBarAdaptation]와 [MaterialTopAppBarAdaptation]에 대한 사용자 정의 설정 함수
 * @see AdaptiveWidget
 * @see CupertinoTopAppBar
 * @see TopAppBar
 * @see CenterAlignedTopAppBar
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
 * 단일 행 상단 앱바를 표시하는 내부 함수로, 중심 정렬 여부에 따라 다른 상단 앱바를 선택적으로 사용합니다.
 *
 * @param title 상단 앱바에 표시할 제목
 * @param isCenterAligned 제목이 중앙 정렬되는지 여부
 * @param colors 상단 앱바의 색상 설정
 * @param modifier 상단 앱바에 적용할 Modifier
 * @param navigationIcon 네비게이션 아이콘
 * @param actions 앱바에 표시할 액션 버튼들
 * @param windowInsets 윈도우 인셋 설정
 * @param scrollBehavior 스크롤 행동 설정
 * @see TopAppBar
 * @see CenterAlignedTopAppBar
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

@Stable
@OptIn(ExperimentalMaterial3Api::class)
/**
 * Material 상단 앱바에 대한 적응형 어댑테이션 클래스로, 상단 앱바의 다양한 속성을 관리합니다.
 *
 * @param colors [TopAppBarColors] - 상단 앱바의 색상 설정
 * @param isCenterAligned 제목이 중앙 정렬되는지 여부
 * @param scrollBehavior 스크롤 행동 설정
 * @see TopAppBarColors
 * @see TopAppBarDefaults
 * @see TopAppBarScrollBehavior
 */
class MaterialTopAppBarAdaptation internal constructor(
    colors: TopAppBarColors,
    isCenterAligned: Boolean = false,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    var isCenterAligned: Boolean by mutableStateOf(isCenterAligned)
    var colors: TopAppBarColors by mutableStateOf(colors)
    var scrollBehavior: TopAppBarScrollBehavior? by mutableStateOf(scrollBehavior)
}

@Stable
/**
 * Cupertino 상단 앱바에 대한 적응형 어댑테이션 클래스로, 상단 앱바의 다양한 속성을 관리합니다.
 *
 * @param colors [CupertinoTopAppBarColors] - 상단 앱바의 색상 설정
 * @param backdrop [LayerBackdrop] - 상단 앱바의 레이어 배경
 * @see CupertinoTopAppBarColors
 * @see LayerBackdrop
 */
class CupertinoTopAppBarAdaptation internal constructor(
    colors: CupertinoTopAppBarColors,
    backdrop: LayerBackdrop
) {
    var colors: CupertinoTopAppBarColors by mutableStateOf(colors)
    var backdrop: LayerBackdrop by mutableStateOf(backdrop)
}

@OptIn(ExperimentalAdaptiveApi::class)
@Stable
/**
 * 상단 앱바 유형에 따라 적응형 어댑테이션을 제공하는 클래스입니다.
 *
 * @see Adaptation
 * @see CupertinoTopAppBarAdaptation
 * @see MaterialTopAppBarAdaptation
 */
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