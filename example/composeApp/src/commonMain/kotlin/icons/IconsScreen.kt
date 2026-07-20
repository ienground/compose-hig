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



package icons

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.layerBackdrop
import zone.ien.hig.CupertinoAlertDialog
import zone.ien.hig.CupertinoIcon
import zone.ien.hig.CupertinoIconButton
import zone.ien.hig.CupertinoNavigateBackLiquidButton
import zone.ien.hig.CupertinoScaffold
import zone.ien.hig.CupertinoSegmentedControl
import zone.ien.hig.CupertinoSegmentedControlTab
import zone.ien.hig.CupertinoText
import zone.ien.hig.CupertinoTopAppBar
import zone.ien.hig.ExperimentalCupertinoApi
import zone.ien.hig.adaptive.AdaptiveWidget
import zone.ien.hig.adaptive.ExperimentalAdaptiveApi
import zone.ien.hig.default
import zone.ien.hig.icons.CupertinoIcons
import zone.ien.hig.icons.filled.Airtag
import zone.ien.hig.icons.filled.Alarm
import zone.ien.hig.icons.filled.Appletv
import zone.ien.hig.icons.filled.Archivebox
import zone.ien.hig.icons.filled.ArrowClockwiseCircle
import zone.ien.hig.icons.filled.ArrowCounterclockwiseCircle
import zone.ien.hig.icons.filled.ArrowCounterclockwiseIcloud
import zone.ien.hig.icons.filled.ArrowDownCircle
import zone.ien.hig.icons.filled.ArrowDownDoc
import zone.ien.hig.icons.filled.ArrowTriangle2CirclepathCamera
import zone.ien.hig.icons.filled.ArrowTriangle2CirclepathCircle
import zone.ien.hig.icons.filled.ArrowTurnUpForwardIphone
import zone.ien.hig.icons.filled.ArrowUpDoc
import zone.ien.hig.icons.filled.ArrowshapeTurnUpLeft
import zone.ien.hig.icons.filled.ArrowshapeTurnUpLeft2
import zone.ien.hig.icons.filled.Backward
import zone.ien.hig.icons.filled.BackwardEnd
import zone.ien.hig.icons.filled.Bag
import zone.ien.hig.icons.filled.BagBadgeMinus
import zone.ien.hig.icons.filled.BagBadgePlus
import zone.ien.hig.icons.filled.Balloon
import zone.ien.hig.icons.filled.Bandage
import zone.ien.hig.icons.filled.Banknote
import zone.ien.hig.icons.filled.Baseball
import zone.ien.hig.icons.filled.Basket
import zone.ien.hig.icons.filled.Basketball
import zone.ien.hig.icons.filled.BedDouble
import zone.ien.hig.icons.filled.Bell
import zone.ien.hig.icons.filled.BellAndWavesLeftAndRight
import zone.ien.hig.icons.filled.BellBadge
import zone.ien.hig.icons.filled.BellCircle
import zone.ien.hig.icons.filled.BellSlash
import zone.ien.hig.icons.filled.Binoculars
import zone.ien.hig.icons.filled.BirthdayCake
import zone.ien.hig.icons.filled.Bolt
import zone.ien.hig.icons.filled.BoltHorizontal
import zone.ien.hig.icons.filled.BoltSlash
import zone.ien.hig.icons.filled.Book
import zone.ien.hig.icons.filled.BookCircle
import zone.ien.hig.icons.filled.BookClosed
import zone.ien.hig.icons.filled.Bookmark
import zone.ien.hig.icons.filled.BookmarkSlash
import zone.ien.hig.icons.filled.Briefcase
import zone.ien.hig.icons.filled.BubbleLeft
import zone.ien.hig.icons.filled.BubbleRight
import zone.ien.hig.icons.filled.Building
import zone.ien.hig.icons.filled.Building2
import zone.ien.hig.icons.filled.Burst
import zone.ien.hig.icons.filled.Camera
import zone.ien.hig.icons.filled.CameraCircle
import zone.ien.hig.icons.filled.Capslock
import zone.ien.hig.icons.filled.Car
import zone.ien.hig.icons.filled.Cart
import zone.ien.hig.icons.filled.CartBadgeMinus
import zone.ien.hig.icons.filled.CartBadgePlus
import zone.ien.hig.icons.filled.Case
import zone.ien.hig.icons.filled.ChartBar
import zone.ien.hig.icons.filled.CheckmarkCircle
import zone.ien.hig.icons.filled.CheckmarkIcloud
import zone.ien.hig.icons.filled.CheckmarkMessage
import zone.ien.hig.icons.filled.CheckmarkSeal
import zone.ien.hig.icons.filled.CheckmarkShield
import zone.ien.hig.icons.filled.CheckmarkSquare
import zone.ien.hig.icons.filled.CircleLefthalfed
import zone.ien.hig.icons.filled.CircleRighthalfed
import zone.ien.hig.icons.filled.Clear
import zone.ien.hig.icons.filled.Clipboard
import zone.ien.hig.icons.filled.Clock
import zone.ien.hig.icons.filled.Cloud
import zone.ien.hig.icons.filled.Cone
import zone.ien.hig.icons.filled.Cpu
import zone.ien.hig.icons.filled.Creditcard
import zone.ien.hig.icons.filled.Cross
import zone.ien.hig.icons.filled.CrossCircle
import zone.ien.hig.icons.filled.CrossVial
import zone.ien.hig.icons.filled.Crown
import zone.ien.hig.icons.filled.Cube
import zone.ien.hig.icons.filled.CupAndSaucer
import zone.ien.hig.icons.filled.DeleteLeft
import zone.ien.hig.icons.filled.DeleteRight
import zone.ien.hig.icons.filled.Dice
import zone.ien.hig.icons.filled.Doc
import zone.ien.hig.icons.filled.DocBadgeArrowUp
import zone.ien.hig.icons.filled.DocBadgePlus
import zone.ien.hig.icons.filled.DocOnDoc
import zone.ien.hig.icons.filled.DocPlaintext
import zone.ien.hig.icons.filled.DocText
import zone.ien.hig.icons.filled.Drop
import zone.ien.hig.icons.filled.Ear
import zone.ien.hig.icons.filled.EllipsisBubble
import zone.ien.hig.icons.filled.EllipsisCircle
import zone.ien.hig.icons.filled.EllipsisMessage
import zone.ien.hig.icons.filled.Envelope
import zone.ien.hig.icons.filled.EnvelopeBadge
import zone.ien.hig.icons.filled.EnvelopeCircle
import zone.ien.hig.icons.filled.EnvelopeOpen
import zone.ien.hig.icons.filled.Eraser
import zone.ien.hig.icons.filled.ExclamationmarkCircle
import zone.ien.hig.icons.filled.ExclamationmarkIcloud
import zone.ien.hig.icons.filled.ExclamationmarkSquare
import zone.ien.hig.icons.filled.ExclamationmarkTriangle
import zone.ien.hig.icons.filled.Externaldrive
import zone.ien.hig.icons.filled.Eye
import zone.ien.hig.icons.filled.EyeSlash
import zone.ien.hig.icons.filled.Facemask
import zone.ien.hig.icons.filled.Fanblades
import zone.ien.hig.icons.filled.FanbladesSlash
import zone.ien.hig.icons.filled.Film
import zone.ien.hig.icons.filled.Flag
import zone.ien.hig.icons.filled.Flag2Crossed
import zone.ien.hig.icons.filled.FlagSlash
import zone.ien.hig.icons.filled.Flame
import zone.ien.hig.icons.filled.Folder
import zone.ien.hig.icons.filled.FolderBadgePlus
import zone.ien.hig.icons.filled.Football
import zone.ien.hig.icons.filled.ForkKnifeCircle
import zone.ien.hig.icons.filled.Forward
import zone.ien.hig.icons.filled.ForwardEnd
import zone.ien.hig.icons.filled.Fuelpump
import zone.ien.hig.icons.filled.Gamecontroller
import zone.ien.hig.icons.filled.Gearshape
import zone.ien.hig.icons.filled.Gearshape2
import zone.ien.hig.icons.filled.Gift
import zone.ien.hig.icons.filled.Giftcard
import zone.ien.hig.icons.filled.GlobeDesk
import zone.ien.hig.icons.filled.Graduationcap
import zone.ien.hig.icons.filled.Hammer
import zone.ien.hig.icons.filled.HandDraw
import zone.ien.hig.icons.filled.HandPointUp
import zone.ien.hig.icons.filled.HandPointUpLeft
import zone.ien.hig.icons.filled.HandRaised
import zone.ien.hig.icons.filled.HandRaisedSlash
import zone.ien.hig.icons.filled.HandTap
import zone.ien.hig.icons.filled.HandThumbsdown
import zone.ien.hig.icons.filled.HandThumbsup
import zone.ien.hig.icons.filled.HandWave
import zone.ien.hig.icons.filled.HandsSparkles
import zone.ien.hig.icons.filled.HeadphonesCircle
import zone.ien.hig.icons.filled.Heart
import zone.ien.hig.icons.filled.HeartCircle
import zone.ien.hig.icons.filled.HeartSlash
import zone.ien.hig.icons.filled.HeartTextSquare
import zone.ien.hig.icons.filled.Hifispeaker
import zone.ien.hig.icons.filled.Homepod
import zone.ien.hig.icons.filled.Homepodmini
import zone.ien.hig.icons.filled.House
import zone.ien.hig.icons.filled.Icloud
import zone.ien.hig.icons.filled.IcloudAndArrowDown
import zone.ien.hig.icons.filled.IcloudAndArrowUp
import zone.ien.hig.icons.filled.InfoBubble
import zone.ien.hig.icons.filled.InfoCircle
import zone.ien.hig.icons.filled.InfoSquare
import zone.ien.hig.icons.filled.Key
import zone.ien.hig.icons.filled.KeyIcloud
import zone.ien.hig.icons.filled.Keyboard
import zone.ien.hig.icons.filled.Lanyardcard
import zone.ien.hig.icons.filled.Leaf
import zone.ien.hig.icons.filled.Level
import zone.ien.hig.icons.filled.Lifepreserver
import zone.ien.hig.icons.filled.LightBeaconMax
import zone.ien.hig.icons.filled.Lightbulb
import zone.ien.hig.icons.filled.LightbulbSlash
import zone.ien.hig.icons.filled.LinkCircle
import zone.ien.hig.icons.filled.ListBulletCircle
import zone.ien.hig.icons.filled.ListBulletClipboard
import zone.ien.hig.icons.filled.ListClipboard
import zone.ien.hig.icons.filled.Location
import zone.ien.hig.icons.filled.Lock
import zone.ien.hig.icons.filled.LockCircle
import zone.ien.hig.icons.filled.LockOpen
import zone.ien.hig.icons.filled.LockSlash
import zone.ien.hig.icons.filled.Magazine
import zone.ien.hig.icons.filled.Mail
import zone.ien.hig.icons.filled.MailStack
import zone.ien.hig.icons.filled.Map
import zone.ien.hig.icons.filled.Medal
import zone.ien.hig.icons.filled.Megaphone
import zone.ien.hig.icons.filled.Menucard
import zone.ien.hig.icons.filled.Message
import zone.ien.hig.icons.filled.MessageBadgeed
import zone.ien.hig.icons.filled.Mic
import zone.ien.hig.icons.filled.MicSlash
import zone.ien.hig.icons.filled.MinusCircle
import zone.ien.hig.icons.filled.Moon
import zone.ien.hig.icons.filled.MoonStars
import zone.ien.hig.icons.filled.Mount
import zone.ien.hig.icons.filled.Newspaper
import zone.ien.hig.icons.filled.Opticaldisc
import zone.ien.hig.icons.filled.Paintbrush
import zone.ien.hig.icons.filled.PaintbrushPointed
import zone.ien.hig.icons.filled.Paintpalette
import zone.ien.hig.icons.filled.PaperclipCircle
import zone.ien.hig.icons.filled.Paperplane
import zone.ien.hig.icons.filled.PartyPopper
import zone.ien.hig.icons.filled.Pause
import zone.ien.hig.icons.filled.PauseCircle
import zone.ien.hig.icons.filled.Pawprint
import zone.ien.hig.icons.filled.PencilCircle
import zone.ien.hig.icons.filled.Person
import zone.ien.hig.icons.filled.Person2
import zone.ien.hig.icons.filled.PersonCircle
import zone.ien.hig.icons.filled.PersonCropCircle
import zone.ien.hig.icons.filled.PersonCropCircleBadgeMinus
import zone.ien.hig.icons.filled.PersonCropCircleBadgePlus
import zone.ien.hig.icons.filled.PersonCropSquare
import zone.ien.hig.icons.filled.PersonIcloud
import zone.ien.hig.icons.filled.PersonTextRectangle
import zone.ien.hig.icons.filled.PersonViewfinder
import zone.ien.hig.icons.filled.PersonWave2
import zone.ien.hig.icons.filled.Phone
import zone.ien.hig.icons.filled.PhoneAndWaveform
import zone.ien.hig.icons.filled.PhoneArrowDownLeft
import zone.ien.hig.icons.filled.PhoneArrowUpRight
import zone.ien.hig.icons.filled.PhoneBadgePlus
import zone.ien.hig.icons.filled.PhoneCircle
import zone.ien.hig.icons.filled.PhoneConnection
import zone.ien.hig.icons.filled.Photo
import zone.ien.hig.icons.filled.PhotoStack
import zone.ien.hig.icons.filled.Pill
import zone.ien.hig.icons.filled.Pin
import zone.ien.hig.icons.filled.PinCircle
import zone.ien.hig.icons.filled.PinSlash
import zone.ien.hig.icons.filled.Pip
import zone.ien.hig.icons.filled.Play
import zone.ien.hig.icons.filled.PlayCircle
import zone.ien.hig.icons.filled.PlusApp
import zone.ien.hig.icons.filled.PlusBubble
import zone.ien.hig.icons.filled.PlusCircle
import zone.ien.hig.icons.filled.PlusMessage
import zone.ien.hig.icons.filled.PlusSquare
import zone.ien.hig.icons.filled.Popcorn
import zone.ien.hig.icons.filled.PowerCircle
import zone.ien.hig.icons.filled.Printer
import zone.ien.hig.icons.filled.Puzzlepiece
import zone.ien.hig.icons.filled.PuzzlepieceExtension
import zone.ien.hig.icons.filled.QuestionmarkApp
import zone.ien.hig.icons.filled.QuestionmarkCircle
import zone.ien.hig.icons.filled.QuestionmarkFolder
import zone.ien.hig.icons.filled.QuestionmarkSquare
import zone.ien.hig.icons.filled.RecordCircle
import zone.ien.hig.icons.filled.RectanglePortraitAndArrowForward
import zone.ien.hig.icons.filled.RectangleStack
import zone.ien.hig.icons.filled.RotateLeft
import zone.ien.hig.icons.filled.RotateRight
import zone.ien.hig.icons.filled.Ruler
import zone.ien.hig.icons.filled.Safari
import zone.ien.hig.icons.filled.Scalemass
import zone.ien.hig.icons.filled.Scroll
import zone.ien.hig.icons.filled.ShazamLogo
import zone.ien.hig.icons.filled.Shield
import zone.ien.hig.icons.filled.ShieldLefthalfed
import zone.ien.hig.icons.filled.ShieldRighthalfed
import zone.ien.hig.icons.filled.ShieldSlash
import zone.ien.hig.icons.filled.Shippingbox
import zone.ien.hig.icons.filled.Shoeprints
import zone.ien.hig.icons.filled.Simcard
import zone.ien.hig.icons.filled.SmallcircleedCircle
import zone.ien.hig.icons.filled.Speaker
import zone.ien.hig.icons.filled.SpeakerMinus
import zone.ien.hig.icons.filled.SpeakerPlus
import zone.ien.hig.icons.filled.SpeakerSlash
import zone.ien.hig.icons.filled.SpeakerWave2
import zone.ien.hig.icons.filled.SquareAndArrowUp
import zone.ien.hig.icons.filled.SquareBottomthirdInseted
import zone.ien.hig.icons.filled.SquareOnSquare
import zone.ien.hig.icons.filled.SquareSplit1x2
import zone.ien.hig.icons.filled.SquareSplit2x1
import zone.ien.hig.icons.filled.SquareStack
import zone.ien.hig.icons.filled.SquareStack3dUp
import zone.ien.hig.icons.filled.SquareTopthirdInseted
import zone.ien.hig.icons.filled.Star
import zone.ien.hig.icons.filled.StarLeadinghalfed
import zone.ien.hig.icons.filled.StarSlash
import zone.ien.hig.icons.filled.Staroflife
import zone.ien.hig.icons.filled.Stop
import zone.ien.hig.icons.filled.StopCircle
import zone.ien.hig.icons.filled.Suitcase
import zone.ien.hig.icons.filled.SunMax
import zone.ien.hig.icons.filled.Tag
import zone.ien.hig.icons.filled.Terminal
import zone.ien.hig.icons.filled.TextBubble
import zone.ien.hig.icons.filled.Theatermasks
import zone.ien.hig.icons.filled.Trash
import zone.ien.hig.icons.filled.TrashSlash
import zone.ien.hig.icons.filled.TrayAndArrowDown
import zone.ien.hig.icons.filled.TrayAndArrowUp
import zone.ien.hig.icons.filled.Trophy
import zone.ien.hig.icons.filled.Tshirt
import zone.ien.hig.icons.filled.Tv
import zone.ien.hig.icons.filled.TvAndHifispeaker
import zone.ien.hig.icons.filled.Umbrella
import zone.ien.hig.icons.filled.Video
import zone.ien.hig.icons.filled.VideoCircle
import zone.ien.hig.icons.filled.VideoSlash
import zone.ien.hig.icons.filled.Volleyball
import zone.ien.hig.icons.filled.WalletPass
import zone.ien.hig.icons.filled.WebCamera
import zone.ien.hig.icons.filled.WifiRouter
import zone.ien.hig.icons.filled.Wineglass
import zone.ien.hig.icons.filled.XmarkApp
import zone.ien.hig.icons.filled.XmarkBin
import zone.ien.hig.icons.filled.XmarkCircle
import zone.ien.hig.icons.filled.XmarkIcloud
import zone.ien.hig.icons.filled.XmarkSeal
import zone.ien.hig.icons.filled.XmarkShield
import zone.ien.hig.icons.filled._4kTv
import zone.ien.hig.icons.outlined.Airplane
import zone.ien.hig.icons.outlined.AirplaneArrival
import zone.ien.hig.icons.outlined.AirplaneDeparture
import zone.ien.hig.icons.outlined.Airplayaudio
import zone.ien.hig.icons.outlined.Airpods
import zone.ien.hig.icons.outlined.AirpodsGen3
import zone.ien.hig.icons.outlined.Airpodsmax
import zone.ien.hig.icons.outlined.Airpodspro
import zone.ien.hig.icons.outlined.Airtag
import zone.ien.hig.icons.outlined.Alarm
import zone.ien.hig.icons.outlined.Alt
import zone.ien.hig.icons.outlined.Angle
import zone.ien.hig.icons.outlined.AntennaRadiowavesLeftAndRight
import zone.ien.hig.icons.outlined.AntennaRadiowavesLeftAndRightSlash
import zone.ien.hig.icons.outlined.AppleLogo
import zone.ien.hig.icons.outlined.Applepencil
import zone.ien.hig.icons.outlined.Appletv
import zone.ien.hig.icons.outlined.Applewatch
import zone.ien.hig.icons.outlined.ApplewatchRadiowavesLeftAndRight
import zone.ien.hig.icons.outlined.ApplewatchWatchface
import zone.ien.hig.icons.outlined.Archivebox
import zone.ien.hig.icons.outlined.Arrow3Trianglepath
import zone.ien.hig.icons.outlined.ArrowClockwise
import zone.ien.hig.icons.outlined.ArrowCounterclockwise
import zone.ien.hig.icons.outlined.ArrowCounterclockwiseIcloud
import zone.ien.hig.icons.outlined.ArrowDown
import zone.ien.hig.icons.outlined.ArrowDownAndLineHorizontalAndArrowUp
import zone.ien.hig.icons.outlined.ArrowDownCircle
import zone.ien.hig.icons.outlined.ArrowDownDoc
import zone.ien.hig.icons.outlined.ArrowDownRightAndArrowUpLeft
import zone.ien.hig.icons.outlined.ArrowDownToLine
import zone.ien.hig.icons.outlined.ArrowLeftAndRight
import zone.ien.hig.icons.outlined.ArrowLeftArrowRight
import zone.ien.hig.icons.outlined.ArrowTriangle2Circlepath
import zone.ien.hig.icons.outlined.ArrowTriangle2CirclepathCamera
import zone.ien.hig.icons.outlined.ArrowTriangleBranch
import zone.ien.hig.icons.outlined.ArrowTurnDownLeft
import zone.ien.hig.icons.outlined.ArrowTurnDownRight
import zone.ien.hig.icons.outlined.ArrowTurnRightUp
import zone.ien.hig.icons.outlined.ArrowTurnUpForwardIphone
import zone.ien.hig.icons.outlined.ArrowTurnUpLeft
import zone.ien.hig.icons.outlined.ArrowTurnUpRight
import zone.ien.hig.icons.outlined.ArrowUpAndDown
import zone.ien.hig.icons.outlined.ArrowUpArrowDown
import zone.ien.hig.icons.outlined.ArrowUpDoc
import zone.ien.hig.icons.outlined.ArrowUpLeftAndArrowDownRight
import zone.ien.hig.icons.outlined.ArrowUturnLeft
import zone.ien.hig.icons.outlined.ArrowUturnRight
import zone.ien.hig.icons.outlined.ArrowshapeTurnUpLeft
import zone.ien.hig.icons.outlined.ArrowshapeTurnUpLeft2
import zone.ien.hig.icons.outlined.At
import zone.ien.hig.icons.outlined.Backward
import zone.ien.hig.icons.outlined.BackwardEnd
import zone.ien.hig.icons.outlined.Bag
import zone.ien.hig.icons.outlined.BagBadgeMinus
import zone.ien.hig.icons.outlined.BagBadgePlus
import zone.ien.hig.icons.outlined.Balloon
import zone.ien.hig.icons.outlined.Bandage
import zone.ien.hig.icons.outlined.Banknote
import zone.ien.hig.icons.outlined.Barcode
import zone.ien.hig.icons.outlined.BarcodeViewfinder
import zone.ien.hig.icons.outlined.Baseball
import zone.ien.hig.icons.outlined.Basket
import zone.ien.hig.icons.outlined.Basketball
import zone.ien.hig.icons.outlined.Battery100
import zone.ien.hig.icons.outlined.BedDouble
import zone.ien.hig.icons.outlined.Bell
import zone.ien.hig.icons.outlined.BellAndWavesLeftAndRight
import zone.ien.hig.icons.outlined.BellBadge
import zone.ien.hig.icons.outlined.BellCircle
import zone.ien.hig.icons.outlined.BellSlash
import zone.ien.hig.icons.outlined.Bicycle
import zone.ien.hig.icons.outlined.Binoculars
import zone.ien.hig.icons.outlined.BirthdayCake
import zone.ien.hig.icons.outlined.Bitcoinsign
import zone.ien.hig.icons.outlined.Bolt
import zone.ien.hig.icons.outlined.BoltHorizontal
import zone.ien.hig.icons.outlined.BoltSlash
import zone.ien.hig.icons.outlined.Book
import zone.ien.hig.icons.outlined.BookCircle
import zone.ien.hig.icons.outlined.BookClosed
import zone.ien.hig.icons.outlined.Bookmark
import zone.ien.hig.icons.outlined.BookmarkSlash
import zone.ien.hig.icons.outlined.Brain
import zone.ien.hig.icons.outlined.BrainHeadProfile
import zone.ien.hig.icons.outlined.Briefcase
import zone.ien.hig.icons.outlined.BubbleLeft
import zone.ien.hig.icons.outlined.BubbleRight
import zone.ien.hig.icons.outlined.Building
import zone.ien.hig.icons.outlined.Building2
import zone.ien.hig.icons.outlined.Burn
import zone.ien.hig.icons.outlined.Burst
import zone.ien.hig.icons.outlined.CableConnector
import zone.ien.hig.icons.outlined.CableConnectorHorizontal
import zone.ien.hig.icons.outlined.Calendar
import zone.ien.hig.icons.outlined.CalendarBadgePlus
import zone.ien.hig.icons.outlined.Camera
import zone.ien.hig.icons.outlined.CameraCircle
import zone.ien.hig.icons.outlined.CameraFilters
import zone.ien.hig.icons.outlined.CameraViewfinder
import zone.ien.hig.icons.outlined.Candybarphone
import zone.ien.hig.icons.outlined.Capslock
import zone.ien.hig.icons.outlined.Car
import zone.ien.hig.icons.outlined.Cart
import zone.ien.hig.icons.outlined.CartBadgeMinus
import zone.ien.hig.icons.outlined.CartBadgePlus
import zone.ien.hig.icons.outlined.Case
import zone.ien.hig.icons.outlined.Centsign
import zone.ien.hig.icons.outlined.Character
import zone.ien.hig.icons.outlined.ChartBar
import zone.ien.hig.icons.outlined.ChartLineDowntrendXyaxis
import zone.ien.hig.icons.outlined.ChartLineUptrendXyaxis
import zone.ien.hig.icons.outlined.CheckerboardShield
import zone.ien.hig.icons.outlined.Checklist
import zone.ien.hig.icons.outlined.ChecklistChecked
import zone.ien.hig.icons.outlined.ChecklistUnchecked
import zone.ien.hig.icons.outlined.Checkmark
import zone.ien.hig.icons.outlined.CheckmarkCircle
import zone.ien.hig.icons.outlined.CheckmarkIcloud
import zone.ien.hig.icons.outlined.CheckmarkMessage
import zone.ien.hig.icons.outlined.CheckmarkSeal
import zone.ien.hig.icons.outlined.CheckmarkShield
import zone.ien.hig.icons.outlined.CheckmarkSquare
import zone.ien.hig.icons.outlined.ChevronBackward
import zone.ien.hig.icons.outlined.ChevronDown
import zone.ien.hig.icons.outlined.ChevronForward
import zone.ien.hig.icons.outlined.ChevronLeftForwardslashChevronRight
import zone.ien.hig.icons.outlined.ChevronUp
import zone.ien.hig.icons.outlined.Clear
import zone.ien.hig.icons.outlined.Clipboard
import zone.ien.hig.icons.outlined.Clock
import zone.ien.hig.icons.outlined.ClockArrowCirclepath
import zone.ien.hig.icons.outlined.Cloud
import zone.ien.hig.icons.outlined.Command
import zone.ien.hig.icons.outlined.CompassDrawing
import zone.ien.hig.icons.outlined.Cone
import zone.ien.hig.icons.outlined.Cpu
import zone.ien.hig.icons.outlined.Creditcard
import zone.ien.hig.icons.outlined.CreditcardTrianglebadgeExclamationmark
import zone.ien.hig.icons.outlined.Crop
import zone.ien.hig.icons.outlined.CropRotate
import zone.ien.hig.icons.outlined.Cross
import zone.ien.hig.icons.outlined.CrossCircle
import zone.ien.hig.icons.outlined.CrossVial
import zone.ien.hig.icons.outlined.Crown
import zone.ien.hig.icons.outlined.Cube
import zone.ien.hig.icons.outlined.CupAndSaucer
import zone.ien.hig.icons.outlined.Curlybraces
import zone.ien.hig.icons.outlined.CursorarrowRays
import zone.ien.hig.icons.outlined.DeleteLeft
import zone.ien.hig.icons.outlined.DeleteRight
import zone.ien.hig.icons.outlined.Desktopcomputer
import zone.ien.hig.icons.outlined.Dice
import zone.ien.hig.icons.outlined.Display
import zone.ien.hig.icons.outlined.Divide
import zone.ien.hig.icons.outlined.Doc
import zone.ien.hig.icons.outlined.DocBadgeArrowUp
import zone.ien.hig.icons.outlined.DocBadgePlus
import zone.ien.hig.icons.outlined.DocOnDoc
import zone.ien.hig.icons.outlined.DocPlaintext
import zone.ien.hig.icons.outlined.DocText
import zone.ien.hig.icons.outlined.DocTextMagnifyingglass
import zone.ien.hig.icons.outlined.Dollarsign
import zone.ien.hig.icons.outlined.DollarsignArrowCirclepath
import zone.ien.hig.icons.outlined.DoorLeftHandClosed
import zone.ien.hig.icons.outlined.DoorLeftHandOpen
import zone.ien.hig.icons.outlined.DotRadiowavesLeftAndRight
import zone.ien.hig.icons.outlined.DotRadiowavesUpForward
import zone.ien.hig.icons.outlined.Drop
import zone.ien.hig.icons.outlined.Ear
import zone.ien.hig.icons.outlined.Earpods
import zone.ien.hig.icons.outlined.Ellipsis
import zone.ien.hig.icons.outlined.EllipsisBubble
import zone.ien.hig.icons.outlined.EllipsisCircle
import zone.ien.hig.icons.outlined.EllipsisCurlybraces
import zone.ien.hig.icons.outlined.EllipsisMessage
import zone.ien.hig.icons.outlined.Envelope
import zone.ien.hig.icons.outlined.EnvelopeBadge
import zone.ien.hig.icons.outlined.EnvelopeCircle
import zone.ien.hig.icons.outlined.EnvelopeOpen
import zone.ien.hig.icons.outlined.Eraser
import zone.ien.hig.icons.outlined.Eurosign
import zone.ien.hig.icons.outlined.Exclamationmark
import zone.ien.hig.icons.outlined.Exclamationmark2
import zone.ien.hig.icons.outlined.Exclamationmark3
import zone.ien.hig.icons.outlined.ExclamationmarkArrowTriangle2Circlepath
import zone.ien.hig.icons.outlined.ExclamationmarkCircle
import zone.ien.hig.icons.outlined.ExclamationmarkIcloud
import zone.ien.hig.icons.outlined.ExclamationmarkSquare
import zone.ien.hig.icons.outlined.ExclamationmarkTriangle
import zone.ien.hig.icons.outlined.Externaldrive
import zone.ien.hig.icons.outlined.Eye
import zone.ien.hig.icons.outlined.EyeSlash
import zone.ien.hig.icons.outlined.Eyebrow
import zone.ien.hig.icons.outlined.Eyedropper
import zone.ien.hig.icons.outlined.Eyeglasses
import zone.ien.hig.icons.outlined.Eyes
import zone.ien.hig.icons.outlined.FaceSmiling
import zone.ien.hig.icons.outlined.FaceSmilingInverse
import zone.ien.hig.icons.outlined.Faceid
import zone.ien.hig.icons.outlined.Facemask
import zone.ien.hig.icons.outlined.Fanblades
import zone.ien.hig.icons.outlined.FanbladesSlash
import zone.ien.hig.icons.outlined.Fibrechannel
import zone.ien.hig.icons.outlined.FigureStand
import zone.ien.hig.icons.outlined.FigureWalk
import zone.ien.hig.icons.outlined.Film
import zone.ien.hig.icons.outlined.Flag
import zone.ien.hig.icons.outlined.Flag2Crossed
import zone.ien.hig.icons.outlined.FlagCheckered2Crossed
import zone.ien.hig.icons.outlined.FlagSlash
import zone.ien.hig.icons.outlined.Flame
import zone.ien.hig.icons.outlined.Flowchart
import zone.ien.hig.icons.outlined.Folder
import zone.ien.hig.icons.outlined.FolderBadgePlus
import zone.ien.hig.icons.outlined.Football
import zone.ien.hig.icons.outlined.ForkKnife
import zone.ien.hig.icons.outlined.ForkKnifeCircle
import zone.ien.hig.icons.outlined.Forward
import zone.ien.hig.icons.outlined.ForwardEnd
import zone.ien.hig.icons.outlined.Francsign
import zone.ien.hig.icons.outlined.Fuelpump
import zone.ien.hig.icons.outlined.Gamecontroller
import zone.ien.hig.icons.outlined.Gear
import zone.ien.hig.icons.outlined.Gearshape
import zone.ien.hig.icons.outlined.Gearshape2
import zone.ien.hig.icons.outlined.Gift
import zone.ien.hig.icons.outlined.Giftcard
import zone.ien.hig.icons.outlined.GlobeDesk
import zone.ien.hig.icons.outlined.Gobackward
import zone.ien.hig.icons.outlined.Goforward
import zone.ien.hig.icons.outlined.Graduationcap
import zone.ien.hig.icons.outlined.Grid
import zone.ien.hig.icons.outlined.Hammer
import zone.ien.hig.icons.outlined.HandDraw
import zone.ien.hig.icons.outlined.HandPointUp
import zone.ien.hig.icons.outlined.HandPointUpLeft
import zone.ien.hig.icons.outlined.HandRaised
import zone.ien.hig.icons.outlined.HandRaisedSlash
import zone.ien.hig.icons.outlined.HandTap
import zone.ien.hig.icons.outlined.HandThumbsdown
import zone.ien.hig.icons.outlined.HandThumbsup
import zone.ien.hig.icons.outlined.HandWave
import zone.ien.hig.icons.outlined.HandsSparkles
import zone.ien.hig.icons.outlined.Headphones
import zone.ien.hig.icons.outlined.HeadphonesCircle
import zone.ien.hig.icons.outlined.Heart
import zone.ien.hig.icons.outlined.HeartCircle
import zone.ien.hig.icons.outlined.HeartSlash
import zone.ien.hig.icons.outlined.HeartTextSquare
import zone.ien.hig.icons.outlined.Hifispeaker
import zone.ien.hig.icons.outlined.Highlighter
import zone.ien.hig.icons.outlined.Homekit
import zone.ien.hig.icons.outlined.Homepod
import zone.ien.hig.icons.outlined.Homepodmini
import zone.ien.hig.icons.outlined.Hourglass
import zone.ien.hig.icons.outlined.House
import zone.ien.hig.icons.outlined.Hryvniasign
import zone.ien.hig.icons.outlined.Icloud
import zone.ien.hig.icons.outlined.IcloudAndArrowDown
import zone.ien.hig.icons.outlined.IcloudAndArrowUp
import zone.ien.hig.icons.outlined.Infinity
import zone.ien.hig.icons.outlined.Info
import zone.ien.hig.icons.outlined.InfoBubble
import zone.ien.hig.icons.outlined.InfoCircle
import zone.ien.hig.icons.outlined.InfoSquare
import zone.ien.hig.icons.outlined.Ipad
import zone.ien.hig.icons.outlined.IpadAndIphone
import zone.ien.hig.icons.outlined.IpadHomebutton
import zone.ien.hig.icons.outlined.Iphone
import zone.ien.hig.icons.outlined.IphoneBadgePlay
import zone.ien.hig.icons.outlined.IphoneHomebutton
import zone.ien.hig.icons.outlined.IphoneHomebuttonRadiowavesLeftAndRight
import zone.ien.hig.icons.outlined.IphoneRadiowavesLeftAndRight
import zone.ien.hig.icons.outlined.Key
import zone.ien.hig.icons.outlined.KeyIcloud
import zone.ien.hig.icons.outlined.Keyboard
import zone.ien.hig.icons.outlined.Lanyardcard
import zone.ien.hig.icons.outlined.Laptopcomputer
import zone.ien.hig.icons.outlined.LaptopcomputerAndIpad
import zone.ien.hig.icons.outlined.LaptopcomputerAndIphone
import zone.ien.hig.icons.outlined.Leaf
import zone.ien.hig.icons.outlined.Level
import zone.ien.hig.icons.outlined.Lifepreserver
import zone.ien.hig.icons.outlined.LightBeaconMax
import zone.ien.hig.icons.outlined.LightMax
import zone.ien.hig.icons.outlined.LightMin
import zone.ien.hig.icons.outlined.Lightbulb
import zone.ien.hig.icons.outlined.LightbulbSlash
import zone.ien.hig.icons.outlined.Link
import zone.ien.hig.icons.outlined.LinkBadgePlus
import zone.ien.hig.icons.outlined.LinkCircle
import zone.ien.hig.icons.outlined.Lirasign
import zone.ien.hig.icons.outlined.ListBullet
import zone.ien.hig.icons.outlined.ListBulletCircle
import zone.ien.hig.icons.outlined.ListBulletClipboard
import zone.ien.hig.icons.outlined.ListBulletIndent
import zone.ien.hig.icons.outlined.ListClipboard
import zone.ien.hig.icons.outlined.ListNumber
import zone.ien.hig.icons.outlined.Livephoto
import zone.ien.hig.icons.outlined.Location
import zone.ien.hig.icons.outlined.Lock
import zone.ien.hig.icons.outlined.LockCircle
import zone.ien.hig.icons.outlined.LockOpen
import zone.ien.hig.icons.outlined.LockSlash
import zone.ien.hig.icons.outlined.Macwindow
import zone.ien.hig.icons.outlined.MacwindowBadgePlus
import zone.ien.hig.icons.outlined.Magazine
import zone.ien.hig.icons.outlined.Mail
import zone.ien.hig.icons.outlined.MailStack
import zone.ien.hig.icons.outlined.Map
import zone.ien.hig.icons.outlined.Mappin
import zone.ien.hig.icons.outlined.MappinAndEllipse
import zone.ien.hig.icons.outlined.MappinSlash
import zone.ien.hig.icons.outlined.Medal
import zone.ien.hig.icons.outlined.Megaphone
import zone.ien.hig.icons.outlined.Memories
import zone.ien.hig.icons.outlined.MenubarRectangle
import zone.ien.hig.icons.outlined.Menucard
import zone.ien.hig.icons.outlined.Message
import zone.ien.hig.icons.outlined.MessageBadge
import zone.ien.hig.icons.outlined.Mic
import zone.ien.hig.icons.outlined.MicSlash
import zone.ien.hig.icons.outlined.Minus
import zone.ien.hig.icons.outlined.MinusCircle
import zone.ien.hig.icons.outlined.MinusMagnifyingglass
import zone.ien.hig.icons.outlined.Moon
import zone.ien.hig.icons.outlined.MoonStars
import zone.ien.hig.icons.outlined.Mount
import zone.ien.hig.icons.outlined.Multiply
import zone.ien.hig.icons.outlined.MusicMic
import zone.ien.hig.icons.outlined.MusicNote
import zone.ien.hig.icons.outlined.MusicNoteList
import zone.ien.hig.icons.outlined.MusicQuarternote3
import zone.ien.hig.icons.outlined.Network
import zone.ien.hig.icons.outlined.Newspaper
import zone.ien.hig.icons.outlined.Nosign
import zone.ien.hig.icons.outlined.NoteText
import zone.ien.hig.icons.outlined.NoteTextBadgePlus
import zone.ien.hig.icons.outlined.Number
import zone.ien.hig.icons.outlined.Opticaldisc
import zone.ien.hig.icons.outlined.Option
import zone.ien.hig.icons.outlined.Paintbrush
import zone.ien.hig.icons.outlined.PaintbrushPointed
import zone.ien.hig.icons.outlined.Paintpalette
import zone.ien.hig.icons.outlined.Paperclip
import zone.ien.hig.icons.outlined.PaperclipBadgeEllipsis
import zone.ien.hig.icons.outlined.PaperclipCircle
import zone.ien.hig.icons.outlined.Paperplane
import zone.ien.hig.icons.outlined.Paragraphsign
import zone.ien.hig.icons.outlined.PartyPopper
import zone.ien.hig.icons.outlined.Pause
import zone.ien.hig.icons.outlined.PauseCircle
import zone.ien.hig.icons.outlined.Pawprint
import zone.ien.hig.icons.outlined.Pencil
import zone.ien.hig.icons.outlined.PencilCircle
import zone.ien.hig.icons.outlined.PencilTipCropCircle
import zone.ien.hig.icons.outlined.Percent
import zone.ien.hig.icons.outlined.Person
import zone.ien.hig.icons.outlined.Person2
import zone.ien.hig.icons.outlined.PersonAndBackgroundDotted
import zone.ien.hig.icons.outlined.PersonCircle
import zone.ien.hig.icons.outlined.PersonCropCircle
import zone.ien.hig.icons.outlined.PersonCropCircleBadgeMinus
import zone.ien.hig.icons.outlined.PersonCropCircleBadgePlus
import zone.ien.hig.icons.outlined.PersonCropSquare
import zone.ien.hig.icons.outlined.PersonIcloud
import zone.ien.hig.icons.outlined.PersonTextRectangle
import zone.ien.hig.icons.outlined.PersonWave2
import zone.ien.hig.icons.outlined.Personalhotspot
import zone.ien.hig.icons.outlined.Phone
import zone.ien.hig.icons.outlined.PhoneAndWaveform
import zone.ien.hig.icons.outlined.PhoneArrowDownLeft
import zone.ien.hig.icons.outlined.PhoneArrowUpRight
import zone.ien.hig.icons.outlined.PhoneBadgePlus
import zone.ien.hig.icons.outlined.PhoneCircle
import zone.ien.hig.icons.outlined.PhoneConnection
import zone.ien.hig.icons.outlined.Photo
import zone.ien.hig.icons.outlined.PhotoStack
import zone.ien.hig.icons.outlined.PhotoTv
import zone.ien.hig.icons.outlined.Pill
import zone.ien.hig.icons.outlined.Pin
import zone.ien.hig.icons.outlined.PinCircle
import zone.ien.hig.icons.outlined.PinSlash
import zone.ien.hig.icons.outlined.Pip
import zone.ien.hig.icons.outlined.PipEnter
import zone.ien.hig.icons.outlined.PipExit
import zone.ien.hig.icons.outlined.Play
import zone.ien.hig.icons.outlined.PlayCircle
import zone.ien.hig.icons.outlined.PlayDisplay
import zone.ien.hig.icons.outlined.Plus
import zone.ien.hig.icons.outlined.PlusApp
import zone.ien.hig.icons.outlined.PlusBubble
import zone.ien.hig.icons.outlined.PlusCircle
import zone.ien.hig.icons.outlined.PlusMagnifyingglass
import zone.ien.hig.icons.outlined.PlusMessage
import zone.ien.hig.icons.outlined.PlusSquare
import zone.ien.hig.icons.outlined.PlusViewfinder
import zone.ien.hig.icons.outlined.Popcorn
import zone.ien.hig.icons.outlined.Power
import zone.ien.hig.icons.outlined.PowerCircle
import zone.ien.hig.icons.outlined.Printer
import zone.ien.hig.icons.outlined.Puzzlepiece
import zone.ien.hig.icons.outlined.PuzzlepieceExtension
import zone.ien.hig.icons.outlined.Qrcode
import zone.ien.hig.icons.outlined.QrcodeViewfinder
import zone.ien.hig.icons.outlined.Questionmark
import zone.ien.hig.icons.outlined.QuestionmarkApp
import zone.ien.hig.icons.outlined.QuestionmarkCircle
import zone.ien.hig.icons.outlined.QuestionmarkFolder
import zone.ien.hig.icons.outlined.QuestionmarkSquare
import zone.ien.hig.icons.outlined.QuoteClosing
import zone.ien.hig.icons.outlined.QuoteOpening
import zone.ien.hig.icons.outlined.Rays
import zone.ien.hig.icons.outlined.RecordCircle
import zone.ien.hig.icons.outlined.Recordingtape
import zone.ien.hig.icons.outlined.RectangleArrowtriangle2Outward
import zone.ien.hig.icons.outlined.RectangleConnectedToLineBelow
import zone.ien.hig.icons.outlined.RectanglePortraitAndArrowForward
import zone.ien.hig.icons.outlined.RectanglePortraitArrowtriangle2Outward
import zone.ien.hig.icons.outlined.RectangleStack
import zone.ien.hig.icons.outlined.Repeat
import zone.ien.hig.icons.outlined.Rosette
import zone.ien.hig.icons.outlined.Rotate3d
import zone.ien.hig.icons.outlined.RotateLeft
import zone.ien.hig.icons.outlined.RotateRight
import zone.ien.hig.icons.outlined.Rublesign
import zone.ien.hig.icons.outlined.Ruler
import zone.ien.hig.icons.outlined.Safari
import zone.ien.hig.icons.outlined.Scalemass
import zone.ien.hig.icons.outlined.Scissors
import zone.ien.hig.icons.outlined.Scope
import zone.ien.hig.icons.outlined.Scribble
import zone.ien.hig.icons.outlined.ScribbleVariable
import zone.ien.hig.icons.outlined.Scroll
import zone.ien.hig.icons.outlined.ServerRack
import zone.ien.hig.icons.outlined.Shareplay
import zone.ien.hig.icons.outlined.ShareplaySlash
import zone.ien.hig.icons.outlined.ShazamLogo
import zone.ien.hig.icons.outlined.Shield
import zone.ien.hig.icons.outlined.ShieldSlash
import zone.ien.hig.icons.outlined.Shippingbox
import zone.ien.hig.icons.outlined.Shuffle
import zone.ien.hig.icons.outlined.SidebarLeft
import zone.ien.hig.icons.outlined.SidebarRight
import zone.ien.hig.icons.outlined.Simcard
import zone.ien.hig.icons.outlined.Skew
import zone.ien.hig.icons.outlined.SliderHorizontal3
import zone.ien.hig.icons.outlined.SliderVertical3
import zone.ien.hig.icons.outlined.Snowflake
import zone.ien.hig.icons.outlined.Soccerball
import zone.ien.hig.icons.outlined.Space
import zone.ien.hig.icons.outlined.Sparkle
import zone.ien.hig.icons.outlined.Sparkles
import zone.ien.hig.icons.outlined.Speaker
import zone.ien.hig.icons.outlined.SpeakerMinus
import zone.ien.hig.icons.outlined.SpeakerPlus
import zone.ien.hig.icons.outlined.SpeakerSlash
import zone.ien.hig.icons.outlined.SpeakerWave2
import zone.ien.hig.icons.outlined.Speedometer
import zone.ien.hig.icons.outlined.Square3Layers3dDownLeft
import zone.ien.hig.icons.outlined.Square3Layers3dDownRight
import zone.ien.hig.icons.outlined.SquareAndArrowUp
import zone.ien.hig.icons.outlined.SquareAndPencil
import zone.ien.hig.icons.outlined.SquareOnSquare
import zone.ien.hig.icons.outlined.SquareSplit1x2
import zone.ien.hig.icons.outlined.SquareSplit2x1
import zone.ien.hig.icons.outlined.SquareStack
import zone.ien.hig.icons.outlined.SquareStack3dUp
import zone.ien.hig.icons.outlined.Star
import zone.ien.hig.icons.outlined.StarSlash
import zone.ien.hig.icons.outlined.Staroflife
import zone.ien.hig.icons.outlined.Sterlingsign
import zone.ien.hig.icons.outlined.Stethoscope
import zone.ien.hig.icons.outlined.Stop
import zone.ien.hig.icons.outlined.StopCircle
import zone.ien.hig.icons.outlined.Suitcase
import zone.ien.hig.icons.outlined.Sum
import zone.ien.hig.icons.outlined.SunMax
import zone.ien.hig.icons.outlined.Swift
import zone.ien.hig.icons.outlined.Tag
import zone.ien.hig.icons.outlined.Target
import zone.ien.hig.icons.outlined.TennisRacket
import zone.ien.hig.icons.outlined.Terminal
import zone.ien.hig.icons.outlined.TextBubble
import zone.ien.hig.icons.outlined.TextMagnifyingglass
import zone.ien.hig.icons.outlined.Theatermasks
import zone.ien.hig.icons.outlined.Timer
import zone.ien.hig.icons.outlined.Touchid
import zone.ien.hig.icons.outlined.Trash
import zone.ien.hig.icons.outlined.TrashSlash
import zone.ien.hig.icons.outlined.TrayAndArrowDown
import zone.ien.hig.icons.outlined.TrayAndArrowUp
import zone.ien.hig.icons.outlined.Trophy
import zone.ien.hig.icons.outlined.Tshirt
import zone.ien.hig.icons.outlined.Tv
import zone.ien.hig.icons.outlined.Umbrella
import zone.ien.hig.icons.outlined.Video
import zone.ien.hig.icons.outlined.VideoCircle
import zone.ien.hig.icons.outlined.VideoSlash
import zone.ien.hig.icons.outlined.Volleyball
import zone.ien.hig.icons.outlined.WalletPass
import zone.ien.hig.icons.outlined.WandAndStars
import zone.ien.hig.icons.outlined.WandAndStarsInverse
import zone.ien.hig.icons.outlined.Waveform
import zone.ien.hig.icons.outlined.WaveformAndMagnifyingglass
import zone.ien.hig.icons.outlined.WaveformAndMic
import zone.ien.hig.icons.outlined.WaveformPathEcg
import zone.ien.hig.icons.outlined.WebCamera
import zone.ien.hig.icons.outlined.Wifi
import zone.ien.hig.icons.outlined.WifiExclamationmark
import zone.ien.hig.icons.outlined.WifiRouter
import zone.ien.hig.icons.outlined.WifiSlash
import zone.ien.hig.icons.outlined.Wind
import zone.ien.hig.icons.outlined.Wineglass
import zone.ien.hig.icons.outlined.WrenchAndScrewdriver
import zone.ien.hig.icons.outlined.Xmark
import zone.ien.hig.icons.outlined.XmarkApp
import zone.ien.hig.icons.outlined.XmarkBin
import zone.ien.hig.icons.outlined.XmarkCircle
import zone.ien.hig.icons.outlined.XmarkIcloud
import zone.ien.hig.icons.outlined.XmarkSeal
import zone.ien.hig.icons.outlined.XmarkShield
import zone.ien.hig.icons.outlined.Yensign
import zone.ien.hig.icons.outlined.Zzz
import zone.ien.hig.icons.outlined._4kTv
import zone.ien.hig.utils.rememberDefaultBackdrop

