package zone.ien.hig

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
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
import zone.ien.hig.theme.LocalColorScheme
import zone.ien.hig.theme.darkColorScheme
import zone.ien.hig.theme.lightColorScheme

@OptIn(ExperimentalTestApi::class, ExperimentalCupertinoApi::class, InternalCupertinoApi::class)
class CupertinoDatePickerNavigationTest {
    @Test
    fun selectedAndCurrentDaysUseDistinctReferenceColors() = runComposeUiTest {
        val actual = mutableListOf<List<Color>>()
        val schemes = listOf(lightColorScheme(), darkColorScheme())
        setContent {
            schemes.forEach { scheme ->
                CompositionLocalProvider(LocalColorScheme provides scheme) {
                    val colors = CupertinoDatePickerDefaults.pagerColors()
                    val selectedBackground = colors.dayContainerColor(isToday = false, selected = true, enabled = true).value
                    val selectedForeground = colors.dayContentColor(isToday = false, selected = true, inRange = false, enabled = true).value
                    val todayBackground = colors.dayContainerColor(isToday = true, selected = false, enabled = true).value
                    val selectedTodayBackground = colors.dayContainerColor(isToday = true, selected = true, enabled = true).value
                    val selectedTodayForeground = colors.dayContentColor(isToday = true, selected = true, inRange = false, enabled = true).value
                    val disabledTodayForeground = colors.dayContentColor(isToday = true, selected = false, inRange = false, enabled = false).value
                    actual.add(listOf(selectedBackground, selectedForeground, todayBackground, selectedTodayBackground, selectedTodayForeground, disabledTodayForeground))
                }
            }
        }
        runOnIdle {
            schemes.forEachIndexed { index, scheme ->
                assertEquals(
                    listOf(scheme.label, scheme.systemBackground, scheme.accent.copy(alpha = 0.12f), scheme.accent, Color.White, scheme.accent.copy(alpha = 0.38f)),
                    actual[index],
                )
            }
        }
    }

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
