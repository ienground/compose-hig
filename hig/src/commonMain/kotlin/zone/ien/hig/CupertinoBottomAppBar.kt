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



package zone.ien.hig

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.runtimeShaderEffect
import zone.ien.hig.section.CupertinoSectionDefaults
import zone.ien.hig.section.CupertinoSectionTokens
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.utils.CupertinoGlassDefaults
import zone.ien.hig.utils.LocalCupertinoBackdrop
import zone.ien.hig.utils.glassEdge
import zone.ien.hig.utils.rememberDefaultBackdrop

/**
 * Return true if container can't scroll forward
 *
 * @return True if the container cannot scroll forward
 */
inline val ScrollableState.isNavigationBarTransparent: Boolean
    get() = !canScrollForward

/**
 * Navigation bar itself does not produce cupertino thin material glass effect.
 * This effect works only inside [CupertinoScaffold], [CupertinoBottomSheetScaffold], [CupertinoBottomSheetContent].
 * Use this function to achieve this effect with custom bottom bar.
 * It will communicate with scaffold and return either [Color.Transparent] if color was
 * successfully applied to scaffold (and navigation bar itself should be transparent) or passed color
 * if scaffold wasn't found.
 *
 * @param color navigation bar container color. Alpha is controlled by the [CupertinoScaffold]
 * @param isTransparent if navigation bar currently should be transparent. See [CupertinoNavigationBar]
 * for use cases example.
 * @return The appropriate color for the navigation bar
 */
@Composable
@ExperimentalCupertinoApi
fun cupertinoTranslucentBottomBarColor(
    color: Color,
    isTranslucent: Boolean,
    isTransparent: Boolean,
): Color {
    if (!isTranslucent) {
        return color
    }

    val appBarsState = LocalAppBarsState.current ?: return color

    DisposableEffect(appBarsState, color) {
        appBarsState.bottomBarColor.value = color
        onDispose {
            appBarsState.bottomBarColor.value = Color.Unspecified
        }
    }

    DisposableEffect(isTransparent, appBarsState) {
        appBarsState.isBottomBarTransparent.value = isTransparent
        onDispose {
            appBarsState.isBottomBarTransparent.value = true
        }
    }
    return Color.Transparent
}

/**
 * Composable function that creates a Cupertino-style bottom app bar.
 *
 * This composable displays a bottom app bar with Cupertino styling, similar to iOS's bottom navigation bar.
 *
 * @param modifier The modifier to be applied to the bottom app bar
 * @param isTranslucent Whether the bottom app bar should be translucent
 * @param isTransparent Whether the bottom app bar should be transparent
 * @param containerColor The color of the bottom app bar container
 * @param contentColor The color of the content in the bottom app bar
 * @param contentPadding The padding around the content in the bottom app bar
 * @param windowInsets The window insets to be applied to the bottom app bar
 * @param content The content to be displayed in the bottom app bar
 */
