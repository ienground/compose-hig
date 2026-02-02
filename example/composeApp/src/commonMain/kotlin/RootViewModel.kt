import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import zone.ien.hig.theme.CupertinoColors
import zone.ien.hig.theme.systemBlue

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