package zone.ien.hig.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CupertinoGlassContrastTest {
    @Test
    fun brightAccentsUseDarkForeground() {
        listOf(Color(0xFFFFCC00), Color(0xFFFF9500), Color.White).forEach { background ->
            val foreground = CupertinoGlassDefaults.contentColor(background)
            assertEquals(Color.Black, foreground)
            assertTrue(contrast(background, foreground) >= 4.5f)
        }
    }

    @Test
    fun darkAccentsUseLightForeground() {
        listOf(Color(0xFF003399), Color(0xFF282A30), Color.Black).forEach { background ->
            val foreground = CupertinoGlassDefaults.contentColor(background)
            assertEquals(Color.White, foreground)
            assertTrue(contrast(background, foreground) >= 4.5f)
        }
    }

    @Test
    fun nativeMidToneAccentsKeepLightForeground() {
        listOf(Color(0xFF007AFF), Color(0xFF438FFF), Color(0xFFE86A00)).forEach { background ->
            assertEquals(Color.White, CupertinoGlassDefaults.contentColor(background))
            assertTrue(contrast(background, Color.White) >= 3f)
        }
    }

    @Test
    fun foregroundRetainsContrastAcrossAccentSpectrum() {
        for (red in 0..255 step 51) {
            for (green in 0..255 step 51) {
                for (blue in 0..255 step 51) {
                    val background = Color(red, green, blue)
                    assertTrue(contrast(background, CupertinoGlassDefaults.contentColor(background)) >= 3f)
                }
            }
        }
    }

    @Test
    fun translucentAccentsAccountForTheUnderlyingTheme() {
        val tint = Color(0xFFFF9500).copy(alpha = 0.2f)
        listOf(Color.White, Color.Black).forEach { underlay ->
            val foreground = CupertinoGlassDefaults.contentColor(tint, underlay)
            assertTrue(contrast(tint.compositeOver(underlay), foreground) >= 4.5f)
        }
        assertEquals(Color.Black, CupertinoGlassDefaults.contentColor(tint, Color.White))
        assertEquals(Color.White, CupertinoGlassDefaults.contentColor(tint, Color.Black))
    }

    private fun contrast(background: Color, foreground: Color): Float {
        val first = background.luminance()
        val second = foreground.luminance()
        return (maxOf(first, second) + 0.05f) / (minOf(first, second) + 0.05f)
    }
}
