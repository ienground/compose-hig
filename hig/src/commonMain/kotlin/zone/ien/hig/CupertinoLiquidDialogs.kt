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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.union
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.colorControls
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.highlight.Highlight
import com.kyant.shapes.RoundedRectangle
import zone.ien.hig.section.CupertinoSectionTokens
import zone.ien.hig.theme.BrightSeparatorColor
import zone.ien.hig.theme.CupertinoColors
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.theme.isDark
import zone.ien.hig.theme.systemGray7

/**
 * Native analog for the compose [CupertinoLiquidAlertDialog].
 *
 * @param onDismissRequest called when dialog is already dismissed. Must not be ignored
 * @param title alert dialog title
 * @param message alert dialog message
 * @param containerColor color of the dialog background
 * @param properties dialog properties
 * @param buttonsOrientation layout orientation of the dialog buttons
 * @param buttons actions builder block
 * */
@Composable
@ExperimentalCupertinoApi
fun CupertinoLiquidAlertDialog(
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
    message: (@Composable () -> Unit)? = null,
    containerColor: Color = CupertinoLiquidDialogsDefaults.ContainerColor,
    shape: Shape = CupertinoLiquidDialogsDefaults.Shape,
    shadowElevation: Dp = CupertinoLiquidDialogsTokens.AlertDialogElevation,
    properties: DialogProperties = DialogProperties(),
    backdrop: Backdrop,
    buttonsOrientation: Orientation = CupertinoLiquidDialogsDefaults.ButtonOrientation,
    buttons: AlertDialogActionsScope.() -> Unit,
) {
    AnimatedDialog(
        properties = properties,
        onDismissRequest = onDismissRequest,
        enterTransition = scaleIn(initialScale = 1.2f) + fadeIn(),
        exitTransition = scaleOut(targetScale = 1.2f) + fadeOut(),
    ) {
        val isDarkTheme = isSystemInDarkTheme()
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(40.dp)
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { shape },
                    effects = {
                        colorControls(
                            brightness = if (!isDarkTheme) 0.2f else 0f,
                            saturation = 1.5f
                        )
                        blur(if (!isDarkTheme) 16.dp.toPx() else 8.dp.toPx())
                        lens(24.dp.toPx(), 48.dp.toPx(), depthEffect = true)
                    },
                    highlight = { Highlight.Plain },
                    onDrawSurface = {
                        drawRect(containerColor)
                    }
                )
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 24.dp)
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
            ) {
                ProvideTextStyle(
                    value = CupertinoTheme.typography.headline.copy(),
                    content = title
                )
                message?.let {
                    ProvideTextStyle(
                        value = CupertinoTheme.typography.body.copy(),
                        content = it
                    )
                }
            }

            val scope = CupertinoLiquidAlertDialogButtonsScopeImpl(buttonsOrientation).apply(buttons)

            scope.Content()
        }
    }
}

@Immutable
object CupertinoLiquidDialogsDefaults {
    val ScrimColor: Color
        @Composable
        @ReadOnlyComposable
        get() = Color.Black.copy(alpha = if (isDark()) .4f else .0f)

    val ButtonOrientation: Orientation = Orientation.Horizontal

    val ContainerColor: Color
        @Composable
        get() = CupertinoColors.systemGray7

