package haptic

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
                    CupertinoNavigateBackLiquidButton(
                        onClick = navigateBack,
                        backdrop = backdrop,
                        modifier = Modifier.padding(start = 16.dp),
                    )
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
                    caption = { CupertinoText("항목을 눌러 실행합니다. Cupertino 전용 타입은 iOS에서 지원합니다.") },
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
                    caption = { CupertinoText("기기와 플랫폼의 지원 여부에 따라 진동이 발생하지 않을 수 있습니다.") },
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
