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


@file:OptIn(
    ExperimentalCupertinoApi::class
)
/*
 * Copyright (c) 2023 Compose Cupertino project and open source contributors.
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

package cupertino

import GeneratedAdaptiveTheme
import RootDetails
import RootRoute
import RootUiState
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import zone.ien.hig.utils.rememberDefaultBackdrop
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import zone.ien.hig.CupertinoActionSheet
import zone.ien.hig.CupertinoActionSheetNative
import zone.ien.hig.CupertinoActivityIndicator
import zone.ien.hig.CupertinoAlertDialogNative
import zone.ien.hig.CupertinoBorderedTextField
import zone.ien.hig.CupertinoBottomSheetContent
import zone.ien.hig.CupertinoBottomSheetScaffold
import zone.ien.hig.CupertinoBottomSheetScaffoldDefaults
import zone.ien.hig.CupertinoBottomSheetScaffoldState
import zone.ien.hig.CupertinoButton
import zone.ien.hig.CupertinoButtonDefaults
import zone.ien.hig.CupertinoButtonSize
import zone.ien.hig.CupertinoCheckBox
import zone.ien.hig.CupertinoDatePicker
import zone.ien.hig.CupertinoDatePickerNative
import zone.ien.hig.CupertinoDatePickerState
import zone.ien.hig.CupertinoDateTimePicker
import zone.ien.hig.CupertinoDateTimePickerNative
import zone.ien.hig.CupertinoDateTimePickerState
import zone.ien.hig.CupertinoDropdownMenu
import zone.ien.hig.CupertinoDropdownMenuNative
import zone.ien.hig.CupertinoIcon
import zone.ien.hig.CupertinoIconButton
import zone.ien.hig.CupertinoLiquidAlertDialog
import zone.ien.hig.CupertinoLiquidButton
import zone.ien.hig.CupertinoLiquidButtonDefaults
import zone.ien.hig.CupertinoLiquidIconButton
import zone.ien.hig.CupertinoMenuItemData
import zone.ien.hig.CupertinoMenuSectionData
import zone.ien.hig.CupertinoNavigationBarItemData
import zone.ien.hig.CupertinoNavigationTitle
import zone.ien.hig.CupertinoPickerState
import zone.ien.hig.CupertinoRangeSlider
import zone.ien.hig.CupertinoSearchTextField
import zone.ien.hig.CupertinoSearchTextFieldDefaults
import zone.ien.hig.CupertinoSegmentedControl
import zone.ien.hig.CupertinoSegmentedControlTab
import zone.ien.hig.CupertinoSlider
import zone.ien.hig.CupertinoSwipeBox
import zone.ien.hig.CupertinoSwitch
import zone.ien.hig.CupertinoText
import zone.ien.hig.CupertinoTextField
import zone.ien.hig.CupertinoTimePicker
import zone.ien.hig.CupertinoTimePickerNative
import zone.ien.hig.CupertinoTimePickerState
import zone.ien.hig.CupertinoTopAppBar
import zone.ien.hig.CupertinoTriStateCheckBox
import zone.ien.hig.CupertinoWheelPicker
import zone.ien.hig.ExperimentalCupertinoApi
import zone.ien.hig.MenuAction
import zone.ien.hig.MenuSection
import zone.ien.hig.PresentationStyle
import zone.ien.hig.adaptive.AdaptiveNavigationBar
import zone.ien.hig.adaptive.AdaptiveNavigationBarItem
import zone.ien.hig.adaptive.AdaptiveNavigationBarNative
import zone.ien.hig.adaptive.ExperimentalAdaptiveApi
import zone.ien.hig.adaptive.Theme
import zone.ien.hig.adaptive.icons.AdaptiveIcons
import zone.ien.hig.adaptive.icons.Add
import zone.ien.hig.adaptive.icons.Menu
import zone.ien.hig.adaptive.icons.Person
import zone.ien.hig.adaptive.icons.Settings
import zone.ien.hig.adaptive.icons.Share
import zone.ien.hig.cancel
import zone.ien.hig.default
import zone.ien.hig.destructive
import zone.ien.hig.icons.CupertinoIcons
import zone.ien.hig.icons.filled.Alarm
import zone.ien.hig.icons.filled.Archivebox
import zone.ien.hig.icons.filled.Banknote
import zone.ien.hig.icons.filled.Pin
import zone.ien.hig.icons.filled.Trash
import zone.ien.hig.icons.outlined.Bookmark
import zone.ien.hig.icons.outlined.FaceSmiling
import zone.ien.hig.icons.outlined.Heart
import zone.ien.hig.icons.outlined.Iphone
import zone.ien.hig.icons.outlined.MoonStars
import zone.ien.hig.icons.outlined.Paintpalette
import zone.ien.hig.icons.outlined.Paperclip
import zone.ien.hig.icons.outlined.RectangleStack
import zone.ien.hig.icons.outlined.SquareAndArrowUp
import zone.ien.hig.icons.outlined.SquareSplit1x2
import zone.ien.hig.icons.outlined.SunMax
import zone.ien.hig.icons.outlined.Trash
import zone.ien.hig.rememberCupertinoBottomSheetScaffoldState
import zone.ien.hig.rememberCupertinoDatePickerState
import zone.ien.hig.rememberCupertinoDateTimePickerState
import zone.ien.hig.rememberCupertinoPickerState
import zone.ien.hig.rememberCupertinoSearchTextFieldState
import zone.ien.hig.rememberCupertinoSheetState
import zone.ien.hig.rememberCupertinoTimePickerState
import zone.ien.hig.section.CupertinoLinkIcon
import zone.ien.hig.section.CupertinoSection
import zone.ien.hig.section.ProvideSectionStyle
import zone.ien.hig.section.SectionItem
import zone.ien.hig.section.SectionLink
import zone.ien.hig.section.SectionScope
import zone.ien.hig.section.SectionStyle
import zone.ien.hig.section.link
import zone.ien.hig.section.section
import zone.ien.hig.section.sectionContainerBackground
import zone.ien.hig.section.sectionTitle
import zone.ien.hig.swipebox.CupertinoSwipeBoxItem
import zone.ien.hig.swipebox.SwipeBoxStates
import zone.ien.hig.swipebox.rememberCupertinoSwipeBoxState
import zone.ien.hig.theme.CupertinoColors
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.theme.systemBlue
import zone.ien.hig.theme.systemCyan
import zone.ien.hig.theme.systemGray
import zone.ien.hig.theme.systemGreen
import zone.ien.hig.theme.systemIndigo
import zone.ien.hig.theme.systemOrange
import zone.ien.hig.theme.systemPurple
import zone.ien.hig.theme.systemRed
import zone.ien.hig.theme.systemYellow
import kotlin.time.Instant

private enum class PickerTab {
    Picker, Time, Date, DateTime
}

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun CupertinoWidgetsScreen(
    uiState: RootUiState,
    onItemValueChanged: (RootDetails) -> Unit,
    onNavigate: (NavKey) -> Unit,
) {

    val scrollState = rememberScrollState()
    val sheetListState = rememberLazyListState()

    val backdrop = rememberDefaultBackdrop()
    val globalBackdrop = rememberDefaultBackdrop()

    val scaffoldState = rememberCupertinoBottomSheetScaffoldState(
        rememberCupertinoSheetState(
            presentationStyle = PresentationStyle.Modal()
        )
    )

    val sheetSectionColor = CupertinoTheme.colorScheme.tertiarySystemBackground

    val focusManager = LocalFocusManager.current

    val nativePickers = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(scrollState.isScrollInProgress) {
        if (scrollState.isScrollInProgress) {
            focusManager.clearFocus(force = true)
        }
    }

    CupertinoBottomSheetScaffold(
        hasNavigationTitle = true,
        colors = CupertinoBottomSheetScaffoldDefaults.colors(
            sheetContainerColor = CupertinoTheme.colorScheme
                .secondarySystemBackground,
        ),
        sheetContent = {
            SheetSample(
                scaffoldState = scaffoldState,
                sheetListState = sheetListState,
                sheetSectionColor = sheetSectionColor
            )
        },
        scaffoldState = scaffoldState,
        topBar = {
            TopBarSample(
                uiState = uiState,
                onItemValueChanged = onItemValueChanged,
                scrollState = scrollState,
                backdrop = backdrop
            )
        },
        bottomBar = {
            BottomBarSample(
                backdrop = backdrop,
                isNative = nativePickers.value
            )
        },
    ) { pv ->
        Body(
            uiState = uiState,
            onItemValueChanged = onItemValueChanged,
            paddingValues = pv,
            scrollState = scrollState,
            scaffoldState = scaffoldState,
            nativePickers = nativePickers,
            backdrop = backdrop,
            onNavigate = onNavigate,
            modifier = Modifier.layerBackdrop(backdrop)
        )
    }
}

@Composable
private fun Body(
    modifier: Modifier = Modifier,
    uiState: RootUiState,
    onItemValueChanged: (RootDetails) -> Unit,
    paddingValues: PaddingValues,
    scrollState: ScrollState,
    scaffoldState: CupertinoBottomSheetScaffoldState,
    nativePickers: MutableState<Boolean>,
    backdrop: Backdrop,
    onNavigate: (NavKey) -> Unit
) {

    val coroutineScope = rememberCoroutineScope()

    val searchState = rememberCupertinoSearchTextFieldState(
        scrollableState = scrollState,
        blockScrollWhenFocusedAndEmpty = true
    )

    ProvideSectionStyle(
        SectionStyle.Sidebar
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .sectionContainerBackground()
                .nestedScroll(searchState.nestedScrollConnection)
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(top = 10.dp)
        ) {

            CupertinoNavigationTitle {
                Text("Cupertino")
            }
            var searchValue by remember {
                mutableStateOf("")
            }
            CupertinoSearchTextField(
                value = searchValue,
                onValueChange = {
                    searchValue = it
                },
                state = searchState,
                paddingValues = CupertinoSearchTextFieldDefaults.PaddingValues +
                        PaddingValues(bottom = 12.dp)
            )
//            /*

            CupertinoSection {
                SectionItem(
                    trailingContent = {
                        CupertinoSwitch(
                            checked = uiState.item.invertLayoutDirection,
                            onCheckedChange = { onItemValueChanged(uiState.item.copy(invertLayoutDirection = it)) },
                        )
                    }
                ) {
                    Text("Toggle layout direction")
                }

                SectionItem {
                    ColorButtons(
                        onColorsChanged = { light, dark ->
                            onItemValueChanged(uiState.item.copy(accentColors = Pair(light, dark)))
                        }
                    )
                }
            }

            LinksWithIcons(
                onSheetClicked = {
                    coroutineScope.launch {
                        scaffoldState.bottomSheetState.show()
                    }
                },
                onNavigate = onNavigate,
            )

            CupertinoSection {
                SwipeBoxExample(scrollState)
            }


            CupertinoSection(
                title = {
                    CupertinoText(
                        text = "Controls".sectionTitle(),
                    )
                }
            ) {
                ButtonsExample()
                SwitchAndProgressBar()
            }

//             */

            CupertinoSection(
                title = {
                    CupertinoText(
                        text = "Popups".sectionTitle(),
                    )
                },
                caption = {
                    CupertinoText(
                        text = "Native dialogs will use UIAlertController on iOS and Compose Cupertino analogs on other platforms",
                    )
                }
            ) {
                SectionItem {
                    DialogsExample(
                        backdrop = backdrop
                    )
                }
                SectionItem {
                    SheetsExamples()
                }
                SectionItem {
                    DropdownExample(
                        backdrop = backdrop
                    )
                }
                SectionItem {
                    DropdownExample2(
                        backdrop = backdrop,
                        isNative = nativePickers.value
                    )
                }
            }

            // TODO broken on web and desktop
            PickersSection(nativePickers)

            Spacer(Modifier.imePadding())
        }
    }
}