    val Shape: RoundedRectangle
        @Composable
        @ReadOnlyComposable
        get() = RoundedRectangle(48.dp)
}
@Composable
private fun AnimatedDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    enterTransition: EnterTransition,
    exitTransition: ExitTransition,
    scrimColor: Color = CupertinoLiquidDialogsDefaults.ScrimColor,
    content: @Composable BoxScope.() -> Unit,
) {
    val haptic = LocalHapticFeedback.current

    Dialog(
        onDismissRequest = onDismissRequest,
        properties =
            FullscreenPopupProperties(
                dismissOnBackPress = properties.dismissOnBackPress,
                dismissOnClickOutside = properties.dismissOnClickOutside,
                usePlatformDefaultWidth = false,
            ),
    ) {
        CompositionLocalProvider(LocalHapticFeedback provides haptic) {
            var visible by remember {
                mutableStateOf(false)
            }
            LaunchedEffect(0) {
                visible = true
            }

            val animatedScrimColor by animateColorAsState(
                if (visible) scrimColor else scrimColor.copy(alpha = 0f),
            )

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .drawWithContent {
                            drawRect(animatedScrimColor)
                            drawContent()
                        }.then(
                            if (properties.dismissOnClickOutside) {
                                Modifier.pointerInput(0) {
                                    detectTapGestures {
                                        onDismissRequest()
                                    }
                                }
                            } else {
                                Modifier
                            },
                        ).then(
                            if (properties.platformInsets) {
                                Modifier
                                    .systemBarsPadding()
                                    .imePadding()
                            } else {
                                Modifier
                            },
                        ),
            ) {
                AnimatedVisibility(
                    visible = visible,
                    enter = enterTransition,
                    exit = exitTransition,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize(),
                        content = content,
                    )
                }
            }
        }
    }
}

private class CupertinoLiquidAlertDialogButtonsScopeImpl(
    private val orientation: Orientation,
) : AlertDialogActionsScope {
    private val buttons = mutableListOf<@Composable () -> Unit>()

    override fun action(
        onClick: () -> Unit,
        style: AlertActionStyle,
        enabled: Boolean,
        title: @Composable () -> Unit,
    ) {
        buttons.add {
            Box(
                Modifier
                    .clickable(
                        enabled = enabled,
                        onClick = onClick,
                        role = Role.Button,
                    ).fillMaxSize(),
                contentAlignment = Alignment.Center,
                content = {
                    val s = style.apply(CupertinoTheme.typography.body, isDark())
                    ProvideTextStyle(
                        s.copy(
                            color =
                                if (enabled) {
                                    s.color
                                } else {
                                    CupertinoTheme.colorScheme.tertiaryLabel
                                },
                        ),
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor provides LocalTextStyle.current.color,
                        ) {
                            title()
                        }
                    }
                },
            )
        }
    }

    @Composable
    fun Content() {
        CompositionLocalProvider(
            LocalSeparatorColor provides BrightSeparatorColor,
        ) {
            Column {
                CupertinoHorizontalDivider()
                if (orientation == Orientation.Horizontal) {
                    Row(
                        modifier =
                            Modifier
                                .height(CupertinoLiquidDialogsTokens.AlertDialogButtonHeight),
                    ) {
                        buttons.fastForEachIndexed { i, btn ->
                            Box(Modifier.weight(1f)) {
                                btn()
                            }
                            if (i != buttons.lastIndex) {
                                CupertinoVerticalDivider()
                            }
                        }
                    }
                } else {
                    buttons.fastForEachIndexed { i, btn ->
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(CupertinoLiquidDialogsTokens.AlertDialogButtonHeight),
                        ) {
                            btn()
                        }
                        if (i != buttons.lastIndex) {
                            CupertinoHorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

internal object CupertinoLiquidDialogsTokens {
    val AlertDialogElevation: Dp = 1.dp
    val AlertDialogPadding = CupertinoSectionTokens.HorizontalPadding
    val AlertDialogWidth: Dp = 270.dp
    val AlertDialogMinHeight: Dp = 110.dp
    val AlertDialogTitleMessageSpacing: Dp = 4.dp
    val AlertDialogButtonHeight: Dp = CupertinoSectionTokens.MinHeight

    val ActionSheetTitlePaddingValues = PaddingValues(12.dp)

    val ActionSheetTitleAndMessagePaddingValues =
        PaddingValues(
            top = 12.dp,
            start = 12.dp,
            end = 12.dp,
            bottom = 24.dp,
        )

    val ActionSheetMaxWidth: Dp = 500.dp
    val ActionSheetSidePadding = 8.dp
    val ActionSheetButtonHeight: Dp = 56.dp
    val ActionSheetTitleMessageSpacing: Dp = 6.dp
    val ActionSheetWindowInsets: WindowInsets
        @Composable
        get() =
            WindowInsets.navigationBars.union(
                WindowInsets(
                    bottom =
                    ActionSheetSidePadding,
                ),
            )
}
