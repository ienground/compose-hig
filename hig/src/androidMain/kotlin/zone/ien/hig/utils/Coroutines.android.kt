package zone.ien.hig.utils

import kotlinx.coroutines.android.awaitFrame

actual suspend fun awaitFrame() {
    awaitFrame()
}