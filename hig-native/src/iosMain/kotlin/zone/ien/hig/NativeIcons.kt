/*
 * Copyright (c) 2023-2024. Compose Cupertino project and open source contributors.
 * Copyright (c) 2025. Scott Lanoue.
 * Copyright (c) 2026. IENGROUND of IENLAB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package zone.ien.hig

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import zone.ien.hig.icons.CupertinoIcons
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.Image
import platform.CoreFoundation.CFStringCompare
import platform.CoreGraphics.CGBitmapContextCreate
import platform.CoreGraphics.CGColorSpaceCreateWithName
import platform.CoreGraphics.CGColorSpaceGetName
import platform.CoreGraphics.CGColorSpaceGetModel
import platform.CoreGraphics.CGColorSpaceRelease
import platform.CoreGraphics.CGImageGetColorSpace
import platform.CoreGraphics.CGContextFlush
import platform.CoreGraphics.CGContextRelease
import platform.CoreGraphics.CGContextScaleCTM
import platform.CoreGraphics.CGContextTranslateCTM
import platform.CoreGraphics.CGImageAlphaInfo
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.kCGBitmapByteOrder32Big
import platform.CoreGraphics.kCGColorSpaceModelMonochrome
import platform.CoreGraphics.kCGColorSpaceSRGB
import platform.UIKit.UIGraphicsPopContext
import platform.UIKit.UIGraphicsPushContext
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
import platform.posix.memcpy
import kotlin.math.roundToInt

/**
 * Returns iOS system SF Symbol with given [systemName].
 *
 * Use [ImageBitmap.Companion.systemImage] to create this image without composable context.
 *
 * @see <a href="https://developer.apple.com/sf-symbols/">SF Symbols</a>
 * */
@Composable
fun CupertinoIcons.named(systemName: String): Painter =
    remember(systemName) {
        BitmapPainter(ImageBitmap.systemImage(systemName))
    }

/**
 * Get iOS/iPadOS/macOS system image (SF Symbol) with given [systemName].
 * Must be remembered for inline Compose usage.
 *
 * @see <a href="https://developer.apple.com/sf-symbols/">SF Symbols</a>
 * @see CupertinoIcons.named
 * */
fun ImageBitmap.Companion.systemImage(systemName: String): ImageBitmap =
    requireNotNull(UIImage.systemImageNamed(systemName)) {
        "Image with name $systemName not found"
    }.toComposeImageBitmap()

@OptIn(ExperimentalForeignApi::class)
fun UIImage.toComposeImageBitmap(): ImageBitmap {
    if (!hasSrgbSourceColorSpace()) return toEncodedComposeImageBitmap()
    return toRasterComposeImageBitmap() ?: toEncodedComposeImageBitmap()
}

@OptIn(ExperimentalForeignApi::class)
private fun UIImage.hasSrgbSourceColorSpace(): Boolean {
    val cgImage = CGImage
    val ciImage = this.CIImage
    val sourceColorSpace =
        when {
            cgImage != null -> CGImageGetColorSpace(cgImage)
            ciImage != null -> ciImage.colorSpace
            else -> return true
        } ?: return false
    if (CGColorSpaceGetModel(sourceColorSpace) == kCGColorSpaceModelMonochrome) return true
    val sourceColorSpaceName = CGColorSpaceGetName(sourceColorSpace) ?: return false
    return CFStringCompare(sourceColorSpaceName, kCGColorSpaceSRGB, 0u) == 0L
}

@OptIn(ExperimentalForeignApi::class)
private fun UIImage.toRasterComposeImageBitmap(): ImageBitmap? {
    val imageSize = size.useContents { width to height }
    val imageScale = scale
    if (!imageScale.isFinite() || imageScale <= 0.0) return null

    val width =
        (imageSize.first * imageScale)
            .takeIf { it.isFinite() && it > 0.0 && it <= Int.MAX_VALUE }
            ?.roundToInt() ?: return null
    val height =
        (imageSize.second * imageScale)
            .takeIf { it.isFinite() && it > 0.0 && it <= Int.MAX_VALUE }
            ?.roundToInt() ?: return null
    if (width <= 0 || height <= 0) return null
    val pixelCount = width.toLong() * height.toLong()
    if (pixelCount > Int.MAX_VALUE / 4L) return null
    val rowBytes = width.toLong() * 4L
    val byteCount = pixelCount * 4L

    val colorSpace = CGColorSpaceCreateWithName(kCGColorSpaceSRGB) ?: return null
    val bytes = ByteArray(byteCount.toInt())
    try {
        bytes.usePinned { pinned ->
            val bitmapInfo =
                kCGBitmapByteOrder32Big or
                    CGImageAlphaInfo.kCGImageAlphaPremultipliedLast.value
            val context =
                CGBitmapContextCreate(
                    data = pinned.addressOf(0),
                    width = width.toULong(),
                    height = height.toULong(),
                    bitsPerComponent = 8u,
                    bytesPerRow = rowBytes.toULong(),
                    space = colorSpace,
                    bitmapInfo = bitmapInfo,
                ) ?: return null

            try {
                CGContextTranslateCTM(context, 0.0, height.toDouble())
                CGContextScaleCTM(context, imageScale, -imageScale)
                UIGraphicsPushContext(context)
                try {
                    drawInRect(
                        CGRectMake(
                            0.0,
                            0.0,
                            imageSize.first,
                            imageSize.second,
                        ),
                    )
                } finally {
                    UIGraphicsPopContext()
                }
                CGContextFlush(context)
            } finally {
                CGContextRelease(context)
            }
        }
    } finally {
        CGColorSpaceRelease(colorSpace)
    }

    val imageInfo =
        ImageInfo(
            width = width,
            height = height,
            colorType = ColorType.RGBA_8888,
            alphaType = ColorAlphaType.PREMUL,
            colorSpace = ColorSpace.sRGB,
        )
    return Image.makeRaster(imageInfo, bytes, rowBytes.toInt()).toComposeImageBitmap()
}

@OptIn(ExperimentalForeignApi::class)
private fun UIImage.toEncodedComposeImageBitmap(): ImageBitmap {
    val bytes =
        requireNotNull(UIImagePNGRepresentation(this)) {
            "Failed to get PNG representation of image"
        }

    val byteArray = ByteArray(bytes.length.toInt())

    byteArray.usePinned {
        memcpy(it.addressOf(0), bytes.bytes, bytes.length)
    }

    return Image
        .makeFromEncoded(byteArray)
        .toComposeImageBitmap()
}
