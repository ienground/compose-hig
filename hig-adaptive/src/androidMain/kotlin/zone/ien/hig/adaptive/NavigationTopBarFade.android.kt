package zone.ien.hig.adaptive

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer

@Composable
internal actual fun navigationTopBarFade(scope: AnimatedVisibilityScope): Modifier {
    val opacity = navigationTopBarOpacity(scope)
    return Modifier.graphicsLayer {
        alpha = opacity.value
        val scale = 1f + (TopBarHiddenScale - 1f) * (1f - alpha)
        scaleX = scale
        scaleY = scale
        // 오프스크린 버퍼의 경계에서 Android 그림자가 잘리지 않게 합니다.
        compositingStrategy = CompositingStrategy.ModulateAlpha
        clip = false
    }
}
