package zone.ien.hig

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCupertinoApi::class, ExperimentalTestApi::class)
class CupertinoStepperTest {
    @Test
    fun buttonsUpdateValueWithinRange() = runComposeUiTest {
        var value by mutableIntStateOf(1)

        setContent {
            CupertinoStepper(
                value = value,
                onValueChange = { value = it },
                valueRange = 0..2,
            )
        }

        onNodeWithContentDescription("Increase").performClick()
        assertEquals(2, value)

        onNodeWithContentDescription("Decrease").performClick()
        assertEquals(1, value)
    }

    @Test
    fun buttonsExposeBoundaryDisabledState() = runComposeUiTest {
        setContent {
            CupertinoStepper(
                value = 0,
                onValueChange = {},
                valueRange = 0..1,
            )
        }

        onNodeWithContentDescription("Decrease").assertIsNotEnabled()
    }

    @Test
    fun arrowKeysAdjustTheFocusedStepper() = runComposeUiTest {
        var value by mutableIntStateOf(1)

        setContent {
            CupertinoStepper(
                value = value,
                onValueChange = { value = it },
                valueRange = 0..2,
                modifier = Modifier.testTag("stepper"),
            )
        }

        onNodeWithTag("stepper")
            .performSemanticsAction(SemanticsActions.RequestFocus) { requestFocus -> requestFocus() }
        onNodeWithTag("stepper").performKeyInput { pressKey(Key.DirectionUp) }

        assertEquals(2, value)
    }
}
