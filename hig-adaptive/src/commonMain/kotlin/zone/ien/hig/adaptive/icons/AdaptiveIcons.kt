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

package zone.ien.hig.adaptive.icons

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import zone.ien.hig.adaptive.LocalTheme
import zone.ien.hig.adaptive.Theme
import zone.ien.hig.icons.CupertinoIcons
import zone.ien.hig.icons.outlined.ArrowClockwise
import zone.ien.hig.icons.outlined.Bell
import zone.ien.hig.icons.outlined.Calendar
import zone.ien.hig.icons.outlined.Cart
import zone.ien.hig.icons.outlined.Checkmark
import zone.ien.hig.icons.outlined.CheckmarkCircle
import zone.ien.hig.icons.outlined.ChevronBackward
import zone.ien.hig.icons.outlined.ChevronDown
import zone.ien.hig.icons.outlined.ChevronForward
import zone.ien.hig.icons.outlined.ChevronUp
import zone.ien.hig.icons.outlined.Ellipsis
import zone.ien.hig.icons.outlined.Envelope
import zone.ien.hig.icons.outlined.ExclamationmarkTriangle
import zone.ien.hig.icons.outlined.FaceSmiling
import zone.ien.hig.icons.outlined.Gearshape
import zone.ien.hig.icons.outlined.HandThumbsup
import zone.ien.hig.icons.outlined.Heart
import zone.ien.hig.icons.outlined.House
import zone.ien.hig.icons.outlined.InfoCircle
import zone.ien.hig.icons.outlined.ListBullet
import zone.ien.hig.icons.outlined.Location
import zone.ien.hig.icons.outlined.Lock
import zone.ien.hig.icons.outlined.MagnifyingGlass
import zone.ien.hig.icons.outlined.Mappin
import zone.ien.hig.icons.outlined.Paperplane
import zone.ien.hig.icons.outlined.Pencil
import zone.ien.hig.icons.outlined.Person
import zone.ien.hig.icons.outlined.PersonCropCircle
import zone.ien.hig.icons.outlined.PersonCropSquare
import zone.ien.hig.icons.outlined.Phone
import zone.ien.hig.icons.outlined.Play
import zone.ien.hig.icons.outlined.Plus
import zone.ien.hig.icons.outlined.PlusCircle
import zone.ien.hig.icons.outlined.RectanglePortraitAndArrowForward
import zone.ien.hig.icons.outlined.SquareAndArrowUp
import zone.ien.hig.icons.outlined.SquareAndPencil
import zone.ien.hig.icons.outlined.Star
import zone.ien.hig.icons.outlined.Trash
import zone.ien.hig.icons.outlined.WrenchAndScrewdriver
import zone.ien.hig.icons.outlined.Xmark
import zone.ien.hig.icons.outlined.XmarkCircle

/**
 * Provides adaptive icons for both Cupertino and Material design.
 *
 * This object contains a collection of adaptive icons that switch between Material and Cupertino design based on
 * the current theme. Icons are defined for both filled and outlined variants.
 */
object AdaptiveIcons {
    object Filled

    object Outlined

    /**
     * Adaptive vector icon based on current theme.
     *
     * To retrieve system images on iOS use [painter]
     *
     * @param material composable that returns the Material design icon
     * @param cupertino composable that returns the Cupertino icon
     * @return the appropriate icon based on the current theme
     */
    @Composable
    fun vector(
        material: @Composable () -> ImageVector,
        cupertino: @Composable () -> ImageVector,
    ): ImageVector =
        when (LocalTheme.current) {
            Theme.Cupertino -> cupertino()
            else -> material()
        }

    /**
     * Native adaptive icon based on current theme.
     *
     * - For [Theme.Material3] and [Theme.Cupertino] on non-iOS it return painter from [material] vector.
     * - For [Theme.Cupertino] on iOS it returns SF Symbol with system name [cupertino]
     *
     * @param material composable that returns the Material design icon
     * @param cupertino composable that returns the Cupertino system name
     * @return the appropriate painter based on the current theme
     */
    @Composable
    fun painter(
        material: @Composable () -> ImageVector,
        cupertino: @Composable () -> String,
    ): Painter =
        when (LocalTheme.current) {
            Theme.Cupertino -> systemImage(cupertino()) ?: rememberVectorPainter(material())
            else -> rememberVectorPainter(material())
        }
}

