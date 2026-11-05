package zone.ien.hig

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.Backdrop
import zone.ien.hig.icons.CupertinoIcons
import zone.ien.hig.icons.outlined.ChevronDown
import zone.ien.hig.icons.outlined.ChevronUp
import zone.ien.hig.theme.CupertinoTheme

/**
 * 현재 선택값과 위아래 화살표를 표시하고 기존 Cupertino 메뉴를 여는 팝업 버튼입니다.
 *
 * [expanded]는 호출자가 관리하며 메뉴 선택 시에도 [onExpandedChange]로 닫을 수 있습니다.
 * [content]에서 [MenuPickerAction]을 사용하면 현재 선택 항목에 체크 표시를 제공합니다.
 * [backdrop]을 생략하면 가장 가까운 [CupertinoScaffold]의 본문을 메뉴 위치에 맞춰 반영합니다.
 */
@Composable
@ExperimentalCupertinoApi
fun CupertinoPopupButton(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backdrop: Backdrop = CupertinoDropdownMenuDefaults.rememberBackdrop(),
    title: @Composable () -> Unit,
    content: @Composable CupertinoMenuScope.() -> Unit,
) {
    Box(modifier = modifier) {
        CupertinoButton(
            onClick = { onExpandedChange(!expanded) },
            enabled = enabled,
            colors = CupertinoButtonDefaults.plainButtonColors(),
            contentPadding = PaddingValues(vertical = 13.dp),
            modifier = Modifier.semantics {
                if (enabled) {
                    if (expanded) collapse { onExpandedChange(false); true }
                    else expand { onExpandedChange(true); true }
                }
            },
        ) {
            ProvideTextStyle(
                value = CupertinoTheme.typography.body.copy(
                    fontSize = 18.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    title()
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        CupertinoIcon(
                            imageVector = CupertinoIcons.Default.ChevronUp,
                            contentDescription = null,
                            modifier = Modifier.size(10.dp, 6.dp),
                        )
                        CupertinoIcon(
                            imageVector = CupertinoIcons.Default.ChevronDown,
                            contentDescription = null,
                            modifier = Modifier.size(10.dp, 6.dp),
                        )
                    }
                }
            }
        }
        CupertinoDropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = { onExpandedChange(false) },
            backdrop = backdrop,
            content = content,
        )
    }
}
