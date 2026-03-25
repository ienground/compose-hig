package test

import androidx.compose.foundation.Image
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import zone.ien.hig.utils.rememberDefaultBackdrop
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import zone.ien.hig.CupertinoDropdownMenu
import zone.ien.hig.CupertinoIcon
import zone.ien.hig.CupertinoLiquidButton
import zone.ien.hig.CupertinoLiquidButtonDefaults
import zone.ien.hig.CupertinoLiquidIconButton
import zone.ien.hig.CupertinoNavigationTitle
import zone.ien.hig.CupertinoScaffold
import zone.ien.hig.CupertinoText
import zone.ien.hig.CupertinoTopAppBar
import zone.ien.hig.ExperimentalCupertinoApi
import zone.ien.hig.MenuAction
import zone.ien.hig.MenuSection
import zone.ien.hig.adaptive.AdaptiveSwitch
import zone.ien.hig.adaptive.ExperimentalAdaptiveApi
import zone.ien.hig.composeapp.generated.resources.Res
import zone.ien.hig.composeapp.generated.resources.img_calib_test2
import zone.ien.hig.icons.CupertinoIcons
import zone.ien.hig.icons.outlined.CheckmarkCircle
import zone.ien.hig.icons.outlined.ChevronBackward
import zone.ien.hig.icons.outlined.PersonCropCircle
import zone.ien.hig.icons.outlined.Pin
import zone.ien.hig.icons.outlined.SunMax

@OptIn(ExperimentalCupertinoApi::class, ExperimentalAdaptiveApi::class)
@Composable
fun TestScreen(
    modifier: Modifier = Modifier
) {
    val backdrop = rememberDefaultBackdrop()
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }
    var enabled by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    CupertinoScaffold(
        snackbarHost = { SnackbarHost(snackbarState) },
        topBar = {
            CupertinoTopAppBar(
//                isBackgroundGradient = true,
                title = {
                    CupertinoText("Cupertino")
                },
                subtitle = {
                    CupertinoText("sub title")
                },
                navigationIcon = {
                    Box {
                        CupertinoLiquidIconButton(
                            onClick = { expanded = true },
                            backdrop = backdrop
                        ) {
                            CupertinoIcon(
                                imageVector = CupertinoIcons.Default.ChevronBackward,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        CupertinoDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            backdrop = rememberLayerBackdrop()
                        ) {
                            MenuSection(
                                title = { Text(text = "Title") }
                            ) {
                                repeat(4) {
                                    MenuAction(
                                        onClick = {}
                                    ) {
                                        Text(text = "Action")
                                    }
                                }
                            }
                        }
                    }
                },
                actions = {
                    CupertinoLiquidButton(
                        onClick = {},
                        backdrop = backdrop
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(2) {
                                Box(
                                    modifier = Modifier.clickable(
                                        interactionSource = null,
                                        indication = null,
                                        onClick = {}
                                    )
                                ) {
                                    CupertinoIcon(
                                        imageVector = CupertinoIcons.Default.SunMax,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                },
                backdrop = backdrop,
                isCenterAligned = true
            )
        },
        hasNavigationTitle = true,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .layerBackdrop(backdrop)
                .verticalScroll(scrollState)
                .padding(it)
        ) {
            CupertinoNavigationTitle(
                subtitle = {
                    Text(
                        text = "87개의 메모"
                    )
                }
            ) {
                Text(
                    text = "메모",
                )
            }

            Image(
                painter = painterResource(Res.drawable.img_calib_test2),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
            )

            Row {
                AdaptiveSwitch(
                    checked = enabled,
                    onCheckedChange = { enabled = it }
                )
                CupertinoLiquidButton(
                    onClick = { coroutineScope.launch { snackbarState.showSnackbar("clicked") } },
                    backdrop = rememberDefaultBackdrop(),
                    colors = CupertinoLiquidButtonDefaults.glassProminentButtonColors(),
                    enabled = enabled
                ) {
                    Text(text = "button")
                }
                CupertinoLiquidButton(
                    onClick = { coroutineScope.launch { snackbarState.showSnackbar("clicked") } },
                    backdrop = rememberDefaultBackdrop(),
                    colors = CupertinoLiquidButtonDefaults.glassProminentButtonColors(),
                    enabled = !enabled
                ) {
                    Text(text = "button")
                }
            }

            var expanded by remember { mutableStateOf(false) }

            Box {
                CupertinoLiquidButton(
                    onClick = { expanded = true },
                    backdrop = rememberDefaultBackdrop(),
                    isInteractive = false,
                    colors = CupertinoLiquidButtonDefaults.glassProminentButtonColors(),
                ) {
                    Text(text = "Open Menu")
                }

                // drawBackdrop is used inside Popup internally
                CupertinoDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    backdrop = rememberDefaultBackdrop()
                ) {
                    MenuSection(
//                        title = {
//                            Text("Menu")
//                        }
                    ) {
                        MenuAction(
                            onClick = { expanded = false },
                            leadingIcon = {
                                CupertinoIcon(
                                    imageVector = CupertinoIcons.Default.CheckmarkCircle,
                                    contentDescription = null
                                )
                            }
                        ) {
                            CupertinoText("메시지 선택")
                        }
                        MenuAction(
                            onClick = { expanded = false },
                            leadingIcon = {
                                CupertinoIcon(
                                    imageVector = CupertinoIcons.Default.Pin,
                                    contentDescription = null
                                )
                            }
                        ) {
                            CupertinoText("고정 편집")
                        }
                        MenuAction(
                            onClick = { expanded = false },
                            leadingIcon = {
                                CupertinoIcon(
                                    imageVector = CupertinoIcons.Default.PersonCropCircle,
                                    contentDescription = null
                                )
                            }
                        ) {
                            CupertinoText("이름 및 사진 설정")
                        }
                    }
//                    MenuDivider()
//                    MenuSection(
////                        title = {
////                            Text("Menu")
////                        }
//                    ) {
//                        MenuAction(
//                            onClick = {
//                                expanded = false
//                            },
//                            icon = {
//                                CupertinoIcon(
//                                    imageVector = CupertinoIcons.Default.SquareAndArrowUp,
//                                    contentDescription = null
//                                )
//                            }
//                        ) {
//                            CupertinoText("Share")
//                        }
//                        MenuAction(
//                            enabled = false,
//                            onClick = {
//                                expanded = false
//                            },
//                            icon = {
//                                CupertinoIcon(
//                                    imageVector = CupertinoIcons.Default.Bookmark,
//                                    contentDescription = null
//                                )
//                            }
//                        ) {
//                            CupertinoText("Add to Favorites")
//                        }
//                    }
                }
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
                    .background(Color.White)
                    .fillMaxWidth()
                    .height(1000.dp)

            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1000.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScreenPreview() {
    TestScreen()
}