[![Maven Central](https://maven-badges.sml.io/sonatype-central/zone.ien.hig/hig/badge.svg?style=flat&subject=Sonatype%20Central&color=blue)](https://maven-badges.sml.io/sonatype-central/zone.ien.hig/hig)
[![Main Workflow](https://github.com/ienground/compose-hig/actions/workflows/publish.yml/badge.svg)](https://github.com/ienground/compose-hig/actions/workflows/publish.yml)

[English](README.md) | [한국어](README.ko.md)

# Compose-HIG

Apple **Human Interface Guidelines(HIG)**를 **Liquid Glass** 비주얼, 네이티브 햅틱 피드백, 부드러운 스프링 물리를 활용해 구현한 현대적이고 높은 수준으로 커스터마이징할 수 있는 **Compose Multiplatform** 라이브러리입니다.

[alexzhirkevich/compose-cupertino](https://github.com/alexzhirkevich/compose-cupertino)와 [slanos/compose-cupertino](https://github.com/slanos/compose-cupertino)를 기반으로 확장했습니다.

---

## 🌟 주요 기능

- **iOS 26 HIG Liquid Glass 스타일**: 인터랙티브 렌즈 굴절, 색수차, 생동감, 동적 밝기 틴트를 지원하는 고급 배경 렌더링.
- **확장된 컴포넌트 구성**:
  - 💧 **Liquid Glass 컴포넌트**: `CupertinoLiquidButton`, `CupertinoLiquidAlertDialog`
  - 🎛️ **세그먼트 컨트롤**: 스프링 드래그 애니메이션, 동적 인디케이터 너비, 햅틱 피드백을 지원하는 `CupertinoSegmentedControl`.
  - 👆 **SwipeBox**: DSL 액션 빌더(`start` 및 `end`), 전체 스와이프 자동 실행, 스프링 응답을 지원하는 `CupertinoSwipeBox`.
  - 📋 **그룹 섹션 및 지연 리스트**: iOS 그룹 인셋 스타일을 지원하는 `CupertinoSection`, `LazyListScope.section`, `stickySection`.
  - 📅 **피커**: `CupertinoPicker`, `CupertinoDatePicker`, `CupertinoTimePicker`, `CupertinoDateTimePicker`.
  - 🧭 **내비게이션 및 구조**: `CupertinoTopAppBar`, `CupertinoNavigationBar`, `CupertinoBottomSheet`, `CupertinoBottomSheetScaffold`, `CupertinoScaffold`.
  - 🔘 **입력 및 스위치**: `CupertinoSwitch`, `CupertinoCheckbox`, `CupertinoTextField`, `CupertinoSearchTextField`.
  - 🎨 **독립형 Cupertino 아이콘**: 무거운 아이콘 팩 의존성을 줄인 경량 벡터 `CupertinoIcons`(Outlined 및 Filled).

---

## 🎬 미리보기 동영상

https://github.com/user-attachments/assets/ef0b2afd-030b-4aae-aa70-e0dfa2bfdd03



### iOS 네이티브 UI 깜빡임

dropdown 메뉴, alert, action sheet와 같은 iOS 네이티브 UI를 표시할 때 Compose 콘텐츠가 잠시 사라지거나 깜빡이면 루트 `ComposeUIViewController`에서 병렬 렌더링을 비활성화하세요.

```kotlin
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeUIViewController

@OptIn(ExperimentalComposeUiApi::class)
fun MainViewController() = ComposeUIViewController(
    configure = {
        parallelRendering = false
    }
) {
    App()
}
```

이 설정은 Compose 렌더링을 UIKit의 화면 표시 전환과 동기화합니다. Compose가 더 이상 전용 스레드에서 렌더링 명령을 인코딩하지 않으므로 렌더링 성능이 일부 낮아질 수 있습니다. 네이티브 UI 전환 중 깜빡임이 발생할 때 적용하세요.

---

## 🚀 라이브 데모

GitHub Pages에 배포된 WasmJS 인터랙티브 웹 쇼케이스를 확인해 보세요.
👉 **[https://ienground.github.io/compose-hig/](https://ienground.github.io/compose-hig/)**

---

## 📦 설치

이 패키지는 Maven Central Repository에 배포되어 있습니다.

`libs.versions.toml`에 의존성을 선언하세요.

```toml
[versions]
hig = "1.3.0"

[libraries]
hig = { group = "zone.ien.hig", name = "hig", version.ref = "hig" }
hig-adaptive = { group = "zone.ien.hig", name = "hig-adaptive", version.ref = "hig" }
hig-native = { group = "zone.ien.hig", name = "hig-native", version.ref = "hig" }
```

`build.gradle.kts`에서는 다음과 같이 설정하세요.

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.hig)
            // Optional adaptive / native extensions
            implementation(libs.hig.adaptive)
        }
    }
}
```

---

## 💡 빠른 코드 예제

### 1. Liquid Glass 버튼

```kotlin
val backdrop = rememberDefaultBackdrop()

CupertinoLiquidButton(
    onClick = { /* handle action */ },
    backdrop = backdrop,
) {
    Text("Liquid Glass Button")
}
```

### 2. 세그먼트 컨트롤

```kotlin
var selectedIndex by remember { mutableStateOf(0) }

CupertinoSegmentedControl(
    selectedTabIndex = selectedIndex,
) {
    CupertinoSegmentedControlTab(
        isSelected = selectedIndex == 0,
        onClick = { selectedIndex = 0 }
    ) {
        Text("First")
    }
    CupertinoSegmentedControlTab(
        isSelected = selectedIndex == 1,
        onClick = { selectedIndex = 1 }
    ) {
        Text("Second")
    }
}
```

### 3. 리스트 항목용 SwipeBox

```kotlin
CupertinoSwipeBox(
    actionItemBuilder = {
        end {
            CupertinoSwipeBoxItem(
                color = CupertinoColors.systemRed,
                icon = CupertinoIcons.Default.Trash,
                label = "Delete",
                onClick = { /* handle delete */ }
            )
        }
    }
) {
    Text("Swipe left to reveal actions", modifier = Modifier.padding(16.dp))
}
```

### 4. 지연 리스트 섹션

```kotlin
LazyColumn {
    section(
        title = { Text("SECTION HEADER") },
        caption = { Text("Section description footer text.") }
    ) {
        item {
            Text("Row Item 1", modifier = Modifier.padding(16.dp))
        }
        item {
            Text("Row Item 2", modifier = Modifier.padding(16.dp))
        }
    }
}
```

---

## 📝 할 일

이전 라이브러리와 비교해 Human Interface Guidelines에 맞도록 많은 컴포넌트를 최신 디자인으로 업데이트했지만, 아직 작업이 진행 중입니다. 기여를 통해 라이브러리 완성에 함께해 주세요.

- [ ] BottomBar
- [ ] Search Bar
- [ ] Dropdown Native
- [ ] Dialog (Non-native)
- [x] Segmented Control

---

## 📄 라이선스

```
Copyright (c) 2023-2024. Compose Cupertino project and open source contributors.
Copyright (c) 2025. Scott Lanoue.
Copyright (c) 2026. IENGROUND of IENLAB.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

## 🤝 기여

기여를 환영합니다. Pull Request를 제출할 때 저작권 헤더를 포함해 주세요.
