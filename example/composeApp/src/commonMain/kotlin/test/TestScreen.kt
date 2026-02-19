package test

import IsIos
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Accessibility
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import zone.ien.hig.CupertinoIcon
import zone.ien.hig.CupertinoLargeFloatingActionButton
import zone.ien.hig.CupertinoLiquidButton
import zone.ien.hig.CupertinoMediumFloatingActionButton
import zone.ien.hig.CupertinoNavigationTitle
import zone.ien.hig.CupertinoScaffold
import zone.ien.hig.CupertinoSmallFloatingActionButton
import zone.ien.hig.CupertinoText
import zone.ien.hig.CupertinoTopAppBar
import zone.ien.hig.ExperimentalCupertinoApi
import zone.ien.hig.adaptive.icons.AdaptiveIcons
import zone.ien.hig.icons.CupertinoIcons
import zone.ien.hig.icons.outlined.MoonStars
import zone.ien.hig.icons.outlined.SunMax

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun TestScreen(
    modifier: Modifier = Modifier
) {
    val backdrop = rememberLayerBackdrop()
    val scrollState = rememberScrollState()
    val density = LocalDensity.current


    val isTransparent by remember(scrollState, density) {
        derivedStateOf {
            // top bar is collapsing only on mobile
            if (IsIos) {
                scrollState.value < density.run { 20.dp.toPx() }
            } else {
                !scrollState.canScrollBackward
            }

        }
    }
//    val isTransparent = true

    LaunchedEffect(isTransparent) {
        println("TopBar isTransparent $isTransparent")
    }

//    /*
    CupertinoScaffold(
        topBar = {
            CupertinoTopAppBar(
                isTranslucent = isTransparent,
                isTransparent = isTransparent,
                title = {
                    CupertinoText("Cupertino")
                },
                isCenterAligned = false
            )
        },
        floatingActionButton = {
//            CupertinoSmallFloatingActionButton(
//            CupertinoMediumFloatingActionButton(
//            CupertinoLargeFloatingActionButton(
            CupertinoLargeFloatingActionButton(
                onClick = {},
                backdrop = backdrop
            ) {
                Icon(
                    painter = AdaptiveIcons.painter(
                        material = { Icons.Rounded.Accessibility },
                        cupertino = { "plus" }
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        modifier = modifier
        /*
        AdaptiveTopAppBarScaffold(
            title = {
                Text(
                    text = "Title"
                )
            },
            topBarAdaptation = {
                cupertino {
                    this.backdrop = backdrop
                }
            }

         */
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(it)
                .background(Color.Red.copy(0.7f))
//                .layerBackdrop(backdrop)
        ) {
            CupertinoNavigationTitle {
                Text(
                    text = "Cupertino",
                    modifier = Modifier.background(Color.Green)
                )
            }

            Text(
                text = "Hello World",
                modifier = Modifier
                    .background(Color.Blue)
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1000.dp)
            )
        }
    }
//     */
    /*
    CupertinoScaffold(
        topBar = {
            CupertinoLiquidTopAppBar(
                title = {},
                navigationIcon = {},
                backdrop = backdrop,
//                isTranslucent = true,
//                isTransparent = true,

            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
                .layerBackdrop(backdrop)
        ) {
            Text(
                text = "Hello World"
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1000.dp)
            )
        }
        /*
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

         */
    }

     */
}

@Preview(showBackground = true)
@Composable
private fun ScreenPreview() {
    TestScreen()
}