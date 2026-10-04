package zone.ien.hig

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.Shadow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import zone.ien.hig.CupertinoLiquidButtonDefaults.glassButtonColors
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.theme.darkColorScheme
import zone.ien.hig.theme.lightColorScheme
import zone.ien.hig.utils.InteractiveHighlight
import zone.ien.hig.utils.CupertinoGlassDefaults
import zone.ien.hig.utils.glassEdge
import kotlin.math.sign
import kotlin.time.Duration.Companion.milliseconds
import androidx.compose.animation.Animatable as ColorAnimatable
import androidx.compose.animation.core.Animatable as FloatAnimatable

/**
 * A composable function that creates a Cupertino-styled liquid button with backdrop effects.
 * 
 * This button features liquid glass-like effects with adaptive tinting and surface colors that 
 * respond to the background theme and content luminance.
 * 
 * @param onClick The action to perform when the button is clicked
 * @param modifier Modifier to be applied to the button
 * @param enabled Whether the button is enabled and interactive
 * @param size The size of the button (Small, Regular, Large, ExtraLarge)
 * @param colors The colors to use for the button appearance
 * @param shape The shape of the button
 * @param contentPadding The padding to apply to the button content
 * @param interactionSource The interaction source to track button interactions
 * @param backdrop The backdrop effect configuration for the button's visual appearance
 * @param isBackgroundAdaptive Whether the button should adapt to the background color
 * @param isInteractive Whether the button responds to interactive gestures
 * @param content The content to display inside the button
 * 
 * Sample usage:
 * ```
 * CupertinoLiquidButton(onClick = { /* handle click */ }, backdrop = backdrop) {
 *     Text("Button")
 * }
 * ```
 */
@Composable
@ExperimentalCupertinoApi
fun CupertinoLiquidButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: CupertinoButtonSize = CupertinoButtonSize.Regular,
    colors: CupertinoLiquidButtonColors = glassButtonColors(),
    shape: Shape = size.shape(CupertinoTheme.shapes),
    contentPadding: PaddingValues = size.contentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    backdrop: Backdrop,
    isBackgroundAdaptive: Boolean = true,
    isInteractive: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val animationScope = rememberCoroutineScope()

    val tintColor by colors.tintColor(enabled)
    val surfaceColor by colors.surfaceColor(enabled)
    val contentColor by colors.contentColor(enabled)

    val lightContentColor by colors.contentColor(enabled, isDark = false)
    val darkContentColor by colors.contentColor(enabled, isDark = true)

    val interactiveHighlight = remember(animationScope) { InteractiveHighlight(animationScope = animationScope) }

    LaunchedEffect(interactionSource, enabled, isInteractive) {
        if (!enabled || !isInteractive) {
            interactiveHighlight.release()
            return@LaunchedEffect
        }
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> interactiveHighlight.press()
                is PressInteraction.Release, is PressInteraction.Cancel -> interactiveHighlight.release()
            }
        }
    }

    val isLightTheme = !CupertinoTheme.colorScheme.isDark
    val graphicsLayer = rememberGraphicsLayer()
    val targetGraphicsLayer = rememberGraphicsLayer()

    val materialTint = CupertinoGlassDefaults.tint
    val luminanceAnimation = remember(enabled, isLightTheme) { FloatAnimatable(if (isLightTheme) 1f else 0f) }
    val contentColorAnimation = remember(enabled) { ColorAnimatable(if (isLightTheme) lightContentColor else darkContentColor) }
    val updatedLightContent by rememberUpdatedState(lightContentColor)
    val updatedDarkContent by rememberUpdatedState(darkContentColor)

    LaunchedEffect(isLightTheme, lightContentColor, darkContentColor) {
        contentColorAnimation.animateTo(contentColor, tween(180))
    }

    if (isBackgroundAdaptive) {
        val defaultColor = CupertinoTheme.colorScheme.systemBackground
        LaunchedEffect(graphicsLayer, isLightTheme, enabled) {
            while (isActive) {
                if (graphicsLayer.size != IntSize.Zero) {
                    try {
                        val averageLuminance = graphicsLayer.toImageBitmap().averageLuminance(sampleWidth = 5, defaultColor = defaultColor)

                        launch {
                            contentColorAnimation.animateTo(
                                if (averageLuminance > 0.5f) updatedLightContent else updatedDarkContent,
                                tween(300)
                            )
                        }
                        luminanceAnimation.animateTo(
                            averageLuminance.toFloat(),
                            tween(300)
                        )
                    } catch (e: RuntimeException) {
                        // When layer is disposed → exit loop
                        break
                    }
                }

                delay(300.milliseconds)
            }
        }
    }

    Box(
        modifier = modifier
            .heightIn(min = if (size == CupertinoButtonSize.Small) 32.dp else 44.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .drawWithContent {
                    targetGraphicsLayer.record {
                        this@drawWithContent.drawContent()
                    }
                }
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { shape },
                    effects = {
                        val l = (luminanceAnimation.value * 2f - 1f).let { sign(it) * it * it }
                        vibrancy()
                        if (isBackgroundAdaptive) {
                            blur(
                                if (l > 0f) lerp(10.dp.toPx(), 18.dp.toPx(), l)
                                else lerp(16.dp.toPx(), 10.dp.toPx(), -l)
                            )
                        } else {
                            blur(CupertinoGlassDefaults.blurRadius.toPx())
                        }
                        if (shape is CornerBasedShape) {
                            val progress = if (enabled && isInteractive) interactiveHighlight.pressProgress.coerceIn(0f, 1f) else 0f
                            lens(4.dp.toPx(), lerp(8.dp.toPx(), 16.dp.toPx(), progress))
                        }
                    },
                    layerBlock = if (enabled && isInteractive) interactiveHighlight.layerBlock else null,
                    highlight = { Highlight.Default.copy(alpha = 0.65f) },
                    shadow = { Shadow(radius = 8.dp, color = Color.Black.copy(alpha = 0.06f)) },
                    onDrawSurface = {
                        if (tintColor.isSpecified) {
                            drawRect(tintColor, blendMode = BlendMode.Hue)
                            drawRect(tintColor.copy(alpha = tintColor.alpha * 0.94f))
                        } else {
                            drawRect(materialTint)
                        }
                        if (surfaceColor.isSpecified) {
                            drawRect(surfaceColor)
                        }
                    },
                    onDrawBackdrop = { drawBackdrop ->
                        drawBackdrop()
                        graphicsLayer.record { drawBackdrop() }
                    }
                )
                .glassEdge(shape)
                .then(if (enabled) Modifier.indication(interactionSource, LocalIndication.current) else Modifier)
                .then(
                    if (enabled && isInteractive) {
                        interactiveHighlight.modifier.then(interactiveHighlight.gestureModifier)
                    } else {
                        Modifier
                    }
                )
                .heightIn(min = if (size == CupertinoButtonSize.Small) 32.dp else 44.dp)
                .padding(contentPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            content = {
                CompositionLocalProvider(
                    LocalContentColor provides if (isBackgroundAdaptive) contentColorAnimation.value else contentColor,
                ) {
                    ProvideTextStyle(size.textStyle(CupertinoTheme.typography)) {
                        content()
                    }
                }
            }
        )

        Canvas(
            Modifier.matchParentSize().then(
                if (enabled && isInteractive) Modifier.graphicsLayer(interactiveHighlight.layerBlock) else Modifier,
            ),
        ) {
            translate(
                left = (this.size.width - targetGraphicsLayer.size.width) / 2f,
                top = (this.size.height - targetGraphicsLayer.size.height) / 2f,
            ) {
                drawLayer(targetGraphicsLayer)
            }
        }
    }
}

@ExperimentalCupertinoApi
@Composable
fun CupertinoLiquidIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CupertinoLiquidButtonColors = glassButtonColors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    backdrop: Backdrop,
    isBackgroundAdaptive: Boolean = true,
    isInteractive: Boolean = true,
    content: @Composable () -> Unit
) {
    CupertinoLiquidButton(
        onClick = onClick,
        modifier = modifier.size(CupertinoLiquidButtonTokens.IconButtonSize),
        enabled = enabled,
        colors = colors,
        size = CupertinoButtonSize.Regular,
        shape = CircleShape,
        interactionSource = interactionSource,
        contentPadding = PaddingValues(8.dp),
        backdrop = backdrop,
        isBackgroundAdaptive = isBackgroundAdaptive,
        isInteractive = isInteractive,
        content = {
            Box(
                modifier = Modifier.size(32.dp),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    )
}

@Immutable
class CupertinoLiquidButtonColors internal constructor(
    private val lightTintColor: Color,
    private val lightSurfaceColor: Color,
    private val lightContentColor: Color,
    private val darkTintColor: Color,
    private val darkSurfaceColor: Color,
    private val darkContentColor: Color,
    private val disabledLightTintColor: Color,
    private val disabledLightSurfaceColor: Color,
    private val disabledLightContentColor: Color,
    private val disabledDarkTintColor: Color,
    private val disabledDarkSurfaceColor: Color,
    private val disabledDarkContentColor: Color,
    internal val lightIndicationColor: Color,
    internal val darkIndicationColor: Color
) {
    /**
     * Represents the tint color for this button, depending on [enabled].
     *
     * @params enabled whether the button is enabled
     */
    @Composable
    fun tintColor(enabled: Boolean, isDark: Boolean = CupertinoTheme.colorScheme.isDark): State<Color> {
        return rememberUpdatedState(
            if (isDark) {
                if (enabled) darkTintColor else disabledDarkTintColor
            } else {
                if (enabled) lightTintColor else disabledLightTintColor
            }
        )
    }

    /**
     * Represents the surface color for this button, depending on [enabled].
     *
     * @params enabled whether the button is enabled
     */
    @Composable
    fun surfaceColor(enabled: Boolean, isDark: Boolean = CupertinoTheme.colorScheme.isDark): State<Color> {
        return rememberUpdatedState(
            if (isDark) {
                if (enabled) darkSurfaceColor else disabledDarkSurfaceColor
            } else {
                if (enabled) lightSurfaceColor else disabledLightSurfaceColor
            }
        )
    }

    /**
     * Represents the content color for this button, depending on [enabled].
     *
     * @params enabled whether the button is enabled
     */
    @Composable
    fun contentColor(enabled: Boolean, isDark: Boolean = CupertinoTheme.colorScheme.isDark): State<Color> {
        return rememberUpdatedState(
            if (isDark) {
                if (enabled) darkContentColor else disabledDarkContentColor
            } else {
                if (enabled) lightContentColor else disabledLightContentColor
            }
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is CupertinoLiquidButtonColors) return false

        if (lightTintColor != other.lightTintColor) return false
        if (lightSurfaceColor != other.lightSurfaceColor) return false
        if (lightContentColor != other.lightContentColor) return false
        if (darkTintColor != other.darkTintColor) return false
        if (darkSurfaceColor != other.darkSurfaceColor) return false
        if (darkContentColor != other.darkContentColor) return false
        if (disabledLightTintColor != other.disabledLightTintColor) return false
        if (disabledLightSurfaceColor != other.disabledLightSurfaceColor) return false
        if (disabledLightContentColor != other.disabledLightContentColor) return false
        if (disabledDarkTintColor != other.disabledDarkTintColor) return false
        if (disabledDarkSurfaceColor != other.disabledDarkSurfaceColor) return false
        if (disabledDarkContentColor != other.disabledDarkContentColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = lightTintColor.hashCode()
        result = 31 * result + lightSurfaceColor.hashCode()
        result = 31 * result + lightContentColor.hashCode()
        result = 31 * result + darkTintColor.hashCode()
        result = 31 * result + darkSurfaceColor.hashCode()
        result = 31 * result + darkContentColor.hashCode()
        result = 31 * result + disabledLightTintColor.hashCode()
        result = 31 * result + disabledLightSurfaceColor.hashCode()
        result = 31 * result + disabledLightContentColor.hashCode()
        result = 31 * result + disabledDarkTintColor.hashCode()
        result = 31 * result + disabledDarkSurfaceColor.hashCode()
        result = 31 * result + disabledDarkContentColor.hashCode()
        return result
    }
}

@Immutable
object CupertinoLiquidButtonDefaults {
    /**
     * Filled button with .glassProminent SwiftUI style
     */
    @Composable
    @ReadOnlyComposable
    fun glassProminentButtonColors(
        lightTintColor: Color = CupertinoTheme.colorScheme.accent,
        lightSurfaceColor: Color = Color.Unspecified,
        lightContentColor: Color = CupertinoGlassDefaults.contentColor(lightTintColor, lightColorScheme().systemBackground),
        darkTintColor: Color = CupertinoTheme.colorScheme.accent,
        darkSurfaceColor: Color = Color.Unspecified,
        darkContentColor: Color = CupertinoGlassDefaults.contentColor(darkTintColor, darkColorScheme().systemBackground),
        disabledLightTintColor: Color = Color.Unspecified,
        disabledLightSurfaceColor: Color = lightColorScheme().systemFill,
        disabledLightContentColor: Color = lightColorScheme().tertiaryLabel,
        disabledDarkTintColor: Color = Color.Unspecified,
        disabledDarkSurfaceColor: Color = darkColorScheme().systemFill,
        disabledDarkContentColor: Color = darkColorScheme().tertiaryLabel,
        lightIndicationColor: Color = lightContentColor.copy(alpha = 0.2f),
        darkIndicationColor: Color = darkContentColor.copy(alpha = 0.2f)
    ): CupertinoLiquidButtonColors = CupertinoLiquidButtonColors(
        lightTintColor = lightTintColor,
        lightSurfaceColor = lightSurfaceColor,
        lightContentColor = lightContentColor,
        darkTintColor = darkTintColor,
        darkSurfaceColor = darkSurfaceColor,
        darkContentColor = darkContentColor,
        disabledLightTintColor = disabledLightTintColor,
        disabledLightSurfaceColor = disabledLightSurfaceColor,
        disabledLightContentColor = disabledLightContentColor,
        disabledDarkTintColor = disabledDarkTintColor,
        disabledDarkSurfaceColor = disabledDarkSurfaceColor,
        disabledDarkContentColor = disabledDarkContentColor,
        lightIndicationColor = lightIndicationColor,
        darkIndicationColor = darkIndicationColor
    )

    /**
     * Transparent button with .glass SwiftUI style
     */
    @Composable
    @ReadOnlyComposable
    fun glassButtonColors(
        lightTintColor: Color = Color.Unspecified,
        lightSurfaceColor: Color = Color.Unspecified,
        lightContentColor: Color = lightColorScheme().label,
        darkTintColor: Color = Color.Unspecified,
        darkSurfaceColor: Color = Color.Unspecified,
        darkContentColor: Color = darkColorScheme().label,
        disabledLightTintColor: Color = Color.Unspecified,
        disabledLightSurfaceColor: Color = Color.Unspecified,
        disabledLightContentColor: Color = lightColorScheme().tertiaryLabel,
        disabledDarkTintColor: Color = Color.Unspecified,
        disabledDarkSurfaceColor: Color = Color.Unspecified,
        disabledDarkContentColor: Color = darkColorScheme().tertiaryLabel,
        lightIndicationColor: Color = lightContentColor.copy(alpha = 0.2f),
        darkIndicationColor: Color = darkContentColor.copy(alpha = 0.2f)
    ): CupertinoLiquidButtonColors = CupertinoLiquidButtonColors(
        lightTintColor = lightTintColor,
        lightSurfaceColor = lightSurfaceColor,
        lightContentColor = lightContentColor,
        darkTintColor = darkTintColor,
        darkSurfaceColor = darkSurfaceColor,
        darkContentColor = darkContentColor,
        disabledLightTintColor = disabledLightTintColor,
        disabledLightSurfaceColor = disabledLightSurfaceColor,
        disabledLightContentColor = disabledLightContentColor,
        disabledDarkTintColor = disabledDarkTintColor,
        disabledDarkSurfaceColor = disabledDarkSurfaceColor,
        disabledDarkContentColor = disabledDarkContentColor,
        lightIndicationColor = lightIndicationColor,
        darkIndicationColor = darkIndicationColor
    )
}

internal object CupertinoLiquidButtonTokens {
    const val PressedPlainButonAlpha = .33f
    val IconButtonSize = 48.dp
    const val BorderedButtonAlpha = .2f
}

private val ZeroPadding = PaddingValues(0.dp)
