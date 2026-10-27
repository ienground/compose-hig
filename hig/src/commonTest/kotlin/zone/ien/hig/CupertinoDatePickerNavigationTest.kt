package zone.ien.hig

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

@OptIn(ExperimentalTestApi::class, ExperimentalCupertinoApi::class)
class CupertinoDatePickerNavigationTest {
    @Test
    fun previousMonthCanReachTheFirstJanuaryAndStopsAtTheBoundary() = runComposeUiTest {
        val state = CupertinoDatePickerState(Instant.parse("2025-02-15T00:00:00Z").toEpochMilliseconds(), 2025..2027)
        setContent { CupertinoDatePicker(state, style = DatePickerStyle.Pager()) }
        val navigation = hasContentDescription("Next Month") or hasContentDescription("Previous Month") or
            hasContentDescription("다음 달") or hasContentDescription("이전 달")
        onAllNodes(navigation)[0].performClick()
        runOnIdle {
            assertEquals(2025, state.stateData.displayedMonth.year)
            assertEquals(1, state.stateData.displayedMonth.month)
        }
        onAllNodes(navigation)[0].assertIsNotEnabled()
    }

    @Test
    fun monthHeaderCanBeActivatedThroughSemantics() = runComposeUiTest {
        val state = CupertinoDatePickerState(Instant.parse("2025-02-15T00:00:00Z").toEpochMilliseconds(), 2025..2027)
        setContent { CupertinoDatePicker(state, style = DatePickerStyle.Pager()) }
        onAllNodes(hasText("2025", substring = true))[0].assertHasClickAction().performClick()
        onNode(hasContentDescription("Next Month") or hasContentDescription("다음 달")).assertDoesNotExist()
    }

    @Test
    fun monthNavigationExposesDistinctDirections() = runComposeUiTest {
        val state = CupertinoDatePickerState(Instant.parse("2025-02-15T00:00:00Z").toEpochMilliseconds(), 2025..2027)
        setContent { CupertinoDatePicker(state, style = DatePickerStyle.Pager()) }
        onNode(hasContentDescription("Previous Month") or hasContentDescription("이전 달")).assertExists()
        onNode(hasContentDescription("Next Month") or hasContentDescription("다음 달")).assertExists()
    }
}
