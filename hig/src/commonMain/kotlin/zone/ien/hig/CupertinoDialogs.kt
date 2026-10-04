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
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.capsule.ContinuousRoundedRectangle
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.highlight.Highlight
import zone.ien.hig.CupertinoDialogsTokens.AlertDialogTitleMessageSpacing
import zone.ien.hig.section.CupertinoSectionTokens
import zone.ien.hig.theme.BrightSeparatorColor
import zone.ien.hig.theme.CupertinoColors
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.theme.isDark
import zone.ien.hig.theme.systemBlue
import zone.ien.hig.theme.systemRed
import zone.ien.hig.utils.CupertinoGlassDefaults
import zone.ien.hig.utils.LocalCupertinoBackdrop
import zone.ien.hig.utils.LocalCupertinoDialogBackdrop
import zone.ien.hig.utils.cupertinoGlassEffects
import zone.ien.hig.utils.glassEdge
import zone.ien.hig.utils.LocalCupertinoDialogBackdropMotion
import zone.ien.hig.utils.rememberCupertinoDialogBackdrop
import androidx.compose.runtime.rememberCoroutineScope
import zone.ien.hig.utils.InteractiveHighlight
import androidx.compose.foundation.basicMarquee
import androidx.compose.ui.draw.clipToBounds
import zone.ien.hig.utils.rememberDefaultBackdrop

/**
 * Style of the Cupertino alert action buttons
 * */
enum class AlertActionStyle {
    /**
     * Default action button
     * */
    Default {
        override fun apply(
            style: TextStyle,
            dark: Boolean,
        ): TextStyle =
            style.copy(
                fontWeight = FontWeight.Normal,
                color = CupertinoColors.systemBlue(dark),
                textAlign = TextAlign.Center,
            )
    },

    /**
     * Cancel action button. It will be displayed below the other buttons
     * with different container color and bolder font
     * */
    Cancel {
        override fun apply(
            style: TextStyle,
            dark: Boolean,
        ): TextStyle =
            style.copy(
                fontWeight = FontWeight.Bold,
                color = CupertinoColors.systemBlue(dark),
                textAlign = TextAlign.Center,
            )
    },

    /**
     * Destructive action button. It will be red
     * */
    Destructive {
        override fun apply(
            style: TextStyle,
            dark: Boolean,
        ): TextStyle =
            style.copy(
                fontWeight = FontWeight.Normal,
                color = CupertinoColors.systemRed(dark),
                textAlign = TextAlign.Center,
            )
    }, ;

    internal abstract fun apply(
        style: TextStyle,
        dark: Boolean,
    ): TextStyle
}

interface AlertDialogActionsScope {
    fun action(
        onClick: () -> Unit,
        style: AlertActionStyle = AlertActionStyle.Default,
        enabled: Boolean = true,
        title: @Composable () -> Unit,
    )
}

/**
 * Alert controller button with default style
 * */
fun AlertDialogActionsScope.default(
    onClick: () -> Unit,
    enabled: Boolean = true,
    title: @Composable () -> Unit,
) = action(
    onClick = onClick,
    style = AlertActionStyle.Default,
    enabled = enabled,
    title = title,
)

/**
 * Alert controller button with destructive style
 * */
fun AlertDialogActionsScope.destructive(
    onClick: () -> Unit,
    enabled: Boolean = true,
    title: @Composable () -> Unit,
) = action(
    onClick = onClick,
    style = AlertActionStyle.Destructive,
    enabled = enabled,
    title = title,
)

/**
 * Alert controller button with cancel style
 * */
fun AlertDialogActionsScope.cancel(
    onClick: () -> Unit,
    enabled: Boolean = true,
    title: @Composable () -> Unit,
) = action(
    onClick = onClick,
    style = AlertActionStyle.Cancel,
    enabled = enabled,
    title = title,
)

