package zone.ien.hig

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.window.PopupProperties
import com.kyant.backdrop.Backdrop

@OptIn(markerClass = [ExperimentalComposeUiApi::class])
@ExperimentalCupertinoApi
@Composable
actual fun CupertinoDropdownMenuNative(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier,
    offset: DpOffset,
    paddingValues: PaddingValues,
    containerColor: Color,
    width: Dp,
    scrollState: ScrollState,
    properties: PopupProperties,
    backdrop: Backdrop,
    items: List<CupertinoMenuItemData>,
    sections: List<CupertinoMenuSectionData>
) {
    CupertinoDropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        offset = offset,
        paddingValues = paddingValues,
        containerColor = containerColor,
        width = width,
        scrollState = scrollState,
        properties = properties,
        backdrop = backdrop
    ) {
        items.fastForEach {
            MenuAction(
                onClick = it.onClick,
                enabled = !it.isDisabled,
                icon = {
                    it.icon?.let { icon ->
                        CupertinoIcon(
                            painter = icon,
                            contentDescription = null
                        )
                    }
                },
                title = {
                    CupertinoText(text = it.title)
                }

            )
        }
        sections.fastForEach { section ->
            MenuSection(
                title = { CupertinoText(text = section.title) }
            ) {
                items.fastForEach {
                    MenuAction(
                        onClick = it.onClick,
                        enabled = !it.isDisabled,
                        icon = {
                            it.icon?.let { icon ->
                                CupertinoIcon(
                                    painter = icon,
                                    contentDescription = null
                                )
                            }
                        },
                        title = {
                            CupertinoText(text = it.title)
                        }

                    )
                }
            }
        }
    }
}