@OptIn(ExperimentalAdaptiveApi::class)
@Preview(showBackground = true)
@Composable
private fun ScreenPreview() {
    GeneratedAdaptiveTheme(
        target = Theme.Cupertino,
        primaryColor = CupertinoColors.systemBlue
    ) {

        Body(
            uiState = RootUiState(),
            onItemValueChanged = {},
            paddingValues = PaddingValues.Zero,
            scrollState = rememberScrollState(),
            scaffoldState = rememberCupertinoBottomSheetScaffoldState(),
            nativePickers = remember { mutableStateOf(false) },
            onNavigate = {},
            backdrop = rememberDefaultBackdrop()
        )
    }
}

@Composable
private fun PickersSection(
    nativePickers: MutableState<Boolean>
) {

    var selectedPickerTab by remember {
        mutableStateOf(PickerTab.Picker)
    }

    val pickerState = rememberCupertinoPickerState()

    val timePickerState = rememberCupertinoTimePickerState()
    val datePickerState = rememberCupertinoDatePickerState()
    val dateTimePickerState = rememberCupertinoDateTimePickerState()

    val pickerValues = remember {
        listOf(
            "January", "February",
            "March", "April",
            "May", "June", "July", "August", "September",
            "October", "November", "December"
        )
    }

    CupertinoSection(
        title = {
            CupertinoText(
                text = "Wheel Pickers".sectionTitle()
            )
        },
        caption = {
            CupertinoText(
                text = when (selectedPickerTab) {
                    PickerTab.Picker ->
                        "Selected: ${
                            pickerValues[pickerState.selectedItemIndex(
                                pickerValues.size
                            )]
                        }"

                    PickerTab.Time -> "${timePickerState.hour}: ${timePickerState.minute}"
                    PickerTab.Date -> remember {
                        derivedStateOf {
                            Instant
                                .fromEpochMilliseconds(datePickerState.selectedDateMillis)
                                .toLocalDateTime(TimeZone.UTC)
                                .toString()
                        }
                    }.value

                    PickerTab.DateTime -> remember {
                        derivedStateOf {
                            Instant
                                .fromEpochMilliseconds(dateTimePickerState.selectedDateTimeMillis)
                                .toLocalDateTime(TimeZone.UTC)
                                .toString()
                        }
                    }.value
                }
            )
        }
    ) {
        SectionItem {
            CupertinoSegmentedControl(
                paddingValues = PaddingValues(0.dp),
                selectedTabIndex = PickerTab.entries.indexOf(selectedPickerTab),
            ) {
                val tabs = PickerTab.entries

                tabs.forEach { s ->
                    CupertinoSegmentedControlTab(
                        isSelected = s == selectedPickerTab,
                        onClick = {
                            selectedPickerTab = s
                        }
                    ) {
                        CupertinoText(s.name)
                    }
                }
            }
        }

        SectionItem(
            trailingContent = {
                CupertinoSwitch(
                    checked = nativePickers.value,
                    onCheckedChange = {
                        nativePickers.value = it
                    }
                )
            }
        ) {
            Text("Native")

        }

        SectionItem {
            when (selectedPickerTab) {
                PickerTab.Picker -> PickerExample(pickerValues, pickerState)
                PickerTab.Time -> TimePickerExample(timePickerState, nativePickers.value)
                PickerTab.Date -> DatePickerExample(datePickerState, nativePickers.value)
                PickerTab.DateTime -> DateTimePicker(dateTimePickerState, nativePickers.value)
            }
        }

    }
}

