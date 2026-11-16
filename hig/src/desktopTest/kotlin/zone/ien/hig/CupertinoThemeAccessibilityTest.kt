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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.LocalSystemTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.SystemTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertSame
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.theme.LocalColorScheme
import zone.ien.hig.theme.darkColorScheme
import zone.ien.hig.theme.isInitializedCupertinoTheme
import zone.ien.hig.theme.lightColorScheme

@Suppress("DEPRECATION")
@OptIn(ExperimentalTestApi::class, InternalCupertinoApi::class, InternalComposeUiApi::class)
class CupertinoThemeAccessibilityTest {
    @Test
    fun unwrappedColorsFollowSystemAppearanceChanges() = runComposeUiTest {
        val systemTheme = mutableStateOf(SystemTheme.Light)
        var isDark: Boolean? = null
        var initialized = true
        var background = Color.Unspecified
        var buttonContent = Color.Unspecified
        setContent {
            CompositionLocalProvider(LocalSystemTheme provides systemTheme.value) {
                val scheme = CupertinoTheme.colorScheme
                val hasTheme = isInitializedCupertinoTheme()
                val content = CupertinoLiquidButtonDefaults.glassButtonColors().contentColor(true).value
                SideEffect {
                    isDark = scheme.isDark
                    background = scheme.systemBackground
                    initialized = hasTheme
                    buttonContent = content
                }
            }
        }

        runOnIdle {
            assertEquals(false, isDark)
            assertEquals(Color.White, background)
            assertEquals(Color.Black, buttonContent)
            assertFalse(initialized)
            systemTheme.value = SystemTheme.Dark
        }
        runOnIdle {
            assertEquals(true, isDark)
            assertEquals(Color.Black, background)
            assertEquals(Color.White, buttonContent)
            assertFalse(initialized)
            systemTheme.value = SystemTheme.Light
        }
        runOnIdle {
            assertEquals(false, isDark)
            assertEquals(Color.White, background)
            assertEquals(Color.Black, buttonContent)
            assertFalse(initialized)
        }
    }

    @Test
    fun providedColorsOverrideSystemAppearance() = runComposeUiTest {
        val systemTheme = mutableStateOf(SystemTheme.Dark)
        val light = lightColorScheme(accent = Color.Red)
        val dark = darkColorScheme(accent = Color.Green)
        val observed = mutableListOf<Pair<zone.ien.hig.theme.ColorScheme, Boolean>>()
        setContent {
            CompositionLocalProvider(LocalSystemTheme provides systemTheme.value) {
                CupertinoTheme(colorScheme = light) {
                    val scheme = CupertinoTheme.colorScheme
                    val initialized = isInitializedCupertinoTheme()
                    SideEffect { observed.add(scheme to initialized) }
                    CompositionLocalProvider(LocalColorScheme provides dark) {
                        val nestedScheme = CupertinoTheme.colorScheme
                        val nestedInitialized = isInitializedCupertinoTheme()
                        SideEffect { observed.add(nestedScheme to nestedInitialized) }
                    }
                }
            }
        }

        runOnIdle {
            assertSame(light, observed[0].first)
            assertSame(dark, observed[1].first)
            assertEquals(listOf(true, true), observed.map { it.second })
            observed.clear()
            systemTheme.value = SystemTheme.Light
        }
        runOnIdle {
            assertSame(light, observed[0].first)
            assertSame(dark, observed[1].first)
            assertEquals(listOf(true, true), observed.map { it.second })
        }
    }

    @Test
    fun unwrappedColorsFollowHighContrastChanges() = runComposeUiTest {
        var secondaryLabel = Color.Unspecified
        try {
            updateHighContrastState(false)
            setContent {
                CompositionLocalProvider(LocalSystemTheme provides SystemTheme.Light) {
                    val color = CupertinoTheme.colorScheme.secondaryLabel
                    SideEffect { secondaryLabel = color }
                }
            }
            runOnIdle {
                assertEquals(Color(0x993C3C43), secondaryLabel)
                updateHighContrastState(true)
            }
            runOnIdle {
                assertEquals(Color(0xCC3C3C43), secondaryLabel)
            }
        } finally {
            updateHighContrastState(false)
        }
    }

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
