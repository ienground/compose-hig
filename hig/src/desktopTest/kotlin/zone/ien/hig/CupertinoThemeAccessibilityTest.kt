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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertNotEquals
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.theme.lightColorScheme

@OptIn(ExperimentalTestApi::class, InternalCupertinoApi::class)
class CupertinoThemeAccessibilityTest {
    @Test
    fun themeRecomposesWhenHighContrastPreferenceChanges() = runComposeUiTest {
        try {
            updateHighContrastState(false)
            setContent {
                CupertinoTheme(colorScheme = lightColorScheme()) {
                    Box(
                        Modifier
                            .size(100.dp)
                            .background(CupertinoTheme.colorScheme.secondaryLabel)
                            .testTag("contrast-color"),
                    )
                }
            }

            val before = onNodeWithTag("contrast-color").captureToImage().toPixelMap()[50, 50]
            updateHighContrastState(true)
            waitForIdle()
            val after = onNodeWithTag("contrast-color").captureToImage().toPixelMap()[50, 50]

            assertNotEquals(before, after)
        } finally {
            updateHighContrastState(false)
        }
    }
}
