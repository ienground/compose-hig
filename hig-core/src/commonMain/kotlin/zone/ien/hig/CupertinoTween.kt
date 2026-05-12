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

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween

/**
 * Cupertino [tween] transition spec.
 *
 * Default values are used for iOS view transitions such as
 * UINavigationController, UIAlertController
 *
 * @param durationMillis the duration of the animation in milliseconds
 * @param delayMillis the delay before the animation starts in milliseconds
 * @param easing the easing function to use for the animation
 * @return a TweenSpec for the Cupertino transition
 */
fun <T> cupertinoTween(
    durationMillis: Int = CupertinoTransitionDuration,
    delayMillis: Int = 0,
    easing: Easing = CupertinoEasing,
): TweenSpec<T> =
    tween(
        durationMillis = durationMillis,
        easing = easing,
        delayMillis = delayMillis,
    )

/**
 * The easing function used for Cupertino transitions.
 */
val CupertinoEasing = CubicBezierEasing(0.2833f, 0.99f, 0.31833f, 0.99f)
/**
 * The default transition duration for Cupertino transitions.
 */
private val CupertinoTransitionDuration = 400
