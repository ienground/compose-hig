package zone.ien.hig

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCupertinoApi::class)
class CupertinoSheetDetentTest {
    @Test
    fun fixedDetentUsesDensityWithoutMultiplyingViewportHeight() {
        val detent = PresentationDetent.Height(200.dp)
        assertEquals(400f, detent.calculate(Density(2f), 1200f))
        assertEquals(400f, detent.calculate(Density(2f), 1800f))
    }

    @Test
    fun fixedDetentFitsShortLandscapeViewport() {
        val detent = PresentationDetent.Height(400.dp)
        assertEquals(300f, detent.calculate(Density(2f), 300f))
    }

    @Test
    fun fixedDetentCanBeSerializedFromUseSite() {
        val detent = PresentationDetent.Height(200.dp)

        val encoded = Json.encodeToString(detent)

        assertEquals(200f, Json.decodeFromString<Float>(encoded))
        assertEquals(detent, Json.decodeFromString<PresentationDetent.Height>(encoded))
    }
}
