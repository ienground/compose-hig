package zone.ien.hig

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test

@OptIn(ExperimentalCupertinoApi::class, ExperimentalTestApi::class)
class CupertinoControlDesignTest {
    @Test
    fun activityIndicatorDefaultsAndSizeVariantsMatchDesignBounds() = runComposeUiTest {
        setContent {
            Column {
                CupertinoActivityIndicator(modifier = Modifier.testTag("regular-indicator"))
                CupertinoActivityIndicator(
                    modifier = Modifier.testTag("small-indicator"),
                    size = CupertinoActivityIndicatorDefaults.SmallSize,
                )
                CupertinoActivityIndicator(
                    modifier = Modifier.testTag("large-indicator"),
                    size = CupertinoActivityIndicatorDefaults.LargeSize,
                )
            }
        }

        onNodeWithTag("regular-indicator")
            .assertWidthIsEqualTo(20.dp)
            .assertHeightIsEqualTo(20.dp)
        onNodeWithTag("small-indicator")
            .assertWidthIsEqualTo(14.dp)
            .assertHeightIsEqualTo(14.dp)
        onNodeWithTag("large-indicator")
            .assertWidthIsEqualTo(35.dp)
            .assertHeightIsEqualTo(35.dp)
    }

    @Test
    fun switchKeepsDesignTrackWidthAndAccessibleTouchHeight() = runComposeUiTest {
        setContent {
            CupertinoSwitch(
                checked = true,
                onCheckedChange = {},
                modifier = Modifier.testTag("switch"),
            )
        }

        onNodeWithTag("switch")
            .assertWidthIsEqualTo(64.dp)
            .assertHeightIsEqualTo(44.dp)
    }
}
