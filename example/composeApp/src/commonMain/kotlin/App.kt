import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.rememberNavBackStack
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.materialkolor.dynamicColorScheme
import org.koin.compose.viewmodel.koinViewModel
import zone.ien.hig.CupertinoButtonDefaults
import zone.ien.hig.CupertinoIcon
import zone.ien.hig.CupertinoIconButton
import zone.ien.hig.ExperimentalCupertinoApi
import zone.ien.hig.adaptive.AdaptiveTheme
import zone.ien.hig.adaptive.CupertinoThemeSpec
import zone.ien.hig.adaptive.ExperimentalAdaptiveApi
import zone.ien.hig.adaptive.MaterialThemeSpec
import zone.ien.hig.adaptive.Theme
import zone.ien.hig.adaptive.icons.AdaptiveIcons
import zone.ien.hig.adaptive.icons.Menu
import zone.ien.hig.adaptive.icons.Person
import zone.ien.hig.adaptive.icons.Settings
import zone.ien.hig.theme.Shapes
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.theme.darkColorScheme
import zone.ien.hig.theme.lightColorScheme

expect val IsIos: Boolean

enum class AppRenderStage {
    EmptyBox,
    ThemeWithEmptyBox,
    RootScreen,
}

data class RootSystemBarAction(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
)

data class RootSystemBarConfiguration(
    val title: String,
    val leadingActions: List<RootSystemBarAction>,
    val trailingActions: List<RootSystemBarAction>,
)

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun App(
    renderStage: AppRenderStage = AppRenderStage.RootScreen,
    composeSystemBars: Boolean = false,
    rootViewModel: RootViewModel? = null,
) {
    if (renderStage == AppRenderStage.EmptyBox) {
        Box(Modifier.fillMaxSize())
        return
    }

    val backStack = rememberNavBackStack(rootConfig, RootRoute.Cupertino)

    val viewModel: RootViewModel = rootViewModel ?: koinViewModel()
    var systemBarOverride by remember { mutableStateOf<RootSystemBarConfiguration?>(null) }
    var selectedSystemTab by rememberSaveable { mutableIntStateOf(0) }

    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    val rightSafeInset = WindowInsets.safeDrawing.getRight(density, LayoutDirection.Ltr)
    val showSystemBarRail = composeSystemBars && IsIos &&
        windowInfo.containerSize.width > windowInfo.containerSize.height && rightSafeInset > 0
    val rightSafeInsetDp = with(density) { rightSafeInset.toDp() }
    val currentRoute = backStack.lastOrNull() as? RootRoute

    LaunchedEffect(currentRoute) {
        systemBarOverride = null
    }

    val theme by derivedStateOf {
        if (viewModel.uiState.item.isMaterial) Theme.Material3 else Theme.Cupertino
    }
    val (lightAccent, darkAccent) = viewModel.uiState.item.accentColors
    val isDark = isSystemInDarkTheme()
//    val isDark = viewModel.uiState.item.isDark
    val direction = LocalLayoutDirection.current

    val directionState by remember {
        derivedStateOf {
            if (viewModel.uiState.item.invertLayoutDirection) {
                if (direction == LayoutDirection.Rtl)
                    LayoutDirection.Ltr else
                    LayoutDirection.Rtl
            } else {
                direction
            }
        }
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides directionState
    ) {

        GeneratedAdaptiveTheme(
            target = theme,
            primaryColor = if (isDark)
                lightAccent else darkAccent,
            useDarkTheme = isDark
        ) {
            when (renderStage) {
                AppRenderStage.EmptyBox -> Unit
                AppRenderStage.ThemeWithEmptyBox -> Box(Modifier.fillMaxSize())
                AppRenderStage.RootScreen -> {
                    val backdropColor = CupertinoTheme.colorScheme.secondarySystemBackground
                    val systemBarBackdrop = rememberLayerBackdrop(
                        onDraw = remember(backdropColor) {
                            { drawRect(backdropColor); drawContent() }
                        },
                    )
                    val systemBarConfiguration = systemBarOverride ?: RootSystemBarConfiguration(
                        title = when (currentRoute) {
                            RootRoute.Adaptive -> "Adaptive"
                            RootRoute.Icons -> "Icons"
                            RootRoute.Sections -> "Sections"
                            RootRoute.Cupertino, null -> "HIG"
                        },
                        leadingActions = if (backStack.size > 1) {
                            listOf(
                                RootSystemBarAction(
                                    title = "뒤로",
                                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                                    onClick = { backStack.removeAt(backStack.lastIndex) },
                                ),
                            )
                        } else {
                            emptyList()
                        },
                        trailingActions = listOf(
                            RootSystemBarAction(
                                title = "방향",
                                icon = Icons.Default.SwapHoriz,
                                onClick = {
                                    val item = viewModel.uiState.item
                                    viewModel.updateUiState(item.copy(invertLayoutDirection = !item.invertLayoutDirection))
                                },
                            ),
                            RootSystemBarAction(
                                title = "전환",
                                icon = Icons.Default.Refresh,
                                onClick = {
                                    val item = viewModel.uiState.item
                                    viewModel.updateUiState(
                                        when (currentRoute) {
                                            RootRoute.Icons -> item.copy(isOutlined = !item.isOutlined)
                                            RootRoute.Sections -> item.copy(isLazySections = !item.isLazySections)
                                            else -> item.copy(isMaterial = !item.isMaterial)
                                        },
                                    )
                                },
                            ),
                        ),
                    )

                    Box(Modifier.fillMaxSize()) {
                        RootNavigationGraph(
                            modifier = Modifier
                                .fillMaxSize()
                                .layerBackdrop(systemBarBackdrop)
                                .absolutePadding(
                                    right = if (showSystemBarRail) rightSafeInsetDp + SystemBarRailWidth else 0.dp,
                                ),
                            backStack = backStack,
                            viewModel = viewModel,
                            systemBarRailVisible = showSystemBarRail,
                            onSystemBarOverrideChanged = { systemBarOverride = it },
                        )

                        if (showSystemBarRail) {
                            RootSystemBarRail(
                                configuration = systemBarConfiguration,
                                backdrop = systemBarBackdrop,
                                selectedTab = selectedSystemTab,
                                onTabSelected = { index ->
                                    selectedSystemTab = index
                                    val destination = when (index) {
                                        0 -> RootRoute.Cupertino
                                        1 -> RootRoute.Adaptive
                                        else -> RootRoute.Sections
                                    }
                                    while (backStack.size > 1) {
                                        backStack.removeAt(backStack.lastIndex)
                                    }
                                    backStack[0] = destination
                                },
                                modifier = Modifier.align(AbsoluteAlignment.CenterRight),
                            )
                        }
                    }
                }
            }
        }
    }
}

