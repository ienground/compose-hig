package zone.ien.hig.adaptive

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer

@Composable
internal actual fun navigationTopBarFade(scope: AnimatedVisibilityScope): Modifier {
    val opacity = scope.transition.animateFloat(
        transitionSpec = { spring(stiffness = Spring.StiffnessMediumLow) },
        label = "TopBarOpacity",
    ) { if (it == EnterExitState.Visible) 1f else 0f }
    return Modifier.graphicsLayer {
        alpha = opacity.value
        // 오프스크린 버퍼의 경계에서 Android 그림자가 잘리지 않게 합니다.
        compositingStrategy = CompositingStrategy.ModulateAlpha
        clip = false
    }
}
