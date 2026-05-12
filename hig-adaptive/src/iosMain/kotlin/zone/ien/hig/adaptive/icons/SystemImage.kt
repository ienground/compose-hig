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



package zone.ien.hig.adaptive.icons

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import zone.ien.hig.icons.CupertinoIcons
import zone.ien.hig.named

/**
 * Platform-specific implementation for retrieving system icons on iOS platforms.
 *
 * This function provides a way to retrieve system icons for iOS platforms.
 * It delegates to the Cupertino icon implementation using the [CupertinoIcons.named] function.
 *
 * @param name The name of the system icon to retrieve.
 * @return A [Painter] for the specified system icon, or null if the icon is not found.
 */
@Composable
internal actual fun systemImage(name: String): Painter? = CupertinoIcons.named(name)