/**
 * Native analog for the compose [CupertinoAlertDialog].
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
fun CupertinoAlertDialog(
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
    message: (@Composable () -> Unit)? = null,
    containerColor: Color = CupertinoDialogsDefaults.AlertContainerColor,
    shape: Shape = CupertinoDialogsDefaults.AlertShape,
    shadowElevation: Dp = CupertinoDialogsTokens.AlertDialogElevation,
    properties: DialogProperties = DialogProperties(),
    buttonsOrientation: Orientation = CupertinoDialogsDefaults.ButtonOrientation,
    buttons: AlertDialogActionsScope.() -> Unit,
) {
    AnimatedDialog(
        properties = properties,
        onDismissRequest = onDismissRequest,
        outsidePressFeedback = true,
        enterTransition = scaleIn(initialScale = 0.94f) + fadeIn(tween(180)),
        exitTransition = scaleOut(targetScale = 0.98f, animationSpec = tween(120)) + fadeOut(tween(120)),
    ) { dismiss, updatePanelBounds ->
        CupertinoDialogPanel(
            title, message, containerColor, shape, shadowElevation,
            buttonsOrientation, dismiss, updatePanelBounds, buttons,
        )
    }
}

@Composable
@OptIn(ExperimentalCupertinoApi::class)
private fun CupertinoDialogPanel(
    title: (@Composable () -> Unit)?,
    message: (@Composable () -> Unit)?,
    containerColor: Color,
    shape: Shape,
    shadowElevation: Dp,
    buttonsOrientation: Orientation,
    dismiss: (afterDismiss: (() -> Unit)?) -> Unit,
    updatePanelBounds: (Rect) -> Unit,
    buttons: AlertDialogActionsScope.() -> Unit,
    emphasizeDefaultAction: Boolean = true,
) {
    val backdrop = rememberCupertinoDialogBackdrop()
    val useDefaultAlertMaterial = containerColor == CupertinoDialogsDefaults.AlertContainerColor
    val isDarkTheme = CupertinoTheme.colorScheme.isDark
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val dialogMaxHeight = if (maxHeight != Dp.Infinity) maxHeight * 0.85f else Dp.Infinity
        CupertinoSurface(
            Modifier
                .align(Alignment.Center)
                .shadow(
                    elevation = shadowElevation,
                    shape = shape,
                    clip = true,
                )
                .then(
                    if (backdrop != null) {
                        Modifier.drawBackdrop(
                            backdrop = backdrop,
                            shape = { shape },
                            effects = { cupertinoGlassEffects(CupertinoDialogsTokens.AlertDialogBlurRadius.toPx(), 4.dp.toPx(), 8.dp.toPx()) },
                            highlight = { Highlight.Plain },
                            onDrawSurface = {
                                drawAlertDialogSurface(containerColor, useDefaultAlertMaterial, isDarkTheme)
                            },
                        )
                    } else {
                        Modifier
                    },
                ).glassEdge(shape)
                .widthIn(max = CupertinoDialogsTokens.AlertDialogWidth)
                .fillMaxWidth()
                .heightIn(
                    min = CupertinoDialogsTokens.AlertDialogMinHeight,
                    max = dialogMaxHeight,
                )
                .onGloballyPositioned { updatePanelBounds(it.boundsInRoot()) },
            color = if (backdrop == null) containerColor else Color.Transparent,
        ) {
            CompositionLocalProvider(LocalContainerColor provides containerColor) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(CupertinoDialogsTokens.AlertDialogOuterPadding),
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(CupertinoDialogsTokens.AlertDialogHeaderPadding),
                        verticalArrangement = Arrangement.spacedBy(AlertDialogTitleMessageSpacing),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        title?.let {
                            ProvideTextStyle(
                                CupertinoTheme.typography.headline.copy(textAlign = TextAlign.Start),
                                content = it,
                            )
                        }
                        message?.let {
                            ProvideTextStyle(
                                CupertinoTheme.typography.body.copy(
                                    color = CupertinoTheme.colorScheme.secondaryLabel,
                                    textAlign = TextAlign.Start,
                                    fontWeight = FontWeight.Normal,
                                ),
                                content = it,
                            )
                        }
                    }

                    CupertinoAlertDialogButtonsScopeImpl(buttonsOrientation, dismiss, emphasizeDefaultAction)
                        .apply(buttons)
                        .Content()
                }
            }
        }
    }
}

/**
 * Compose alert dialog with iOS action sheet style.
 *
 * @param onDismissRequest called when dialog is already dismissed. Must not be ignored
 * @param title alert dialog title
 * @param message alert dialog message
 * @param containerColor not used in native dialog
 * @param secondaryContainerColor not used in native dialog
 * @param properties dialog properties
 * @param buttons actions builder block
 *
 */
