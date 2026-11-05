@file:OptIn(zone.ien.hig.ExperimentalCupertinoApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)

package cupertino

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import zone.ien.hig.utils.rememberDefaultBackdrop
import zone.ien.hig.CupertinoBottomAppBar
import zone.ien.hig.CupertinoBottomAppBarAction
import zone.ien.hig.CupertinoButton
import zone.ien.hig.CupertinoHorizontalDivider
import zone.ien.hig.CupertinoIcon
import zone.ien.hig.CupertinoLiquidIconButton
import zone.ien.hig.CupertinoNavigateBackLiquidButton
import zone.ien.hig.CupertinoNavigationBar
import zone.ien.hig.CupertinoNavigationBarItem
import zone.ien.hig.CupertinoNavigationBarItemData
import zone.ien.hig.CupertinoNavigationBarNative
import zone.ien.hig.CupertinoNavigationTitle
import zone.ien.hig.CupertinoSheetState
import zone.ien.hig.CupertinoScaffold
import zone.ien.hig.LocalCupertinoRightSystemArea
import zone.ien.hig.CupertinoSurface
import zone.ien.hig.CupertinoSwipeBox
import zone.ien.hig.CupertinoText
import zone.ien.hig.CupertinoTopAppBar
import zone.ien.hig.CupertinoVerticalDivider
import zone.ien.hig.icons.CupertinoIcons
import zone.ien.hig.icons.filled.Gearshape
import zone.ien.hig.icons.filled.Trash
import zone.ien.hig.icons.outlined.Heart
import zone.ien.hig.icons.outlined.Pencil
import zone.ien.hig.icons.outlined.RectangleStack
import zone.ien.hig.swipebox.CupertinoSwipeBoxItem
import zone.ien.hig.swipebox.rememberCupertinoSwipeBoxState
import zone.ien.hig.theme.CupertinoColors
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.theme.systemBlue
import zone.ien.hig.theme.systemRed

@Composable
internal fun BottomSheetDemo(state: CupertinoSheetState, onShow: () -> Unit) {
    DemoSection("Preview", "Drag the handle to resize the sheet or swipe down to dismiss.") {
        CupertinoButton(onClick = onShow) { CupertinoText(text = "Show bottom sheet") }
        DemoResult("State: ${state.currentValue} · Target: ${state.targetValue}")
    }
    DemoSection("Behavior") {
        CupertinoText(text = "PresentationDetent.Medium / Large")
        CupertinoText(text = "The sheet contains 100 scrollable items.")
        CupertinoText(text = "Tap Done, tap outside or swipe down to dismiss.")
    }
}

@Composable
internal fun SwipeBoxDemo(scrollState: ScrollableState) {
    var fullSwipe by rememberSaveable { mutableStateOf(true) }
    var twoWay by rememberSaveable { mutableStateOf(true) }
    var deleted by rememberSaveable { mutableStateOf(false) }
    var favorite by rememberSaveable { mutableStateOf(false) }
    var result by rememberSaveable { mutableStateOf("Swipe the row to reveal actions") }
    var revision by rememberSaveable { mutableIntStateOf(0) }
    DemoSection("Preview", "Swipe left to delete or right to favorite.") {
        if (!deleted) key(revision, twoWay, fullSwipe) {
            val state = rememberCupertinoSwipeBoxState(scrollableState = scrollState)
            val toggleFavorite = { favorite = !favorite; result = if (favorite) "Added to favorites" else "Removed from favorites" }
            val delete = { deleted = true; result = "Item deleted" }
            CupertinoSwipeBox(
                state = state, modifier = Modifier.fillMaxWidth(), height = 72.dp,
                startToEndFullSwipeEnabled = fullSwipe, endToStartFullSwipeEnabled = fullSwipe,
                actionItemBuilder = {
                    if (twoWay) start(onClick = toggleFavorite) {
                        CupertinoSwipeBoxItem(color = CupertinoColors.systemBlue, onClick = toggleFavorite,
                            icon = CupertinoIcons.Default.Heart, onClickLabel = "Toggle favorite")
                    }
                    end(onClick = delete) {
                        CupertinoSwipeBoxItem(color = CupertinoColors.systemRed, onClick = delete,
                            icon = CupertinoIcons.Filled.Trash, onClickLabel = "Item deleted")
                    }
                },
            ) {
                Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
                    CupertinoText(text = if (favorite) "Favorite photo" else "Travel photo")
                }
            }
        }
        DemoResult(result)
        CupertinoButton(onClick = { deleted = false; favorite = false; revision++; result = "Item restored" }) {
            CupertinoText(text = "Restore item")
        }
    }
    DemoSection("Properties") {
        DemoToggle("Full swipe action", fullSwipe) { fullSwipe = it }
        DemoToggle("Actions on both sides", twoWay) { twoWay = it }
    }
}

