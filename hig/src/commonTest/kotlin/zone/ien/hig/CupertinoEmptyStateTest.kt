package zone.ien.hig

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCupertinoApi::class, ExperimentalTestApi::class)
class CupertinoEmptyStateTest {
    @Test
    fun remainsVisibleInsideAnUnboundedScrollingColumn() = runComposeUiTest {
        setContent {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                CupertinoEmptyState(
                    title = { CupertinoText(text = "검색 결과 없음") },
                    description = { CupertinoText(text = "다른 검색어를 입력하세요.") },
                )
            }
        }
        onNodeWithText("검색 결과 없음").assertIsDisplayed()
        onNodeWithText("다른 검색어를 입력하세요.").assertIsDisplayed()
    }

    @Test
    fun rendersContentSlotsAndRunsTheOptionalAction() = runComposeUiTest {
        var actionInvoked = false

        setContent {
            CupertinoEmptyState(
                title = { CupertinoText("No results") },
                description = { CupertinoText("Try another search") },
                icon = { CupertinoText("★") },
                onAction = { actionInvoked = true },
                action = { CupertinoText("Search again") },
            )
        }

        onNodeWithText("No results").assertExists()
        onNodeWithText("Try another search").assertExists()
        onNodeWithText("★").assertExists()
        onNodeWithText("Search again").performClick()

        assertTrue(actionInvoked)
    }
}