@Composable
@ExperimentalCupertinoApi
fun CupertinoActionSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    title: (@Composable () -> Unit)? = null,
    message: (@Composable () -> Unit)? = null,
    containerColor: Color = CupertinoDialogsDefaults.ContainerColor,
    secondaryContainerColor: Color = CupertinoTheme.colorScheme.tertiarySystemBackground,
    properties: DialogProperties = DialogProperties(),
    content: (@Composable () -> Unit)? = null,
    buttons: AlertDialogActionsScope.() -> Unit,
) {
    val backdrop = LocalCupertinoDialogBackdrop.current ?: LocalCupertinoBackdrop.current
    CompositionLocalProvider(
        LocalContainerColor provides containerColor,
    ) {
        DialogSheet(
            visible = visible,
            onDismissRequest = onDismissRequest,
            dialogProperties = properties,
        ) {
            val hasTitle = title != null || message != null

            val scope =
                CupertinoActionSheetImpl(
                    hasTitle = hasTitle,
                    primaryContainerColor = containerColor,
                    secondaryContainerColor = secondaryContainerColor,
                    backdrop = backdrop,
                ).apply(buttons)

            scope.run {
                val pickerToolbar = content != null && hasPickerActions
                Content(
                    title = {
                        Column {
                            if (hasTitle && !pickerToolbar) {
                                Column(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                paddingValues =
                                                    if (message != null && title != null) {
                                                        CupertinoDialogsTokens.ActionSheetTitleAndMessagePaddingValues
                                                    } else {
                                                        CupertinoDialogsTokens.ActionSheetTitlePaddingValues
                                                    },
                                            ),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement =
                                        Arrangement
                                            .spacedBy(CupertinoDialogsTokens.ActionSheetTitleMessageSpacing),
                                ) {
                                    CompositionLocalProvider(
                                        LocalContentColor provides CupertinoTheme.colorScheme.secondaryLabel,
                                    ) {
                                        if (title != null) {
                                            ProvideTextStyle(
                                                CupertinoTheme.typography.footnote.copy(
                                                    fontWeight =
                                                        if (message != null) {
                                                            FontWeight.SemiBold
                                                        } else {
                                                            FontWeight.Normal
                                                        },
                                                    textAlign = TextAlign.Center,
                                                ),
                                            ) {
                                                title()
                                            }
                                        }
                                        if (message != null) {
                                            ProvideTextStyle(
                                                CupertinoTheme.typography.footnote.copy(
                                                    textAlign = TextAlign.Center,
                                                    fontWeight = FontWeight.Normal,
                                                ),
                                            ) {
                                                message()
                                            }
                                        }
                                    }
                                }
                            }
                            if (content != null && !pickerToolbar) {
                                if (hasTitle) {
                                    CupertinoHorizontalDivider()
                                }
                                CompositionLocalProvider(
                                    LocalContainerColor provides containerColor,
                                    content = content,
                                )
                            }
                        }
                    },
                    pickerTitle = title.takeIf { pickerToolbar },
                    pickerContent = content.takeIf { pickerToolbar },
                    pickerMessage = message.takeIf { pickerToolbar },
                )
            }
        }
    }
}

@Immutable
object CupertinoDialogsDefaults {
    val ScrimColor: Color
        @Composable
        @ReadOnlyComposable
        get() = Color.Black.copy(alpha = if (isDark()) .4f else .2f)

    val ButtonOrientation: Orientation = Orientation.Horizontal

    val ContainerColor: Color
        @Composable
        get() = CupertinoGlassDefaults.panelTint

    val AlertContainerColor: Color
        @Composable
        @ReadOnlyComposable
        get() = if (Accessibility.isReduceTransparencyEnabled) {
            if (CupertinoTheme.colorScheme.isDark) Color(0xFF1A1A1A) else Color(0xFFF2F2F7)
        } else if (CupertinoTheme.colorScheme.isDark) {
            Color(0xFF1A1A1A).copy(alpha = 0.72f)
        } else {
            Color.White.copy(alpha = 0.7f)
        }

    val Shape: ContinuousRoundedRectangle
        @Composable
        @ReadOnlyComposable
        get() = ContinuousRoundedRectangle(28.dp)

    val AlertShape: ContinuousRoundedRectangle
        @Composable
        @ReadOnlyComposable
        get() = ContinuousRoundedRectangle(CupertinoDialogsTokens.AlertDialogCornerRadius)
}

internal fun DrawScope.drawAlertDialogSurface(
    containerColor: Color,
    useDefaultMaterial: Boolean,
    isDark: Boolean,
) {
    if (!useDefaultMaterial || Accessibility.isReduceTransparencyEnabled) {
        drawRect(containerColor)
    } else if (isDark) {
        drawRect(Color(0xFF1A1A1A).copy(alpha = 0.72f))
        drawRect(Color.White.copy(alpha = 0.08f), blendMode = BlendMode.Luminosity)
        drawRect(Color.White.copy(alpha = 0.04f), blendMode = BlendMode.Lighten)
    } else {
        drawRect(Color.White.copy(alpha = 0.7f), blendMode = BlendMode.Lighten)
        drawRect(Color(0xFFBFBFBF).copy(alpha = 0.1f), blendMode = BlendMode.Darken)
    }
}

