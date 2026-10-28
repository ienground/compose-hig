@file:OptIn(zone.ien.hig.ExperimentalCupertinoApi::class, kotlin.js.ExperimentalWasmJsInterop::class)

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import cupertino.DatePickerExample
import cupertino.DateTimePicker
import cupertino.PickerExample
import cupertino.RecentCallsSwipeChapter
import cupertino.TimePickerExample
import kotlinx.browser.window
import kotlinx.coroutines.launch
import org.w3c.dom.events.Event
import org.jetbrains.compose.resources.painterResource
import zone.ien.hig.composeapp.generated.resources.Res
import zone.ien.hig.composeapp.generated.resources.glass_landscape
import zone.ien.hig.MenuAction
import zone.ien.hig.MenuDivider
import zone.ien.hig.default
import zone.ien.hig.cancel
import zone.ien.hig.destructive
import zone.ien.hig.CupertinoActionSheet
import zone.ien.hig.CupertinoActivityIndicator
import zone.ien.hig.CupertinoAlertDialog
import zone.ien.hig.CupertinoBorderedTextField
import zone.ien.hig.CupertinoBottomSheetContent
import zone.ien.hig.CupertinoBottomSheetScaffold
import zone.ien.hig.CupertinoBottomSheetScaffoldDefaults
import zone.ien.hig.CupertinoButton
import zone.ien.hig.CupertinoButtonDefaults
import zone.ien.hig.CupertinoButtonSize
import zone.ien.hig.CupertinoCheckBox
import zone.ien.hig.CupertinoDropdownMenu
import zone.ien.hig.CupertinoHorizontalDivider
import zone.ien.hig.CupertinoIcon
import zone.ien.hig.CupertinoIconButton
import zone.ien.hig.CupertinoLiquidAlertDialog
import zone.ien.hig.CupertinoLiquidButton
import zone.ien.hig.CupertinoLiquidButtonDefaults
import zone.ien.hig.CupertinoLiquidIconButton
import zone.ien.hig.CupertinoNavigationBar
import zone.ien.hig.CupertinoNavigationBarItem
import zone.ien.hig.CupertinoRangeSlider
import zone.ien.hig.CupertinoScaffold
import zone.ien.hig.CupertinoSearchTextField
import zone.ien.hig.CupertinoSegmentedControl
import zone.ien.hig.CupertinoSegmentedControlTab
import zone.ien.hig.CupertinoSlider
import zone.ien.hig.CupertinoSmallFloatingActionButton
import zone.ien.hig.CupertinoSwitch
import zone.ien.hig.CupertinoText
import zone.ien.hig.CupertinoTextField
import zone.ien.hig.CupertinoTopAppBar
import zone.ien.hig.CupertinoTriStateCheckBox
import zone.ien.hig.rememberCupertinoBottomSheetScaffoldState
import zone.ien.hig.rememberCupertinoDatePickerState
import zone.ien.hig.rememberCupertinoDateTimePickerState
import zone.ien.hig.rememberCupertinoPickerState
import zone.ien.hig.rememberCupertinoTimePickerState
import zone.ien.hig.icons.CupertinoIcons
import zone.ien.hig.icons.filled.Heart
import zone.ien.hig.section.CupertinoSection
import zone.ien.hig.section.ProvideSectionStyle
import zone.ien.hig.section.SectionItem
import zone.ien.hig.section.SectionLink
import zone.ien.hig.section.SectionStyle
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.theme.darkColorScheme
import zone.ien.hig.theme.lightColorScheme

@JsFun("(key) => new URLSearchParams(window.location.hash.slice(1)).get(key) || ''")
private external fun option(key: String): String

@JsFun("() => { document.getElementById('loading')?.remove(); }")
private external fun previewReady()

private fun previewText(korean: String, english: String): String =
    if (option("language") == "ko") korean else english

