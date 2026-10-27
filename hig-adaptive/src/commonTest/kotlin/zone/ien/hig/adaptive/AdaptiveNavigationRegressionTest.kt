package zone.ien.hig.adaptive

import kotlin.test.Test
import kotlin.test.assertEquals

class AdaptiveNavigationRegressionTest {
    @Test
    fun nativeMaterialItemInvokesSelectionAndItemCallbacksInOrder() {
        val callbackOrder = mutableListOf<String>()

        dispatchNavigationBarItemClick(
            index = 2,
            onTabSelected = { callbackOrder += "selected:$it" },
            onItemClick = { callbackOrder += "item" },
        )

        assertEquals(listOf("selected:2", "item"), callbackOrder)
    }
}
