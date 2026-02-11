package zone.ien.hig

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceAtMost
import androidx.compose.ui.util.lerp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.utils.InteractiveHighlight
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tanh

@Composable
@ExperimentalCupertinoApi
fun CupertinoLiquidButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: CupertinoButtonSize = CupertinoButtonSize.Regular,
    colors: CupertinoLiquidButtonColors = CupertinoLiquidButtonDefaults.glassProminentButtonColors(),
    shape: Shape = size.shape(CupertinoTheme.shapes),
    contentPadding: PaddingValues = size.contentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    backdrop: Backdrop,
//    isInteractive: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val animationScope = rememberCoroutineScope()
    val tintColor by colors.tintColor(enabled)
    val surfaceColor by colors.surfaceColor(enabled)
    val contentColor by colors.contentColor(enabled)

    val interactiveHighlight = remember(animationScope) {
        InteractiveHighlight(
            animationScope = animationScope
        )
    }

    Row(
        modifier
            .drawBackdrop(
                backdrop = backdrop,
                shape = { shape },
                effects = {
                    vibrancy()
                    blur(2.dp.toPx())
                    lens(12.dp.toPx(), 24.dp.toPx())
                },
                layerBlock = if (enabled) {
                    {
                        val width = this.size.width
                        val height = this.size.height

                        val progress = interactiveHighlight.pressProgress
                        val scale = lerp(1f, 1f + 4.dp.toPx() / height, progress)

                        val maxOffset = this.size.minDimension
                        val initialDerivative = 0.05f
                        val offset = interactiveHighlight.offset
                        translationX = maxOffset * tanh(initialDerivative * offset.x / maxOffset)
                        translationY = maxOffset * tanh(initialDerivative * offset.y / maxOffset)

                        val maxDragScale = 4.dp.toPx() / height
                        val offsetAngle = atan2(offset.y, offset.x)
                        scaleX =
                            scale +
                                    maxDragScale * abs(cos(offsetAngle) * offset.x / this.size.maxDimension) *
                                    (width / height).fastCoerceAtMost(1f)
                        scaleY =
                            scale +
                                    maxDragScale * abs(sin(offsetAngle) * offset.y / this.size.maxDimension) *
                                    (height / width).fastCoerceAtMost(1f)
                    }
                } else {
                    null
                },
                onDrawSurface = {
                    if (tintColor.isSpecified) {
                        drawRect(tintColor, blendMode = BlendMode.Hue)
                        drawRect(tintColor.copy(alpha = 0.75f))
                    }
                    if (surfaceColor.isSpecified) {
                        drawRect(surfaceColor)
                    }
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = if (enabled) null else LocalIndication.current,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            )
            .then(
                if (enabled) {
                    Modifier
                        .then(interactiveHighlight.modifier)
                        .then(interactiveHighlight.gestureModifier)
                } else {
                    Modifier
                }
            )
            .height(48.dp)
            .padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        content = {
            CompositionLocalProvider(
                LocalContentColor provides contentColor,
            ) {
               content()
            }
        }
    )
}

@Immutable
class CupertinoLiquidButtonColors internal constructor(
    private val tintColor: Color,
    private val surfaceColor: Color,
    private val contentColor: Color,
    private val disabledTintColor: Color,
    private val disabledSurfaceColor: Color,
    private val disabledContentColor: Color,
    internal val indicationColor: Color,
) {
    /**
     * Represents the tint color for this button, depending on [enabled].
     *
     * @params enabled whether the button is enabled
     */
    @Composable
    fun tintColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(if (enabled) tintColor else disabledTintColor)
    }

    /**
     * Represents the surface color for this button, depending on [enabled].
     *
     * @params enabled whether the button is enabled
     */
    @Composable
    fun surfaceColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(if (enabled) surfaceColor else disabledSurfaceColor)
    }

    /**
     * Represents the content color for this button, depending on [enabled].
     *
     * @params enabled whether the button is enabled
     */
    @Composable
    fun contentColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(if (enabled) contentColor else disabledContentColor)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is CupertinoLiquidButtonColors) return false

        if (tintColor != other.tintColor) return false
        if (surfaceColor != other.surfaceColor) return false
        if (contentColor != other.contentColor) return false
        if (disabledTintColor != other.disabledTintColor) return false
        if (disabledSurfaceColor != other.disabledSurfaceColor) return false
        if (disabledContentColor != other.disabledContentColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tintColor.hashCode()
        result = 31 * result + surfaceColor.hashCode()
        result = 31 * result + contentColor.hashCode()
        result = 31 * result + disabledTintColor.hashCode()
        result = 31 * result + disabledSurfaceColor.hashCode()
        result = 31 * result + disabledContentColor.hashCode()
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
        tintColor: Color = CupertinoTheme.colorScheme.accent,
        surfaceColor: Color = Color.Unspecified,
        contentColor: Color = Color.White,
        disabledTintColor: Color = Color.Unspecified,
        disabledSurfaceColor: Color = CupertinoTheme.colorScheme.systemFill,
        disabledContentColor: Color = CupertinoTheme.colorScheme.tertiaryLabel,
        indicationColor: Color = contentColor.copy(alpha = 0.2f)
    ): CupertinoLiquidButtonColors = CupertinoLiquidButtonColors(
        tintColor = tintColor,
        surfaceColor = surfaceColor,
        contentColor = contentColor,
        disabledTintColor = disabledTintColor,
        disabledSurfaceColor = disabledSurfaceColor,
        disabledContentColor = disabledContentColor,
        indicationColor = indicationColor
    )

    /**
     * Transparent button with .glass SwiftUI style
     */
    @Composable
    @ReadOnlyComposable
    fun glassButtonColors(
        tintColor: Color = Color.Unspecified,
        surfaceColor: Color = Color.Unspecified,
        contentColor: Color = CupertinoTheme.colorScheme.label,
        disabledTintColor: Color = Color.Unspecified,
        disabledSurfaceColor: Color = Color.Unspecified,
        disabledContentColor: Color = CupertinoTheme.colorScheme.tertiaryLabel,
        indicationColor: Color = contentColor.copy(alpha = 0.2f)
    ): CupertinoLiquidButtonColors = CupertinoLiquidButtonColors(
        tintColor = tintColor,
        surfaceColor = surfaceColor,
        contentColor = contentColor,
        disabledTintColor = disabledTintColor,
        disabledSurfaceColor = disabledSurfaceColor,
        disabledContentColor = disabledContentColor,
        indicationColor = indicationColor
    )
}

internal object CupertinoLiquidButtonTokens {
    const val PressedPlainButonAlpha = .33f
    val IconButtonSize = 42.dp
    const val BorderedButtonAlpha = .2f
}

private val ZeroPadding = PaddingValues(0.dp)