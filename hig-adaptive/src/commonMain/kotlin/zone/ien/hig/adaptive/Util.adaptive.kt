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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified

/**
 * Returns this Color if it is specified, otherwise returns the result of calling [block].
 *
 * This utility function provides a clean way to handle optional Colors that may be null or unspecified.
 * It's commonly used in adaptive components where default colors need to be provided.
 *
 * @param block The block that provides a default Color value if this Color is null or unspecified.
 * @return This Color if it is specified, otherwise the result of [block].
 */
inline fun Color?.takeOrElse(block: () -> Color): Color = if (this != null && isSpecified) this else block()
