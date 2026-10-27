package zone.ien.hig

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertRangeInfoEquals
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.v2.runComposeUiTest
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class CupertinoSliderAccessibilityTest {
    @Test
    fun sliderExposesProgressAndAcceptsAccessibilityAdjustment() = runComposeUiTest {
        var value by mutableFloatStateOf(0.5f)

        setContent {
            CupertinoSlider(
                value = value,
                onValueChange = { value = it },
                modifier = Modifier.testTag("slider"),
                backdrop = rememberLayerBackdrop(),
            )
        }

        val slider = onNodeWithTag("slider")
        slider.assertRangeInfoEquals(ProgressBarRangeInfo(0.5f, 0f..1f, 0))

        var accepted = false
        slider.performSemanticsAction(SemanticsActions.SetProgress) { setProgress ->
            accepted = setProgress(0.8f)
        }

        runOnIdle {
            assertTrue(accepted)
            assertEquals(0.8f, value, 0.0001f)
        }
    }

    @Test
    fun sliderCanBeAdjustedWithKeyboard() = runComposeUiTest {
        var value by mutableFloatStateOf(0.5f)

        setContent {
            CupertinoSlider(
                value = value,
                onValueChange = { value = it },
                modifier = Modifier.testTag("slider"),
                backdrop = rememberLayerBackdrop(),
            )
        }

        val slider = onNodeWithTag("slider")
        slider.performSemanticsAction(SemanticsActions.RequestFocus) { requestFocus -> requestFocus() }
        slider.performKeyInput { pressKey(Key.DirectionRight) }

        runOnIdle { assertEquals(0.51f, value, 0.0001f) }
    }

    @Test
    fun rangeSliderExposesSignedValuesAndAdjustsEachThumbWithinTheRange() = runComposeUiTest {
        var value by mutableStateOf(-8f..-2f)

        setContent {
            CupertinoRangeSlider(
                value = value,
                onValueChange = { value = it },
                valueRange = -10f..-1f,
                modifier = Modifier.testTag("range-slider"),
                backdrop = rememberLayerBackdrop(),
            )
        }

        onNode(
            hasProgressBarRangeInfo(ProgressBarRangeInfo(-8f, -10f..-2f, 0)),
            useUnmergedTree = true,
        ).assertRangeInfoEquals(ProgressBarRangeInfo(-8f, -10f..-2f, 0))
        onNode(
            hasProgressBarRangeInfo(ProgressBarRangeInfo(-2f, -8f..-1f, 0)),
            useUnmergedTree = true,
        ).assertRangeInfoEquals(ProgressBarRangeInfo(-2f, -8f..-1f, 0))

        var startAccepted = false
        onNode(
            hasProgressBarRangeInfo(ProgressBarRangeInfo(-8f, -10f..-2f, 0)),
            useUnmergedTree = true,
        ).performSemanticsAction(SemanticsActions.SetProgress) { setProgress ->
            startAccepted = setProgress(-7f)
        }
        runOnIdle {
            assertTrue(startAccepted)
            assertEquals(-7f..-2f, value)
        }

        var endAccepted = false
        onNode(
            hasProgressBarRangeInfo(ProgressBarRangeInfo(-2f, -7f..-1f, 0)),
            useUnmergedTree = true,
        ).performSemanticsAction(SemanticsActions.SetProgress) { setProgress ->
            endAccepted = setProgress(-3f)
        }
        runOnIdle {
            assertTrue(endAccepted)
            assertEquals(-7f..-3f, value)
        }
    }

    @Test
    fun rangeSliderCanAdjustTheFocusedThumbWithKeyboard() = runComposeUiTest {
        var value by mutableStateOf(0.2f..0.8f)

        setContent {
            CupertinoRangeSlider(
                value = value,
                onValueChange = { value = it },
                modifier = Modifier.testTag("range-slider"),
                backdrop = rememberLayerBackdrop(),
            )
        }

        val startThumb = onNode(
            hasProgressBarRangeInfo(ProgressBarRangeInfo(0.2f, 0f..0.8f, 0)),
            useUnmergedTree = true,
        )
        startThumb.performSemanticsAction(SemanticsActions.RequestFocus) { requestFocus -> requestFocus() }
        startThumb.performKeyInput { pressKey(Key.DirectionRight) }

        runOnIdle {
            assertEquals(0.21f, value.start, 0.0001f)
            assertEquals(0.8f, value.endInclusive, 0.0001f)
        }
    }

    @Test
    fun rangeSliderReportsAvailableDiscreteStepsAndCanIncreaseEndThumb() = runComposeUiTest {
        var value by mutableStateOf(8f..9f)

        setContent {
            CupertinoRangeSlider(
                value = value,
                onValueChange = { value = it },
                steps = 9,
                valueRange = 0f..10f,
                modifier = Modifier.testTag("range-slider"),
                backdrop = rememberLayerBackdrop(),
            )
        }

        onNode(
            hasProgressBarRangeInfo(ProgressBarRangeInfo(8f, 0f..9f, 8)),
            useUnmergedTree = true,
        ).assertRangeInfoEquals(ProgressBarRangeInfo(8f, 0f..9f, 8))
        val endThumb = onNode(
            hasProgressBarRangeInfo(ProgressBarRangeInfo(9f, 8f..10f, 1)),
            useUnmergedTree = true,
        )
        endThumb.assertRangeInfoEquals(ProgressBarRangeInfo(9f, 8f..10f, 1))

        var accepted = false
        endThumb.performSemanticsAction(SemanticsActions.SetProgress) { setProgress ->
            accepted = setProgress(10f)
        }

        runOnIdle {
            assertTrue(accepted)
            assertEquals(8f..10f, value)
        }
    }

    @Test
    fun rangeSliderReportsOffGridThumbStepsAndAllowsBothBoundaryAdjustments() = runComposeUiTest {
        var value by mutableStateOf(8f..9.5f)

        setContent {
            CupertinoRangeSlider(
                value = value,
                onValueChange = { value = it },
                steps = 9,
                valueRange = 0f..10f,
                modifier = Modifier.testTag("range-slider"),
                backdrop = rememberLayerBackdrop(),
            )
        }

        val startThumb = onNode(
            hasProgressBarRangeInfo(ProgressBarRangeInfo(8f, 0f..9.5f, 9)),
            useUnmergedTree = true,
        )
        val endThumb = onNode(
            hasProgressBarRangeInfo(ProgressBarRangeInfo(9.5f, 8f..10f, 1)),
            useUnmergedTree = true,
        )
        startThumb.assertRangeInfoEquals(ProgressBarRangeInfo(8f, 0f..9.5f, 9))
        endThumb.assertRangeInfoEquals(ProgressBarRangeInfo(9.5f, 8f..10f, 1))

        var startAccepted = false
        startThumb.performSemanticsAction(SemanticsActions.SetProgress) { setProgress ->
            startAccepted = setProgress(9.5f)
        }
        runOnIdle {
            assertTrue(startAccepted)
            assertEquals(9.5f..9.5f, value)
            value = 8f..9.5f
        }

        var endAccepted = false
        onNode(
            hasProgressBarRangeInfo(ProgressBarRangeInfo(9.5f, 8f..10f, 1)),
            useUnmergedTree = true,
        ).performSemanticsAction(SemanticsActions.SetProgress) { setProgress ->
            endAccepted = setProgress(8f)
        }
        runOnIdle {
            assertTrue(endAccepted)
            assertEquals(8f..8f, value)
        }
    }

    @Test
    fun rangeSliderSetProgressCanSelectAnOffGridThumbMinimum() = runComposeUiTest {
        var value by mutableStateOf(8.5f..9.5f)

        setContent {
            CupertinoRangeSlider(
                value = value,
                onValueChange = { value = it },
                steps = 9,
                valueRange = 0f..10f,
                modifier = Modifier.testTag("range-slider"),
                backdrop = rememberLayerBackdrop(),
            )
        }

        val endThumb = onNode(
            hasProgressBarRangeInfo(ProgressBarRangeInfo(9.5f, 8.5f..10f, 1)),
            useUnmergedTree = true,
        )
        var accepted = false
        endThumb.performSemanticsAction(SemanticsActions.SetProgress) { setProgress ->
            accepted = setProgress(8.5f)
        }

        runOnIdle {
            assertTrue(accepted)
            assertEquals(8.5f..8.5f, value)
        }
    }

    @Test
    fun disabledSliderRejectsAccessibilityAdjustment() = runComposeUiTest {
        var value by mutableFloatStateOf(0.5f)

        setContent {
            CupertinoSlider(
                value = value,
                onValueChange = { value = it },
                enabled = false,
                modifier = Modifier.testTag("slider"),
                backdrop = rememberLayerBackdrop(),
            )
        }

        val slider = onNodeWithTag("slider")
        slider.assertIsNotEnabled()

        var accepted = true
        slider.performSemanticsAction(SemanticsActions.SetProgress) { setProgress ->
            accepted = setProgress(0.8f)
        }
        runOnIdle {
            assertFalse(accepted)
            assertEquals(0.5f, value, 0.0001f)
        }
    }
}
