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

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme as materialLightColorScheme
import androidx.compose.material3.Shapes as MaterialShapes
import androidx.compose.material3.ColorScheme as MaterialColorScheme
import androidx.compose.material3.Typography as MaterialTypography

import zone.ien.hig.theme.ColorScheme as CupertinoColorScheme
import zone.ien.hig.theme.Typography as CupertinoTypography
import zone.ien.hig.theme.Shapes as CupertinoShapes
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.theme.lightColorScheme as cupertinoLightColorScheme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import zone.ien.hig.LocalContentColorProvider
import zone.ien.hig.LocalTextStyleProvider
import zone.ien.hig.CupertinoIcon
import zone.ien.hig.CupertinoText

import androidx.compose.material3.LocalContentColor as MaterialLocalContentColor
import androidx.compose.material3.LocalTextStyle as MaterialLocalTextStyle

/**
 * 테마의 유형을 정의하는 열거형입니다.
 * 
 * @property Cupertino Cupertino 스타일 테마
 * @property Material3 Material Design 3 스타일 테마
 */
enum class Theme {
    Cupertino, Material3
}

/**
 * 적응형 테마로, 현재 테마에 따라 Material 또는 Cupertino 위젯을 사용합니다.
 * 
 * 이 테마는 [Text] ↔ [CupertinoText] 및 [Icon] ↔ [CupertinoIcon] 컴포넌트를 동일하게 작동하게 만듭니다.
 * 
 * 현재 테마는 [content] 내에서 [currentTheme] 속성을 사용하여 접근할 수 있습니다.
 * 
 * @param target [AdaptiveWidget]에 대한 테마
 * @param material [MaterialTheme] 사양
 * @param cupertino [CupertinoTheme] 사양
 * @param content 테마가 적용된 내용
 */
@ExperimentalAdaptiveApi
@Composable
fun AdaptiveTheme(
    target: Theme = DefaultTheme,
    material: MaterialThemeSpec = MaterialThemeSpec.Default(),
    cupertino: CupertinoThemeSpec = CupertinoThemeSpec.Default(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalTheme provides target,
        LocalContentColorProvider provides MaterialLocalContentColor,
        LocalTextStyleProvider provides MaterialLocalTextStyle,
    ) {
        when (LocalTheme.current) {
            Theme.Cupertino -> {
                MaterialTheme(
                    colorScheme = material.colorScheme,
                    shapes = material.shapes,
                    typography = material.typography
                ) {
                    CupertinoTheme(
                        colorScheme = cupertino.colorScheme,
                        shapes = cupertino.shapes,
                        typography = cupertino.typography,
                        content = content
                    )
                }
            }

            Theme.Material3 -> {
                CupertinoTheme(
                    colorScheme = cupertino.colorScheme,
                    shapes = cupertino.shapes,
                    typography = cupertino.typography
                ) {
                    MaterialTheme(
                        colorScheme = material.colorScheme,
                        shapes = material.shapes,
                        typography = material.typography,
                        content = content
                    )
                }
            }
        }
    }
}

/**
 * 적응형 테마로, 현재 테마에 따라 Material 또는 Cupertino 위젯을 사용합니다.
 * 
 * 이 테마는 [Text] ↔ [CupertinoText] 및 [Icon] ↔ [CupertinoIcon] 컴포넌트를 동일하게 작동하게 만듭니다.
 * 
 * 현재 테마는 [content] 내에서 [currentTheme] 속성을 사용하여 접근할 수 있습니다.
 * 
 * @param target [AdaptiveWidget]에 대한 테마. iOS의 경우 [Theme.Cupertino] 기본값을 사용하고, 다른 플랫폼의 경우 [Theme.Material3] 기본값을 사용합니다.
 * @param material [MaterialTheme] 사양. 참고로 람다 파라미터를 내용으로 사용해야 합니다.
 * @param cupertino [CupertinoTheme] 사양. 참고로 람다 파라미터를 내용으로 사용해야 합니다.
 * @param content 테마가 적용된 내용
 */
@ExperimentalAdaptiveApi
@Deprecated(
    message = "테마 사양을 파라미터로 받는 버전을 사용하세요",
    replaceWith = ReplaceWith(
        "AdaptiveTheme(target, MaterialThemeSpec.Default(), CupertinoThemeSpec.Default(), content)",
        "import zone.ien.hig.adaptive.MaterialThemeSpec",
        "import zone.ien.hig.adaptive.CupertinoThemeSpec",
    )
)
@Composable
fun AdaptiveTheme(
    target: Theme = DefaultTheme,
    material: @Composable (content: @Composable () -> Unit) -> Unit = {
        MaterialTheme(content = it)
    },
    cupertino: @Composable (content: @Composable () -> Unit) -> Unit = {
        CupertinoTheme(content = it)
    },
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalTheme provides target,
        LocalContentColorProvider provides MaterialLocalContentColor,
        LocalTextStyleProvider provides MaterialLocalTextStyle,
    ) {
        when (LocalTheme.current) {
            Theme.Cupertino -> {
                material {
                    cupertino(content)
                }
            }

            Theme.Material3 -> {
                cupertino {
                    material(content)
                }
            }
        }
    }
}

