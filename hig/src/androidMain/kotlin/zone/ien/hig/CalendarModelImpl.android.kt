/*
 * Copyright (c) 2023-2024. Compose Cupertino project and open source contributors.
 * Copyright (c) 2025. Scott Lanoue.
 * Copyright (c) 2026. IENGROUND of IENLAB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zone.ien.hig

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.chrono.Chronology
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DateTimeParseException
import java.time.format.DecimalStyle
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale
import androidx.compose.ui.platform.LocalLocale

/**
 * Gets the default locale for the device.
 *
 * @return The default [CalendarLocale] for the device
 */
@Composable
@ReadOnlyComposable
internal actual fun defaultLocale(): CalendarLocale =
    LocalConfiguration.current.run {
        val locales = locales
        if (locales.isEmpty) LocalLocale.current.platformLocale else locales[0]
    }

/**
 * Gets the current locale for the device.
 *
 * @return The current [CalendarLocale] for the device
 */
internal actual fun currentLocale(): CalendarLocale = Locale.getDefault()

/**
 * A [CalendarModel] implementation for API >= 26.
 *
 * This implementation uses Java 8 Time APIs available on Android API level 26 and above.
 *
 * @property today The current date
 * @property firstDayOfWeek The first day of the week for the default locale
 * @property weekdayNames The weekday names for the default locale
 */
@RequiresApi(Build.VERSION_CODES.O)
internal class AndroidCalendarModelImpl: CalendarModel {
    /**
     * Gets the current date.
     *
     * @return The [CalendarDate] representing today
     */
    override val today
        get(): CalendarDate {
            val systemLocalDate = LocalDate.now()
            return CalendarDate(
                year = systemLocalDate.year,
                month = systemLocalDate.monthValue,
                dayOfMonth = systemLocalDate.dayOfMonth,
                utcTimeMillis =
                    systemLocalDate
                        .atTime(LocalTime.MIDNIGHT)
                        .atZone(utcTimeZoneId)
                        .toInstant()
                        .toEpochMilli(),
            )
        }

    /**
     * Gets the first day of the week for the default locale.
     *
     * @return The first day of the week (1 = Sunday, 2 = Monday, etc.)
     */
    override val firstDayOfWeek: Int = WeekFields.of(Locale.getDefault()).firstDayOfWeek.value

    /**
     * Gets the weekday names for the default locale.
     *
     * @return A list of pairs containing full and short weekday names
     */
    override val weekdayNames: List<Pair<String, String>> = weekdayNames(Locale.getDefault())

    /**
     * Gets the weekday names for a specific locale.
     *
     * @param locale The [Locale] to get weekday names for
     * @return A list of pairs containing full and short weekday names
     */
    fun weekdayNames(locale: Locale): List<Pair<String, String>> =
        // This will start with Monday as the first day, according to ISO-8601.
        with(locale) {
            DayOfWeek.entries.map {
                it.getDisplayName(
                    TextStyle.FULL,
                    // locale =
                    this,
                ) to
                    it.getDisplayName(
                        TextStyle.SHORT,
                        // locale =
                        this,
                    )
            }
        }

    /**
     * Gets the date input format for the given locale.
     *
     * @param locale The [Locale] to get the input format for
     * @return The [DateInputFormat] for the specified locale
     */
    override fun getDateInputFormat(locale: Locale): DateInputFormat =
        datePatternAsInputFormat(
            DateTimeFormatterBuilder.getLocalizedDateTimePattern(
                // dateStyle =
                FormatStyle.SHORT,
                // timeStyle =
                null,
                // chrono =
                Chronology.ofLocale(locale),
                // locale =
                locale,
            ),
        )

    /**
     * Gets the canonical date from a timestamp.
     *
     * @param timeInMillis The timestamp in milliseconds
     * @return The [CalendarDate] for the given timestamp
     */
    override fun getCanonicalDate(timeInMillis: Long): CalendarDate {
        val localDate =
            Instant.ofEpochMilli(timeInMillis).atZone(utcTimeZoneId).toLocalDate()
        return CalendarDate(
            year = localDate.year,
            month = localDate.monthValue,
            dayOfMonth = localDate.dayOfMonth,
            utcTimeMillis = localDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
        )
    }

    /**
     * Gets the month from a timestamp.
     *
     * @param timeInMillis The timestamp in milliseconds
     * @return The [CalendarMonth] for the given timestamp
     */
    override fun getMonth(timeInMillis: Long): CalendarMonth =
        getMonth(
            Instant
                .ofEpochMilli(timeInMillis)
                .atZone(utcTimeZoneId)
                .withDayOfMonth(1)
                .toLocalDate(),
        )

    /**
     * Gets the month from a [CalendarDate].
     *
     * @param date The [CalendarDate] to get the month for
     * @return The [CalendarMonth] for the given date
     */
    override fun getMonth(date: CalendarDate): CalendarMonth = getMonth(LocalDate.of(date.year, date.month, 1))

    /**
     * Gets the month from year and month values.
     *
     * @param year The year
     * @param month The month (1-12)
     * @return The [CalendarMonth] for the given year and month
     */
    override fun getMonth(
        year: Int,
        month: Int,
    ): CalendarMonth = getMonth(LocalDate.of(year, month, 1))

    /**
     * Gets a date with the specified year, month, and day.
     *
     * @param year The year
     * @param month The month (1-12)
     * @param day The day of month (1-31)
     * @return The [CalendarDate] for the given year, month, and day
     */
    override fun getDate(
        year: Int,
        month: Int,
        day: Int,
    ): CalendarDate = CalendarDate(year, month, day, 0)

