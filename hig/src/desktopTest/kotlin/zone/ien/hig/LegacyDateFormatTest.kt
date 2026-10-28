package zone.ien.hig

import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals

class LegacyDateFormatTest {
    @Test
    fun monthNamesRemainInOrderFromJanuaryThirtyFirstInLeapAndCommonYears() {
        val expected =
            listOf(
                "January",
                "February",
                "March",
                "April",
                "May",
                "June",
                "July",
                "August",
                "September",
                "October",
                "November",
                "December",
            )

        for (year in listOf(2024, 2023)) {
            val calendar = GregorianCalendar(year, Calendar.JANUARY, 31)

            assertEquals(expected, LegacyDateFormat.monthsNames(calendar, Locale.US))
        }
    }

    @Test
    fun monthNamesFallBackWhenCalendarHasNoDisplayName() {
        val calendar = object : GregorianCalendar(2024, Calendar.JANUARY, 1) {
            override fun getDisplayName(field: Int, style: Int, locale: Locale): String? = null
        }

        val names = LegacyDateFormat.monthsNames(calendar, Locale.US)

        assertEquals(12, names.size)
        assertEquals("January", names.first())
        assertEquals("December", names.last())
    }
}
