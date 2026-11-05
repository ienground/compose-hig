package zone.ien.hig

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.capsule.ContinuousRoundedRectangle
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.utils.CupertinoGlassDefaults
import zone.ien.hig.utils.cupertinoPopupContentPadding
import zone.ien.hig.utils.rememberCupertinoDialogBackdrop
import zone.ien.hig.utils.rememberDefaultBackdrop

private val PopoverShadow = Shadow(radius = 50.dp, color = Color.Black.copy(alpha = 0.3f), offset = DpOffset(0.dp, 10.dp))
private val PopoverShadowPadding = PopoverShadow.radius + PopoverShadow.offset.y

/** 팝오버의 화살표가 놓이는 변입니다. 시작과 끝은 레이아웃 방향을 따릅니다. */
enum class CupertinoPopoverEdge { Top, Bottom, Start, End }

/**
 * 호출 위치를 가리키는 화살표가 있는 Cupertino 팝오버입니다.
 *
 * 기준 요소와 같은 Box 안에 배치합니다. [edge] 쪽에 공간이 부족하면 반대편으로 전환하고,
 * 창 경계에 맞춰 이동해도 화살표는 기준 요소를 가리킵니다. 긴 콘텐츠는 내부에서 스크롤됩니다.
 */
@Composable
@ExperimentalCupertinoApi
fun CupertinoPopover(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    edge: CupertinoPopoverEdge = CupertinoPopoverEdge.Top,
    width: Dp = 200.dp,
    contentPadding: PaddingValues = PaddingValues(24.dp),
    containerColor: Color = CupertinoGlassDefaults.panelTint,
    backdrop: Backdrop = rememberCupertinoDialogBackdrop() ?: rememberDefaultBackdrop(),
    properties: PopupProperties = PopupProperties(focusable = true, clippingEnabled = false),
    content: @Composable ColumnScope.() -> Unit,
) {
    if (!expanded) return
    val density = LocalDensity.current
    val direction = LocalLayoutDirection.current
    val window = LocalWindowInfo.current.containerSize
    val shadowPadding = with(density) {
        if (window.width > 0 && window.height > 0) {
            minOf(PopoverShadowPadding.roundToPx(), window.width / 2, window.height / 2).toDp()
        } else {
            PopoverShadowPadding
        }
    }
    var placement by remember(edge, direction) {
        mutableStateOf(CupertinoPopoverPlacement(IntOffset.Zero, edge, Float.NaN))
    }
    val positionProvider = remember(edge, density) {
        CupertinoPopoverPositionProvider(edge, density) { placement = it }
    }
    val shape = remember(placement.edge, placement.arrowOffset) {
        CupertinoPopoverShape(placement.edge, placement.arrowOffset)
    }
    val maxWidth = with(density) {
        if (window.width > 0) (window.width.toDp() - 32.dp).coerceAtLeast(0.dp) else width
    }
    val maxHeight = with(density) {
        if (window.height > 0) (window.height.toDp() - 32.dp).coerceAtLeast(0.dp) else Dp.Infinity
    }
    val arrowPadding = when (placement.edge) {
        CupertinoPopoverEdge.Top -> PaddingValues(top = 13.dp)
        CupertinoPopoverEdge.Bottom -> PaddingValues(bottom = 13.dp)
        CupertinoPopoverEdge.Start -> PaddingValues(start = 13.dp)
        CupertinoPopoverEdge.End -> PaddingValues(end = 13.dp)
    }
    val contentColor = CupertinoTheme.colorScheme.label
    Popup(
        popupPositionProvider = positionProvider,
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Box(
                Modifier
                    .cupertinoPopupContentPadding(shadowPadding, properties.dismissOnClickOutside, onDismissRequest),
            ) {
                Column(
                    modifier = modifier
                        .width(width.coerceAtMost(maxWidth))
                        .heightIn(max = maxHeight)
                        .dropShadow(shape, PopoverShadow)
                        .drawBackdrop(
                            backdrop = backdrop,
                            shape = { shape },
                            effects = { blur(65.dp.toPx()) },
                            onDrawSurface = { drawRect(containerColor) },
                        )
                        .clip(shape)
                        .padding(arrowPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(contentPadding),
                    content = content,
                )
            }
        }
    }
}

internal data class CupertinoPopoverPlacement(
    val offset: IntOffset,
    val edge: CupertinoPopoverEdge,
    val arrowOffset: Float,
)

