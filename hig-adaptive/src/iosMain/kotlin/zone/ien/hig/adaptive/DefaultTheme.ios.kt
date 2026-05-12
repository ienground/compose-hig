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
 * iOS 플랫폼에 맞춘 기본 테마를 반환하는 actual 값
 * 
 * 이 값은 iOS 환경에서 사용되는 기본 테마를 제공하며,
 * iOS 전용 테마인 Cupertino 테마를 반환합니다.
 */
internal actual val DefaultTheme: Theme
    get() = Theme.Cupertino