@Composable
@ReadOnlyComposable
internal expect fun FullscreenPopupProperties(
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = false,
    usePlatformDefaultWidth: Boolean = true,
): DialogProperties

@Composable
internal expect fun PrepareComposeDialogWindow()

expect val DialogProperties.platformInsets: Boolean

@Composable
internal fun AnimatedDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    enterTransition: EnterTransition,
    exitTransition: ExitTransition,
    scrimColor: Color = CupertinoDialogsDefaults.ScrimColor,
    visible: Boolean = true,
    outsidePressFeedback: Boolean = false,
    content: @Composable BoxScope.(
        dismiss: (afterDismiss: (() -> Unit)?) -> Unit,
        updatePanelBounds: (Rect) -> Unit,
    ) -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    val visibility = remember { MutableTransitionState(false) }
    val animationScope = rememberCoroutineScope()
    val outsideInteraction = remember(animationScope) { InteractiveHighlight(animationScope) }
    var dismissRequested by remember { mutableStateOf(false) }
    var afterDismiss by remember { mutableStateOf<(() -> Unit)?>(null) }
    var panelBounds by remember { mutableStateOf<Rect?>(null) }
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)
    val dismiss = remember(visibility) {
        { action: (() -> Unit)? ->
            if (!dismissRequested) {
                afterDismiss = action
                dismissRequested = true
                visibility.targetState = false
            }
        }
    }

    LaunchedEffect(visible) {
        if (visible) dismissRequested = false
        visibility.targetState = visible
    }
    LaunchedEffect(visibility.currentState, visibility.isIdle, dismissRequested) {
        if (dismissRequested && visibility.isIdle && !visibility.currentState) {
            val action = afterDismiss
            afterDismiss = null
            if (action == null) currentOnDismissRequest() else action()
        }
    }
    if ((!visible || dismissRequested) && visibility.isIdle && !visibility.currentState) return

    Dialog(
        onDismissRequest = { dismiss(null) },
        properties =
            FullscreenPopupProperties(
                dismissOnBackPress = properties.dismissOnBackPress,
                dismissOnClickOutside = properties.dismissOnClickOutside,
                usePlatformDefaultWidth = false,
            ),
    ) {
        PrepareComposeDialogWindow()
        CompositionLocalProvider(LocalHapticFeedback provides haptic) {
            val animatedScrimColor by animateColorAsState(
                if (visibility.targetState) scrimColor else scrimColor.copy(alpha = 0f),
            )

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .drawWithContent {
                            drawRect(animatedScrimColor)
                            drawContent()
                        }.pointerInput(visibility.targetState, properties.dismissOnClickOutside, outsidePressFeedback) {
                            detectTapGestures(
                                onPress = { offset ->
                                    if (visibility.targetState && panelBounds?.contains(offset) == false &&
                                        !properties.dismissOnClickOutside && outsidePressFeedback
                                    ) {
                                        outsideInteraction.press()
                                        try {
                                            tryAwaitRelease()
                                        } finally {
                                            outsideInteraction.release()
                                        }
                                    }
                                },
                                onTap = { offset ->
                                    if (visibility.targetState && properties.dismissOnClickOutside &&
                                        panelBounds?.contains(offset) == false
                                    ) dismiss(null)
                                },
                            )
                        }.then(
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
                    visibleState = visibility,
                    enter = enterTransition,
                    exit = exitTransition,
                ) {
                    Box(Modifier.fillMaxSize().graphicsLayer {
                        val scale = 1f + outsideInteraction.pressProgress * 0.012f
                        scaleX = scale
                        scaleY = scale
                    }) {
                        CompositionLocalProvider(
                            LocalCupertinoDialogBackdropMotion provides {
                                outsideInteraction.pressProgress
                                animatedScrimColor.alpha
                            },
                        ) {
                            content(dismiss) { panelBounds = it }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    dialogProperties: DialogProperties,
    content: @Composable () -> Unit,
) {
    AnimatedSheet(
        visible = visible,
        onDismissRequest = onDismissRequest,
        properties = dialogProperties,
    ) { updatePanelBounds ->
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val maxSheetHeight = if (maxHeight != Dp.Infinity) maxHeight * 0.9f else Dp.Infinity
            Box(
                Modifier
                    .widthIn(max = CupertinoDialogsTokens.ActionSheetMaxWidth)
                    .heightIn(max = maxSheetHeight)
                    .align(Alignment.BottomCenter)
                    .verticalScroll(rememberScrollState())
                    .onGloballyPositioned { updatePanelBounds(it.boundsInRoot()) },
            ) {
                content()
            }
        }
    }
}

@Composable
private fun AnimatedSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    scrimColor: Color = CupertinoDialogsDefaults.ScrimColor,
    content: @Composable BoxScope.(updatePanelBounds: (Rect) -> Unit) -> Unit,
) {
    val expandedStates = remember { MutableTransitionState(false) }
    var panelBounds by remember { mutableStateOf<Rect?>(null) }
    val updatePanelBounds: (Rect) -> Unit = { bounds ->
        if (panelBounds != bounds) panelBounds = bounds
    }

    expandedStates.targetState = visible

    if (expandedStates.currentState || expandedStates.targetState) {
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
            PrepareComposeDialogWindow()
            CompositionLocalProvider(LocalHapticFeedback provides haptic) {
                val transition = rememberTransition(expandedStates, "CupertinoSheet")

                val animatedScrimColor by transition.animateColor(
                    transitionSpec = {
                        sheetAnimation()
                    },
                ) {
                    if (it) scrimColor else scrimColor.copy(alpha = 0f)
                }

                val transitionProgress by transition.animateFloat(
                    transitionSpec = {
                        sheetAnimation()
                    },
                ) {
                    if (it) 0f else 1f
                }

                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .drawWithContent {
                                drawRect(animatedScrimColor)
                                drawContent()
                            }.let {
                                if (properties.dismissOnClickOutside && visible) {
                                    it.pointerInput(visible) {
                                        detectTapGestures { offset ->
                                            if (panelBounds?.contains(offset) != true) {
                                                onDismissRequest()
                                            }
                                        }
                                    }
                                } else {
                                    it
                                }
                            },
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    translationY = size.height * transitionProgress
                                },
                    ) {
                        content(updatePanelBounds)
                    }
                }
            }
        }
    }
}

