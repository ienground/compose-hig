package zone.ien.hig.adaptive

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import zone.ien.hig.AlertActionStyle
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalAdaptiveApi::class, ExperimentalTestApi::class)
class AdaptiveAlertDialogNativeTest {
    @Test
    fun cancelActionUsesItsOwnRendererAndCallback() = runComposeUiTest {
        var confirmed = 0
        var cancelled = 0
        setContent {
            MaterialTheme {
                CompositionLocalProvider(LocalTheme provides Theme.Material3) {
                    AdaptiveAlertDialogNative(
                        onDismissRequest = {}, title = "제목", message = "내용",
                        adaptation = {
                            material {
                                confirmButton = { _, enabled, onClick, title ->
                                    TextButton(onClick, enabled = enabled) { Text("확인:$title") }
                                }
                                dismissButton = { _, enabled, onClick, title ->
                                    TextButton(onClick, enabled = enabled) { Text("취소:$title") }
                                }
                            }
                        },
                    ) {
                        action(onClick = { confirmed++ }, title = "저장")
                        action(onClick = { cancelled++ }, style = AlertActionStyle.Cancel, title = "닫기")
                    }
                }
            }
        }
        onNode(hasText("취소:닫기")).performClick()
        runOnIdle {
            assertEquals(0, confirmed)
            assertEquals(1, cancelled)
        }
        onNode(hasText("확인:저장")).performClick()
        runOnIdle { assertEquals(1, confirmed) }
    }
}
