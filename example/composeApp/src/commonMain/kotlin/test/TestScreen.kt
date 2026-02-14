package test

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import zone.ien.hig.CupertinoLiquidButton
import zone.ien.hig.CupertinoLiquidTopAppBar
import zone.ien.hig.CupertinoScaffold
import zone.ien.hig.ExperimentalCupertinoApi
import zone.ien.hig.composeapp.generated.resources.Res
import zone.ien.hig.composeapp.generated.resources.img_calib_test

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun TestScreen(
    modifier: Modifier = Modifier
) {
    val backdrop = rememberLayerBackdrop()
    CupertinoScaffold(
        topBar = {
            CupertinoLiquidTopAppBar(
                title = {},
                navigationIcon = {},
                backdrop = backdrop,
                isTranslucent = true,
                isTransparent = true,

            )
        },
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.padding(it)
        ) {
            var offsetX by remember { mutableStateOf(0.dp) }
            var offsetY by remember { mutableStateOf(0.dp) }
            val density = LocalDensity.current

            Image(
                painter = painterResource(Res.drawable.img_calib_test),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .layerBackdrop(backdrop)
                    .offset(offsetX, offsetY)
                    .pointerInput(Unit) {
                        detectTransformGestures { centroid, pan, gestureZoom, gestureRotate ->
                            val targetX = pan.x
                            val targetY = pan.y

                            offsetX = with (density) { targetX.toDp() } + offsetX
                            offsetY = with (density) { targetY.toDp() } + offsetY
                        }
                    }
                    .fillMaxSize()
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CupertinoLiquidButton(
                    onClick = {},
                    backdrop = backdrop
                ) {
                    Text(text = "Hello")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScreenPreview() {
    TestScreen()
}