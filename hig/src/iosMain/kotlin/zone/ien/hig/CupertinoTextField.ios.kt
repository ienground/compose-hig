package zone.ien.hig

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.input.PlatformImeOptions

@OptIn(ExperimentalComposeUiApi::class)
internal actual fun KeyboardOptions.enableNativeInput() =
    this
//    copy(platformImeOptions = PlatformImeOptions { usingNativeTextInput(true) })