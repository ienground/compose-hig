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
import com.kyant.capsule.ContinuousRoundedRectangle

/**
 * A class that holds the shapes for different design systems.
 *
 * This class provides a consistent set of shapes that can be used across both Material and Cupertino design systems.
 * It includes shapes for different sizes and also provides HIG-specific shapes.
 *
 * @param extraSmall The extra small shape, typically used for small components.
 * @param small The small shape, typically used for small components.
 * @param medium The medium shape, typically used for medium components.
 * @param large The large shape, typically used for large components.
 * @param extraLarge The extra large shape, typically used for large components.
 * @param higExtraSmall The extra small HIG shape, typically used for small components.
 * @param higSmall The small HIG shape, typically used for small components.
 * @param higMedium The medium HIG shape, typically used for medium components.
 * @param higLarge The large HIG shape, typically used for large components.
 * @param higExtraLarge The extra large HIG shape, typically used for large components.
 */
@Immutable
class Shapes(
    val extraSmall: CornerBasedShape = RoundedCornerShape(4.dp),
    val small: CornerBasedShape = RoundedCornerShape(8.dp),
    val medium: CornerBasedShape = RoundedCornerShape(12.dp),
    val large: CornerBasedShape = RoundedCornerShape(16.dp),
    val extraLarge: CornerBasedShape = RoundedCornerShape(28.dp),
    val higExtraSmall: ContinuousRoundedRectangle = ContinuousRoundedRectangle(4.dp),
    val higSmall: ContinuousRoundedRectangle = ContinuousRoundedRectangle(8.dp),
    val higMedium: ContinuousRoundedRectangle = ContinuousRoundedRectangle(12.dp),
    val higLarge: ContinuousRoundedRectangle = ContinuousRoundedRectangle(16.dp),
    val higExtraLarge: ContinuousRoundedRectangle = ContinuousRoundedRectangle(24.dp)
) {
    /**
     * Creates a copy of this Shapes object with the specified values replaced.
     *
     * @param extraSmall The extra small shape, typically used for small components.
     * @param small The small shape, typically used for small components.
     * @param medium The medium shape, typically used for medium components.
     * @param large The large shape, typically used for large components.
     * @param extraLarge The extra large shape, typically used for large components.
     * @param higExtraSmall The extra small HIG shape, typically used for small components.
     * @param higSmall The small HIG shape, typically used for small components.
     * @param higMedium The medium HIG shape, typically used for medium components.
     * @param higLarge The large HIG shape, typically used for large components.
     * @param higExtraLarge The extra large HIG shape, typically used for large components.
     * @return A new Shapes object with the specified values replaced.
     */
    fun copy(
        extraSmall: CornerBasedShape = this.extraSmall,
        small: CornerBasedShape = this.small,
        medium: CornerBasedShape = this.medium,
        large: CornerBasedShape = this.large,
        extraLarge: CornerBasedShape = this.extraLarge,
        higExtraSmall: ContinuousRoundedRectangle = this.higExtraSmall,
        higSmall: ContinuousRoundedRectangle = this.higSmall,
        higMedium: ContinuousRoundedRectangle = this.higMedium,
        higLarge: ContinuousRoundedRectangle = this.higLarge,
        higExtraLarge: ContinuousRoundedRectangle = this.higExtraLarge
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



