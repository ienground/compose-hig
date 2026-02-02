import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation3.runtime.rememberNavBackStack
import navigation.RootNavigationGraph
import navigation.RootRoute
import navigation.rootConfig
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import zone.ien.hig.adaptive.ExperimentalAdaptiveApi
import zone.ien.hig.adaptive.Theme

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun App() {
    val backStack = rememberNavBackStack(rootConfig, RootRoute.Cupertino)

    val viewModel: RootViewModel = koinViewModel()

    val theme by derivedStateOf {
        if (viewModel.uiState.item.isMaterial) Theme.Material3 else Theme.Cupertino
    }
    val (lightAccent, darkAccent) = viewModel.uiState.item.accentColors
    val isDark = viewModel.uiState.item.isDark
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
            RootNavigationGraph(
                backStack = backStack,
                viewModel = viewModel
            )
        }
    }
}