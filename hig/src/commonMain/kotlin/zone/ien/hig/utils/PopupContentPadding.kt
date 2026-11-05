package zone.ien.hig.utils

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp

/** 본문 밖의 그림자 여백을 누르면 팝업의 외부 터치와 동일하게 닫습니다. */
internal fun Modifier.cupertinoPopupContentPadding(
    padding: Dp,
    dismissOnClickOutside: Boolean,
    onDismissRequest: () -> Unit,
): Modifier = pointerInput(padding, dismissOnClickOutside, onDismissRequest) {
    if (dismissOnClickOutside) {
        val paddingPx = padding.toPx()
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            val position = down.position
            if (position.x < paddingPx || position.x >= size.width - paddingPx ||
                position.y < paddingPx || position.y >= size.height - paddingPx
            ) {
                down.consume()
                onDismissRequest()
            }
        }
    }
}.padding(padding)
