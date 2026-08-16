package zone.ien.hig.utils

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

actual suspend fun awaitFrame() {
    delay((1000L / 60L).milliseconds)
}