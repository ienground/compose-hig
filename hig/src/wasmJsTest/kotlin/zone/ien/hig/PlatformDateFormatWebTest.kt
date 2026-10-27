package zone.ien.hig

import androidx.compose.ui.text.intl.Locale
import kotlin.test.Test
import kotlin.test.assertEquals

class PlatformDateFormatWebTest {
    @Test
    fun dateOnlySkeletonUsesUtcAtPositiveOffsetBoundary() {
        val timestamp = kotlin.time.Instant.parse("2026-10-27T23:30:00Z").toEpochMilliseconds()

        assertEquals(
            "10/27/2026",
            PlatformDateFormat.formatWithSkeleton(timestamp, "yMd", Locale("en-US")),
        )
    }

    @Test
    fun patternMonthNameUsesUtcAtMonthBoundary() {
        val timestamp = kotlin.time.Instant.parse("2026-10-31T23:30:00Z").toEpochMilliseconds()

        assertEquals(
            "October 31, 2026",
            PlatformDateFormat.formatWithPattern(timestamp, "MMMM dd, yyyy", Locale("en-US")),
        )
    }

    @Test
    fun arabicAndThaiDateInputFormatsKeepAllThreeFields() {
        assertEquals("dd/MM/yyyy", PlatformDateFormat.getDateInputFormat(Locale("ar-EG")).patternWithDelimiters)
        assertEquals("dd/MM/yyyy", PlatformDateFormat.getDateInputFormat(Locale("th-TH")).patternWithDelimiters)
    }
}