@Composable
internal fun NavigationBarDemo() {
    var selected by rememberSaveable { mutableIntStateOf(0) }
    var native by rememberSaveable { mutableStateOf(false) }
    val items = listOf("Photos" to CupertinoIcons.Default.RectangleStack,
        "Favorites" to CupertinoIcons.Default.Heart, "Settings" to CupertinoIcons.Filled.Gearshape)
    DemoSection("Preview") {
        CupertinoText(text = "${items[selected].first}", style = CupertinoTheme.typography.title2)
        Box(Modifier.fillMaxWidth().height(100.dp).testTag("preview-navigation"), contentAlignment = Alignment.Center) {
            if (native) {
                CupertinoNavigationBarNative(
                    selectedTabIndex = { selected }, onTabSelected = { selected = it }, backdrop = LocalDemoBackdrop.current ?: rememberDefaultBackdrop(),
                    windowInsets = WindowInsets(0, 0, 0, 0),
                    items = items.mapIndexed { index, (title, icon) ->
                        CupertinoNavigationBarItemData(onClick = { selected = index }, icon = rememberVectorPainter(icon), label = title)
                    },
                )
            } else {
                CupertinoNavigationBar(
                    selectedTabIndex = { selected }, onTabSelected = { selected = it }, tabsCount = items.size,
                    backdrop = LocalDemoBackdrop.current ?: rememberDefaultBackdrop(),
                    windowInsets = WindowInsets(0, 0, 0, 0),
                ) {
                    items.forEachIndexed { index, (title, icon) ->
                        CupertinoNavigationBarItem(
                            onClick = { selected = index },
                            icon = { CupertinoIcon(imageVector = icon, contentDescription = title) },
                            label = { CupertinoText(text = title) },
                        )
                    }
                }
            }
        }
        DemoResult("selectedTabIndex = $selected · ${items[selected].first}")
    }
    DemoSection("Properties") {
        DemoToggle("Native implementation", native) { native = it }
        NativeImplementationNote(native)
    }
}

@Composable
internal fun TopBarDemo() {
    var centered by rememberSaveable { mutableStateOf(true) }
    var subtitle by rememberSaveable { mutableStateOf(true) }
    var result by rememberSaveable { mutableStateOf("Try the toolbar actions") }
    DemoSection("Preview") {
        CompositionLocalProvider(LocalCupertinoRightSystemArea provides false) {
            CupertinoScaffold(
                modifier = Modifier.fillMaxWidth().height(180.dp),
                containerColor = Color.Transparent,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                topBar = {
                    CupertinoTopAppBar(
                        title = { CupertinoText(text = "Photos") },
                        subtitle = if (subtitle) { { CupertinoText(text = "12 items") } } else null,
                        isCenterAligned = centered, backdrop = LocalDemoBackdrop.current ?: rememberDefaultBackdrop(), windowInsets = WindowInsets(0, 0, 0, 0),
                        navigationIcon = {
                            CupertinoNavigateBackLiquidButton(
                                onClick = { result = "Back pressed" },
                                backdrop = LocalDemoBackdrop.current ?: rememberDefaultBackdrop(),
                                modifier = Modifier.testTag("preview-toolbar-back").semantics { contentDescription = "Back" },
                            )
                        },
                        actions = {
                            CupertinoLiquidIconButton(
                                onClick = { result = "Edit pressed" },
                                backdrop = LocalDemoBackdrop.current ?: rememberDefaultBackdrop(),
                            ) {
                                CupertinoIcon(CupertinoIcons.Default.Pencil, contentDescription = "Edit")
                            }
                        },
                    )
                },
            ) { padding ->
                Column(Modifier.fillMaxSize().padding(padding)) {
                    CupertinoNavigationTitle { CupertinoText(text = "Photos") }
                }
            }
        }
        DemoResult(result)
    }
    DemoSection("Properties") {
        DemoToggle("Centered title", centered) { centered = it }
        DemoToggle("Subtitle", subtitle) { subtitle = it }
    }
    DemoSection("Bottom toolbar") {
        CupertinoBottomAppBar(windowInsets = WindowInsets(0, 0, 0, 0)) {
            CupertinoBottomAppBarAction(onClick = { result = "Share pressed" }) { CupertinoText(text = "Share") }
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) { CupertinoText(text = "12 items") }
            CupertinoBottomAppBarAction(onClick = { result = "Delete pressed" }) { CupertinoText(text = "Delete") }
        }
    }
}

@Composable
internal fun FoundationDemo() {
    DemoSection("Typography", "Use Appearance in the catalog to change text size and theme.") {
        val typography = CupertinoTheme.typography
        listOf("Large Title" to typography.largeTitle, "Title 1" to typography.title1,
            "Title 2" to typography.title2, "Title 3" to typography.title3, "Headline" to typography.headline,
            "Body" to typography.body, "Callout" to typography.callout, "Subhead" to typography.subhead,
            "Footnote" to typography.footnote, "Caption 1" to typography.caption1, "Caption 2" to typography.caption2)
            .forEach { (name, style) -> CupertinoText(text = "$name · Hello", style = style) }
    }
    DemoSection("Surfaces and dividers") {
        val colors = CupertinoTheme.colorScheme
        listOf("systemBackground" to colors.systemBackground,
            "secondarySystemBackground" to colors.secondarySystemBackground,
            "tertiarySystemBackground" to colors.tertiarySystemBackground).forEach { (name, color) ->
            CupertinoSurface(color = color, modifier = Modifier.fillMaxWidth()) {
                CupertinoText(text = name, modifier = Modifier.padding(16.dp))
            }
        }
        CupertinoHorizontalDivider()
        Row(Modifier.height(40.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            CupertinoIcon(imageVector = CupertinoIcons.Default.Heart, contentDescription = "Heart", tint = colors.accent)
            CupertinoVerticalDivider()
            CupertinoText(text = "Secondary label", color = colors.secondaryLabel)
        }
    }
}
