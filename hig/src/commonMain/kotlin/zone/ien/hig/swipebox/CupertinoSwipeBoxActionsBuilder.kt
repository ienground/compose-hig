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

package zone.ien.hig.swipebox

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

/**
 * DSL builder for configuring start and end action items inside a [zone.ien.hig.CupertinoSwipeBox].
 */
class CupertinoSwipeBoxActionsBuilder {
    private val _startActions = mutableListOf<SwipeAction>()
    private val _endActions = mutableListOf<SwipeAction>()

    /**
     * List of configured start (left to right) actions.
     */
    val startActions: List<SwipeAction> get() = _startActions

    /**
     * List of configured end (right to left) actions.
     */
    val endActions: List<SwipeAction> get() = _endActions

    /**
     * Adds a start-side swipe action item.
     *
     * @param key Optional unique key for the action item.
     * @param onClick Callback executed when full-swipe triggers this action item.
     * @param content Composable slot for the action item content (typically [CupertinoSwipeBoxItem]).
     */
    fun start(
        key: Any? = null,
        onClick: (() -> Unit)? = null,
        content: @Composable RowScope.() -> Unit,
    ) {
        _startActions.add(SwipeAction(key, onClick, content))
    }

    /**
     * Adds an end-side swipe action item.
     *
     * @param key Optional unique key for the action item.
     * @param onClick Callback executed when full-swipe triggers this action item.
     * @param content Composable slot for the action item content (typically [CupertinoSwipeBoxItem]).
     */
    fun end(
        key: Any? = null,
        onClick: (() -> Unit)? = null,
        content: @Composable RowScope.() -> Unit,
    ) {
        _endActions.add(SwipeAction(key, onClick, content))
    }

    /**
     * Wrapper class representing an individual swipe action.
     */
    class SwipeAction(
        val key: Any? = null,
        val onClick: (() -> Unit)? = null,
        val content: @Composable RowScope.() -> Unit,
    )
}