@OptIn(ExperimentalCupertinoApi::class, ExperimentalFoundationApi::class)
@Composable
private fun SwipeBoxExample(scrollableState: ScrollableState) {

    val scope = rememberCoroutineScope()

    val exampleSwipeBoxOnClick: (String) -> Unit = { message ->
        println("Action triggered with message: $message")
    }

    val openSwipeBoxState =
        remember { mutableStateOf<AnchoredDraggableState<SwipeBoxStates>?>(null) }

    val state0 = rememberCupertinoSwipeBoxState(
        key = "swipeBox0",
        scrollableState = scrollableState,
        openSwipeBoxState = openSwipeBoxState,
        coroutineScope = scope
    )
    CupertinoSwipeBox(
        state = state0,
        actionItemBuilder = {
            start(onClick = { exampleSwipeBoxOnClick("Full Swipe on Trash") }) {
                CupertinoSwipeBoxItem(
                    color = CupertinoColors.systemRed,
                    onClick = { exampleSwipeBoxOnClick("Trash") },
                    label = "Trash",
                )
            }
            end(onClick = { exampleSwipeBoxOnClick("Full Swipe on Archivebox") }) {
                CupertinoSwipeBoxItem(
                    color = CupertinoColors.systemBlue,
                    onClick = { exampleSwipeBoxOnClick("Archivebox") },
                    icon = CupertinoIcons.Filled.Archivebox,
                )
            }
        }
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterStart),
            text = "Swipe Me"
        )
    }

    val state1 = rememberCupertinoSwipeBoxState(
        key = "swipeBox1",
        scrollableState = scrollableState,
        openSwipeBoxState = openSwipeBoxState,
        coroutineScope = scope
    )
    CupertinoSwipeBox(
        startToEndFullSwipeEnabled = false,
        endToStartFullSwipeEnabled = false,
        state = state1,
        actionItemBuilder = {
            start {
                CupertinoSwipeBoxItem(
                    color = CupertinoColors.systemRed,
                    onClick = { exampleSwipeBoxOnClick("Trash") },
                    label = "Trash",
                )
            }
            end {
                CupertinoSwipeBoxItem(
                    color = CupertinoColors.systemBlue,
                    onClick = { exampleSwipeBoxOnClick("Archivebox") },
                    icon = CupertinoIcons.Filled.Archivebox,
                )
            }
        }
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterStart),
            text = "Swipe Me (no full swipe)"
        )
    }

    val state2 = rememberCupertinoSwipeBoxState(
        key = "swipeBox2",
        scrollableState = scrollableState,
        openSwipeBoxState = openSwipeBoxState,
        coroutineScope = scope
    )
    CupertinoSwipeBox(
        state = state2,
        actionItemBuilder = {
            start(onClick = { exampleSwipeBoxOnClick("Full Swipe on Trash") }) {
                CupertinoSwipeBoxItem(
                    color = CupertinoColors.systemRed,
                    onClick = { exampleSwipeBoxOnClick("Trash") },
                    label = "Trash",
                )
            }
            start {
                CupertinoSwipeBoxItem(
                    color = CupertinoColors.systemYellow,
                    onClick = { exampleSwipeBoxOnClick("Alarm") },
                    icon = CupertinoIcons.Filled.Alarm,
                )
            }
        },
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterStart),
            text = "One way Swipe Me"
        )
    }

    val state3 = rememberCupertinoSwipeBoxState(
        key = "swipeBox3",
        scrollableState = scrollableState,
        openSwipeBoxState = openSwipeBoxState,
        coroutineScope = scope
    )
    CupertinoSwipeBox(
        state = state3,
        actionItemBuilder = {
            start(onClick = { exampleSwipeBoxOnClick("Full swipe on Clock") }) {
                CupertinoSwipeBoxItem(
                    color = CupertinoColors.systemGreen,
                    onClick = { exampleSwipeBoxOnClick("Clock") },
                    label = "Clock",
                )
            }
            start {
                CupertinoSwipeBoxItem(
                    color = CupertinoColors.systemYellow,
                    onClick = { exampleSwipeBoxOnClick("BankNote") },
                    icon = CupertinoIcons.Filled.Banknote,
                )
            }
            end {
                CupertinoSwipeBoxItem(
                    color = CupertinoColors.systemGray,
                    onClick = { exampleSwipeBoxOnClick("Trash") },
                    label = "Trash",
                )
            }
            end(onClick = { exampleSwipeBoxOnClick("Full swipe on Alarm") }) {
                CupertinoSwipeBoxItem(
                    color = CupertinoColors.systemRed,
                    onClick = { exampleSwipeBoxOnClick("Alarm") },
                    icon = CupertinoIcons.Filled.Alarm,
                )
            }
        },
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterStart),
            text = "Two way 2 item Swipe Me"
        )
    }

    val state4 = rememberCupertinoSwipeBoxState(
        key = "swipeBox4",
        scrollableState = scrollableState,
        openSwipeBoxState = openSwipeBoxState,
        coroutineScope = scope
    )
    CupertinoSwipeBox(
        state = state4,
        actionItemBuilder = {
            end(onClick = { exampleSwipeBoxOnClick("Archivebox") }) {
                CupertinoSwipeBoxItem(
                    color = CupertinoColors.systemBlue,
                    onClick = { exampleSwipeBoxOnClick("Archivebox") },
                    icon = CupertinoIcons.Filled.Archivebox,
                )
            }
        },
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterEnd),
            text = "One way Swipe Me"
        )
    }

    val state5 = rememberCupertinoSwipeBoxState(
        key = "swipeBox5",
        scrollableState = scrollableState,
        openSwipeBoxState = openSwipeBoxState,
        coroutineScope = scope
    )
    CupertinoSwipeBox(
        state = state5,
        endToStartFullSwipeEnabled = false,
        actionItemBuilder = {
            start(onClick = { exampleSwipeBoxOnClick("Full swipe on Clock") }) {
                CupertinoSwipeBoxItem(
                    color = CupertinoColors.systemGreen,
                    onClick = { exampleSwipeBoxOnClick("Clock") },
                    label = "Clock",
                )
            }
            end {
                CupertinoSwipeBoxItem(
                    color = CupertinoColors.systemRed,
                    onClick = { exampleSwipeBoxOnClick("Trash") },
                    icon = CupertinoIcons.Filled.Trash,
                )
            }
            end {
                CupertinoSwipeBoxItem(
                    color = CupertinoColors.systemGray,
                    onClick = { exampleSwipeBoxOnClick("Pin") },
                    icon = CupertinoIcons.Filled.Pin,
                )
            }
            end {
                CupertinoSwipeBoxItem(
                    color = CupertinoColors.systemBlue,
                    onClick = { exampleSwipeBoxOnClick("Archivebox") },
                    icon = CupertinoIcons.Filled.Archivebox,
                )
            }
        },
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterStart),
            text = "3 item swipe box"
        )
    }

}

