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
import android.text.format.DateFormat
import androidx.annotation.ChecksSdkIntAtLeast


internal actual object PlatformDateFormat {

    /**
     * The delegate for date formatting operations based on API level.
     * For API level 26 and above, uses AndroidCalendarModelImpl; otherwise, throws an error.
     */
    private val delegate by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AndroidCalendarModelImpl()
        } else error("should not be used for api < 26")
    }

    /**
     * Gets the first day of the week for the current locale.
     *
     * @return the first day of the week (1 = Sunday, 2 = Monday, etc.)
     */
    actual val firstDayOfWeek: Int
        get() = apiCheck(
            old = { LegacyDateFormat.firstDayOfWeek },
            new = { delegate.firstDayOfWeek }
        )

    /**
     * Formats a date using a custom pattern.
     *
     * @param utcTimeMillis The UTC timestamp to format (milliseconds from epoch)
     * @param pattern The date format pattern to use
     * @param locale The [CalendarLocale] to use when formatting the date
     * @return The formatted date string
     */
    actual fun formatWithPattern(
        utcTimeMillis: Long,
        pattern: String,
        locale: CalendarLocale
    ): String = apiCheck(
        old = {
            LegacyDateFormat.formatWithPattern(utcTimeMillis, pattern, locale)
        },
        new = {
            delegate.formatWithPattern(utcTimeMillis, pattern, locale)
        }
    )

    /**
     * Formats a date using a skeleton pattern.
     *
     * @param utcTimeMillis The UTC timestamp to format (milliseconds from epoch)
     * @param skeleton The date format skeleton to use
     * @param locale The [CalendarLocale] to use when formatting the date
     * @return The formatted date string
     */
    actual fun formatWithSkeleton(
        utcTimeMillis: Long,
        skeleton: String,
        locale: CalendarLocale
    ): String = apiCheck(
        old = {
            LegacyDateFormat.formatWithSkeleton(utcTimeMillis, skeleton, locale)
        },
        new = {
            val pattern = DateFormat.getBestDateTimePattern(locale, skeleton)
            AndroidCalendarModelImpl.formatWithPattern(utcTimeMillis, pattern, locale)
        }
    )

    /**
     * Parses a date string using the provided pattern.
     *
     * @param date The date string to parse
     * @param pattern The date format pattern to use
     * @return The parsed [CalendarDate] or null if parsing fails
     */
    actual fun parse(
        date: String,
        pattern: String
    ): CalendarDate? = apiCheck(
        old = {
            LegacyDateFormat.parse(date, pattern)
        },
        new = {
            delegate.parse(date, pattern)
        }
    )

    /**
     * Gets the date input format for the given locale.
     *
     * @param locale The [CalendarLocale] to get the input format for
     * @return The [DateInputFormat] for the specified locale
     */
    actual fun getDateInputFormat(locale: CalendarLocale): DateInputFormat {
        return apiCheck(
            old = { LegacyDateFormat.getDateInputFormat(locale) },
            new = { delegate.getDateInputFormat(locale) }
        )
    }

    /**
     * Gets the weekday names for the given locale.
     *
     * From CalendarModelImpl.android.kt weekdayNames.
     *
     * Legacy model returns short ('Mon') format while newer version returns narrow ('M') format
     *
     * @param locale The [CalendarLocale] to get weekday names for
     * @return A list of pairs containing full and short weekday names
     */
    actual fun weekdayNames(locale: CalendarLocale): List<Pair<String, String>> {
        return apiCheck(
            old = { LegacyDateFormat.weekdayNames(locale) },
            new = { delegate.weekdayNames(locale) }
        )
    }

    /**
     * Gets the month names for the given locale.
     *
     * @param locale The [CalendarLocale] to get month names for
     * @return A list of month names
     */
    actual fun monthsNames(locale: CalendarLocale): List<String> {
        return LegacyDateFormat.monthsNames(locale)
    }

    /**
     * Checks if the time format is 24-hour for the given locale.
     *
     * https://android.googlesource.com/platform/frameworks/base/+/jb-release/core/java/android/text/format/DateFormat.java
     *
     * public static boolean is24HourFormat(Context context) -- used by Android date format
     *
     * @param locale The [CalendarLocale] to check
     * @return true if the locale uses 24-hour format, false otherwise
     */
    actual fun is24HourFormat(locale: CalendarLocale): Boolean {
        return LegacyDateFormat.is24HourFormat(locale)
    }
}

/**
 * Checks API level and chooses the appropriate implementation.
 *
 * @param old The function to execute on older API versions
 * @param new The function to execute on newer API versions
 * @return The result of the appropriate function
 */
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O, lambda = 2)
private fun <T> apiCheck(old: () -> T, new: () -> T): T {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        new() else old()
}
