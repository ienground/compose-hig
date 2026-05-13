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
 * The supported themes for adaptive widgets.
 *
 * This enum defines the two supported themes that can be used in [AdaptiveTheme].
 * The choice of theme affects how adaptive widgets behave and which design system they follow.
 */
enum class Theme {
    Cupertino, Material3
}

/**
 * Adaptive theme depending on [target]. It allows to seamlessly use Material and Cupertino widgets.
 *
 * This theme provides a way to seamlessly use Material and Cupertino widgets together in the same application
 * by adapting based on the target theme. It also makes [Text] and [CupertinoText], as well as [Icon] and [CupertinoIcon]
 * behave identically in both design systems.
 *
 * The current theme target can be accessed inside the [content] using [currentTheme] property.
 *
 * @param target theme for adaptive widgets. Defaults to [Theme.Cupertino] for iOS and [Theme.Material3] for other platforms.
 * @param material [MaterialThemeSpec] specification.
 * @param cupertino [CupertinoThemeSpec] specification.
 * @param content themed content.
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
 * Adaptive theme depending on [target]. It allows to seamlessly use Material and Cupertino widgets.
 *
 * This theme also allows to use [Text] together with [CupertinoText] and
 * [Icon] together with [CupertinoIcon] both in Material and Cupertino widgets.
 * This components will behave identically
 *
 * Current theme target can be accessed inside the [content] using [currentTheme] property.
 *
 * @param target theme for adaptive widgets. Defaults to [Theme.Cupertino] for iOS
 * and [Theme.Material3] for other platforms
 * @param material [MaterialTheme] specification. NOTE: You must use lambda parameter as a content
 * @param cupertino [CupertinoTheme] specification. NOTE: You must use lambda parameter as a content
 * @param content themed content
 */
@ExperimentalAdaptiveApi
@Deprecated(
    message = "Use the version that takes theme specifications as parameters",
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

/**
 * Material theme specification.
 *
 * This class holds the specification for Material themes including color scheme, shapes, and typography.
 *
 * @param colorScheme the color scheme to use for Material design
 * @param shapes the shapes to use for Material design
 * @param typography the typography to use for Material design
 */
@Immutable
@ExperimentalAdaptiveApi
class MaterialThemeSpec(
    val colorScheme: MaterialColorScheme = materialLightColorScheme(),
    val shapes: MaterialShapes = MaterialShapes(),
    val typography: MaterialTypography = MaterialTypography(),
) {
    /**
     * Creates a copy of this MaterialThemeSpec with the specified values replaced.
     * 
     * @param colorScheme the new color scheme
     * @param shapes the new shapes
     * @param typography the new typography
     * @return a copy of this MaterialThemeSpec
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
         * Creates a default Material theme specification based on the current MaterialTheme.
         *
         * @param colorScheme the color scheme to use for Material design
         * @param shapes the shapes to use for Material design
         * @param typography the typography to use for Material design
         * @return a MaterialThemeSpec with the default values
         */
        @Composable
        fun Default(
            colorScheme: MaterialColorScheme = MaterialTheme.colorScheme,
            shapes: MaterialShapes = MaterialTheme.shapes,
            typography: MaterialTypography = MaterialTheme.typography,
        ) = MaterialThemeSpec(colorScheme, shapes, typography)
    }
}

/**
 * Cupertino theme specification.
 *
 * This class holds the specification for Cupertino themes including color scheme, shapes, and typography.
 *
 * @param colorScheme the color scheme to use for Cupertino design
 * @param shapes the shapes to use for Cupertino design
 * @param typography the typography to use for Cupertino design
 */
@Immutable
@ExperimentalAdaptiveApi
/**
 * Cupertino theme specification class that defines color, shapes, and typography for Cupertino theme.
 * 
 * @param colorScheme [CupertinoColorScheme] - Theme color scheme
 * @param shapes [CupertinoShapes] - Theme shapes
 * @param typography [CupertinoTypography] - Theme typography
 */
class CupertinoThemeSpec(
    val colorScheme: CupertinoColorScheme = cupertinoLightColorScheme(),
    val shapes: CupertinoShapes = CupertinoShapes(),
    val typography: CupertinoTypography = CupertinoTypography()
) {
    /**
     * Creates a copy of this CupertinoThemeSpec with the specified values replaced.
     * 
     * @param colorScheme the new color scheme
     * @param shapes the new shapes
     * @param typography the new typography
     * @return a copy of this CupertinoThemeSpec
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
         * Creates a default Cupertino theme specification based on the current CupertinoTheme.
         *
         * @param colorScheme the color scheme to use for Cupertino design
         * @param shapes the shapes to use for Cupertino design
         * @param typography the typography to use for Cupertino design
         * @return a CupertinoThemeSpec with the default values
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
 * The theme declared as a target in [AdaptiveTheme], allowing you to check the current theme.
 */
@ExperimentalAdaptiveApi
val currentTheme: Theme
    @Composable
    get() = LocalTheme.current

internal expect val DefaultTheme: Theme

internal val LocalTheme = staticCompositionLocalOf<Theme> {
    error("Adaptive theme is not provided. Please add AdaptiveTheme { } to the root of your composable hierarchy")
}