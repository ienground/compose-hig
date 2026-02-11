import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation3.runtime.rememberNavBackStack
import com.materialkolor.dynamicColorScheme
import org.koin.compose.viewmodel.koinViewModel
import zone.ien.hig.adaptive.AdaptiveTheme
import zone.ien.hig.adaptive.CupertinoThemeSpec
import zone.ien.hig.adaptive.ExperimentalAdaptiveApi
import zone.ien.hig.adaptive.MaterialThemeSpec
import zone.ien.hig.adaptive.Theme
import zone.ien.hig.theme.Shapes
import zone.ien.hig.theme.darkColorScheme
import zone.ien.hig.theme.lightColorScheme

expect val IsIos: Boolean

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
