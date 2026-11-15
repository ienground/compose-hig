@file:OptIn(zone.ien.hig.ExperimentalCupertinoApi::class)

package cupertino

import IsIos
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant
import zone.ien.hig.CupertinoBorderedSecureTextField
import zone.ien.hig.CupertinoBorderedTextField
import zone.ien.hig.CupertinoButton
import zone.ien.hig.CupertinoButtonDefaults
import zone.ien.hig.CupertinoDatePicker
import zone.ien.hig.CupertinoDateTimePicker
import zone.ien.hig.CupertinoSearchTextField
import zone.ien.hig.CupertinoSecureTextField
import zone.ien.hig.CupertinoText
import zone.ien.hig.CupertinoTextField
import zone.ien.hig.CupertinoWheelPicker
import zone.ien.hig.DatePickerStyle
import zone.ien.hig.rememberCupertinoDatePickerState
import zone.ien.hig.rememberCupertinoDateTimePickerState
import zone.ien.hig.rememberCupertinoPickerState
import zone.ien.hig.rememberCupertinoSearchTextFieldState
import zone.ien.hig.rememberCupertinoTimePickerState

@Composable
internal fun TextFieldDemo() {
    var text by rememberSaveable { mutableStateOf("") }
    var enabled by rememberSaveable { mutableStateOf(true) }
    var readOnly by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf(false) }
    var singleLine by rememberSaveable { mutableStateOf(true) }
    val password = rememberTextFieldState()
    val focusManager = LocalFocusManager.current
    DemoSection("Preview", "Both fields share the same value.") {
        CupertinoText(text = "Plain field")
        CupertinoTextField(
            value = text, onValueChange = { text = it }, enabled = enabled, readOnly = readOnly,
            isError = error, singleLine = singleLine, modifier = Modifier.fillMaxWidth(),
            placeholder = { CupertinoText(text = "Enter your name") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        )
        CupertinoText(text = "Bordered field")
        CupertinoBorderedTextField(
            value = text, onValueChange = { text = it }, enabled = enabled, readOnly = readOnly,
            isError = error, singleLine = singleLine, modifier = Modifier.fillMaxWidth(),
            placeholder = { CupertinoText(text = "Enter your name") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        )
        if (error) CupertinoText(text = "This field has a validation error.")
        DemoResult("Input: ${text.ifEmpty { "(empty)" }} · ${text.length} characters")
        CupertinoButton(onClick = { text = "" }, enabled = enabled && !readOnly && text.isNotEmpty(),
            colors = CupertinoButtonDefaults.tintedButtonColors()) { CupertinoText(text = "Clear input") }
    }
    DemoSection("Properties") {
        DemoToggle("Enabled", enabled) { enabled = it }
        DemoToggle("Read only", readOnly) { readOnly = it }
        DemoToggle("Error", error) { error = it }
        DemoToggle("Single line", singleLine) { singleLine = it }
    }
    DemoSection("Secure fields", "Secure fields mask the same shared password.") {
        CupertinoSecureTextField(state = password, enabled = enabled, readOnly = readOnly,
            placeholder = { CupertinoText(text = "Password") }, modifier = Modifier.fillMaxWidth())
        CupertinoBorderedSecureTextField(state = password, enabled = enabled, readOnly = readOnly,
            placeholder = { CupertinoText(text = "Password") }, modifier = Modifier.fillMaxWidth())
        DemoResult("Password length: ${password.text.length}")
    }
}

@Composable
internal fun SearchFieldDemo(scrollState: ScrollableState) {
    var query by rememberSaveable { mutableStateOf("") }
    var enabled by rememberSaveable { mutableStateOf(true) }
    val searchState = rememberCupertinoSearchTextFieldState(scrollableState = scrollState,
        blockScrollWhenFocusedAndEmpty = false)
    val focusManager = LocalFocusManager.current
    val results = listOf("Seoul", "Busan", "Incheon", "Daejeon", "Daegu", "Gwangju", "Jeju").filter { it.contains(query.trim(), ignoreCase = true) }
    DemoSection("Preview", "Search for a city. Cancel clears the query and focus.") {
        CupertinoSearchTextField(
            value = query, onValueChange = { query = it }, state = searchState, enabled = enabled,
            paddingValues = PaddingValues(0.dp), modifier = Modifier.fillMaxWidth(),
            placeholder = { CupertinoText(text = "Search cities") },
            cancelButton = {
                CupertinoButton(onClick = { query = ""; focusManager.clearFocus() },
                    colors = CupertinoButtonDefaults.plainButtonColors()) { CupertinoText(text = "Cancel") }
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    CupertinoButton(onClick = { query = "" }, enabled = enabled,
                        colors = CupertinoButtonDefaults.plainButtonColors()) { CupertinoText(text = "Clear") }
                }
            },
        )
        DemoResult("Query: ${query.ifEmpty { "(none)" }} · ${results.size} results")
        if (results.isEmpty()) CupertinoText(text = "No cities found.")
        results.forEach { CupertinoText(text = it) }
    }
    DemoSection("Properties") { DemoToggle("Enabled", enabled) { enabled = it } }
}

