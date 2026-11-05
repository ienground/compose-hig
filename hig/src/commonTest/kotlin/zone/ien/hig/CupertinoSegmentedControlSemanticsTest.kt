package zone.ien.hig

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test

@OptIn(ExperimentalCupertinoApi::class, ExperimentalTestApi::class)
class CupertinoSegmentedControlSemanticsTest {
    @Test
    fun smallAndLargeControlsUseDesignHeights() = runComposeUiTest {
        setContent {
            Column {
                CupertinoSegmentedControl(
                    selectedTabIndex = 0,
                    size = CupertinoSegmentedControlSize.Small,
                    modifier = Modifier.testTag("small-segmented-control"),
                    paddingValues = PaddingValues(0.dp),
                ) {
                    CupertinoSegmentedControlTab(onClick = {}, isSelected = true) {
                        CupertinoText(text = "First")
                    }
                    CupertinoSegmentedControlTab(onClick = {}, isSelected = false) {
                        CupertinoText(text = "Second")
                    }
                }
                CupertinoSegmentedControl(
                    selectedTabIndex = 0,
                    size = CupertinoSegmentedControlSize.Large,
                    modifier = Modifier.testTag("large-segmented-control"),
                    paddingValues = PaddingValues(0.dp),
                ) {
                    CupertinoSegmentedControlTab(onClick = {}, isSelected = true) {
                        CupertinoText(text = "First")
                    }
                    CupertinoSegmentedControlTab(onClick = {}, isSelected = false) {
                        CupertinoText(text = "Second")
                    }
                }
            }
        }

        onNodeWithTag("small-segmented-control").assertHeightIsEqualTo(32.dp)
        onNodeWithTag("large-segmented-control").assertHeightIsEqualTo(50.dp)
    }

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
