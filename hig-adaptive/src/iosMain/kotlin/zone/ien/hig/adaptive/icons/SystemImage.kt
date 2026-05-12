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
 * iOS 플랫폼에 맞춘 시스템 이미지를 반환하는 함수
 * 
 * 이 함수는 주어진 이름에 해당하는 시스템 이미지를 반환하며,
 * iOS 환경에서는 CupertinoIcons를 사용하여 이미지를 로드합니다.
 * 
 * @param name 시스템 이미지 이름
 * @return 해당 이름의 시스템 이미지 또는 null
 */
@Composable
internal actual fun systemImage(name: String): Painter? = CupertinoIcons.named(name)
