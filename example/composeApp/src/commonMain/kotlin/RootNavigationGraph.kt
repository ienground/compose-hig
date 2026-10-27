import adaptive.AdaptiveWidgetsScreen
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import zone.ien.hig.CupertinoScaffold
import zone.ien.hig.CupertinoText
import zone.ien.hig.ExperimentalCupertinoApi
import zone.ien.hig.adaptive.AdaptiveListDetailPane
import zone.ien.hig.adaptive.adaptivePaneAnimation
import zone.ien.hig.adaptive.isAdaptiveListDetailExpanded
import zone.ien.hig.adaptive.rememberAdaptiveListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import cupertino.CupertinoWidgetsScreen
import icons.IconsScreen
import haptic.HapticFeedbackScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import sections.SectionsScreen

@Serializable
sealed interface RootRoute: NavKey {
    @Serializable data object Cupertino: RootRoute
    @Serializable data object Adaptive: RootRoute
    @Serializable data object Icons: RootRoute
    @Serializable data object Sections: RootRoute
    @Serializable data object HapticFeedback: RootRoute
}

val rootConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(RootRoute.Cupertino::class, RootRoute.Cupertino.serializer())
            subclass(RootRoute.Adaptive::class, RootRoute.Adaptive.serializer())
            subclass(RootRoute.Icons::class, RootRoute.Icons.serializer())
            subclass(RootRoute.Sections::class, RootRoute.Sections.serializer())
            subclass(RootRoute.HapticFeedback::class, RootRoute.HapticFeedback.serializer())
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalCupertinoApi::class)
@Composable
fun RootNavigationGraph(
    modifier: Modifier = Modifier,
    backStack: NavBackStack<NavKey>,
    viewModel: RootViewModel
) {
    val listDetailStrategy = rememberAdaptiveListDetailSceneStrategy<NavKey>(
        shouldHandleSinglePaneLayout = false,
    )
    val paneAnimation = adaptivePaneAnimation()
    NavDisplay(
        sceneStrategies = listOf(listDetailStrategy),
        backStack = backStack,
        modifier = modifier,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator() // ViewModel lifecycle guarantee
        ),
        entryProvider = entryProvider {
            entry<RootRoute.Cupertino>(
                metadata = ListDetailSceneStrategy.listPane(detailPlaceholder = {
                    if (isAdaptiveListDetailExpanded) {
                        AdaptiveListDetailPane(ListDetailPaneScaffoldRole.Detail) {
                            CupertinoScaffold { padding ->
                                Box(Modifier.fillMaxSize().padding(padding).padding(24.dp), contentAlignment = Alignment.Center) {
                                    CupertinoText("메뉴에서 화면을 선택하세요.")
                                }
                            }
                        }
                    }
                }) + ListDetailSceneStrategy.preferredPaneSize(width = 0.4f) + paneAnimation,
            ) {
                AdaptiveListDetailPane(ListDetailPaneScaffoldRole.List) {
                    val isExpanded = isAdaptiveListDetailExpanded
                    CupertinoWidgetsScreen(
                        uiState = viewModel.uiState,
                        onItemValueChanged = viewModel::updateUiState,
                        onNavigate = { route ->
                            if (backStack.lastOrNull() != route) {
                                if (isExpanded) {
                                    while (backStack.lastOrNull() != RootRoute.Cupertino && backStack.size > 1) {
                                        backStack.removeAt(backStack.lastIndex)
                                    }
                                }
                                backStack.add(route)
                            }
                        }
                    )
                }
            }
            entry<RootRoute.Adaptive>(
                metadata = ListDetailSceneStrategy.detailPane() + paneAnimation,
            ) {
                AdaptiveListDetailPane(ListDetailPaneScaffoldRole.Detail) {
                    AdaptiveWidgetsScreen(
                        uiState = viewModel.uiState,
                        onItemValueChanged = viewModel::updateUiState,
                        navigateBack = { backStack.removeAt(backStack.lastIndex) }
                    )
                }
            }
            entry<RootRoute.Icons>(
                metadata = ListDetailSceneStrategy.detailPane() + paneAnimation,
            ) {
                AdaptiveListDetailPane(ListDetailPaneScaffoldRole.Detail) {
                    IconsScreen(
                        navigateBack = { backStack.removeAt(backStack.lastIndex) }
                    )
                }
            }
            entry<RootRoute.Sections>(
                metadata = ListDetailSceneStrategy.detailPane() + paneAnimation,
            ) {
                AdaptiveListDetailPane(ListDetailPaneScaffoldRole.Detail) {
                    SectionsScreen(
                        navigateBack = { backStack.removeAt(backStack.lastIndex) }
                    )
                }
            }
            entry<RootRoute.HapticFeedback>(
                metadata = ListDetailSceneStrategy.detailPane() + paneAnimation,
            ) {
                AdaptiveListDetailPane(ListDetailPaneScaffoldRole.Detail) {
                    HapticFeedbackScreen(
                        navigateBack = { backStack.removeAt(backStack.lastIndex) }
                    )
                }
            }
        }
    )
}