internal class CupertinoAlertDialogButtonsScopeImpl(
    private val orientation: Orientation,
    private val dismiss: (afterDismiss: (() -> Unit)?) -> Unit,
    private val emphasizeDefaultAction: Boolean = true,
): AlertDialogActionsScope {
    private val buttons = mutableListOf<Pair<AlertActionStyle, @Composable (Boolean) -> Unit>>()

    override fun action(
        onClick: () -> Unit,
        style: AlertActionStyle,
        enabled: Boolean,
        title: @Composable () -> Unit,
    ) {
        buttons.add(style to { isPrimary ->
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.98f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
            )
            val highlightAlpha by animateFloatAsState(
                targetValue = if (isPressed) 1f else 0f,
                animationSpec = tween(100),
            )
            val colorScheme = CupertinoTheme.colorScheme
            val accent = colorScheme.accent
            val buttonBackground =
                if (isPrimary && style == AlertActionStyle.Default) {
                    accent
                } else {
                    colorScheme.tertiarySystemFill
                }
            val buttonContentColor =
                when {
                    !enabled -> colorScheme.tertiaryLabel
                    style == AlertActionStyle.Destructive -> CupertinoColors.systemRed(colorScheme.isDark)
                    isPrimary && style == AlertActionStyle.Default ->
                        CupertinoGlassDefaults.contentColor(accent, colorScheme.systemBackground)
                    else -> colorScheme.label
                }
            val animatedBackground by animateColorAsState(
                targetValue = if (isPressed && isPrimary) accent.copy(alpha = 0.86f) else buttonBackground,
                animationSpec = tween(100),
            )
            val selection = CupertinoGlassDefaults.selection
            Box(
                Modifier
                    .heightIn(min = CupertinoDialogsTokens.AlertDialogButtonHeight)
                    .clickable(
                        enabled = enabled,
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = {
                            dismiss(onClick)
                        },
                        role = Role.Button,
                    ),
                contentAlignment = Alignment.Center,
                propagateMinConstraints = true,
                content = {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(animatedBackground)
                            .drawWithContent {
                                if (highlightAlpha > 0f) {
                                    drawRect(selection.copy(alpha = selection.alpha * highlightAlpha))
                                }
                                drawContent()
                            }
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        val s =
                            CupertinoTheme.typography.body.copy(
                                color = buttonContentColor,
                                fontWeight = if (isPrimary) FontWeight.SemiBold else FontWeight.Normal,
                                textAlign = TextAlign.Center,
                            )
                        ProvideTextStyle(
                            s,
                        ) {
                            CompositionLocalProvider(
                                LocalContentColor provides LocalTextStyle.current.color,
                            ) {
                                title()
                            }
                        }
                    }
                },
            )
        })
    }

    @Composable
    fun Content() {
        if (buttons.isEmpty()) return
        val density = LocalDensity.current
        val fontScale = density.fontScale
        val gapPx = with(density) { CupertinoDialogsTokens.AlertDialogButtonSpacing.roundToPx() }
        val minButtonHeight = with(density) { CupertinoDialogsTokens.AlertDialogButtonHeight.roundToPx() }
        SubcomposeLayout { constraints ->
            val orderedButtons =
                if (orientation == Orientation.Horizontal) {
                    buttons.filter { it.first == AlertActionStyle.Cancel } +
                        buttons.filter { it.first != AlertActionStyle.Cancel }
                } else if (!emphasizeDefaultAction) {
                    buttons.filter { it.first == AlertActionStyle.Destructive } +
                        buttons.filter { it.first == AlertActionStyle.Default } +
                        buttons.filter { it.first == AlertActionStyle.Cancel }
                } else {
                    buttons.filter { it.first != AlertActionStyle.Cancel } +
                        buttons.filter { it.first == AlertActionStyle.Cancel }
                }
            val primaryIndex = orderedButtons.indexOfLast { it.first == AlertActionStyle.Default }
            val actions =
                orderedButtons.mapIndexed { index, button ->
                    subcompose("action-$index") { button.second(emphasizeDefaultAction && index == primaryIndex) }.single()
                }
            val preferredWidths = actions.map { it.maxIntrinsicWidth(constraints.maxHeight) }
            val width =
                if (constraints.maxWidth == Constraints.Infinity) {
                    val contentWidth =
                        if (orientation == Orientation.Horizontal) preferredWidths.sum() + gapPx * (actions.size - 1)
                        else preferredWidths.maxOrNull() ?: 0
                    maxOf(constraints.minWidth, contentWidth)
                } else {
                    constraints.maxWidth
                }
            val canUseHorizontal =
                orientation == Orientation.Horizontal &&
                    actions.size <= 2 &&
                    fontScale <= AlertDialogHorizontalFontScaleLimit &&
                    preferredWidths.maxOrNull()?.times(actions.size)?.plus(
                        gapPx * (actions.size - 1),
                    )?.let { it <= width } == true
            val actionWidths =
                if (canUseHorizontal) {
                    val cellWidth = (width - gapPx * (actions.size - 1)) / actions.size
                    List(actions.size) { cellWidth }
                } else {
                    List(actions.size) { width }
                }
            val actionPlaceables =
                actions.mapIndexed { index, action ->
                    val actionWidth = actionWidths[index]
                    action.measure(
                        Constraints(
                            minWidth = actionWidth,
                            maxWidth = actionWidth,
                            minHeight = minButtonHeight,
                            maxHeight = constraints.maxHeight.coerceAtLeast(minButtonHeight),
                        ),
                    )
                }
            val height =
                if (canUseHorizontal) {
                    actionPlaceables.maxOf { it.height }
                } else {
                    actionPlaceables.sumOf { it.height } + gapPx * (actionPlaceables.size - 1)
                }
            layout(width, height) {
                if (canUseHorizontal) {
                    var x = 0
                    actionPlaceables.forEachIndexed { index, placeable ->
                        placeable.placeRelative(x, 0)
                        x += placeable.width + gapPx
                    }
                } else {
                    var y = 0
                    actionPlaceables.forEachIndexed { _, placeable ->
                        placeable.placeRelative(0, y)
                        y += placeable.height + gapPx
                    }
                }
            }
        }
    }
}

