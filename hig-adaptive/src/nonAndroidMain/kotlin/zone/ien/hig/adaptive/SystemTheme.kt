/*
 * Copyright (c) 2023 Compose Cupertino project and open source contributors.
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

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

/**
 * 시스템 Material Color Scheme을 반환하는 함수
 * 
 * 이 함수는 Android 플랫폼에서 사용되는 시스템 색상 기준을 반환하며,
 * 현재는 null을 반환하는 구현을 가지고 있습니다.
 * 
 * @param dark 다크 모드 여부
 * @return 시스템 색상 스킴 또는 null
 */
@Composable
internal actual fun systemMaterialColorScheme(dark: Boolean): ColorScheme? {
    val r: ColorScheme? = null // https://github.com/JetBrains/compose-multiplatform/issues/3900
    return r
}