@Composable
private fun TopBarSample(
    uiState: RootUiState,
    onItemValueChanged: (RootDetails) -> Unit,
    scrollState: ScrollState,
    backdrop: LayerBackdrop
) {
    CupertinoTopAppBar(
        backdrop = backdrop,
        actions = {
            CupertinoLiquidButton(
                onClick = {},
                backdrop = backdrop,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(2) {
                        Box(
                            modifier = Modifier.clickable(
                                interactionSource = null,
                                indication = null,
                                onClick = { onItemValueChanged(uiState.item.copy(isDark = !uiState.item.isDark)) }
                            )
                        ) {
                            AnimatedContent(uiState.item.isDark) {
                                if (it) {
                                    CupertinoIcon(
                                        imageVector = CupertinoIcons.Default.SunMax,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    CupertinoIcon(
                                        imageVector = CupertinoIcons.Default.MoonStars,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        title = {
            CupertinoText("Cupertino")
        }
    )
}

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
private fun BottomBarSample(
    backdrop: LayerBackdrop,
    isNative: Boolean
) {
    var tab by remember { mutableStateOf(0) }
    val content = listOf(
        "Profile" to AdaptiveIcons.Outlined.Person,
        "Menu" to AdaptiveIcons.Outlined.Menu,
        "Settings" to AdaptiveIcons.Outlined.Settings,
    )

    if (isNative) {
        AdaptiveNavigationBarNative(
            selectedTabIndex = { tab },
            onTabSelected = { tab = it },
            adaptation = {
                cupertino { this.backdrop = backdrop }
            },
            items = content.mapIndexed { index, item ->
                CupertinoNavigationBarItemData(
                    onClick = { tab = index },
                    icon = rememberVectorPainter(item.second),
                    label = item.first
                )
            }
        )
    } else {
        AdaptiveNavigationBar(
            selectedTabIndex = { tab },
            onTabSelected = { tab = it },
            tabsCount = 3,
            adaptation = {
                cupertino { this.backdrop = backdrop }
            },
        ) {
            content.forEachIndexed { index, pair ->
                AdaptiveNavigationBarItem(
                    index = index,
                    onClick = { tab = index },
                    icon = {
                        CupertinoIcon(
                            imageVector = pair.second,
                            contentDescription = pair.first,
                        )
                    },
                    label = {
                        Text(pair.first)
                    },
                )
            }
        }
    }
}

@Composable
private fun SheetSample(
    scaffoldState: CupertinoBottomSheetScaffoldState,
    sheetListState: LazyListState,
    sheetSectionColor: Color
) {

    val coroutineScope = rememberCoroutineScope()

    CupertinoBottomSheetContent(
        topBar = {
            CupertinoTopAppBar(
                title = {
                    CupertinoText("Bottom Sheet")
                },
                actions = {
                    CupertinoButton(
                        colors = CupertinoButtonDefaults.plainButtonColors(),
                        onClick = {
                            coroutineScope.launch {
                                scaffoldState.bottomSheetState.hide()
                            }
                        }
                    ) {
                        CupertinoText("Done")
                    }
                },
//                isTransparent = sheetListState.isTopBarTransparent
            )
        }
    ) { pv ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = sheetListState,
            contentPadding = pv,
        ) {

            section(
                color = sheetSectionColor
            ) {
                repeat(100) {
                    link(onClick = {}) {
                        CupertinoText("Item $it")
                    }
                }
            }
            item {
                Spacer(Modifier.imePadding())
            }
        }
    }
}

@Composable
private operator fun PaddingValues.plus(other: PaddingValues): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current

    return PaddingValues(
        top = calculateTopPadding() + other.calculateTopPadding(),
        bottom = calculateBottomPadding() + other.calculateBottomPadding(),
        start = calculateStartPadding(layoutDirection) + other.calculateStartPadding(layoutDirection),
        end = calculateEndPadding(layoutDirection) + other.calculateEndPadding(layoutDirection)
    )
}

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun PickerExample(
    pickerValues: List<String>,
    pickerState: CupertinoPickerState
) {
    CupertinoWheelPicker(
        state = pickerState,
        items = pickerValues,
        modifier = Modifier.fillMaxWidth(),
        containerColor = CupertinoTheme.colorScheme.secondarySystemGroupedBackground
    ) {
        CupertinoText(it)
    }
}

@Composable
fun TimePickerExample(
    state: CupertinoTimePickerState, native: Boolean
) {
    if (native) {
        CupertinoTimePickerNative(
            modifier = Modifier.fillMaxWidth(),
            state = state
        )
    } else {
        CupertinoTimePicker(
            modifier = Modifier.fillMaxWidth(),
            state = state
        )
    }
}

@Composable
fun DatePickerExample(
    state: CupertinoDatePickerState, native: Boolean
) {
    if (native) {
        CupertinoDatePickerNative(
            state = state,
            modifier = Modifier.fillMaxWidth(),
        )
    } else {
        CupertinoDatePicker(
            modifier = Modifier.fillMaxWidth(),
            state = state,
        )
    }
}


@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun DateTimePicker(
    state: CupertinoDateTimePickerState, native: Boolean
) {

    if (native) {
        CupertinoDateTimePickerNative(
            state = state,
            modifier = Modifier.fillMaxWidth()
        )
    } else {
        CupertinoDateTimePicker(
            modifier = Modifier.fillMaxWidth(),
            state = state
        )
    }
}


@Composable
private fun SectionScope.SwitchAndProgressBar() {
    val backdrop = rememberDefaultBackdrop()

    SectionItem {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            var active1 by remember {
                mutableStateOf(true)
            }

            var active2 by remember {
                mutableStateOf(false)
            }
            CupertinoSwitch(
                checked = active1,
                onCheckedChange = {
                    active1 = it
                }
            )
            CupertinoSwitch(
                checked = active2,
                onCheckedChange = {
                    active2 = it
                }
            )
            CupertinoSwitch(
                checked = active1,
                enabled = false,
                onCheckedChange = {}
            )
            CupertinoSwitch(
                checked = active2,
                enabled = false,
                onCheckedChange = {}
            )

            CupertinoActivityIndicator()
        }
    }

    SectionItem {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            var b by remember {
                mutableStateOf(.5f)
            }
            var enabled by remember { mutableStateOf(true) }
            CupertinoSlider(
                modifier = Modifier.weight(1f),
                value = b,
                onValueChange = {
                    b = it
                },
                enabled = enabled,
                backdrop = backdrop
            )

            CupertinoActivityIndicator(
                progress = b
            )
//            Text(
//                text = b.toString().take(4),
//                modifier = Modifier.width(40.dp),
//                maxLines = 1
//            )
        }
    }

    SectionItem {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            var b by remember {
                mutableStateOf(.5f)
            }
            CupertinoSlider(
                modifier = Modifier.weight(1f),
                enabled = false,
                value = b,
                onValueChange = {
                    b = it
                },
                backdrop = backdrop
            )

            CupertinoActivityIndicator(
                progress = b
            )
//            Text(
//                text = b.toString().take(4),
//                modifier = Modifier.width(40.dp),
//                maxLines = 1
//            )
        }
    }

    SectionItem {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            var b by remember {
                mutableStateOf(.5f)
            }
            CupertinoSlider(
                modifier = Modifier.weight(1f),
                value = b,
                steps = 2,
                onValueChange = {
                    b = it
                },
                backdrop = backdrop
            )

            Text(
                text = b.toString().take(4),
                modifier = Modifier.width(40.dp),
                maxLines = 1
            )
        }
    }

    SectionItem {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            var b by remember { mutableStateOf(0.25f..0.75f) }

            CupertinoRangeSlider(
                modifier = Modifier.weight(1f),
                value = b,
                steps = 4,
                onValueChange = {
                    b = it
                },
                backdrop = backdrop
            )

//            Text(
//                text = b.toString().take(4),
//                modifier = Modifier.width(40.dp),
//                maxLines = 1
//            )
        }
    }

    SectionItem {
        var v by remember {
            mutableStateOf("")
        }

        CupertinoTextField(
            value = v,
            onValueChange = {
                v = it
            },
            placeholder = {
                CupertinoText("Text field...")
            },
        )
    }


    SectionItem {
        var v by remember {
            mutableStateOf("")
        }

        CupertinoBorderedTextField(
            value = v,
            onValueChange = {
                v = it
            },
            placeholder = {
                CupertinoText("Text field...")
            },
            contentAlignment = Alignment.Bottom,
            colors = zone.ien.hig.CupertinoBorderedTextFieldDefaults.colors(
                focusedContainerColor = CupertinoTheme.colorScheme.systemBackground
            ),
            shape = CupertinoTheme.shapes.large,
            leadingIcon = {
                CupertinoIcon(
                    modifier = Modifier.height(zone.ien.hig.CupertinoIconDefaults.MediumSize),
                    imageVector = zone.ien.hig.icons.CupertinoIcons.Outlined.FaceSmiling,
                    contentDescription = null
                )
            },
            trailingIcon = {
                CupertinoIcon(
                    modifier = Modifier.height(zone.ien.hig.CupertinoIconDefaults.MediumSize),
                    imageVector = zone.ien.hig.icons.CupertinoIcons.Outlined.Paperclip,
                    contentDescription = null
                )
            },
        )
    }
}

@Composable
private fun ColorButtons(
    onColorsChanged: (light: Color, dark: Color) -> Unit
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CupertinoIconButton(
            onClick = {
                onColorsChanged(
                    CupertinoColors.systemBlue(false),
                    CupertinoColors.systemBlue(true)
                )
            },
            colors = CupertinoButtonDefaults.tintedButtonColors(
                contentColor = CupertinoColors.systemBlue
            )
        ) {
            CupertinoIcon(
                imageVector = CupertinoIcons.Default.Paintpalette,
                contentDescription = null
            )
        }
        CupertinoIconButton(
            onClick = {
                onColorsChanged(
                    CupertinoColors.systemGreen(false),
                    CupertinoColors.systemGreen(true)
                )
            },
            colors = CupertinoButtonDefaults.tintedButtonColors(
                contentColor = CupertinoColors.systemGreen
            )
        ) {
            CupertinoIcon(
                imageVector = CupertinoIcons.Default.Paintpalette,
                contentDescription = null
            )
        }
        CupertinoIconButton(
            onClick = {
                onColorsChanged(
                    CupertinoColors.systemPurple(false),
                    CupertinoColors.systemPurple(true)
                )
            },
            colors = CupertinoButtonDefaults.tintedButtonColors(
                contentColor = CupertinoColors.systemPurple
            )
        ) {
            CupertinoIcon(
                imageVector = CupertinoIcons.Default.Paintpalette,
                contentDescription = null
            )
        }

        CupertinoIconButton(
            onClick = {
                onColorsChanged(
                    CupertinoColors.systemOrange(false),
                    CupertinoColors.systemOrange(true)
                )
            },
            colors = CupertinoButtonDefaults.tintedButtonColors(
                contentColor = CupertinoColors.systemOrange
            )
        ) {
            CupertinoIcon(
                imageVector = CupertinoIcons.Default.Paintpalette,
                contentDescription = null
            )
        }
        CupertinoIconButton(
            onClick = {
                onColorsChanged(
                    CupertinoColors.systemRed(false),
                    CupertinoColors.systemRed(true)
                )
            },
            colors = CupertinoButtonDefaults.tintedButtonColors(
                contentColor = CupertinoColors.systemRed
            )
        ) {
            CupertinoIcon(
                imageVector = CupertinoIcons.Default.Paintpalette,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun SectionScope.ButtonsExample() {
    val backdrop = rememberDefaultBackdrop()
    SectionItem {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var a by remember { mutableStateOf(true) }
            var b by remember { mutableStateOf(false) }
            var c by remember { mutableStateOf(ToggleableState.Indeterminate) }

            CupertinoCheckBox(checked = a, onCheckedChange = { a = it })
            CupertinoCheckBox(checked = b, onCheckedChange = { b = it })
            CupertinoTriStateCheckBox(state = c, onClick = {
                c = when (c) {
                    ToggleableState.On -> ToggleableState.Off
                    ToggleableState.Off -> ToggleableState.Indeterminate
                    ToggleableState.Indeterminate -> ToggleableState.On
                }
            })
            CupertinoIconButton(
                onClick = {},
            ) {
                CupertinoIcon(
                    imageVector = AdaptiveIcons.Outlined.Share,
                    contentDescription = null
                )
            }
            CupertinoIconButton(
                onClick = {},
                colors = CupertinoButtonDefaults.tintedButtonColors()
            ) {
                CupertinoIcon(
                    imageVector = AdaptiveIcons.Outlined.Add,
                    contentDescription = null
                )
            }
            CupertinoIconButton(
                onClick = {},
                colors = CupertinoButtonDefaults.grayButtonColors()
            ) {
                CupertinoIcon(
                    imageVector = AdaptiveIcons.Outlined.Settings,
                    contentDescription = null
                )
            }
            CupertinoIconButton(
                onClick = {},
                enabled = false,
            ) {
                CupertinoIcon(
                    imageVector = AdaptiveIcons.Outlined.Add,
                    contentDescription = null
                )
            }
        }
    }
    SectionItem {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var a by remember { mutableStateOf(true) }
            var b by remember { mutableStateOf(false) }
            var c by remember { mutableStateOf(ToggleableState.Indeterminate) }

            CupertinoCheckBox(checked = a, onCheckedChange = { a = it })
            CupertinoCheckBox(checked = b, onCheckedChange = { b = it })
            CupertinoTriStateCheckBox(state = c, onClick = {
                c = when (c) {
                    ToggleableState.On -> ToggleableState.Off
                    ToggleableState.Off -> ToggleableState.Indeterminate
                    ToggleableState.Indeterminate -> ToggleableState.On
                }
            })
            CupertinoLiquidIconButton(
                backdrop = backdrop,
                onClick = {},
                isBackgroundAdaptive = false,
            ) {
                CupertinoIcon(
                    imageVector = AdaptiveIcons.Outlined.Share,
                    contentDescription = null
                )
            }
            CupertinoLiquidIconButton(
                backdrop = backdrop,
                onClick = {},
                isBackgroundAdaptive = false,
                colors = CupertinoLiquidButtonDefaults.glassProminentButtonColors()
            ) {
                CupertinoIcon(
                    imageVector = AdaptiveIcons.Outlined.Settings,
                    contentDescription = null
                )
            }
            CupertinoLiquidIconButton(
                backdrop = backdrop,
                onClick = {},
                isBackgroundAdaptive = false,
                enabled = false,
            ) {
                CupertinoIcon(
                    imageVector = AdaptiveIcons.Outlined.Add,
                    contentDescription = null
                )
            }
        }
    }

    SectionItem {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            CupertinoButton(
                colors = CupertinoButtonDefaults.grayButtonColors(),
                onClick = {},
                size = CupertinoButtonSize.Small
            ) {
                CupertinoText("Gray S")
            }

            CupertinoButton(
                colors = CupertinoButtonDefaults.tintedButtonColors(),
                onClick = {},
                size = CupertinoButtonSize.Regular
            ) {
                CupertinoText("Tinted M")
            }

            CupertinoButton(
                colors = CupertinoButtonDefaults.filledButtonColors(
                ),
                onClick = {},
                size = CupertinoButtonSize.Large
            ) {
                CupertinoText("Filled L")
            }

        }
    }

    SectionItem {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CupertinoButton(
                colors = CupertinoButtonDefaults.plainButtonColors(),
                onClick = {}
            ) {
                CupertinoText("Plain")
            }
            CupertinoButton(
                colors = CupertinoButtonDefaults.plainButtonColors(),
                onClick = {},
                enabled = false
            ) {
                CupertinoText("Disabled")
            }

            CupertinoButton(
                colors = CupertinoButtonDefaults.filledButtonColors(),
                onClick = {},
                enabled = false
            ) {
                CupertinoText("Disabled")
            }
        }
    }

    SectionItem {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CupertinoLiquidButton(
                colors = CupertinoLiquidButtonDefaults.glassProminentButtonColors(),
                onClick = {},
                enabled = true,
                isBackgroundAdaptive = false,
                backdrop = backdrop
            ) {
                CupertinoText("Filled")
            }
            CupertinoLiquidButton(
                colors = CupertinoLiquidButtonDefaults.glassProminentButtonColors(),
                onClick = {},
                enabled = false,
                isBackgroundAdaptive = false,
                backdrop = backdrop
            ) {
                CupertinoText("Disabled")
            }
        }
    }

    SectionItem {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CupertinoLiquidButton(
                colors = CupertinoLiquidButtonDefaults.glassButtonColors(),
                onClick = {},
                enabled = true,
                isBackgroundAdaptive = false,
                backdrop = backdrop
            ) {
                CupertinoText("Glass")
            }
            CupertinoLiquidButton(
                colors = CupertinoLiquidButtonDefaults.glassButtonColors(),
                onClick = {},
                enabled = false,
                isBackgroundAdaptive = false,
                backdrop = backdrop
            ) {
                CupertinoText("Disabled")
            }
        }
    }
}

