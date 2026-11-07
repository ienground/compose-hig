package zone.ien.hig

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.tooling.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class, ExperimentalCupertinoApi::class, ExperimentalComposeRuntimeApi::class, InternalComposeApi::class)
class CupertinoScaffoldPerformanceTest {
    @Test
    fun clampedScrollEdgeLimitsCompositionWork() = runComposeUiTest {
        mainClock.autoAdvance = false
        lateinit var scroll: androidx.compose.foundation.ScrollState
        var contentCompositions = 0
        var topCompositions = 0
        var scopeEntries = 0
        val observer = object : CompositionObserver {
            override fun onBeginComposition(composition: ObservableComposition) {}
            override fun onEndComposition(composition: ObservableComposition) {}
            override fun onScopeEnter(scope: RecomposeScope) { scopeEntries++ }
            override fun onScopeExit(scope: RecomposeScope) {}
            override fun onScopeInvalidated(scope: RecomposeScope, value: Any?) {}
            override fun onReadInScope(scope: RecomposeScope, value: Any) {}
            override fun onScopeDisposed(scope: RecomposeScope) {}
        }
        setContent {
            val composition = currentComposer.composition
            DisposableEffect(composition) {
                val handle = composition.setObserver(observer)
                onDispose { handle?.dispose() }
            }
            scroll = rememberScrollState()
            CompositionLocalProvider(LocalCupertinoRightSystemArea provides false) {
                CupertinoScaffold(
                    topBar = { SideEffect { topCompositions++ }; Box(Modifier.height(50.dp)) },
                ) {
                    SideEffect { contentCompositions++ }
                    Column(Modifier.fillMaxSize().verticalScroll(scroll).cupertinoScrollEdge(scroll)) {
                        Box(Modifier.height(3000.dp))
                    }
                }
            }
        }
        mainClock.advanceTimeBy(1000)
        runOnIdle { scroll.dispatchRawDelta(200f) }
        mainClock.advanceTimeBy(1000)
        scopeEntries = 0
        val initialScopeEntries = scopeEntries
        val initialContent = contentCompositions
        val initialTop = topCompositions
        repeat(48) { index ->
            runOnIdle { scroll.dispatchRawDelta(if (index < 24) 1f else -1f) }
            mainClock.advanceTimeBy(32)
        }
        runOnIdle {
            assertTrue(scopeEntries - initialScopeEntries < 3, "합성 스코프 진입: ${scopeEntries - initialScopeEntries}")
            assertTrue(contentCompositions - initialContent < 3, "콘텐츠 재합성: ${contentCompositions - initialContent}")
            assertTrue(topCompositions - initialTop < 3, "상단 바 재합성: ${topCompositions - initialTop}")
        }
    }
}
