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
    val extraSmall: RoundedRectangle = ShapeDefaults.ExtraSmall,
    val small: RoundedRectangle = ShapeDefaults.Small,
    val medium: RoundedRectangle = ShapeDefaults.Medium,
    val large: RoundedRectangle = ShapeDefaults.Large,
    val extraLarge: RoundedRectangle = ShapeDefaults.ExtraLarge,
) {
    fun copy(
        extraSmall: RoundedRectangle = this.extraSmall,
        small: RoundedRectangle = this.small,
        medium: RoundedRectangle = this.medium,
        large: RoundedRectangle = this.large,
        extraLarge: RoundedRectangle = this.extraLarge,
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
    val ExtraSmall: RoundedRectangle = RoundedRectangle(4.dp)

    /** Small sized corner shape */
    val Small: RoundedRectangle = RoundedRectangle(8.dp)

    /** Medium sized corner shape */
    val Medium: RoundedRectangle = RoundedRectangle(12.dp)

    /** Large sized corner shape */
    val Large: RoundedRectangle = RoundedRectangle(16.dp)

    /** Extra large sized corner shape */
    val ExtraLarge: RoundedRectangle = RoundedRectangle(24.dp)
}
