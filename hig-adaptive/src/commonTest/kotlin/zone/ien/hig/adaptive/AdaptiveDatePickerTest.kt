package zone.ien.hig.adaptive

import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import zone.ien.hig.CupertinoDatePickerState
import zone.ien.hig.ExperimentalCupertinoApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalAdaptiveApi::class, ExperimentalCupertinoApi::class, ExperimentalMaterial3Api::class, ExperimentalTestApi::class)
class AdaptiveDatePickerTest {
    @Test
    fun materialPickerUsesTheCupertinoYearRange() = runComposeUiTest {
        val initialDate = 1_761_523_200_000L // 2025-10-27 UTC
        val state = CupertinoDatePickerState(initialDate, 2025..2027)
        lateinit var materialState: DatePickerState

        setContent {
            MaterialTheme {
                CompositionLocalProvider(LocalTheme provides Theme.Material3) {
                    AdaptiveDatePicker(state, adaptation = {
                        material { materialState = this.state }
                    })
                }
            }
        }

        runOnIdle {
            assertEquals(2025..2027, materialState.yearRange)
            materialState.selectedDateMillis = 1_704_067_200_000L // 2024-01-01 UTC
            assertNull(materialState.selectedDateMillis)
            assertEquals(initialDate, state.selectedDateMillis)
        }
        runOnIdle {
            assertEquals(initialDate, state.selectedDateMillis)
            materialState.selectedDateMillis = 1_798_329_600_000L // 2026-12-27 UTC
        }
        runOnIdle { assertEquals(1_798_329_600_000L, state.selectedDateMillis) }
    }
}
