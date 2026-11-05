@file:OptIn(zone.ien.hig.ExperimentalCupertinoApi::class)

package cupertino

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.LayerBackdrop
import org.jetbrains.compose.resources.painterResource
import zone.ien.hig.composeapp.generated.resources.Res
import zone.ien.hig.composeapp.generated.resources.catalog_landscape
import zone.ien.hig.CupertinoPopupButton
import zone.ien.hig.MenuPickerAction
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import zone.ien.hig.CupertinoLiquidIconButton
import zone.ien.hig.CupertinoNavigateBackLiquidButton
import zone.ien.hig.CupertinoIcon
import zone.ien.hig.icons.CupertinoIcons
import zone.ien.hig.icons.outlined.ArrowCounterclockwise
import zone.ien.hig.CupertinoBottomSheetScaffold
import zone.ien.hig.CupertinoBottomSheetScaffoldDefaults
import zone.ien.hig.PresentationDetent
import zone.ien.hig.PresentationStyle
import zone.ien.hig.rememberCupertinoBottomSheetScaffoldState
import zone.ien.hig.rememberCupertinoSheetState
import kotlinx.coroutines.launch
import zone.ien.hig.CupertinoSwitch
import zone.ien.hig.CupertinoSurface
import zone.ien.hig.section.CupertinoSection
import zone.ien.hig.section.SectionStyle
import zone.ien.hig.utils.rememberDefaultBackdrop
import zone.ien.hig.CupertinoText
import zone.ien.hig.CupertinoTopAppBar
import zone.ien.hig.cupertinoScrollEdge
import zone.ien.hig.theme.CupertinoTheme

@Composable
fun ComponentDetailScreen(component: CatalogComponent, navigateBack: () -> Unit) {
    var revision by rememberSaveable(component) { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()
    val background = CupertinoTheme.colorScheme.systemGroupedBackground
    val backdrop = rememberLayerBackdrop {
        drawRect(background)
        drawContent()
    }
    val scope = rememberCoroutineScope()
    val sheetState = key(component, revision) {
        rememberCupertinoBottomSheetScaffoldState(rememberCupertinoSheetState(
            presentationStyle = PresentationStyle.Modal(detents = setOf(PresentationDetent.Medium, PresentationDetent.Large)),
        ))
    }
    CupertinoBottomSheetScaffold(
        colors = CupertinoBottomSheetScaffoldDefaults.colors(containerColor = CupertinoTheme.colorScheme.systemGroupedBackground),
        scaffoldState = sheetState,
        sheetContent = {
            key(component, revision) {
                SheetSample(sheetState, rememberLazyListState(), CupertinoTheme.colorScheme.tertiarySystemBackground, backdrop)
            }
        },
        topBar = {
            CupertinoTopAppBar(
                title = { CupertinoText(text = component.title) },
                navigationIcon = {
                    CupertinoNavigateBackLiquidButton(
                        onClick = navigateBack,
                        backdrop = backdrop,
                        modifier = Modifier.testTag("navigate-back").semantics { contentDescription = "Back" },
                    )
                },
                actions = {
                    CupertinoLiquidIconButton(onClick = { revision++ }, backdrop = backdrop, modifier = Modifier.testTag("reset-demo")) {
                        CupertinoIcon(CupertinoIcons.Default.ArrowCounterclockwise, contentDescription = "Reset")
                    }
                },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().layerBackdrop(backdrop).padding(padding).imePadding(),
            contentAlignment = Alignment.TopCenter,
        ) {
            CompositionLocalProvider(
                LocalDemoBackdrop provides backdrop,
                LocalDemoPhoto provides (component in glassComponents),
            ) {
                Column(
                    modifier = Modifier.widthIn(max = 760.dp).fillMaxSize().verticalScroll(scrollState)
                        .cupertinoScrollEdge(scrollState).padding(top = 12.dp, bottom = 28.dp).testTag("component-detail"),
                    verticalArrangement = Arrangement.spacedBy(28.dp),
                ) {
                    Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        CupertinoText(text = component.category.title.uppercase(), style = CupertinoTheme.typography.caption1,
                            color = CupertinoTheme.colorScheme.secondaryLabel)
                        CupertinoText(text = component.description, color = CupertinoTheme.colorScheme.secondaryLabel,
                            style = CupertinoTheme.typography.subhead)
                    }
                    key(component, revision) {
                        when (component) {
                            CatalogComponent.Button -> ButtonDemo()
                            CatalogComponent.LiquidButton -> LiquidButtonDemo()
                            CatalogComponent.FloatingActionButton -> FloatingActionButtonDemo()
                            CatalogComponent.Switch -> SwitchDemo()
                            CatalogComponent.Checkbox -> CheckboxDemo()
                            CatalogComponent.Slider -> SliderDemo()
                            CatalogComponent.Stepper -> StepperDemo()
                            CatalogComponent.SegmentedControl -> SegmentedControlDemo()
                            CatalogComponent.PageControl -> PageControlDemo()
                            CatalogComponent.TextField -> TextFieldDemo()
                            CatalogComponent.SearchField -> SearchFieldDemo(scrollState)
                            CatalogComponent.WheelPicker -> WheelPickerDemo()
                            CatalogComponent.DatePicker, CatalogComponent.TimePicker, CatalogComponent.DateTimePicker -> DateTimeDemo(component)
                            CatalogComponent.Progress -> ProgressDemo()
                            CatalogComponent.Badge -> BadgeDemo()
                            CatalogComponent.EmptyState -> EmptyStateDemo()
                            CatalogComponent.AlertDialog -> AlertDialogDemo()
                            CatalogComponent.ActionSheet -> ActionSheetDemo()
                            CatalogComponent.Menu -> MenuDemo()
                            CatalogComponent.PopupButton -> PopupButtonDemo()
                            CatalogComponent.Popover -> PopoverDemo()
                            CatalogComponent.BottomSheet -> BottomSheetDemo(sheetState.bottomSheetState) {
                                scope.launch { sheetState.bottomSheetState.show() }
                            }
                            CatalogComponent.SwipeBox -> SwipeBoxDemo(scrollState)
                            CatalogComponent.NavigationBar -> NavigationBarDemo()
                            CatalogComponent.TopBar -> TopBarDemo()
                            CatalogComponent.Foundation -> FoundationDemo()
                        }
                    }
                    DemoSection("API") {
                        component.apiNames.split(' ').forEach { name ->
                            CupertinoText(text = name, style = CupertinoTheme.typography.footnote)
                        }
                    }
                }
            }
        }
    }
}

internal val LocalDemoBackdrop = staticCompositionLocalOf<LayerBackdrop?> { null }
private val LocalDemoPhoto = staticCompositionLocalOf { false }
private val glassComponents = setOf(
    CatalogComponent.LiquidButton, CatalogComponent.FloatingActionButton, CatalogComponent.Switch,
    CatalogComponent.Slider, CatalogComponent.SegmentedControl, CatalogComponent.NavigationBar,
    CatalogComponent.TopBar, CatalogComponent.AlertDialog, CatalogComponent.Menu,
    CatalogComponent.PopupButton, CatalogComponent.Popover,
)

@Composable
internal fun DemoSection(title: String, description: String? = null, content: @Composable ColumnScope.() -> Unit) {
    val isPreview = title == "Preview" || title == "Sizes"
    if (isPreview) {
        val backdrop = rememberLayerBackdrop()
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CupertinoText(text = title, style = CupertinoTheme.typography.headline)
            if (description != null) {
                CupertinoText(text = description, color = CupertinoTheme.colorScheme.secondaryLabel,
                    style = CupertinoTheme.typography.footnote)
            }
            CupertinoSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = CupertinoTheme.shapes.extraLarge,
                color = CupertinoTheme.colorScheme.secondarySystemBackground,
            ) {
                Box {
                    Box(Modifier.matchParentSize().layerBackdrop(backdrop)
                        .background(CupertinoTheme.colorScheme.secondarySystemBackground)) {
                        if (LocalDemoPhoto.current) {
                            Image(painterResource(Res.drawable.catalog_landscape), contentDescription = null,
                                modifier = Modifier.matchParentSize(), contentScale = ContentScale.Crop)
                        }
                    }
                    CompositionLocalProvider(LocalDemoBackdrop provides backdrop) {
                        Column(Modifier.fillMaxWidth().heightIn(min = 220.dp).padding(horizontal = 12.dp, vertical = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            content = content)
                    }
                }
            }
        }
    } else {
        val color = CupertinoTheme.colorScheme.secondarySystemGroupedBackground
        val backdrop = rememberLayerBackdrop { drawRect(color) }
        CupertinoSection(
            style = SectionStyle.InsetGrouped,
            contentPadding = PaddingValues(horizontal = 20.dp),
            title = { CupertinoText(text = title) },
            caption = description?.let { { CupertinoText(text = it) } },
        ) {
            Box {
                Box(Modifier.matchParentSize().layerBackdrop(backdrop).background(color))
                CompositionLocalProvider(LocalDemoBackdrop provides backdrop) {
                    Column(Modifier.fillMaxWidth().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp), content = content)
                }
            }
        }
    }
}

