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

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

/**
 * Android implementation of the HazeNode for drawing effects.
 *
 * @param areas The list of rectangles to draw the effect on
 * @param backgroundColor The background color for the effect
 * @param tint The tint color for the effect
 * @param blurRadius The blur radius for the effect
 * @param density The density for drawing operations
 */
internal actual class HazeNode actual constructor(
    private var areas: List<Rect>,
    private var backgroundColor: Color,
    private var tint: Color,
    private var blurRadius: Dp,
    private val density: Density,
): Modifier.Node(),
    DrawModifierNode {
    
    /**
     * Updates the HazeNode with new parameters.
     *
     * @param areas The list of rectangles to draw the effect on
     * @param backgroundColor The background color for the effect
     * @param tint The tint color for the effect
     * @param blurRadius The blur radius for the effect
     */
    actual fun update(
        areas: List<Rect>,
        backgroundColor: Color,
        tint: Color,
        blurRadius: Dp,
    ) {
        this.areas = areas
        this.backgroundColor = backgroundColor
        this.tint = tint
        this.blurRadius = blurRadius
    }

    /**
     * Draws the haze effect on the content.
     *
     * @param drawContent The content to draw
     */
    override fun ContentDrawScope.draw() {
        drawContent()

        for (area in areas) {
            // We need to boost the alpha as we don't have a blur effect
            drawRect(
                color = tint.copy(alpha = 1f),
                topLeft = area.topLeft,
                size = area.size,
            )
        }
    }
}
