package zone.ien.hig

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Instant

@OptIn(ExperimentalTestApi::class, ExperimentalCupertinoApi::class)
class CupertinoPickerPresentationTest {
    @Test
    fun emptyListCanResolveItsTopBarAppearanceBeforeTheFirstLayout() = runComposeUiTest {
        var transparent = false

        setContent {
            val state = rememberLazyListState()
            transparent = state.isTopBarTransparent()
            LazyColumn(state = state) {}
        }

        runOnIdle { assertTrue(transparent) }
    }

    @Test
    fun dateTimePagerSelectsADateWithoutLosingTheTime() = runComposeUiTest {
        val state = CupertinoDateTimePickerState(
            initialSelectedDateMillis = Instant.parse("2026-10-27T00:00:00Z").toEpochMilliseconds(),
            yearRange = 2025..2027,
            initialHour = 13,
            initialMinute = 45,
            is24Hour = true,
        )

        setContent {
            CupertinoDateTimePicker(state, style = DatePickerStyle.Pager())
        }

        onNode(hasText("28", substring = true) and hasClickAction()).performClick()

        runOnIdle {
            assertEquals(Instant.parse("2026-10-28T13:45:00Z").toEpochMilliseconds(), state.selectedDateTimeMillis)
        }

        runOnIdle { state.setSelection(Instant.parse("2026-10-29T16:20:00Z").toEpochMilliseconds()) }
        onNode(
            hasText("29", substring = true) and SemanticsMatcher.expectValue(SemanticsProperties.Selected, true),
        ).assertExists()
        runOnIdle {
            assertEquals(16, state.selectedHour)
            assertEquals(20, state.selectedMinute)
        }
    }
}
