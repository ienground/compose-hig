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
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

/**
 * Creates a haptic feedback instance for Cupertino styling.
 *
 * @return a [HapticFeedback] instance for Cupertino styling
 */
@Composable
expect fun rememberCupertinoHapticFeedback(): HapticFeedback

/**
 * Cupertino haptic feedback types that work only on iOS.
 *
 * These haptic feedback types are available for public usage in iosMain as
 * extension properties of [HapticFeedbackType.Companion]
 */
@InternalCupertinoApi
object CupertinoHapticFeedback {
    /**
     * Selection changed haptic feedback type.
     */
    val SelectionChanged: HapticFeedbackType = HapticFeedbackType(1001)

    /**
     * Success haptic feedback type.
     */
    val Success: HapticFeedbackType = HapticFeedbackType(2001)
    
    /**
     * Warning haptic feedback type.
     */
    val Warning: HapticFeedbackType = HapticFeedbackType(2002)
    
    /**
     * Error haptic feedback type.
     */
    val Error: HapticFeedbackType = HapticFeedbackType(2003)

    /**
     * Impact light haptic feedback type.
     */
    val ImpactLight: HapticFeedbackType = HapticFeedbackType(3001)
    
    /**
     * Impact medium haptic feedback type.
     */
    val ImpactMedium: HapticFeedbackType = HapticFeedbackType(3002)
    
    /**
     * Impact heavy haptic feedback type.
     */
    val ImpactHeavy: HapticFeedbackType = HapticFeedbackType(3003)
    
    /**
     * Impact rigid haptic feedback type.
     */
    val ImpactRigid: HapticFeedbackType = HapticFeedbackType(3004)
    
    /**
     * Impact soft haptic feedback type.
     */
    val ImpactSoft: HapticFeedbackType = HapticFeedbackType(3005)
}
