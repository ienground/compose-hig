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

import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import zone.ien.hig.CupertinoDividerDefaults
import zone.ien.hig.CupertinoHorizontalDivider
import zone.ien.hig.CupertinoVerticalDivider

/**
 * Adaptive divider component that provides different divider styles based on Material Design and Cupertino themes.
 *
 * This function is deprecated and replaced by [AdaptiveHorizontalDivider] function.
 *
 * @param modifier Modifier to be applied to the element
 * @param adaptation Custom configuration function for [DividerAdaptation]
 * @see AdaptiveHorizontalDivider
 * @see HorizontalDivider
 * @see CupertinoHorizontalDivider
 */
@Deprecated(
    replaceWith = ReplaceWith(
        "AdaptiveHorizontalDivider(modifier,adaptation)",
        "import zone.ien.hig.adaptive.AdaptiveHorizontalDivider"
    ),
    message = "Use AdaptiveHorizontalDivider instead")
@Composable
@ExperimentalAdaptiveApi
fun AdaptiveDivider(
    modifier: Modifier = Modifier,
    adaptation: AdaptationScope<DividerAdaptation, DividerAdaptation>.() -> Unit = {}
) = AdaptiveHorizontalDivider(modifier, adaptation)


/**
 * Adaptive horizontal divider component that provides different divider styles based on Material Design and Cupertino themes.
 *
 * [AdaptiveHorizontalDivider] uses the Material Design [HorizontalDivider] component and the Cupertino [CupertinoHorizontalDivider] component to
 * provide an appropriate horizontal divider for each operating system.
 *
 * @param modifier Modifier to be applied to the element
 * @param adaptation Custom configuration function for [DividerAdaptation]
 * @see AdaptiveWidget
 * @see HorizontalDivider
 * @see CupertinoHorizontalDivider
 */
@Composable
@ExperimentalAdaptiveApi
fun AdaptiveHorizontalDivider(
    modifier: Modifier = Modifier,
    adaptation: AdaptationScope<DividerAdaptation, DividerAdaptation>.() -> Unit = {}
) {
    AdaptiveWidget(
        adaptation = remember {
            DividerAdaptationScope()
        },
        adaptationScope = adaptation,
        material = {
            HorizontalDivider(
                modifier = modifier,
                thickness = it.thickness,
                color = it.color
            )
        },
        cupertino = {
            CupertinoHorizontalDivider(
                modifier = modifier,
                thickness = it.thickness,
                color = it.color
            )
        }
    )
}

/**
 * Adaptive vertical divider component that provides different divider styles based on Material Design and Cupertino themes.
 *
 * [AdaptiveVerticalDivider] uses the Material Design [VerticalDivider] component and the Cupertino [CupertinoVerticalDivider] component to
 * provide an appropriate vertical divider for each operating system.
 *
 * @param modifier Modifier to be applied to the element
 * @param adaptation Custom configuration function for [DividerAdaptation]
 * @see AdaptiveWidget
 * @see VerticalDivider
 * @see CupertinoVerticalDivider
 */
@Composable
@ExperimentalAdaptiveApi
fun AdaptiveVerticalDivider(
    modifier: Modifier = Modifier,
    adaptation: AdaptationScope<DividerAdaptation, DividerAdaptation>.() -> Unit = {}
) {
    AdaptiveWidget(
        adaptation = remember {
            DividerAdaptationScope()
        },
        adaptationScope = adaptation,
        material = {
            VerticalDivider(
                modifier = modifier,
                thickness = it.thickness,
                color = it.color
            )
        },
        cupertino = {
            CupertinoVerticalDivider(
                modifier = modifier,
                thickness = it.thickness,
                color = it.color
            )
        }
    )
}

@ExperimentalAdaptiveApi
/**
 * Implementation class for divider component adaptation.
 *
 * @see Adaptation
 * @see DividerAdaptation
 */
private class DividerAdaptationScope: Adaptation<DividerAdaptation, DividerAdaptation>() {

    @Composable
    override fun rememberCupertinoAdaptation(): DividerAdaptation {

        val color = CupertinoDividerDefaults.color
        return remember(color) {
            DividerAdaptation(
                color = color,
                thickness = CupertinoDividerDefaults.Thickness
            )
        }
    }

    @Composable
    override fun rememberMaterialAdaptation(): DividerAdaptation {
        val color = DividerDefaults.color

        return remember(color) {
            DividerAdaptation(
                color = color,
                thickness = DividerDefaults.Thickness
            )
        }
    }
}

/**
 * Adaptation class for divider components that manages theme-specific values for dividers.
 *
 * This class handles the adaptation between Cupertino and Material design for dividers,
 * providing appropriate color and thickness styling for both design systems.
 *
 * @param color The color of the divider
 * @param thickness The thickness of the divider
 */
@Stable
/**
 * Adaptation class for divider components that manages theme-specific values for dividers.
 *
 * This class handles the adaptation between Cupertino and Material design for dividers,
 * providing appropriate color and thickness styling for both design systems.
 *
 * @param color The color of the divider
 * @param thickness The thickness of the divider
 */
class DividerAdaptation internal constructor(
    color: Color,
    thickness: Dp
) {
    var color: Color by mutableStateOf(color)
    var thickness: Dp by mutableStateOf(thickness)
}