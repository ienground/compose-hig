package zone.ien.hig.utils

import androidx.compose.ui.geometry.Offset
import kotlin.test.Test
import kotlin.test.assertEquals

class CupertinoBackdropCoordinatesTest {
    @Test
    fun windowOriginAndInsetsTranslateSampling() {
        val transform = backdropTransform(Offset(-40f, -96f), Offset(1f, 0f), Offset(0f, 1f))
        assertEquals(Offset.Zero, transform.map(Offset(40f, 96f)))
        assertEquals(Offset(120f, 80f), transform.map(Offset(160f, 176f)))
    }

    @Test
    fun animatedScaleAndLandscapeRotationKeepSourceAligned() {
        val transform = backdropTransform(Offset(15f, 30f), Offset(0f, 0.5f), Offset(-0.5f, 0f))
        assertEquals(Offset(5f, 35f), transform.map(Offset(10f, 20f)))
    }
}
