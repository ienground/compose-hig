package zone.ien.hig

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGSizeMake
import platform.IOSurface.IOSurfaceDecrementUseCount
import platform.IOSurface.IOSurfaceIncrementUseCount
import platform.IOSurface.IOSurfaceIsInUse
import platform.QuartzCore.CACurrentMediaTime
import zone.ien.hig.uikit.rendering.test.CMPDrawable
import zone.ien.hig.uikit.rendering.test.HIGCreateTestMetalLayer
import kotlin.test.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalForeignApi::class)
class CupertinoRenderingTest {
    @BeforeTest
    fun installFix() {
        assertTrue(enableCupertinoParallelRenderingFix())
        assertTrue(enableCupertinoParallelRenderingFix())
    }

    @Test
    fun busySurfacesAreExcludedAndPoolStopsAtFiveWithoutBlockingMainThread() {
        val layer = HIGCreateTestMetalLayer()!!.apply { drawableSize = CGSizeMake(8.0, 8.0) }
        val busy = mutableListOf<CMPDrawable>()
        try {
            repeat(5) {
                val drawable = assertNotNull(layer.nextDrawable())
                assertTrue(busy.none { it.surface == drawable.surface }, "합성 중인 버퍼를 재사용하면 안 됩니다")
                IOSurfaceIncrementUseCount(drawable.surface)
                busy += drawable
                assertTrue(IOSurfaceIsInUse(drawable.surface))
                layer.releaseDrawable(drawable)
            }

            val start = CACurrentMediaTime()
            assertNull(layer.nextDrawable(), "최대 5개가 모두 사용 중이면 프레임을 건너뛰어야 합니다")
            assertTrue(CACurrentMediaTime() - start < 0.1, "메인 스레드에서 버퍼를 기다리면 안 됩니다")

            val idle = busy.removeAt(0)
            IOSurfaceDecrementUseCount(idle.surface)
            val reused = assertNotNull(layer.nextDrawable())
            assertEquals(idle, reused)
            layer.releaseDrawable(reused)
        } finally {
            busy.forEach { IOSurfaceDecrementUseCount(it.surface) }
            layer.drainDrawables()
        }
    }

    @Test
    fun idleSurfaceIsReusedBeforeAllocatingAnotherBuffer() {
        val layer = HIGCreateTestMetalLayer()!!.apply { drawableSize = CGSizeMake(8.0, 8.0) }
        assertNotNull(layer.device, "Metal 장치를 초기화해야 합니다")
        try {
            val first = assertNotNull(layer.nextDrawable())
            layer.releaseDrawable(first)
            val next = assertNotNull(layer.nextDrawable())
            assertEquals(first, next)
            layer.releaseDrawable(next)
        } finally {
            layer.drainDrawables()
        }
    }

    @Test
    fun resizingDrainsPoolAndAllocatesSurfaceWithNewSize() {
        val layer = HIGCreateTestMetalLayer()!!.apply { drawableSize = CGSizeMake(8.0, 8.0) }
        try {
            val first = assertNotNull(layer.nextDrawable())
            layer.releaseDrawable(first)
            val generation = layer.drawablesGeneration
            layer.drawableSize = CGSizeMake(16.0, 16.0)
            assertEquals(generation + 1, layer.drawablesGeneration)
            val resized = assertNotNull(layer.nextDrawable())
            assertEquals(16uL, platform.IOSurface.IOSurfaceGetWidth(resized.surface))
            assertEquals(16uL, platform.IOSurface.IOSurfaceGetHeight(resized.surface))
            layer.releaseDrawable(resized)
        } finally {
            layer.drainDrawables()
        }
    }

    @Test
    fun gpuWorkMustFinishBeforeReusingSurface() {
        val layer = HIGCreateTestMetalLayer()!!.apply { drawableSize = CGSizeMake(8.0, 8.0) }
        try {
            val first = assertNotNull(layer.nextDrawable())
            first.isWaitingForCommandBufferCompletion = true
            layer.releaseDrawable(first)
            val next = assertNotNull(layer.nextDrawable())
            assertFalse(first == next)
            first.isWaitingForCommandBufferCompletion = false
            layer.releaseDrawable(next)
        } finally {
            layer.drainDrawables()
        }
    }

    @Test
    fun emptySizeDoesNotBlockMainThread() {
        val layer = HIGCreateTestMetalLayer()!!
        val start = CACurrentMediaTime()
        assertNull(layer.nextDrawable())
        assertTrue(CACurrentMediaTime() - start < 0.1)
    }

    @Test
    fun installationFromBackgroundThreadIsRejected() {
        var installed = true
        val completed = platform.darwin.dispatch_semaphore_create(0)
        platform.darwin.dispatch_async(platform.darwin.dispatch_get_global_queue(0, 0u)) {
            installed = enableCupertinoParallelRenderingFix()
            platform.darwin.dispatch_semaphore_signal(completed)
        }
        val timeout = platform.darwin.dispatch_time(platform.darwin.DISPATCH_TIME_NOW, 2_000_000_000)
        assertEquals(0L, platform.darwin.dispatch_semaphore_wait(completed, timeout))
        assertFalse(installed)
    }
}
