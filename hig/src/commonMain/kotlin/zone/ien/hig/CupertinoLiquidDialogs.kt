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

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.shadow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.highlight.Highlight
import com.kyant.capsule.ContinuousRoundedRectangle
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.utils.glassEdge

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
    val useDefaultAlertMaterial = containerColor == CupertinoDialogsDefaults.AlertContainerColor
    val isDarkTheme = CupertinoTheme.colorScheme.isDark
    AnimatedDialog(
        properties = properties,
        onDismissRequest = onDismissRequest,
        enterTransition = scaleIn(initialScale = 0.94f) + fadeIn(tween(180)),
        exitTransition = scaleOut(targetScale = 0.98f, animationSpec = tween(120)) + fadeOut(tween(120)),
    ) { dismiss, updatePanelBounds ->
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val dialogMaxHeight = if (maxHeight != Dp.Infinity) maxHeight * 0.85f else Dp.Infinity
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .shadow(shadowElevation, shape, clip = true)
                    .widthIn(max = CupertinoDialogsTokens.AlertDialogWidth)
                    .fillMaxWidth()
                    .heightIn(
                        min = CupertinoLiquidDialogsTokens.AlertDialogMinHeight,
                        max = dialogMaxHeight,
                    )
                    .verticalScroll(rememberScrollState())
                    .drawBackdrop(
                        backdrop = backdrop,
                        shape = { shape },
                        effects = {
                            blur(CupertinoDialogsTokens.AlertDialogBlurRadius.toPx())
                        },
                        highlight = { Highlight.Plain },
                        onDrawSurface = {
                            drawAlertDialogSurface(containerColor, useDefaultAlertMaterial, isDarkTheme)
                        },
                    )
                    .glassEdge(shape)
                    .onGloballyPositioned { updatePanelBounds(it.boundsInRoot()) },
            ) {
                CompositionLocalProvider(LocalContainerColor provides containerColor) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(CupertinoDialogsTokens.AlertDialogOuterPadding),
                    ) {
                        Column(
                            verticalArrangement =
                                Arrangement.spacedBy(CupertinoDialogsTokens.AlertDialogTitleMessageSpacing),
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(CupertinoDialogsTokens.AlertDialogHeaderPadding),
                        ) {
                            ProvideTextStyle(
                                value = CupertinoTheme.typography.headline.copy(textAlign = TextAlign.Start),
                                content = title,
                            )
                            message?.let {
                                ProvideTextStyle(
                                    value = CupertinoTheme.typography.body.copy(
                                        color = CupertinoTheme.colorScheme.secondaryLabel,
                                        textAlign = TextAlign.Start,
                                    ),
                                    content = it,
                                )
                            }
                        }

                        CupertinoAlertDialogButtonsScopeImpl(buttonsOrientation, dismiss)
                            .apply(buttons)
                            .Content()
                    }
                }
            }
        }
    }
}

@Immutable
object CupertinoLiquidDialogsDefaults {
    val ScrimColor: Color
        @Composable
        @ReadOnlyComposable
        get() = Color.Black.copy(alpha = if (CupertinoTheme.colorScheme.isDark) .4f else .2f)

    val ButtonOrientation: Orientation = Orientation.Horizontal

    val ContainerColor: Color
        @Composable
        @ReadOnlyComposable
        get() = CupertinoDialogsDefaults.AlertContainerColor

    val Shape: ContinuousRoundedRectangle
        @Composable
        @ReadOnlyComposable
        get() = CupertinoDialogsDefaults.AlertShape
}
internal object CupertinoLiquidDialogsTokens {
    val AlertDialogElevation: Dp = 1.dp
    val AlertDialogMinHeight: Dp = 110.dp

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