@Immutable
@ExperimentalAdaptiveApi
/**
 * Material 테마 사양 클래스로, Material Design 3 테마의 컬러, 모양, 타이포그래피를 정의합니다.
 * 
 * @param colorScheme [MaterialColorScheme] - 테마의 컬러 스키마
 * @param shapes [MaterialShapes] - 테마의 모양
 * @param typography [MaterialTypography] - 테마의 타이포그래피
 */
class MaterialThemeSpec(
    val colorScheme: MaterialColorScheme = materialLightColorScheme(),
    val shapes: MaterialShapes = MaterialShapes(),
    val typography: MaterialTypography = MaterialTypography(),
) {
    /**
     * MaterialThemeSpec 인스턴스를 복사하여 새로운 인스턴스를 생성합니다.
     * 
     * @param colorScheme 새로운 컬러 스키마
     * @param shapes 새로운 모양
     * @param typography 새로운 타이포그래피
     * @return 복사된 MaterialThemeSpec 인스턴스
     */
    fun copy(
        colorScheme: MaterialColorScheme = this.colorScheme,
        shapes: MaterialShapes = this.shapes,
        typography: MaterialTypography = this.typography
    ) = MaterialThemeSpec(
        colorScheme = colorScheme,
        shapes = shapes,
        typography = typography
    )

    override fun toString(): String {
        return "MaterialThemeSpec(colorScheme=$colorScheme, shapes=$shapes, typography=$typography)"
    }

    companion object {
        /**
         * MaterialThemeSpec의 기본 인스턴스를 생성합니다.
         * 
         * @param colorScheme 컬러 스키마. 기본값은 MaterialTheme.colorScheme입니다.
         * @param shapes 모양. 기본값은 MaterialTheme.shapes입니다.
         * @param typography 타이포그래피. 기본값은 MaterialTheme.typography입니다.
         * @return 생성된 MaterialThemeSpec 인스턴스
         */
        @Composable
        fun Default(
            colorScheme: MaterialColorScheme = MaterialTheme.colorScheme,
            shapes: MaterialShapes = MaterialTheme.shapes,
            typography: MaterialTypography = MaterialTheme.typography,
        ) = MaterialThemeSpec(colorScheme, shapes, typography)
    }
}

@Immutable
@ExperimentalAdaptiveApi
/**
 * Cupertino 테마 사양 클래스로, Cupertino 테마의 컬러, 모양, 타이포그래피를 정의합니다.
 * 
 * @param colorScheme [CupertinoColorScheme] - 테마의 컬러 스키마
 * @param shapes [CupertinoShapes] - 테마의 모양
 * @param typography [CupertinoTypography] - 테마의 타이포그래피
 */
class CupertinoThemeSpec(
    val colorScheme: CupertinoColorScheme = cupertinoLightColorScheme(),
    val shapes: CupertinoShapes = CupertinoShapes(),
    val typography: CupertinoTypography = CupertinoTypography()
) {
    /**
     * CupertinoThemeSpec 인스턴스를 복사하여 새로운 인스턴스를 생성합니다.
     * 
     * @param colorScheme 새로운 컬러 스키마
     * @param shapes 새로운 모양
     * @param typography 새로운 타이포그래피
     * @return 복사된 CupertinoThemeSpec 인스턴스
     */
    fun copy(
        colorScheme: CupertinoColorScheme = this.colorScheme,
        shapes: CupertinoShapes = this.shapes,
        typography: CupertinoTypography = this.typography
    ) = CupertinoThemeSpec(
        colorScheme = colorScheme,
        shapes = shapes,
        typography = typography
    )

    override fun toString(): String {
        return "CupertinoThemeSpec(colorScheme=$colorScheme, shapes=$shapes, typography=$typography)"
    }
    companion object {
        /**
         * CupertinoThemeSpec의 기본 인스턴스를 생성합니다.
         * 
         * @param colorScheme 컬러 스키마. 기본값은 CupertinoTheme.colorScheme입니다.
         * @param shapes 모양. 기본값은 CupertinoTheme.shapes입니다.  
         * @param typography 타이포그래피. 기본값은 CupertinoTheme.typography입니다.
         * @return 생성된 CupertinoThemeSpec 인스턴스
         */
        @Composable
        fun Default(
            colorScheme: CupertinoColorScheme = CupertinoTheme.colorScheme,
            shapes: CupertinoShapes = CupertinoTheme.shapes,
            typography: CupertinoTypography = CupertinoTheme.typography,
        ) = CupertinoThemeSpec(colorScheme, shapes, typography)
    }
}


/**
 * [AdaptiveTheme]에서 선언된 테마로, 현재 테마를 확인할 수 있습니다.
 */
@ExperimentalAdaptiveApi
val currentTheme: Theme
    @Composable
    get() = LocalTheme.current

internal expect val DefaultTheme: Theme

internal val LocalTheme = staticCompositionLocalOf<Theme> {
    error("Adaptive theme is not provided. Please add AdaptiveTheme { } to the root of your composable hierarchy")
}