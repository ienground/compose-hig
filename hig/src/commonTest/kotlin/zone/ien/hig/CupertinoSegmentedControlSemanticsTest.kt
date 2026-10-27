package zone.ien.hig

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlin.test.Test

@OptIn(ExperimentalCupertinoApi::class, ExperimentalTestApi::class)
class CupertinoSegmentedControlSemanticsTest {
    @Test
    fun tabsExposeSelectionAndUpdateItAfterClick() = runComposeUiTest {
        var selectedTabIndex by mutableIntStateOf(0)

        setContent {
            CupertinoSegmentedControl(selectedTabIndex = selectedTabIndex) {
                CupertinoSegmentedControlTab(
                    onClick = { selectedTabIndex = 0 },
                    isSelected = selectedTabIndex == 0,
                ) {
                    androidx.compose.material3.Text("First")
                }
                CupertinoSegmentedControlTab(
                    onClick = { selectedTabIndex = 1 },
                    isSelected = selectedTabIndex == 1,
                ) {
                    androidx.compose.material3.Text("Second")
                }
            }
        }

        onNodeWithText("First").assertIsSelected()
        onNodeWithText("Second").assertIsNotSelected()
        onNodeWithText("First").assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab))
        onNodeWithText("Second").assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab))

        onNodeWithText("Second").performClick()

        onNodeWithText("First").assertIsNotSelected()
        onNodeWithText("Second").assertIsSelected()
        onNodeWithText("First").assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab))
        onNodeWithText("Second").assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab))
    }
}
