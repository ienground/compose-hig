@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package haptic

import zone.ien.hig.CupertinoTopAppBarDefaults
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import zone.ien.hig.adaptive.isAdaptiveListDetailExpanded
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import zone.ien.hig.CupertinoHapticFeedback
import zone.ien.hig.CupertinoNavigateBackLiquidButton
import zone.ien.hig.CupertinoScaffold
import zone.ien.hig.CupertinoText
import zone.ien.hig.CupertinoTopAppBar
import zone.ien.hig.ExperimentalCupertinoApi
import zone.ien.hig.InternalCupertinoApi
import zone.ien.hig.cupertinoScrollEdge
import zone.ien.hig.section.CupertinoSection
import zone.ien.hig.section.SectionLink
import zone.ien.hig.section.SectionStyle
import zone.ien.hig.theme.CupertinoTheme

@OptIn(ExperimentalCupertinoApi::class, InternalCupertinoApi::class)
@Composable
fun HapticFeedbackScreen(navigateBack: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    val backdrop = rememberLayerBackdrop()
    val scrollState = rememberLazyListState()
    val cupertinoTypes = listOf(
        "SelectionChanged" to CupertinoHapticFeedback.SelectionChanged,
        "Success" to CupertinoHapticFeedback.Success,
        "Warning" to CupertinoHapticFeedback.Warning,
        "Error" to CupertinoHapticFeedback.Error,
        "ImpactLight" to CupertinoHapticFeedback.ImpactLight,
        "ImpactMedium" to CupertinoHapticFeedback.ImpactMedium,
        "ImpactHeavy" to CupertinoHapticFeedback.ImpactHeavy,
        "ImpactRigid" to CupertinoHapticFeedback.ImpactRigid,
        "ImpactSoft" to CupertinoHapticFeedback.ImpactSoft,
    )

    CupertinoScaffold(
        containerColor = CupertinoTheme.colorScheme.systemGroupedBackground,
        topBar = {
            CupertinoTopAppBar(
                title = { CupertinoText("Haptic Feedback") },
                navigationIcon = {
                    if (!isAdaptiveListDetailExpanded) {
                        CupertinoNavigateBackLiquidButton(
                            onClick = navigateBack,
                            backdrop = backdrop,
                            modifier = Modifier.padding(CupertinoTopAppBarDefaults.navigationIconPadding),
                        )
                    }
                },
                backdrop = backdrop,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            state = scrollState,
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .layerBackdrop(backdrop)
                .cupertinoScrollEdge(scrollState),
        ) {
            item(key = "cupertino") {
                CupertinoSection(
                    style = SectionStyle.InsetGrouped,
                    title = { CupertinoText("Cupertino") },
                    caption = { CupertinoText("Tap an item to play feedback. Cupertino types are available on iOS.") },
                ) {
                    cupertinoTypes.forEach { (label, type) ->
                        SectionLink(
                            onClick = { haptic.performHapticFeedback(type) },
                            chevron = {},
                        ) {
                            CupertinoText(label)
                        }
                    }
                }
            }
            item(key = "compose") {
                CupertinoSection(
                    style = SectionStyle.InsetGrouped,
                    title = { CupertinoText("Compose") },
                    caption = { CupertinoText("Feedback depends on device and platform support.") },
                ) {
                    HapticFeedbackType.values().forEach { type ->
                        SectionLink(
                            onClick = { haptic.performHapticFeedback(type) },
                            chevron = {},
                        ) {
                            CupertinoText(type.toString())
                        }
                    }
                }
            }
        }
    }
}
