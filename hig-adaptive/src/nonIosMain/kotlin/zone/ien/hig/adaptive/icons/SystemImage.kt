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

/**
 * 비 iOS 플랫폼에 맞춘 시스템 이미지를 반환하는 함수
 * 
 * 이 함수는 iOS가 아닌 플랫폼(예: Android, Desktop 등)에서 사용되며,
 * 항상 null을 반환하여 시스템 이미지 사용을 방지합니다.
 * 
 * @param name 시스템 이미지 이름
 * @return null (시스템 이미지 지원되지 않음)
 */
@Composable
internal actual fun systemImage(name: String): Painter? = null
