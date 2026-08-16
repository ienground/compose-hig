import adaptive.AdaptiveWidgetsScreen
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
}

val rootConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(RootRoute.Cupertino::class, RootRoute.Cupertino.serializer())
            subclass(RootRoute.Adaptive::class, RootRoute.Adaptive.serializer())
            subclass(RootRoute.Icons::class, RootRoute.Icons.serializer())
            subclass(RootRoute.Sections::class, RootRoute.Sections.serializer())
        }
    }
}

@Composable
fun RootNavigationGraph(
    modifier: Modifier = Modifier,
    backStack: NavBackStack<NavKey>,
    viewModel: RootViewModel
) {
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator() // ViewModel lifecycle guarantee
        ),
        entryProvider = entryProvider {
            entry<RootRoute.Cupertino> {
                CupertinoWidgetsScreen(
                    uiState = viewModel.uiState,
                    onItemValueChanged = viewModel::updateUiState,
                    onNavigate = { backStack.add(it) }
                )
            }
            entry<RootRoute.Adaptive> {
                AdaptiveWidgetsScreen(
                    uiState = viewModel.uiState,
                    onItemValueChanged = viewModel::updateUiState,
                    navigateBack = { backStack.removeAt(backStack.lastIndex) }
                )
            }
            entry<RootRoute.Icons> {
                IconsScreen(
                    navigateBack = { backStack.removeAt(backStack.lastIndex) }
                )
            }
            entry<RootRoute.Sections> {
                SectionsScreen(
                    navigateBack = { backStack.removeAt(backStack.lastIndex) }
                )
            }
        }
    )
}