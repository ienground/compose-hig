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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.kyant.backdrop.Backdrop

/**
 * Data class representing a menu item in a dropdown menu.
 *
 * @param title the text title of the menu item
 * @param enabled whether the menu item is enabled
 * @param icon an optional icon painter for the menu item
 * @param isDestructive whether this is a destructive menu action
 * @param onClick callback when the menu item is clicked
 */
data class CupertinoMenuItemData(
    val title: String,
    val enabled: Boolean = true,
    val icon: Painter? = null,
    val isDestructive: Boolean = false,
    val onClick: () -> Unit,
)

/**
 * Data class representing a section in a dropdown menu.
 *
 * @param title the title of the section
 * @param icon an optional icon for the section
 * @param options display options for the section
 * @param items the list of menu items in this section
 */
data class CupertinoMenuSectionData(
    val title: String,
    val icon: Painter? = null,
    val options: HigMenuOptions = HigMenuOptions.DisplayInline,
    val items: List<CupertinoMenuItemData>,
)

/**
 * Enum representing display options for dropdown menu sections.
 */
enum class HigMenuOptions {
    DisplayInline, SingleSelection, DisplayAsPalette, Destructive
}

/**
 * A dropdown menu that is implemented with native platform widgets on iOS and Compose widgets on
 * non-iOS platforms.
 *
 * @param expanded whether the menu is currently expanded
 * @param onDismissRequest callback when the menu should be dismissed
 * @param modifier the [Modifier] to be applied to this menu
 * @param offset the offset for positioning the menu
 * @param paddingValues the padding values for the menu
 * @param containerColor the color of the menu container
 * @param width the width of the menu
 * @param scrollState the scroll state for the menu
 * @param properties popup properties for the menu
 * @param backdrop the backdrop for the menu
 * @param items list of menu items
 * @param sections list of menu sections
 */
@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalCupertinoApi
@Composable
expect fun CupertinoDropdownMenuNative(
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
    items: List<CupertinoMenuItemData> = listOf(),
    sections: List<CupertinoMenuSectionData> = listOf(),
)

data class CupertinoMenuSectionData(
    val title: String,
    val icon: Painter? = null,
    val options: HigMenuOptions = HigMenuOptions.DisplayInline,
    val items: List<CupertinoMenuItemData>,
)

enum class HigMenuOptions {
    DisplayInline, SingleSelection, DisplayAsPalette, Destructive
}

@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalCupertinoApi
@Composable
expect fun CupertinoDropdownMenuNative(
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
    items: List<CupertinoMenuItemData> = listOf(),
    sections: List<CupertinoMenuSectionData> = listOf(),
)