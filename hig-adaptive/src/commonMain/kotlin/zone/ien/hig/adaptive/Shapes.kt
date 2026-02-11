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



package zone.ien.hig.adaptive

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp
import com.kyant.shapes.RoundedRectangle
import com.kyant.shapes.UnevenRoundedRectangle
import zone.ien.hig.theme.Shapes as CupertinoShapes
import androidx.compose.material3.Shapes as MaterialShapes
@Immutable
class Shapes(
    val extraSmall: CornerBasedShape = RoundedCornerShape(4.dp),
    val small: CornerBasedShape = RoundedCornerShape(8.dp),
    val medium: CornerBasedShape = RoundedCornerShape(12.dp),
    val large: CornerBasedShape = RoundedCornerShape(16.dp),
    val extraLarge: CornerBasedShape = RoundedCornerShape(28.dp),
    val higExtraSmall: UnevenRoundedRectangle = 4.dp.let { RoundedRectangle(it, it, it, it) },
    val higSmall: UnevenRoundedRectangle = 8.dp.let { RoundedRectangle(it, it, it, it) },
    val higMedium: UnevenRoundedRectangle = 12.dp.let { RoundedRectangle(it, it, it, it) },
    val higLarge: UnevenRoundedRectangle = 16.dp.let { RoundedRectangle(it, it, it, it) },
    val higExtraLarge: UnevenRoundedRectangle = 24.dp.let { RoundedRectangle(it, it, it, it) }
) {
    fun copy(
        extraSmall: CornerBasedShape = this.extraSmall,
        small: CornerBasedShape = this.small,
        medium: CornerBasedShape = this.medium,
        large: CornerBasedShape = this.large,
        extraLarge: CornerBasedShape = this.extraLarge,
        higExtraSmall: UnevenRoundedRectangle = this.higExtraSmall,
        higSmall: UnevenRoundedRectangle = this.higSmall,
        higMedium: UnevenRoundedRectangle = this.higMedium,
        higLarge: UnevenRoundedRectangle = this.higLarge,
        higExtraLarge: UnevenRoundedRectangle = this.higExtraLarge
    ) = Shapes(
        extraSmall = extraSmall,
        small = small,
        medium = medium,
        large = large,
        extraLarge = extraLarge,
        higExtraSmall = higExtraSmall,
        higSmall = higSmall,
        higMedium = higMedium,
        higLarge = higLarge,
        higExtraLarge = higExtraLarge
    )
}



