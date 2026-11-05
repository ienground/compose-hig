package zone.ien.hig

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertRangeInfoEquals
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlin.test.Test

@OptIn(ExperimentalCupertinoApi::class, ExperimentalTestApi::class)
class CupertinoProgressIndicatorTest {
    @Test
    fun progressExposesFractionAndOptionalLabel() = runComposeUiTest {
        setContent {
            CupertinoProgressIndicator(
                progress = 0.25f,
                label = "Downloading",
            )
        }

        onNodeWithText("Downloading").assertExists()
        onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo(0.25f, 0f..1f, 0)))
            .assertRangeInfoEquals(ProgressBarRangeInfo(0.25f, 0f..1f, 0))
    }
}