@OptIn(ExperimentalCupertinoApi::class, ExperimentalAdaptiveApi::class)
@Composable
fun IconsScreen(
    navigateBack: () -> Unit
) {
    val backdrop = rememberDefaultBackdrop()
    var isOutlined by remember { mutableStateOf(true) }
    val pagerState = rememberPagerState { 2 }

    LaunchedEffect(isOutlined){
        pagerState.animateScrollToPage(if (isOutlined) 0 else 1)
    }

    CupertinoScaffold(
        topBar = {
            CupertinoTopAppBar(
                navigationIcon = {
                    AdaptiveWidget(
                        cupertino = {
                            CupertinoNavigateBackLiquidButton(
                                onClick = navigateBack,
                                backdrop = backdrop,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        },
                        material = {
                            IconButton(
                                onClick = navigateBack
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    )
                },
                title = {
                    CupertinoSegmentedControl(
                        modifier = Modifier
                            .width(200.dp),
                        selectedTabIndex = if (isOutlined) 0 else 1,
                        backdrop = backdrop,
                    ) {
                        CupertinoSegmentedControlTab(
                            isSelected = isOutlined,
                            onClick = {
                                isOutlined = true
                            }
                        ) {
                            CupertinoText("Outlined")
                        }
                        CupertinoSegmentedControlTab(
                            isSelected = !isOutlined,
                            onClick = {
                                isOutlined = false

                            }
                        ) {
                            CupertinoText("Filled")
                        }
                    }
                },
                backdrop = backdrop
            )
        }
    ) { pv ->
        var selectedIcon by remember { mutableStateOf<ImageVector?>(null) }
        if (selectedIcon != null) {
            CupertinoAlertDialog(
                title = {
                    CupertinoIcon(
                        imageVector = selectedIcon!!,
                        contentDescription = null
                    )
                },
                message = {
                    CupertinoText(
                        "CupertinoIcon.${if (isOutlined) "Outlined" else "Filled"}.${selectedIcon!!.name}"
                    )
                },
                onDismissRequest = {
                    selectedIcon = null
                }
            ){
                default(onClick = {
                    selectedIcon = null
                }){
                    CupertinoText("Close")
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
            modifier = Modifier.layerBackdrop(backdrop)
        ) { page ->
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Adaptive(48.dp),
                contentPadding = pv
            ) {
                items(if (page == 0) Outlined else Filled) {
                    CupertinoIconButton(
                        onClick = {
                            selectedIcon = it
                        }
                    ) {
                        CupertinoIcon(
                            imageVector = it,
                            contentDescription = it.name
                        )
                    }
                }
            }
        }
    }
}

private val Outlined = listOf(
    CupertinoIcons.Outlined.Airplane,
    CupertinoIcons.Outlined.AirplaneArrival,
    CupertinoIcons.Outlined.AirplaneDeparture,
    CupertinoIcons.Outlined.Airplayaudio,
    CupertinoIcons.Outlined.Airpods,
    CupertinoIcons.Outlined.AirpodsGen3,
    CupertinoIcons.Outlined.Airpodsmax,
    CupertinoIcons.Outlined.Airpodspro,
    CupertinoIcons.Outlined.Airtag,
    CupertinoIcons.Outlined.Alarm,
    CupertinoIcons.Outlined.Alt,
    CupertinoIcons.Outlined.Angle,
    CupertinoIcons.Outlined.AntennaRadiowavesLeftAndRight,
    CupertinoIcons.Outlined.AntennaRadiowavesLeftAndRightSlash,
    CupertinoIcons.Outlined.AppleLogo,
    CupertinoIcons.Outlined.Applepencil,
    CupertinoIcons.Outlined.Appletv,
    CupertinoIcons.Outlined.Applewatch,
    CupertinoIcons.Outlined.ApplewatchRadiowavesLeftAndRight,
    CupertinoIcons.Outlined.ApplewatchWatchface,
    CupertinoIcons.Outlined.Archivebox,
    CupertinoIcons.Outlined.Arrow3Trianglepath,
    CupertinoIcons.Outlined.ArrowClockwise,
    CupertinoIcons.Outlined.ArrowCounterclockwise,
    CupertinoIcons.Outlined.ArrowCounterclockwiseIcloud,
    CupertinoIcons.Outlined.ArrowDown,
    CupertinoIcons.Outlined.ArrowDownAndLineHorizontalAndArrowUp,
    CupertinoIcons.Outlined.ArrowDownCircle,
    CupertinoIcons.Outlined.ArrowDownDoc,
    CupertinoIcons.Outlined.ArrowDownRightAndArrowUpLeft,
    CupertinoIcons.Outlined.ArrowDownToLine,
    CupertinoIcons.Outlined.ArrowLeftAndRight,
    CupertinoIcons.Outlined.ArrowLeftArrowRight,
    CupertinoIcons.Outlined.ArrowTriangle2Circlepath,
    CupertinoIcons.Outlined.ArrowTriangle2CirclepathCamera,
    CupertinoIcons.Outlined.ArrowTriangleBranch,
    CupertinoIcons.Outlined.ArrowTurnDownLeft,
    CupertinoIcons.Outlined.ArrowTurnDownRight,
    CupertinoIcons.Outlined.ArrowTurnRightUp,
    CupertinoIcons.Outlined.ArrowTurnUpForwardIphone,
    CupertinoIcons.Outlined.ArrowTurnUpLeft,
    CupertinoIcons.Outlined.ArrowTurnUpRight,
    CupertinoIcons.Outlined.ArrowUpAndDown,
    CupertinoIcons.Outlined.ArrowUpArrowDown,
    CupertinoIcons.Outlined.ArrowUpDoc,
    CupertinoIcons.Outlined.ArrowUpLeftAndArrowDownRight,
    CupertinoIcons.Outlined.ArrowUturnLeft,
    CupertinoIcons.Outlined.ArrowUturnRight,
    CupertinoIcons.Outlined.ArrowshapeTurnUpLeft,
    CupertinoIcons.Outlined.ArrowshapeTurnUpLeft2,
    CupertinoIcons.Outlined.At,
    CupertinoIcons.Outlined.Backward,
    CupertinoIcons.Outlined.BackwardEnd,
    CupertinoIcons.Outlined.Bag,
    CupertinoIcons.Outlined.BagBadgeMinus,
    CupertinoIcons.Outlined.BagBadgePlus,
    CupertinoIcons.Outlined.Balloon,
    CupertinoIcons.Outlined.Bandage,
    CupertinoIcons.Outlined.Banknote,
    CupertinoIcons.Outlined.Barcode,
    CupertinoIcons.Outlined.BarcodeViewfinder,
    CupertinoIcons.Outlined.Baseball,
    CupertinoIcons.Outlined.Basket,
    CupertinoIcons.Outlined.Basketball,
    CupertinoIcons.Outlined.Battery100,
    CupertinoIcons.Outlined.BedDouble,
    CupertinoIcons.Outlined.Bell,
    CupertinoIcons.Outlined.BellAndWavesLeftAndRight,
    CupertinoIcons.Outlined.BellBadge,
    CupertinoIcons.Outlined.BellCircle,
    CupertinoIcons.Outlined.BellSlash,
    CupertinoIcons.Outlined.Bicycle,
    CupertinoIcons.Outlined.Binoculars,
    CupertinoIcons.Outlined.BirthdayCake,
    CupertinoIcons.Outlined.Bitcoinsign,
    CupertinoIcons.Outlined.Bolt,
    CupertinoIcons.Outlined.BoltHorizontal,
    CupertinoIcons.Outlined.BoltSlash,
    CupertinoIcons.Outlined.Book,
    CupertinoIcons.Outlined.BookCircle,
    CupertinoIcons.Outlined.BookClosed,
    CupertinoIcons.Outlined.Bookmark,
    CupertinoIcons.Outlined.BookmarkSlash,
    CupertinoIcons.Outlined.Brain,
    CupertinoIcons.Outlined.BrainHeadProfile,
    CupertinoIcons.Outlined.Briefcase,
    CupertinoIcons.Outlined.BubbleLeft,
    CupertinoIcons.Outlined.BubbleRight,
    CupertinoIcons.Outlined.Building,
    CupertinoIcons.Outlined.Building2,
    CupertinoIcons.Outlined.Burn,
    CupertinoIcons.Outlined.Burst,
    CupertinoIcons.Outlined.CableConnector,
    CupertinoIcons.Outlined.CableConnectorHorizontal,
    CupertinoIcons.Outlined.Calendar,
    CupertinoIcons.Outlined.CalendarBadgePlus,
    CupertinoIcons.Outlined.Camera,
    CupertinoIcons.Outlined.CameraCircle,
    CupertinoIcons.Outlined.CameraFilters,
    CupertinoIcons.Outlined.CameraViewfinder,
    CupertinoIcons.Outlined.Candybarphone,
    CupertinoIcons.Outlined.Capslock,
    CupertinoIcons.Outlined.Car,
    CupertinoIcons.Outlined.Cart,
    CupertinoIcons.Outlined.CartBadgeMinus,
    CupertinoIcons.Outlined.CartBadgePlus,
    CupertinoIcons.Outlined.Case,
    CupertinoIcons.Outlined.Centsign,
    CupertinoIcons.Outlined.Character,
    CupertinoIcons.Outlined.ChartBar,
    CupertinoIcons.Outlined.ChartLineDowntrendXyaxis,
    CupertinoIcons.Outlined.ChartLineUptrendXyaxis,
    CupertinoIcons.Outlined.CheckerboardShield,
    CupertinoIcons.Outlined.Checklist,
    CupertinoIcons.Outlined.ChecklistChecked,
    CupertinoIcons.Outlined.ChecklistUnchecked,
    CupertinoIcons.Outlined.Checkmark,
    CupertinoIcons.Outlined.CheckmarkCircle,
    CupertinoIcons.Outlined.CheckmarkIcloud,
    CupertinoIcons.Outlined.CheckmarkMessage,
    CupertinoIcons.Outlined.CheckmarkSeal,
    CupertinoIcons.Outlined.CheckmarkShield,
    CupertinoIcons.Outlined.CheckmarkSquare,
    CupertinoIcons.Outlined.ChevronBackward,
    CupertinoIcons.Outlined.ChevronDown,
    CupertinoIcons.Outlined.ChevronForward,
    CupertinoIcons.Outlined.ChevronLeftForwardslashChevronRight,
    CupertinoIcons.Outlined.ChevronUp,
    CupertinoIcons.Outlined.Clear,
    CupertinoIcons.Outlined.Clipboard,
    CupertinoIcons.Outlined.Clock,
    CupertinoIcons.Outlined.ClockArrowCirclepath,
    CupertinoIcons.Outlined.Cloud,
    CupertinoIcons.Outlined.Command,
    CupertinoIcons.Outlined.CompassDrawing,
    CupertinoIcons.Outlined.Cone,
    CupertinoIcons.Outlined.Cpu,
    CupertinoIcons.Outlined.Creditcard,
    CupertinoIcons.Outlined.CreditcardTrianglebadgeExclamationmark,
    CupertinoIcons.Outlined.Crop,
    CupertinoIcons.Outlined.CropRotate,
    CupertinoIcons.Outlined.Cross,
    CupertinoIcons.Outlined.CrossCircle,
    CupertinoIcons.Outlined.CrossVial,
    CupertinoIcons.Outlined.Crown,
    CupertinoIcons.Outlined.Cube,
    CupertinoIcons.Outlined.CupAndSaucer,
    CupertinoIcons.Outlined.Curlybraces,
    CupertinoIcons.Outlined.CursorarrowRays,
    CupertinoIcons.Outlined.DeleteLeft,
    CupertinoIcons.Outlined.DeleteRight,
    CupertinoIcons.Outlined.Desktopcomputer,
    CupertinoIcons.Outlined.Dice,
    CupertinoIcons.Outlined.Display,
    CupertinoIcons.Outlined.Divide,
    CupertinoIcons.Outlined.Doc,
    CupertinoIcons.Outlined.DocBadgeArrowUp,
    CupertinoIcons.Outlined.DocBadgePlus,
    CupertinoIcons.Outlined.DocOnDoc,
    CupertinoIcons.Outlined.DocPlaintext,
    CupertinoIcons.Outlined.DocText,
    CupertinoIcons.Outlined.DocTextMagnifyingglass,
    CupertinoIcons.Outlined.Dollarsign,
    CupertinoIcons.Outlined.DollarsignArrowCirclepath,
    CupertinoIcons.Outlined.DoorLeftHandClosed,
    CupertinoIcons.Outlined.DoorLeftHandOpen,
    CupertinoIcons.Outlined.DotRadiowavesLeftAndRight,
    CupertinoIcons.Outlined.DotRadiowavesUpForward,
    CupertinoIcons.Outlined.Drop,
    CupertinoIcons.Outlined.Ear,
    CupertinoIcons.Outlined.Earpods,
    CupertinoIcons.Outlined.Ellipsis,
    CupertinoIcons.Outlined.EllipsisBubble,
    CupertinoIcons.Outlined.EllipsisCircle,
    CupertinoIcons.Outlined.EllipsisCurlybraces,
    CupertinoIcons.Outlined.EllipsisMessage,
    CupertinoIcons.Outlined.Envelope,
    CupertinoIcons.Outlined.EnvelopeBadge,
    CupertinoIcons.Outlined.EnvelopeCircle,
    CupertinoIcons.Outlined.EnvelopeOpen,
    CupertinoIcons.Outlined.Eraser,
    CupertinoIcons.Outlined.Eurosign,
    CupertinoIcons.Outlined.Exclamationmark,
    CupertinoIcons.Outlined.Exclamationmark2,
    CupertinoIcons.Outlined.Exclamationmark3,
    CupertinoIcons.Outlined.ExclamationmarkArrowTriangle2Circlepath,
    CupertinoIcons.Outlined.ExclamationmarkCircle,
    CupertinoIcons.Outlined.ExclamationmarkIcloud,
    CupertinoIcons.Outlined.ExclamationmarkSquare,
    CupertinoIcons.Outlined.ExclamationmarkTriangle,
    CupertinoIcons.Outlined.Externaldrive,
    CupertinoIcons.Outlined.Eye,
    CupertinoIcons.Outlined.EyeSlash,
    CupertinoIcons.Outlined.Eyebrow,
    CupertinoIcons.Outlined.Eyedropper,
    CupertinoIcons.Outlined.Eyeglasses,
    CupertinoIcons.Outlined.Eyes,
    CupertinoIcons.Outlined.FaceSmiling,
    CupertinoIcons.Outlined.FaceSmilingInverse,
    CupertinoIcons.Outlined.Faceid,
    CupertinoIcons.Outlined.Facemask,
    CupertinoIcons.Outlined.Fanblades,
    CupertinoIcons.Outlined.FanbladesSlash,
    CupertinoIcons.Outlined.Fibrechannel,
    CupertinoIcons.Outlined.FigureStand,
    CupertinoIcons.Outlined.FigureWalk,
    CupertinoIcons.Outlined.Film,
    CupertinoIcons.Outlined.Flag,
    CupertinoIcons.Outlined.Flag2Crossed,
    CupertinoIcons.Outlined.FlagCheckered2Crossed,
    CupertinoIcons.Outlined.FlagSlash,
    CupertinoIcons.Outlined.Flame,
    CupertinoIcons.Outlined.Flowchart,
    CupertinoIcons.Outlined.Folder,
    CupertinoIcons.Outlined.FolderBadgePlus,
    CupertinoIcons.Outlined.Football,
    CupertinoIcons.Outlined.ForkKnife,
    CupertinoIcons.Outlined.ForkKnifeCircle,
    CupertinoIcons.Outlined.Forward,
    CupertinoIcons.Outlined.ForwardEnd,
    CupertinoIcons.Outlined.Francsign,
    CupertinoIcons.Outlined.Fuelpump,
    CupertinoIcons.Outlined.Gamecontroller,
    CupertinoIcons.Outlined.Gear,
    CupertinoIcons.Outlined.Gearshape,
    CupertinoIcons.Outlined.Gearshape2,
    CupertinoIcons.Outlined.Gift,
    CupertinoIcons.Outlined.Giftcard,
    CupertinoIcons.Outlined.GlobeDesk,
    CupertinoIcons.Outlined.Gobackward,
    CupertinoIcons.Outlined.Goforward,
    CupertinoIcons.Outlined.Graduationcap,
    CupertinoIcons.Outlined.Grid,
    CupertinoIcons.Outlined.Hammer,
    CupertinoIcons.Outlined.HandDraw,
    CupertinoIcons.Outlined.HandPointUp,
    CupertinoIcons.Outlined.HandPointUpLeft,
    CupertinoIcons.Outlined.HandRaised,
    CupertinoIcons.Outlined.HandRaisedSlash,
    CupertinoIcons.Outlined.HandTap,
    CupertinoIcons.Outlined.HandThumbsdown,
    CupertinoIcons.Outlined.HandThumbsup,
    CupertinoIcons.Outlined.HandWave,
    CupertinoIcons.Outlined.HandsSparkles,
    CupertinoIcons.Outlined.Headphones,
    CupertinoIcons.Outlined.HeadphonesCircle,
    CupertinoIcons.Outlined.Heart,
    CupertinoIcons.Outlined.HeartCircle,
    CupertinoIcons.Outlined.HeartSlash,
    CupertinoIcons.Outlined.HeartTextSquare,
    CupertinoIcons.Outlined.Hifispeaker,
    CupertinoIcons.Outlined.Highlighter,
    CupertinoIcons.Outlined.Homekit,
    CupertinoIcons.Outlined.Homepod,
    CupertinoIcons.Outlined.Homepodmini,
    CupertinoIcons.Outlined.Hourglass,
    CupertinoIcons.Outlined.House,
    CupertinoIcons.Outlined.Hryvniasign,
    CupertinoIcons.Outlined.Icloud,
    CupertinoIcons.Outlined.IcloudAndArrowDown,
    CupertinoIcons.Outlined.IcloudAndArrowUp,
    CupertinoIcons.Outlined.Infinity,
    CupertinoIcons.Outlined.Info,
    CupertinoIcons.Outlined.InfoBubble,
    CupertinoIcons.Outlined.InfoCircle,
    CupertinoIcons.Outlined.InfoSquare,
    CupertinoIcons.Outlined.Ipad,
    CupertinoIcons.Outlined.IpadAndIphone,
    CupertinoIcons.Outlined.IpadHomebutton,
    CupertinoIcons.Outlined.Iphone,
    CupertinoIcons.Outlined.IphoneBadgePlay,
    CupertinoIcons.Outlined.IphoneHomebutton,
    CupertinoIcons.Outlined.IphoneHomebuttonRadiowavesLeftAndRight,
    CupertinoIcons.Outlined.IphoneRadiowavesLeftAndRight,
    CupertinoIcons.Outlined.Key,
    CupertinoIcons.Outlined.KeyIcloud,
    CupertinoIcons.Outlined.Keyboard,
    CupertinoIcons.Outlined.Lanyardcard,
    CupertinoIcons.Outlined.Laptopcomputer,
    CupertinoIcons.Outlined.LaptopcomputerAndIpad,
    CupertinoIcons.Outlined.LaptopcomputerAndIphone,
    CupertinoIcons.Outlined.Leaf,
    CupertinoIcons.Outlined.Level,
    CupertinoIcons.Outlined.Lifepreserver,
    CupertinoIcons.Outlined.LightBeaconMax,
    CupertinoIcons.Outlined.LightMax,
    CupertinoIcons.Outlined.LightMin,
    CupertinoIcons.Outlined.Lightbulb,
    CupertinoIcons.Outlined.LightbulbSlash,
    CupertinoIcons.Outlined.Link,
    CupertinoIcons.Outlined.LinkBadgePlus,
    CupertinoIcons.Outlined.LinkCircle,
    CupertinoIcons.Outlined.Lirasign,
    CupertinoIcons.Outlined.ListBullet,
    CupertinoIcons.Outlined.ListBulletCircle,
    CupertinoIcons.Outlined.ListBulletClipboard,
    CupertinoIcons.Outlined.ListBulletIndent,
    CupertinoIcons.Outlined.ListClipboard,
    CupertinoIcons.Outlined.ListNumber,
    CupertinoIcons.Outlined.Livephoto,
    CupertinoIcons.Outlined.Location,
    CupertinoIcons.Outlined.Lock,
    CupertinoIcons.Outlined.LockCircle,
    CupertinoIcons.Outlined.LockOpen,
    CupertinoIcons.Outlined.LockSlash,
    CupertinoIcons.Outlined.Macwindow,
    CupertinoIcons.Outlined.MacwindowBadgePlus,
    CupertinoIcons.Outlined.Magazine,
    CupertinoIcons.Outlined.Mail,
    CupertinoIcons.Outlined.MailStack,
    CupertinoIcons.Outlined.Map,
    CupertinoIcons.Outlined.Mappin,
    CupertinoIcons.Outlined.MappinAndEllipse,
    CupertinoIcons.Outlined.MappinSlash,
    CupertinoIcons.Outlined.Medal,
    CupertinoIcons.Outlined.Megaphone,
    CupertinoIcons.Outlined.Memories,
    CupertinoIcons.Outlined.MenubarRectangle,
    CupertinoIcons.Outlined.Menucard,
    CupertinoIcons.Outlined.Message,
    CupertinoIcons.Outlined.MessageBadge,
    CupertinoIcons.Outlined.Mic,
    CupertinoIcons.Outlined.MicSlash,
    CupertinoIcons.Outlined.Minus,
    CupertinoIcons.Outlined.MinusCircle,
    CupertinoIcons.Outlined.MinusMagnifyingglass,
    CupertinoIcons.Outlined.Moon,
    CupertinoIcons.Outlined.MoonStars,
    CupertinoIcons.Outlined.Mount,
    CupertinoIcons.Outlined.Multiply,
    CupertinoIcons.Outlined.MusicMic,
    CupertinoIcons.Outlined.MusicNote,
    CupertinoIcons.Outlined.MusicNoteList,
    CupertinoIcons.Outlined.MusicQuarternote3,
    CupertinoIcons.Outlined.Network,
    CupertinoIcons.Outlined.Newspaper,
    CupertinoIcons.Outlined.Nosign,
    CupertinoIcons.Outlined.NoteText,
    CupertinoIcons.Outlined.NoteTextBadgePlus,
    CupertinoIcons.Outlined.Number,
    CupertinoIcons.Outlined.Opticaldisc,
    CupertinoIcons.Outlined.Option,
    CupertinoIcons.Outlined.Paintbrush,
    CupertinoIcons.Outlined.PaintbrushPointed,
    CupertinoIcons.Outlined.Paintpalette,
    CupertinoIcons.Outlined.Paperclip,
    CupertinoIcons.Outlined.PaperclipBadgeEllipsis,
    CupertinoIcons.Outlined.PaperclipCircle,
    CupertinoIcons.Outlined.Paperplane,
    CupertinoIcons.Outlined.Paragraphsign,
    CupertinoIcons.Outlined.PartyPopper,
    CupertinoIcons.Outlined.Pause,
    CupertinoIcons.Outlined.PauseCircle,
    CupertinoIcons.Outlined.Pawprint,
    CupertinoIcons.Outlined.Pencil,
    CupertinoIcons.Outlined.PencilCircle,
    CupertinoIcons.Outlined.PencilTipCropCircle,
    CupertinoIcons.Outlined.Percent,
    CupertinoIcons.Outlined.Person,
    CupertinoIcons.Outlined.Person2,
    CupertinoIcons.Outlined.PersonAndBackgroundDotted,
    CupertinoIcons.Outlined.PersonCircle,
    CupertinoIcons.Outlined.PersonCropCircle,
    CupertinoIcons.Outlined.PersonCropCircleBadgeMinus,
    CupertinoIcons.Outlined.PersonCropCircleBadgePlus,
    CupertinoIcons.Outlined.PersonCropSquare,
    CupertinoIcons.Outlined.PersonIcloud,
    CupertinoIcons.Outlined.PersonTextRectangle,
    CupertinoIcons.Outlined.PersonWave2,
    CupertinoIcons.Outlined.Personalhotspot,
    CupertinoIcons.Outlined.Phone,
    CupertinoIcons.Outlined.PhoneAndWaveform,
    CupertinoIcons.Outlined.PhoneArrowDownLeft,
    CupertinoIcons.Outlined.PhoneArrowUpRight,
    CupertinoIcons.Outlined.PhoneBadgePlus,
    CupertinoIcons.Outlined.PhoneCircle,
    CupertinoIcons.Outlined.PhoneConnection,
    CupertinoIcons.Outlined.Photo,
    CupertinoIcons.Outlined.PhotoStack,
    CupertinoIcons.Outlined.PhotoTv,
    CupertinoIcons.Outlined.Pill,
    CupertinoIcons.Outlined.Pin,
    CupertinoIcons.Outlined.PinCircle,
    CupertinoIcons.Outlined.PinSlash,
    CupertinoIcons.Outlined.Pip,
    CupertinoIcons.Outlined.PipEnter,
    CupertinoIcons.Outlined.PipExit,
    CupertinoIcons.Outlined.Play,
    CupertinoIcons.Outlined.PlayCircle,
    CupertinoIcons.Outlined.PlayDisplay,
    CupertinoIcons.Outlined.Plus,
    CupertinoIcons.Outlined.PlusApp,
    CupertinoIcons.Outlined.PlusBubble,
    CupertinoIcons.Outlined.PlusCircle,
    CupertinoIcons.Outlined.PlusMagnifyingglass,
    CupertinoIcons.Outlined.PlusMessage,
    CupertinoIcons.Outlined.PlusSquare,
    CupertinoIcons.Outlined.PlusViewfinder,
    CupertinoIcons.Outlined.Popcorn,
    CupertinoIcons.Outlined.Power,
    CupertinoIcons.Outlined.PowerCircle,
    CupertinoIcons.Outlined.Printer,
    CupertinoIcons.Outlined.Puzzlepiece,
    CupertinoIcons.Outlined.PuzzlepieceExtension,
    CupertinoIcons.Outlined.Qrcode,
    CupertinoIcons.Outlined.QrcodeViewfinder,
    CupertinoIcons.Outlined.Questionmark,
    CupertinoIcons.Outlined.QuestionmarkApp,
    CupertinoIcons.Outlined.QuestionmarkCircle,
    CupertinoIcons.Outlined.QuestionmarkFolder,
    CupertinoIcons.Outlined.QuestionmarkSquare,
    CupertinoIcons.Outlined.QuoteClosing,
    CupertinoIcons.Outlined.QuoteOpening,
    CupertinoIcons.Outlined.Rays,
    CupertinoIcons.Outlined.RecordCircle,
    CupertinoIcons.Outlined.Recordingtape,
    CupertinoIcons.Outlined.RectangleArrowtriangle2Outward,
    CupertinoIcons.Outlined.RectangleConnectedToLineBelow,
    CupertinoIcons.Outlined.RectanglePortraitAndArrowForward,
    CupertinoIcons.Outlined.RectanglePortraitArrowtriangle2Outward,
    CupertinoIcons.Outlined.RectangleStack,
    CupertinoIcons.Outlined.Repeat,
    CupertinoIcons.Outlined.Rosette,
    CupertinoIcons.Outlined.Rotate3d,
    CupertinoIcons.Outlined.RotateLeft,
    CupertinoIcons.Outlined.RotateRight,
    CupertinoIcons.Outlined.Rublesign,
    CupertinoIcons.Outlined.Ruler,
    CupertinoIcons.Outlined.Safari,
    CupertinoIcons.Outlined.Scalemass,
    CupertinoIcons.Outlined.Scissors,
    CupertinoIcons.Outlined.Scope,
    CupertinoIcons.Outlined.Scribble,
    CupertinoIcons.Outlined.ScribbleVariable,
    CupertinoIcons.Outlined.Scroll,
    CupertinoIcons.Outlined.ServerRack,
    CupertinoIcons.Outlined.Shareplay,
    CupertinoIcons.Outlined.ShareplaySlash,
    CupertinoIcons.Outlined.ShazamLogo,
    CupertinoIcons.Outlined.Shield,
    CupertinoIcons.Outlined.ShieldSlash,
    CupertinoIcons.Outlined.Shippingbox,
    CupertinoIcons.Outlined.Shuffle,
    CupertinoIcons.Outlined.SidebarLeft,
    CupertinoIcons.Outlined.SidebarRight,
    CupertinoIcons.Outlined.Simcard,
    CupertinoIcons.Outlined.Skew,
    CupertinoIcons.Outlined.SliderHorizontal3,
    CupertinoIcons.Outlined.SliderVertical3,
    CupertinoIcons.Outlined.Snowflake,
    CupertinoIcons.Outlined.Soccerball,
    CupertinoIcons.Outlined.Space,
    CupertinoIcons.Outlined.Sparkle,
    CupertinoIcons.Outlined.Sparkles,
    CupertinoIcons.Outlined.Speaker,
    CupertinoIcons.Outlined.SpeakerMinus,
    CupertinoIcons.Outlined.SpeakerPlus,
    CupertinoIcons.Outlined.SpeakerSlash,
    CupertinoIcons.Outlined.SpeakerWave2,
    CupertinoIcons.Outlined.Speedometer,
    CupertinoIcons.Outlined.Square3Layers3dDownLeft,
    CupertinoIcons.Outlined.Square3Layers3dDownRight,
    CupertinoIcons.Outlined.SquareAndArrowUp,
    CupertinoIcons.Outlined.SquareAndPencil,
    CupertinoIcons.Outlined.SquareOnSquare,
    CupertinoIcons.Outlined.SquareSplit1x2,
    CupertinoIcons.Outlined.SquareSplit2x1,
    CupertinoIcons.Outlined.SquareStack,
    CupertinoIcons.Outlined.SquareStack3dUp,
    CupertinoIcons.Outlined.Star,
    CupertinoIcons.Outlined.StarSlash,
    CupertinoIcons.Outlined.Staroflife,
    CupertinoIcons.Outlined.Sterlingsign,
    CupertinoIcons.Outlined.Stethoscope,
    CupertinoIcons.Outlined.Stop,
    CupertinoIcons.Outlined.StopCircle,
    CupertinoIcons.Outlined.Suitcase,
    CupertinoIcons.Outlined.Sum,
    CupertinoIcons.Outlined.SunMax,
    CupertinoIcons.Outlined.Swift,
    CupertinoIcons.Outlined.Tag,
    CupertinoIcons.Outlined.Target,
    CupertinoIcons.Outlined.TennisRacket,
    CupertinoIcons.Outlined.Terminal,
    CupertinoIcons.Outlined.TextBubble,
    CupertinoIcons.Outlined.TextMagnifyingglass,
    CupertinoIcons.Outlined.Theatermasks,
    CupertinoIcons.Outlined.Timer,
    CupertinoIcons.Outlined.Touchid,
    CupertinoIcons.Outlined.Trash,
    CupertinoIcons.Outlined.TrashSlash,
    CupertinoIcons.Outlined.TrayAndArrowDown,
    CupertinoIcons.Outlined.TrayAndArrowUp,
    CupertinoIcons.Outlined.Trophy,
    CupertinoIcons.Outlined.Tshirt,
    CupertinoIcons.Outlined.Tv,
    CupertinoIcons.Outlined.Umbrella,
    CupertinoIcons.Outlined.Video,
    CupertinoIcons.Outlined.VideoCircle,
    CupertinoIcons.Outlined.VideoSlash,
    CupertinoIcons.Outlined.Volleyball,
    CupertinoIcons.Outlined.WalletPass,
    CupertinoIcons.Outlined.WandAndStars,
    CupertinoIcons.Outlined.WandAndStarsInverse,
    CupertinoIcons.Outlined.Waveform,
    CupertinoIcons.Outlined.WaveformAndMagnifyingglass,
    CupertinoIcons.Outlined.WaveformAndMic,
    CupertinoIcons.Outlined.WaveformPathEcg,
    CupertinoIcons.Outlined.WebCamera,
    CupertinoIcons.Outlined.Wifi,
    CupertinoIcons.Outlined.WifiExclamationmark,
    CupertinoIcons.Outlined.WifiRouter,
    CupertinoIcons.Outlined.WifiSlash,
    CupertinoIcons.Outlined.Wind,
    CupertinoIcons.Outlined.Wineglass,
    CupertinoIcons.Outlined.WrenchAndScrewdriver,
    CupertinoIcons.Outlined.Xmark,
    CupertinoIcons.Outlined.XmarkApp,
    CupertinoIcons.Outlined.XmarkBin,
    CupertinoIcons.Outlined.XmarkCircle,
    CupertinoIcons.Outlined.XmarkIcloud,
    CupertinoIcons.Outlined.XmarkSeal,
    CupertinoIcons.Outlined.XmarkShield,
    CupertinoIcons.Outlined.Yensign,
    CupertinoIcons.Outlined.Zzz,
    CupertinoIcons.Outlined._4kTv,
)

private val Filled = listOf(
            CupertinoIcons.Filled.Airtag,
            CupertinoIcons.Filled.Alarm,
            CupertinoIcons.Filled.Appletv,
            CupertinoIcons.Filled.Archivebox,
            CupertinoIcons.Filled.ArrowClockwiseCircle,
            CupertinoIcons.Filled.ArrowCounterclockwiseCircle,
            CupertinoIcons.Filled.ArrowCounterclockwiseIcloud,
            CupertinoIcons.Filled.ArrowDownCircle,
            CupertinoIcons.Filled.ArrowDownDoc,
            CupertinoIcons.Filled.ArrowTriangle2CirclepathCamera,
            CupertinoIcons.Filled.ArrowTriangle2CirclepathCircle,
            CupertinoIcons.Filled.ArrowTurnUpForwardIphone,
            CupertinoIcons.Filled.ArrowUpDoc,
            CupertinoIcons.Filled.ArrowshapeTurnUpLeft,
            CupertinoIcons.Filled.ArrowshapeTurnUpLeft2,
            CupertinoIcons.Filled.Backward,
            CupertinoIcons.Filled.BackwardEnd,
            CupertinoIcons.Filled.Bag,
            CupertinoIcons.Filled.BagBadgeMinus,
            CupertinoIcons.Filled.BagBadgePlus,
            CupertinoIcons.Filled.Balloon,
            CupertinoIcons.Filled.Bandage,
            CupertinoIcons.Filled.Banknote,
            CupertinoIcons.Filled.Baseball,
            CupertinoIcons.Filled.Basket,
            CupertinoIcons.Filled.Basketball,
            CupertinoIcons.Filled.BedDouble,
            CupertinoIcons.Filled.Bell,
            CupertinoIcons.Filled.BellAndWavesLeftAndRight,
            CupertinoIcons.Filled.BellBadge,
            CupertinoIcons.Filled.BellCircle,
            CupertinoIcons.Filled.BellSlash,
            CupertinoIcons.Filled.Binoculars,
            CupertinoIcons.Filled.BirthdayCake,
            CupertinoIcons.Filled.Bolt,
            CupertinoIcons.Filled.BoltHorizontal,
            CupertinoIcons.Filled.BoltSlash,
            CupertinoIcons.Filled.Book,
            CupertinoIcons.Filled.BookCircle,
            CupertinoIcons.Filled.BookClosed,
            CupertinoIcons.Filled.Bookmark,
            CupertinoIcons.Filled.BookmarkSlash,
            CupertinoIcons.Filled.Briefcase,
            CupertinoIcons.Filled.BubbleLeft,
            CupertinoIcons.Filled.BubbleRight,
            CupertinoIcons.Filled.Building,
            CupertinoIcons.Filled.Building2,
            CupertinoIcons.Filled.Burst,
            CupertinoIcons.Filled.Camera,
            CupertinoIcons.Filled.CameraCircle,
            CupertinoIcons.Filled.Capslock,
            CupertinoIcons.Filled.Car,
            CupertinoIcons.Filled.Cart,
            CupertinoIcons.Filled.CartBadgeMinus,
            CupertinoIcons.Filled.CartBadgePlus,
            CupertinoIcons.Filled.Case,
            CupertinoIcons.Filled.ChartBar,
            CupertinoIcons.Filled.CheckmarkCircle,
            CupertinoIcons.Filled.CheckmarkIcloud,
            CupertinoIcons.Filled.CheckmarkMessage,
            CupertinoIcons.Filled.CheckmarkSeal,
            CupertinoIcons.Filled.CheckmarkShield,
            CupertinoIcons.Filled.CheckmarkSquare,
            CupertinoIcons.Filled.CircleLefthalfed,
            CupertinoIcons.Filled.CircleRighthalfed,
            CupertinoIcons.Filled.Clear,
            CupertinoIcons.Filled.Clipboard,
            CupertinoIcons.Filled.Clock,
            CupertinoIcons.Filled.Cloud,
            CupertinoIcons.Filled.Cone,
            CupertinoIcons.Filled.Cpu,
            CupertinoIcons.Filled.Creditcard,
            CupertinoIcons.Filled.Cross,
            CupertinoIcons.Filled.CrossCircle,
            CupertinoIcons.Filled.CrossVial,
            CupertinoIcons.Filled.Crown,
            CupertinoIcons.Filled.Cube,
            CupertinoIcons.Filled.CupAndSaucer,
            CupertinoIcons.Filled.DeleteLeft,
            CupertinoIcons.Filled.DeleteRight,
            CupertinoIcons.Filled.Dice,
            CupertinoIcons.Filled.Doc,
            CupertinoIcons.Filled.DocBadgeArrowUp,
            CupertinoIcons.Filled.DocBadgePlus,
            CupertinoIcons.Filled.DocOnDoc,
            CupertinoIcons.Filled.DocPlaintext,
            CupertinoIcons.Filled.DocText,
            CupertinoIcons.Filled.Drop,
            CupertinoIcons.Filled.Ear,
            CupertinoIcons.Filled.EllipsisBubble,
            CupertinoIcons.Filled.EllipsisCircle,
            CupertinoIcons.Filled.EllipsisMessage,
            CupertinoIcons.Filled.Envelope,
            CupertinoIcons.Filled.EnvelopeBadge,
            CupertinoIcons.Filled.EnvelopeCircle,
            CupertinoIcons.Filled.EnvelopeOpen,
            CupertinoIcons.Filled.Eraser,
            CupertinoIcons.Filled.ExclamationmarkCircle,
            CupertinoIcons.Filled.ExclamationmarkIcloud,
            CupertinoIcons.Filled.ExclamationmarkSquare,
            CupertinoIcons.Filled.ExclamationmarkTriangle,
            CupertinoIcons.Filled.Externaldrive,
            CupertinoIcons.Filled.Eye,
            CupertinoIcons.Filled.EyeSlash,
            CupertinoIcons.Filled.Facemask,
            CupertinoIcons.Filled.Fanblades,
            CupertinoIcons.Filled.FanbladesSlash,
            CupertinoIcons.Filled.Film,
            CupertinoIcons.Filled.Flag,
            CupertinoIcons.Filled.Flag2Crossed,
            CupertinoIcons.Filled.FlagSlash,
            CupertinoIcons.Filled.Flame,
            CupertinoIcons.Filled.Folder,
            CupertinoIcons.Filled.FolderBadgePlus,
            CupertinoIcons.Filled.Football,
            CupertinoIcons.Filled.ForkKnifeCircle,
            CupertinoIcons.Filled.Forward,
            CupertinoIcons.Filled.ForwardEnd,
            CupertinoIcons.Filled.Fuelpump,
            CupertinoIcons.Filled.Gamecontroller,
            CupertinoIcons.Filled.Gearshape,
            CupertinoIcons.Filled.Gearshape2,
            CupertinoIcons.Filled.Gift,
            CupertinoIcons.Filled.Giftcard,
            CupertinoIcons.Filled.GlobeDesk,
            CupertinoIcons.Filled.Graduationcap,
            CupertinoIcons.Filled.Hammer,
            CupertinoIcons.Filled.HandDraw,
            CupertinoIcons.Filled.HandPointUp,
            CupertinoIcons.Filled.HandPointUpLeft,
            CupertinoIcons.Filled.HandRaised,
            CupertinoIcons.Filled.HandRaisedSlash,
            CupertinoIcons.Filled.HandTap,
            CupertinoIcons.Filled.HandThumbsdown,
            CupertinoIcons.Filled.HandThumbsup,
            CupertinoIcons.Filled.HandWave,
            CupertinoIcons.Filled.HandsSparkles,
            CupertinoIcons.Filled.HeadphonesCircle,
            CupertinoIcons.Filled.Heart,
            CupertinoIcons.Filled.HeartCircle,
            CupertinoIcons.Filled.HeartSlash,
            CupertinoIcons.Filled.HeartTextSquare,
            CupertinoIcons.Filled.Hifispeaker,
            CupertinoIcons.Filled.Homepod,
            CupertinoIcons.Filled.Homepodmini,
            CupertinoIcons.Filled.House,
            CupertinoIcons.Filled.Icloud,
            CupertinoIcons.Filled.IcloudAndArrowDown,
            CupertinoIcons.Filled.IcloudAndArrowUp,
            CupertinoIcons.Filled.InfoBubble,
            CupertinoIcons.Filled.InfoCircle,
            CupertinoIcons.Filled.InfoSquare,
            CupertinoIcons.Filled.Key,
            CupertinoIcons.Filled.KeyIcloud,
            CupertinoIcons.Filled.Keyboard,
            CupertinoIcons.Filled.Lanyardcard,
            CupertinoIcons.Filled.Leaf,
            CupertinoIcons.Filled.Level,
            CupertinoIcons.Filled.Lifepreserver,
            CupertinoIcons.Filled.LightBeaconMax,
            CupertinoIcons.Filled.Lightbulb,
            CupertinoIcons.Filled.LightbulbSlash,
            CupertinoIcons.Filled.LinkCircle,
            CupertinoIcons.Filled.ListBulletCircle,
            CupertinoIcons.Filled.ListBulletClipboard,
            CupertinoIcons.Filled.ListClipboard,
            CupertinoIcons.Filled.Location,
            CupertinoIcons.Filled.Lock,
            CupertinoIcons.Filled.LockCircle,
            CupertinoIcons.Filled.LockOpen,
            CupertinoIcons.Filled.LockSlash,
            CupertinoIcons.Filled.Magazine,
            CupertinoIcons.Filled.Mail,
            CupertinoIcons.Filled.MailStack,
            CupertinoIcons.Filled.Map,
            CupertinoIcons.Filled.Medal,
            CupertinoIcons.Filled.Megaphone,
            CupertinoIcons.Filled.Menucard,
            CupertinoIcons.Filled.Message,
            CupertinoIcons.Filled.MessageBadgeed,
            CupertinoIcons.Filled.Mic,
            CupertinoIcons.Filled.MicSlash,
            CupertinoIcons.Filled.MinusCircle,
            CupertinoIcons.Filled.Moon,
            CupertinoIcons.Filled.MoonStars,
            CupertinoIcons.Filled.Mount,
            CupertinoIcons.Filled.Newspaper,
            CupertinoIcons.Filled.Opticaldisc,
            CupertinoIcons.Filled.Paintbrush,
            CupertinoIcons.Filled.PaintbrushPointed,
            CupertinoIcons.Filled.Paintpalette,
            CupertinoIcons.Filled.PaperclipCircle,
            CupertinoIcons.Filled.Paperplane,
            CupertinoIcons.Filled.PartyPopper,
            CupertinoIcons.Filled.Pause,
            CupertinoIcons.Filled.PauseCircle,
            CupertinoIcons.Filled.Pawprint,
            CupertinoIcons.Filled.PencilCircle,
            CupertinoIcons.Filled.Person,
            CupertinoIcons.Filled.Person2,
            CupertinoIcons.Filled.PersonCircle,
            CupertinoIcons.Filled.PersonCropCircle,
            CupertinoIcons.Filled.PersonCropCircleBadgeMinus,
            CupertinoIcons.Filled.PersonCropCircleBadgePlus,
            CupertinoIcons.Filled.PersonCropSquare,
            CupertinoIcons.Filled.PersonIcloud,
            CupertinoIcons.Filled.PersonTextRectangle,
            CupertinoIcons.Filled.PersonViewfinder,
            CupertinoIcons.Filled.PersonWave2,
            CupertinoIcons.Filled.Phone,
            CupertinoIcons.Filled.PhoneAndWaveform,
            CupertinoIcons.Filled.PhoneArrowDownLeft,
            CupertinoIcons.Filled.PhoneArrowUpRight,
            CupertinoIcons.Filled.PhoneBadgePlus,
            CupertinoIcons.Filled.PhoneCircle,
            CupertinoIcons.Filled.PhoneConnection,
            CupertinoIcons.Filled.Photo,
            CupertinoIcons.Filled.PhotoStack,
            CupertinoIcons.Filled.Pill,
            CupertinoIcons.Filled.Pin,
            CupertinoIcons.Filled.PinCircle,
            CupertinoIcons.Filled.PinSlash,
            CupertinoIcons.Filled.Pip,
            CupertinoIcons.Filled.Play,
            CupertinoIcons.Filled.PlayCircle,
            CupertinoIcons.Filled.PlusApp,
            CupertinoIcons.Filled.PlusBubble,
            CupertinoIcons.Filled.PlusCircle,
            CupertinoIcons.Filled.PlusMessage,
            CupertinoIcons.Filled.PlusSquare,
            CupertinoIcons.Filled.Popcorn,
            CupertinoIcons.Filled.PowerCircle,
            CupertinoIcons.Filled.Printer,
            CupertinoIcons.Filled.Puzzlepiece,
            CupertinoIcons.Filled.PuzzlepieceExtension,
            CupertinoIcons.Filled.QuestionmarkApp,
            CupertinoIcons.Filled.QuestionmarkCircle,
            CupertinoIcons.Filled.QuestionmarkFolder,
            CupertinoIcons.Filled.QuestionmarkSquare,
            CupertinoIcons.Filled.RecordCircle,
            CupertinoIcons.Filled.RectanglePortraitAndArrowForward,
            CupertinoIcons.Filled.RectangleStack,
            CupertinoIcons.Filled.RotateLeft,
            CupertinoIcons.Filled.RotateRight,
            CupertinoIcons.Filled.Ruler,
            CupertinoIcons.Filled.Safari,
            CupertinoIcons.Filled.Scalemass,
            CupertinoIcons.Filled.Scroll,
            CupertinoIcons.Filled.ShazamLogo,
            CupertinoIcons.Filled.Shield,
            CupertinoIcons.Filled.ShieldLefthalfed,
            CupertinoIcons.Filled.ShieldRighthalfed,
            CupertinoIcons.Filled.ShieldSlash,
            CupertinoIcons.Filled.Shippingbox,
            CupertinoIcons.Filled.Shoeprints,
            CupertinoIcons.Filled.Simcard,
            CupertinoIcons.Filled.SmallcircleedCircle,
            CupertinoIcons.Filled.Speaker,
            CupertinoIcons.Filled.SpeakerMinus,
            CupertinoIcons.Filled.SpeakerPlus,
            CupertinoIcons.Filled.SpeakerSlash,
            CupertinoIcons.Filled.SpeakerWave2,
            CupertinoIcons.Filled.SquareAndArrowUp,
            CupertinoIcons.Filled.SquareBottomthirdInseted,
            CupertinoIcons.Filled.SquareOnSquare,
            CupertinoIcons.Filled.SquareSplit1x2,
            CupertinoIcons.Filled.SquareSplit2x1,
            CupertinoIcons.Filled.SquareStack,
            CupertinoIcons.Filled.SquareStack3dUp,
            CupertinoIcons.Filled.SquareTopthirdInseted,
            CupertinoIcons.Filled.Star,
            CupertinoIcons.Filled.StarLeadinghalfed,
            CupertinoIcons.Filled.StarSlash,
            CupertinoIcons.Filled.Staroflife,
            CupertinoIcons.Filled.Stop,
            CupertinoIcons.Filled.StopCircle,
            CupertinoIcons.Filled.Suitcase,
            CupertinoIcons.Filled.SunMax,
            CupertinoIcons.Filled.Tag,
            CupertinoIcons.Filled.Terminal,
            CupertinoIcons.Filled.TextBubble,
            CupertinoIcons.Filled.Theatermasks,
            CupertinoIcons.Filled.Trash,
            CupertinoIcons.Filled.TrashSlash,
            CupertinoIcons.Filled.TrayAndArrowDown,
            CupertinoIcons.Filled.TrayAndArrowUp,
            CupertinoIcons.Filled.Trophy,
            CupertinoIcons.Filled.Tshirt,
            CupertinoIcons.Filled.Tv,
            CupertinoIcons.Filled.TvAndHifispeaker,
            CupertinoIcons.Filled.Umbrella,
            CupertinoIcons.Filled.Video,
            CupertinoIcons.Filled.VideoCircle,
            CupertinoIcons.Filled.VideoSlash,
            CupertinoIcons.Filled.Volleyball,
            CupertinoIcons.Filled.WalletPass,
            CupertinoIcons.Filled.WebCamera,
            CupertinoIcons.Filled.WifiRouter,
            CupertinoIcons.Filled.Wineglass,
            CupertinoIcons.Filled.XmarkApp,
            CupertinoIcons.Filled.XmarkBin,
            CupertinoIcons.Filled.XmarkCircle,
            CupertinoIcons.Filled.XmarkIcloud,
            CupertinoIcons.Filled.XmarkSeal,
            CupertinoIcons.Filled.XmarkShield,
            CupertinoIcons.Filled._4kTv,
)
