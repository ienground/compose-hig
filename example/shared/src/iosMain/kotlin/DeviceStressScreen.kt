@file:OptIn(zone.ien.hig.ExperimentalCupertinoApi::class, zone.ien.hig.adaptive.ExperimentalAdaptiveApi::class)

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import zone.ien.hig.icons.CupertinoIcons
import zone.ien.hig.icons.outlined.Heart
import adaptive.AdaptiveWidgetsScreen
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cupertino.CatalogComponent
import cupertino.ComponentDetailScreen
import kotlinx.coroutines.delay
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSProcessInfo
import zone.ien.hig.*
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.adaptive.*
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop

/** iPhone 12에서 동일한 부하를 반복 실행하는 예제 앱 전용 계측 화면. */
@Composable
internal fun DeviceStressScreen() {
    var phase by remember { mutableStateOf("warmup") }
    var component by remember { mutableStateOf<CatalogComponent?>(null) }
    var cycle by remember { mutableIntStateOf(0) }
    var tick by remember { mutableIntStateOf(0) }
    val scroll = rememberScrollState()
    val arguments = remember { NSProcessInfo.processInfo.arguments }
    val onlyCase = remember {
        arguments.filterIsInstance<String>().firstOrNull { it.startsWith("--hig-stress-case=") }?.substringAfter('=')
    }
    LaunchedEffect(Unit) {
        suspend fun enter(name: String) {
            phase = name
            NSNotificationCenter.defaultCenter.postNotificationName("HIGStressPhase", null, mapOf("case" to name))
            scroll.scrollTo(0)
            withFrameNanos { }
        }
        suspend fun run(duration: Long, maximum: Int = 240) {
            val start = withFrameNanos { it }
            var frame = 0
            while (true) {
                val now = withFrameNanos { it }
                if (now - start > duration * 1_000_000) break
                val limit = minOf(maximum, scroll.maxValue)
                val step = frame++ % 120
                scroll.scrollTo((if (step < 60) step else 120 - step) * limit / 60)
                tick++
            }
        }
        delay(5_000)
        if (!arguments.contains("--hig-stress-focus") && onlyCase == null) {
            repeat(2) { round ->
                cycle = round
                for (item in CatalogComponent.entries) {
                    component = item
                    enter("catalog/$round/${item.name}")
                    run(2_500, 800)
                    component = null
                    enter("unmount/$round/${item.name}")
                    run(250)
                }
            }
        }
        component = null
        val focusedCases = listOf("plain-compose", "baseline", "scaffold", "glass-static", "glass-adaptive", "controls", "native-pickers", "native-wheel", "adaptive", "alert", "liquid-alert", "native-alert", "action-sheet", "native-action-sheet", "menu", "native-menu", "popup", "popover", "sheet", "native-navigation", "color-picker", "sections", "icons", "adaptive-extra")
        require(onlyCase == null || onlyCase in focusedCases + "native-pickers-cancel") { "알 수 없는 부하 단계: $onlyCase" }
        val selectedCases = when {
            onlyCase != null -> listOf(onlyCase)
            arguments.contains("--hig-stress-core-only") -> focusedCases.take(7)
            else -> focusedCases
        }
        for (name in selectedCases) {
            enter("focused/$name")
            run(10_000)
        }
        enter("rest")
        run(2_000)
        NSNotificationCenter.defaultCenter.postNotificationName("HIGStressComplete", null)
        phase = "완료"
    }
    AdaptiveTheme(target = Theme.Cupertino, cupertino = CupertinoThemeSpec.Default()) {
        key(component, cycle) {
            if (component != null) {
                ComponentDetailScreen(component!!, scroll) {}
            } else {
                val mode = phase.substringAfter('/')
                val backdrop = rememberLayerBackdrop()
                val content: @Composable (PaddingValues) -> Unit = { padding ->
                    Box(Modifier.fillMaxSize()) {
                        Box(Modifier.matchParentSize()
                            .then(if (mode == "plain-compose") Modifier else Modifier.layerBackdrop(backdrop))
                            .background(Color.White))
                        Column(Modifier.fillMaxSize().verticalScroll(scroll).cupertinoScrollEdge(scroll).padding(padding)
                            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (mode == "plain-compose") androidx.compose.foundation.text.BasicText(text = phase) else CupertinoText(text = phase)
                        when (mode) {
                            "glass-static", "glass-adaptive" -> repeat(12) {
                                CupertinoLiquidButton(onClick = {}, backdrop = backdrop,
                                    isBackgroundAdaptive = mode == "glass-adaptive") { CupertinoText(text = "버튼 $it") }
                            }
                            "controls" -> {
                                CupertinoSwitch(checked = tick / 15 % 2 == 0, onCheckedChange = {}, backdrop = backdrop)
                                CupertinoCheckBox(checked = tick / 15 % 2 == 0, onCheckedChange = {})
                                CupertinoSlider(value = (tick % 120) / 120f, onValueChange = {}, backdrop = backdrop)
                                CupertinoWheelPicker(state = rememberCupertinoPickerState(), items = (0..30).toList()) {
                                    CupertinoText(text = "$it")
                                }
                                CupertinoActivityIndicator()
                            }
                            "native-pickers", "native-pickers-cancel" -> key(if (mode == "native-pickers-cancel") tick / 5 else 0) {
                                CupertinoDatePickerNative(state = rememberCupertinoDatePickerState())
                                CupertinoTimePickerNative(state = rememberCupertinoTimePickerState())
                                CupertinoDateTimePickerNative(state = rememberCupertinoDateTimePickerState())
                            }
                        }
                        repeat(30) { Box(Modifier.fillMaxWidth().height(50.dp).background(if (it % 2 == 0) Color.LightGray else Color.White)) }
                        }
                    }
                }
                if (mode == "adaptive") {
                    key(tick / 60) { AdaptiveWidgetsScreen(RootUiState(), {}, {}) }
                } else if (mode == "sections") {
                    key(tick / 60) { sections.SectionsScreen {} }
                } else if (mode == "icons") {
                    key(tick / 60) { icons.IconsScreen {} }
                } else if (mode == "adaptive-extra") {
                    Column(Modifier.fillMaxSize().padding(top = 80.dp)) {
                        AdaptiveSurface {
                            AdaptiveRangeSlider(value = 0.2f..(0.5f + (tick % 30) / 100f), onValueChange = {})
                        }
                        AdaptiveTonalButton(onClick = {}) { CupertinoText(text = "동작") }
                        AdaptiveHorizontalDivider()
                        AdaptiveVerticalDivider(Modifier.height(40.dp))
                    }
                } else if (mode in setOf("alert", "liquid-alert", "native-alert", "action-sheet", "native-action-sheet", "menu", "native-menu", "popup", "popover", "sheet", "native-navigation", "color-picker", "sections", "icons", "adaptive-extra", "native-wheel")) {
                    PresentationStressContent(mode, tick / 60 % 2 == 0, tick / 30, backdrop)
                } else if (mode == "scaffold") {
                    CupertinoScaffold(topBar = { CupertinoTopAppBar(title = { CupertinoText(text = "스크롤 계측") }) }, content = content)
                } else content(PaddingValues())
            }
        }
    }
}