internal class CupertinoPopoverPositionProvider(
    private val preferredEdge: CupertinoPopoverEdge,
    private val density: Density,
    private val onPlacement: (CupertinoPopoverPlacement) -> Unit = {},
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val margin = with(density) { 16.dp.roundToPx() }
        val shadowPadding = minOf(
            with(density) { PopoverShadowPadding.roundToPx() },
            popupContentSize.width / 2,
            popupContentSize.height / 2,
        )
        val contentSize = IntSize(
            popupContentSize.width - shadowPadding * 2,
            popupContentSize.height - shadowPadding * 2,
        )
        val isLtr = layoutDirection == LayoutDirection.Ltr
        fun candidate(edge: CupertinoPopoverEdge): IntOffset = when (edge) {
            CupertinoPopoverEdge.Top -> IntOffset(anchorBounds.center.x - contentSize.width / 2, anchorBounds.bottom)
            CupertinoPopoverEdge.Bottom -> IntOffset(anchorBounds.center.x - contentSize.width / 2, anchorBounds.top - contentSize.height)
            CupertinoPopoverEdge.Start -> IntOffset(
                if (isLtr) anchorBounds.right else anchorBounds.left - contentSize.width,
                anchorBounds.center.y - contentSize.height / 2,
            )
            CupertinoPopoverEdge.End -> IntOffset(
                if (isLtr) anchorBounds.left - contentSize.width else anchorBounds.right,
                anchorBounds.center.y - contentSize.height / 2,
            )
        }
        val vertical = preferredEdge == CupertinoPopoverEdge.Top || preferredEdge == CupertinoPopoverEdge.Bottom
        fun fits(offset: IntOffset): Boolean = if (vertical) {
            offset.y >= margin && offset.y + contentSize.height <= windowSize.height - margin
        } else {
            offset.x >= margin && offset.x + contentSize.width <= windowSize.width - margin
        }
        val opposite = when (preferredEdge) {
            CupertinoPopoverEdge.Top -> CupertinoPopoverEdge.Bottom
            CupertinoPopoverEdge.Bottom -> CupertinoPopoverEdge.Top
            CupertinoPopoverEdge.Start -> CupertinoPopoverEdge.End
            CupertinoPopoverEdge.End -> CupertinoPopoverEdge.Start
        }
        val edge = if (!fits(candidate(preferredEdge)) && fits(candidate(opposite))) opposite else preferredEdge
        val desired = candidate(edge)
        fun clamp(value: Int, window: Int, content: Int): Int {
            val safeMargin = minOf(margin, ((window - content) / 2).coerceAtLeast(0))
            return value.coerceIn(safeMargin, (window - content - safeMargin).coerceAtLeast(safeMargin))
        }
        val position = IntOffset(
            clamp(desired.x, windowSize.width, contentSize.width),
            clamp(desired.y, windowSize.height, contentSize.height),
        )
        val popupPosition = position - IntOffset(shadowPadding, shadowPadding)
        onPlacement(
            CupertinoPopoverPlacement(
                popupPosition,
                edge,
                if (vertical) (anchorBounds.center.x - position.x).toFloat()
                else (anchorBounds.center.y - position.y).toFloat(),
            ),
        )
        return popupPosition
    }
}

private class CupertinoPopoverShape(
    private val edge: CupertinoPopoverEdge,
    private val arrowOffset: Float,
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val vertical = edge == CupertinoPopoverEdge.Top || edge == CupertinoPopoverEdge.Bottom
        val arrowHeight = with(density) { 13.dp.toPx() }.coerceAtMost(if (vertical) size.height else size.width)
        val left = edge == CupertinoPopoverEdge.Start && layoutDirection == LayoutDirection.Ltr ||
            edge == CupertinoPopoverEdge.End && layoutDirection == LayoutDirection.Rtl
        val bodyOffset = Offset(if (!vertical && left) arrowHeight else 0f, if (edge == CupertinoPopoverEdge.Top) arrowHeight else 0f)
        val bodySize = Size(size.width - if (vertical) 0f else arrowHeight, size.height - if (vertical) arrowHeight else 0f)
        val body = Path().apply {
            addOutline(ContinuousRoundedRectangle(38.dp).createOutline(bodySize, layoutDirection, density))
            translate(bodyOffset)
        }
        val axisLength = if (vertical) size.width else size.height
        val halfWidth = minOf(with(density) { 28.dp.toPx() }, axisLength / 2f)
        val inset = minOf(with(density) { 38.dp.toPx() } + halfWidth, axisLength / 2f)
        val center = (arrowOffset.takeIf { it.isFinite() } ?: axisLength / 2f).coerceIn(inset, axisLength - inset)
        fun point(x: Float, y: Float): Offset {
            val along = center + (x - 28f) * halfWidth / 28f
            val outward = y / 13f * arrowHeight
            return when {
                edge == CupertinoPopoverEdge.Top -> Offset(along, arrowHeight - outward)
                edge == CupertinoPopoverEdge.Bottom -> Offset(along, size.height - arrowHeight + outward)
                left -> Offset(arrowHeight - outward, along)
                else -> Offset(size.width - arrowHeight + outward, along)
            }
        }
        val arrow = Path()
        val start = point(0f, 0f)
        arrow.moveTo(start.x, start.y)
        fun curve(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float) {
            val a = point(x1, y1)
            val b = point(x2, y2)
            val c = point(x3, y3)
            arrow.cubicTo(a.x, a.y, b.x, b.y, c.x, c.y)
        }
        // docs/ios27.fig의 Arrow(5433:21457), 56×13pt 곡선을 유지합니다.
        curve(2.0404f, 0.0019f, 4.0802f, -0.0066f, 6.1206f, 0.012f)
        curve(8.0735f, 0.03f, 10.2592f, 0.0555f, 12.1597f, 0.657f)
        curve(14.2126f, 1.3071f, 15.5717f, 2.4437f, 16.9391f, 3.7814f)
        curve(17.9236f, 4.7435f, 19.8574f, 6.7963f, 20.799f, 7.7874f)
        curve(21.5697f, 8.6f, 23.0782f, 10.2062f, 23.9019f, 10.9833f)
        curve(24.9388f, 11.9609f, 26.2204f, 13f, 28.0006f, 13f)
        curve(29.7807f, 13f, 31.0618f, 11.9609f, 32.0981f, 10.9843f)
        curve(32.9218f, 10.2077f, 34.4303f, 8.6005f, 35.2016f, 7.7884f)
        curve(36.142f, 6.7973f, 38.0758f, 4.7445f, 39.0609f, 3.7824f)
        curve(40.4307f, 2.4447f, 41.7874f, 1.3081f, 43.8397f, 0.658f)
        curve(45.7408f, 0.058f, 47.9271f, 0.031f, 49.8788f, 0.013f)
        curve(51.9198f, -0.0056f, 53.9596f, 0.0029f, 56f, 0.0029f)
        val end = point(56f, 0f)
        arrow.lineTo(end.x, end.y)
        arrow.close()
        return Outline.Generic(Path.combine(PathOperation.Union, body, arrow))
    }
}
