package zone.ien.hig

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.capsule.ContinuousCapsule
import zone.ien.hig.theme.CupertinoColors
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.theme.systemRed

/**
 * 숫자나 짧은 상태를 표시하는 iOS 배지입니다.
 *
 * [content]의 의미 설명과 숫자 축약 방식은 호출자가 지정합니다.
 */
@Composable
fun CupertinoBadge(
    modifier: Modifier = Modifier,
    containerColor: Color = CupertinoColors.systemRed,
    contentColor: Color = Color.White,
    content: @Composable () -> Unit,
) {
    CupertinoSurface(
        modifier = modifier,
        shape = ContinuousCapsule(),
        color = containerColor,
        contentColor = contentColor,
    ) {
        Box(
            modifier = Modifier
                .defaultMinSize(minWidth = 24.dp, minHeight = 24.dp)
                .padding(horizontal = 7.dp, vertical = 2.5.dp),
            contentAlignment = Alignment.Center,
        ) {
            ProvideTextStyle(
                value = CupertinoTheme.typography.callout.copy(
                    fontSize = 16.sp,
                    lineHeight = 19.sp,
                ),
                content = content,
            )
        }
    }
}