@ExperimentalCupertinoApi
@Composable
fun CupertinoBottomAppBar(
    modifier: Modifier = Modifier,
    isTranslucent: Boolean = true,
    isTransparent: Boolean = false,
    containerColor: Color = CupertinoNavigationBarDefaults.containerColor,
    contentColor: Color = CupertinoTheme.colorScheme.accent,
    contentPadding: PaddingValues = CupertinoSectionDefaults.PaddingValues,
    windowInsets: WindowInsets = WindowInsets.navigationBars,
    content: @Composable RowScope.() -> Unit,
) {
    BottomAppBarSurface(
        modifier = modifier,
        isTranslucent = isTranslucent,
        isTransparent = isTransparent,
        containerColor = containerColor,
        contentColor = contentColor,
        windowInsets = windowInsets,
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}

/**
 * leading, center, trailing 영역의 콘텐츠를 담는 하단 도구 막대 슬롯입니다.
 */
class CupertinoBottomAppBarSlots(
    val leadingContent: @Composable RowScope.() -> Unit = {},
    val centerContent: @Composable RowScope.() -> Unit = {},
    val trailingContent: @Composable RowScope.() -> Unit = {},
)

/**
 * leading, center, trailing 영역을 분리해 배치하는 하단 도구 막대를 만듭니다.
 * 각 영역의 동작 버튼은 [CupertinoBottomAppBarAction]을 사용해 Liquid Glass로 표시할 수 있습니다.
 */
@ExperimentalCupertinoApi
@Composable
fun CupertinoBottomAppBar(
    slots: CupertinoBottomAppBarSlots,
    modifier: Modifier = Modifier,
    isTranslucent: Boolean = true,
    isTransparent: Boolean = false,
    containerColor: Color = CupertinoNavigationBarDefaults.containerColor,
    contentColor: Color = CupertinoTheme.colorScheme.accent,
    contentPadding: PaddingValues = CupertinoSectionDefaults.PaddingValues,
    windowInsets: WindowInsets = WindowInsets.navigationBars,
) {
    BottomAppBarSurface(
        modifier = modifier,
        isTranslucent = isTranslucent,
        isTransparent = isTransparent,
        containerColor = containerColor,
        contentColor = contentColor,
        windowInsets = windowInsets,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
        ) {
            Row(
                modifier = Modifier.align(Alignment.CenterStart),
                horizontalArrangement = Arrangement.spacedBy(CupertinoSectionTokens.InlinePadding),
                verticalAlignment = Alignment.CenterVertically,
                content = slots.leadingContent,
            )
            Row(
                modifier = Modifier.align(Alignment.Center),
                horizontalArrangement = Arrangement.spacedBy(CupertinoSectionTokens.InlinePadding),
                verticalAlignment = Alignment.CenterVertically,
                content = slots.centerContent,
            )
            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                horizontalArrangement = Arrangement.spacedBy(CupertinoSectionTokens.InlinePadding),
                verticalAlignment = Alignment.CenterVertically,
                content = slots.trailingContent,
            )
        }
    }
}

/** 하단 도구 막대 안에 Liquid Glass 동작 버튼을 표시합니다. */
@ExperimentalCupertinoApi
@Composable
fun CupertinoBottomAppBarAction(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val backdrop = LocalCupertinoBackdrop.current ?: rememberDefaultBackdrop()
    CupertinoLiquidButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        backdrop = backdrop,
        content = content,
    )
}

@Composable
@OptIn(ExperimentalCupertinoApi::class)
private fun BottomAppBarSurface(
    modifier: Modifier,
    isTranslucent: Boolean,
    isTransparent: Boolean,
    containerColor: Color,
    contentColor: Color,
    windowInsets: WindowInsets,
    content: @Composable () -> Unit,
) {
    val backdrop = LocalCupertinoBackdrop.current ?: rememberDefaultBackdrop()
    val drawsGlass = isTranslucent && !isTransparent
    val surfaceColor = if (isTranslucent) {
        cupertinoTranslucentBottomBarColor(
            color = containerColor,
            isTranslucent = true,
            isTransparent = true,
        )
        Color.Transparent
    } else {
        containerColor
    }
    val shape = CupertinoTheme.shapes.extraLarge

    CupertinoSurface(
        modifier = modifier
            .padding(
                horizontal = CupertinoSectionTokens.HorizontalPadding,
            )
            .windowInsetsPadding(windowInsets),
        shape = shape,
        color = surfaceColor,
        contentColor = contentColor,
    ) {
        Box(Modifier.fillMaxWidth()) {
            if (drawsGlass) {
                BottomAppBarGlass(backdrop, shape, containerColor)
            }
            content()
        }
    }
}

@Composable
private fun BoxScope.BottomAppBarGlass(
    backdrop: LayerBackdrop,
    shape: androidx.compose.ui.graphics.Shape,
    tint: Color,
) {
    Box(
        Modifier
            .matchParentSize()
            .drawBackdrop(
                backdrop = backdrop,
                shape = { shape },
                effects = {
                    blur(CupertinoGlassDefaults.blurRadius.toPx())
                    runtimeShaderEffect(
                        "MaterialTint",
                        """
                            uniform shader content;
                            layout(color) uniform half4 tint;
                            uniform float tintIntensity;

                            half4 main(float2 coord) {
                                return mix(content.eval(coord), tint, tintIntensity);
                            }
                        """.trimIndent(),
                        "content",
                    ) {
                        setColorUniform("tint", tint)
                        setFloatUniform("tintIntensity", 0.48f)
                    }
                },
            )
            .glassEdge(shape),
    )
}