private class CupertinoActionSheetImpl(
    private val hasTitle: Boolean,
    private val primaryContainerColor: Color,
    private val secondaryContainerColor: Color,
    private val backdrop: LayerBackdrop?,
): AlertDialogActionsScope {
    private val buttons = mutableListOf<Pair<AlertActionStyle, @Composable () -> Unit>>()
    val hasPickerActions: Boolean
        get() =
            buttons.size == 2 &&
                buttons.count { it.first == AlertActionStyle.Cancel } == 1 &&
                buttons.count { it.first == AlertActionStyle.Default } == 1
    private var pickerToolbarActive = false

    @OptIn(ExperimentalCupertinoApi::class)
    override fun action(
        onClick: () -> Unit,
        style: AlertActionStyle,
        enabled: Boolean,
        title: @Composable () -> Unit,
    ) {
        buttons.add(
            style to {
                if (pickerToolbarActive && style == AlertActionStyle.Default) {
                    val buttonBackdrop = backdrop ?: rememberDefaultBackdrop()
                    CupertinoLiquidButton(
                        onClick = onClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(CupertinoDialogsTokens.PickerActionHeight),
                        enabled = enabled,
                        colors =
                            CupertinoLiquidButtonDefaults.glassProminentButtonColors(
                                lightTintColor = CupertinoTheme.colorScheme.accent,
                                darkTintColor = CupertinoTheme.colorScheme.accent,
                            ),
                        shape = CircleShape,
                        backdrop = buttonBackdrop,
                    ) {
                        ProvideTextStyle(
                            CupertinoTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                            content = title,
                        )
                    }
                } else {
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.98f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
                    )
                    val highlightAlpha by animateFloatAsState(
                        targetValue = if (isPressed) 1f else 0f,
                        animationSpec = tween(100),
                    )
                    val selection = CupertinoGlassDefaults.selection
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .heightIn(
                                    min =
                                        if (pickerToolbarActive) {
                                            CupertinoDialogsTokens.PickerActionHeight
                                        } else {
                                            CupertinoDialogsTokens.ActionSheetButtonHeight
                                        },
                                )
                                .drawWithContent {
                                    if (highlightAlpha > 0f) {
                                        drawRect(selection.copy(alpha = selection.alpha * highlightAlpha))
                                    }
                                    drawContent()
                                }
                                .clickable(
                                    enabled = enabled,
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = onClick,
                                    role = Role.Button,
                                ),
                        contentAlignment = Alignment.Center,
                        content = {
                            Box(
                                Modifier.graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                },
                            ) {
                                val s =
                                    style.apply(
                                        if (pickerToolbarActive) {
                                            CupertinoTheme.typography.body
                                        } else {
                                            CupertinoTheme.typography.title3
                                        },
                                        isDark(),
                                    )
                                ProvideTextStyle(
                                    s.copy(
                                        fontWeight =
                                            when {
                                                pickerToolbarActive && style == AlertActionStyle.Cancel -> FontWeight.Normal
                                                style == AlertActionStyle.Cancel -> FontWeight.SemiBold
                                                else -> FontWeight.Normal
                                            },
                                        color =
                                            when {
                                                !enabled -> CupertinoTheme.colorScheme.tertiaryLabel
                                                pickerToolbarActive && style == AlertActionStyle.Cancel ->
                                                    CupertinoTheme.colorScheme.label
                                                else -> s.color
                                            },
                                    ),
                                ) {
                                    CompositionLocalProvider(
                                        LocalContentColor provides LocalTextStyle.current.color,
                                    ) {
                                        title()
                                    }
                                }
                            }
                        },
                    )
                }
            },
        )
    }

    @Composable
    fun Content(
        title: (@Composable ColumnScope.() -> Unit)? = null,
        pickerTitle: (@Composable () -> Unit)? = null,
        pickerContent: (@Composable () -> Unit)? = null,
        pickerMessage: (@Composable () -> Unit)? = null,
    ) {
        val actionSheetShape = CupertinoDialogsDefaults.Shape
        pickerToolbarActive = pickerContent != null && hasPickerActions
        val surfaceShape =
            if (pickerToolbarActive) {
                CupertinoDialogsTokens.PickerSheetShape
            } else {
                actionSheetShape
            }
        val surfaceInset =
            if (pickerToolbarActive) {
                CupertinoDialogsTokens.PickerSheetInset
            } else {
                CupertinoDialogsTokens.ActionSheetSidePadding
            }
        val actionSheetInsets =
            if (pickerToolbarActive) {
                WindowInsets.navigationBars.union(
                    WindowInsets(bottom = CupertinoDialogsTokens.PickerSheetBottomInset),
                )
            } else {
                CupertinoDialogsTokens.ActionSheetWindowInsets
            }
        CompositionLocalProvider(
            LocalSeparatorColor provides BrightSeparatorColor,
        ) {
            Column(
                modifier =
                    Modifier
                        .windowInsetsPadding(actionSheetInsets),
            ) {
                CupertinoSurface(
                    modifier =
                        Modifier
                            .padding(
                                start = surfaceInset,
                                end = surfaceInset,
                                top = CupertinoDialogsTokens.ActionSheetSidePadding,
                            )
                            .then(
                                if (backdrop != null) {
                                    Modifier.drawBackdrop(
                                        backdrop = backdrop,
                                        shape = { surfaceShape },
                                        effects = { blur(CupertinoGlassDefaults.blurRadius.toPx()) },
                                        highlight = { Highlight.Plain },
                                        onDrawSurface = { drawRect(primaryContainerColor) },
                                    )
                                } else {
                                    Modifier
                                },
                            ).glassEdge(surfaceShape),
                    shape = surfaceShape,
                    color = if (backdrop == null) primaryContainerColor else Color.Transparent,
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth(),
                    ) {
                        if (pickerToolbarActive) {
                            val cancelAction = buttons.first { it.first == AlertActionStyle.Cancel }
                            val primaryAction = buttons.first { it.first == AlertActionStyle.Default }
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = CupertinoDialogsTokens.PickerToolbarHeight)
                                        .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(Modifier.weight(1f)) {
                                    cancelAction.second()
                                }
                                Box(
                                    Modifier.weight(1.5f)
                                        .clipToBounds()
                                        .basicMarquee(iterations = Int.MAX_VALUE, repeatDelayMillis = 3000, initialDelayMillis = 2500, velocity = 24.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    pickerTitle?.let { titleContent ->
                                        ProvideTextStyle(
                                            CupertinoTheme.typography.headline.copy(
                                                textAlign = TextAlign.Center,
                                            ),
                                            content = titleContent,
                                        )
                                    }
                                }
                                Box(Modifier.weight(1f)) {
                                    primaryAction.second()
                                }
                            }
                            pickerMessage?.let { messageContent ->
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    ProvideTextStyle(
                                        CupertinoTheme.typography.footnote.copy(
                                            textAlign = TextAlign.Center,
                                        ),
                                        content = messageContent,
                                    )
                                }
                            }
                            CupertinoHorizontalDivider()
                            pickerContent?.let { picker ->
                                CompositionLocalProvider(
                                    LocalContainerColor provides primaryContainerColor,
                                    LocalCupertinoSheetSurfaceDrawn provides true,
                                    content = picker,
                                )
                            }
                        } else {
                            title?.invoke(this)

                            buttons
                                .filter { it.first != AlertActionStyle.Cancel }
                                .fastForEachIndexed { i, btn ->
                                    if (i > 0 || hasTitle) {
                                        CupertinoHorizontalDivider()
                                    }
                                    btn.second()
                                }
                        }
                    }
                }

                if (!pickerToolbarActive) {
                    buttons
                        .filter { it.first == AlertActionStyle.Cancel }
                        .fastForEach {
                            CupertinoSurface(
                                modifier =
                                    Modifier
                                        .padding(
                                            start = CupertinoDialogsTokens.ActionSheetSidePadding,
                                            end = CupertinoDialogsTokens.ActionSheetSidePadding,
                                            top = CupertinoDialogsTokens.ActionSheetSidePadding,
                                        )
                                        .then(
                                            if (backdrop != null) {
                                                Modifier.drawBackdrop(
                                                    backdrop = backdrop,
                                                    shape = { actionSheetShape },
                                                    effects = { blur(CupertinoGlassDefaults.blurRadius.toPx()) },
                                                    highlight = { Highlight.Plain },
                                                    onDrawSurface = { drawRect(secondaryContainerColor) },
                                                )
                                            } else {
                                                Modifier
                                            },
                                        ).glassEdge(actionSheetShape),
                                shape = actionSheetShape,
                                color = if (backdrop == null) secondaryContainerColor else Color.Transparent,
                            ) {
                                it.second()
                            }
                        }
                }
            }
        }
    }
}

