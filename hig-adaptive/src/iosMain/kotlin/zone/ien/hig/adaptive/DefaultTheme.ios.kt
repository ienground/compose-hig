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

/**
 * Platform-specific default theme for iOS platforms.
 *
 * This file defines the default theme for iOS platforms to use Cupertino design.
 * It ensures consistent theming across iOS applications using this library.
 *
 * On iOS platforms, the default theme is set to [Theme.Cupertino] to provide a native Cupertino experience.
 *
 * @return The default theme for iOS platforms, which is [Theme.Cupertino].
 */
internal actual val DefaultTheme: Theme
    get() = Theme.Cupertino