private val SystemBarRailWidth = 56.dp

@OptIn(ExperimentalCupertinoApi::class)
@Composable
private fun RootSystemBarRail(
    configuration: RootSystemBarConfiguration,
    backdrop: LayerBackdrop,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val safeInsets = WindowInsets.safeDrawing.only(
        WindowInsetsSides.Right + WindowInsetsSides.Top + WindowInsetsSides.Bottom,
    )
    val tabs = listOf(
        "프로필" to AdaptiveIcons.Outlined.Person,
        "메뉴" to AdaptiveIcons.Outlined.Menu,
        "설정" to AdaptiveIcons.Outlined.Settings,
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(safeInsets),
    ) {
        val capsuleColor = CupertinoTheme.colorScheme.secondarySystemBackground.copy(alpha = 0.82f)
        val capsuleBorder = CupertinoTheme.colorScheme.separator.copy(alpha = 0.25f)
        val capsuleShape = RoundedCornerShape(32.dp)
        val actionTop = maxHeight * 0.12f

        Column(
            modifier = Modifier
                .align(AbsoluteAlignment.TopRight)
                .padding(top = actionTop)
                .absolutePadding(right = 4.dp)
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { capsuleShape },
                    effects = {
                        vibrancy()
                        blur(2.dp.toPx())
                        lens(24.dp.toPx(), 24.dp.toPx())
                    },
                    onDrawSurface = { drawRect(capsuleColor) },
                )
                .clip(capsuleShape)
                .border(1.dp, capsuleBorder, capsuleShape)
                .padding(4.dp),
        ) {
            (configuration.leadingActions + configuration.trailingActions).forEach { action ->
                CupertinoIconButton(
                    onClick = action.onClick,
                    colors = CupertinoButtonDefaults.plainButtonColors(),
                ) {
                    CupertinoIcon(
                        imageVector = action.icon,
                        contentDescription = action.title,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(AbsoluteAlignment.BottomRight)
                .padding(bottom = 8.dp)
                .absolutePadding(right = 4.dp)
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { capsuleShape },
                    effects = {
                        vibrancy()
                        blur(2.dp.toPx())
                        lens(24.dp.toPx(), 24.dp.toPx())
                    },
                    onDrawSurface = { drawRect(capsuleColor) },
                )
                .clip(capsuleShape)
                .border(1.dp, capsuleBorder, capsuleShape)
                .padding(4.dp),
        ) {
            tabs.forEachIndexed { index, (title, icon) ->
                CupertinoIconButton(
                    onClick = { onTabSelected(index) },
                    colors = if (selectedTab == index) {
                        CupertinoButtonDefaults.filledButtonColors()
                    } else {
                        CupertinoButtonDefaults.plainButtonColors()
                    },
                ) {
                    CupertinoIcon(
                        imageVector = icon,
                        contentDescription = title,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }
    }
}

@ExperimentalAdaptiveApi
@Composable
fun GeneratedAdaptiveTheme(
    target: Theme,
    primaryColor: Color,
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    shapes: zone.ien.hig.adaptive.Shapes = zone.ien.hig.adaptive.Shapes(),
    content: @Composable () -> Unit
) {
    AdaptiveTheme(
        target = target,
        material = MaterialThemeSpec.Default(
            colorScheme = dynamicColorScheme(
                seedColor = primaryColor,
                isDark = useDarkTheme
            ),
            shapes = androidx.compose.material3.Shapes(
                extraSmall = shapes.extraSmall,
                small = shapes.small,
                medium = shapes.medium,
                large = shapes.large,
                extraLarge = shapes.extraLarge
            )
        ),
        cupertino = CupertinoThemeSpec.Default(
            colorScheme = if (useDarkTheme)
                darkColorScheme(accent = primaryColor)
            else lightColorScheme(accent = primaryColor),
            shapes = Shapes(
                extraSmall = shapes.higExtraSmall,
                small = shapes.higSmall,
                medium = shapes.higMedium,
                large = shapes.higLarge,
                extraLarge = shapes.higExtraLarge
            )
        ),
        content = content
    )
}
