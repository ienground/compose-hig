package zone.ien.hig

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

/** 내비게이션이 제공하는 상단 바 영역별 공유 전환입니다. */
interface TopBarTransition {
    /** 화면 사이에서 같은 역할의 영역을 연결합니다. */
    enum class Slot { NavigationIcon, Actions }

    /** 화면 전환에 동기화된 [slot]의 위치와 내용을 전환합니다. */
    @Composable
    fun modifier(slot: Slot): Modifier
}

/** 전환 호스트가 없으면 기존 상단 바 배치를 유지합니다. */
val LocalTopBarTransition = staticCompositionLocalOf<TopBarTransition?> { null }