@Composable
internal fun DemoToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().heightIn(min = 44.dp), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        CupertinoText(text = label, modifier = Modifier.weight(1f), style = CupertinoTheme.typography.subhead)
        CupertinoSwitch(checked = checked, onCheckedChange = onCheckedChange,
            backdrop = LocalDemoBackdrop.current ?: rememberDefaultBackdrop(),
            modifier = Modifier.semantics { contentDescription = label })
    }
}

@Composable
internal fun <T> DemoChoice(label: String, values: List<T>, selected: T, onSelect: (T) -> Unit, title: (T) -> String) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Row(Modifier.fillMaxWidth().heightIn(min = 44.dp), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        CupertinoText(text = label, modifier = Modifier.weight(1f), style = CupertinoTheme.typography.subhead)
        CupertinoPopupButton(expanded = expanded, onExpandedChange = { expanded = it },
            title = { CupertinoText(text = title(selected)) }) {
            values.forEach { value ->
                MenuPickerAction(isSelected = value == selected, onClick = { onSelect(value); expanded = false },
                    title = { CupertinoText(text = title(value)) })
            }
        }
    }
}

@Composable
internal fun DemoResult(value: String) {
    CupertinoSurface(
        shape = CupertinoTheme.shapes.small,
        color = CupertinoTheme.colorScheme.systemBackground.copy(alpha = .9f),
        contentColor = CupertinoTheme.colorScheme.secondaryLabel,
    ) {
        CupertinoText(text = value, modifier = Modifier.testTag("demo-result").padding(horizontal = 10.dp, vertical = 6.dp),
            style = CupertinoTheme.typography.footnote)
    }
}