val AdaptiveIcons.Outlined.AccountBox
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.AccountBox },
            cupertino = { CupertinoIcons.Outlined.PersonCropSquare },
        )
val AdaptiveIcons.Outlined.AccountCircle
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.AccountCircle },
            cupertino = { CupertinoIcons.Outlined.PersonCropCircle },
        )
val AdaptiveIcons.Outlined.Add
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.Add },
            cupertino = { CupertinoIcons.Outlined.Plus },
        )
val AdaptiveIcons.Outlined.AddCircle
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.AddCircle },
            cupertino = { CupertinoIcons.Outlined.PlusCircle },
        )

// val AdaptiveIcons.Outlined.ArrowBack
//    @Composable
//    get() = AdaptiveIcons.icon(
//        material = MaterialSymbols.ArrowBack,
//        cupertino = CupertinoIcons.Outlined.ChevronBackward
//    )
// val AdaptiveIcons.Outlined.ArrowDropDown
//    @Composable
//    get() = AdaptiveIcons.icon(
//        material = MaterialSymbols.ArrowDropDown,
//        cupertino = CupertinoIcons.Outlined.ArrowDropDown
//    )
// val AdaptiveIcons.Outlined.ArrowForward
//    @Composable
//    get() = AdaptiveIcons.icon(
//        material = MaterialSymbols.ArrowForward,
//        cupertino = CupertinoIcons.Outlined.ArrowForward
//    )
val AdaptiveIcons.Outlined.Build
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.Build },
            cupertino = { CupertinoIcons.Outlined.WrenchAndScrewdriver },
        )
val AdaptiveIcons.Outlined.Call
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.Call },
            cupertino = { CupertinoIcons.Outlined.Phone },
        )
val AdaptiveIcons.Outlined.Check
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.Check },
            cupertino = { CupertinoIcons.Outlined.Checkmark },
        )
val AdaptiveIcons.Outlined.CheckCircle
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.CheckCircle },
            cupertino = { CupertinoIcons.Outlined.CheckmarkCircle },
        )
val AdaptiveIcons.Outlined.Clear
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.Clear },
            cupertino = { CupertinoIcons.Outlined.XmarkCircle },
        )
val AdaptiveIcons.Outlined.Close
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.Close },
            cupertino = { CupertinoIcons.Outlined.Xmark },
        )
val AdaptiveIcons.Outlined.Create
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.Create },
            cupertino = { CupertinoIcons.Outlined.SquareAndPencil },
        )
val AdaptiveIcons.Outlined.DateRange
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.DateRange },
            cupertino = { CupertinoIcons.Outlined.Calendar },
        )
val AdaptiveIcons.Outlined.Delete
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.Delete },
            cupertino = { CupertinoIcons.Outlined.Trash },
        )
val AdaptiveIcons.Outlined.Done
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.Done },
            cupertino = { CupertinoIcons.Outlined.Checkmark },
        )
val AdaptiveIcons.Outlined.Edit
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.Edit },
            cupertino = { CupertinoIcons.Outlined.Pencil },
        )
val AdaptiveIcons.Outlined.Email
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.Email },
            cupertino = { CupertinoIcons.Outlined.Envelope },
        )
val AdaptiveIcons.Outlined.ExitToApp
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.ExitToApp },
            cupertino = { CupertinoIcons.Outlined.RectanglePortraitAndArrowForward },
        )
val AdaptiveIcons.Outlined.Face
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.Face },
            cupertino = { CupertinoIcons.Outlined.FaceSmiling },
        )
val AdaptiveIcons.Outlined.Favorite
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.Favorite },
            cupertino = { CupertinoIcons.Outlined.Heart },
        )
val AdaptiveIcons.Outlined.FavoriteBorder
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.FavoriteBorder },
            cupertino = { CupertinoIcons.Outlined.Heart },
        )
