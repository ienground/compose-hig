package zone.ien.hig.utils

import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.runtime.MutableState
import androidx.compose.ui.layout.LayoutCoordinates
import com.kyant.backdrop.Backdrop
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Matrix
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.RectangleShape
import com.kyant.backdrop.BackdropEffectScope
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.highlight.Highlight
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import zone.ien.hig.Accessibility
import zone.ien.hig.isHighContrastEnabled
import zone.ien.hig.isReduceTransparencyEnabled
import zone.ien.hig.theme.CupertinoTheme

internal val LocalCupertinoBackdrop = staticCompositionLocalOf<LayerBackdrop?> { null }

internal val LocalCupertinoDialogBackdrop = staticCompositionLocalOf<LayerBackdrop?> { null }

@Composable
fun rememberDefaultBackdrop(): LayerBackdrop {
    val background = CupertinoTheme.colorScheme.systemBackground
    return rememberLayerBackdrop {
        drawRect(background)
        drawContent()
    }
}

internal object CupertinoGlassDefaults {
    val blurRadius = 16.dp

    val tint: Color
        @Composable
        @ReadOnlyComposable
        get() = if (Accessibility.isReduceTransparencyEnabled) {
            opaqueMaterial
        } else if (CupertinoTheme.colorScheme.isDark) {
            Color(0xFF6C7076).copy(alpha = 0.36f)
        } else {
            Color.White.copy(alpha = 0.38f)
        }

    val panelTint: Color
        @Composable
        @ReadOnlyComposable
        get() = if (Accessibility.isReduceTransparencyEnabled) {
            opaqueMaterial
        } else if (CupertinoTheme.colorScheme.isDark) {
            Color(0xFF3A3D43).copy(alpha = 0.86f)
        } else {
            Color(0xFFF7F7F9).copy(alpha = 0.82f)
        }

    private val opaqueMaterial: Color
        @Composable
        @ReadOnlyComposable
        get() = if (CupertinoTheme.colorScheme.isDark) Color(0xFF383A3F) else Color(0xFFF2F2F7)

    val border: Color
        @Composable
        @ReadOnlyComposable
        get() = if (CupertinoTheme.colorScheme.isDark) {
            Color.White.copy(alpha = if (Accessibility.isHighContrastEnabled) 0.44f else 0.22f)
        } else {
            Color.Black.copy(alpha = if (Accessibility.isHighContrastEnabled) 0.28f else 0.16f)
        }

    val selection: Color
        @Composable
        @ReadOnlyComposable
        get() = if (CupertinoTheme.colorScheme.isDark) {
            Color.Black.copy(alpha = 0.38f)
        } else {
            Color.Black.copy(alpha = 0.06f)
        }

    fun contentColor(background: Color, underlay: Color = Color.White): Color {
        val visibleBackground =
            if (background.isSpecified) background.compositeOver(underlay) else underlay
        val whiteContrast = 1.05f / (visibleBackground.luminance() + 0.05f)
        return if (whiteContrast >= 3f) Color.White else Color.Black
    }
}

@Composable
internal fun Modifier.glassEdge(shape: Shape): Modifier {
    val edge = CupertinoGlassDefaults.border
    val highlight = Color.White.copy(alpha = if (CupertinoTheme.colorScheme.isDark) 0.42f else 0.9f)
    return border(
        width = (1f / LocalDensity.current.density).dp,
        brush = Brush.verticalGradient(listOf(highlight, edge, edge)),
        shape = shape,
    )
}

internal fun BackdropEffectScope.cupertinoGlassEffects(
    blurRadius: Float,
    lensHeight: Float = 0f,
    lensDepth: Float = 0f,
) {
    vibrancy()
    blur(blurRadius)
    if (lensHeight > 0f && lensDepth > 0f) lens(lensHeight, lensDepth)
}

/** 실제 배경 레이어 위에 Sidebar용 material을 그립니다. */
@Composable
fun Modifier.cupertinoSidebarMaterial(backdrop: LayerBackdrop): Modifier {
    val tint = CupertinoGlassDefaults.tint.copy(
        alpha = if (Accessibility.isReduceTransparencyEnabled) 1f
        else if (CupertinoTheme.colorScheme.isDark) 0.38f else 0.5f,
    )
    return drawBackdrop(
        backdrop = backdrop,
        shape = { RectangleShape },
        effects = { cupertinoGlassEffects(30.dp.toPx()) },
        highlight = { null },
        shadow = { null },
        onDrawSurface = { drawRect(tint) },
    )
}

internal val LocalCupertinoBackdropCoordinates = staticCompositionLocalOf<MutableState<LayoutCoordinates?>?> { null }

internal val LocalCupertinoDialogBackdropMotion = staticCompositionLocalOf<() -> Unit> { {} }

@Composable
internal fun rememberCupertinoDialogBackdrop(): Backdrop? {
    val backdrop = LocalCupertinoDialogBackdrop.current ?: LocalCupertinoBackdrop.current ?: return null
    val source = LocalCupertinoBackdropCoordinates.current ?: return backdrop
    val observeMotion = rememberUpdatedState(LocalCupertinoDialogBackdropMotion.current)
    return remember(backdrop, source) {
        object : Backdrop {
            override val isCoordinatesDependent = true

            override fun DrawScope.drawBackdrop(
                density: Density,
                coordinates: LayoutCoordinates?,
                layerBlock: (GraphicsLayerScope.() -> Unit)?,
            ) {
                observeMotion.value()
                val origin = source.value ?: return
                val target = coordinates ?: return
                if (!origin.isAttached || !target.isAttached) return
                val zero = target.screenToLocal(origin.localToScreen(Offset.Zero))
                val x = target.screenToLocal(origin.localToScreen(Offset(1f, 0f))) - zero
                val y = target.screenToLocal(origin.localToScreen(Offset(0f, 1f))) - zero
                if (!zero.x.isFinite() || !zero.y.isFinite()) return
                val matrix = backdropTransform(zero, x, y)
                withTransform({ transform(matrix) }) {
                    drawLayer(backdrop.graphicsLayer)
                }
            }
        }
    }
}