private fun <T> Transition.Segment<Boolean>.sheetAnimation(): FiniteAnimationSpec<T> =
    if (true isTransitioningTo false) {
        spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow,
        )
    } else {
        cupertinoTween()
    }

internal object CupertinoDialogsTokens {
    val AlertDialogElevation: Dp = 0.dp
    val AlertDialogOuterPadding: Dp = 14.dp
    val AlertDialogHeaderPadding = PaddingValues(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 24.dp)
    val AlertDialogWidth: Dp = 300.dp
    val AlertDialogCornerRadius: Dp = 34.dp
    val AlertDialogBlurRadius: Dp = 30.dp
    val AlertDialogMinHeight: Dp = 110.dp
    val AlertDialogTitleMessageSpacing: Dp = 10.dp
    val AlertDialogButtonHeight: Dp = 48.dp
    val AlertDialogButtonSpacing: Dp = 8.dp

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
    val PickerSheetInset = 14.dp
    val PickerSheetBottomInset = 12.dp
    val PickerSheetShape = ContinuousRoundedRectangle(34.dp)
    val ActionSheetButtonHeight: Dp = 56.dp
    val PickerActionHeight: Dp = 44.dp
    val PickerToolbarHeight: Dp = 60.dp
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

private const val AlertDialogHorizontalFontScaleLimit = 1.2f
