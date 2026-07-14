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

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImageRenderingMode
import platform.UIKit.UIUserInterfaceStyle
import platform.UIKit.UIView
import platform.UIKit.UIViewController

internal fun UIViewController.applyTheme(dark: Boolean) {
    overrideUserInterfaceStyle =
        if (dark) {
            UIUserInterfaceStyle.UIUserInterfaceStyleDark
        } else {
            UIUserInterfaceStyle.UIUserInterfaceStyleLight
        }
}

internal fun UIView.applyTheme(dark: Boolean) {
    listOf(this, superview).forEach {
        it?.overrideUserInterfaceStyle =
            if (dark) {
                UIUserInterfaceStyle.UIUserInterfaceStyleDark
            } else {
                UIUserInterfaceStyle.UIUserInterfaceStyleLight
            }
    }
}

@OptIn(ExperimentalForeignApi::class)
fun ImageBitmap.toUIImage(): UIImage {
    val skiaImage = Image.makeFromBitmap(this.asSkiaBitmap())
    val pngData = skiaImage.encodeToData(EncodedImageFormat.PNG)
        ?: return UIImage()
    val pngBytes = pngData.bytes
    val nsData = pngBytes.usePinned { pinned ->
        NSData.dataWithBytes(pinned.addressOf(0), pngBytes.size.toULong())
    }
    return UIImage(data = nsData).imageWithRenderingMode(UIImageRenderingMode.UIImageRenderingModeAlwaysOriginal)
}

fun Painter.toImageBitmap(
    size: Size = intrinsicSize,
    density: Density = Density(1f),
    layoutDirection: LayoutDirection = LayoutDirection.Ltr,
): ImageBitmap {
    val width = size.width.toInt().takeIf { it > 0 } ?: 64
    val height = size.height.toInt().takeIf { it > 0 } ?: 64
    val bitmap = ImageBitmap(width, height)
    val canvas = Canvas(bitmap)
    CanvasDrawScope().draw(
        density = density,
        layoutDirection = layoutDirection,
        canvas = canvas,
        size = Size(width.toFloat(), height.toFloat())
    ) {
        // Force render as white -> so AlwaysTemplate can multiply tint correctly
        with(this) {
            drawContext.canvas.let { c ->
                val paint = androidx.compose.ui.graphics.Paint().apply {
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                        androidx.compose.ui.graphics.Color.White
                    )
                }
                c.saveLayer(
                    bounds = androidx.compose.ui.geometry.Rect(
                        0f, 0f, width.toFloat(), height.toFloat()
                    ),
                    paint = paint
                )
            }
        }
        draw(Size(width.toFloat(), height.toFloat()))
        drawContext.canvas.restore()
    }
    return bitmap
}

@OptIn(ExperimentalForeignApi::class)
fun Painter.toUIImage(size: Size = intrinsicSize): UIImage {
    return this.toImageBitmap(size).toUIImage()
}

@OptIn(ExperimentalForeignApi::class)
fun UIImage.resized(maxSize: Double): UIImage {
    val originalWidth = this.size.useContents { width }
    val originalHeight = this.size.useContents { height }

    if (originalWidth <= 0.0 || originalHeight <= 0.0) return this

    val scale = maxSize / maxOf(originalWidth, originalHeight)
    val targetWidth = originalWidth * scale
    val targetHeight = originalHeight * scale

    val targetSize = CGSizeMake(targetWidth, targetHeight)
    UIGraphicsBeginImageContextWithOptions(targetSize, false, 0.0)
    this.imageWithRenderingMode(UIImageRenderingMode.UIImageRenderingModeAlwaysOriginal)
        .drawInRect(CGRectMake(0.0, 0.0, targetWidth, targetHeight))
    val resizedImage = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    return resizedImage
        ?.imageWithRenderingMode(UIImageRenderingMode.UIImageRenderingModeAlwaysTemplate)
        ?: this
}