@Composable
private fun PresentationStressContent(mode: String, visible: Boolean, selection: Int, backdrop: com.kyant.backdrop.backdrops.LayerBackdrop) {
    Box(Modifier.fillMaxSize().padding(top = 80.dp), contentAlignment = Alignment.TopCenter) {
        Box(Modifier.matchParentSize().layerBackdrop(backdrop).background(Color.White))
        CupertinoText(text = mode)
        when (mode) {
            "alert" -> if (visible) CupertinoAlertDialog(onDismissRequest = {}, title = { CupertinoText(text = "테스트") }) {
                default(onClick = {}) { CupertinoText(text = "확인") }
            }
            "liquid-alert" -> if (visible) CupertinoLiquidAlertDialog(onDismissRequest = {}, backdrop = backdrop, title = { CupertinoText(text = "테스트") }) {
                default(onClick = {}) { CupertinoText(text = "확인") }
            }
            "native-alert" -> if (visible) CupertinoAlertDialogNative(onDismissRequest = {}, title = "테스트", message = "반복 표시") {
                default(onClick = {}, title = "확인")
            }
            "action-sheet" -> CupertinoActionSheet(visible = visible, onDismissRequest = {}, title = { CupertinoText(text = "테스트") }) {
                default(onClick = {}) { CupertinoText(text = "확인") }
                cancel(onClick = {}) { CupertinoText(text = "취소") }
            }
            "native-action-sheet" -> CupertinoActionSheetNative(visible = visible, onDismissRequest = {}, title = "테스트") {
                default(onClick = {}, title = "확인")
                cancel(onClick = {}, title = "취소")
            }
            "menu" -> CupertinoDropdownMenu(expanded = visible, onDismissRequest = {}) {
                MenuAction(onClick = {}) { CupertinoText(text = "동작") }
            }
            "native-menu" -> CupertinoDropdownMenuNative(expanded = visible, onDismissRequest = {}, items = listOf(CupertinoMenuItemData(title = "동작", onClick = {})))
            "popup" -> CupertinoPopupButton(expanded = visible, onExpandedChange = {}, title = { CupertinoText(text = "선택") }) {
                MenuPickerAction(isSelected = true, onClick = {}, title = { CupertinoText(text = "항목") })
            }
            "popover" -> CupertinoPopover(expanded = visible, onDismissRequest = {}, backdrop = backdrop) { CupertinoText(text = "반복 표시") }
            "sheet" -> {
                val state = rememberCupertinoBottomSheetScaffoldState(rememberCupertinoSheetState(presentationStyle = PresentationStyle.Modal(detents = setOf(PresentationDetent.Medium, PresentationDetent.Large))))
                LaunchedEffect(visible) { if (visible) state.bottomSheetState.show() else state.bottomSheetState.hide() }
                CupertinoBottomSheetScaffold(scaffoldState = state, sheetContent = { CupertinoText(text = "시트 계측") }) { CupertinoText(text = "배경") }
            }
            "native-navigation" -> {
                val painter = rememberVectorPainter(CupertinoIcons.Default.Heart)
                CupertinoNavigationBarNative(backdrop = backdrop, selectedTabIndex = { selection % 3 }, onTabSelected = {}, items = List(3) {
                    CupertinoNavigationBarItemData(onClick = {}, icon = painter, label = "탭 $it")
                })
            }
            "color-picker" -> if (visible) CupertinoColorPickerNative(color = if (selection % 2 == 0) Color.Red else Color.Blue, onColorChanged = {}, onDismissRequest = {})
            "native-wheel" -> {
                val state = rememberCupertinoPickerState()
                LaunchedEffect(selection) { state.scrollToItem(selection % 20) }
                CupertinoPickerNative(state = state, items = (0..20).toList()) { "$it" }
            }
        }
    }
}
