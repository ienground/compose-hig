package cupertino

import GeneratedAdaptiveTheme
import RootUiState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import zone.ien.hig.ExperimentalCupertinoApi
import zone.ien.hig.adaptive.ExperimentalAdaptiveApi
import zone.ien.hig.adaptive.Theme
import zone.ien.hig.theme.CupertinoColors
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.theme.systemBlue

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
private fun CupertinoPreviewTheme(
    content: @Composable () -> Unit,
) {
    GeneratedAdaptiveTheme(
        target = Theme.Cupertino,
        primaryColor = CupertinoColors.systemBlue,
        content = content,
    )
}

@Preview(
    name = "Cupertino 위젯 전체 화면",
    showBackground = true,
)
@Composable
private fun CupertinoWidgetsScreenPreview() {
    CupertinoPreviewTheme {
        CupertinoWidgetsScreen(
            uiState = RootUiState(),
            onItemValueChanged = {},
            onNavigate = {},
        )
    }
}

@OptIn(ExperimentalCupertinoApi::class)
@Preview(
    name = "iOS 26 세그먼트 컨트롤",
    showBackground = true,
)
@Composable
private fun CupertinoSegmentedControlPreview() {
    CupertinoPreviewTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CupertinoTheme.colorScheme.systemBackground)
                .padding(vertical = 24.dp),
        ) {
            Ios26SegmentedControlChapter()
        }
    }
}

@OptIn(ExperimentalCupertinoApi::class)
@Preview(
    name = "iOS 26 스와이프 액션",
    showBackground = true,
)
@Composable
private fun CupertinoSwipeActionsPreview() {
    CupertinoPreviewTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(CupertinoTheme.colorScheme.systemBackground)
                .padding(vertical = 24.dp),
        ) {
            RecentCallsSwipeChapter(
                scrollableState = rememberScrollState(),
            )
        }
    }
}
