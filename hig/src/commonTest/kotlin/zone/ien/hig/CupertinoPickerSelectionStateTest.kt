package zone.ien.hig

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant

@OptIn(ExperimentalCupertinoApi::class, InternalCupertinoApi::class)
class CupertinoPickerSelectionStateTest {
    private val years = 2025..2027
    private val initialDate = utc("2026-10-27T00:00:00Z")

    @Test
    fun dateAndDateTimeKeepYearBoundsInBothPresentationModes() {
        for (manual in listOf(false, true)) {
            val date = CupertinoDatePickerState(initialDate, years).apply { isManual = manual }
            val dateTime = CupertinoDateTimePickerState(initialDate, years, 13, 45, true).apply { isManual = manual }
            for (invalid in listOf(utc("2024-12-31T23:59:00Z"), utc("2028-01-01T00:00:00Z"))) {
                assertFailsWith<IllegalArgumentException> { date.setSelection(invalid) }
                assertFailsWith<IllegalArgumentException> { dateTime.setSelection(invalid) }
            }
            assertEquals(initialDate, date.selectedDateMillis)
            assertEquals(utc("2026-10-27T13:45:00Z"), dateTime.selectedDateTimeMillis)
            date.setSelection(utc("2025-01-01T00:00:00Z"))
            dateTime.setSelection(utc("2027-12-31T23:59:00Z"))
            assertEquals(utc("2025-01-01T00:00:00Z"), date.selectedDateMillis)
            assertEquals(utc("2027-12-31T23:59:00Z"), dateTime.selectedDateTimeMillis)
        }
    }

    @Test
    fun dateSelectionImmediatelyUpdatesTheWheelSelection() {
        val state = CupertinoDatePickerState(initialDate, years)

        state.setSelection(utc("2026-11-28T00:00:00Z"))

        assertEquals(utc("2026-11-28T00:00:00Z"), state.selectedDateMillis)
        assertEquals(2026, state.stateData.yearRange.first + state.stateData.yearState.selectedItemIndex)
        assertEquals(10, state.stateData.monthState.currentSelectedItem(12))
        assertEquals(27, state.stateData.dayState.currentSelectedItem(31))
    }

    @Test
    fun dateSelectionRemainsCanonicalWhenSwitchingPresentationModes() {
        val state = CupertinoDatePickerState(initialDate, years)
        state.setSelection(utc("2026-11-28T15:20:00Z"))

        state.isManual = true
        assertEquals(utc("2026-11-28T00:00:00Z"), state.selectedDateMillis)
        state.setSelection(utc("2027-02-06T12:30:00Z"))
        state.isManual = false

        assertEquals(utc("2027-02-06T00:00:00Z"), state.selectedDateMillis)
    }

    @Test
    fun dateSelectionSurvivesSavingAndRestoringInBothModes() {
        for (manual in listOf(false, true)) {
            val state = CupertinoDatePickerState(initialDate, years)
            state.isManual = manual
            state.setSelection(utc("2027-02-06T00:00:00Z"))

            val restored = restore(CupertinoDatePickerState.Saver(), state)

            assertEquals(utc("2027-02-06T00:00:00Z"), restored.selectedDateMillis)
            restored.isManual = !manual
            assertEquals(utc("2027-02-06T00:00:00Z"), restored.selectedDateMillis)
        }
    }

    @Test
    fun twelveHourTimePreservesMidnightNoonAndEveningInitialValues() {
        for (hour in listOf(0, 11, 12, 13, 23)) {
            val state = CupertinoTimePickerState(hour, 45, is24Hour = false)

            assertEquals(hour, state.hour, "초기 시각: $hour")
            assertEquals(45, state.minute)
        }
    }

    @Test
    fun twelveHourDateTimePreservesTheFullInitialTimestamp() {
        for (hour in listOf(0, 11, 12, 13, 23)) {
            val state = CupertinoDateTimePickerState(initialDate, years, hour, 45, false)

            assertEquals(hour, state.selectedHour, "초기 시각: $hour")
            assertEquals(initialDate + (hour * 60L + 45L) * 60_000L, state.selectedDateTimeMillis)
        }
    }

    @Test
    fun timeModeChangesPreserveEveningHoursAndNativeEdits() {
        val state = CupertinoTimePickerState(13, 45, is24Hour = false)

        state.isManual = true
        assertEquals(13, state.hour)
        assertEquals(45, state.minute)
        state.manualHour = 23
        state.manualMinute = 17
        state.isManual = false

        assertEquals(23, state.hour)
        assertEquals(17, state.minute)
        state.isManual = true
        assertEquals(23, state.hour)
        assertEquals(17, state.minute)
    }

    @Test
    fun twelveHourTimeSurvivesSavingAndRestoring() {
        val state = CupertinoTimePickerState(23, 17, is24Hour = false)

        val restored = restore(CupertinoTimePickerState.Saver(), state)

        assertEquals(23, restored.hour)
        assertEquals(17, restored.minute)
    }

    @Test
    fun switchingDateTimeToNativePreservesInitialTime() {
        val state = CupertinoDateTimePickerState(initialDate, years, 13, 45, true)

        state.isManual = true

        assertEquals(utc("2026-10-27T13:45:00Z"), state.selectedDateTimeMillis)
        assertEquals(13, state.selectedHour)
        assertEquals(45, state.selectedMinute)
    }

    @Test
    fun dateTimeSelectionImmediatelyUpdatesDateAndTimeInBothModes() {
        for (manual in listOf(false, true)) {
            val state = CupertinoDateTimePickerState(initialDate, years, 13, 45, false)
            state.isManual = manual

            state.setSelection(utc("2027-02-06T16:20:00Z"))

            assertEquals(utc("2027-02-06T16:20:00Z"), state.selectedDateTimeMillis)
            assertEquals(16, state.selectedHour)
            assertEquals(20, state.selectedMinute)
            state.isManual = !manual
            assertEquals(utc("2027-02-06T16:20:00Z"), state.selectedDateTimeMillis)
            assertEquals(16, state.selectedHour)
            assertEquals(20, state.selectedMinute)
        }
    }

    @Test
    fun manualDateTimeUsesHoursAndMinutesInsteadOfMilliseconds() {
        val state = CupertinoDateTimePickerState(initialDate, years, 0, 0, true)
        state.isManual = true

        state.setSelection(utc("2026-10-28T23:59:00Z"))

        assertEquals(23, state.selectedHour)
        assertEquals(59, state.selectedMinute)
    }

    @Test
    fun dateTimeSelectionSurvivesSavingAndRestoringInBothModes() {
        for (manual in listOf(false, true)) {
            val state = CupertinoDateTimePickerState(initialDate, years, 0, 0, false)
            state.isManual = manual
            state.setSelection(utc("2027-02-06T16:20:00Z"))

            val restored = restore(CupertinoDateTimePickerState.Saver(false), state)

            assertEquals(utc("2027-02-06T16:20:00Z"), restored.selectedDateTimeMillis)
            assertEquals(16, restored.selectedHour)
            assertEquals(20, restored.selectedMinute)
        }
    }

    private fun utc(value: String): Long = Instant.parse(value).toEpochMilliseconds()

    private fun <T, S : Any> restore(saver: Saver<T, S>, state: T): T {
        val saved = with(saver) { SaverScope { true }.save(state) }
        return requireNotNull(saver.restore(requireNotNull(saved)))
    }
}
