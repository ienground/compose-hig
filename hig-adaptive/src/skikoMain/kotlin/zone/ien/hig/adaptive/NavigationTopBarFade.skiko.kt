package zone.ien.hig.adaptive

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset

@Composable
internal actual fun navigationTopBarFade(scope: AnimatedVisibilityScope): Modifier = with(scope) {
    val shadowOutset = 24.dp
    val opacity = navigationTopBarOpacity(scope)
    Modifier
        .layout { measurable, constraints ->
            val outset = shadowOutset.roundToPx()
            val placeable = measurable.measure(constraints.offset(outset * 2, outset * 2))
            layout(placeable.width - outset * 2, placeable.height - outset * 2) {
                placeable.place(-outset, -outset)
            }
        }
        // 페이드 렌더링 영역만 확장하고 버튼의 배치와 공유 경계는 유지합니다.
        .graphicsLayer {
            alpha = opacity.value
            val scale = 1f + (TopBarHiddenScale - 1f) * (1f - alpha)
            scaleX = scale
            scaleY = scale
        }
        .padding(shadowOutset)
}
