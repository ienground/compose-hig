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

import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.by
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.var
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import zone.ien.hig.CupertinoDividerDefaults
import zone.ien.hig.CupertinoHorizontalDivider
import zone.ien.hig.CupertinoVerticalDivider

/**
 * 적응형 구분선 컴포넌트로, Material Design과 Cupertino 테마에 따라 다른 구분선 스타일을 제공합니다.
 *
 * 이 함수는 [AdaptiveHorizontalDivider] 함수로 대체되었습니다.
 *
 * @param modifier 요소에 적용할 Modifier
 * @param adaptation [DividerAdaptation]에 대한 사용자 정의 설정 함수
 * @see AdaptiveHorizontalDivider
 * @see HorizontalDivider
 * @see CupertinoHorizontalDivider
 */
@Deprecated(
    replaceWith = ReplaceWith(
        "AdaptiveHorizontalDivider(modifier,adaptation)",
        "import zone.ien.hig.adaptive.AdaptiveHorizontalDivider"
    ),
    message = "Use AdaptiveHorizontalDivider instead")
@Composable
@ExperimentalAdaptiveApi
fun AdaptiveDivider(
    modifier: Modifier = Modifier,
    adaptation: AdaptationScope<DividerAdaptation, DividerAdaptation>.() -> Unit = {}
) = AdaptiveHorizontalDivider(modifier, adaptation)


/**
 * 적응형 수평 구분선 컴포넌트로, Material Design과 Cupertino 테마에 따라 다른 구분선 스타일을 제공합니다.
 *
 * [AdaptiveHorizontalDivider]는 Material Design의 [HorizontalDivider] 컴포넌트와 Cupertino의 [CupertinoHorizontalDivider] 컴포넌트를 사용하여
 * 운영체제별로 적절한 수평 구분선을 제공합니다.
 *
 * @param modifier 요소에 적용할 Modifier
 * @param adaptation [DividerAdaptation]에 대한 사용자 정의 설정 함수
 * @see AdaptiveWidget
 * @see HorizontalDivider
 * @see CupertinoHorizontalDivider
 */
@Composable
@ExperimentalAdaptiveApi
fun AdaptiveHorizontalDivider(
    modifier: Modifier = Modifier,
    adaptation: AdaptationScope<DividerAdaptation, DividerAdaptation>.() -> Unit = {}
) {
    AdaptiveWidget(
        adaptation = remember {
            DividerAdaptationScope()
        },
        adaptationScope = adaptation,
        material = {
            HorizontalDivider(
                modifier = modifier,
                thickness = it.thickness,
                color = it.color
            )
        },
        cupertino = {
            CupertinoHorizontalDivider(
                modifier = modifier,
                thickness = it.thickness,
                color = it.color
            )
        }
    )
}

/**
 * 적응형 수직 구분선 컴포넌트로, Material Design과 Cupertino 테마에 따라 다른 구분선 스타일을 제공합니다.
 *
 * [AdaptiveVerticalDivider]는 Material Design의 [VerticalDivider] 컴포넌트와 Cupertino의 [CupertinoVerticalDivider] 컴포넌트를 사용하여
 * 운영체제별로 적절한 수직 구분선을 제공합니다.
 *
 * @param modifier 요소에 적용할 Modifier
 * @param adaptation [DividerAdaptation]에 대한 사용자 정의 설정 함수
 * @see AdaptiveWidget
 * @see VerticalDivider
 * @see CupertinoVerticalDivider
 */
@Composable
@ExperimentalAdaptiveApi
fun AdaptiveVerticalDivider(
    modifier: Modifier = Modifier,
    adaptation: AdaptationScope<DividerAdaptation, DividerAdaptation>.() -> Unit = {}
) {
    AdaptiveWidget(
        adaptation = remember {
            DividerAdaptationScope()
        },
        adaptationScope = adaptation,
        material = {
            VerticalDivider(
                modifier = modifier,
                thickness = it.thickness,
                color = it.color
            )
        },
        cupertino = {
            CupertinoVerticalDivider(
                modifier = modifier,
                thickness = it.thickness,
                color = it.color
            )
        }
    )
}

@ExperimentalAdaptiveApi
/**
 * 구분선 컴포넌트에 대한 적응형 어댑테이션을 구현하는 클래스입니다.
 *
 * @see Adaptation
 * @see DividerAdaptation
 */
private class DividerAdaptationScope: Adaptation<DividerAdaptation, DividerAdaptation>() {

    @Composable
    override fun rememberCupertinoAdaptation(): DividerAdaptation {

        val color = CupertinoDividerDefaults.color
        return remember(color) {
            DividerAdaptation(
                color = color,
                thickness = CupertinoDividerDefaults.Thickness
            )
        }
    }

    @Composable
    override fun rememberMaterialAdaptation(): DividerAdaptation {
        val color = DividerDefaults.color

        return remember(color) {
            DividerAdaptation(
                color = color,
                thickness = DividerDefaults.Thickness
            )
        }
    }
}

@Stable
/**
 * 구분선 컴포넌트에 대한 적응형 어댑테이션 클래스로, 구분선의 색상과 두께 속성을 관리합니다.
 *
 * @param color 구분선의 색상
 * @param thickness 구분선의 두께
 * @see Color
 * @see Dp
 */
class DividerAdaptation internal constructor(
    color: Color,
    thickness: Dp
) {
    var color: Color by mutableStateOf(color)
    var thickness: Dp by mutableStateOf(thickness)
}
    )
}

@Composable
@ExperimentalAdaptiveApi
fun AdaptiveVerticalDivider(
    modifier: Modifier = Modifier,
    adaptation: AdaptationScope<DividerAdaptation, DividerAdaptation>.() -> Unit = {}
) {
    AdaptiveWidget(
        adaptation = remember {
            DividerAdaptationScope()
        },
        adaptationScope = adaptation,
        material = {
            VerticalDivider(
                modifier = modifier,
                thickness = it.thickness,
                color = it.color
            )
        },
        cupertino = {
            CupertinoVerticalDivider(
                modifier = modifier,
                thickness = it.thickness,
                color = it.color
            )
        }
    )
}

@ExperimentalAdaptiveApi
private class DividerAdaptationScope: Adaptation<DividerAdaptation, DividerAdaptation>() {

    @Composable
    override fun rememberCupertinoAdaptation(): DividerAdaptation {

        val color = CupertinoDividerDefaults.color
        return remember(color) {
            DividerAdaptation(
                color = color,
                thickness = CupertinoDividerDefaults.Thickness
            )
        }
    }

    @Composable
    override fun rememberMaterialAdaptation(): DividerAdaptation {
        val color = DividerDefaults.color

        return remember(color) {
            DividerAdaptation(
                color = color,
                thickness = DividerDefaults.Thickness
            )
        }
    }
}

@Stable
class DividerAdaptation internal constructor(
    color: Color,
    thickness: Dp
) {
    var color: Color by mutableStateOf(color)
    var thickness: Dp by mutableStateOf(thickness)
}
