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



package sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.layerBackdrop
import zone.ien.hig.CupertinoDatePickerState
import zone.ien.hig.CupertinoNavigateBackLiquidButton
import zone.ien.hig.CupertinoScaffold
import zone.ien.hig.CupertinoSegmentedControl
import zone.ien.hig.CupertinoSegmentedControlTab
import zone.ien.hig.CupertinoText
import zone.ien.hig.CupertinoTimePickerState
import zone.ien.hig.CupertinoTopAppBar
import zone.ien.hig.ExperimentalCupertinoApi
import zone.ien.hig.MenuDivider
import zone.ien.hig.MenuPickerAction
import zone.ien.hig.icons.CupertinoIcons
import zone.ien.hig.icons.outlined.Heart
import zone.ien.hig.rememberCupertinoDatePickerState
import zone.ien.hig.rememberCupertinoTimePickerState
import zone.ien.hig.section.CupertinoLinkIcon
import zone.ien.hig.section.CupertinoSection
import zone.ien.hig.section.LazySectionScope
import zone.ien.hig.section.SectionItem
import zone.ien.hig.section.SectionStyle
import zone.ien.hig.section.datePicker
import zone.ien.hig.section.dropdownMenu
import zone.ien.hig.section.link
import zone.ien.hig.section.rememberSectionState
import zone.ien.hig.section.section
import zone.ien.hig.section.sectionTitle
import zone.ien.hig.section.switch
import zone.ien.hig.section.textField
import zone.ien.hig.section.timePicker
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.utils.rememberDefaultBackdrop

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun SectionsScreen(
    navigateBack: () -> Unit
) {
    val backdrop = rememberDefaultBackdrop()
    var isLazy by remember { mutableStateOf(true) }
    val pagerState = rememberPagerState { 2 }
    val toggleState = remember { mutableStateOf(false) }

    val datePickerState = rememberCupertinoDatePickerState()
    val timePickerState = rememberCupertinoTimePickerState()

    var datePickerExpanded by remember { mutableStateOf<SectionStyle?>(null) }
    var timePickerExpanded by remember { mutableStateOf<SectionStyle?>(null) }
    var pickerExpanded by remember { mutableStateOf<SectionStyle?>(null) }

    val pickedIndex = remember { mutableStateOf(0) }
    val textFieldValue = remember { mutableStateOf("") }
    val lazyState = rememberLazyListState()
    val defaultState = rememberScrollState()

    val currentState = if (isLazy) lazyState else defaultState
    val sectionState = rememberSectionState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(currentState.isScrollInProgress) {
        if (currentState.isScrollInProgress) {
            focusManager.clearFocus()
        }
    }

    LaunchedEffect(isLazy) {
        pagerState.animateScrollToPage(if (isLazy) 0 else 1)
    }

    CupertinoScaffold(
        topBar = {
            CupertinoTopAppBar(
                navigationIcon = {
                    CupertinoNavigateBackLiquidButton(
                        onClick = navigateBack,
                        backdrop = backdrop,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                },
                title = {
                    CupertinoSegmentedControl(
                        selectedTabIndex = if (isLazy) 0 else 1,
                        modifier =
                            Modifier
                                .width(200.dp),
                    ) {
                        CupertinoSegmentedControlTab(
                            isSelected = isLazy,
                            onClick = {
                                isLazy = true
                            },
                        ) {
                            CupertinoText("Lazy")
                        }
                        CupertinoSegmentedControlTab(
                            isSelected = !isLazy,
                            onClick = {
                                isLazy = false
                            },
                        ) {
                            CupertinoText("Default")
                        }
                    }
                },
                backdrop = backdrop
            )
        },
    ) { pv ->
        if (isLazy) {
            LazyColumn(
                state = lazyState,
                contentPadding = pv,
                modifier =
                    Modifier
                        .layerBackdrop(backdrop)
                        .fillMaxSize()
                        .background(CupertinoTheme.colorScheme.systemGroupedBackground),
            ) {
                SectionStyle.entries.forEach { style ->
                    section(
                        state = sectionState,
                        style = style,
                        title = {
                            Title(style)
                        },
                        caption = {
                            Caption()
                        },
                    ) {
                        sectionContent(
                            toggle = toggleState,
                            datePickerState = datePickerState,
                            datePickerExpanded = datePickerExpanded == style,
                            onDatePickerExpanded = { datePickerExpanded = if (it) style else null },
                            timePickerState = timePickerState,
                            timePickerExpanded = timePickerExpanded == style,
                            onTimePickerExpanded = {
                                timePickerExpanded = if (it) style else null
                            },
                            pickedIndex = pickedIndex,
                            pickerExpanded = pickerExpanded == style,
                            onpickerExpanded = {
                                pickerExpanded = if (it) style else null
                            },
                            textFieldValue = textFieldValue,
                        )
                    }
                }
            }
        } else {
            Column(
                modifier =
                    Modifier
                        .layerBackdrop(backdrop)
                        .verticalScroll(defaultState)
                        .background(CupertinoTheme.colorScheme.systemGroupedBackground)
                        .fillMaxSize()
                        .padding(pv),
            ) {
                SectionStyle.entries.forEach { style ->
                    CupertinoSection(
                        state = sectionState,
                        style = style,
                        title = {
                            Title(style)
                        },
                        caption = {
                            Caption()
                        },
                    ) {
                        SectionItem(
                            leadingContent = {
                                CupertinoLinkIcon(imageVector = CupertinoIcons.Default.Heart)
                            },
                            trailingContent = {
                                Text("Trailing")
                            },
                        ) {
                            Text("Section item")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Title(style: SectionStyle) {
    CupertinoText(
        text = "${style.name} section".sectionTitle(),
    )
}

@Composable
private fun Caption() {
    CupertinoText(
        text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed luctus felis sed maximus accumsan.",
    )
}

@OptIn(ExperimentalCupertinoApi::class)
private fun LazySectionScope.sectionContent(
    toggle: MutableState<Boolean>,
    datePickerState: CupertinoDatePickerState,
    datePickerExpanded: Boolean,
    onDatePickerExpanded: (Boolean) -> Unit,
    timePickerState: CupertinoTimePickerState,
    timePickerExpanded: Boolean,
    onTimePickerExpanded: (Boolean) -> Unit,
    pickerExpanded: Boolean,
    pickedIndex: MutableState<Int>,
    onpickerExpanded: (Boolean) -> Unit,
    textFieldValue: MutableState<String>,
) {
    item {
        CupertinoText(
            text = "Simple text",
            modifier = Modifier.padding(it),
        )
    }

    textField(
        value = textFieldValue.value,
        singleLine = true,
        onValueChange = { textFieldValue.value = it },
        placeholder = {
            Text("Text field")
        },
    )

    link(
        onClick = {},
    ) {
        CupertinoText("Clickable label")
    }
    datePicker(
        state = datePickerState,
        expanded = datePickerExpanded,
        onExpandedChange = onDatePickerExpanded,
        title = {
            CupertinoText("Date Picker")
        },
    )
    timePicker(
        state = timePickerState,
        expanded = timePickerExpanded,
        onExpandedChange = onTimePickerExpanded,
        title = {
            CupertinoText("Time Picker")
        },
    )

    dropdownMenu(
        expanded = pickerExpanded,
        onDismissRequest = {
            onpickerExpanded(false)
        },
        onClick = {
            onpickerExpanded(true)
        },
        title = {
            CupertinoText("Popup picker")
        },
        selectedLabel = {
            CupertinoText(if (pickedIndex.value == 0) "None" else "Item ${pickedIndex.value}")
        },
    ) {
        MenuPickerAction(
            isSelected = pickedIndex.value == 0,
            onClick = {
                pickedIndex.value = 0
                onpickerExpanded(false)
            },
        ) {
            Text("None")
        }
        MenuDivider()

        repeat(7) {
            MenuPickerAction(
                isSelected = pickedIndex.value == it + 1,
                onClick = {
                    pickedIndex.value = it + 1
                    onpickerExpanded(false)
                },
            ) {
                Text("Item $it")
            }
        }
    }

    switch(
        checked = toggle.value,
        onCheckedChange = {
            toggle.value = it
        },
    ) {
        CupertinoText("Toggle")
    }
}
