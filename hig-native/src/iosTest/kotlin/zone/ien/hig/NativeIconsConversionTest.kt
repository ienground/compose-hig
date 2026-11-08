package zone.ien.hig

import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.Image
import platform.CoreFoundation.CFStringCompare
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import platform.CoreGraphics.CGImageRelease
import platform.CoreGraphics.CGBitmapContextCreate
import platform.CoreGraphics.CGBitmapContextCreateImage
import platform.CoreGraphics.CGColorSpaceCreateWithName
import platform.CoreGraphics.CGColorSpaceGetModel
import platform.CoreGraphics.CGColorSpaceGetName
import platform.CoreGraphics.CGColorSpaceRelease
import platform.CoreGraphics.CGImageGetColorSpace
import platform.CoreGraphics.CGContextFillRect
import platform.CoreGraphics.CGContextRelease
import platform.CoreGraphics.CGContextSetRGBFillColor
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGImageAlphaInfo
import platform.CoreGraphics.kCGBitmapByteOrder32Big
import platform.CoreGraphics.kCGColorSpaceDisplayP3
import platform.CoreGraphics.kCGColorSpaceModelMonochrome
import platform.CoreGraphics.kCGColorSpaceSRGB
import platform.CoreImage.CIColor
import platform.CoreImage.CIContext
import platform.CoreImage.CIImage
import platform.CoreImage.kCIFormatRGBA8
import platform.CoreImage.createCGImage
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
import platform.UIKit.UIImageOrientation
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.posix.memcpy
import platform.posix._CLOCK_THREAD_CPUTIME_ID
import platform.posix.clock_gettime_nsec_np

@OptIn(ExperimentalForeignApi::class)
class NativeIconsConversionTest {
    @Test
    fun convertsImageBackedByCIImage() {
        val coreImage =
            CIImage.imageWithColor(
                CIColor(red = 0.8, green = 0.2, blue = 0.1, alpha = 0.5),
            ).imageByCroppingToRect(CGRectMake(0.0, 0.0, 16.0, 8.0))
        val image = UIImage(cIImage = coreImage)

        val bitmap = image.toComposeImageBitmap()
        val ciContext = CIContext.contextWithOptions(null)
        val cgImage =
            assertNotNull(
                ciContext.createCGImage(
                    image = coreImage,
                    fromRect = coreImage.extent,
                    format = kCIFormatRGBA8,
                    colorSpace = null,
                    deferred = false,
                ),
            )
        val referenceImage =
            UIImage(
                cGImage = cgImage,
                scale = image.scale,
                orientation = image.imageOrientation,
            )
        CGImageRelease(cgImage)
        val referenceCenter = referenceImage.toPreviousPngImageBitmap().toPixelMap()[8, 4]
        val previousPngCenter = image.toPreviousPngImageBitmap().toPixelMap()[8, 4]

        assertEquals(16, bitmap.width)
        assertEquals(8, bitmap.height)
        val center = bitmap.toPixelMap()[8, 4]
        val pngRepresentation = UIImagePNGRepresentation(image)
        assertTrue(referenceCenter.alpha in 0.49f..0.51f, "Core Image reference should contain 50% alpha")
        assertTrue(
            previousPngCenter.alpha in 0.49f..0.51f,
            "Connected previous PNG path should retain 50% alpha; got ${previousPngCenter.alpha}",
        )
        assertTrue(
            center.alpha in 0.49f..0.51f,
            "Expected 50% alpha; got ${center.alpha}. PNG bytes=${pngRepresentation?.length}, CGImage=${image.CGImage != null}",
        )
        assertTrue(center.red > center.green && center.green > center.blue)
    }

    @Test
    fun systemSymbolConversionMatchesPreviousPngPixels() {
        val image = assertNotNull(UIImage.systemImageNamed("chevron.left"))

        assertBitmapsMatch(
            expected = image.toPreviousPngImageBitmap(),
            actual = image.toComposeImageBitmap(),
        )
    }

    @Test
    fun scaledOrientedMonochromeImageMatchesUIKitRendering() {
        val systemImage = assertNotNull(UIImage.systemImageNamed("chevron.left"))
        val pngData = assertNotNull(UIImagePNGRepresentation(systemImage))
        val bitmapBackedImage = UIImage(data = pngData)
        val orientedImage =
            UIImage(
                cGImage = assertNotNull(bitmapBackedImage.CGImage),
                scale = 2.0,
                orientation = UIImageOrientation.UIImageOrientationRight,
            )
        val expectedImage = orientedImage.renderedByUIKit()

        assertBitmapsMatch(
            expected = expectedImage.toPreviousPngImageBitmap(),
            actual = orientedImage.toComposeImageBitmap(),
        )
    }