    /**
     * Gets the day of week for the given date.
     *
     * @param date The [CalendarDate] to get the day of week for
     * @return The day of week (1 = Sunday, 2 = Monday, etc.)
     */
    override fun getDayOfWeek(date: CalendarDate): Int = date.toLocalDate().dayOfWeek.value

    /**
     * Adds months to a month.
     *
     * @param from The [CalendarMonth] to add months to
     * @param addedMonthsCount The number of months to add
     * @return The [CalendarMonth] with added months
     */
    override fun plusMonths(
        from: CalendarMonth,
        addedMonthsCount: Int,
    ): CalendarMonth {
        if (addedMonthsCount <= 0) return from

        val firstDayLocalDate = from.toLocalDate()
        val laterMonth = firstDayLocalDate.plusMonths(addedMonthsCount.toLong())
        return getMonth(laterMonth)
    }

    /**
     * Subtracts months from a month.
     *
     * @param from The [CalendarMonth] to subtract months from
     * @param subtractedMonthsCount The number of months to subtract
     * @return The [CalendarMonth] with subtracted months
     */
    override fun minusMonths(
        from: CalendarMonth,
        subtractedMonthsCount: Int,
    ): CalendarMonth {
        if (subtractedMonthsCount <= 0) return from

        val firstDayLocalDate = from.toLocalDate()
        val earlierMonth = firstDayLocalDate.minusMonths(subtractedMonthsCount.toLong())
        return getMonth(earlierMonth)
    }

    /**
     * Formats a date using a custom pattern.
     *
     * @param utcTimeMillis The UTC timestamp to format (milliseconds from epoch)
     * @param pattern The date format pattern to use
     * @param locale The [Locale] to use when formatting the date
     * @return The formatted date string
     */
    override fun formatWithPattern(
        utcTimeMillis: Long,
        pattern: String,
        locale: Locale,
    ): String = Companion.formatWithPattern(utcTimeMillis, pattern, locale)

    /**
     * Parses a date string using the provided pattern.
     *
     * @param date The date string to parse
     * @param pattern The date format pattern to use
     * @return The parsed [CalendarDate] or null if parsing fails
     */
    override fun parse(
        date: String,
        pattern: String,
    ): CalendarDate? {
        // TODO: A DateTimeFormatter can be reused.
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return try {
            val localDate = LocalDate.parse(date, formatter)
            CalendarDate(
                year = localDate.year,
                month = localDate.month.value,
                dayOfMonth = localDate.dayOfMonth,
                utcTimeMillis =
                    localDate
                        .atTime(LocalTime.MIDNIGHT)
                        .atZone(utcTimeZoneId)
                        .toInstant()
                        .toEpochMilli(),
            )
        } catch (pe: DateTimeParseException) {
            null
        }
    }

    /**
     * Returns a string representation of this calendar model.
     *
     * @return The string "CalendarModel"
     */
    override fun toString(): String = "CalendarModel"

    companion object {
        /**
         * Formats a UTC timestamp into a string with a given date format pattern.
         *
         * @param utcTimeMillis a UTC timestamp to format (milliseconds from epoch)
         * @param pattern a date format pattern
         * @param locale the [Locale] to use when formatting the given timestamp
         * @return The formatted date string
         */
        fun formatWithPattern(
            utcTimeMillis: Long,
            pattern: String,
            locale: Locale,
        ): String {
            val formatter: DateTimeFormatter =
                DateTimeFormatter
                    .ofPattern(pattern, locale)
                    .withDecimalStyle(DecimalStyle.of(locale))
            return Instant
                .ofEpochMilli(utcTimeMillis)
                .atZone(utcTimeZoneId)
                .toLocalDate()
                .format(formatter)
        }

        /**
         * Holds a UTC [ZoneId].
         *
         * @return The UTC [ZoneId] for time zone operations
         */
        internal val utcTimeZoneId: ZoneId = ZoneId.of("UTC")
    }

    /**
     * Gets a calendar month from a LocalDate.
     *
     * @param firstDayLocalDate The first day of the month as a LocalDate
     * @return The [CalendarMonth] for the given LocalDate
     */
    private fun getMonth(firstDayLocalDate: LocalDate): CalendarMonth {
        val difference = firstDayLocalDate.dayOfWeek.value - firstDayOfWeek
        val daysFromStartOfWeekToFirstOfMonth =
            if (difference < 0) {
                difference + DaysInWeek
            } else {
                difference
            }
        val firstDayEpochMillis =
            firstDayLocalDate
                .atTime(LocalTime.MIDNIGHT)
                .atZone(utcTimeZoneId)
                .toInstant()
                .toEpochMilli()
        return CalendarMonth(
            year = firstDayLocalDate.year,
            month = firstDayLocalDate.monthValue,
            numberOfDays = firstDayLocalDate.lengthOfMonth(),
            daysFromStartOfWeekToFirstOfMonth = daysFromStartOfWeekToFirstOfMonth,
            startUtcTimeMillis = firstDayEpochMillis,
        )
    }

    /**
     * Converts a CalendarMonth to a LocalDate.
     *
     * @return The [LocalDate] representation of the calendar month
     */
    private fun CalendarMonth.toLocalDate(): LocalDate = Instant.ofEpochMilli(startUtcTimeMillis).atZone(
        utcTimeZoneId
    ).toLocalDate()

    /**
     * Converts a CalendarDate to a LocalDate.
     *
     * @return The [LocalDate] representation of the calendar date
     */
    private fun CalendarDate.toLocalDate(): LocalDate =
        LocalDate.of(
            this.year,
            this.month,
            this.dayOfMonth,
        )
}