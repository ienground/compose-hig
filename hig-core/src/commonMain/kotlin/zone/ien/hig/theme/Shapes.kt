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



package zone.ien.hig.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp
import com.kyant.shapes.RoundedRectangle
import com.kyant.shapes.UnevenRoundedRectangle
import zone.ien.hig.InternalCupertinoApi

@Stable
class Shapes(
    val extraSmall: UnevenRoundedRectangle = ShapeDefaults.ExtraSmall,
    val small: UnevenRoundedRectangle = ShapeDefaults.Small,
    val medium: UnevenRoundedRectangle = ShapeDefaults.Medium,
    val large: UnevenRoundedRectangle = ShapeDefaults.Large,
    val extraLarge: UnevenRoundedRectangle = ShapeDefaults.ExtraLarge,
) {
    fun copy(
        extraSmall: UnevenRoundedRectangle = this.extraSmall,
        small: UnevenRoundedRectangle = this.small,
        medium: UnevenRoundedRectangle = this.medium,
        large: UnevenRoundedRectangle = this.large,
        extraLarge: UnevenRoundedRectangle = this.extraLarge,
    ) = Shapes(
        extraSmall = extraSmall,
        small = small,
        medium = medium,
        large = large,
        extraLarge = extraLarge,
    )
}

@InternalCupertinoApi
val LocalShapes = staticCompositionLocalOf { Shapes() }

@Immutable
object ShapeDefaults {
    /** Extra small sized corner shape */
    val ExtraSmall: UnevenRoundedRectangle = 4.dp.let { RoundedRectangle(it, it, it, it) }

    /** Small sized corner shape */
    val Small: UnevenRoundedRectangle = 8.dp.let { RoundedRectangle(it, it, it, it) }

    /** Medium sized corner shape */
    val Medium: UnevenRoundedRectangle = 12.dp.let { RoundedRectangle(it, it, it, it) }

    /** Large sized corner shape */
    val Large: UnevenRoundedRectangle = 16.dp.let { RoundedRectangle(it, it, it, it) }

    /** Extra large sized corner shape */
    val ExtraLarge: UnevenRoundedRectangle = 24.dp.let { RoundedRectangle(it, it, it, it) }
}