    @Test
    fun scaledOrientedSrgbImageMatchesUIKitRendering() {
        val baseImage = makeSrgbPatternImage()
        val orientedImage =
            UIImage(
                cGImage = assertNotNull(baseImage.CGImage),
                scale = 2.0,
                orientation = UIImageOrientation.UIImageOrientationRight,
            )
        val expectedImage = orientedImage.renderedByUIKit()

        assertBitmapsMatch(
            expected = expectedImage.toPreviousPngImageBitmap(),
            actual = orientedImage.toComposeImageBitmap(),
        )
    }

    @Test
    fun wideGamutImageMatchesPreviousPngPixels() {
        val colorSpace = assertNotNull(CGColorSpaceCreateWithName(kCGColorSpaceDisplayP3))
        val context =
            CGBitmapContextCreate(
                data = null,
                width = 1u,
                height = 1u,
                bitsPerComponent = 8u,
                bytesPerRow = 0u,
                space = colorSpace,
                bitmapInfo =
                    kCGBitmapByteOrder32Big or
                        CGImageAlphaInfo.kCGImageAlphaPremultipliedLast.value,
            )
        try {
            val p3Context = assertNotNull(context)
            CGContextSetRGBFillColor(p3Context, 0.0, 1.0, 0.0, 1.0)
            CGContextFillRect(p3Context, CGRectMake(0.0, 0.0, 1.0, 1.0))
            val cgImage = assertNotNull(CGBitmapContextCreateImage(p3Context))
            val image = UIImage(cGImage = cgImage)
            CGImageRelease(cgImage)

            val expected = image.toPreviousPngImageBitmap()
            val actual = image.toComposeImageBitmap()
            assertBitmapsMatch(expected = expected, actual = actual)
        } finally {
            context?.let(::CGContextRelease)
            CGColorSpaceRelease(colorSpace)
        }
    }

    @Test
    fun logsSevenSampleThreadCpuMedianForPngAndRgbaConversion() {
        val image = assertNotNull(UIImage.systemImageNamed("chevron.left"))
        val sourceCGImage = image.CGImage
        val sourceCIImage = image.CIImage
        val sourceColorSpace =
            when {
                sourceCGImage != null -> CGImageGetColorSpace(sourceCGImage)
                sourceCIImage != null -> sourceCIImage.colorSpace
                else -> null
            }
        val directProfileEligible =
            when {
                sourceCGImage == null && sourceCIImage == null -> true
                sourceColorSpace == null -> false
                CGColorSpaceGetModel(sourceColorSpace) == kCGColorSpaceModelMonochrome -> true
                else -> {
                    val name = CGColorSpaceGetName(sourceColorSpace)
                    name != null && CFStringCompare(name, kCGColorSpaceSRGB, 0u) == 0L
                }
            }
        assertTrue(directProfileEligible, "chevron.left must not use PNG color-space fallback")
        val sourceDescription =
            when {
                sourceCGImage == null && sourceCIImage == null -> "no bitmap backing"
                sourceColorSpace == null -> "no color space"
                CGColorSpaceGetModel(sourceColorSpace) == kCGColorSpaceModelMonochrome -> "monochrome"
                else -> "sRGB"
            }
        repeat(8) {
            image.toPreviousPngImageBitmap()
            image.toComposeImageBitmap()
        }

        val pngSamples = measureSevenSampleThreadCpuNanos { image.toPreviousPngImageBitmap() }
        val rgbaSamples = measureSevenSampleThreadCpuNanos { image.toComposeImageBitmap() }
        val pngPixelSamples =
            measureSevenSampleThreadCpuNanos(forcePixelRead = true) {
                image.toPreviousPngImageBitmap()
            }
        val rgbaPixelSamples =
            measureSevenSampleThreadCpuNanos(forcePixelRead = true) {
                image.toComposeImageBitmap()
            }
        val pngMedian = pngSamples.sorted()[pngSamples.size / 2]
        val rgbaMedian = rgbaSamples.sorted()[rgbaSamples.size / 2]
        val pngPixelMedian = pngPixelSamples.sorted()[pngPixelSamples.size / 2]
        val rgbaPixelMedian = rgbaPixelSamples.sorted()[rgbaPixelSamples.size / 2]

        println(
            "NativeIcons chevron.left CPU ns/conversion: " +
                "PNG samples=${pngSamples.joinToString()} median=$pngMedian; " +
                "RGBA samples=${rgbaSamples.joinToString()} median=$rgbaMedian",
        )
        println(
            "NativeIcons chevron.left CPU ns/conversion+pixel-read: " +
                "PNG samples=${pngPixelSamples.joinToString()} median=$pngPixelMedian; " +
                "RGBA samples=${rgbaPixelSamples.joinToString()} median=$rgbaPixelMedian",
        )
        println("NativeIcons chevron.left direct profile path: $sourceDescription")
    }