@Composable
internal fun WheelPickerDemo() {
    var infinite by rememberSaveable { mutableStateOf(true) }
    var rotation by rememberSaveable { mutableStateOf(true) }
    var enabled by rememberSaveable { mutableStateOf(true) }
    val values = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    var selected by rememberSaveable { mutableIntStateOf(0) }
    DemoSection("Preview", "Drag the wheel to select a day.") {
        key(infinite) {
            val state = rememberCupertinoPickerState(infinite = infinite, initiallySelectedItemIndex = selected)
            val selectedIndex = state.selectedItemIndex(values.size)
            androidx.compose.runtime.LaunchedEffect(selectedIndex) {
                selected = selectedIndex
            }
            CupertinoWheelPicker(state = state, items = values, enabled = enabled, withRotation = rotation,
                modifier = Modifier.fillMaxWidth()) { CupertinoText(text = it) }
            DemoResult("Selected: ${values[selectedIndex]} · Index: $selectedIndex")
        }
    }
    DemoSection("Properties") {
        DemoToggle("Enabled", enabled) { enabled = it }
        DemoToggle("Infinite scrolling", infinite) { infinite = it }
        DemoToggle("Wheel rotation", rotation) { rotation = it }
    }
}

@Composable
internal fun DateTimeDemo(component: CatalogComponent) {
    var native by rememberSaveable { mutableStateOf(false) }
    var calendar by rememberSaveable { mutableStateOf(false) }
    var is24Hour by rememberSaveable { mutableStateOf(true) }
    var hour by rememberSaveable { mutableIntStateOf(9) }
    var minute by rememberSaveable { mutableIntStateOf(30) }
    DemoSection("Preview") {
        when (component) {
            CatalogComponent.DatePicker -> {
                val state = rememberCupertinoDatePickerState()
                if (native) DatePickerExample(state, native = true)
                else CupertinoDatePicker(state = state, modifier = Modifier.fillMaxWidth(),
                    style = if (calendar) DatePickerStyle.Pager() else DatePickerStyle.Wheel())
                DemoResult("Date: ${Instant.fromEpochMilliseconds(state.selectedDateMillis).toLocalDateTime(TimeZone.UTC).date}")
            }
            CatalogComponent.TimePicker -> key(is24Hour) {
                val state = rememberCupertinoTimePickerState(initialHour = hour, initialMinute = minute, is24Hour = is24Hour)
                androidx.compose.runtime.LaunchedEffect(state.hour, state.minute) { hour = state.hour; minute = state.minute }
                TimePickerExample(state, native)
                DemoResult("Time: ${state.hour.toString().padStart(2, '0')}:${state.minute.toString().padStart(2, '0')}")
            }
            CatalogComponent.DateTimePicker -> {
                val state = rememberCupertinoDateTimePickerState(initialHour = 9, initialMinute = 30)
                if (native) DateTimePicker(state, native = true)
                else CupertinoDateTimePicker(state = state, modifier = Modifier.fillMaxWidth(),
                    style = if (calendar) DatePickerStyle.Pager() else DatePickerStyle.Wheel())
                DemoResult("Date and time: ${Instant.fromEpochMilliseconds(state.selectedDateTimeMillis).toLocalDateTime(TimeZone.UTC)}")
            }
            else -> Unit
        }
    }
    DemoSection("Properties") {
        DemoToggle("Native implementation", native) { native = it }
        if (!native && component != CatalogComponent.TimePicker) {
            DemoToggle("Calendar style", calendar) { calendar = it }
        }
        if (component == CatalogComponent.TimePicker) {
            DemoToggle("24-hour time", is24Hour) { is24Hour = it }
        }
        NativeImplementationNote(native)
    }
}

@Composable
internal fun NativeImplementationNote(native: Boolean) {
    CupertinoText(text = when {
        !native -> "Rendered with Compose"
        IsIos -> "Rendered with UIKit"
        else -> "Native uses UIKit on iOS and Compose on this platform."
    })
}
