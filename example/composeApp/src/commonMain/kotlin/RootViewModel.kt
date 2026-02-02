import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import zone.ien.hig.theme.CupertinoColors
import zone.ien.hig.theme.systemBlue

fun koinInitialize() {
    startKoin {
        modules(
            listOf(
                module {
                    viewModel { RootViewModel() }
                }
            )
        )
    }
}

class RootViewModel: ViewModel() {
    var uiState by mutableStateOf(RootUiState())
        private set

    fun updateUiState(item: RootDetails) {
        uiState = RootUiState(item = item)
    }
}

data class RootUiState(
    val item: RootDetails = RootDetails()
)

data class RootDetails(
    val accentColors: Pair<Color, Color> = CupertinoColors.systemBlue(false) to CupertinoColors.systemBlue(true),
    val invertLayoutDirection: Boolean = false,
    val isDark: Boolean = false,
    val isMaterial: Boolean = false
)