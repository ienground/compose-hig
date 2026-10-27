/*
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

import android.content.Context
import android.os.Build
import android.view.accessibility.AccessibilityManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

@OptIn(InternalCupertinoApi::class)
@Composable
internal actual fun ObserveHighContrastSetting() {
    val context = LocalContext.current
    val accessibilityManager =
        context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager

    DisposableEffect(context, accessibilityManager) {
        if (Build.VERSION.SDK_INT < 36 || accessibilityManager == null) {
            updateHighContrastState(false)
            onDispose {}
        } else {
            val listener = AccessibilityManager.HighContrastTextStateChangeListener { enabled ->
                updateHighContrastState(enabled)
            }
            accessibilityManager.addHighContrastTextStateChangeListener(context.mainExecutor, listener)
            updateHighContrastState(accessibilityManager.isHighContrastTextEnabled)
            onDispose {
                accessibilityManager.removeHighContrastTextStateChangeListener(listener)
            }
        }
    }
}
