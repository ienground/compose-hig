[![Maven Central](https://maven-badges.sml.io/sonatype-central/zone.ien.hig/hig/badge.svg?style=flat&subject=Sonatype%20Central&color=blue)](https://maven-badges.sml.io/sonatype-central/zone.ien.hig/hig)
[![Main Workflow](https://github.com/ienground/compose-hig/actions/workflows/publish.yml/badge.svg)](https://github.com/ienground/compose-hig/actions/workflows/publish.yml)

# Compose-HIG

A modern, highly customizable **Compose Multiplatform** library implementing Apple's **Human Interface Guidelines (HIG)** with **Liquid Glass** visuals, native haptic feedback dynamics, and smooth spring physics.

Forked and expanded from [alexzhirkevich/compose-cupertino](https://github.com/alexzhirkevich/compose-cupertino) and [slanos/compose-cupertino](https://github.com/slanos/compose-cupertino).

---

## 🌟 Key Features

- **iOS 26 HIG Liquid Glass Aesthetic**: Advanced backdrop rendering with interactive lens refraction, chromatic aberration, vibrancy, and dynamic luminance tinting.
- **Enhanced Component Suite**:
  - 💧 **Liquid Glass Components**: `CupertinoLiquidButton`, `CupertinoLiquidAlertDialog`
  - 🎛️ **Segmented Control**: `CupertinoSegmentedControl` with spring drag animations, dynamic indicator width, and haptic feedback.
  - 👆 **SwipeBox**: `CupertinoSwipeBox` with DSL action builders (`start` & `end`), full-swipe auto-trigger, and spring response.
  - 📋 **Grouped Sections & Lazy Lists**: `CupertinoSection`, `LazyListScope.section`, and `stickySection` with iOS grouped inset styling.
  - 📅 **Pickers**: `CupertinoPicker`, `CupertinoDatePicker`, `CupertinoTimePicker`, and `CupertinoDateTimePicker`.
  - 🧭 **Navigation & Structure**: `CupertinoTopAppBar`, `CupertinoNavigationBar`, `CupertinoBottomSheet`, `CupertinoBottomSheetScaffold`, `CupertinoScaffold`.
  - 🔘 **Inputs & Switches**: `CupertinoSwitch`, `CupertinoCheckbox`, `CupertinoTextField`, `CupertinoSearchTextField`.
  - 🎨 **Standalone Cupertino Icons**: Lightweight vector `CupertinoIcons` (Outlined & Filled) removing unnecessary dependencies on heavy icon packs.

---

## 🎬 Preview Video

https://github.com/user-attachments/assets/107fbb68-8604-4621-8037-d373c835406e

---

## 🚀 Live Demo

Try the WasmJS interactive web showcase deployed via GitHub Pages:  
👉 **[https://ienground.github.io/compose-hig/](https://ienground.github.io/compose-hig/)**

---

## 📦 Installation

This package is published to Maven Central Repository.

Declare the dependency in `libs.versions.toml`:

```toml
[versions]
hig = "1.3.0"

[libraries]
hig = { group = "zone.ien.hig", name = "hig", version.ref = "hig" }
hig-adaptive = { group = "zone.ien.hig", name = "hig-adaptive", version.ref = "hig" }
hig-native = { group = "zone.ien.hig", name = "hig-native", version.ref = "hig" }
```

In `build.gradle.kts`:

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

## 💡 Quick Code Examples

### 1. Liquid Glass Button
```kotlin
val backdrop = rememberDefaultBackdrop()

CupertinoLiquidButton(
    onClick = { /* handle action */ },
    backdrop = backdrop,
) {
    Text("Liquid Glass Button")
}
```

### 2. Segmented Control
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

### 3. SwipeBox for List Items
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

### 4. Lazy List Section
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

## 📝 To-do

Compared to the previous library, we have made efforts to update many components from the Human Interface Guidelines to match the latest design, but there is still work in progress. Please help complete the library with your contributions!

- [ ] BottomBar
- [ ] Search Bar
- [ ] Dropdown Native
- [ ] Dialog (Non-native)
- [x] Segmented Control

---

## 📄 License

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

## 🤝 Contributions

Contributions are welcome! Please ensure copyright headers are included when submitting Pull Requests.