@OptIn(ExperimentalCupertinoApi::class)
@Composable
private fun DialogsExample(
    backdrop: Backdrop
) {

    var alertVisible by remember { mutableStateOf(false) }
    var nativeAlertVisible by remember { mutableStateOf(false) }

    if (alertVisible) {
        CupertinoLiquidAlertDialog(
            onDismissRequest = {
                alertVisible = false
            },
            title = {
                CupertinoText("Alert Dialog")
            },
            message = {
                CupertinoText("Alert dialog message")
            },
            backdrop = backdrop
        ) {
            destructive(
                onClick = {
                    alertVisible = false
                }
            ) {
                CupertinoText("Cancel")
            }
            default(
                onClick = {
                    alertVisible = false
                }
            ) {
                CupertinoText("OK")
            }
        }
    }
    if (nativeAlertVisible) {
        CupertinoAlertDialogNative(
            onDismissRequest = {
                nativeAlertVisible = false
            },
            title = "Alert Dialog",
            message = "Alert dialog message"
        ) {
            destructive(
                onClick = {
                    nativeAlertVisible = false
                },
                title = "Cancel"
            )
            default(
                onClick = {
                    nativeAlertVisible = false
                },
                title = "OK"
            )
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CupertinoButton(
            colors = CupertinoButtonDefaults.tintedButtonColors(),
            onClick = {
                alertVisible = true
            }
        ) {
            CupertinoText("Alert")
        }
        CupertinoButton(
            colors = CupertinoButtonDefaults.tintedButtonColors(),
            onClick = {
                nativeAlertVisible = true
            }
        ) {
            CupertinoText("Native")
        }
    }
}

@Composable
private fun SheetsExamples() {

    var sheetVisible by remember {
        mutableStateOf(false)
    }
    var nativeSheetVisible by remember {
        mutableStateOf(false)
    }

    CupertinoActionSheet(
        visible = sheetVisible,
        onDismissRequest = {
            sheetVisible = false
        },
        title = {
            CupertinoText("Action Sheet")
        },
        message = {
            CupertinoText("This is a message of the action sheet")
        },
    ) {
        default(
            onClick = {
                sheetVisible = false
            }
        ) {
            CupertinoText("OK")
        }
        destructive(
            onClick = {
                sheetVisible = false
            }
        ) {
            CupertinoText("Delete")
        }

        cancel(
            onClick = {
                sheetVisible = false
            }
        ) {
            CupertinoText("Cancel")
        }
    }
    CupertinoActionSheetNative(
        visible = nativeSheetVisible,
        onDismissRequest = {
            nativeSheetVisible = false
        },
        title = "Action Sheet",
        message = "This is a message of the action sheet"
    ) {
        default(
            onClick = {
                nativeSheetVisible = false
            },
            title = "OK"
        )
        destructive(
            onClick = {
                nativeSheetVisible = false
            },
            title = "Delete"
        )

        cancel(
            onClick = {
                nativeSheetVisible = false
            },
            title = "Cancel"
        )
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CupertinoButton(
            colors = CupertinoButtonDefaults.tintedButtonColors(),
            onClick = {
                sheetVisible = true
            }
        ) {
            CupertinoText("Action Sheet")
        }
        CupertinoButton(
            colors = CupertinoButtonDefaults.tintedButtonColors(),
            onClick = {
                nativeSheetVisible = true
            }
        ) {
            CupertinoText("Native")
        }
    }
}


@Composable
private fun DropdownExample(
    backdrop: Backdrop
) {
    var dropdownVisible by remember { mutableStateOf(false) }
    var pickerSheetVisible by remember { mutableStateOf(false) }
    val layerBackdrop = rememberDefaultBackdrop()

    CupertinoActionSheet(
        visible = pickerSheetVisible,
        onDismissRequest = {
            pickerSheetVisible = false
        },
        title = {
            CupertinoText("Cupertino Picker Sheet")
        },
        message = {
            CupertinoText("Pickers are the most used case for such sheets but you can place below any content you want")
        },
        buttons = {
            default(
                onClick = {
                    pickerSheetVisible = false
                },
            ) {
                CupertinoText("Confirm")
            }
            cancel(
                onClick = {
                    pickerSheetVisible = false
                },
            ) {
                CupertinoText("Cancel")
            }
        },
        content = {
            CupertinoDatePicker(
                state = rememberCupertinoDatePickerState(),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        CupertinoLiquidButton(
            colors = CupertinoLiquidButtonDefaults.glassProminentButtonColors(),
            backdrop = layerBackdrop,
            onClick = {
                pickerSheetVisible = true
            }
        ) {
            CupertinoText("Picker Sheet")
        }

        Spacer(Modifier.weight(1f))
        //Menu bar should be in the box with anchor to align correctly
        Box {
            CupertinoLiquidButton(
                colors = CupertinoLiquidButtonDefaults.glassProminentButtonColors(),
                backdrop = layerBackdrop,
                onClick = { dropdownVisible = !dropdownVisible }
            ) {
                CupertinoText("Menu")
            }

            val red = CupertinoColors.systemRed

            CupertinoDropdownMenu(
                expanded = dropdownVisible,
                onDismissRequest = { dropdownVisible = false },
                backdrop = backdrop
            ) {
                MenuSection(
                    title = {
                        Text("Menu")
                    }
                ) {
                    MenuAction(
                        onClick = {
                            dropdownVisible = false
                        },
                        leadingIcon = {
                            CupertinoIcon(
                                imageVector = CupertinoIcons.Default.SquareAndArrowUp,
                                contentDescription = null
                            )
                        }
                    ) {
                        CupertinoText("Share")
                    }
                    MenuAction(
                        enabled = false,
                        onClick = {
                            dropdownVisible = false
                        },
                        leadingIcon = {
                            CupertinoIcon(
                                imageVector = CupertinoIcons.Default.Bookmark,
                                contentDescription = null
                            )
                        }
                    ) {
                        CupertinoText("Add to Favorites")
                    }
                }

                MenuAction(
                    onClick = {
                        dropdownVisible = false

                    },
                    contentColor = red,
                    leadingIcon = {
                        CupertinoIcon(
                            imageVector = CupertinoIcons.Default.Trash,
                            contentDescription = null
                        )
                    }
                ) {
                    CupertinoText("Delete")
                }
            }
        }
    }
}

@Composable
private fun DropdownExample2(
    backdrop: Backdrop,
    isNative: Boolean
) {
    var dropdownVisible by remember { mutableStateOf(false) }
    val layerBackdrop = rememberDefaultBackdrop()

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        //Menu bar should be in the box with anchor to align correctly
        Box {
            CupertinoLiquidButton(
                colors = CupertinoLiquidButtonDefaults.glassProminentButtonColors(),
                backdrop = layerBackdrop,
                onClick = { dropdownVisible = !dropdownVisible }
            ) {
                CupertinoText("Menu")
            }

            val red = CupertinoColors.systemRed

            if (isNative) {
                CupertinoDropdownMenuNative(
                    expanded = dropdownVisible,
                    onDismissRequest = { dropdownVisible = false },
                    backdrop = backdrop,
                    sections = listOf(
                        CupertinoMenuSectionData(
                            title = "Menu",
                            items = listOf(
                                CupertinoMenuItemData(
                                    title = "Share",
                                    onClick = {
                                        dropdownVisible = false
                                    }
                                )
                            )
                        )
                    )
                )
            } else {
                CupertinoDropdownMenu(
                    expanded = dropdownVisible,
                    onDismissRequest = { dropdownVisible = false },
                    backdrop = backdrop
                ) {
                    MenuSection(
                        title = {
                            Text("Menu")
                        }
                    ) {
                        MenuAction(
                            onClick = {
                                dropdownVisible = false
                            },
                            leadingIcon = {
                                CupertinoIcon(
                                    imageVector = CupertinoIcons.Default.SquareAndArrowUp,
                                    contentDescription = null
                                )
                            }
                        ) {
                            CupertinoText("Share")
                        }
                        MenuAction(
                            enabled = false,
                            onClick = {
                                dropdownVisible = false
                            },
                            leadingIcon = {
                                CupertinoIcon(
                                    imageVector = CupertinoIcons.Default.Bookmark,
                                    contentDescription = null
                                )
                            }
                        ) {
                            CupertinoText("Add to Favorites")
                        }
                    }

                    MenuAction(
                        onClick = {
                            dropdownVisible = false

                        },
                        contentColor = red,
                        leadingIcon = {
                            CupertinoIcon(
                                imageVector = CupertinoIcons.Default.Trash,
                                contentDescription = null
                            )
                        }
                    ) {
                        CupertinoText("Delete")
                    }
                }
            }
        }
    }
}

@Composable
private fun LinksWithIcons(
    onSheetClicked: () -> Unit,
    onNavigate: (NavKey) -> Unit,
) {
    CupertinoSection {
        SectionLink(
            icon = {
                CupertinoLinkIcon(
                    imageVector = CupertinoIcons.Default.Heart,
                    contentDescription = null,
                    containerColor = CupertinoColors.systemRed
                )
            },
            caption = {
                Text("One")
            },
            onClick = {
                onNavigate(RootRoute.Icons)
            }
        ) {
            CupertinoText("SF Symbols")
        }

//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(200.dp)
//                .background(Color.Black)
//        )

        SectionLink(
            icon = {
                CupertinoLinkIcon(
                    imageVector = CupertinoIcons.Default.SquareSplit1x2,
                    containerColor = CupertinoColors.systemIndigo
                )
            },
            caption = {
                Text("Two")
            },
            onClick = {
                onNavigate(RootRoute.Sections)
            }
        ) {
            CupertinoText("Sections")
        }


        SectionLink(
            icon = {
                CupertinoLinkIcon(
                    imageVector = CupertinoIcons.Default.Iphone,
                    containerColor = CupertinoColors.systemBlue
                )
            },
            caption = {
                Text("Three")
            },
            onClick = {
                onNavigate(RootRoute.Adaptive)
            }
        ) {
            CupertinoText("Adaptive Widgets")
        }

        SectionLink(
            icon = {
                CupertinoLinkIcon(
                    imageVector = CupertinoIcons.Default.RectangleStack,
                    contentDescription = null,
                    containerColor = CupertinoColors.systemCyan
                )
            },
            caption = {
                Text("Four")
            },
            onClick = onSheetClicked
        ) {
            CupertinoText("Bottom Sheet")
        }
    }
}