    private fun UIImage.renderedByUIKit(): UIImage {
        val imageSize = size.useContents { width to height }
        UIGraphicsBeginImageContextWithOptions(size, false, scale)
        return try {
            drawInRect(CGRectMake(0.0, 0.0, imageSize.first, imageSize.second))
            assertNotNull(UIGraphicsGetImageFromCurrentImageContext())
        } finally {
            UIGraphicsEndImageContext()
        }
    }

    private fun makeSrgbPatternImage(): UIImage {
        val colorSpace = assertNotNull(CGColorSpaceCreateWithName(kCGColorSpaceSRGB))
        val context =
            CGBitmapContextCreate(
                data = null,
                width = 4u,
                height = 2u,
                bitsPerComponent = 8u,
                bytesPerRow = 0u,
                space = colorSpace,
                bitmapInfo =
                    kCGBitmapByteOrder32Big or
                        CGImageAlphaInfo.kCGImageAlphaPremultipliedLast.value,
            )
        try {
            val bitmapContext = assertNotNull(context)
            CGContextSetRGBFillColor(bitmapContext, 0.0, 0.0, 0.0, 1.0)
            CGContextFillRect(bitmapContext, CGRectMake(0.0, 0.0, 4.0, 2.0))
            CGContextSetRGBFillColor(bitmapContext, 0.0, 1.0, 0.0, 1.0)
            CGContextFillRect(bitmapContext, CGRectMake(0.0, 0.0, 2.0, 2.0))
            CGContextSetRGBFillColor(bitmapContext, 1.0, 0.0, 0.0, 1.0)
            CGContextFillRect(bitmapContext, CGRectMake(2.0, 0.0, 2.0, 2.0))
            val cgImage = assertNotNull(CGBitmapContextCreateImage(bitmapContext))
            val image = UIImage(cGImage = cgImage)
            CGImageRelease(cgImage)
            return image
        } finally {
            context?.let(::CGContextRelease)
            CGColorSpaceRelease(colorSpace)
        }
    }

    private fun UIImage.toPreviousPngImageBitmap(): androidx.compose.ui.graphics.ImageBitmap {
        val data = assertNotNull(UIImagePNGRepresentation(this))
        val bytes = ByteArray(data.length.toInt())
        bytes.usePinned {
            memcpy(it.addressOf(0), data.bytes, data.length)
        }
        return Image.makeFromEncoded(bytes).toComposeImageBitmap()
    }

    private fun assertBitmapsMatch(
        expected: androidx.compose.ui.graphics.ImageBitmap,
        actual: androidx.compose.ui.graphics.ImageBitmap,
    ) {
        assertEquals(expected.width, actual.width)
        assertEquals(expected.height, actual.height)
        val expectedPixels = expected.toPixelMap()
        val actualPixels = actual.toPixelMap()
        for (y in 0 until expected.height) {
            for (x in 0 until expected.width) {
                val expectedColor = expectedPixels[x, y]
                val actualColor = actualPixels[x, y]
                assertTrue(
                    kotlin.math.abs(expectedColor.red - actualColor.red) <= 1f / 255f &&
                        kotlin.math.abs(expectedColor.green - actualColor.green) <= 1f / 255f &&
                        kotlin.math.abs(expectedColor.blue - actualColor.blue) <= 1f / 255f &&
                        kotlin.math.abs(expectedColor.alpha - actualColor.alpha) <= 1f / 255f,
                    "Pixel ($x, $y) differs: expected=$expectedColor actual=$actualColor",
                )
            }
        }
    }

    private fun measureSevenSampleThreadCpuNanos(
        forcePixelRead: Boolean = false,
        convert: () -> androidx.compose.ui.graphics.ImageBitmap,
    ): List<Long> {
        val iterationsPerSample = 40
        return List(7) {
            var widthChecksum = 0
            var alphaChecksum = 0f
            val start = clock_gettime_nsec_np(_CLOCK_THREAD_CPUTIME_ID)
            repeat(iterationsPerSample) {
                val bitmap = convert()
                widthChecksum += bitmap.width
                if (forcePixelRead) {
                    val pixelMap = bitmap.toPixelMap()
                    for (y in 0 until bitmap.height) {
                        for (x in 0 until bitmap.width) {
                            alphaChecksum += pixelMap[x, y].alpha
                        }
                    }
                }
            }
            val elapsed = clock_gettime_nsec_np(_CLOCK_THREAD_CPUTIME_ID) - start
            assertTrue(widthChecksum > 0)
            if (forcePixelRead) assertTrue(alphaChecksum > 0f)
            elapsed.toLong() / iterationsPerSample
        }
    }
}