@Composable
internal fun ComponentPlayground() {
    var revision by remember { mutableIntStateOf(0) }
    DisposableEffect(Unit) {
        val listener: (Event) -> Unit = { revision++ }
        window.addEventListener("hashchange", listener)
        onDispose { window.removeEventListener("hashchange", listener) }
    }
    key(revision) {
        val component = option("component").ifEmpty { "button" }
        if (component == "sample") {
            App()
        } else {
            val accent = when (option("accent")) {
                "green" -> Color(0xFF248A3D)
                "orange" -> Color(0xFFFF9500)
                "pink" -> Color(0xFFFF2D55)
                else -> Color(0xFF007AFF)
            }
            CupertinoTheme(
                colorScheme = if (option("dark") == "true") darkColorScheme(accent = accent)
                else lightColorScheme(accent = accent),
            ) {
                if (component == "alert-dialog" || component == "action-sheet") {
                    CupertinoScaffold(containerColor = Color.Transparent, contentWindowInsets = WindowInsets(0)) {
                        PlaygroundPreview(component)
                    }
                } else {
                    PlaygroundPreview(component)
                }
            }
        }
        LaunchedEffect(Unit) { previewReady() }
    }
}

@Composable
private fun PlaygroundPreview(component: String) {
    val enabled = option("enabled") != "false"
    val label = option("label").ifEmpty { previewText("계속", "Continue") }
    val value = option("value").toFloatOrNull()?.coerceIn(0f, 1f) ?: .5f
    val size = CupertinoButtonSize.entries.find { it.name == option("size") } ?: CupertinoButtonSize.Regular
    val backdrop = rememberLayerBackdrop()
    var checked by remember { mutableStateOf(option("checked") != "false") }
    var text by remember { mutableStateOf(option("text")) }
    var slider by remember { mutableFloatStateOf(value) }
    var range by remember { mutableStateOf(.2f..value.coerceAtLeast(.3f)) }
    var selected by remember { mutableIntStateOf(option("selected").toIntOrNull()?.coerceIn(0, 2) ?: 0) }
    var clicks by remember { mutableIntStateOf(0) }
    var visible by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf("") }
    val imageBackground = option("background") == "image"
    val scroll = rememberScrollState()
    val scope = rememberCoroutineScope()
    val buttonColors = when (option("variant")) {
        "tinted" -> CupertinoButtonDefaults.tintedButtonColors()
        "plain" -> CupertinoButtonDefaults.plainButtonColors()
        "gray" -> CupertinoButtonDefaults.grayButtonColors()
        else -> CupertinoButtonDefaults.filledButtonColors()
    }
    val liquidColors = when (option("variant")) {
        "glass" -> CupertinoLiquidButtonDefaults.glassButtonColors()
        else -> CupertinoLiquidButtonDefaults.glassProminentButtonColors()
    }
    val screenComponent = component in listOf("scaffold", "top-bar", "navigation-bar", "bottom-sheet")
    val backgroundContent: @Composable (Modifier) -> Unit = { modifier ->
        Box(modifier.background(CupertinoTheme.colorScheme.systemGroupedBackground)) {
            if (imageBackground) {
                Image(
                    painter = painterResource(Res.drawable.glass_landscape),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
    Box(Modifier.fillMaxSize().background(CupertinoTheme.colorScheme.systemGroupedBackground)) {
        if (!screenComponent) {
            backgroundContent(Modifier.matchParentSize().layerBackdrop(backdrop))
        }
        Column(
            Modifier.fillMaxSize().verticalScroll(scroll).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (component) {
                "button" -> {
                    CupertinoButton(onClick = { clicks++ }, enabled = enabled, size = size, colors = buttonColors) { CupertinoText(label) }
                    CupertinoText(previewText("클릭: $clicks", "Clicks: $clicks"))
                }
                "liquid-button" -> {
                    CupertinoLiquidButton(onClick = { clicks++ }, enabled = enabled, size = size, colors = liquidColors, backdrop = backdrop) { CupertinoText(label) }
                    CupertinoText(previewText("클릭: $clicks", "Clicks: $clicks"))
                }
                "icon-button" -> {
                    CupertinoIconButton(onClick = { clicks++ }, enabled = enabled) { CupertinoIcon(CupertinoIcons.Filled.Heart, contentDescription = previewText("좋아요", "Like")) }
                    CupertinoText(previewText("좋아요: $clicks", "Likes: $clicks"))
                }
                "liquid-icon-button" -> {
                    CupertinoLiquidIconButton(onClick = { clicks++ }, enabled = enabled, backdrop = backdrop) { CupertinoIcon(CupertinoIcons.Filled.Heart, contentDescription = previewText("좋아요", "Like")) }
                    CupertinoText(previewText("좋아요: $clicks", "Likes: $clicks"))
                }
                "switch" -> {
                    CupertinoSwitch(checked = checked, onCheckedChange = { checked = it }, enabled = enabled, backdrop = backdrop)
                    CupertinoText(if (checked) previewText("켜짐", "On") else previewText("꺼짐", "Off"))
                }
                "checkbox" -> {
                    CupertinoCheckBox(checked = checked, onCheckedChange = { checked = it }, enabled = enabled)
                    CupertinoText(if (checked) previewText("선택됨", "Checked") else previewText("선택 안 됨", "Unchecked"))
                }
                "tri-state-checkbox" -> {
                    val states = listOf(ToggleableState.Indeterminate, ToggleableState.On, ToggleableState.Off)
                    CupertinoTriStateCheckBox(state = states[selected], onClick = { selected = (selected + 1) % 3 }, enabled = enabled)
                    CupertinoText(states[selected].toString())
                }
                "slider" -> {
                    CupertinoSlider(value = slider, onValueChange = { slider = it }, enabled = enabled, steps = option("steps").toIntOrNull()?.coerceIn(0, 10) ?: 0, backdrop = backdrop)
                    CupertinoText(previewText("값: ${(slider * 100).toInt()}%", "Value: ${(slider * 100).toInt()}%"))
                }
                "range-slider" -> {
                    CupertinoRangeSlider(value = range, onValueChange = { range = it }, enabled = enabled, backdrop = backdrop)
                    CupertinoText(previewText("범위: ${(range.start * 100).toInt()}–${(range.endInclusive * 100).toInt()}%", "Range: ${(range.start * 100).toInt()}–${(range.endInclusive * 100).toInt()}%"))
                }
                "text-field" -> CupertinoTextField(value = text, onValueChange = { text = it }, enabled = enabled, isError = option("error") == "true", placeholder = { CupertinoText(label) }, singleLine = true)
                "bordered-text-field" -> CupertinoBorderedTextField(value = text, onValueChange = { text = it }, enabled = enabled, isError = option("error") == "true", placeholder = { CupertinoText(label) }, singleLine = true)
                "search-field" -> CupertinoSearchTextField(value = text, onValueChange = { text = it }, enabled = enabled, placeholder = { CupertinoText(label) })
                "segmented-control" -> {
                    CupertinoSegmentedControl(selectedTabIndex = selected, backdrop = backdrop) {
                        listOf(previewText("전체", "All"), previewText("즐겨찾기", "Favorites"), previewText("최근", "Recent")).forEachIndexed { index, title ->
                            CupertinoSegmentedControlTab(isSelected = selected == index, onClick = { selected = index }) { CupertinoText(title) }
                        }
                    }
                    CupertinoText(previewText("선택: ${selected + 1}", "Selected: ${selected + 1}"))
                }
                "activity-indicator" -> {
                    if (option("determinate") == "true") CupertinoActivityIndicator(progress = value, size = 32.dp)
                    else CupertinoActivityIndicator(size = 32.dp)
                }
                "divider" -> {
                    CupertinoText(label)
                    CupertinoHorizontalDivider()
                    CupertinoText(previewText("콘텐츠 사이를 구분합니다.", "Separates content."))
                }
                "text" -> CupertinoText(label, style = when (option("style")) {
                    "largeTitle" -> CupertinoTheme.typography.largeTitle
                    "title1" -> CupertinoTheme.typography.title1
                    "caption1" -> CupertinoTheme.typography.caption1
                    else -> CupertinoTheme.typography.body
                })
                "icon" -> CupertinoIcon(CupertinoIcons.Filled.Heart, contentDescription = label, modifier = Modifier.size((24 + value * 48).dp), tint = CupertinoTheme.colorScheme.accent)
                "section" -> ProvideSectionStyle(if (option("grouped") != "false") SectionStyle.InsetGrouped else SectionStyle.Grouped) {
                    CupertinoSection(title = { CupertinoText(label) }) {
                        SectionItem(trailingContent = { CupertinoSwitch(checked = checked, onCheckedChange = { checked = it }, backdrop = backdrop) }) { CupertinoText(previewText("알림", "Notifications")) }
                        SectionLink(onClick = { clicks++ }) { CupertinoText(previewText("상세 보기 · $clicks", "View details · $clicks")) }
                    }
                }
                "wheel-picker" -> {
                    val items = listOf(previewText("서울", "Seoul"), previewText("부산", "Busan"), previewText("대구", "Daegu"), previewText("인천", "Incheon"), previewText("광주", "Gwangju"), previewText("대전", "Daejeon"), previewText("제주", "Jeju"))
                    val state = rememberCupertinoPickerState()
                    PickerExample(items, state)
                    CupertinoText(previewText("선택: ${items[state.selectedItemIndex(items.size)]}", "Selected: ${items[state.selectedItemIndex(items.size)]}"))
                }
                "time-picker" -> {
                    val state = rememberCupertinoTimePickerState()
                    TimePickerExample(state, false)
                    CupertinoText("${state.hour}:${state.minute}")
                }
                "date-picker" -> DatePickerExample(rememberCupertinoDatePickerState(), false)
                "date-time-picker" -> DateTimePicker(rememberCupertinoDateTimePickerState(), false)
                "alert-dialog", "liquid-alert-dialog", "action-sheet", "dropdown-menu" -> {
                    Box {
                        CupertinoButton(onClick = { visible = true }, enabled = enabled) { CupertinoText(label) }
                        if (component == "dropdown-menu") {
                            CupertinoDropdownMenu(expanded = visible, onDismissRequest = { visible = false }, backdrop = backdrop) {
                                MenuAction(onClick = { result = previewText("공유", "Share"); visible = false }) { CupertinoText(previewText("공유", "Share")) }
                                MenuDivider()
                                MenuAction(onClick = { result = previewText("삭제", "Delete"); visible = false }) { CupertinoText(previewText("삭제", "Delete")) }
                            }
                        }
                    }
                    if (component == "action-sheet") CupertinoActionSheet(visible = visible, onDismissRequest = { visible = false }, title = { CupertinoText(label) }, message = { CupertinoText(previewText("실행할 작업을 선택하세요.", "Choose an action.")) }) {
                        default(onClick = { result = previewText("저장", "Save"); visible = false }) { CupertinoText(previewText("저장", "Save")) }
                        destructive(onClick = { result = previewText("삭제", "Delete"); visible = false }) { CupertinoText(previewText("삭제", "Delete")) }
                        cancel(onClick = { visible = false }) { CupertinoText(previewText("취소", "Cancel")) }
                    }
                    if (visible && component == "alert-dialog") CupertinoAlertDialog(onDismissRequest = { visible = false }, title = { CupertinoText(label) }, message = { CupertinoText(previewText("변경 사항을 저장할까요?", "Would you like to save your changes?")) }) {
                        default(onClick = { result = previewText("저장됨", "Saved"); visible = false }) { CupertinoText(previewText("저장", "Save")) }
                        cancel(onClick = { visible = false }) { CupertinoText(previewText("취소", "Cancel")) }
                    }
                    if (visible && component == "liquid-alert-dialog") CupertinoLiquidAlertDialog(onDismissRequest = { visible = false }, title = { CupertinoText(label) }, message = { CupertinoText(previewText("변경 사항을 저장할까요?", "Would you like to save your changes?")) }, backdrop = backdrop) {
                        default(onClick = { result = previewText("저장됨", "Saved"); visible = false }) { CupertinoText(previewText("저장", "Save")) }
                        cancel(onClick = { visible = false }) { CupertinoText(previewText("취소", "Cancel")) }
                    }
                    CupertinoText(result)
                }
                "swipe-box" -> RecentCallsSwipeChapter(scroll)
                "floating-action-button" -> {
                    CupertinoSmallFloatingActionButton(onClick = { clicks++ }, backdrop = backdrop) { CupertinoIcon(CupertinoIcons.Filled.Heart, contentDescription = previewText("좋아요", "Like")) }
                    CupertinoText(previewText("클릭: $clicks", "Clicks: $clicks"))
                }
                else -> if (!screenComponent) CupertinoText(previewText("컴포넌트를 찾을 수 없습니다.", "Component not found."))
            }
        }
        if (screenComponent) {
            val topBar: @Composable () -> Unit = { CupertinoTopAppBar(title = { CupertinoText(label) }, backdrop = backdrop, windowInsets = WindowInsets(0)) }
            val bottomBar: @Composable () -> Unit = {
                CupertinoNavigationBar(backdrop = backdrop, selectedTabIndex = { selected }, onTabSelected = { selected = it }, tabsCount = 3, windowInsets = WindowInsets(0)) {
                    listOf(previewText("홈", "Home"), previewText("즐겨찾기", "Favorites"), previewText("설정", "Settings")).forEachIndexed { index, title ->
                        CupertinoNavigationBarItem(onClick = { selected = index }, icon = { CupertinoIcon(CupertinoIcons.Filled.Heart, contentDescription = null) }, label = { CupertinoText(title) })
                    }
                }
            }
            if (component == "bottom-sheet") {
                val state = rememberCupertinoBottomSheetScaffoldState()
                CupertinoBottomSheetScaffold(
                    scaffoldState = state,
                    colors = CupertinoBottomSheetScaffoldDefaults.colors(containerColor = Color.Transparent),
                    sheetContent = {
                    CupertinoBottomSheetContent { padding ->
                        Column(Modifier.fillMaxWidth().padding(padding).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            CupertinoText(label)
                            CupertinoText(previewText("아래로 드래그하거나 버튼으로 닫을 수 있습니다.", "Drag down or use the button to dismiss."))
                            CupertinoButton(onClick = { scope.launch { state.bottomSheetState.hide() } }) { CupertinoText(previewText("닫기", "Close")) }
                        }
                    }
                }) { padding ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        backgroundContent(Modifier.matchParentSize().layerBackdrop(backdrop))
                        CupertinoButton(modifier = Modifier.padding(padding), onClick = { scope.launch { state.bottomSheetState.show() } }) { CupertinoText(label) }
                    }
                }
            } else CupertinoScaffold(
                containerColor = Color.Transparent,
                topBar = { if (component != "navigation-bar") topBar() },
                bottomBar = { if (component != "top-bar") bottomBar() },
            ) { padding ->
                Box(Modifier.fillMaxSize()) {
                    backgroundContent(Modifier.matchParentSize().layerBackdrop(backdrop))
                    Column(Modifier.fillMaxSize().padding(padding).padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        CupertinoText("${listOf(previewText("홈", "Home"), previewText("즐겨찾기", "Favorites"), previewText("설정", "Settings"))[selected]}")
                        CupertinoText(previewText("바를 눌러 화면 상태를 변경하세요.", "Use the bar to switch screens."))
                    }
                }
            }
        }
    }
}
