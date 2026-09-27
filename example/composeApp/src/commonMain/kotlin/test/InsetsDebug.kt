package test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp

@Composable
fun InsetsDebug(modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val size = LocalWindowInfo.current.containerSize

    fun values(insets: WindowInsets): String = with(insets) {
        "L ${getLeft(density, layoutDirection)}  T ${getTop(density)}  " +
            "R ${getRight(density, layoutDirection)}  B ${getBottom(density)}"
    }

    Column(modifier.background(Color.Black).padding(12.dp)) {
        Text("window ${size.width} × ${size.height} px", color = Color.White)
        Text("safeDrawing  ${values(WindowInsets.safeDrawing)}", color = Color.White)
        Text("safeContent  ${values(WindowInsets.safeContent)}", color = Color.White)
        Text("statusBars   ${values(WindowInsets.statusBars)}", color = Color.White)
        Text("navigationBars ${values(WindowInsets.navigationBars)}", color = Color.White)
        Text("systemBars   ${values(WindowInsets.systemBars)}", color = Color.White)
    }
}