val AdaptiveIcons.Outlined.Home
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.Home },
            cupertino = { CupertinoIcons.Outlined.House },
        )
val AdaptiveIcons.Outlined.Info
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.Info },
            cupertino = { CupertinoIcons.Outlined.InfoCircle },
        )
val AdaptiveIcons.Outlined.KeyboardArrowDown
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.KeyboardArrowDown },
            cupertino = { CupertinoIcons.Outlined.ChevronDown },
        )
val AdaptiveIcons.Outlined.KeyboardArrowLeft
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.KeyboardArrowLeft },
            cupertino = { CupertinoIcons.Outlined.ChevronBackward },
        )
val AdaptiveIcons.Outlined.KeyboardArrowRight
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.KeyboardArrowRight },
            cupertino = { CupertinoIcons.Outlined.ChevronForward },
        )

val AdaptiveIcons.Outlined.KeyboardArrowUp
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.KeyboardArrowUp },
            cupertino = { CupertinoIcons.Outlined.ChevronUp },
        )
val AdaptiveIcons.Outlined.List
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.List },
            cupertino = { CupertinoIcons.Outlined.ListBullet },
        )
val AdaptiveIcons.Outlined.LocationOn
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.LocationOn },
            cupertino = { CupertinoIcons.Outlined.Location },
        )
val AdaptiveIcons.Outlined.Lock
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.Lock },
            cupertino = { CupertinoIcons.Outlined.Lock },
        )
val AdaptiveIcons.Outlined.MailOutline
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.MailOutline },
            cupertino = { CupertinoIcons.Outlined.Envelope },
        )
val AdaptiveIcons.Outlined.Menu
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.Menu },
            cupertino = { CupertinoIcons.Outlined.ListBullet },
        )
val AdaptiveIcons.Outlined.MoreVert
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.MoreVert },
            cupertino = { CupertinoIcons.Outlined.Ellipsis },
        )
val AdaptiveIcons.Outlined.Notifications
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.Notifications },
            cupertino = { CupertinoIcons.Outlined.Bell },
        )
val AdaptiveIcons.Outlined.Person
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.Person },
            cupertino = { CupertinoIcons.Outlined.Person },
        )
val AdaptiveIcons.Outlined.Phone
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.Phone },
            cupertino = { CupertinoIcons.Outlined.Phone },
        )
val AdaptiveIcons.Outlined.Place
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.Place },
            cupertino = { CupertinoIcons.Outlined.Mappin },
        )
val AdaptiveIcons.Outlined.PlayArrow
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.PlayArrow },
            cupertino = { CupertinoIcons.Outlined.Play },
        )
val AdaptiveIcons.Outlined.Refresh
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.Refresh },
            cupertino = { CupertinoIcons.Outlined.ArrowClockwise },
        )
val AdaptiveIcons.Outlined.Search
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.Search },
            cupertino = { CupertinoIcons.Outlined.MagnifyingGlass },
        )
val AdaptiveIcons.Outlined.Send
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.Send },
            cupertino = { CupertinoIcons.Outlined.Paperplane },
        )
val AdaptiveIcons.Outlined.Settings
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.Settings },
            cupertino = { CupertinoIcons.Outlined.Gearshape },
        )
val AdaptiveIcons.Outlined.Share
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.Share },
            cupertino = { CupertinoIcons.Outlined.SquareAndArrowUp },
        )
val AdaptiveIcons.Outlined.ShoppingCart
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.ShoppingCart },
            cupertino = { CupertinoIcons.Outlined.Cart },
        )
val AdaptiveIcons.Outlined.Star
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.Star },
            cupertino = { CupertinoIcons.Outlined.Star },
        )
val AdaptiveIcons.Outlined.ThumbUp
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.ThumbUp },
            cupertino = { CupertinoIcons.Outlined.HandThumbsup },
        )
val AdaptiveIcons.Outlined.Warning
    @Composable
    get() =
        AdaptiveIcons.vector(
            material = { MaterialSymbols.Warning },
            cupertino = { CupertinoIcons.Outlined.ExclamationmarkTriangle },
        )

@Composable
internal expect fun systemImage(name: String): Painter?
