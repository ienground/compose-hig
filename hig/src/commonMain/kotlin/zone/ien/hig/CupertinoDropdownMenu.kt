/*
 * Copyright (c) 2023-2024. Compose Cupertino project and open source contributors.
 * Copyright (c) 2025. Scott Lanoue.
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

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.kyant.backdrop.Backdrop
import zone.ien.hig.icons.CupertinoIcons
import zone.ien.hig.icons.outlined.Checkmark
import zone.ien.hig.section.CupertinoSectionDefaults
import zone.ien.hig.section.SectionStyle
import zone.ien.hig.theme.CupertinoColors
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.theme.systemGray5

/**
 * Cupertino elevated dropdown menu. Usually used for top bar actions.
 *
 * @see MenuSection
 * @see MenuTitle
 * @see MenuAction
 * @see MenuPickerAction
 * @see MenuDivider
 * */
@Composable
@ExperimentalCupertinoApi
expect fun CupertinoDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    paddingValues: PaddingValues = CupertinoDropdownMenuDefaults.PaddingValues,
    containerColor: Color = CupertinoDropdownMenuDefaults.ContainerColor,
    width: Dp = CupertinoDropdownMenuDefaults.DefaultWidth,
    scrollState: ScrollState = rememberScrollState(),
    properties: PopupProperties = PopupProperties(focusable = true, clippingEnabled = false),
    backdrop: Backdrop,
    content: @Composable CupertinoMenuScope.() -> Unit,
)

sealed interface CupertinoMenu {

    data class MenuItem(): CupertinoMenu
    data class MenuSection(): CupertinoMenu
    data class MenuTitle(): CupertinoMenu
    data class MenuAction(): CupertinoMenu
    data class MenuPickerAction(): CupertinoMenu
    data class MenuDevider(): CupertinoMenu
}

/**
 * Contains default values used for [hig.CupertinoDropdownMenu].
 */
@Immutable
object CupertinoDropdownMenuDefaults {
    val DefaultWidth = 260.dp
    val SmallWidth = 160.dp

    val Elevation = 16.dp

    val PaddingValues = PaddingValues(0.dp)

    val Shape: Shape
        @Composable
        @ReadOnlyComposable
        get() = CupertinoSectionDefaults.shape(SectionStyle.InsetGrouped)

    val ContainerColor: Color
        @Composable
        @ReadOnlyComposable
        get() = CupertinoTheme.colorScheme.tertiarySystemBackground

    val ContentColor: Color
        @Composable
        @ReadOnlyComposable
        get() = CupertinoTheme.colorScheme.label

    val DividerColor: Color
        @Composable
        @ReadOnlyComposable
        get() = CupertinoColors.systemGray5

    @Composable
    fun PickerLeadingIcon() {
        CupertinoIcon(
            imageVector = CupertinoIcons.Default.Checkmark,
            modifier = Modifier.size(CupertinoIconDefaults.SmallSize),
            contentDescription = null,
        )
    }
}